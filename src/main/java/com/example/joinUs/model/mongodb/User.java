package com.example.joinUs.model.mongodb;

import com.example.joinUs.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.cypher.internal.expressions.functions.E;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "members")
public class User {

    @Id
    @Field("member_id")
    private String member_id;

    @Field("member_name")
    private String member_name;

    @Field("city")
    private City city;

    @Field("member_status")
    private String member_status;

    @Field("bio")
    private String bio;

    @Field("topics")
    private List<Topic> topics;

    @Field("event_count")
    private double event_count;

    @Field("group_count")
    private double group_count;

    @Field("upcoming_events")
    private List<Event> upcoming_events;

    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();

        dto.setMember_id(this.member_id);
        dto.setMember_name(this.member_name);

        if (this.city != null) {
            dto.setCity(this.city.toDTO());
        }

        dto.setMember_status(this.member_status);
        dto.setBio(this.bio);

        dto.setTopics(this.topics);

        dto.setEvent_count(this.event_count);
        dto.setGroup_count(this.group_count);

        if (this.upcoming_events != null) {
            dto.setUpcoming_events(
                    this.upcoming_events.stream()
                            .map(Event::toDTO)
                            .toList()
            );
        }

        return dto;
    }


//"member_id","bio","city","country","hometown","joined",
// "lat","link","lon","member_name","state","member_status","visited","group_id"

//    {
//        "_id": {…},
//        "member_id": "8386",
//            "member_name": "Scott Heiferman",
//            "member_status": "active",
//            "link": "http://www.meetup.com/members/6",
//            "bio": "Community organizer",
//            "topics": […],
//        "city": {…},
//        "event_count": 28,
//            "upcoming_events": […],
//        "group_count": {…}
//    }


}
