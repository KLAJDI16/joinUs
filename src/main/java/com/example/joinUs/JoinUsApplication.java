package com.example.joinUs;

<<<<<<< HEAD
=======
import com.example.joinUs.repository.Event_Neo4J_Repo;
import com.example.joinUs.repository.Group_Neo4J_Repo;
import com.example.joinUs.repository.Topic_Neo4J_Repo;
import com.example.joinUs.repository.User_Neo4J_Repo;
import io.netty.util.HashingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
>>>>>>> 0d7ce7b88f65fe2189779230d548e44d0ecfbc07
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
<<<<<<< HEAD
public class JoinUsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoinUsApplication.class, args);
    }
=======
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
>>>>>>> 0d7ce7b88f65fe2189779230d548e44d0ecfbc07


	@Override
	public void run(String... args) throws Exception {
		System.out.println(userNeo4JRepo.findAll());
	}
}
