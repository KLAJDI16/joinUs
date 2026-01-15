package com.example.joinUs.controller;

import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/fromGraph")
    public List<UserNeo4jDTO> getAllUsersFromGraph() {
        return userService.getAllUsersFromGraph();
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
