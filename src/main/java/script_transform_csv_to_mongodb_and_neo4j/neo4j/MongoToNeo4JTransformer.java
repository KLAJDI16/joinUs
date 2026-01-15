package script_transform_csv_to_mongodb_and_neo4j.neo4j;

import com.mongodb.client.MongoClient;
import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import script_transform_csv_to_mongodb_and_neo4j.mongoDb.CsvToMongoTransformer;

import java.util.concurrent.TimeUnit;


public class MongoToNeo4JTransformer {
    private static String uri="neo4j://127.0.0.1:7687";
    private static String dbUserName="neo4j";
    private static String dbPassword="Klajdi2003.";
    private static String defaultDatabase="neo4j";

    public  Driver driver ;

    private  void initDriver() {
     Config config =    Config.builder()
                .withConnectionTimeout(2, TimeUnit.MINUTES)
                .withMaxConnectionPoolSize(100)

                .build();

        if (dbUserName!=null &&!dbUserName.isEmpty() && dbPassword!=null && !dbPassword.isEmpty() ){
        driver = GraphDatabase.driver(uri, AuthTokens.basic(dbUserName,dbPassword),config);}
        else {
            driver=GraphDatabase.driver(uri,config);
        }
    }
    public  Driver getNeo4jDriver(){
        if (driver==null) {
            initDriver();
        }
        return driver;
    }



    public void transformMongoDataToNeo4j() throws Exception {
        CsvToMongoTransformer.verifyMongoDatabaseAndCollections();
        Neo4JOperations neo4JOperations = new Neo4JOperations(getNeo4jDriver(),defaultDatabase);
        Thread thread1 = new Thread(() -> neo4JOperations.createMemberNodes());
        Thread thread2 = new Thread(() -> neo4JOperations.createEventNodes());
        Thread thread3 = new Thread(() -> neo4JOperations.createGroupNodes());
        Thread thread4 = new Thread(() -> neo4JOperations.createTopicNodes());

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();

        neo4JOperations.createEdgesFromRsvp();
        neo4JOperations.createGroupEventsEdges();
        neo4JOperations.createGroupTopicsEdges();
        neo4JOperations.createGroupMemberEdges();
        neo4JOperations.createMemberTopicsEdges();

        neo4JOperations.mongoClient.close();
        this.driver.close();

    }
}


















