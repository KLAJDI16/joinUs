package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.*;
import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.mapping.UserMapper;
import com.example.joinUs.model.mongodb.City;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.model.neo4j.User_Neo4J;
import com.example.joinUs.repository.EventRepository;
import com.example.joinUs.repository.GroupRepository;
import com.example.joinUs.repository.UserRepository;
import com.example.joinUs.repository.User_Neo4J_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authManager;

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

    @Autowired
    private UserMapper userMapper;

//    public List<UserDTO> getAllUsers() {
//        return null; // TODO
//        return userRepository.findAll().stream()
//                .map(user -> user.toDTO())
//                .toList();
//    }

    public List<User_Neo4J> getMembersOfAgroup(String groupId){

        return  userNeo4JRepo.getMembersLinkedToGroup(groupId);
    }

    public UserDTO getUserProfile(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = null;

        Authentication authentication = securityContext.getAuthentication();
        if (authentication!=null){
           user= (User) authentication.getPrincipal();
        }
        return userMapper.toDTO(user);
    }
    public UserDTO editUserProfile(UserDTO userUpdates){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User member = (User) securityContext.getAuthentication().getPrincipal();
        String userId = member.getMemberId();

        // Fetch the existing user from DB
        User existingUser = userRepository.findByMember_id(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only the allowed fields
        if (userUpdates.getBio() != null) existingUser.setBio(userUpdates.getBio());
        if (userUpdates.getMemberName() != null) existingUser.setMemberName(userUpdates.getMemberName());
        if (userUpdates.getMemberStatus() != null) existingUser.setMemberStatus(userUpdates.getMemberStatus());

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
            if (updatedCity.getLocalizedCountryName() != null)
                existingCity.setLocalizedCountryName(updatedCity.getLocalizedCountryName());

            existingUser.setCity(existingCity);
        }

        // Save the updated user
        userRepository.save(existingUser);

//        return existingUser.toDTO();
        return null;
    }

    public List<UserNeo4jDTO> getAllUsersFromGraph() {
        return userNeo4JRepo.findAll().stream().map(userNeo4J -> userNeo4J.toDTO()).toList();
    }

//    public User createUser(UserDTO userDTO) {
//        User user = User.(userDTO);
//        userRepository.save(user);
//        return user;
//    }

    public boolean checkUserHasPermissionToEditGroup(String groupId)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null || !authentication.isAuthenticated() ){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User is not authenticated in the application");
        }
        User user = (User) authentication.getPrincipal();
        String userId=user.getMemberId();
        List<Group> groups = groupRepository.findGroupsByOrganizerId(userId);
        if (groups==null|| groups.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You do not have permission to edit this group");

        for (Group group : groups){
            if (group.getGroupId().equalsIgnoreCase(groupId)) return true;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You do not have permission to edit this group");


    }

    public boolean checkUserHasPermissionToEditEvent(String eventId)  {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if ( authentication==null || !authentication.isAuthenticated()){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User is not authenticated in the application");
//        }
//        User user = (User) authentication.getPrincipal();
        User user = Utils.getUserFromContext();
        String userId=user.getMemberId();
        List<Group> groups = groupRepository.findGroupsByOrganizerId(userId);
        if (groups==null)  throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You do not have permission to edit this event");

//        boolean foundEvent;
        for (Group group : groups){
            List<Event> events = eventRepository.findEventsByCreatorGroup(group.getGroupId());
            for (Event event : events) {
                if (event.getEventId().equalsIgnoreCase(eventId)) return true;
            }
        }
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You do not have permission to edit this event");
    }

    //TODO implement correctly
    public ResponseMessage registerUser(UserDTO userDTO) {
       User user = userMapper.toEntity(userDTO);
       user.setEventCount(0);
       user.setGroupCount(0);

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDTO.getMemberName(), userDTO.getPassword()
                    )
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            return new ResponseMessage("success", "Your registration was succesful");
        } catch (Exception e) {
         throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Your registration failed "+"\n Error : "+e.getMessage().toString());
        }
    }

    public SecurityContext loginUser(LoginForm loginForm){
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.username, loginForm.password
                )
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        return context;

    }
}

