package com.example.joinUs.repository;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.model.mongodb.Event;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    String fields= "{'_id':1,'event_name':1,'venue.city.name':1,'member_count':1,'creator_group':1,'event_time':1,'fee.amount':1}";

//    String eventProjection= "{ $project:"+fields+" }";


    Optional<Event> findById(String id); //TODO check


    boolean existsById(String id);

    void deleteById(String id);




    @Query(value = "{_id:{$ne:null}}",fields = fields)
    Page<EventDTO> findAllEvents(Pageable pageable);

    @Query(value = "{ _id: ?0 }",fields = fields)
    EventDTO findEventWithId(String id);

//    @Query(value = "{_id:{$ne:null}}",fields = fields,count = true)
//    Page<EventDTO> findAll(Pageable pageable);

    @Query(value = "{'event_name' : { $regex: ?0, $options: 'i' } }",fields = fields)
    Page<EventDTO> searchByEventName(String name,Pageable pageable);

//    List<EventSummaryDTO> findByEventNameContainingIgnoreCase(String name);

    @Query(value = "{ 'venue.city.name' : { $regex: ?0, $options: 'i' } }",fields = fields)
    Page<EventDTO> findByCityName(String cityName,Pageable pageable);

    //{ 'id': 1, 'artists': 1, 'coverURL': 1, 'title': 1 }
    @Query(value = "{ 'category.name' : { $regex: ?0, $options: 'i' } }",fields = fields)
    Page<EventDTO> findByCategoryName(String categoryName,Pageable pageable);

//    @Query("""
//        {
//          'venue.city.name' : ?0,
//          'event_name' : { $regex: ?1, $options: 'i' }
//        }
//    """)
//    List<Event> findByCityAndName(String city, String name);

    @Query(
            value = "{ 'member_count' : { $gte: ?0, $lte: ?1 } }",
            fields = fields,
            sort =   "{ 'member_count' : -1 }"
    )
    Page<EventDTO> findByMemberCountBetween(int minMemberCount, int maxMemberCount, Pageable pageable);

    @Query(
            value = "{ 'member_count' : { $gte: ?0 } }",
            fields = fields,
            sort =   "{ 'member_count' : -1 }"
    )
    Page<EventDTO> findByMemberCountGreaterThan(int minMemberCount,  Pageable pageable);

    @Query(
            value = "{ 'member_count' : { $lte: ?0 } }",
            fields = fields,
            sort =   "{ 'member_count' : -1 }"
    )
    Page<EventDTO> findByMemberCountLessThan(int maxMemberCount,  Pageable pageable);

    @Query(
            value = "{ 'event_time' : { $gte: ?0, $lte: ?1 } }",
            fields = fields,
            sort = "{ 'event_time' : 1 }"

    )
    Page<EventDTO> findByEventTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query(
            value = "{ 'event_time' : { $gte: ?0} }",
            fields = fields,
            sort = "{ 'event_time' : 1 }"
    )
    Page<EventDTO> findByEventTimeAfter(LocalDateTime from,Pageable pageable);

    @Query(
            value = "{ 'event_time' : { $lte: ?0} }",
            fields = fields,
            sort = "{ 'event_time' : 1 }"
    )
    Page<EventDTO> findByEventTimeBefore(LocalDateTime to,Pageable pageable);

    @Query(
            value = "{ 'fee.amount' : {$lte:?0} }",
            fields = fields,
            sort = "{ 'fee.amount' : 1 }"
    )
    Page<EventDTO> findByEventFeeIsLessOrEqual(int max,Pageable pageable);

    @Query(value = "{'creator_group.id' : ?0 }",fields = fields)
    List<Event> findEventsByCreatorGroup(String id);



//    List<EventSummaryDTO> findByEventNameContainingIgnoreCase(String name);




    // USING THE AGGREGATION APPROACH
