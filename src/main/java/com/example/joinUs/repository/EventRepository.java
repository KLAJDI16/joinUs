package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Event;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

    Event findByEventId(String eventId);

    @Query("{'creator_group.id' : ?0 }")
    List<Event> findEventsByCreatorGroup(String id);

}
