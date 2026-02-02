package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.TopicNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicNeo4JRepository extends Neo4jRepository<TopicNeo4J, String> {

    // List<Topic_Neo4J> findByTopicName(String topicName);


}