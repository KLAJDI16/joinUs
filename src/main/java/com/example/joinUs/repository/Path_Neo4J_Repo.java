package com.example.joinUs.repository;

import org.neo4j.driver.types.Path;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface Path_Neo4J_Repo extends Repository<Object, Long> {

    @Query("""
        MATCH (a:Member {member_id:$fromId}), (b:Member {member_id:$toId})
        MATCH p = shortestPath((a)-[*..$maxDepth]-(b))
        RETURN p
        LIMIT 1
    """)
    Optional<Path> shortestPathBetweenMembers(
            @Param("fromId") String fromId,
            @Param("toId") String toId,
            @Param("maxDepth") long maxDepth
    );
}
