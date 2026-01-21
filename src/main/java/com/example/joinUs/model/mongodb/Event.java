package com.example.joinUs.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "events")
public class Event {

    @Id
    @Field("_id")
    private ObjectId id;

    private String eventId;
    private String description;
    private String eventUrl;
    private String eventName;
    private String eventStatus;

    private Date created;
    private Date eventTime;
    private Date updated;

    private Integer duration;
    private Integer utcOffset;

    private Fee fee;
    private Venue venue;
    private List<Category> categories;

    private Integer memberCount;

    private Group creatorGroup;

}

