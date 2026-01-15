package com.example.joinUs.controller;


import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.dto.EventNeo4jDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/fromMongo")
    public List<EventDTO> getAllEvents(){
        return eventService.getAllEvents();
    }




    @GetMapping("/fromGraph")
    public List<EventNeo4jDTO> getAllUsersFromGraph() {
        return eventService.getAllEventsFromGraph();
    }


}
