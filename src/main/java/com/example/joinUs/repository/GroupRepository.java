package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends MongoRepository<Group,String> {

    @Query("{'group_id' : ?0 }")
     Group findGroupById(String id);


    @Query("{'organizer_members.id' : ?0 }")
    List<Group> findGroupsByOrganizerId(String id);
//Find group by exact name
@Query("{ 'group_name': ?0 }")
List<Group> findByGroupName(String name);

//Find groups in a city
@Query("{ 'city.name': ?0 }")
List<Group> findByCityName(String cityName);

//Find groups by category name (embedded categories)
@Query("{ 'categories.name': ?0 }")
List<Group> findByCategoryName(String categoryName);

//Find “big groups” by member count
@Query("{ 'member_count': { $gte: ?0 } }")
List<Group> findGroupsWithMinMembers(Double minMembers);

//Find groups with high event count
@Query("{ 'event_count': { $gte: ?0 } }")
List<Group> findGroupsWithMinEvents(Double minEvents);

//Count Groups per City to see “Which cities have the most groups?”
@Aggregation(pipeline = {
        "{ $group: { _id: '$city.name', groupsCount: { $sum: 1 } } }",
        "{ $sort: { groupsCount: -1 } }",
        "{ $limit: 20 }"
})
List<Document> countGroupsByCity();

// Count Groups per Category
@Aggregation(pipeline = {
        "{ $unwind: '$categories' }",
        "{ $group: { _id: '$categories.name', groupsCount: { $sum: 1 } } }",
        "{ $sort: { groupsCount: -1 } }",
        "{ $limit: 20 }"
})
List<Document> countGroupsByCategory();

//Top Groups by Member Count to find largest communities
@Aggregation(pipeline = {
        "{ $sort: { member_count: -1 } }",
        "{ $limit: 20 }",
        "{ $project: { group_id: 1, group_name: 1, member_count: 1, city: '$city.name' } }"
})
List<Document> topGroupsByMembers();


}
