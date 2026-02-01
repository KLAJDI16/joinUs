package com.example.joinUs.repository;

import com.example.joinUs.dto.PopularTopicDTO;
import com.example.joinUs.model.neo4j.Topic_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Topic_Neo4J_Repo extends Neo4jRepository<Topic_Neo4J, String> {

    // Optional derived query example (only if you need it):
    // List<Topic_Neo4J> findByTopicName(String topicName);

    @Query("""
        MATCH (t:Topic)
        OPTIONAL MATCH (m:Member)-[:INTERESTED_IN]->(t)
        OPTIONAL MATCH (g:Group)-[:HAS_TOPIC]->(t)
        WITH t, count(DISTINCT m) AS memberCount, count(DISTINCT g) AS groupCount
        RETURN
          t.topic_id AS topicId,
          t.topic_name AS topicName,
          memberCount AS memberCount,
          groupCount AS groupCount,
          (memberCount + groupCount) AS totalCount
        ORDER BY totalCount DESC
        LIMIT $limit
    """)
    List<PopularTopicDTO> findMostPopularTopics(@Param("limit") long limit);
}
