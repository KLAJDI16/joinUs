package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.*;
import com.example.joinUs.dto.embedded.EventEmbeddedDTO;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import static java.util.TimeZone.getTimeZone;


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
//    private Neo4jClient neo4jClient;



    @Autowired
    private Event_Neo4J_Repo eventNeo4JRepo; // TODO





    public Page<EventDTO> getAllEvents(int page, int size) {
        Page<EventDTO> events = eventRepository.findAllEvents(PageRequest.of(page, size + 1));
        return events;

    }

    public EventDTO getEventById(String id) {
        Event event = eventRepository.findByEventId(id);
        if (event == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with eventId " + id);
        return eventMapper.toDTO(event);
    }

    public UserDTO attendEvent(String id) {
        Event event = eventRepository.findByEventId(id);
        if (event == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with eventId " + id);
        try {

            User user = Utils.getUserFromContext();

            List<Event> userUpcomingEvents = user.getUpcomingEvents();
            userUpcomingEvents.add(eventEmbeddedMapper.toEntity(eventEmbeddedMapper.toDTO(event)));
            user.setUpcomingEvents(userUpcomingEvents);
            user.setEventCount(user.getEventCount() + 1);
            event.setMemberCount(event.getMemberCount() + 1);
            eventRepository.save(event);
            userRepository.save(user);

//        mongoTemplate.updateFirst(null, BasicUpdate.update().inc("event_count",1))

            //TODO complete the part for the Neo4J too
            return userMapper.toDTO(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public UserDTO dontAttendEvent(String id) {


        Event event = eventRepository.findByEventId(id);
        if (event == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with eventId " + id);
        try {
            User user = Utils.getUserFromContext();
            user.removeUpcomingEvent(id);
            List<EventEmbeddedDTO> eventEmbeddedDTOList;
            user.setEventCount(user.getEventCount() - 1);
            event.setMemberCount(event.getMemberCount() - 1);
            eventRepository.save(event);
            userRepository.save(user);
            //TODO complete the part for the Neo4J too
            return userMapper.toDTO(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public EventDTO createEvent(EventDTO eventDTO) {

        EventDTO eventDTO1 = createEventDTO(eventDTO);
        try {
            Event entity = eventMapper.toEntity(eventDTO1);
            Event saved = eventRepository.save(entity);
            return eventMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    private EventDTO createEventDTO(EventDTO eventDTO) {

        if (Utils.isNullOrEmpty(eventDTO.getEventId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventId must be provided");
        }
        if (eventRepository.existsByEventId(eventDTO.getEventId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already exists: " + eventDTO.getEventId());
        }

        if (Utils.isNullOrEmpty(eventDTO.getEventName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "eventName must be provided");
        }
        if (Utils.isNullOrEmpty(eventDTO.getEventTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "eventTime must be provided");
        }
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
        if (Utils.isNullOrEmpty(eventDTO.getDuration())) newEventDTO.setDuration(86400); //1 day
        if (Utils.isNullOrEmpty(eventDTO.getFee())) newEventDTO.setFee(FeeDTO.getDefaultDTO());

        setVenueAndCityForCreate(newEventDTO, eventDTO);

        setCreatorGroupForCreate(newEventDTO, eventDTO);

        return newEventDTO;

    }


    public EventDTO updateEvent(EventDTO eventDTO, String id) { // TODO revisit, especially PATCH semantics
        Event event = eventRepository.findByEventId(id);
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event with eventId : " + eventDTO.getEventId());
        }
        userService.checkUserHasPermissionToEditEvent(id);

        try {
            Event entity = eventMapper.toEntity(eventDTO);

            //Fields that should remain as they were before the update
            entity.setEventId(id);
            entity.setId(event.getId());
            entity.setMemberCount(event.getMemberCount());
            entity.setCreatorGroup(event.getCreatorGroup());


            setVenueAndCityForUpdate(entity, eventDTO);
            entity.setUpdated(new Date());

            Event saved = eventRepository.save(entity);


            return eventMapper.toDTO(saved);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }


    public void deleteEvent(String id) {
        if (!eventRepository.existsByEventId(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id);
        }
        userService.checkUserHasPermissionToEditEvent(id);
        eventRepository.deleteByEventId(id);
    }

    public Page<EventDTO> filterByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size + 1);
        return eventRepository.findByCategoryName(category, pageable);
    }

    public List<EventDTO> filterByMemberRange(int min,
                                              int max,
                                              int page, int size) {
        long startTime = System.currentTimeMillis();
        List<EventDTO> list = eventRepository.findSecondWayByMemberCount(min, max, page, size + 1);
        long endTime = System.currentTimeMillis();
        System.out.println("PROCESS USING AGGREGATION LASTED : " + (endTime - startTime) + " milliseconds");
        return list;
    }

    public Page<EventDTO> filterByMemberCount(
            int min,
            int max,
            int page, int size
    ) {
        long startTime = System.currentTimeMillis();

        Pageable pageable =
                PageRequest.of(page, size + 1);

        Page<EventDTO> page1 = eventRepository.findByMemberCountBetween(min, max, pageable);
        long endTime = System.currentTimeMillis();
        System.out.println("PROCESS USING QUERY LASTED : " + (endTime - startTime) + " milliseconds");
        return page1;
    }

    public Page<EventDTO> filterByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {

        try {
            LocalDateTime date = LocalDateTime.now();

            if (to != null)
                return eventRepository.findByEventTimeBetween(from != null ? from : date, to, PageRequest.of(page, size + 1));
            else {
                return eventRepository.findByEventTimeAfter(from != null ? from : date, PageRequest.of(page, size + 1));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }


    }

    public Page<EventDTO> filterByMaxFee(int maxFee, int page, int size) {
        return eventRepository.findByEventFeeIsLessOrEqual(maxFee, PageRequest.of(page, size + 1));
    }

    public Page<EventDTO> filterByCity(String city, int page, int size) {
        return eventRepository.findByCityName(city, PageRequest.of(page, size + 1));
    }

    public Page<EventDTO> findByEventNameContainingIgnoreCase(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size + 1);

        return eventRepository.searchByEventName(name, pageable);
    }

    public PageImpl<EventSummaryDTO> search(
            String name,
            String city,
            String category,
            Integer minMembers,
            Integer maxMembers,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Integer maxFee,
            Integer page,
            Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Query query = new Query();

        if (name != null)
            query.addCriteria(Criteria.where("event_name").regex(name, "i"));

        if (city != null)
            query.addCriteria(Criteria.where("venue.city.name").regex(city));

        if (minMembers != null)
            query.addCriteria(Criteria.where("member_count").gte(minMembers));

        if (maxMembers != null)
            query.addCriteria(Criteria.where("member_count").lte(maxMembers));

        if (fromDate != null)
            query.addCriteria(Criteria.where("event_time").gte(fromDate));

//        if (toDate != null)
//            query.addCriteria(Criteria.where("event_time").lte(toDate));

        if (category != null)
            query.addCriteria(Criteria.where("categories.name").is(category));

        if (maxFee != null)
            query.addCriteria(Criteria.where("fee.amount").lte(maxFee));


        List<EventSummaryDTO> results = mongoTemplate.find(query, Event.class)
                .stream()
                .map(e -> eventSummaryMapper.toDTO(e))
                .toList();

        return new PageImpl<EventSummaryDTO>(results, pageable, results.size());

    }

    private void setVenueAndCityForCreate(EventDTO newEventDTO, EventDTO eventDTO) {
        VenueDTO venueDTO = updateVenueForEvent(eventDTO);
        if (venueDTO != null) newEventDTO.setVenue(venueDTO);
    }

    private VenueDTO updateVenueForEvent(EventDTO eventDTO) {
        VenueDTO venueDTO = eventDTO.getVenue();
        if (venueDTO != null) {
            CityDTO cityDTO = null;
            if (!Utils.isNullOrEmpty(venueDTO.getCity())) {
                if (!Utils.isNullOrEmpty(eventDTO.getVenue().getCity().getId())) {
                    String cityId = eventDTO.getVenue().getCity().getId();
                    City city = cityRepository.findByCityId(cityId);
                    if (city != null) cityDTO = cityMapper.toDTO(city);
                } else if (!Utils.isNullOrEmpty(eventDTO.getVenue().getCity().getName())) {
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
        List<Group> groups = groupRepository.findGroupsByOrganizerId(memberId);
        if (!Utils.isNullOrEmpty(groups)) {
            if (groups.size() == 1) {
                Group group = groups.getFirst();
                newEventDTO.setCreatorGroup(groupMapper.toDTO(group)); //TODO replace with GroupEmbeddedDTO ?!
                return;
            } else if (eventDTO.getCreatorGroup() != null) {
                String groupId = eventDTO.getCreatorGroup().getGroupId();
                String groupName = eventDTO.getCreatorGroup().getGroupName();

                for (Group group : groups) {
                    if (!Utils.isNullOrEmpty(groupId)) {
                        if (group.getGroupId().equalsIgnoreCase(groupId)) {
                            newEventDTO.setCreatorGroup(groupMapper.toDTO(group));
                            break;
                        }
                    } else if (Utils.isNullOrEmpty(groupId) && !Utils.isNullOrEmpty(groupName)) {
                        if (group.getGroupName().equalsIgnoreCase(groupName)) {
                            newEventDTO.setCreatorGroup(groupMapper.toDTO(group));
                            break;
                        }
                    }
                }
            }
        }

    }

}
