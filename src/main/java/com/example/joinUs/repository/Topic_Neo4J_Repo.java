package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.Topic_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Topic_Neo4J_Repo extends Neo4jRepository<Topic_Neo4J, String> {

    // List<Topic_Neo4J> findByTopicName(String topicName);


}