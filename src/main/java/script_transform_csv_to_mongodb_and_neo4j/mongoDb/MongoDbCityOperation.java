package script_transform_csv_to_mongodb_and_neo4j.mongoDb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.List;

public class MongoDbCityOperation {
    public MongoClient mongoClient;
    public MongoDatabase mongoOriginalDatabase;
    public static String newCityCollectionName="cities";

    public MongoDbCityOperation(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoCollection getCityCollection() {
        mongoOriginalDatabase.createCollection(newCityCollectionName);
        MongoCollection mongoCollection = mongoOriginalDatabase.getCollection(newCityCollectionName);
        return mongoCollection;
    }


    public MongoDbCityOperation(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        mongoOriginalDatabase = mongoClient.getDatabase("joinUs");
    }

    protected static Document extractCityToEmbedFromId(String cityId){
        MongoCollection cityCollection = CsvToMongoTransformer.csvDocuments.getCollection("cities.csv");

        return (Document) cityCollection.find(Filters.eq("city_id", cityId))
                .projection(new Document("_id", 0).append("member_count", 0).append("ranking", 0)).first();
    }
    protected static Document extractCityToEmbedFromCityName(String cityName){
        MongoCollection cityCollection = CsvToMongoTransformer.csvDocuments.getCollection("cities.csv");

        return (Document) cityCollection.find(Filters.eq("city", cityName))
                .projection(new Document("_id", 0).append("member_count", 0).append("ranking", 0)).first();
    }

    private Document extractCityDocument(Document oldDocument){
        if (oldDocument==null || oldDocument.isEmpty()) return new Document();
        Document documentToReturn = new Document();
        List<String> keysToIncludeDirectly=List.of("country",
                "localized_country_name","zip","state");
        for (String key:oldDocument.keySet()){
            if (keysToIncludeDirectly.contains(key))
            documentToReturn.append(key,oldDocument.getString(key));
        }
        documentToReturn.append("name",oldDocument.get("city"));
        documentToReturn.append("id",oldDocument.get("city_id"));

        double ranking = Double.parseDouble(oldDocument.getString("ranking"));
        long member_count=Long.parseLong(oldDocument.getString("member_count"));
        double distance = Double.parseDouble(oldDocument.getString("distance"));
        double latitude = Double.parseDouble(oldDocument.getString("latitude"));
        double longitude = Double.parseDouble(oldDocument.getString("longitude"));

        documentToReturn.append("distance",distance);
        documentToReturn.append("ranking",ranking);
        documentToReturn.append("member_count",member_count);
        documentToReturn.append("latitude",latitude);
        documentToReturn.append("longitude",longitude);

        return documentToReturn;
    }

    public void createCityCollection(){
        MongoCollection newCollection = getCityCollection();
        MongoCollection oldCollection= CsvToMongoTransformer.csvDocuments.getCollection("cities.csv");
        MongoCursor mongoCursor = oldCollection.find().cursor();
        Document newDocument;
        Document oldDocument;
        while (mongoCursor.hasNext()){
            oldDocument=(Document) mongoCursor.next();
            newDocument=extractCityDocument(oldDocument);
            newCollection.insertOne(newDocument);
        }
    }

}
