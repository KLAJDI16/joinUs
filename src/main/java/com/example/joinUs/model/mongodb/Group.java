package com.example.joinUs.model.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "groups")
public class Group {

    @Id
    @Field("_id")
    private ObjectId id;

    @Indexed(unique = true)
    private String groupId;

    private String description;
    private String groupName;
    private String link;
    private String timezone;
    private Date created;

    private City city;
    private List<Category> categories;
    private GroupPhoto groupPhoto;

    private Integer memberCount;
    private Integer eventCount;

    private List<User> organizerMembers;
    private List<Event> upcomingEvents;

}
