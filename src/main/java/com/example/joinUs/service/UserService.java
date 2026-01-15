package com.example.joinUs.service;

import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.repository.UserRepository;
import com.example.joinUs.repository.User_Neo4J_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private User_Neo4J_Repo userNeo4JRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> user.toDTO())
                .toList();
    }

    public List<UserNeo4jDTO> getAllUsersFromGraph() {
        return userNeo4JRepo.findAll().stream().map(userNeo4J -> userNeo4J.toDTO()).toList();
    }

}

