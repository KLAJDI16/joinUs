package com.example.joinUs.service;

import com.example.joinUs.dto.GroupCommunityDTO;
import com.example.joinUs.repository.Group_Neo4J_Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityAnalyticsService {

    private final Group_Neo4J_Repo groupNeo4JRepo;

    public CommunityAnalyticsService(Group_Neo4J_Repo groupNeo4JRepo) {
        this.groupNeo4JRepo = groupNeo4JRepo;
    }

    public List<GroupCommunityDTO> getGroupCommunities(long minShared, long limit) {
        return groupNeo4JRepo.findGroupCommunities(minShared, limit);
    }
}
