package com.example.joinUs.service;

import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.dto.EventNeo4jDTO;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.repository.EventRepository;
import com.example.joinUs.repository.Event_Neo4J_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Event_Neo4J_Repo eventNeo4JRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<EventDTO> getAllEvents(){
        return eventRepository.findAll().stream()
                .map(Event::toDTO)
                .toList();
    }

    public List<EventNeo4jDTO> getAllEventsFromGraph(){
        return eventNeo4JRepo.findAll().stream()
                .map(e -> e.toDTO())
                .toList();
    }
}
