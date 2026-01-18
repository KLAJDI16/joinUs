package script_transform_csv_to_mongodb_and_neo4j;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import script_transform_csv_to_mongodb_and_neo4j.mongoDb.CsvToMongoTransformer;
import script_transform_csv_to_mongodb_and_neo4j.neo4j.MongoToNeo4JTransformer;

import java.util.Date;

public class DataTransformerScript {
    public static void main(String[] args) throws Exception {
        long startTime=System.currentTimeMillis();
        System.out.println("STARTING TIME "+new Date());
        ParallelExecutor parallelExecutor = new ParallelExecutor();
        new CsvToMongoTransformer(parallelExecutor).transformCsvDataToMongoDB();
        new MongoToNeo4JTransformer(parallelExecutor).transformMongoDataToNeo4j();
        parallelExecutor.close();
        long endTime=System.currentTimeMillis();
        System.out.println("THE PROCESS TOOK "+(endTime-startTime)/1000+" seconds");
    }
}
