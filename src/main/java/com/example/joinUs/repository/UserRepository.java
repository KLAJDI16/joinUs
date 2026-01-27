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
    @Query("{ 'member_id': ?0 }")
    Optional<User> findMemberById(String member_id);

    // Find by exact member name
    @Query("{ 'member_name': ?0 }")
    List<User> findMemberByName(String member_name);

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

    // Count users in a specific city
    @Query(value = "{ 'city.city_name': ?0 }", count = true)
    long countByCity(String cityName);

    // Check existence by member_id
//    boolean existsByMember_id(String member_id);

}
