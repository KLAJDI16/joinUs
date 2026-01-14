package script_transform_csv_to_mongodb_and_neo4j.mongoDb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MongoDbGroupOperations {
    public MongoClient mongoClient;
    public MongoDatabase mongoOriginalDatabase;
    public static String newGroupCollectionName="groups";

    public MongoCollection getNewGroupsCollection(){
        mongoOriginalDatabase.createCollection(newGroupCollectionName);
        return  mongoOriginalDatabase.getCollection(newGroupCollectionName);
    }
    public MongoDbGroupOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoDbGroupOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase, MongoCollection eventCollection) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoDbGroupOperations(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        mongoOriginalDatabase = mongoClient.getDatabase("joinUs");
    }

    public  List<Document> extractCategoriesFromGroup(Document oldGroupDocument){
        if (oldGroupDocument==null){
//            System.out.println("PAUSE HERE");
            return new ArrayList<>();
        }
        Document category = new Document();

        List<Document> categories=new ArrayList<>();

        category.append("category_id",oldGroupDocument.getString("category_id"))
                .append("name",oldGroupDocument.getString("category___name"));

        categories.add(category);

        return categories;
    }


    /**
     * Adds members from the first dataset groups.csv ("members")
     * and counts members from the second dataset rsvps.csv
     * @param oldGroupDocument
     * @return
     */
    public int extractMemberCount(Document oldGroupDocument){


        int directMemberCount= Integer.parseInt(oldGroupDocument.getString("members"));
        String group_id=oldGroupDocument.getString("group_id");
        int membersFromRsvps = 0;
        //rsvps.csv
        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");

        List<Document> aggregationList = Arrays.asList(new Document("$match",
                        new Document("group_id", group_id)),
                new Document("$group",
                        new Document("_id",
                                new Document("group_id", "$member_id"))),
                new Document("$count", "memberCount"));
        MongoCursor mongoCursor = rsvpsCollection.aggregate(aggregationList).cursor();

        if (mongoCursor.hasNext()) {
            membersFromRsvps = ((Document) mongoCursor.next()).getInteger("memberCount");
        }

        return membersFromRsvps+directMemberCount;

    }

    public List<Document> extractUpcomingEventsFromEvents_csv(String group_id){
        MongoCollection eventsCollection = CsvToMongoTransformer.csvDocuments.getCollection("events.csv");
        List<String> eventIdsFromEvents = new ArrayList<>();
        MongoCursor cursor = eventsCollection.find(Filters.eq("group_id", group_id)).cursor();
        List<Document> upcomingEvents=new ArrayList<>();
        while (cursor.hasNext()) {
            eventIdsFromEvents.add(((Document) cursor.next()).getString("event_id"));
        }

        return new MongoDbEventOperations(mongoClient).extractUpcomingEventsToEmbed(eventIdsFromEvents);

    }

    public List<Document> extractUpcomingEventsFromRSVPS_csv(String group_id){
        //rsvps.csv
        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");
        List<String> eventIdsFromRsvps = new ArrayList<>();
        MongoCursor cursor = rsvpsCollection.find(Filters.eq("group_id", group_id)).cursor();


        while (cursor.hasNext()) {
            eventIdsFromRsvps.add(((Document) cursor.next()).getString("event_id"));
        }

        return new MongoDbEventOperations(mongoClient).extractUpcomingEventsToEmbed(eventIdsFromRsvps);
    }
    public List<Document> extractUpcomingEvents(Document oldGroupDocument){

        if (oldGroupDocument == null || oldGroupDocument.isEmpty()) return new ArrayList<>();

        String group_id=oldGroupDocument.getString("group_id");

        List<Document> upcomingEvents = new ArrayList<>();
        upcomingEvents.addAll(extractUpcomingEventsFromRSVPS_csv(group_id));
        upcomingEvents.addAll(extractUpcomingEventsFromEvents_csv(group_id));
        return upcomingEvents;
    }

    //We can use this method if we want to do the same as for the user (although we have categories for the aggregations)
    public List<Document> extractTopicsForGroups(Document oldGroupDocument){
        if (oldGroupDocument == null || oldGroupDocument.isEmpty()) return new ArrayList<>();


        String group_id=oldGroupDocument.getString("group_id");
        MongoCollection topicCollection = CsvToMongoTransformer.csvDocuments.getCollection("groups_topics.csv");

        List<Document> groupTopics = new ArrayList<>();
        MongoCursor cursor = topicCollection
                .find(new Document("group_id", group_id))
                .projection(new Document("topic_id", 1).append("topic_key", 1))
                .cursor();
        while (cursor.hasNext()) {
            groupTopics.add((Document) cursor.next());
        }

        return groupTopics;
    }

    public Document extractCityForGroup(Document oldGroupDocument){
        String group_id=oldGroupDocument.getString("group_id");
        String cityName=oldGroupDocument.getString("city");
        return MongoDbCityOperation.extractCityToEmbedFromCityName(cityName);
    }

    public int extractEventCount(Document oldGroupDocument){
        String group_id=oldGroupDocument.getString("group_id");
    int event_count=0;
        int eventCountFromRsvps=extractEventCountFromRsvps_csv(group_id);
        int eventCountFromEventCsv=extractEventCountFromEvents_csv(group_id);

        event_count=eventCountFromEventCsv+eventCountFromRsvps;

        return eventCountFromRsvps;
    }
    public   int extractEventCountFromRsvps_csv(String group_id){
        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");

        List<Document> aggregationList = Arrays.asList(new Document("$match",
                        new Document("group_id", group_id)),
                new Document("$group",
                        new Document("_id",
                                new Document("group_id", "$event_id"))),
                new Document("$count", "eventCount"));

        int eventCountFromRsvps=0;

        MongoCursor mongoCursor = rsvpsCollection.aggregate(aggregationList).cursor();

        if (mongoCursor.hasNext()) {
            eventCountFromRsvps=((Document) mongoCursor.next()).getInteger("eventCount");
        }

        return eventCountFromRsvps;
    }
    public  int extractEventCountFromEvents_csv(String group_id){
        MongoCollection eventsCollection = CsvToMongoTransformer.csvDocuments.getCollection("events.csv");

        List<Document> aggregationList = Arrays.asList(new Document("$match",
                        new Document("group_id", group_id)),
                new Document("$count", "eventCount"));

        int eventCountFromEventsCsv=0;

        MongoCursor mongoCursor = eventsCollection.aggregate(aggregationList).cursor();

        if (mongoCursor.hasNext()) {
            eventCountFromEventsCsv=((Document) mongoCursor.next()).getInteger("eventCount");
        }

        return eventCountFromEventsCsv;
    }

    public List<Document> extractOrganizer(Document oldGroupDocument){
        List<Document> organizers=new ArrayList<>();
//        String group_id=oldGroupDocument.getString("group_id");
        Document organizer = new Document();
        organizer.append("name",oldGroupDocument.getString("organizer___name"));
        organizer.append("id",oldGroupDocument.getString("organizer___member_id"));

        organizers.add(organizer);

        return organizers;
    }

    public Document extractGroupDocument(Document oldGroupDocument) throws ParseException {
        List<String> directKeysToInclude=Arrays.asList(
                "group_id","group_name","timezone","link");
        Document newGroupDocument=new Document();
        for (String key : oldGroupDocument.keySet()){
            if (directKeysToInclude.contains(key)){
                newGroupDocument.append(key,oldGroupDocument.getString(key));
            }
        }
        CsvToMongoTransformer.assignIfFound(newGroupDocument,"description",oldGroupDocument.getString("description"));

        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date created = simpleDateFormat.parse(oldGroupDocument.getString("created"));

        newGroupDocument.append("created",created);

        Double rating = Double.parseDouble(oldGroupDocument.getString("rating"));
        Long utcOffset = Long.parseLong(oldGroupDocument.getString("utc_offset"));
        newGroupDocument.append("rating",rating);
        newGroupDocument.append("utc_offset",utcOffset);

        return newGroupDocument;
    }

    public Document extractGroupPhoto(Document oldGroupDocument){
        Document photo = new Document();
        for (String key : oldGroupDocument.keySet()){
            if (key.startsWith("group_photo___")){
                String newKey=key.substring("group_photo___".length());
                CsvToMongoTransformer.assignIfFound(photo,newKey,oldGroupDocument.getString(key));
            }
        }
        return photo;
    }

    public void createGroupCollection() throws ParseException {

        MongoCollection groupCollection = getNewGroupsCollection();
        Document newGroupDocument ;
        MongoCursor mongoCursor = CsvToMongoTransformer.csvDocuments.getCollection("groups.csv").find().cursor();
        while (mongoCursor.hasNext()){
            Document oldGroupDocument = (Document) mongoCursor.next();
            newGroupDocument=extractGroupDocument(oldGroupDocument);
            newGroupDocument.append("city",extractCityForGroup(oldGroupDocument));
            newGroupDocument.append("categories",extractCategoriesFromGroup(oldGroupDocument));
            newGroupDocument.append("event_count",extractEventCount(oldGroupDocument));
            newGroupDocument.append("member_count",extractMemberCount(oldGroupDocument));
            newGroupDocument.append("organizer_members",extractOrganizer(oldGroupDocument));
            newGroupDocument.append("upcoming_events",extractUpcomingEvents(oldGroupDocument));
            newGroupDocument.append("group_photo",extractGroupPhoto(oldGroupDocument));
//            newGroupDocument.append("topcis",extractTopicsForGroups(oldGroupDocument));//might do this if we want

        groupCollection.insertOne(newGroupDocument);
        }
    }


}
