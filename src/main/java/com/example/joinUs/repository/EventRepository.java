package com.example.joinUs.repository;

import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.model.mongodb.Event;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {

    String eventNameNotEmpty = " 'event_name':{$ne:\"\"} ";
    // TODO why this?
    String fields = "{'event_id':1,'event_name':1,'venue.city.name':1,'member_count':1,'creator_group':1,'event_time':1,'fee.amount':1}";

    Optional<Event> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    void deleteByEventId(String eventId);

    // TODO this should return Event, not EventDTO
    @Query(value = "{" + eventNameNotEmpty + "}", fields = fields)
    Page<EventDTO> findAllEvents(Pageable pageable);

    @Query(value = "{'creator_group.id' : ?0 }", fields = fields)
    List<Event> findEventsByCreatorGroup(String id);

}
