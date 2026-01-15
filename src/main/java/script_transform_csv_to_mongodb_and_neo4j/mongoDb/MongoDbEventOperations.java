package script_transform_csv_to_mongodb_and_neo4j.mongoDb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;


public class MongoDbEventOperations {
    public MongoClient mongoClient;
    public MongoDatabase mongoOriginalDatabase;
    public static String newEventCollectionName="events";

    public MongoCollection getNewEventCollection(){
        mongoOriginalDatabase.createCollection(newEventCollectionName);
        return  mongoOriginalDatabase.getCollection(newEventCollectionName);
    }

    public MongoDbEventOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoDbEventOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase, MongoCollection eventCollection) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoDbEventOperations(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        mongoOriginalDatabase = mongoClient.getDatabase("joinUs");
    }

    public Document extractFeeFromEvent(Document oldEvent) {

        Document feeDocument = new Document();
        feeDocument.append("accepts", oldEvent.getString("fee___accepts"));
        feeDocument.append("amount", Double.parseDouble(oldEvent.getString("fee___amount")));
        String currency = oldEvent.getString("fee___currency");

        if (!currency.equalsIgnoreCase("not_found")) {
            feeDocument.append("currency", currency);
        }
        feeDocument.append("description", oldEvent.getString("fee___description"));

        String isFeeRequired = oldEvent.getString("fee___required");
        if (isFeeRequired.equalsIgnoreCase("0")) feeDocument.append("isRequired", false);
        else feeDocument.append("isRequired", true);

        return feeDocument;
    }

    /**
     * Joining groups by name here to be safer ,since we are going to change the ids
     * @param oldEvent
     * @return
     */
    public Document extractCreatorGroupForEvent(Document oldEvent){
        Document groupDocument = new Document();

        MongoCollection groupCollection = CsvToMongoTransformer.csvDocuments.getCollection("groups.csv");

        String groupName= oldEvent.getString("group___name");

        Document document = (Document) groupCollection.find(Filters.eq("group_name",groupName)).first();

        if ( document ==null) return null;

        groupDocument.append("id",document.getString("group_id"));
        groupDocument.append("name",document.getString(
                "group_name"));

        groupDocument.append("link",document.getString("link"));

    return groupDocument;

    }

    public Document extractVenueForEvent(Document oldEvent){

        Document venue = new Document();

        String cityName= oldEvent.getString("venue___city");
        Document city = MongoDbCityOperation.extractCityToEmbedFromCityName(cityName);
        venue.append("city",city);
        CsvToMongoTransformer.assignIfFound(venue,"address_1",oldEvent.getString("venue___address_1"));
        CsvToMongoTransformer.assignIfFound(venue,"address_2",oldEvent.getString("venue___address_2"));
        String phoneNumber = oldEvent.getString("venue___phone");
        if (!phoneNumber.equalsIgnoreCase("-1")) {
            venue.append("phone_number",oldEvent.getString("venue___phone"));
        }

        return venue;


//        venue___address_1
//        "100 Larkin Street"
//        venue___address_2
//        "not_found"
//        venue___city
//        "San Francisco"
//        venue___country
//        "us"
//        venue_id
//        "25559451"
//        venue___lat
//        "0.00000000"
//        venue___localized_country_name
//        "USA"
//        venue___lon
//        "0.00000000"
//        venue___name
//        "San Francisco Public Library, 1st Floor, Stong Room"
//        venue___phone
//        "-1"
//        venue___repinned
//        "0"
//        venue___state
//        "CA"
//        venue___zip
//        "-1"
    }

    public Document extractEventDocument(Document oldEvent) throws ParseException {

        Document event = new Document();

        List<String> fieldsToIncludeDirectly= List.of("event_id",
                "description","event_url","event_name","event_status","event_status");

        for (String key : oldEvent.keySet()){
            if (fieldsToIncludeDirectly.contains(key)){
                event.append(key,oldEvent.getString(key));
            }
        }
      SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date created = simpleDateFormat.parse(oldEvent.getString("created"));
        Date event_time = simpleDateFormat.parse(oldEvent.getString("event_time"));
        Date updated = simpleDateFormat.parse(oldEvent.getString("updated"));

        Instant eventTimeInstant = event_time.toInstant();
        Instant updatedInstant = updated.toInstant();

        //Adding 9 years to simulate "upcoming events" since currently the latest date in the data is 2018
        //TODO modify accordingly
        event_time = Date.from(eventTimeInstant.plus(9*365+2,ChronoUnit.DAYS));
        updated = Date.from(updatedInstant.plus(9*365+2,ChronoUnit.DAYS));

        event.append("created",created);
        event.append("event_time",event_time);
        event.append("updated",updated);

        Long duration = Long.parseLong(oldEvent.getString("duration"));
        Long utcOffset = Long.parseLong(oldEvent.getString("utc_offset"));

        event.append("duration",duration);
        event.append("utc_offset",utcOffset);


        return event;
    }

    /**
     * For now this method gets the category directly from the Group
     * @param oldEvent
     * @return
     */
    public List<Document> extractCategoryForEvent(Document oldEvent){
        Document eventDocument = new Document();

        MongoCollection groupCollection = CsvToMongoTransformer.csvDocuments.getCollection("groups.csv");

        Document group = (Document) groupCollection.find(Filters.eq("group_name",oldEvent.getString("group___name"))).first();


        return new MongoDbGroupOperations(this.mongoClient).extractCategoriesFromGroup(group);
    }
    public long extractMemberCountForEvent(Document oldEvent){
        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");
        String event_id = oldEvent.getString("event_id");
        List<Document> aggregationList =Arrays.asList(
                new Document("$match",
                        new Document("event_id", event_id)),
                new Document("$group",
                        new Document("_id", "$event_id")
                                .append("member_count",
                                        new Document("$sum", 1L))));
        long member_count=0;
        Document document =(Document) rsvpsCollection.aggregate(aggregationList).first();
       if (document!=null ) member_count+= document.getLong("member_count");

     return member_count;

    }

    public void createEventCollection() throws ParseException {

        MongoCollection newEventCollection = getNewEventCollection();

        MongoCollection oldEventCollection = CsvToMongoTransformer.csvDocuments.getCollection("events.csv");
        MongoCursor mongoCursor = oldEventCollection.find().cursor();
        Document newEvent ;
        while (mongoCursor.hasNext()) {
            Document oldDocument = (Document) mongoCursor.next();

            newEvent = extractEventDocument(oldDocument);

            CsvToMongoTransformer.assignIfFound(newEvent,"creator_group",extractCreatorGroupForEvent(oldDocument));
            newEvent.append("categories",extractCategoryForEvent(oldDocument));
            newEvent.append("fee",extractFeeFromEvent(oldDocument));
            newEvent.append("venue",extractVenueForEvent(oldDocument));
            newEvent.append("member_count",extractMemberCountForEvent(oldDocument));
            newEventCollection.insertOne(newEvent);
        }
    }

    public List<Document> extractUpcomingEventsToEmbed(List<String> event_ids){
        List<Document> upcomingEvents = new ArrayList<>();
        MongoCollection<Document> eventCollection = mongoOriginalDatabase.getCollection("events");


        try (MongoCursor<Document> eventCursor = eventCollection.find(
                Filters.and(
                        Filters.in("event_id", event_ids)
                        , Filters.gt("event_time", new Date())
                )
        ).cursor()) {


            while (eventCursor.hasNext()) {
                Document documentToEmbed = new Document();
                Document eventDocument = eventCursor.next();
                Date event_time = eventDocument.getDate("event_time");

    //            if (event_time.after(new Date())) {
                documentToEmbed.append("event_id", eventDocument.getString("event_id"));
                documentToEmbed.append("event_name", eventDocument.getString("event_name"));
                documentToEmbed.append("event_time", event_time);
                upcomingEvents.add(documentToEmbed);
    //            }
            }
        }
        return upcomingEvents;
    }





}
