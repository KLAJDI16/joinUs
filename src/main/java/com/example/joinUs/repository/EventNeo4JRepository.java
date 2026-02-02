package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.EventNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventNeo4JRepository extends Neo4jRepository<EventNeo4J, String> {
    public static final String ATTENDS = "ATTENDS";

    @Query("""
MATCH (u:Event { event_Id: $eventId })
DETACH DELETE u
""")
    void deleteEvent(String eventId);

    @Query("MATCH ( e: Event {event_id: $eventId})    " +
            "MATCH ( m: Member {member_id: $userId})   " +
            "MERGE (e)-[:ATTENDS]-> (m) " +
            "MERGE (m)-[:ATTENDS]-> (e) ")
     void addUserAttending(String eventId, String userId);

    @Query("""
MATCH (e:Event {event_id: $eventId})-[r1:ATTENDS]->(m:Member {member_id: $userId})
MATCH (m)-[r2:ATTENDS]->(e)
DELETE r1, r2
""")
    void revokeUserAttending(String eventId, String userId);

    @Query("""
MATCH (g:Group {group_id: $groupId})
MATCH (e:Event {event_id: $eventId})
MERGE (g)-[:HAS_EVENT]->(e)
MERGE (e)-[:HAS_EVENT]->(g)
""")
    void addGroupEventRelation(String groupId, String eventId);

    @Query("""
MATCH (g:Group {group_id: $groupId})-[r1:HAS_EVENT]->(e:Event {event_id: $eventId})
MATCH (e)-[r2:HAS_EVENT]->(g)
DELETE r1, r2
""")
    void removeGroupEventRelation(String groupId, String eventId);

    @Query("""
        MATCH (g:Group {group_id: $groupId})-[:ORGANIZES]-(e:Event)
        RETURN e
    """)
    List<EventNeo4J> findAllEventsOrganizedByGroup(String groupId);

    @Query("""
    MATCH (m:Member {member_id: $memberId})-[:ATTENDS]-(e:Event)
    RETURN e
""")
    List<EventNeo4J> findAllEventsAttendedByUser(String memberId);

}
