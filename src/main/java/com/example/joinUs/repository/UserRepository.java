package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    // Find by exact member name
    @Query("{ '_id': ?0 }")
    List<User> findMemberById(String member_id);

    // Find by exact member name
    @Query("{ 'member_name': ?0 }")
    List<User> findMemberByName(String member_name);
    // Find by member_id (you could also just use findById inherited from MongoRepository)
//    Optional<User> findByMember_id(String member_id);

    // Find all users in a specific city
    @Query("{ 'city.name': ?0 }")
    List<User> findByCity(String cityName);

    // Find all users who are admins
    @Query("{ 'isAdmin': true }")
    List<User> findAdmins();

    // Find users who have organized a specific group
    @Query("{ 'created_groups': ?0 }")
    List<User> findByCreated_groupsContaining(String groupId);

    // Find users by topic key
    @Query("{ 'topics.topic_name': ?0 }")
    List<User> findByTopicName(String topic_name);

    // Update operations via repository usually use save(entity)
    // Example usage:
    // 1. Retrieve user: Optional<User> u = userRepository.findById(id);
    // 2. Modify fields on User object
    // 3. Save: userRepository.save(user);

    // Delete by member_id
//    void deleteByMember_id(String member_id);

    // Count users in a specific city
    @Query(value = "{ 'city.name': ?0 }", count = true)
    long countByCity(String cityName);

//    //Aggr 1 Cities with the largest user base.
//    @Aggregation(pipeline = {
//            "{ $group: { _id: '$city.name', usersCount: { $sum: 1 } } }",
//            "{ $sort: { usersCount: -1 } }",
//            "{ $limit: 20 }"
//    })
//    List<Document> countUsersByCity();
//
//    // Aggr 2 Users With Upcoming Events (high intent users)
//    @Aggregation(pipeline = {
//            "{ $project: { member_id: 1, member_name: 1, upcomingCount: { $size: { $ifNull: ['$upcoming_events', []] } } } }",
//            "{ $match: { upcomingCount: { $gt: 0 } } }",
//            "{ $sort: { upcomingCount: -1 } }",
//            "{ $limit: 20 }"
//    })
//    List<Document> usersWithUpcomingEvents();
//
//    //Aggr 3 Average activity by city (avg event_count + group_count)
//    @Aggregation(pipeline = {
//            "{ $project: { city: '$city.name', activity: { $add: [ { $ifNull: ['$event_count', 0] }, { $ifNull: ['$group_count', 0] } ] } } }",
//            "{ $group: { _id: '$city', avgActivity: { $avg: '$activity' }, users: { $sum: 1 } } }",
//            "{ $sort: { avgActivity: -1 } }",
//            "{ $limit: 20 }",
//            "{ $project: { city: '$_id', avgActivity: 1, users: 1, _id: 0 } }"
//    })
//    List<Document> avgActivityByCity();

}
