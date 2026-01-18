package script_transform_csv_to_mongodb_and_neo4j.neo4j;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.bson.Document;
import org.neo4j.driver.Driver;
import org.neo4j.driver.ExecutableQuery;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Session;
import script_transform_csv_to_mongodb_and_neo4j.ConfigurationFileReader;
import script_transform_csv_to_mongodb_and_neo4j.CsvDataUpdater;
import script_transform_csv_to_mongodb_and_neo4j.ParallelExecutor;
import script_transform_csv_to_mongodb_and_neo4j.mongoDb.CsvToMongoTransformer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JOperations {

    public static Driver driver = MongoToNeo4JTransformer.getNeo4jDriver();
    public static MongoClient mongoClient;
    public static MongoDatabase mongoDatabase;
    public static MongoDatabase mongoCSVDatabase;
    public static String neo4jDatabase= ConfigurationFileReader.getNeo4JDatabase();
    public static ParallelExecutor parallelExecutor=new ParallelExecutor();


    public Neo4JOperations(Driver driver, String neo4jDatabase, ParallelExecutor parallelExecutor) {
        this.driver = driver;
        this.mongoClient = CsvToMongoTransformer.client;
        this.mongoDatabase = CsvToMongoTransformer.newMongoDatabase;
        this.mongoCSVDatabase = CsvToMongoTransformer.csvDocuments;
        this.neo4jDatabase = neo4jDatabase;
        this.parallelExecutor = parallelExecutor;
    }

    protected void createMemberNodes() {
        List<String> fieldsToInclude = List.of("member_id", "bio", "member_name", "member_status", "city", "hometown");
        createNodesByCollectionName("members", "Member", fieldsToInclude, true);
        createNeo4JIndex("Member","member_id");
//        MongoCollection membersCollection = mongoDatabase.getCollection("members");
//        MongoCursor<Document> mongoCursor = membersCollection.find()
//                .projection(new Document("member_id",true).append("member_name",true)
//                        .append("member_status",true).append("hometown",true).append("link",true)
//                        .append("bio",true).append("city",true).append("city.localized_country_name",true).append("_id",false)).cursor();
//
//        HashMap<String,Object> mapForGraph = new HashMap<>();
//        while (mongoCursor.hasNext()){
//         Document memberDocument =  mongoCursor.next();
//         String queryForNeo4j = "CREATE (:Member "+CsvToMongoTransformer.flattenDocument(memberDocument).toJson()+")";
//         driver.executableQuery(queryForNeo4j);
//        }
//        return true;
    }

    protected void createTopicNodes() {
        List<String> fieldsToInclude = List.of("topic_id", "description", "link", "topic_name");
        createNodesByCollectionName("topics", "Topic", fieldsToInclude, true);
        createNeo4JIndex("Topic","topic_id");
    }

    protected void createEventNodes() {
        List<String> fieldsToInclude = List.of("event_id", "event_name", "event_time", "description", "event_url", "venue.city", "fee"
        );
        createNodesByCollectionName("events", "Event", fieldsToInclude, true);
        createNeo4JIndex("Event","event_id");

    }

    protected void createGroupNodes() {
        List<String> fieldsToInclude = List.of("group_id", "group_name", "city", "description", "link");
        createNodesByCollectionName("groups", "Group", fieldsToInclude, true);
        createNeo4JIndex("Group","group_id");

    }

    //Create Member-Group , Member-Event ,Group-Event
    protected void createEdgesFromRsvp() {
        MongoCollection<Document> rsvpsCollection = mongoCSVDatabase.getCollection("rsvps.csv");
        try (MongoCursor<Document> mongoCursor = rsvpsCollection.find().cursor()) {
            String member_id;
            String event_id;
            String group_id;
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                member_id = document.getString("member_id");
                event_id = document.getString("event_id");
                group_id = document.getString("group_id");

                executeQueryInParallel(
                        driver.executableQuery("""
                                MATCH (member:Member {member_id:$member_id})
                                MATCH (event:Event {event_id:$event_id})
                                MATCH (group:Group {group_id:$group_id})
                                CREATE (member)-[:MEMBER_OF]->(group)
                                CREATE (group)-[:ORGANIZES]->(event)
                                CREATE (member)-[:ATTENDS]->(event)
                                CREATE (member)<-[:MEMBER_OF]-(group)
                                CREATE (group)<-[:ORGANIZES]-(event)
                                CREATE (member)<-[:ATTENDS]-(event)
                                """).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build())
                        .withParameters(Map.of("member_id", member_id, "event_id", event_id, "group_id", group_id))
                );
//                .execute()
            }
        }
    }


    //
    protected void createMemberTopicsEdges() {
        MongoCollection<Document> collecion = mongoCSVDatabase.getCollection("members_topics.csv");
        try (MongoCursor<Document> mongoCursor = collecion.find().cursor()) {
            String member_id;
            String topic_id;

            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                member_id = document.getString("member_id");
                topic_id = document.getString("topic_id");

           createMemberTopicEdge(member_id,topic_id);
            }
        }
    }


    //
    protected void createGroupTopicsEdges() {
        MongoCollection<Document> collecion = mongoCSVDatabase.getCollection("groups_topics.csv");
        try (MongoCursor<Document> mongoCursor = collecion.find().cursor()) {
            String group_id;
            String topic_id;

            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                group_id = document.getString("group_id");
                topic_id = document.getString("topic_id");

             createGroupTopicEdge(group_id,topic_id);
            }
        }
    }

    /**
     * This is extra to the edges created from Rsvps.csv , this is based on original members.csv data
     */
    protected void createGroupMemberEdges() {
        MongoCollection<Document> collection = mongoCSVDatabase.getCollection("members.csv");
        try (MongoCursor<Document> mongoCursor = collection.find().cursor()) {
            String group_id;
            String member_id;

            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();

                group_id = document.getString("group_id");
                member_id = document.getString("member_id");

           createMemberGroupEdge(member_id,group_id);
            }
        }
    }
    public static void createNeo4JIndex(String nodeName,String indexKey){

       executeQueryInParallel(driver.executableQuery(" CREATE INDEX "+indexKey+"_index"
                +" IF NOT EXISTS "
                     +" FOR (e:"+nodeName+")"
                    +"  ON (e."+nodeName+");"
                +"").withConfig(QueryConfig.builder()
                .withDatabase(neo4jDatabase).build()));
    }
    /**
     * This is extra to the edges created from Rsvps.csv , this is based on group_id contained in original events.csv data
     */
    protected void createGroupEventsEdges() {
        MongoCollection<Document> collection = CsvToMongoTransformer.newMongoDatabase.getCollection("events");
        try (MongoCursor<Document> mongoCursor = collection
                .find(new Document("creator_group.id", new Document("$exists", true))).cursor()) {

            String group_id;
            String event_id;

            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();

                group_id = document.getEmbedded(List.of("creator_group","id"),String.class);
                event_id = document.getString("event_id");

          createGroupEventEdge(group_id,event_id);
            }
        }
    }



    private void createNodesByMongoCursor(MongoCursor<Document> mongoCursor, String nodesName, boolean flattenTheDocument) {
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            document = flattenTheDocument ? CsvToMongoTransformer.flattenDocument(document) : document;
            String queryForNeo4j = "CREATE (p:" + nodesName + "  " +docToNeo4jNode(document) + " )";
            executeQueryInParallel(
                    driver.executableQuery(queryForNeo4j).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build())
//                            .execute();
        );
        }
    }

    private void createNodesByCollectionName(String collectionName, String nodesName, List<String> fieldsToInclude, boolean flattenTheDocument) {
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        Document projection = new Document("_id", false);
        for (String field : fieldsToInclude) projection.append(field, true);
        try (MongoCursor<Document> mongoCursor = collection.find()
                .projection(projection).cursor()) {
            createNodesByMongoCursor(mongoCursor, nodesName, flattenTheDocument);
        }
    }


    public static void createNodeFromDocument(String nodesName,Document documentToModify,List<String> fieldsToInclude,boolean flattenTheDocument){
        Document document = new Document();
        for (String key:documentToModify.keySet()){
            if (fieldsToInclude.contains(key)){
                document.append(key,documentToModify.get(key));
            }
        }
        if (flattenTheDocument) document = CsvToMongoTransformer.flattenDocument(document);
        String queryForNeo4j = "CREATE (p:" + nodesName + "  " +docToNeo4jNode(document) + " )";
        executeQueryInParallel(
                driver.executableQuery(queryForNeo4j).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build())
//                            .execute();
        );
    }
    public static void createGroupNode(Document documentToModify,List<String> fieldsToInclude,boolean flattenTheDocument){
        List<String> fields ;
        if (fieldsToInclude==null || fieldsToInclude.isEmpty()){
            fields= List.of("group_id", "group_name", "city", "description", "link");
        }
        else fields=fieldsToInclude;
        createNodeFromDocument("Group",documentToModify,fields,flattenTheDocument);
    }
    public static void createEventNode(Document documentToModify,List<String> fieldsToInclude,boolean flattenTheDocument){
        List<String> fields ;
        if (fieldsToInclude==null || fieldsToInclude.isEmpty()){
            fields=  List.of("event_id", "event_name", "event_time", "description", "event_url", "venue.city", "fee"
            );
        }
        else fields=fieldsToInclude;
        createNodeFromDocument("Event",documentToModify,fields,flattenTheDocument);
    }
    public static void createTopicNode(Document documentToModify,List<String> fieldsToInclude,boolean flattenTheDocument){
        List<String> fields ;
        if (fieldsToInclude==null || fieldsToInclude.isEmpty()){
            fields = List.of("topic_id", "description", "link", "topic_name");
        }
        else fields=fieldsToInclude;
        createNodeFromDocument("Topic",documentToModify,fields,flattenTheDocument);
    }
    public static void createMemberNode(Document documentToModify,List<String> fieldsToInclude,boolean flattenTheDocument){
        List<String> fields ;
        if (fieldsToInclude==null || fieldsToInclude.isEmpty()){
            fields = List.of("member_id", "bio", "member_name", "member_status", "city", "hometown");
        }
        else fields=fieldsToInclude;
        createNodeFromDocument("Member",documentToModify,fields,flattenTheDocument);
    }

    public static void createMemberGroupEdge(String member_id,String group_id){
        executeQueryInParallel(   driver.executableQuery("""
                        MATCH (group:Group {group_id:$group_id})
                        MATCH (member:Member {member_id:$member_id})
                        CREATE (member)-[:MEMBER_OF]->(group)
                        CREATE (member)<-[:MEMBER_OF]-(group)
                        
                        """).withConfig(QueryConfig.builder()
                        .withDatabase(neo4jDatabase).build())
                .withParameters(Map.of("group_id", group_id, "member_id", member_id)));
    }
    public static void createGroupTopicEdge(String group_id,String topic_id){
        executeQueryInParallel(
                driver.executableQuery("""
                        MATCH (group:Group {group_id:$group_id})
                        MATCH (topic:Topic {topic_id:$topic_id})
                        CREATE (group)-[:INTERESTED_IN]->(topic)
                        CREATE (group)<-[:INTERESTED_IN]-(topic)
                        """).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build())
                        .withParameters(Map.of("group_id", group_id, "topic_id", topic_id)));
    }
    public static void createMemberTopicEdge(String member_id,String topic_id){
        executeQueryInParallel( driver.executableQuery("""
                        MATCH (member:Member {member_id:$member_id})
                        MATCH (topic:Topic {topic_id:$topic_id})
                        CREATE (member)-[:INTERESTED_IN]->(topic)
                        CREATE (member)<-[:INTERESTED_IN]-(topic)
                        """).withConfig(QueryConfig.builder()
                        .withDatabase(neo4jDatabase).build())
                .withParameters(Map.of("member_id", member_id, "topic_id", topic_id)));
    }
    public static void createGroupEventEdge(String group_id,String event_id){
        executeQueryInParallel(driver.executableQuery("""
                         MATCH (group:Group {group_id:$group_id})
                         MATCH (event:Event {event_id:$event_id})
                        CREATE (group)<-[:ORGANIZES]-(event)
                        CREATE (group)-[:ORGANIZES]->(event)
                        """).withConfig(QueryConfig.builder()
                        .withDatabase(neo4jDatabase).build())
                .withParameters(Map.of("group_id", group_id, "event_id", event_id))
        );
    }
    public static void createMemberEventEdge(String member_id,String event_id){
        executeQueryInParallel(driver.executableQuery("""
                         MATCH (member:Member {member_id:$member_id})
                         MATCH (event:Event {event_id:$event_id})
                        CREATE (member)<-[:ATTENDS]-(event)
                        CREATE (member)-[:ATTENDS]->(event)
                        """).withConfig(QueryConfig.builder()
                        .withDatabase(neo4jDatabase).build())
                .withParameters(Map.of("member_id", member_id, "event_id", event_id))
        );
    }


    /**
    Function to remove the quotas from the keys
     */
    public static String docToNeo4jNode(Document document){
       StringBuilder node =new StringBuilder("{");
       for (String key:document.keySet()){
           node.append(key.replaceAll("\"",""));
           node.append(": ");
           node.append("\""+(document.get(key)!=null ? document.get(key).toString() : "")+"\"");
           node.append(",");
       }
       node.deleteCharAt(node.lastIndexOf(","));
       node.append("}");
       return node.toString();

    }
    public static void executeQueryInParallel(ExecutableQuery executableQuery){
//    parallelExecutor.submit(() -> executableQuery.execute());
      if (ConfigurationFileReader.getProperty("transferDataToNeo4j").equalsIgnoreCase("false")) return;
    executableQuery.execute();
    }

