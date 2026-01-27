package com.example.joinUs.model.neo4j;

import com.example.joinUs.dto.GroupNeo4jDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Node("Group")
public class Group_Neo4J {

    @Id
    @Property("group_id")
    private String groupId;
    @Property("group_name")
    private String groupName;
    @Property("description")
    private String description;
    @Property("link")
    private String link;

    // City fields
//    @Property(name = "city_country")
//    private String city_country;
//
//    @Property(name = "city_state")
//    private String city_state;
//
//    @Property(name = "city_zip")
//    private String city_zip;
//
//    @Property(name = "city_localized_country_name")
//    private String city_localized_country_name;
//
//    @Property(name = "city_city")
//    private String city_name;
//
//    @Property(name = "city_city_id")
//    private String city_city_id;
//
//    @Property(name = "city_latitude")
//    private String city_latitude;
//
//    @Property(name = "city_longitude")
//    private String city_longitude;
//
//    @Property(name = "city_distance")
//    private String city_distance;
//
//
//    public GroupNeo4jDTO toDTO() {
//        return GroupNeo4jDTO.builder()
//                .groupId(this.groupId)
//                .groupName(this.groupId)
//                .description(this.description)
//                .link(this.link)
//                .cityCountry(this.city_country)
//                .cityState(this.city_state)
//                .cityZip(this.city_zip)
//                .cityLocalizedCountryName(this.city_localized_country_name)
//                .cityName(this.city_name)
//                .cityCityId(this.city_city_id)
//                .cityLatitude(this.city_latitude)
//                .cityLongitude(this.city_longitude)
//                .cityDistance(this.city_distance)
//                .build();
//    }
}

