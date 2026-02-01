package com.example.joinUs.service;

import com.example.joinUs.dto.PopularTopicDTO;
import com.example.joinUs.repository.Topic_Neo4J_Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicAnalyticsService {

    private final Topic_Neo4J_Repo topicNeo4JRepo;

    public TopicAnalyticsService(Topic_Neo4J_Repo topicNeo4JRepo) {
        this.topicNeo4JRepo = topicNeo4JRepo;
    }

    public List<PopularTopicDTO> getMostPopularTopics(long limit) {
        return topicNeo4JRepo.findMostPopularTopics(limit);
    }
}
