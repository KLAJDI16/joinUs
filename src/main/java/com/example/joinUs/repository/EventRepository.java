package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Event;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

    Event findByEventId(String eventId);

}
