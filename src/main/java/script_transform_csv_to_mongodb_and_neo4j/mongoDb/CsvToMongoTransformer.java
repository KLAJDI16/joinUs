package script_transform_csv_to_mongodb_and_neo4j.mongoDb;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.opencsv.CSVReader;
import org.bson.Document;
import org.bson.conversions.Bson;
import script_transform_csv_to_mongodb_and_neo4j.ConfigurationFileReader;
import tools.jackson.databind.ObjectMapper;

import javax.print.Doc;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CsvToMongoTransformer {

    public static MongoClient client = MongoClients.create(ConfigurationFileReader.getMongoUrl());
    public static final String firstDatasetFolder = ConfigurationFileReader.checkAndGetProp("firstDatasetFolder");// PATH OF THE DATASET FOUND BY MOIN
    public static final String secondDatasetFolder = ConfigurationFileReader.checkAndGetProp("secondDatasetFolder");//PATH OF THE DATASET FOUND BY FLORIAN
    public static final MongoDatabase csvDocuments = client.getDatabase("CSV_Documents");
    public static final MongoDatabase newMongoDatabase=client.getDatabase(ConfigurationFileReader.getMongoDatabase());
    private static final int BATCH_SIZE = 1000;




    /**
     *
     * @param csvPath folder to find the files to import on MongoDB as they are
     * @throws Exception
     *
     * The goal of this method is simply to import the csv data on MongoDB ,
     * which would still require the necessary modifications to make sense for MongoDB.
     *
     *
     */
    public static void importCsvToMongoDB(
            String csvPath
    ) throws Exception {


   if (csvPath.contains("edge")) return; //THESE ARE FOR THE Neo4J so not needed here

        if (!Files.isDirectory(Path.of(csvPath))) {
            MongoCollection<Document> collection = csvDocuments.getCollection(
                    csvPath.substring(csvPath.lastIndexOf(File.separator) + 1));

            try (CSVReader reader = new CSVReader(
                    new FileReader(csvPath))) {

                String[] header = reader.readNext(); // first line
                String[] row;

                List<Document> batch = new ArrayList<>();

                while ((row = reader.readNext()) != null) {
                    Document doc = new Document();

                    for (int i = 0; i < header.length; i++) {
                        doc.append(header[i].contains(".") ? header[i].replace(".","___") : header[i], row.length > i ? row[i] : null);
                    }

                    batch.add(doc);

                    if (batch.size() == BATCH_SIZE) {
                        collection.insertMany(batch);
                        batch.clear();
                    }
                }
                if (!batch.isEmpty()) {
                    collection.insertMany(batch);
                }
            }
        } else {
            for (File file : new File(csvPath).listFiles()) {
                importCsvToMongoDB(file.toString());
            }
        }
    }


    public void tranformCsvDataToMongoDB() throws Exception {

                long startingTime = System.currentTimeMillis();

// Step-1 , import all the csv data to MongoDB as it is (replace '.' with '___' in the fields that contain '.' due to issues with MongoDB)
//        System.out.println("IMPORTING THE CSV DATA INTO MongoDB !!!! ");

        importCsvToMongoDB(firstDatasetFolder);
        importCsvToMongoDB(secondDatasetFolder);

//Step-2 , Get Ids from the second dataset and assign them
// throughout collections of the first dataset in order to save the state of rsvps.csv for the GraphDB




        Thread thread2 = new Thread( () -> {

            updateIdsForCollection("meta-events.csv", "event_id",
                    "event_id", "events.csv");
            System.out.println("UPDATED THE COLLECTION : updateIdsForCollection.events");

        }
        );

        Thread thread1 = new Thread(() ->{

            MongoDbUserOperations.updateIdsForMembers();
            System.out.println("UPDATED THE COLLECTION : MongoDbUserOperations.updateIdsForMembers()");

        });

        Thread thread3 = new Thread( () -> {

            updateIdsForCollection("meta-groups.csv", "group_id",
                    "group_id", "groups.csv", "members.csv", "groups_topics.csv", "events.csv");
            System.out.println("UPDATED THE COLLECTION : updateIdsForCollection.groups");

        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
//
////Step-3 ,Create the new Collection we will use for our project , in the new database 'JoinUs'
//
        System.out.println("NOW READY TO CREATE THE NEW DATABASE !!!! ");

        System.out.println("CREATING THE COLLECTION : createEventCollection");

        new MongoDbEventOperations(client,newMongoDatabase).createEventCollection();


        Thread thread4 =  new Thread(() ->   new MongoDbUserOperations(client,newMongoDatabase)
            .createMemberCollection(true));


        Thread thread5 =     new Thread(() -> {
            try {
                new MongoDbGroupOperations(client,newMongoDatabase).createGroupCollection();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread6 =     new Thread(() ->    new MongoDbTopicOperation(client,newMongoDatabase).createTopicCollection());

        Thread thread7 =     new Thread(() ->    new MongoDbCityOperation(client,newMongoDatabase).createCityCollection());

        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();

        thread4.join();
        thread5.join();
        thread6.join();
        thread7.join();



        long endingTime = System.currentTimeMillis();

        System.out.println("THE  PROCESS TOOK "+(endingTime-startingTime)/1000 +" seconds ");


    }


    public static Map<String, Object> flattenDocument(
            Object value,
            String parentKey,
            String delimiter
    ) {
        Map<String, Object> flattenedMap = new HashMap<>();

        if (value instanceof Document) {
            Document document = (Document) value;

            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String newKey = parentKey.isEmpty()
                        ? entry.getKey()
                        : parentKey + delimiter + entry.getKey();

                flattenedMap.putAll(
                        flattenDocument(entry.getValue(), newKey, delimiter)
                );
            }

        } else if (value instanceof List<?>) {
            // Same behavior as JsonNode array → store as string
            flattenedMap.put(parentKey, value.toString());

        } else {
            // Primitive or other BSON type
            flattenedMap.put(parentKey, value);
        }

        return flattenedMap;
    }

    /**
     *
     * @param sourceCollection The meta collection from which we will get the id
     * @param sourceKey
     * @param destinationCollections  All the collections which need to update the key (id) to what has been chosen from sourceCollection
     *                                for example (meta-groups ---> group_id  -->   group.csv, groups_topics.csv , events.csv)
     * @param destinationKey   The key needs to have the same name for all the destinationCollections , e.g. group_id
     * @return
     */

    public static List<String> updateIdsForCollection(String sourceCollection, String sourceKey, String destinationKey, String... destinationCollections){
        MongoCollection sourceColl= CsvToMongoTransformer.csvDocuments.getCollection(sourceCollection);
        if (destinationCollections==null || destinationCollections.length<1) throw new RuntimeException("Please provide a destinationCollection");

        List<MongoCollection> destinationColls = new ArrayList<>();

        for (String destColl : destinationCollections){
            destinationColls.add(CsvToMongoTransformer.csvDocuments.getCollection(destColl));
        }
//        MongoCollection destColl= CsvToMongoTransformer.csvDocuments.getCollection(destinationCollection);

        List<String> IdsFromSourceColl = retrieveIds(sourceCollection,sourceKey);

        int totalSourceRecords=IdsFromSourceColl.size();
        List<String> destinationIds = retrieveIds(destinationCollections[0], destinationKey);
        int totalDesinationRecords = destinationIds.size();


        Set<String> chosenRecords = new HashSet<>();

        Random random = new Random();
        String chosenRecord;

        int idsChosen =0;


        while (idsChosen < Math.min(totalSourceRecords,totalDesinationRecords)){
            do{
                chosenRecord = destinationIds.get(random.nextInt(totalDesinationRecords));
            }
            while (chosenRecords.contains(chosenRecord));

            chosenRecords.add(chosenRecord);

            //Updating the key for all the collections
            for (MongoCollection destinationCollection : destinationColls) {

                destinationCollection.updateMany(
                        Filters.eq(destinationKey, chosenRecord),
                        new Document("$set",
                                new Document(destinationKey, IdsFromSourceColl.get(idsChosen))
                        )
                );
            }

            idsChosen++;
        }
        return chosenRecords.stream().toList();
    }

    public static List<String> retrieveIds(String collection,String key){
        MongoCollection sourceColl= CsvToMongoTransformer.csvDocuments.getCollection(collection);
        List<String> IdsFromSourceColl = new ArrayList<>();

        MongoCursor mongoCursor = sourceColl.find()
                .projection(new Document(key,true).append("_id",false)).cursor();
        if(mongoCursor !=null) {
            while (mongoCursor.hasNext()) {
                IdsFromSourceColl.add(((Document)mongoCursor.next()).get(key,String.class));
            }
        }
        return  IdsFromSourceColl;
    }
    public static List<String> retrieveIds(String collection, String key, Bson bson){
        MongoCollection sourceColl= CsvToMongoTransformer.csvDocuments.getCollection(collection);
        List<String> IdsFromSourceColl = new ArrayList<>();

        MongoCursor mongoCursor = sourceColl.find(bson)
                .projection(new Document(key,true).append("_id",false)).cursor();
        if(mongoCursor !=null) {
            while (mongoCursor.hasNext()) {
                IdsFromSourceColl.add(((Document)mongoCursor.next()).get(key,String.class));
            }
        }
        return  IdsFromSourceColl;
    }

    public static void assignIfFound(Document document,String key,Object value){
        if (value == null) return;

        if (value instanceof Document){
            Document document1 = (Document) value;
             if (document1.isEmpty()) return;
        }

        if (value instanceof String){
           String val = (String) value;
           if (val.equalsIgnoreCase("not_found")|| val.isEmpty()) return;
        }

            document.append(key,value);
    }

    //To be used for the creation of Neo4J database ,for easier than working with CSV
    public static void verifyMongoDatabaseAndCollections() throws Exception{
        AtomicBoolean databaseExists= new AtomicBoolean(false);
        client.listDatabaseNames().forEach(e -> {
            if (e.equalsIgnoreCase(ConfigurationFileReader.getMongoDatabase())){
                databaseExists.set(true);
            }
        });
        if (!databaseExists.get()) throw new Exception("Database"+CsvToMongoTransformer.newMongoDatabase.getName()+" not created in MongoDb."
                );

        AtomicBoolean allCollectionsCreated = new AtomicBoolean(false);
        List<String> collectionsToVerify=List.of("events","members","topics","groups");

        for (String coll : collectionsToVerify) {
            allCollectionsCreated.set(false);
            client.getDatabase(ConfigurationFileReader.getNeo4JDatabase()).listCollectionNames().forEach(e -> {
                if (e.equalsIgnoreCase(coll)) {
                    allCollectionsCreated.set(true);
                }
            });
            if (!allCollectionsCreated.get()) throw new Exception("Collection "+coll+" has  not been " +
                    "created in MongoDb.");

        }

    }

    public static Document flattenDocument(Document document){
        Map<String, Object> flat =
                flattenDocument(document, "", "_");

           return new Document(flat);
    }
    public static Document cloneDocument(Document document){
        if (document==null) return null;
        Document document1 = new Document();
        for (String key:document.keySet()){
            document1.append(key,document.get(key));
        }
        return document1;
    }




}