protected static void  createMemberGroupsEdge(){
    createEdgesFromCSV(CsvDataUpdater.transformedMembers,"member_id","group_id");
}
    protected static void  createGroupTopicsEdge(){
        createEdgesFromCSV(CsvDataUpdater.groupTopicsPath,"group_id","topic_id");
    }

    private static void createEdgesFromCSV(String filePath,String firstKey,String secondKey){
   double BATCH_SIZE = 1_000;
    try (
            Session session = driver.session();
            CSVReader reader = new CSVReader(new FileReader(filePath));
    ) {
        List<Map<String, Object>> batch = new ArrayList<>();
        List<String[]> batch2=new ArrayList<>();
        String[] row;

        String[] header = reader.readNext();
        int indexOf_firstKey=-1;
        int index_Of_secondKey=-1;
        for (int i=0;i<header.length;i++){
            if (header[i].equalsIgnoreCase(firstKey)) indexOf_firstKey=i;
            else if (header[i].equalsIgnoreCase(secondKey)) index_Of_secondKey=i;
        }
        if (indexOf_firstKey==-1 || index_Of_secondKey==-1) throw new RuntimeException("The key does not exist");

        while ((row = reader.readNext()) != null) {


            addEdge(firstKey,secondKey,new String[]{row[indexOf_firstKey],row[index_Of_secondKey]});

//            batch2.add(new String[]{row[indexOf_firstKey],row[index_Of_secondKey]});
//
//            if (batch2.size() == BATCH_SIZE) {
//                writeBatch( firstKey, secondKey,batch2);
//                batch2.clear();
//            }
        }
//
//        if (!batch2.isEmpty()) {
//            writeBatch( firstKey, secondKey, batch2);
//        }
    } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
    } catch (IOException | CsvValidationException e) {
        throw new RuntimeException(e);
    }


}
    private static void addEdge(String firstKey,String secondKey, String[] row) {
        String firstNode=findNode(firstKey);
        String secondNode=findNode(secondKey);
        String relationship = findRelationship(firstKey,secondKey);
        executeQueryInParallel(driver.executableQuery(

                " MATCH (m:"+firstNode+" {"+firstKey+": "+row[0]+"}) "
                +" MATCH (e:"+secondNode+"  {"+secondKey+": "+row[1]+"}) "
                +" MERGE (m)-[:"+relationship+"]->(e) "
                +" MERGE (m)<-[:"+relationship+"]-(e) "
                + "").withConfig(QueryConfig.builder()
                .withDatabase(neo4jDatabase).build()));
    }

    private static void writeBatch(String firstKey,String secondKey, List<String[]> batch) {

        String firstNode=findNode(firstKey);
        String secondNode=findNode(secondKey);
        String relationship = findRelationship(firstKey,secondKey);

        try (Session session = driver.session()) {
            session.executeWrite(tx ->
                    tx.run(
                            ""
                           + " UNWIND $rows AS row "
                           +" MATCH (m:"+firstNode+" {"+firstKey+": row[0]}) "
                           +" MATCH (e:"+secondNode+"  {"+secondKey+": row[1]}) "
                           +" MERGE (m)-[:"+relationship+"]->(e) "
                            +" MERGE (m)<-[:"+relationship+"]-(e) "
                                    + "",
                            Map.of("rows", batch)
                    )
            );
        }
    }

    private static String findRelationship(String firstKey,String secondKey){
        if ((firstKey.equalsIgnoreCase("member_id")
                && secondKey.equalsIgnoreCase("group_id"))||
                (firstKey.equalsIgnoreCase("group_id")
                        && secondKey.equalsIgnoreCase("member_id"))) return "MEMBER_OF";
        else if ((firstKey.equalsIgnoreCase("topic_id")
                && secondKey.equalsIgnoreCase("group_id"))||
                (firstKey.equalsIgnoreCase("group_id")
                        && secondKey.equalsIgnoreCase("topic_id"))) return "HAS_TOPIC";
        return null;
    }
    private static String findNode(String primaryKey) {
        if (primaryKey.equalsIgnoreCase("member_id")) return "Member";
        if (primaryKey.equalsIgnoreCase("group_id")) return "Group";
        if (primaryKey.equalsIgnoreCase("event_id")) return "Event";
        if (primaryKey.equalsIgnoreCase("topic_id")) return "Topic";

        return null;
    }






}
