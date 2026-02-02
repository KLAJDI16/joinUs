package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.EventNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventNeo4JRepository extends Neo4jRepository<EventNeo4J, String> {
    public static final String ATTENDS = "ATTENDS";



    @Query("MATCH ( e: Event {event_id: $eventId})    " +
            "MATCH ( m: Member {member_id: $userId})   " +
            "CREATE (e)-[:ATTENDS]-> (m) " +
            "CREATE (m)-[:ATTENDS]-> (e) ")
     void addUserAttending(String eventId, String userId);
}
