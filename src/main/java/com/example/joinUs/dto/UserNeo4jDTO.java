package com.example.joinUs.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNeo4jDTO {

    private String memberId;
    private String memberName;
    private String memberStatus;
    private String bio;
    private String eventCount;
    private String groupCount;

    private String hometown;
    private String cityCountry;
    private String cityState;
    private String cityZip;
    private String cityLocalizedCountryName;
    private String cityName;
    private String cityCityId;
    private String cityDistance;
    private String cityLatitude;
    private String cityLongitude;


}
