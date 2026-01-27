package com.example.joinUs.controller;

import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
//
//    @GetMapping("")
//    public List<UserDTO> getAllUsers() {
//        return userService.getAllUsers();
//    }
//    @GetMapping("/fromGraph")
//    public List<UserNeo4jDTO> getAllUsersFromGraph() {
//        return userService.getAllUsersFromGraph();
//    }

    @GetMapping("")
    public JsonObject getAllUsers() {
        JsonObject jsonObject = new JsonObject("{\"result\":\"Successfully hit GET /users \"}");
        return jsonObject;
    }
    @PostMapping("")
    public JsonObject createUser() {
        JsonObject jsonObject = new JsonObject("{\"result\":\"Successfully hit POST /users \"}");
        return jsonObject;
    }

    @GetMapping("/profile")
    public ResponseEntity getUserProfile(){
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity editUserProfile(@RequestBody UserDTO user){
        return ResponseEntity.ok().body(userService.editUserProfile(user));
    }
    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserProfile(HttpServletRequest request){
         userService.deleteProfile();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }







//    // Example: get user by id
//    @GetMapping("/{id}")
//    public UserDTO getUserById(@PathVariable String id) {
//        return userService.getUserById(id);
//    }

    // Example: create a new user
//    @PostMapping("")
//    public UserDTO createUser(@RequestBody UserDTO userDTO) {
//        return userService.createUser(userDTO);
//    }
}
