package com.example.joinUs.repository;

import com.example.joinUs.model.neo4j.Group_Neo4J;
import com.example.joinUs.model.neo4j.User_Neo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface User_Neo4J_Repo extends Neo4jRepository<User_Neo4J,String> {

    @Query(
  """
  MATCH (p:Member)-[:MEMBER_OF]->(n:Group { group_id: $groupId })
   RETURN DISTINCT n ;
  """
    )
  List<User_Neo4J> getMembersLinkedToGroup(String groupId);


}
