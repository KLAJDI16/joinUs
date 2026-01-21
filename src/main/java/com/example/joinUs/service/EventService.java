package com.example.joinUs.service;

import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.mapping.EventMapper;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.repository.EventRepository;
import com.example.joinUs.repository.Event_Neo4J_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private Event_Neo4J_Repo eventNeo4JRepo; // TODO

    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.toDTOs(events);
    }

    public EventDTO getEventById(String id) {
        Event event = eventRepository.findByEventId(id);
        return eventMapper.toDTO(event);
    }

}
