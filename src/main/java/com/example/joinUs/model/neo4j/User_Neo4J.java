package com.example.joinUs.model.neo4j;


import com.example.joinUs.dto.CityDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.model.mongodb.Topic;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Node("Member")
public class User_Neo4J {

    @Id
    @Property(name = "member_id")
    private String memberId;

    @Property(name = "member_name")
    private String memberName;

    @Property(name = "member_status")
    private String memberStatus;

    @Property(name = "bio")
    private String bio;

    // City-related fields
//    @Property(name = "hometown")
//    private String hometown;
//
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
//    private String city_id;
//
//    @Property(name = "city_distance")
//    private String city_distance;
//
//    @Property(name = "city_latitude")
//    private String city_latitude;
//
//    @Property(name = "city_longitude")
//    private String city_longitude;

//    // Other relationships
//    private List<Topic> topics;



//    public UserNeo4jDTO toDTO() {
//        return UserNeo4jDTO.builder()
//                .memberId(this.memberId)
//                .memberName(this.memberName)
//                .memberStatus(this.memberStatus)
//                .bio(this.bio)
//                   .hometown(this.hometown)
//                .cityCountry(this.city_country)
//                .cityState(this.city_state)
//                .cityZip(this.city_zip)
//                .cityLocalizedCountryName(this.city_localized_country_name)
//                .cityName(this.city_name)
//                .cityCityId(this.city_id)
//                .cityDistance(this.city_distance)
//                .cityLatitude(this.city_latitude)
//                .cityLongitude(this.city_longitude)
////                .topics(this.topics != null
////                        ? this.topics.stream().map(Topic_Neo4J::toDTO).collect(Collectors.toList())
////                        : null)
//                .build();
//    }
}
//(:Member {member_id: "2069", hometown: "New York, NY", city_country: "us",
// city_state: "NY", city_zip: "10001", city_localized_country_name: "USA",
// member_name: "Matt Meeker", city_latitude: "40.75000000", city_longitude: "-73.98999800",
// member_status: "active", city_city: "New York", city_city_id: "10001", city_distance: "2526.837"})
