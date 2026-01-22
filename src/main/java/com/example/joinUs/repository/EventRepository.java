package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

    @Query("{'group_id' : ?0 }")
    Event findEventById(String id);

    @Query("{'creator_group.id' : ?0 }")
    List<Event> findEventsByCreator_group(String id);

}
