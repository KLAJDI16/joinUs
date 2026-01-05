package com.example.joinUs.model.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
//@Document(collection = "topic")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {

    @Id
    private String id;
    private String name;
    private String description;
    private String urlkey;

    //    {
//        "id": 1,
//            "name": "Latin Music",
//            "description": "Meet with Latin Music fans in your town.",
//            "urlkey": "latinmusic",
//    }

}
