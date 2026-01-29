package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.*;
import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.mapping.*;
import com.example.joinUs.mapping.embedded.EventEmbeddedMapper;
import com.example.joinUs.mapping.summary.EventSummaryMapper;
import com.example.joinUs.model.mongodb.City;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import static com.example.joinUs.Utils.isNullOrEmpty;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.http.HttpStatus.*;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventEmbeddedMapper eventEmbeddedMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private VenueMapper venueMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EventSummaryMapper eventSummaryMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    //    @Autowired
    //    private Neo4jClient neo4jClient; // TODO

    @Autowired
    private Event_Neo4J_Repo eventNeo4JRepo; // TODO

    public Page<EventDTO> getAllEvents(int page, int size) {
        return eventRepository.findAllEvents(PageRequest.of(page, size + 1)); // TODO why +1 here?

    }

    public EventDTO getEventById(String id) {
        Event event = getEventOrThrow(id);
        return eventMapper.toDTO(event);
    }

    private Event getEventOrThrow(String id) {
        return eventRepository.findByEventId(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Event not found: " + id));
    }

    public UserDTO attendEvent(String id) { // TODO why does this method return a UserDTO?
        Event event = getEventOrThrow(id);
        try {
            User user = Utils.getUserFromContext(); // TODO use injection instead of static method

            List<Event> userUpcomingEvents = user.getUpcomingEvents();
            userUpcomingEvents.add(eventEmbeddedMapper.toEntity(eventEmbeddedMapper.toDTO(event)));
            user.setUpcomingEvents(userUpcomingEvents);
            user.setEventCount(user.getEventCount() + 1);
            event.setMemberCount(event.getMemberCount() + 1); // TODO what about consistency during synchronous writes?
            eventRepository.save(event);
            userRepository.save(user);

            //TODO complete the part for the Neo4J too
            return userMapper.toDTO(user);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public UserDTO revokeAttendance(String id) { // TODO why does this method return a UserDTO?
        Event event = getEventOrThrow(id);
        try {
            User user = Utils.getUserFromContext();
            user.removeUpcomingEvent(id);
            user.setEventCount(user.getEventCount() - 1);
            event.setMemberCount(event.getMemberCount() - 1);
            eventRepository.save(event);
            userRepository.save(user);
            //TODO complete the part for the Neo4J too
            return userMapper.toDTO(user);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public EventDTO createEvent(EventDTO eventDTO) {

        EventDTO newEvent = createEventDTO(eventDTO);
        try {
            Event entity = eventMapper.toEntity(newEvent);
            Event saved = eventRepository.save(entity);
            return eventMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    private EventDTO createEventDTO(EventDTO eventDTO) { // TODO: why not just modify the existing dto?

        if (isNullOrEmpty(eventDTO.getEventId())) {
            throw new ResponseStatusException(BAD_REQUEST, "eventId must be provided");
        }
        if (eventRepository.existsByEventId(eventDTO.getEventId())) {
            throw new ResponseStatusException(CONFLICT, "Event already exists: " + eventDTO.getEventId());
        }

        if (isNullOrEmpty(eventDTO.getEventName())) {
            throw new ResponseStatusException(CONFLICT, "eventName must be provided");
        }
        if (isNullOrEmpty(eventDTO.getEventTime())) {
            throw new ResponseStatusException(CONFLICT, "eventTime must be provided");
        }
        // TODO I think we can't just assume the system's timezone
        int utcOffset = SimpleTimeZone.getTimeZone(ZoneId.systemDefault()).getOffset(System.currentTimeMillis()) / 1000;

        EventDTO newEventDTO = new EventDTO();
        newEventDTO.setEventId(eventDTO.getEventId());
        newEventDTO.setEventName(eventDTO.getEventName());
        newEventDTO.setEventTime(eventDTO.getEventTime());
        newEventDTO.setCreated(new Date());
        newEventDTO.setUpdated(new Date());
        newEventDTO.setMemberCount(0);
        newEventDTO.setEventStatus("upcoming");
        newEventDTO.setUtcOffset(utcOffset);
        // TODO why not leave it null?
        if (isNullOrEmpty(eventDTO.getDuration())) newEventDTO.setDuration(86400); //1 day
        if (isNullOrEmpty(eventDTO.getFee())) newEventDTO.setFee(FeeDTO.getDefaultDTO());

        // TODO why is this a separate method?
        setVenueAndCityForCreate(newEventDTO, eventDTO);

        setCreatorGroupForCreate(newEventDTO, eventDTO);

        return newEventDTO;

    }

    public EventDTO updateEvent(EventDTO eventDTO, String id) { // TODO revisit, especially PATCH semantics
        Event event = getEventOrThrow(id);

        userService.checkUserHasPermissionToEditEvent(id);

        try {
            Event newEvent = eventMapper.toEntity(eventDTO);

            //Fields that should remain as they were before the update
            newEvent.setEventId(id);
            newEvent.setId(event.getId());
            newEvent.setMemberCount(event.getMemberCount());
            newEvent.setCreatorGroup(event.getCreatorGroup());

            setVenueAndCityForUpdate(newEvent, eventDTO);
            newEvent.setUpdated(new Date());

            Event saved = eventRepository.save(newEvent);

            return eventMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    public void deleteEvent(String id) {
        if (!eventRepository.existsByEventId(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Event not found: " + id);
        }
        userService.checkUserHasPermissionToEditEvent(id);
        eventRepository.deleteByEventId(id);
        // TODO delete upcomingEvent embeddings and counts from member and group
    }

    public Page<EventSummaryDTO> search(
            String name,
            String city,
            String category,
            Integer minMembers,
            Integer maxMembers,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Integer maxFee,
            int page,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Query query = new Query();

        if (name != null) query.addCriteria(where("event_name").regex(name, "i"));

        // TODO searching for city id or name here?
        if (city != null) query.addCriteria(where("venue.city.name").regex(city));

        if (minMembers != null) query.addCriteria(where("member_count").gte(minMembers));

        if (maxMembers != null) query.addCriteria(where("member_count").lte(maxMembers));

        if (fromDate != null) query.addCriteria(where("event_time").gte(fromDate));

        // TODO include again?
        //        if (toDate != null)
        //            query.addCriteria(Criteria.where("event_time").lte(toDate));

        if (category != null) query.addCriteria(where("categories.name").is(category));

        if (maxFee != null) query.addCriteria(where("fee.amount").lte(maxFee));

        // TODO does this load all matched events into the application?
        List<EventSummaryDTO> results = mongoTemplate.find(query, Event.class)
                .stream()
                .map(eventSummaryMapper::toDTO)
                .toList();

        return new PageImpl<>(results, pageable, results.size());

    }

    private void setVenueAndCityForCreate(EventDTO newEventDTO, EventDTO eventDTO) {
        VenueDTO venueDTO = updateVenueForEvent(eventDTO);
        if (venueDTO != null) newEventDTO.setVenue(venueDTO);
    }

    private VenueDTO updateVenueForEvent(EventDTO eventDTO) {
        VenueDTO venueDTO = eventDTO.getVenue();
        if (venueDTO != null) {
            CityDTO cityDTO = null;
            if (!isNullOrEmpty(venueDTO.getCity())) {
                if (!isNullOrEmpty(eventDTO.getVenue().getCity().getId())) {
                    String cityId = eventDTO.getVenue().getCity().getId();
                    City city = cityRepository.findByCityId(cityId);
                    if (city != null) cityDTO = cityMapper.toDTO(city);
                } else if (!isNullOrEmpty(eventDTO.getVenue().getCity().getName())) {
                    String cityName = eventDTO.getVenue().getCity().getName();
                    City city = cityRepository.findByName(cityName);
                    if (city != null) cityDTO = cityMapper.toDTO(city);
                }
            }
            if (cityDTO != null) {
                venueDTO.setCity(cityDTO);
            }
        }
        return venueDTO;
    }

    private void setVenueAndCityForUpdate(Event newEvent, EventDTO eventDTO) {
        VenueDTO venueDTO = updateVenueForEvent(eventDTO);
        if (venueDTO != null) newEvent.setVenue(venueMapper.toEntity(venueDTO));
    }

    //TODO require the group_id or throw error and check if the user is organizer of that group
    public void setCreatorGroupForCreate(EventDTO newEventDTO, EventDTO eventDTO) {

        User user = Utils.getUserFromContext();
        String memberId = user.getMemberId();
        if (isNullOrEmpty(eventDTO.getCreatorGroup())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Please provide a groupId or a groupName that will represent the creatorGroup of the event");
        }
        String groupName = eventDTO.getCreatorGroup().getGroupName();
        String groupId = eventDTO.getCreatorGroup().getGroupId();

        List<Group> groups = groupRepository.findGroupByGroupIdOrGroupName(groupId, groupName);
        if (isNullOrEmpty(groups)) {
            throw new ResponseStatusException(NOT_FOUND,
                    "No group exists with groupId " + groupId + " or groupName " + groupName);
        }
        Group group = groups.getFirst();

        userService.checkUserHasPermissionToEditGroup(group.getGroupId());

        newEventDTO.setCreatorGroup(groupMapper.toDTO(group));
    }
}
