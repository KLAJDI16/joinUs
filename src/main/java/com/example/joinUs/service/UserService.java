package com.example.joinUs.service;

import com.example.joinUs.dto.CityDTO;
import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.model.mongodb.City;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.EventRepository;
import com.example.joinUs.repository.GroupRepository;
import com.example.joinUs.repository.UserRepository;
import com.example.joinUs.repository.User_Neo4J_Repo;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private User_Neo4J_Repo userNeo4JRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> user.toDTO())
                .toList();
    }

    public UserDTO getUserProfile(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object principal = securityContext.getAuthentication().getPrincipal();
        User user = (User) principal;
        return user.toDTO();
    }
    public UserDTO editUserProfile(UserDTO userUpdates){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User member = (User) securityContext.getAuthentication().getPrincipal();
        String userId = member.getMember_id();

        // Fetch the existing user from DB
        User existingUser = userRepository.findByMember_id(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only the allowed fields
        if (userUpdates.getBio() != null) existingUser.setBio(userUpdates.getBio());
        if (userUpdates.getMember_name() != null) existingUser.setMember_name(userUpdates.getMember_name());
        if (userUpdates.getMember_status() != null) existingUser.setMember_status(userUpdates.getMember_status());

        if (userUpdates.getCity() != null) {
            CityDTO updatedCity = userUpdates.getCity();
            City existingCity = existingUser.getCity() != null ? existingUser.getCity() : new City();

            if (updatedCity.getId() != null) existingCity.setId(updatedCity.getId());
            if (updatedCity.getName() != null) existingCity.setName(updatedCity.getName());
            if (updatedCity.getCountry() != null) existingCity.setCountry(updatedCity.getCountry());
            if (updatedCity.getState() != null) existingCity.setState(updatedCity.getState());
            if (updatedCity.getZip() != null) existingCity.setZip(updatedCity.getZip());
            if (updatedCity.getLatitude() != null) existingCity.setLatitude(updatedCity.getLatitude());
            if (updatedCity.getLongitude() != null) existingCity.setLongitude(updatedCity.getLongitude());
            if (updatedCity.getDistance() != null) existingCity.setDistance(updatedCity.getDistance());
            if (updatedCity.getLocalized_country_name() != null)
                existingCity.setLocalized_country_name(updatedCity.getLocalized_country_name());

            existingUser.setCity(existingCity);
        }

        // Save the updated user
        userRepository.save(existingUser);

        return existingUser.toDTO();
    }

    public List<UserNeo4jDTO> getAllUsersFromGraph() {
        return userNeo4JRepo.findAll().stream().map(userNeo4J -> userNeo4J.toDTO()).toList();
    }

    public User createUser(UserDTO userDTO) {
        User user = User.fromDTO(userDTO);
        userRepository.save(user);
        return user;
    }

    public JsonObject userHasPermissionToEditGroup(String groupId) throws ApplicationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()){
            throw new ApplicationException("User is not authenticated in the application");
        }
        User user = (User) authentication.getPrincipal();
        String userId=user.getMember_id();
        List<Group> groups = groupRepository.findGroupsByOrganizerId(userId);
        if (groups==null|| groups.isEmpty())
            return new JsonObject("""
                {"result":"You do not have permission to edit this group"}
                """);

        for (Group group : groups){
            if (group.getGroup_id().equalsIgnoreCase(groupId)) return null;
        }

        return new JsonObject("""
                {"result":"You do not have permission to edit this group"}
                """);

    }

    public JsonObject userHasPermissionToEditEvent(String eventId) throws ApplicationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()){
            throw new ApplicationException("User is not authenticated in the application");
        }
        User user = (User) authentication.getPrincipal();
        String userId=user.getMember_id();
        List<Group> groups = groupRepository.findGroupsByOrganizerId(userId);
        if (groups==null)   return new JsonObject("""
                {"result":"You do not have permission to edit this event"}
                """);;

        for (Group group : groups){
            List<Event> events = eventRepository.findEventsByCreator_group(group.getGroup_id());
            for (Event event : events) {
                if (event.getEvent_id().equalsIgnoreCase(eventId)) return null;
            }
        }

            return new JsonObject("""
                {"result":"You do not have permission to edit this event"}
                """);
    }

}

