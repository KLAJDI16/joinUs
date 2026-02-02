package com.example.joinUs.repository;


import com.example.joinUs.model.neo4j.GroupNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupNeo4JRepository extends Neo4jRepository<GroupNeo4J, String> {

}
