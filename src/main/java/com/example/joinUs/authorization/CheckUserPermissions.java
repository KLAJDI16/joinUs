package com.example.joinUs.authorization;

import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class CheckUserPermissions {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public MongoTemplate mongoTemplate;





}
