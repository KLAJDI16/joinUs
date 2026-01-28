package com.example.joinUs.service;

import com.example.joinUs.dto.PathNodeDTO;
import com.example.joinUs.dto.PathResponseDTO;
import com.example.joinUs.repository.Path_Neo4J_Repo;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphPathService {

    private final Path_Neo4J_Repo pathRepo;

    public GraphPathService(Path_Neo4J_Repo pathRepo) {
        this.pathRepo = pathRepo;
    }

    public PathResponseDTO shortestPath(String fromMemberId, String toMemberId, long maxDepth) {
        Optional<Path> pathOpt = pathRepo.shortestPathBetweenMembers(fromMemberId, toMemberId, maxDepth);

        if (pathOpt.isEmpty()) {
            return PathResponseDTO.builder()
                    .fromMemberId(fromMemberId)
                    .toMemberId(toMemberId)
                    .hops(-1)
                    .nodes(List.of())
                    .relationships(List.of())
                    .build();
        }

        Path path = pathOpt.get();

        List<PathNodeDTO> nodes = new ArrayList<>();
        List<String> rels = new ArrayList<>();

        // First node
        nodes.add(nodeToDto(path.start()));

        // Then each segment adds relationship + next node
        path.forEach(segment -> {
            rels.add(segment.relationship().type());
            nodes.add(nodeToDto(segment.end()));
        });

        return PathResponseDTO.builder()
                .fromMemberId(fromMemberId)
                .toMemberId(toMemberId)
                .hops(path.length())
                .nodes(nodes)
                .relationships(rels)
                .build();
    }

    private PathNodeDTO nodeToDto(Node node) {
        String label = node.labels().iterator().hasNext() ? node.labels().iterator().next() : "Unknown";

        // Try to extract a meaningful ID field depending on label
        String idKey = switch (label) {
            case "Member" -> "member_id";
            case "Group"  -> "group_id";
            case "Event"  -> "event_id";
            case "Topic"  -> "topic_id";
            default       -> "id";
        };

        String idValue = node.containsKey(idKey) ? Objects.toString(node.get(idKey).asObject()) : node.elementId();

        // Convert properties map
        Map<String, Object> props = new LinkedHashMap<>();
        node.asMap().forEach(props::put);

        return PathNodeDTO.builder()
                .labels(label)
                .idKey(idKey)
                .idValue(idValue)
                .properties(props)
                .build();
    }
}
