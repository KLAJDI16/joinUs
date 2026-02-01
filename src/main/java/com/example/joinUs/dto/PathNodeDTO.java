package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathNodeDTO {
    private String labels;     // e.g. "Member", "Group", "Topic", "Event"
    private String idKey;      // e.g. member_id, group_id, event_id, topic_id
    private String idValue;    // actual value
    private Map<String, Object> properties; // optional extra info
}
