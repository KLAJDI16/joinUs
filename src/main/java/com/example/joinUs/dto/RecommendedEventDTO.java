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
public class RecommendedEventDTO {
    private String eventId;
    private String eventName;
    private String eventUrl;
    private String eventTime;

    // a simple score (e.g., number of peers attending)
    private long score;

    // optional explanations
    private List<String> basedOnGroupIds;
}
