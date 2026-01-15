package com.example.joinUs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityDTO {

    private String id;
    private String name;
    private String country;
    private String zip;
    private String state;
    private String localized_country_name;
    private long latitude;
    private long longitude;
    private long distance;
}