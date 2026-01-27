package com.example.joinUs.controller;



import com.example.joinUs.dto.RecommendedEventDTO;
import com.example.joinUs.dto.RecommendedGroupDTO;
import com.example.joinUs.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // 1) Event recommendations (peers in same groups)
    @GetMapping("/events")
    public List<RecommendedEventDTO> recommendEvents(
            @RequestParam(defaultValue = "20") long limit
    ) {
        return recommendationService.recommendEvents(limit);
    }

    // 2) Group recommendations by topics
    @GetMapping("/groups/by-topics")
    public List<RecommendedGroupDTO> recommendGroupsByTopics(
            @RequestParam(defaultValue = "20") long limit
    ) {
        return recommendationService.recommendGroupsByTopics(limit);
    }

    // 3) Group recommendations by peers ("people like you")
    @GetMapping("/groups/by-peers")
    public List<RecommendedGroupDTO> recommendGroupsByPeers(
            @RequestParam(defaultValue = "20") long limit
    ) {
        return recommendationService.recommendGroupsByPeers(limit);
    }

    // Optional: combined list
    @GetMapping("/groups")
    public List<RecommendedGroupDTO> recommendGroupsCombined(
            @RequestParam(defaultValue = "20") long limitEach,
            @RequestParam(defaultValue = "20") long finalLimit
    ) {
        return recommendationService.recommendGroupsCombined(limitEach, finalLimit);
    }
}
