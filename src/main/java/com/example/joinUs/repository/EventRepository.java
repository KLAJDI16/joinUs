package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Event;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD

=======
import java.util.Date;
>>>>>>> 0d7ce7b88f65fe2189779230d548e44d0ecfbc07
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

<<<<<<< HEAD
    Event findByEventId(String eventId);

    @Query("{'creator_group.id' : ?0 }")
    List<Event> findEventsByCreatorGroup(String id);
=======
    @Query("{'group_id' : ?0 }")
    Event findEventById(String id);
//Find a
    @Query("{'creator_group.id' : ?0 }")
    List<Event> findEventsByCreator_group(String id);
//Find event by JoinUS event ID
@Query("{ 'event_id': ?0 }")
Event findByEventId(String eventId);

//Find all events created by a group
@Query("{ 'creator_group.group_id': ?0 }")
List<Event> findByCreatorGroupId(String groupId);

//Find events by status (e.g. upcoming, cancelled)
@Query("{ 'event_status': ?0 }")
List<Event> findByEventStatus(String status);

//Find upcoming events (sorted by date)
@Query(
        value = "{ 'event_time': { $gte: ?0 } }",
        sort = "{ 'event_time': 1 }"
)
List<Event> findUpcomingEvents(Date now);

//Find past events
@Query(
        value = "{ 'event_time': { $lt: ?0 } }",
        sort = "{ 'event_time': -1 }"
)
List<Event> findPastEvents(Date now);

//Find events by category name (knowing categories are embedded List<Category> categories;)
@Query("{ 'categories.name': ?0 }")
List<Event> findByCategoryName(String categoryName);

//Find events by city (via creator group)
@Query("{ 'creator_group.city.name': ?0 }")
List<Event> findByCity(String cityName);

//Find events with high attendance
@Query(
        value = "{ 'member_count': { $gte: ?0 } }",
        sort = "{ 'member_count': -1 }"
)
List<Event> findPopularEvents(Double minMembers);

//Find events in a date range
@Query(
        value = "{ 'event_time': { $gte: ?0, $lte: ?1 } }",
        sort = "{ 'event_time': 1 }"
)
List<Event> findEventsBetweenDates(Date start, Date end);

//Find recently updated events
@Query(
        value = "{ 'updated': { $ne: null } }",
        sort = "{ 'updated': -1 }"
)
List<Event> findRecentlyUpdatedEvents();



//Count Events per City to see Which cities host the most events
@Aggregation(pipeline = {
        "{ $group: { _id: '$creator_group.city.name', eventsCount: { $sum: 1 } } }",
        "{ $sort: { eventsCount: -1 } }"
})
List<Document> countEventsByCity();



//How many events are created each month.
@Aggregation(pipeline = {
        "{ $match: { event_time: { $ne: null } } }",
        "{ $group: { _id: { year: { $year: '$event_time' }, month: { $month: '$event_time' } }, eventsCount: { $sum: 1 } } }",
        "{ $sort: { '_id.year': 1, '_id.month': 1 } }"
})
List<Document> countEventsPerMonth();




//Count Events per Category to see Which categories are most common
    @Aggregation(pipeline = {
            "{ $unwind: '$categories' }",
            "{ $group: { _id: '$categories.name', eventsCount: { $sum: 1 } } }",
            "{ $sort: { eventsCount: -1 } }"
    })
    List<Document> countEventsByCategory();




//Trending Categories (Shows which categories are growing or declining.) See which category people attend most
    @Aggregation(pipeline = {
            "{ $match: { event_time: { $gte: ?1 } } }",
            "{ $unwind: '$categories' }",
            "{ $group: { " +
                    "_id: '$categories.name', " +
                    "lastWeek: { $sum: { $cond: [ { $gte: ['$event_time', ?0] }, { $ifNull: ['$member_count', 0] }, 0 ] } }, " +
                    "previousWeek: { $sum: { $cond: [ { $and: [ { $gte: ['$event_time', ?1] }, { $lt: ['$event_time', ?0] } ] }, { $ifNull: ['$member_count', 0] }, 0 ] } } " +
                    "} }",
            "{ $addFields: { delta: { $subtract: ['$lastWeek', '$previousWeek'] } } }",
            "{ $sort: { delta: -1 } }"
    })
    List<Document> trendingCategoriesWeekly(Date lastWeekStart, Date prevWeekStart);
    })



//Popularity of Paid vs Free Events showing Do people attend paid or free events more?
@Aggregation(pipeline = {
        "{ $addFields: { " +
                "eventType: { $cond: [ " +
                "{ $or: [ { $eq: ['$fee.amount', 0] }, { $eq: ['$fee', null] }, { $eq: ['$fee.amount', null] } ] }, " +
                "'FREE', 'PAID' ] }, " +
                "attendance: { $ifNull: ['$member_count', 0] } " +
                "} }",

        "{ $group: { _id: '$eventType', eventsCount: { $sum: 1 }, totalAttendance: { $sum: '$attendance' }, avgAttendance: { $avg: '$attendance' } } }",

        "{ $project: { type: '$_id', eventsCount: 1, totalAttendance: 1, avgAttendance: 1, _id: 0 } }",
        "{ $sort: { totalAttendance: -1 } }"
})
List<Document> paidVsFreePopularity();

}

