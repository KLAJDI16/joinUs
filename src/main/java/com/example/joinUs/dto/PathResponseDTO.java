package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathResponseDTO {
    private String fromMemberId;
    private String toMemberId;

    // number of edges in path
    private long hops;

    // ordered list of nodes in the path (simplified)
    private List<PathNodeDTO> nodes;

    // ordered list of relationship types between nodes
    private List<String> relationships;
}
