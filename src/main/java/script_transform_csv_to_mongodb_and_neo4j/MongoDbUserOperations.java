package script_transform_csv_to_mongodb_and_neo4j;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

public class MongoDbUserOperations {
    public MongoClient mongoClient;
    public MongoDatabase mongoOriginalDatabase;
    public static String newUserCollectionName="members";

    public MongoCollection getNewUserCollection(){
        mongoOriginalDatabase.createCollection(newUserCollectionName);
        return  mongoOriginalDatabase.getCollection(newUserCollectionName);
    }
    public MongoDbUserOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoDbUserOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase, MongoCollection userCollection) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
    }

    public MongoDbUserOperations(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        mongoOriginalDatabase = mongoClient.getDatabase("joinUs");
    }

    /**
     * As we agreed we would embed topics to a member in MongoDB for aggregations
     * purpose although we also have them in the GraphDB
     *
     * @param memberId
     * @return
     */
    public List<Document> extractTopicPerMember(String memberId) {

        MongoCollection topicCollection = CsvToMongoImporter.csvDocuments.getCollection("members_topics.csv");

        List<Document> userTopics = new ArrayList<>();
        MongoCursor cursor = topicCollection
                .find(new Document("member_id", memberId))
                .projection(new Document("topic_id", 1).append("topic_key", 1))
                .cursor();
        while (cursor.hasNext()) {
            userTopics.add((Document) cursor.next());
        }

        return userTopics;
    }

    /**
     * For now this method counts the groups the user is from both the first Dataset (members.csv)
     * and the second dataset (rspvs.csv) .Might be changed later.
     *
     * @param memberId
     * @return
     */
    public long extractGroupCountPerMember(String memberId, List<Document> members) {
        //rsvps.csv
        MongoCollection rsvpsCollection = CsvToMongoImporter.csvDocuments.getCollection("rsvps.csv");

        List<Document> aggregationList = Arrays.asList(new Document("$match",
                        new Document("member_id", memberId)),
                new Document("$group",
                        new Document("_id",
                                new Document("member_id", "$group_id"))),
                new Document("$count", "groupCount"));
        int groupCountFromRsvps = 0;
        long groupCountFromMembers=0;
        MongoCursor mongoCursor = rsvpsCollection.aggregate(aggregationList).cursor();

        if (mongoCursor.hasNext()) {
          groupCountFromMembers = ((Document) mongoCursor.next()).getInteger("groupCount");
        }
        if (members != null) {
            groupCountFromMembers = members.size();
        } else {
            MongoCollection membersCollection = CsvToMongoImporter.csvDocuments.getCollection("members.csv");
            groupCountFromMembers = membersCollection.countDocuments(new Document("member_id", memberId));
        }
        return groupCountFromMembers + groupCountFromRsvps;
    }

    public int extractEventCountPerMember(String memberId) {
        //rsvps.csv
        MongoCollection rsvpsCollection = CsvToMongoImporter.csvDocuments.getCollection("rsvps.csv");

        List<Document> aggregationList = Arrays.asList(new Document("$match",
                        new Document("member_id", memberId)),
                new Document("$group",
                        new Document("_id",
                                new Document("member_id", "$event_id"))),
                new Document("$count", "eventCount"));


        int eventCountFromRsvps=0;

        MongoCursor mongoCursor = rsvpsCollection.aggregate(aggregationList).cursor();

        if (mongoCursor.hasNext()) {
        eventCountFromRsvps=((Document) mongoCursor.next()).getInteger("eventCount");
        }
        return eventCountFromRsvps;
    }

    /**
     * Should call this after modifying the events  ,
     * so that the event_time makes sense, and thus we avoid returning empty lists
     *
     * @param memberId
     * @return
     */
    public List<Document> extractFutureEventsPerMember(String memberId) {
        //rsvps.csv
        MongoCollection rsvpsCollection = CsvToMongoImporter.csvDocuments.getCollection("rsvps.csv");
        List<String> eventIdsFromRsvps = new ArrayList<>();
        MongoCursor cursor = rsvpsCollection.find(Filters.eq("member_id", memberId)).cursor();
        List<Document> eventDocumentsToEmbeddToUser = new ArrayList<>();

        if (cursor != null) {
            while (cursor.hasNext()) {
                eventIdsFromRsvps.add(((Document) cursor.next()).get("event_id", String.class));
            }

            MongoCollection eventCollection = mongoOriginalDatabase.getCollection("event");


            List<Document> aggregationList = Arrays.asList(
                    new Document("$match", Filters.in("event_id", eventIdsFromRsvps)));



            MongoCursor eventCursor = eventCollection.find(Filters.and(Filters.in("event_id",eventIdsFromRsvps),Filters.gt("event_time",new Date()))).cursor();

            List<Document> upcomingEvents=new ArrayList<>();

            if (eventCursor != null) {
                while (eventCursor.hasNext()) {
                    Document documentToEmbed = new Document();
                    Document eventDocument = (Document) eventCursor.next();

                    documentToEmbed.append("event_id",eventDocument.getString("event_id"));
                    documentToEmbed.append("event_name",eventDocument.getString("event_name"));
                    documentToEmbed.append("event_time",eventDocument.getDate("event_time"));

                    upcomingEvents.add(documentToEmbed);
                }
            }
        }
        return eventDocumentsToEmbeddToUser;

    }


    public Document extractCityDocumentToEmbedPerMember(String memberId, List<Document> members) {

        String cityName;
        if (members != null && !members.isEmpty()) {
            Document member = members.get(0);
            cityName = member.getString("city");
        } else {
            MongoCollection memberCollection = CsvToMongoImporter.csvDocuments.getCollection("members.csv");
            cityName = ((Document) memberCollection.find(Filters.eq("member_id", memberId))
                    .first()).getString("city");
        }
        return MongoDbCityOperation.extractCityToEmbedFromCityName(cityName);
    }

    /**
     * To extract the needed fields that we are going to use for  the member in the final database (joinUs)
     *
     * @param memberId
     * @param members  to avoid unnecessary iteration and therefore performance degradation .
     * @return
     */
    public Document extractMemberDocument(String memberId, List<Document> members) {

        Document document;
        if (members != null && !members.isEmpty()) {
            document = new Document();
            Document member = members.get(0);
            document.append("member_id", member.getString("member_id"));
            document.append("member_name", member.getString("member_name"));
            document.append("member_status", member.getString("member_status"));
            document.append("hometown", member.getString("hometown"));
            document.append("link", member.getString("link"));
            document.append("bio", member.getString("bio"));
        } else {

            MongoCollection memberCollection = CsvToMongoImporter.csvDocuments.getCollection("members.csv");
            document = ((Document) memberCollection.find(Filters.eq("member_id", memberId))
                    .projection(new Document("member_id", 1).append("member_name", 1)
                            .append("member_status", 1).append("_id", 0).append("bio", 1).append("hometown", 1).append("link", 1))
                    .first());
        }

        if (!document.isEmpty() && document.getString("bio").equalsIgnoreCase("not_found")) {
            document.remove("bio");
        }
        return document;
    }

    public void createMemberCollection() {
        MongoCollection oldMemberCollection = CsvToMongoImporter.csvDocuments.getCollection("members.csv");
        MongoCollection newMemberCollection = getNewUserCollection();
        Document finalDocument = new Document();
        MongoCursor mongoCursor = oldMemberCollection.find().cursor();
        List<Document> membersWithId = new ArrayList<>();
        Set<String> membersAlreadyEvaluated = new HashSet<>();
        String memberId;
        while (mongoCursor.hasNext()) {
            Document document = (Document) mongoCursor.next();
            memberId = document.getString("member_id");
            if (!membersAlreadyEvaluated.contains(memberId)) {
                membersWithId = getMembersWithMemberId(memberId);
                finalDocument = extractMemberDocument(memberId, membersWithId);
                finalDocument.append("topics", this.extractTopicPerMember(memberId));
                finalDocument.append("city", this.extractCityDocumentToEmbedPerMember(memberId, membersWithId));
                finalDocument.append("event_count", this.extractEventCountPerMember(memberId));
                finalDocument.append("upcoming_events", this.extractFutureEventsPerMember(memberId));
                finalDocument.append("group_count", this.extractGroupCountPerMember(memberId, membersWithId));
                if (finalDocument != null) {
                    newMemberCollection.insertOne(finalDocument);
                }
                membersAlreadyEvaluated.add(memberId);
            }
        }
    }

    public List<Document> getMembersWithMemberId(String member_id) {
        List<Document> documentList = new ArrayList<>();
        MongoCollection oldMemberCollection = CsvToMongoImporter.csvDocuments.getCollection("members.csv");
        MongoCursor mongoCursor = oldMemberCollection.find(Filters.eq("member_id", member_id)).cursor();
        while (mongoCursor.hasNext()) {
            Document document = (Document) mongoCursor.next();
            documentList.add(document);
        }
        return documentList;
    }


    public static List retrieveIdsFromMetaMembers(){
        MongoCollection metaMembersCollection= CsvToMongoImporter.csvDocuments.getCollection("meta-members.csv");
        List<String> IdsFromMetaMembersColl = new ArrayList<>();

                 MongoCursor mongoCursor = metaMembersCollection.find()
                         .projection(new Document("member_id",true).append("_id",false)).cursor();
                 if(mongoCursor !=null) {
                     while (mongoCursor.hasNext()) {
            IdsFromMetaMembersColl.add(((Document)mongoCursor.next()).get("member_id",String.class));
                     }
                 }
                 return IdsFromMetaMembersColl;
    }
    public static List retrieveIdsFromMembers(int limit){
        MongoCollection metaMembersCollection= CsvToMongoImporter.csvDocuments.getCollection("members.csv");
        List<String> IdsFromMetaMembersColl = new ArrayList<>();

        MongoCursor mongoCursor = metaMembersCollection.find()
                .projection(new Document("member_id",true).append("_id",false)).cursor();
        if(mongoCursor !=null) {
            int count=0;
            while (mongoCursor.hasNext()) {
                IdsFromMetaMembersColl.add(((Document)mongoCursor.next()).get("member_id",String.class));
                count++;
                if (count>=limit && limit!=-1) break;
            }
        }
        return IdsFromMetaMembersColl;
    }
    public static List updateRandomIdsForMembers(){
        MongoCollection metaMembersCollection= CsvToMongoImporter.csvDocuments.getCollection("members.csv");


       List<String> IdsFromMetaMembersColl = retrieveIdsFromMetaMembers();
       int totalMetaMembers=IdsFromMetaMembersColl.size();
       List<String> membersId = retrieveIdsFromMembers(-1);
       int totalMembers = membersId.size();

       int membersChosen=0;


        Set<Integer> randomNumbers = new HashSet<>();
        Set<String> chosenMembers = new HashSet<>();

        Random random = new Random();
        int value = -1;
        String chosenMember;
          while (membersChosen<=totalMetaMembers){
            do{
                chosenMember = membersId.get(random.nextInt(totalMembers));
            }
             while (chosenMembers.contains(chosenMember));


              chosenMembers.add(chosenMember);

              metaMembersCollection.updateMany(
                      Filters.eq("member_id", chosenMember),
                      new Document("$set",
                              new Document("member_id", IdsFromMetaMembersColl.get(membersChosen))
                      )
              );

             membersChosen++;
          }
return IdsFromMetaMembersColl;
    }


//        public void allUserOperations () {
////        for ()
//        }


}
