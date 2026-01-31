package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    // Find by exact member id
    @Query("{ 'member_id': ?0 }")
    Optional<User> findByMember_id(String member_id);

    // Find by exact member name
    @Query("{ 'member_name': ?0 }")
    Optional<List<User>> findByMember_name(String member_name);

    // Find by member_id (you could also just use findById inherited from MongoRepository)
//    Optional<User> findByMember_id(String member_id);

    // Find all users in a specific city
    @Query("{ 'city.city_name': ?0 }")
    List<User> findByCity(String cityName);

    // Find all users who are admins
    @Query("{ 'isAdmin': true }")
    List<User> findAdmins();

    // Find users who have organized a specific group
    @Query("{ 'created_groups': ?0 }")
    List<User> findByCreated_groupsContaining(String groupId);

    // Find users by topic key
    @Query("{ 'topics.topic_key': ?0 }")
    List<User> findByTopic(String topicKey);

    // Update operations via repository usually use save(entity)
    // Example usage:
    // 1. Retrieve user: Optional<User> u = userRepository.findById(id);
    // 2. Modify fields on User object
    // 3. Save: userRepository.save(user);

    // Delete by member_id
//    void deleteByMember_id(String member_id);

    // Optional custom query: find by status and sort by name
    @Query(value = "{ 'member_status': ?0 }", sort = "{ 'member_name': 1 }")
    List<User> findByStatusSortedByName(String status);

    // Count users in a specific city (countByCity("Paris") “How many users are in this one city?)
    // That’s filtering + counting
    //    //but aggregate $group  shows “How many users per city?”
    @Query(value = "{ 'city.city_name': ?0 }", count = true)
    long countByCity(String cityName);

    // Check existence by member_id
//  boolean existsByMember_id(String member_id);




    //Cities with the largest user base.
    @Aggregation(pipeline = {
            "{ $group: { _id: '$city.name', usersCount: { $sum: 1 } } }",
            "{ $sort: { usersCount: -1 } }",
            "{ $limit: 20 }"
    })
    List<Document> countUsersByCity();


    //Distribution of User Status (active / inactive)
    @Aggregation(pipeline = {
            "{ $group: { _id: '$member_status', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }"
    })
    List<Document> countUsersByStatus();



    //Users With Upcoming Events (high intent users)
    @Aggregation(pipeline = {
            "{ $project: { member_id: 1, member_name: 1, upcomingCount: { $size: { $ifNull: ['$upcoming_events', []] } } } }",
            "{ $match: { upcomingCount: { $gt: 0 } } }",
            "{ $sort: { upcomingCount: -1 } }",
            "{ $limit: 20 }"
    })
    List<Document> usersWithUpcomingEvents();


    //Users by “activity score (Simple scoring using:
    //event_count + group_count)
    @Aggregation(pipeline = {
            "{ $project: { member_id: 1, member_name: 1, " +
                    "activityScore: { $add: [ { $ifNull: ['$event_count', 0] }, { $ifNull: ['$group_count', 0] } ] } } }",
            "{ $sort: { activityScore: -1 } }",
            "{ $limit: 20 }"
    })
    List<Document> topUsersByActivityScore();


    //Users who have many topics → broad interests.
    @Aggregation(pipeline = {
            "{ $project: { member_id: 1, member_name: 1, topicCount: { $size: { $ifNull: ['$topics', []] } } } }",
            "{ $sort: { topicCount: -1 } }",
            "{ $limit: 20 }"
    })
    List<Document> findUsersWithMostTopics();


    //Topic diversity (how many unique topics exist among users)
    @Aggregation(pipeline = {
            "{ $unwind: '$topics' }",
            "{ $group: { _id: '$topics.topic_id' } }",
            "{ $count: 'uniqueTopicsCount' }"
    })
    List<Document> countUniqueTopicsInUsers();


    //Topic Popularity Among Users
    @Aggregation(pipeline = {
            "{ $unwind: '$topics' }",
            "{ $group: { _id: '$topics.topic_name', usersCount: { $sum: 1 } } }",
            "{ $sort: { usersCount: -1 } }",
            "{ $limit: 30 }"
    })
    List<Document> mostPopularUserTopics();



    //Topic popularity per city (top topics inside each city) showing shows for each city, which topics appear most.
    @Aggregation(pipeline = {
            "{ $unwind: '$topics' }",
            "{ $group: { _id: { city: '$city.name', topic: '$topics.topic_name' }, usersCount: { $sum: 1 } } }",
            "{ $sort: { usersCount: -1 } }",
            "{ $group: { _id: '$_id.city', topTopics: { $push: { topic: '$_id.topic', usersCount: '$usersCount' } } } }",
            "{ $project: { city: '$_id', topTopics: { $slice: ['$topTopics', 10] }, _id: 0 } }",
            "{ $limit: 20 }"
    })
    List<Document> topTopicsPerCity();




    //Top Organizers (users who created most groups)
    @Aggregation(pipeline = {
            "{ $project: { member_id: 1, member_name: 1, groupsCreated: { $size: { $ifNull: ['$groups_organizer', []] } } } }",
            "{ $sort: { groupsCreated: -1 } }",
            "{ $limit: 20 }"
    })
    List<Document> topGroupOrganizers();


    //Average activity by city (avg event_count + group_count)
    @Aggregation(pipeline = {
            "{ $project: { city: '$city.name', activity: { $add: [ { $ifNull: ['$event_count', 0] }, { $ifNull: ['$group_count', 0] } ] } } }",
            "{ $group: { _id: '$city', avgActivity: { $avg: '$activity' }, users: { $sum: 1 } } }",
            "{ $sort: { avgActivity: -1 } }",
            "{ $limit: 20 }",
            "{ $project: { city: '$_id', avgActivity: 1, users: 1, _id: 0 } }"
    })
    List<Document> avgActivityByCity();



//Most Active Cities Shows cities where users are most active, not just numerous.

    @Aggregation(pipeline = {
            // Count registered members per city
            "{ $group: { _id: '$city.name', registeredMembers: { $sum: 1 } } }",

            // Join with events collection to compute attendance per city
            "{ $lookup: { " +
                    "from: 'events', " +
                    "let: { cityName: '$_id' }, " +
                    "pipeline: [ " +
                    "{ $match: { $expr: { $eq: ['$creator_group.city.name', '$$cityName'] } } }, " +
                    "{ $group: { _id: null, totalAttendance: { $sum: { $ifNull: ['$member_count', 0] } } } } " +
                    "], " +
                    "as: 'eventStats' } }",

            "{ $unwind: { path: '$eventStats', preserveNullAndEmptyArrays: true } }",

            // Compute activity ratio
            "{ $addFields: { " +
                    "totalAttendance: { $ifNull: ['$eventStats.totalAttendance', 0] }, " +
                    "attendancePerMember: { $cond: [ { $gt: ['$registeredMembers', 0] }, { $divide: ['$eventStats.totalAttendance', '$registeredMembers'] }, 0 ] } " +
                    "} }",

            "{ $project: { city: '$_id', registeredMembers: 1, totalAttendance: 1, attendancePerMember: 1, _id: 0 } }",
            "{ $sort: { attendancePerMember: -1 } }"
    })
    List<Document> mostActiveCities();


}