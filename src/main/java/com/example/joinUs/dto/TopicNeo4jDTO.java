package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicNeo4jDTO {

    private String topicId;
    private String topicName;
    private String description;
    private String link;
}
