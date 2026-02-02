package com.example.joinUs.dto.analytics;

import lombok.Data;

@Data
public class ActivityScorePerUserAnalytic {
    public    String userId;
    public   String userName;
    public    Integer activityScore;
}
