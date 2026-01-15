package script_transform_csv_to_mongodb_and_neo4j.mongoDb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoDbTopicOperation {
    public MongoClient mongoClient;
    public MongoDatabase mongoOriginalDatabase;
    public static String newTopicCollectionName ="topics";

    public MongoDbTopicOperation(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoCollection getNewTopicCollection() {
        mongoOriginalDatabase.createCollection(newTopicCollectionName);
        MongoCollection mongoCollection = mongoOriginalDatabase.getCollection(newTopicCollectionName);
        return mongoCollection;
    }


    public MongoDbTopicOperation(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        mongoOriginalDatabase = mongoClient.getDatabase("joinUs");
    }



    public Document extractTopicDocument(Document oldDocument){
        if (oldDocument==null || oldDocument.isEmpty()) return new Document();
        Document documentToReturn = new Document();
        List<String> keysToIncludeDirectly=List.of("topic_id", "description",
                "link","topic_name","urlkey");
        for (String key:oldDocument.keySet()){
            if (keysToIncludeDirectly.contains(key))
             documentToReturn.append(key,oldDocument.getString(key));
        }
        long member_count=Long.parseLong(oldDocument.getString("members"));
        documentToReturn.append("member_count",member_count);


        return documentToReturn;
    }

    public void createTopicCollection(){
        MongoCollection newCollection = getNewTopicCollection();
        MongoCollection oldCollection= CsvToMongoTransformer.csvDocuments.getCollection("topics.csv");
        MongoCursor mongoCursor = oldCollection.find().cursor();
        Document newDocument;
        Document oldDocument;
        while (mongoCursor.hasNext()){
            oldDocument=(Document) mongoCursor.next();
            newDocument=extractTopicDocument(oldDocument);
            newCollection.insertOne(newDocument);
        }
    }
}
