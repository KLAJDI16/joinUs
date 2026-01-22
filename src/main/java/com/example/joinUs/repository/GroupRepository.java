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

}
