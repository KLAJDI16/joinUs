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

}
