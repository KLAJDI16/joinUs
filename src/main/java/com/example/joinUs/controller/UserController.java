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

}
