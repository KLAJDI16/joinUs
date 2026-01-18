package script_transform_csv_to_mongodb_and_neo4j.neo4j;

import com.mongodb.client.MongoClient;
import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import script_transform_csv_to_mongodb_and_neo4j.ConfigurationFileReader;
import script_transform_csv_to_mongodb_and_neo4j.ParallelExecutor;
import script_transform_csv_to_mongodb_and_neo4j.mongoDb.CsvToMongoTransformer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class MongoToNeo4JTransformer {
    private static String uri=ConfigurationFileReader.getNeo4JURL();
    private static String dbUserName= ConfigurationFileReader.getNeo4JUsername();
    private static String dbPassword=ConfigurationFileReader.getNeo4JPassword();
    private static String defaultDatabase=ConfigurationFileReader.getNeo4JDatabase();

    private ParallelExecutor parallelExecutor;

    public MongoToNeo4JTransformer(ParallelExecutor parallelExecutor){
        this.parallelExecutor = parallelExecutor;
    }

    public static Driver driver ;

    private static void initDriver() {
     Config config =    Config.builder()
                .withConnectionTimeout(2, TimeUnit.MINUTES)
                .withMaxConnectionPoolSize(2000)
                .build();

        if (dbUserName!=null &&!dbUserName.isEmpty() && dbPassword!=null && !dbPassword.isEmpty() ){
        driver = GraphDatabase.driver(uri, AuthTokens.basic(dbUserName,dbPassword),config);}
        else {
            driver=GraphDatabase.driver(uri,config);
        }
    }
    public static   Driver getNeo4jDriver(){
        if (driver==null) {
            initDriver();
        }
        return driver;
    }



    public void transformMongoDataToNeo4j() throws Exception {
        CsvToMongoTransformer.verifyMongoDatabaseAndCollections();
        Neo4JOperations neo4JOperations = new Neo4JOperations(getNeo4jDriver(),defaultDatabase,parallelExecutor);

//        System.out.println("CREATING Nodes");
        Future[] futures = new Future[5];

//        futures[0] = parallelExecutor.submit(() -> neo4JOperations.createMemberNodes());
//        futures[1] = parallelExecutor.submit(() -> neo4JOperations.createEventNodes());
//        futures[2] = parallelExecutor.submit(() -> neo4JOperations.createGroupNodes());
//        futures[3] = parallelExecutor.submit(() -> neo4JOperations.createTopicNodes());
//
//        ParallelExecutor.getFutures(futures);

        System.out.println("CREATING Edges ");

//        futures[0] = parallelExecutor.submit(() -> neo4JOperations.createEdgesFromRsvp());
//        futures[1] = parallelExecutor.submit(() -> neo4JOperations.createGroupEventsEdges());
//        futures[2] = parallelExecutor.submit(() -> neo4JOperations.createGroupTopicsEdges());
//        futures[3] = parallelExecutor.submit(() -> neo4JOperations.createGroupMemberEdges());
//        futures[4] = parallelExecutor.submit(() -> neo4JOperations.createMemberTopicsEdges());
     parallelExecutor.submit( () ->     Neo4JOperations.createGroupTopicsEdge());
      parallelExecutor.submit( () ->    Neo4JOperations.createMemberGroupsEdge());

          ParallelExecutor.getFutures(futures);
        System.out.println("Finished CREATING Edges ");

        neo4JOperations.mongoClient.close();
        this.driver.close();

    }
}


















