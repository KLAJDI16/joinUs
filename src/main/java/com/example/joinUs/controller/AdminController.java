package com.example.joinUs.controller;

import com.example.joinUs.dto.*;
import com.example.joinUs.service.*;
import org.bson.json.JsonObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TopicAnalyticsService topicAnalyticsService;
    private final GraphPathService graphPathService;
    private final CommunityAnalyticsService communityAnalyticsService;
    private final GroupAnalyticsService groupAnalyticsService;
    private final BridgeAnalyticsService bridgeAnalyticsService;

    // ✅ ONE constructor – initializes ALL final fields
    public AdminController(
            TopicAnalyticsService topicAnalyticsService,
            GraphPathService graphPathService,
            CommunityAnalyticsService communityAnalyticsService,
            GroupAnalyticsService groupAnalyticsService,
            BridgeAnalyticsService bridgeAnalyticsService
    ) {
        this.topicAnalyticsService = topicAnalyticsService;
        this.graphPathService = graphPathService;
        this.communityAnalyticsService = communityAnalyticsService;
        this.groupAnalyticsService = groupAnalyticsService;
        this.bridgeAnalyticsService = bridgeAnalyticsService;
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

    // Top groups by members
    @GetMapping("/groups/popular")
    public List<PopularGroupDTO> getPopularGroups(
            @RequestParam(defaultValue = "20") long limit
    ) {
        return groupAnalyticsService.getMostPopularGroups(limit);
    }

    // Bridge groups
    @GetMapping("/groups/bridge")
    public List<BridgeGroupDTO> getBridgeGroups(
            @RequestParam(defaultValue = "20") long limit
    ) {
        return bridgeAnalyticsService.getBridgeGroups(limit);
    }
}
