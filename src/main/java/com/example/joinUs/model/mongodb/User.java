package com.example.joinUs.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "members")
public class User {

    @Id
    @Field("member_id")
    private String memberId;

    @Field("member_name")
    private String memberName;

    @Field("city")
    private City city;

    @Field("member_status")
    private String memberStatus;

    @Field("bio")
    private String bio;

    @Field("topics")
    private List<Topic> topics;

    @Field("event_count")
    private Integer eventCount;

    @Field("group_count")
    private Integer groupCount;

    @Field("upcoming_events")
    private List<Event> upcomingEvents;

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
