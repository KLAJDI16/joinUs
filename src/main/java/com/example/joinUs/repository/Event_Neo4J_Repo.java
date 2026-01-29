package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.Event_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Event_Neo4J_Repo extends Neo4jRepository<Event_Neo4J, String> {

}
