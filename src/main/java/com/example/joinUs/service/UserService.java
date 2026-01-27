package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.*;
import com.example.joinUs.mapping.UserMapper;
import com.example.joinUs.model.mongodb.City;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.*;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public List<UserDTO> getAllUsers() {
//        return null; // TODO
//        return userRepository.findAll().stream()
//                .map(user -> user.toDTO())
//                .toList();
//    }

    public UserDTO getUserProfile(){
        User user = Utils.getUserFromContext();
        return userMapper.toDTO(user);
    }
    public UserDTO editUserProfile(UserDTO userUpdate){

        User existingUser = Utils.getUserFromContext();
        String userId = existingUser.getMemberId();

        userUpdate.setMemberId(userId);


        // Update only the allowed fields ,the others remain as they were
        if (userUpdate.getBio() != null) existingUser.setBio(userUpdate.getBio());
        if (userUpdate.getMemberName() != null) existingUser.setMemberName(userUpdate.getMemberName());
        if (userUpdate.getMemberStatus() != null) existingUser.setMemberStatus(userUpdate.getMemberStatus());
        if (userUpdate.getPassword()!=null) existingUser.setPassword(passwordEncoder.encode(userUpdate.getPassword()));

        if (userUpdate.getCity() != null) {
            CityDTO updatedCity = userUpdate.getCity();
            if (!Utils.isNullOrEmpty(updatedCity.getId())) {
                City city = cityRepository.findByCityId(updatedCity.getId());
                if (city != null) existingUser.setCity(city);
            }
            else if (!Utils.isNullOrEmpty(updatedCity.getName())){
                City city = cityRepository.findByName(updatedCity.getName());
                if (city != null) existingUser.setCity(city);
            }
        }

        userRepository.save(existingUser);

        return userMapper.toDTO(existingUser);
    }


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
    public SecurityContext registerUser(UserDTO userDTO) {

     if (!Utils.isNullOrEmpty(userRepository.findMemberByName(userDTO.getMemberName())))
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A user with the username "+userDTO.getMemberName()+" is already registered");
     if (Utils.isNullOrEmpty(userDTO.getPassword())){
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please provide a password");
     }
   try{
       String password=userDTO.getPassword();
       User user = userMapper.toEntity(userDTO);
       user.setEventCount(0);
       user.setGroupCount(0);
       user.setIsAdmin(false);
       user.setPassword(passwordEncoder.encode(password));
       user.setUpcomingEvents(new ArrayList<>());

        if (userDTO.getBio() != null) user.setBio(userDTO.getBio());
        if (userDTO.getMemberName() != null) user.setMemberName(userDTO.getMemberName());
        if (userDTO.getMemberStatus() != null) user.setMemberStatus(userDTO.getMemberStatus());

        if (userDTO.getCity() != null) {
            CityDTO updatedCity = userDTO.getCity();
            if (!Utils.isNullOrEmpty(updatedCity.getId())) {
                City city = cityRepository.findByCityId(updatedCity.getId());
                if (city != null) user.setCity(city);
            }
            else if (!Utils.isNullOrEmpty(updatedCity.getName())){
                City city = cityRepository.findByName(updatedCity.getName());
                if (city != null) user.setCity(city);
            }
        }
       userRepository.save(user);
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getMemberName(), password
                )
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

            return context;
        } catch (Exception e) {
         throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Your registration failed "+"\n Error : "+e.getMessage());
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

    public void deleteProfile() {
        User user = Utils.getUserFromContext();
        userRepository.delete(user);
    }
}

