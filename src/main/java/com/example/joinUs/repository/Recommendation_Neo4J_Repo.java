package com.example.joinUs.repository;



import com.example.joinUs.dto.RecommendedEventDTO;
import com.example.joinUs.dto.RecommendedGroupDTO;
import com.example.joinUs.model.neo4j.User_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Recommendation_Neo4J_Repo extends Neo4jRepository<User_Neo4J, String> {

    /**
     * 1) Recommend events attended by peers (members sharing a group with me),
     * excluding events I already attend.
     *
     * Score = number of distinct peers attending the event.
     */
    @Query("""
        MATCH (me:Member {member_id: $memberId})-[:MEMBER_OF]->(g:Group)<-[:MEMBER_OF]-(peer:Member)
        MATCH (peer)-[:ATTENDS]->(e:Event)
        WHERE NOT (me)-[:ATTENDS]->(e)
        RETURN
          e.event_id   AS eventId,
          e.event_name AS eventName,
          e.event_url  AS eventUrl,
          toString(e.event_time) AS eventTime,
          count(DISTINCT peer) AS score,
          collect(DISTINCT g.group_id)[0..3] AS basedOnGroupIds
        ORDER BY score DESC
        LIMIT $limit
    """)
    List<RecommendedEventDTO> recommendEventsByPeerAttendance(
            @Param("memberId") String memberId,
            @Param("limit") long limit
    );

    /**
     * 2) Recommend groups by topics the user is interested in.
     *
     * Score = number of matching topics.
     */
    @Query("""
        MATCH (me:Member {member_id: $memberId})-[:INTERESTED_IN]->(t:Topic)<-[:HAS_TOPIC]-(g:Group)
        WHERE NOT (me)-[:MEMBER_OF]->(g)
        RETURN
          g.group_id   AS groupId,
          g.group_name AS groupName,
          g.link       AS link,
          count(DISTINCT t) AS score,
          collect(DISTINCT t.topic_name)[0..5] AS matchedTopics,
          [] AS sharedGroupIds
        ORDER BY score DESC
        LIMIT $limit
    """)
    List<RecommendedGroupDTO> recommendGroupsByTopics(
            @Param("memberId") String memberId,
            @Param("limit") long limit
    );

    /**
     * 3) Recommend groups based on my current groups and other members' groups ("people like you").
     *
     * Score = number of distinct peers who are in that group.
     */
    @Query("""
        MATCH (me:Member {member_id: $memberId})-[:MEMBER_OF]->(g1:Group)<-[:MEMBER_OF]-(peer:Member)
        MATCH (peer)-[:MEMBER_OF]->(g2:Group)
        WHERE NOT (me)-[:MEMBER_OF]->(g2)
        RETURN
          g2.group_id   AS groupId,
          g2.group_name AS groupName,
          g2.link       AS link,
          count(DISTINCT peer) AS score,
          [] AS matchedTopics,
          collect(DISTINCT g1.group_id)[0..5] AS sharedGroupIds
        ORDER BY score DESC
        LIMIT $limit
    """)
    List<RecommendedGroupDTO> recommendGroupsByPeers(
            @Param("memberId") String memberId,
            @Param("limit") long limit
    );
}
