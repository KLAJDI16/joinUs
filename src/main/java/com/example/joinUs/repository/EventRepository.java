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
public interface EventRepository extends MongoRepository<Event, ObjectId> {

    String fields= "{'event_id':1,'event_name':1,'venue.city.name':1,'member_count':1,'creator_group':1,'event_time':1,'fee.amount':1}";

//    String eventProjection= "{ $project:"+fields+" }";


    Optional<Event> findByEventId(String eventId); //TODO check


    boolean existsByEventId(String eventId);

    void deleteByEventId(String eventId);




//    @Query(value = "{"+eventNameNotEmpty+"}",fields = fields)
//    Page<Event> findAll(Pageable pageable);

    @Query(value = "{'event_name' : { $regex: ?0, $options: 'i' } }",fields = fields)
    Page<EventDTO> searchByEventName(String name,Pageable pageable);

//    List<EventSummaryDTO> findByEventNameContainingIgnoreCase(String name);

    @Query(value = "{ 'venue.city.name' : { $regex: ?0, $options: 'i' } }",fields = fields)
    Page<EventDTO> findByCityName(String cityName,Pageable pageable);

    //{ 'id': 1, 'artists': 1, 'coverURL': 1, 'title': 1 }
    @Query(value = "{ 'categories.name' : { $regex: ?0, $options: 'i' } }",fields = fields)
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
