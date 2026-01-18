package script_transform_csv_to_mongodb_and_neo4j.mongoDb;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import script_transform_csv_to_mongodb_and_neo4j.ParallelExecutor;
import script_transform_csv_to_mongodb_and_neo4j.neo4j.Neo4JOperations;

import java.util.*;
import java.util.concurrent.Future;


public class MongoDbUserOperations {
    public MongoClient mongoClient;
    public MongoDatabase mongoOriginalDatabase;
    public static String newUserCollectionName="members";
    public static List<String> metaMembersIds;
    private final ParallelExecutor parallelExecutor;

    public MongoCollection getNewUserCollection(){
        mongoOriginalDatabase.createCollection(newUserCollectionName);
        return  mongoOriginalDatabase.getCollection(newUserCollectionName);
    }
    public MongoDbUserOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase, ParallelExecutor parallelExecutor) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
        this.parallelExecutor = parallelExecutor;
    }

    public MongoDbUserOperations(MongoClient mongoClient, MongoDatabase mongoOriginalDatabase, MongoCollection userCollection, ParallelExecutor parallelExecutor) {
        this.mongoClient = mongoClient;
        this.mongoOriginalDatabase = mongoOriginalDatabase;
        this.parallelExecutor = parallelExecutor;
    }

    public MongoDbUserOperations(MongoClient mongoClient, ParallelExecutor parallelExecutor) {
        this.mongoClient = mongoClient;
        mongoOriginalDatabase = mongoClient.getDatabase("joinUs");
        this.parallelExecutor = parallelExecutor;
    }

    /**
     * As we agreed we would embed topics to a member in MongoDB for aggregations
     * purpose although we also have them in the GraphDB
     *
     * @param memberId
     * @return
     */
    public   List<Document> extractTopicPerMember(String memberId) {

        MongoCollection topicCollection = CsvToMongoTransformer.csvDocuments.getCollection("members_topics.csv");

        List<Document> userTopics = new ArrayList<>();
        try (MongoCursor<Document> cursor = topicCollection
                .find(new Document("member_id", memberId))
                .projection(new Document("topic_id", 1).append("topic_key", 1))
                .cursor()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
           parallelExecutor.submit( () ->     Neo4JOperations.createMemberTopicEdge(memberId,document.getString("topic_id")));
                userTopics.add( document);
            }
        }

        return userTopics;
    }


    public HashMap<String,Double> extractGroupCountPerMember() {

        HashMap<String,Double> groupCount=new HashMap<>();

//        //rsvps.csv
//        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");
//
//        List<Document> aggregationList = Arrays.asList(new Document("$group",
//                        new Document("_id", "$member_id")
//                                .append("groupCount",
//                                        new Document("$sum",
//                                                new Document("$cond", Arrays.asList(new Document("$ifNull", Arrays.asList("$group_id", false)), 1L, 0L))))),
//                new Document("$project",
//                        new Document("member_id", "$_id")
//                                .append("groupCount",
//                                        new Document("$toDouble", "$groupCount"))
//                                .append("_id", 0L)));
//        int groupCountFromRsvps = 0;
//        long groupCountFromMembers=0;
//        try (MongoCursor<Document> mongoCursor = rsvpsCollection.aggregate(aggregationList).cursor()) {
//
//            if (mongoCursor.hasNext()) {
//                Document document = mongoCursor.next();
//                groupCount.put(document.getString("member_id"), document.getDouble("groupCount"));
//            }
//        }

       List<Document> membersAggregationList= Arrays.asList(new Document("$group",
                        new Document("_id", "$member_id")
                                .append("groupCount",
                                        new Document("$addToSet", "$group_id"))),
                new Document("$project",
                        new Document("_id", 0L)
                                .append("member_id", "$_id")
                                .append("groupCount",
                                        new Document("$size", "$groupCount"))),
                new Document("$project",
                        new Document("member_id", 1L)
                                .append("groupCount",
                                        new Document("$toDouble", "$groupCount"))));


        MongoCollection membersCollection = CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
            MongoCursor<Document> cursor = membersCollection.aggregate(membersAggregationList).cursor();

            while (cursor.hasNext()){
                Document document = cursor.next();
                String member_id = document.getString("member_id");
                groupCount.put(member_id,document.getDouble("groupCount"));
            }


        return groupCount;
    }

    public HashMap<String,Double> extractEventCountPerMember() {
        //rsvps.csv
        HashMap<String,Double> eventCount = new HashMap<>();
        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");

        List<Document> aggregationList = Arrays.asList(new Document("$group",
                        new Document("_id", "$member_id")
                                .append("count",
                                        new Document("$sum", 1L))),
                new Document("$project",
                        new Document("member_id", "$_id")
                                .append("event_count",
                                        new Document("$toDouble", "$count"))
                                .append("_id", 0L)));


        try (MongoCursor<Document> mongoCursor = rsvpsCollection.aggregate(aggregationList).cursor()) {

            if (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                eventCount.put(document.getString("member_id"),document.getDouble("event_count"));
            }
        }

        return eventCount;
    }

    /**
     * Should call this after modifying the events  ,
     * so that the event_time makes sense, and thus we avoid returning empty lists
     *
     * @param memberId
     * @return
     */
    public  List<Document> extractFutureEventsPerMember(String memberId) {
        //rsvps.csv
        MongoCollection rsvpsCollection = CsvToMongoTransformer.csvDocuments.getCollection("rsvps.csv");
        List<String> eventIdsFromRsvps = new ArrayList<>();
        List<Document> upcomingEvents;
        try (MongoCursor<Document> cursor = rsvpsCollection.find(Filters.eq("member_id", memberId)).cursor()) {
            upcomingEvents = new ArrayList<>();
            if (cursor != null) {
                while (cursor.hasNext()) {
                    String event_id = ( cursor.next()).getString("event_id");
              parallelExecutor.submit( () ->       Neo4JOperations.createMemberEventEdge(event_id,memberId));
                    eventIdsFromRsvps.add(event_id);
                }

                MongoCollection eventCollection = CsvToMongoTransformer.newMongoDatabase.getCollection("events");

                MongoCursor eventCursor = eventCollection.find
                        (
                                Filters.and(
                                        Filters.in("event_id", eventIdsFromRsvps)
                                        , Filters.gt("event_time", new Date()
                                        )
                                )
                        ).cursor();


                if (eventCursor != null) {
                    while (eventCursor.hasNext()) {
                        Document documentToEmbed = new Document();
                        Document eventDocument = (Document) eventCursor.next();
                        Date event_time = eventDocument.getDate("event_time");

    //                    if (event_time.after(new Date())) {

                            documentToEmbed.append("event_id", eventDocument.getString("event_id"));
                            documentToEmbed.append("event_name", eventDocument.getString("event_name"));
                            documentToEmbed.append("event_time", event_time);

                            upcomingEvents.add(documentToEmbed);
    //                    }
                    }
                }
            }
        }
        return upcomingEvents;
    }


    public static Document extractCityDocumentToEmbedPerMember(String memberId, List<Document> members) {

        String cityName;
        if (members != null && !members.isEmpty()) {
            Document member = members.get(0);
            cityName = member.getString("city");
        } else {
            MongoCollection memberCollection = CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
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
    public static Document extractMemberDocument(String memberId, List<Document> members) {

        Document document;
        if (members != null && !members.isEmpty()) {
            document = new Document();
            Document member = members.get(0);
            document.append("member_id", member.getString("member_id"));
            document.append("member_name", member.getString("member_name"));
            document.append("member_status", member.getString("member_status"));
            CsvToMongoTransformer.assignIfFound(document,"hometown",member.getString("hometown"));
            document.append("link", member.getString("link"));
            CsvToMongoTransformer.assignIfFound(document,"bio",member.getString("bio"));

        } else {
            MongoCollection memberCollection = CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
            document = ((Document) memberCollection.find(Filters.eq("member_id", memberId))
                    .projection(new Document("member_id", 1).append("member_name", 1)
                            .append("member_status", 1).append("_id", 0).append("bio", 1).append("hometown", 1).append("link", 1))
                    .first());
        }
        return document;
    }

    public void createMemberCollection(boolean includeOnlyMembersWithModifiedId) throws Exception {

        List<String> memberIds = metaMembersIds!=null? metaMembersIds : retrieveIdsFromMetaMembers();
        boolean includeFilter = includeOnlyMembersWithModifiedId && !memberIds.isEmpty();

        MongoCollection oldMemberCollection = CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
        MongoCollection newMemberCollection = getNewUserCollection();
        Document finalDocument = new Document();

        try (MongoCursor<Document> mongoCursor = includeFilter ? oldMemberCollection.find(Filters.in("member_id", memberIds)).cursor() : oldMemberCollection.find().cursor()) {

            HashMap<String,Double> groupCount = extractGroupCountPerMember();
            HashMap<String,Double> eventCount = extractEventCountPerMember();

            List<Document> membersWithId = new ArrayList<>();
            Set<String> membersAlreadyEvaluated = new HashSet<>();
            String memberId;

            boolean neo4JIndexCreated=false;

            while (mongoCursor.hasNext()) {
                Document document =  mongoCursor.next();
                memberId = document.getString("member_id");
                Future[] futures = new Future[4];
    //            futures[0] = parallelExecutor.submit(MongoDbUserOperations::extractTopicPerMember,memberId);

                if (!membersAlreadyEvaluated.contains(memberId)) {

                    futures[0] = parallelExecutor.submit(MongoDbUserOperations::extractMemberDocument,memberId,membersWithId);

                    futures[2] = parallelExecutor.submit(MongoDbUserOperations::extractCityDocumentToEmbedPerMember,memberId,membersWithId);

                    finalDocument= (Document) futures[0].get();

                    finalDocument.append("city", futures[2].get());

                    Neo4JOperations.createMemberNode(finalDocument,null,true);

                    if (!neo4JIndexCreated){
                        Neo4JOperations.createNeo4JIndex("Member","member_id");
                        neo4JIndexCreated=true;
                    }
                    futures[1] = parallelExecutor.submit(e -> extractTopicPerMember(e),memberId);
                    finalDocument.append("topics", futures[1].get());
                    futures[3] = parallelExecutor.submit(e -> extractFutureEventsPerMember(e),memberId);

                    finalDocument.append("upcoming_events", futures[3].get());


//                    finalDocument = extractMemberDocument(memberId, membersWithId);
//                    finalDocument.append("topics", this.extractTopicPerMember(memberId));
//                    finalDocument.append("city", this.extractCityDocumentToEmbedPerMember(memberId, membersWithId));
                    finalDocument.append("event_count", eventCount.get(memberId));
//                    finalDocument.append("upcoming_events", this.extractFutureEventsPerMember(memberId));
                    finalDocument.append("group_count", groupCount.get(memberId));


                    Document finalDocument1 = finalDocument;
                    parallelExecutor.submit(() ->  newMemberCollection.insertOne(finalDocument1));

                    membersAlreadyEvaluated.add(memberId);
                }
            }
        }
    }

    public List<Document> getMembersWithMemberId(String member_id) {
        List<Document> documentList = new ArrayList<>();
        MongoCollection oldMemberCollection = CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
        try (MongoCursor mongoCursor = oldMemberCollection.find(Filters.eq("member_id", member_id)).cursor()) {
            while (mongoCursor.hasNext()) {
                Document document = (Document) mongoCursor.next();
                documentList.add(document);
            }
        }
        return documentList;
    }


    public static List retrieveIdsFromMetaMembers(){
        MongoCollection metaMembersCollection= CsvToMongoTransformer.csvDocuments.getCollection("meta-members.csv");
     metaMembersCollection.createIndex(new Document("member_id",1));
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
        MongoCollection metaMembersCollection= CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
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

    public static List updateIdsForMembers() {
        MongoCollection membersCollection = CsvToMongoTransformer.csvDocuments.getCollection("members.csv");
        MongoCollection memberTopicsCollection = CsvToMongoTransformer.csvDocuments.getCollection("members_topics.csv");
        membersCollection.createIndex(new Document("member_id", 1));
        memberTopicsCollection.createIndex(new Document("member_id", 1));
        List<String> IdsFromMetaMembersColl = retrieveIdsFromMetaMembers();
        int totalMetaMembers = IdsFromMetaMembersColl.size();
//       List<String> membersId = retrieveIdsFromMembers(-1);


        int membersChosen = 0;
        List<Document> aggregationList = Arrays.asList(
                new Document("$group",
                        new Document("_id", "$member_id")
                                .append("firstMember",
                                        new Document("$first", "$member_id"))),
                new Document("$limit", totalMetaMembers));


        try (MongoCursor<Document> mongoCursor = membersCollection.aggregate(aggregationList).cursor()) {

            Set<String> chosenMembers = new HashSet<>();
            chosenMembers.addAll(IdsFromMetaMembersColl);

            int value = -1;
            String chosenMember;
/*
            do {
                chosenRecord = destinationIds.get(random.nextInt(totalDesinationRecords));
            }
            while (chosenRecords.contains(chosenRecord));

            chosenRecords.add(chosenRecord);
 */
            while (membersChosen <= totalMetaMembers && mongoCursor.hasNext()) {
                String memberIdToUpdate;
                do {
                    memberIdToUpdate =  mongoCursor.next().getString("_id");
                } while (chosenMembers.contains(memberIdToUpdate) && mongoCursor.hasNext());

                chosenMembers.add(memberIdToUpdate);

                String finalMemberIdToUpdate = memberIdToUpdate;
                int finalMembersChosen = membersChosen;
                Thread thread1 = new Thread(() -> {
                    membersCollection.updateMany(
                            Filters.eq("member_id", finalMemberIdToUpdate),
                            new Document("$set",
                                    new Document("member_id", IdsFromMetaMembersColl.get(finalMembersChosen))
                            )
                    );
                });
                String finalMemberIdToUpdate1 = memberIdToUpdate;
                int finalMembersChosen1 = membersChosen;
                Thread thread2 = new Thread(() -> {
                    memberTopicsCollection.updateMany(Filters.eq("member_id", finalMemberIdToUpdate1), new Document("$set",
                            new Document("member_id", IdsFromMetaMembersColl.get(finalMembersChosen1))
                    ));
                });
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();

                membersChosen++;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        metaMembersIds = IdsFromMetaMembersColl;
        return IdsFromMetaMembersColl;
    }


//        public void allUserOperations () {
////        for ()
//        }


}
