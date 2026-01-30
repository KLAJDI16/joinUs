package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

    @Query("{'group_id' : ?0 }")
    Event findEventById(String id);

    @Query("{'creator_group.id' : ?0 }")
    List<Event> findEventsByCreator_group(String id);
//Find event by Meetup event ID
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

//Count Events per Category to see Which categories are most common
@Aggregation(pipeline = {
        "{ $unwind: '$categories' }",
        "{ $group: { _id: '$categories.name', eventsCount: { $sum: 1 } } }",
        "{ $sort: { eventsCount: -1 } }"
})
List<Document> countEventsByCategory();

//How active users are in each city (average attendance)
@Aggregation(pipeline = {
        "{ $group: { _id: '$creator_group.city.name', avgAttendance: { $avg: '$member_count' } } }",
        "{ $sort: { avgAttendance: -1 } }"
})
List<Document> averageAttendanceByCity();

//How many events are created each month.
@Aggregation(pipeline = {
        "{ $match: { event_time: { $ne: null } } }",
        "{ $group: { _id: { year: { $year: '$event_time' }, month: { $month: '$event_time' } }, eventsCount: { $sum: 1 } } }",
        "{ $sort: { '_id.year': 1, '_id.month': 1 } }"
})
List<Document> countEventsPerMonth();


}

