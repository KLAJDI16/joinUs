package com.example.joinUs.repository;

import com.example.joinUs.dto.GroupCommunityDTO;
import com.example.joinUs.model.neo4j.Group_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Group_Neo4J_Repo extends Neo4jRepository<Group_Neo4J, String> {

    // Example derived query (optional)
    // List<Group_Neo4J> findByCityName(String cityName);

    @Query("""
        MATCH (g1:Group)<-[:MEMBER_OF]-(m:Member)-[:MEMBER_OF]->(g2:Group)
        WHERE g1 <> g2 AND g1.group_id < g2.group_id
        WITH g1, g2, count(DISTINCT m) AS sharedMembers
        WHERE sharedMembers >= $minShared
        RETURN
          g1.group_id AS group1Id,
          g1.group_name AS group1Name,
          g2.group_id AS group2Id,
          g2.group_name AS group2Name,
          sharedMembers AS sharedMembers
        ORDER BY sharedMembers DESC
        LIMIT $limit
    """)
    List<GroupCommunityDTO> findGroupCommunities(
            @Param("minShared") long minShared,
            @Param("limit") long limit
    );
}
