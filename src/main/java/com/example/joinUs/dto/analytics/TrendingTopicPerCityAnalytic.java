package com.example.joinUs.dto.analytics;

import lombok.Data;

import java.util.List;

@Data
public class TrendingTopicPerCityAnalytic {
    public String city;
    public List<TopicPerUserAnalytic> topTopics;
}
