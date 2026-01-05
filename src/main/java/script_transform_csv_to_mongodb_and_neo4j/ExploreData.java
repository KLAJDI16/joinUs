package script_transform_csv_to_mongodb_and_neo4j;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExploreData {
    public static List<String> colors= List.of(
            "\u001B[30m", // BLACK
            "\u001B[31m", // RED
            "\u001B[32m", // GREEN
            "\u001B[33m", // YELLOW
            "\u001B[34m", // BLUE
            "\u001B[35m", // MAGENTA
            "\u001B[36m", // CYAN
            "\u001B[37m", // WHITE
            "\u001B[90m", // BRIGHT BLACK (GRAY)
            "\u001B[91m", // BRIGHT RED
            "\u001B[92m", // BRIGHT GREEN
            "\u001B[94m"  // BRIGHT BLUE
    );

    public static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    public static final String firstDatasetFolder = "C:\\Users\\Public\\JoinUs_Dataset\\Meetup\\";//LOCAL PATH OF THE DATASET FOUND BY MOIN
    public static final String secondDatasetFolder = "C:\\Users\\Public\\JoinUs_Dataset\\dataset2_joinUs\\";//LOCAL PATH OF THE DATASET FOUND BY FLORIAN
    public static final MongoDatabase firstDatasetDB = client.getDatabase("csvDocuments");
    public static final MongoDatabase secondDatasetDB = client.getDatabase("secondDatasetDB");

    public static void analyzeEvents(){
        int count=0;
        Document eventsDocument;
        Document meta_eventsDocument;
        MongoCursor eventsCursor=getAndResetCursor("events.csv");

        while (eventsCursor.hasNext()){
            eventsDocument = (Document) eventsCursor.next();
            MongoCursor meta_eventsCursor=getAndResetCursor("rsvps.csv");
            while (meta_eventsCursor.hasNext()){
                meta_eventsDocument = (Document) meta_eventsCursor.next();
                if (meta_eventsDocument.get("event_id",String.class)
                        .equalsIgnoreCase(eventsDocument.get("event_id",String.class))){
                    System.out.println("ELEMENT WITH id : "+meta_eventsDocument.get("event_id",String.class));
                    count++;
                }
            }
        }
        System.out.println("Total elements in common : "+count);
    }
    public static void analyzeCollections(String firstCollection,String secondCollection,String firstKey,String secondKey){
//     new Thread(() -> {
     String printColor=colors.get((int) Math.floor(Math.random()*10));
//         synchronized (ExploreData.class) {
             System.out.println("\n " + printColor + " ANALYZING " + firstCollection + " BY COMPARING " + secondKey + " \n"+"\u001B[37m");
//         }
         int count=0;
         Document eventsDocument;
         Document meta_eventsDocument;
         MongoCursor eventsCursor=getAndResetCursor(firstCollection);

         while (eventsCursor.hasNext()){
             eventsDocument = (Document) eventsCursor.next();
             MongoCursor meta_eventsCursor=getAndResetCursor(secondCollection);
             while (meta_eventsCursor.hasNext()){
                 meta_eventsDocument = (Document) meta_eventsCursor.next();
                 String firstString =meta_eventsDocument.get(firstKey,String.class);
                 String secondString = eventsDocument.get(secondKey,String.class);
                 if (firstString!=null && secondString!=null )
                 {
                     if (firstString.equalsIgnoreCase(secondString))   count++;
//                    System.out.println("ELEMENT WITH id : "+meta_eventsDocument.get(firstKey,String.class));

                 }
             }
         }
//         synchronized (ExploreData.class) {
             System.out.println("Total elements in common for " + firstCollection + " : " + count);
//         }
    }
//    ).start();}


    public static MongoCursor getAndResetCursor(String collectionName){

        if (collectionName.equalsIgnoreCase("events.csv")){
            MongoCollection events = firstDatasetDB.getCollection("events.csv");
            return events.find().projection(new Document("event_id",true).append("event_name",true).append("group_id",true).append("_id",false)
            ).cursor();
        }
       else if (collectionName.equalsIgnoreCase("meta-events.csv")){
            MongoCollection meta_events = firstDatasetDB.getCollection("meta-events.csv");
            return  meta_events.find().projection(new Document("event_id",true).append("_id",false).append("name",true).append("group_id",true)
            ).cursor();
        }
        else if (collectionName.equalsIgnoreCase("rsvps.csv")){
            MongoCollection rsvps_events = firstDatasetDB.getCollection("rsvps.csv");
            return  rsvps_events.find().projection(new Document("event_id",true).append("_id",false).append("group_id",true)
            ).cursor();
        }
        else {
            MongoCollection collection = firstDatasetDB.getCollection(collectionName);
            return collection.find().cursor();
        }
    }
}
