package script_transform_csv_to_mongodb_and_neo4j;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.opencsv.CSVReader;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvToMongoImporter {
    public static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    public static final String firstDatasetFolder = "C:\\Users\\Public\\JoinUs_Dataset\\Meetup\\";//LOCAL PATH OF THE DATASET FOUND BY MOIN
    public static final String secondDatasetFolder = "C:\\Users\\Public\\JoinUs_Dataset\\dataset2_joinUs\\";//LOCAL PATH OF THE DATASET FOUND BY FLORIAN
    public static final MongoDatabase csvDocuments = client.getDatabase("CSV_Documents");
    public static final MongoDatabase newMongoDatabase=client.getDatabase("joinUs");
    private static final int BATCH_SIZE = 1000;

    public static void main(String[] args) throws Exception {

        long startingTime = System.currentTimeMillis();

// Step-1 , import all the csv data to MongoDB as it is (replace '.' with '___' in the fields that contain '.' due to issues with MongoDB)
        importCsvToMongoDB(firstDatasetFolder);
        importCsvToMongoDB(firstDatasetFolder);

//Step-2 , Get Ids from the second dataset and assign them
// throughout collections of the first dataset in order to save the state of rsvps.csv for the GraphDB
        updateIdsForCollection("meta-events.csv", "event_id",
                "events.csv", "event_id");
        updateIdsForCollection("meta-groups.csv", "group_id",
                "groups.csv", "group_id");
        updateIdsForCollection("meta-members.csv", "member_id",
                "members.csv", "member_id");

//Step-3 ,Create the new Collection we will use for our project , in the new database 'JoinUs'
        new MongoDbEventOperations(client,newMongoDatabase).createEventCollection();
        new MongoDbUserOperations(client,newMongoDatabase).createMemberCollection();
        new MongoDbGroupOperations(client,newMongoDatabase).createGroupCollection();
        new MongoDbTopicOperation(client,newMongoDatabase).createTopicCollection();
        new MongoDbCityOperation(client,newMongoDatabase).createCityCollection();

        long endingTime = System.currentTimeMillis();

        System.out.println("THE  PROCESS TOOK "+(endingTime-startingTime)/1000 +" seconds ");
    }


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


        if (!Files.isDirectory(Path.of(csvPath))
        ) {
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

/**
Get all Ids/Otherkeys from the collection of the sourceCollection
and assign them randomly to the records of the destinationCollection

For now ,it is supposed to assign random Ids from the collections of the second dataset
 to the collections of the first dataset , in order to not change the GraphDB edges (rsvps.csv)
 */
    public static List<String> updateIdsForCollection(String sourceCollection, String sourceKey, String destinationCollection, String destinationKey){
        MongoCollection sourceColl= CsvToMongoImporter.csvDocuments.getCollection(sourceCollection);
        List<String> IdsFromSourceColl = retrieveIds(sourceCollection,sourceKey);

        int totalSourceRecords=IdsFromSourceColl.size();
        List<String> destinationIds = retrieveIds(destinationCollection,destinationKey);
        int totalDesinationRecords = destinationIds.size();


        Set<Integer> randomNumbers = new HashSet<>();
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

            sourceColl.updateMany(
                    Filters.eq(sourceKey, chosenRecord),
                    new Document("$set",
                            new Document(destinationKey, IdsFromSourceColl.get(idsChosen))
                    )
            );

            idsChosen++;
        }
        return chosenRecords.stream().toList();
    }

    public static List<String> retrieveIds(String collection,String key){
        MongoCollection sourceColl= CsvToMongoImporter.csvDocuments.getCollection(collection);
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
        MongoCollection sourceColl= CsvToMongoImporter.csvDocuments.getCollection(collection);
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

    public static void assignIfFound(Document document,String key,String value){
        if (!value.equalsIgnoreCase("not_found")){
            document.append(key,value);
        }
    }
}
