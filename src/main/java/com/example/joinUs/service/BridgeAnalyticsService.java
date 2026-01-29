package com.example.joinUs.service;

import com.example.joinUs.dto.BridgeGroupDTO;
import com.example.joinUs.repository.Group_Neo4J_Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BridgeAnalyticsService {

    private final Group_Neo4J_Repo groupNeo4JRepo;

    public BridgeAnalyticsService(Group_Neo4J_Repo groupNeo4JRepo) {
        this.groupNeo4JRepo = groupNeo4JRepo;
    }

    public List<BridgeGroupDTO> getBridgeGroups(long limit) {
        return groupNeo4JRepo.findBridgeGroups(limit);
    }
}
