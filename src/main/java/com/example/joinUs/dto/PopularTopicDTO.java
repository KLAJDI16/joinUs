package com.example.joinUs.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularTopicDTO {
    private String topicId;
    private String topicName;

    private long memberCount;  // how many members interested
    private long groupCount;   // how many groups have it
    private long totalCount;   // memberCount + groupCount
}
