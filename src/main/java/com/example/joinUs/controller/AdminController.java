package com.example.joinUs.controller;

import com.example.joinUs.dto.GroupCommunityDTO;
import com.example.joinUs.dto.PathResponseDTO;
import com.example.joinUs.dto.PopularTopicDTO;
import com.example.joinUs.service.CommunityAnalyticsService;
import com.example.joinUs.service.GraphPathService;
import com.example.joinUs.service.TopicAnalyticsService;
import org.bson.json.JsonObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TopicAnalyticsService topicAnalyticsService;
    private final GraphPathService graphPathService;
    private final CommunityAnalyticsService communityAnalyticsService;

    // ✅ SINGLE constructor – initializes ALL final fields
    public AdminController(
            TopicAnalyticsService topicAnalyticsService,
            GraphPathService graphPathService,
            CommunityAnalyticsService communityAnalyticsService
    ) {
        this.topicAnalyticsService = topicAnalyticsService;
        this.graphPathService = graphPathService;
        this.communityAnalyticsService = communityAnalyticsService;
    }

    // ------------------------
    // BASIC ADMIN CHECK
    // ------------------------
    @GetMapping("")
    public JsonObject getMetrics() {
        return new JsonObject("{\"result\":\"Successfully hit GET /admin\"}");
    }

    // ------------------------
    // NEO4J ANALYTICS
    // ------------------------

    // Popular Topics
    @GetMapping("/topics/popular")
    public List<PopularTopicDTO> getPopularTopics(
            @RequestParam(defaultValue = "20") long limit
    ) {
        return topicAnalyticsService.getMostPopularTopics(limit);
    }

    // Shortest path between two members
    @GetMapping("/paths/members")
    public PathResponseDTO shortestPathBetweenMembers(
            @RequestParam String fromId,
            @RequestParam String toId,
            @RequestParam(defaultValue = "6") long maxDepth
    ) {
        return graphPathService.shortestPath(fromId, toId, maxDepth);
    }

    // Community detection
    @GetMapping("/groups/communities")
    public List<GroupCommunityDTO> getGroupCommunities(
            @RequestParam(defaultValue = "5") long minShared,
            @RequestParam(defaultValue = "50") long limit
    ) {
        return communityAnalyticsService.getGroupCommunities(minShared, limit);
    }
}
