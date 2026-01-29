package com.example.joinUs.service;

import com.example.joinUs.dto.PopularGroupDTO;
import com.example.joinUs.repository.Group_Neo4J_Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupAnalyticsService {

    private final Group_Neo4J_Repo groupNeo4JRepo;

    public GroupAnalyticsService(Group_Neo4J_Repo groupNeo4JRepo) {
        this.groupNeo4JRepo = groupNeo4JRepo;
    }

    public List<PopularGroupDTO> getMostPopularGroups(long limit) {
        return groupNeo4JRepo.findMostPopularGroups(limit);
    }
}
