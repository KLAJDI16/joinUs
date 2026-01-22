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
    private String latitude;

    @Field("longitude")
    private String longitude;

    @Field("distance")
    private String distance;

    public CityDTO toDTO() {
        CityDTO dto = new CityDTO();

        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCountry(this.country);
        dto.setZip(this.zip);
        dto.setState(this.state);
        dto.setLocalizedCountryName(this.localized_country_name);
        dto.setLatitude(this.latitude);
        dto.setLongitude(this.longitude);
        dto.setDistance(this.distance);

        return dto;
    }
    public static City fromDTO(CityDTO dto) {
        if (dto == null) return null;

        City city = new City();

        city.setId(dto.getId());
        city.setName(dto.getName());
        city.setCountry(dto.getCountry());
        city.setZip(dto.getZip());
        city.setState(dto.getState());
        city.setLocalized_country_name(dto.getLocalizedCountryName());
        city.setLatitude(dto.getLatitude());
        city.setLongitude(dto.getLongitude());
        city.setDistance(dto.getDistance());

        return city;
    }

}
