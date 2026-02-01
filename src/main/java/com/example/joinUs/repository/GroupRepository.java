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

}
