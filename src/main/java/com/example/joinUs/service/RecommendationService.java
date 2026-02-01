package com.example.joinUs.service;



import com.example.joinUs.dto.RecommendedEventDTO;
import com.example.joinUs.dto.RecommendedGroupDTO;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.Recommendation_Neo4J_Repo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final Recommendation_Neo4J_Repo recRepo;

    public RecommendationService(Recommendation_Neo4J_Repo recRepo) {
        this.recRepo = recRepo;
    }

    private String currentMemberId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal; // your Mongo User is the principal
        return user.getMember_id();
    }

    public List<RecommendedEventDTO> recommendEvents(long limit) {
        return recRepo.recommendEventsByPeerAttendance(currentMemberId(), limit);
    }

    public List<RecommendedGroupDTO> recommendGroupsByTopics(long limit) {
        return recRepo.recommendGroupsByTopics(currentMemberId(), limit);
    }

    public List<RecommendedGroupDTO> recommendGroupsByPeers(long limit) {
        return recRepo.recommendGroupsByPeers(currentMemberId(), limit);
    }

    /**
     * Optional: combine topic-based + peer-based group recommendations into one list.
     * We keep the best score per group and merge explanations.
     */
    public List<RecommendedGroupDTO> recommendGroupsCombined(long limitEach, long finalLimit) {
        List<RecommendedGroupDTO> byTopics = recommendGroupsByTopics(limitEach);
        List<RecommendedGroupDTO> byPeers  = recommendGroupsByPeers(limitEach);

        Map<String, RecommendedGroupDTO> merged = new LinkedHashMap<>();

        for (RecommendedGroupDTO g : byTopics) {
            merged.put(g.getGroupId(), g);
        }
        for (RecommendedGroupDTO g : byPeers) {
            merged.merge(g.getGroupId(), g, (a, b) -> {
                // keep max score
                long bestScore = Math.max(a.getScore(), b.getScore());
                a.setScore(bestScore);

                // merge reasons
                Set<String> topics = new LinkedHashSet<>(Optional.ofNullable(a.getMatchedTopics()).orElse(List.of()));
                topics.addAll(Optional.ofNullable(b.getMatchedTopics()).orElse(List.of()));
                a.setMatchedTopics(new ArrayList<>(topics));

                Set<String> shared = new LinkedHashSet<>(Optional.ofNullable(a.getSharedGroupIds()).orElse(List.of()));
                shared.addAll(Optional.ofNullable(b.getSharedGroupIds()).orElse(List.of()));
                a.setSharedGroupIds(new ArrayList<>(shared));

                return a;
            });
        }

        return merged.values().stream()
                .sorted(Comparator.comparingLong(RecommendedGroupDTO::getScore).reversed())
                .limit(finalLimit)
                .collect(Collectors.toList());
    }
}

