package com.example.joinUs.model.mongodb;


import com.example.joinUs.dto.GroupDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
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
    @Field("group_id")
    private String group_id;

    @Field("description")
    private String description;

    @Field("group_name")
    private String group_name;

    @Field("link")
    private String link;

    @Field("timezone")
    private String timezone;

    @Field("created")
    private Date created;

    @Field("city")
    private City city;

    @Field("categories")
    private List<Category> categories;

    @Field("member_count")
    private double member_count;

    @Field("event_count")
    private double event_count;

    @Field("organizer_members")
    private List<User> organizer_members;

    @Field("upcoming_events")
    private List<Event> upcoming_events;

    @Field("group_photo")
    private GroupPhoto group_photo;

    public GroupDTO toDTO() {
        GroupDTO dto = new GroupDTO();

        dto.setGroup_id(this.group_id);
        dto.setDescription(this.description);
        dto.setGroup_name(this.group_name);
        dto.setLink(this.link);
        dto.setTimezone(this.timezone);
        dto.setCreated(this.created);

        if (this.city!=null) {
            dto.setCity(this.city.toDTO());
        }
        dto.setCategories(this.categories);

        dto.setMember_count(this.member_count);
        dto.setEvent_count(this.event_count);

        if (organizer_members!=null) {
            dto.setOrganizer_members(this.organizer_members.stream()
                    .map(User::toDTO).toList());
        }else dto.setOrganizer_members(new ArrayList<>());

        if (this.upcoming_events != null) {
            dto.setUpcoming_events(
                    this.upcoming_events.stream()
                            .map(Event::toDTO)
                            .toList()
            );
        }else dto.setUpcoming_events(new ArrayList<>());

        dto.setGroup_photo(this.group_photo);

        return dto;
    }
}
