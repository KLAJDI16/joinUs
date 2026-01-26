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
    String fields= "{'group_id':1,'group_name':1,'upcoming_events':1,'city.name':1','category.name':1,'member_count':1,'event_count':1}";
    String projection="{$project:"+fields+"}";

    Optional<Group> findByGroupId(String groupId);

    boolean existsByGroupId(String groupId);

    void deleteByGroupId(String groupId);

    @Query("{'organizer_members.member_id' : ?0 }")
    List<Group> findGroupsByOrganizerId(String id);




    @Aggregation(pipeline = {
            "{ $skip: ?0} ",
            " {$limit: ?1} "
    })
    List<GroupDTO> findAllByAggregation(int offset,int limit);

}
