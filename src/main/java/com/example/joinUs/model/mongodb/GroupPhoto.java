package com.example.joinUs.model.mongodb;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPhoto {
    private String base_url;
    private String highres_link;
    private String photo_id;
    private String photo_link;
    private String thumb_link;
    private String type;

//     "base_url": "https://secure.meetupstatic.com",
//                "highres_link": "https://secure.meetupstatic.com/photos/event/1/d/3/a/highres_61087482.jpeg",
//                "photo_id": "61087482",
//                "photo_link": "https://secure.meetupstatic.com/photos/event/1/d/3/a/600_61087482.jpeg",
//                "thumb_link": "https://secure.meetupstatic.com/photos/event/1/d/3/a/thumb_61087482.jpeg",
//                "type": "event"
}
