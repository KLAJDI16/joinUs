package com.example.joinUs.model.mongodb;

import com.example.joinUs.dto.CityDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "cities")
public class City {

    @Id
    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Field("country")
    private String country;

    @Field("zip")
    private String zip;

    @Field("state")
    private String state;

    @Field("localized_country_name")
    private String localized_country_name;

    @Field("latitude")
    private long latitude;

    @Field("longitude")
    private long longitude;

    @Field("distance")
    private long distance;

    public CityDTO toDTO() {
        CityDTO dto = new CityDTO();

        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCountry(this.country);
        dto.setZip(this.zip);
        dto.setState(this.state);
        dto.setLocalized_country_name(this.localized_country_name);
        dto.setLatitude(this.latitude);
        dto.setLongitude(this.longitude);
        dto.setDistance(this.distance);

        return dto;
    }
}
