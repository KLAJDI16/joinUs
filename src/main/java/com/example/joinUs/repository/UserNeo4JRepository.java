package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.UserNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeo4JRepository extends Neo4jRepository<UserNeo4J,String> {

    @Query(
  """
  MATCH (p:Member)-[:MEMBER_OF]->(n:Group { group_id: $groupId })
   RETURN DISTINCT n ;
  """
    )
  List<UserNeo4J> getMembersLinkedToGroup(String groupId);

    @Query("""
MATCH (u:Member { member_id: $memberId })
DETACH DELETE u
""")
    void deleteUser(String memberId);

}
