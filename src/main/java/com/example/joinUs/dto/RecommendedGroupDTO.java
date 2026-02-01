

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
public class RecommendedGroupDTO {
    private String groupId;
    private String groupName;
    private String link;

    // score = number of topic matches OR number of peers
    private long score;

    // explanations
    private List<String> matchedTopics;   // used for topic-based
    private List<String> sharedGroupIds;  // used for peer-based
}
