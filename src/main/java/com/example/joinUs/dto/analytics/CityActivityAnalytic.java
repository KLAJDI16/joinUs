package com.example.joinUs.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityActivityAnalytic {

    private String city;
    private Integer totalMembers;
    private Integer activeMembers;
    private Double activityRatio;
}