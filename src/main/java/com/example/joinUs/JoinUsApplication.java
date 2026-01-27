package com.example.joinUs;

import com.example.joinUs.repository.Event_Neo4J_Repo;
import com.example.joinUs.repository.Group_Neo4J_Repo;
import com.example.joinUs.repository.Topic_Neo4J_Repo;
import com.example.joinUs.repository.User_Neo4J_Repo;
import io.netty.util.HashingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Security;

@SpringBootApplication
@EnableAutoConfiguration
public class JoinUsApplication implements CommandLineRunner {

	@Autowired
	User_Neo4J_Repo userNeo4JRepo;

	@Autowired
	Event_Neo4J_Repo eventNeo4JRepo;

	@Autowired
	Group_Neo4J_Repo groupNeo4JRepo;

	@Autowired
	Topic_Neo4J_Repo topicNeo4JRepo;

	public static void main(String[] args) {
		SpringApplication.run(JoinUsApplication.class, args);
		System.out.println("================================================================");

	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println(userNeo4JRepo.findAll());
	}
}
