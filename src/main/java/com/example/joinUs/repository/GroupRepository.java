package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    Group findByGroupId(String groupId);

}