//
//    @Aggregation( pipeline = {
//            "{$match:{'event_name' : { $regex: ?0, $options: 'i' } }}",
//            "{$skip: ?1}",
//            "{$limit: ?2}",
//            eventProjection
//    }
//    )
//    List<EventDTO> searchByEventNameAggregation(String name,int offset,int limit);
//
//    @Aggregation(pipeline = {
//            "{$match { 'venue.city.name' : ?0 } }",
//            "{$skip: ?1}",
//            "{$limit: ?2}",
//            eventProjection
//    }
//    )
//    List<EventDTO> findByCityNameAggregation(String cityName,int offset,int limit);
//
//
//    @Aggregation(pipeline = {
//            "{$match : { 'categories.name' : { $regex: ?0, $options: 'i' } } }",
//            "{$skip: ?1}",
//            "{$limit: ?2}",
//            eventProjection
//    })
//    List<EventDTO> findByCategoryNameAggregation(String categoryName,int offset,int limit);
//
////    @Query("""
////        {
////          'venue.city.name' : ?0,
////          'event_name' : { $regex: ?1, $options: 'i' }
////        }
////    """)
////    List<Event> findByCityAndName(String city, String name);
//
//   //Aggr 7 Count Events per City to see Which cities host the most events
//@Aggregation(pipeline = {
//        "{ $match: { 'venue.city.name': { $nin: [null, ''] } } }",
//        "{ $group: { _id: '$venue.city.name', eventsCount: { $sum: 1 } } }",
//        "{ $sort: { eventsCount: -1 } }",
//        "{ $project: { _id: 0, city: '$_id', eventsCount: 1 } }"
//})
//List<Document> countEventsByVenueCity();

//Aggr 8 Count Events per Category to see Which categories are most common
//@Aggregation(pipeline = {
//        "{ $unwind: { path: '$category', preserveNullAndEmptyArrays: false } }",
//        "{ $match: { 'category.name': { $nin: [null, ''] } } }",
//        "{ $group: { _id: '$category.name', eventsCount: { $sum: 1 } } }",
//        "{ $sort: { eventsCount: -1 } }",
//        "{ $project: { _id: 0, category: '$_id', eventsCount: 1 } }"
//})
//List<Document> countEventsByCategory();

// Aggr 9  Events per month(platform activity over time)
//@Aggregation(pipeline = {
//        "{ $match: { event_time: { $ne: null } } }",
//        "{ $group: { _id: { year: { $year: '$event_time' }, month: { $month: '$event_time' } }, eventsCount: { $sum: 1 } } }",
//        "{ $sort: { '_id.year': 1, '_id.month': 1 } }",
//        "{ $addFields: { year: '$_id.year', month: '$_id.month' } }",
//        "{ $project: { _id: 0, year: 1, month: 1, eventsCount: 1 } }"
//})
//List<Document> countEventsPerMonth();




//    @Aggregation(pipeline = {
//            "{$match :{ 'member_count' : { $gte: ?0, $lte: ?1 } } }",
//            "{$sort:{ 'member_count':-1 } }",
//            "{$skip: ?2}",
//            "{$limit: ?3}",
//            eventProjection
//    }
//    )
//    List<EventDTO> findByMemberCountBetweenAggregation(int minMemberCount, int maxMemberCount, int offset,int limit);
//
//    @Aggregation(pipeline = {
//            "{$match:  { 'event_time' : { $gte: ?0, $lte: ?1 } } }",
//            "{$sort:{ 'event_time' : 1 }}",
//            "{$skip: ?2}",
//            "{$limit: ?3}",
//            eventProjection}
//    )
//    List<EventDTO> findByEventTimeBetweenAggregation(Date from, Date to, int offset,int limit);
//
//    @Aggregation(pipeline = {
//            "{$match: { 'fee.amount' : {$lte:?0} } }",
//            "{$sort: { 'fee.amount' : 1 } }",
//            "{$skip: ?1}",
//            "{$limit: ?2}",
//            eventProjection
//    }
//    )
//    List<EventDTO> findByEventFeeIsLessOrEqualAggregation(int max, int offset,int limit);
//
//
//
//    @Aggregation(pipeline = {
//            "{$match: { 'member_count' : { $gte: ?0, $lte: ?1 } }}",
//            "{$sort:{'member_count':-1}}",
//            "{$skip: ?2}",
//            "{$limit: ?3}",
//            eventProjection
//    })
//    List<EventDTO> findSecondWayByMemberCount(int minMemberCount,int maxMemberCount,int offset,int limit);

}
