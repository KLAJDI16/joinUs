package com.example.joinUs.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "cities")
public class City {

    @Id
    private String id;
    private String name;

    private String country;
    private String zip;
    private String state;
    private String localizedCountryName;

    private Double latitude;
    private Double longitude;
    private Double distance;

}
