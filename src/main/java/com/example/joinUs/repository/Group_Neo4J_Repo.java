package com.example.joinUs.repository;


import com.example.joinUs.model.neo4j.Group_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Group_Neo4J_Repo extends Neo4jRepository<Group_Neo4J, String> {
    // Example: find groups by city
    // List<Group_Neo4J> findByCityName(String cityName);
}
