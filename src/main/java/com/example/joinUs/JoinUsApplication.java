package com.example.joinUs;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.neo4j.User_Neo4J;
import com.example.joinUs.repository.User_Neo4J_Repo;
import com.example.joinUs.service.EventService;
import com.example.joinUs.service.GroupService;
import com.example.joinUs.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;

import java.util.List;


@SpringBootApplication
public class JoinUsApplication implements CommandLineRunner {

    @Autowired
    public EventService eventService;

    @Autowired
    public UserService userService;

    @Autowired
    public GroupService groupService;

    public static void main(String[] args) {
        SpringApplication.run(JoinUsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
