package com.example.joinUs.controller;

import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.service.EventService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController()
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // TODO do we even need that function?
    @GetMapping("")
    public Page<EventDTO> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return eventService.getAllEvents(page, size);
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable String id) {
        return eventService.getEventById(id);
    }

    @PostMapping("")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventDTO> editEvent(@PathVariable String id, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(eventDTO, id));
    }

    @PatchMapping("/{id}/attend")
    public ResponseEntity attendEvent(@PathVariable String id) { // TODO return type
        return ResponseEntity.ok(eventService.attendEvent(id));
    }

    @PatchMapping("/{id}/revokeAttendance")
    public ResponseEntity revokeAttendance(@PathVariable String id) { // TODO return type
        return ResponseEntity.ok(eventService.revokeAttendance(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
    }

    @GetMapping("/search")
    public Page<EventSummaryDTO> searchEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minMembers,
            @RequestParam(required = false) Integer maxMembers,
            @Parameter(example = "2026-03-25T23:00:00.000")
            LocalDateTime fromDate,
            @Parameter(example = "2027-01-25T23:00:00.000")
            LocalDateTime toDate,
            @RequestParam(required = false) Integer maxFee,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "100") Integer pageSize
    ) {
        return eventService.search(
                name,
                city,
                category,
                minMembers,
                maxMembers,
                fromDate,
                toDate,
                maxFee,
                page,
                pageSize
        );
    }
}
