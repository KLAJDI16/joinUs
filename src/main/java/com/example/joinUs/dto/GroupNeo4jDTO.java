package com.example.joinUs.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupNeo4jDTO {

    private String groupId;
    private String groupName;
    private String description;
    private String link;

    private String cityCountry;
    private String cityState;
    private String cityZip;
    private String cityLocalizedCountryName;
    private String cityName;
    private String cityCityId;
    private String cityLatitude;
    private String cityLongitude;
    private String cityDistance;


}
