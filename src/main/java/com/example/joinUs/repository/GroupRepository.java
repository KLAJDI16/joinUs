package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    Optional<Group> findByGroupId(String groupId);

    boolean existsByGroupId(String groupId);

    void deleteByGroupId(String groupId);

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



    // Count Groups per Category
    @Aggregation(pipeline = {
            "{ $unwind: '$categories' }",
            "{ $group: { _id: '$categories.name', groupsCount: { $sum: 1 } } }",
            "{ $sort: { groupsCount: -1 } }",
            "{ $limit: 20 }"
    })
    List<Document> countGroupsByCategory();



//Count Groups per City to see “Which cities have the most groups?”
@Aggregation(pipeline = {
        "{ $group: { _id: '$city.name', groupsCount: { $sum: 1 } } }",
        "{ $sort: { groupsCount: -1 } }",
        "{ $limit: 20 }"
})
List<Document> countGroupsByCity();


    //Top Cities by Groups Created in the Past 10 Years
    @Aggregation(pipeline = {
            "{ $match: { created: { $gte: { $dateSubtract: { startDate: '$$NOW', unit: 'year', amount: 10 } } } } }",
            "{ $group: { _id: '$city.name', groupsCreated: { $sum: 1 } } }",
            "{ $sort: { groupsCreated: -1 } }",
            "{ $limit: 20 }",
            "{ $project: { city: '$_id', groupsCreated: 1, _id: 0 } }"
    })
    List<Document> topCitiesByGroupsLast10Years();



    //Top cities by upcoming events count (overall activity)
    @Aggregation(pipeline = {
            "{ $match: { event_time: { $gte: ?0 } } }",
            "{ $group: { _id: '$creator_group.city.name', totalUpcomingEvents: { $sum: 1 } } }",
            "{ $sort: { totalUpcomingEvents: -1 } }",
            "{ $limit: 20 }",
            "{ $project: { city: '$_id', totalUpcomingEvents: 1, _id: 0 } }"
    })
    List<Document> topCitiesByUpcomingEvents(Date now);




//Top Groups by Member Count to find largest communities
@Aggregation(pipeline = {
        "{ $sort: { member_count: -1 } }",
        "{ $limit: 20 }",
        "{ $project: { group_id: 1, group_name: 1, member_count: 1, city: '$city.name' } }"
})
List<Document> topGroupsByMembers();



//Groups created per year (trend) showing Growth of groups over time.
@Aggregation(pipeline = {
        "{ $match: { created: { $ne: null } } }",
        "{ $group: { _id: { $year: '$created' }, groupsCreated: { $sum: 1 } } }",
        "{ $sort: { _id: 1 } }",
        "{ $project: { year: '$_id', groupsCreated: 1, _id: 0 } }"
})
List<Document> groupsCreatedPerYear();



//Organizer leaderboard (top organizers by number of groups)
@Aggregation(pipeline = {
        "{ $unwind: '$organizer_members' }",
        "{ $group: { _id: '$organizer_members.member_id', organizerName: { $first: '$organizer_members.member_name' }, groupsOrganized: { $sum: 1 } } }",
        "{ $sort: { groupsOrganized: -1 } }",
        "{ $limit: 20 }",
        "{ $project: { memberId: '$_id', organizerName: 1, groupsOrganized: 1, _id: 0 } }"
})
List<Document> topOrganizersByGroups();



}
