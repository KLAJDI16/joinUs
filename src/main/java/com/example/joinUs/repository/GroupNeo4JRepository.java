package com.example.joinUs.repository;


import com.example.joinUs.model.neo4j.GroupNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupNeo4JRepository extends Neo4jRepository<GroupNeo4J, String> {

    @Query("""
MATCH (u:Group { group_id: $groupId })
DETACH DELETE u
""")
    void deleteGroup(String groupId);

    @Query("""
        MATCH (m:Member {member_id: $memberId})-[:MEMBER_OF]-(g:Group)
        RETURN DISTINCT g
    """)
    List<GroupNeo4J> findAllGroupsOfUser(String memberId);


}
