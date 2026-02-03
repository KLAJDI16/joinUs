package com.example.joinUs.repository;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.model.mongodb.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    String fields= "{'_id':1,'group_name':1,'upcoming_events':1,'city.name':1','category.name':1,'member_count':1,'event_count':1}";
    String projection="{$project:"+fields+"}";

    Optional<Group> findById(String id);

    Page<Group> findAll(Pageable pageable);

    boolean existsById(String id);

    void deleteById(String id);

    @Query("{'organizers.member_id' : ?0 }")
    List<Group> findGroupsByOrganizerId(String id);

    //{$or:[{group_id:"5817263"},{group_name:"San Francisco Startup Socials"}]}

    @Query("{$or:[{ '_id': ?0 },{ 'group_name': ?1 }]}")
    List<Group> findGroupByGroupIdOrGroupName(String groupId,String groupName);

    @Aggregation(pipeline = {
            "{ $skip: ?0} ",
            " {$limit: ?1} "
    })
    List<GroupDTO> findAllByAggregation(int offset,int limit);

//    //Count Events per City to see Which cities host the most events
//    @Aggregation(pipeline = {
//            "{ $group: { _id: '$creator_group.city.name', eventsCount: { $sum: 1 } } }",
//            "{ $sort: { eventsCount: -1 } }"
//    })
//    List<Document> countEventsByCity();
//
//    // aggr 4 1 Top Cities by Groups Created in the Past 10 Years
//    @Aggregation(pipeline = {
//            "{ $match: { created: { $gte: new Date('2016-02-01T00:00:00.000Z') } } }",
//            "{ $group: { _id: '$city.name', groupsCreated: { $sum: 1 } } }",
//            "{ $sort: { groupsCreated: -1 } }",
//            "{ $limit: 20 }"
//    })
//    List<Document> topCitiesByGroupsSince2016();
//
//    // Aggr 5 Organizer leaderboard (top organizers by number of groups)
//    @Aggregation(pipeline = {
//            "{ $unwind: '$organizer_members' }",
//            "{ $group: { _id: '$organizer_members.member_id', organizerName: { $first: '$organizer_members.member_name' }, groupsOrganized: { $sum: 1 } } }",
//            "{ $sort: { groupsOrganized: -1 } }",
//            "{ $limit: 20 }",
//            "{ $project: { memberId: '$_id', organizerName: 1, groupsOrganized: 1, _id: 0 } }"
//    })
//    List<Document> topOrganizersByGroups();

    //Aggr 6 Groups created per year (trend) showing Growth of groups over time.
//    @Aggregation(pipeline = {
//            "{ $match: { created: { $ne: null } } }",
//            "{ $addFields: { created_date: { $cond: [ { $eq: [ { $type: '$created' }, 'date' ] }, '$created', { $toDate: '$created' } ] } } }",
//            "{ $group: { _id: { $year: '$created_date' }, groupsCreated: { $sum: 1 } } }",
//            "{ $sort: { _id: 1 } }",
//            "{ $project: { _id: 0, year: '$_id', groupsCreated: 1 } }"
//    })
//    List<Document> groupsCreatedPerYear();




//
//
//
//
//
}
