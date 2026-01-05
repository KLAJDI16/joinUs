package com.example.joinUs.model.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String member_name;
    private String city;
    private boolean isActive;




//"member_id","bio","city","country","hometown","joined",
// "lat","link","lon","member_name","state","member_status","visited","group_id"

    //    {
//        "id": 1,
//            "name": "Matt Meeker",
//            "bio": "Hi, I'm Matt...",
//            "hometown": "New York",
//            "joined": "2016-09-17 20:10:50",
//            "latitude": 40.72,
//            "longitude": -74.00,
//            "city": {
//        "id": 1,
//                "name": "New York",
//    },
//        "group_count": 5,
//            "event_count": 10,
//            "topic_count": 7
//    }


}
