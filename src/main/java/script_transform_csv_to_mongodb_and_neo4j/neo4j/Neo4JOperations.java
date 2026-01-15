package script_transform_csv_to_mongodb_and_neo4j.neo4j;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;
import script_transform_csv_to_mongodb_and_neo4j.mongoDb.CsvToMongoTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JOperations {

    public Driver driver;
    public MongoClient mongoClient;
    public MongoDatabase mongoDatabase;
    public MongoDatabase mongoCSVDatabase;
    public String neo4jDatabase;


    public Neo4JOperations(Driver driver,String neo4jDatabase) {
        this.driver = driver;
        this.mongoClient = CsvToMongoTransformer.client;
        this.mongoDatabase = CsvToMongoTransformer.newMongoDatabase;
        this.mongoCSVDatabase = CsvToMongoTransformer.csvDocuments;
        this.neo4jDatabase = neo4jDatabase;
    }

    public void createMemberNodes() {
        List<String> fieldsToInclude = List.of("member_id", "bio", "member_name", "member_status", "city", "hometown");
        createNodesByCollectionName("members", "Member", fieldsToInclude, true);

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

    public void createTopicNodes() {
        List<String> fieldsToInclude = List.of("topic_id", "description", "link", "topic_name");
        createNodesByCollectionName("topics", "Topic", fieldsToInclude, true);

    }

    public void createEventNodes() {
        List<String> fieldsToInclude = List.of("event_id", "event_name", "event_time", "description", "event_url", "venue.city", "fee"
        );
        createNodesByCollectionName("events", "Event", fieldsToInclude, true);
    }

    public void createGroupNodes() {
        List<String> fieldsToInclude = List.of("group_id", "group_name", "city", "description", "link");
        createNodesByCollectionName("groups", "Group", fieldsToInclude, true);

    }

    //Create Member-Group , Member-Event ,Group-Event
    public void createEdgesFromRsvp() {
        MongoCollection<Document> rsvpsCollection = mongoCSVDatabase.getCollection("rsvps.csv");
        MongoCursor<Document> mongoCursor = rsvpsCollection.find().cursor();
        String member_id;
        String event_id;
        String group_id;
        while (mongoCursor.hasNext()) {
            member_id = mongoCursor.next().getString("member_id");
            event_id = mongoCursor.next().getString("event_id");
            group_id = mongoCursor.next().getString("group_id");

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
                    .withParameters(Map.of("member_id", member_id, "event_id", event_id, "group_id", group_id)).execute();
        }
    }

    //
    public void createMemberTopicsEdges() {
        MongoCollection<Document> collecion = mongoCSVDatabase.getCollection("members_topics.csv");
        MongoCursor<Document> mongoCursor = collecion.find().cursor();
        String member_id;
        String topic_id;

        while (mongoCursor.hasNext()) {
            member_id = mongoCursor.next().getString("member_id");
            topic_id = mongoCursor.next().getString("topic_id");

            driver.executableQuery("""
                    MATCH (member:Member {member_id:$member_id})
                    MATCH (topic:Topic {topic_id:$topic_id})
                    CREATE (member)-[:INTERESTED_IN]->(topic)
                    CREATE (member)<-[:INTERESTED_IN]-(topic)
                    """).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build()).withParameters(Map.of("member_id", member_id, "topic_id", topic_id)).execute();
        }
    }

    //
    public void createGroupTopicsEdges() {
        MongoCollection<Document> collecion = mongoCSVDatabase.getCollection("groups_topics.csv");
        MongoCursor<Document> mongoCursor = collecion.find().cursor();
        String group_id;
        String topic_id;

        while (mongoCursor.hasNext()) {
            group_id = mongoCursor.next().getString("group_id");
            topic_id = mongoCursor.next().getString("topic_id");

            driver.executableQuery("""
                    MATCH (group:Group {group_id:$group_id})
                    MATCH (topic:Topic {topic_id:$topic_id})
                    CREATE (group)-[:INTERESTED_IN]->(topic)
                    CREATE (group)<-[:INTERESTED_IN]-(topic)
                    """).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build()).withParameters(Map.of("group_id", group_id, "topic_id", topic_id)).execute();
        }
    }

    /**
     * This is extra to the edges created from Rsvps.csv , this is based on original members.csv data
     */
    public void createGroupMemberEdges() {
        MongoCollection<Document> collecion = mongoCSVDatabase.getCollection("members.csv");
        MongoCursor<Document> mongoCursor = collecion.find().cursor();
        String group_id;
        String member_id;

        while (mongoCursor.hasNext()) {
            group_id = mongoCursor.next().getString("group_id");
            member_id = mongoCursor.next().getString("member_id");

            driver.executableQuery("""
                    MATCH (group:Group {group_id:$group_id})
                    MATCH (member:Member {member_id:$member_id})
                    CREATE (member)-[:MEMBER_OF]->(group)
                    CREATE (member)<-[:MEMBER_OF]-(group)

                    """).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build()).withParameters(Map.of("group_id", group_id, "member_id", member_id)).execute();
        }
    }
    /**
     * This is extra to the edges created from Rsvps.csv , this is based on group_id contained in original events.csv data
     */
    public void createGroupEventsEdges() {
        MongoCollection<Document> collecion = CsvToMongoTransformer.newMongoDatabase.getCollection("events");
        try (MongoCursor<Document> mongoCursor = collecion
                .find(new Document("creator_group.id", new Document("$exists", true))).cursor()) {

            String group_id;
            String event_id;

            while (mongoCursor.hasNext()) {
                group_id = mongoCursor.next().getEmbedded(List.of("creator_group","id"),String.class);
                event_id = mongoCursor.next().getString("event_id");

                driver.executableQuery("""
                         MATCH (group:Group {group_id:$group_id})
                         MATCH (event:Event {event_id:$event_id})
                        CREATE (group)<-[:ORGANIZES]-(event)
                        CREATE (group)-[:ORGANIZES]->(event)
                        """).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build()).withParameters(Map.of("group_id", group_id, "event_id", event_id)).execute();
            }
        }
    }


    private void createNodesByMongoCursor(MongoCursor<Document> mongoCursor, String nodesName, boolean flattenTheDocument) {
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            document = flattenTheDocument ? CsvToMongoTransformer.flattenDocument(document) : document;
            String queryForNeo4j = "CREATE (p:" + nodesName + "  " +docToNeo4jNode(document) + " )";
            driver.executableQuery(queryForNeo4j).withConfig(QueryConfig.builder().withDatabase(neo4jDatabase).build()).execute();
        }
    }

    private void createNodesByCollectionName(String collectionName, String nodesName, List<String> fieldsToInclude, boolean flattenTheDocument) {
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        Document projection = new Document("_id", false);
        for (String field : fieldsToInclude) projection.append(field, true);
        MongoCursor<Document> mongoCursor = collection.find()
                .projection(projection).cursor();
        createNodesByMongoCursor(mongoCursor, nodesName, flattenTheDocument);
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


}
