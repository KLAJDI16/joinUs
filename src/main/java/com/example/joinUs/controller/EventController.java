package com.example.joinUs.controller;

import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.service.EventService;
import com.example.joinUs.service.UserService;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<EventDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable String id) {
        return eventService.getEventById(id);
    }

    @PostMapping("")
    public JsonObject createEvent() { // TODO
        JsonObject jsonObject = new JsonObject("{\"result\":\"Successfully hit POST /events \"}");
        return jsonObject;
    }

    @PutMapping("/{id}")
    public ResponseEntity editEvent(@PathVariable String id) { // TODO
        try {
            if (userService.userHasPermissionToEditEvent(id) != null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(userService.userHasPermissionToEditEvent(id));
            } else {
                return ResponseEntity.ok().body( new JsonObject("{\"result\":\"Successfully hit POST /events \"}"));
            }

        } catch (ApplicationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
