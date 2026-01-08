package script_transform_csv_to_mongodb_and_neo4j;

import script_transform_csv_to_mongodb_and_neo4j.mongoDb.CsvToMongoTransformer;
import script_transform_csv_to_mongodb_and_neo4j.neo4j.MongoToNeo4JTransformer;

public class DataTransformerScript {
    public static void main(String[] args) throws Exception {
        new CsvToMongoTransformer().tranformCsvDataToMongoDB();
        new MongoToNeo4JTransformer().transformMongoDataToNeo4j();
    }
}
