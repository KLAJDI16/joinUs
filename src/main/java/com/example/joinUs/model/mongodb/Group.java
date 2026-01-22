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

//    @Id
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
    private Double member_count;

    @Field("event_count")
    private Double event_count;

    @Field("organizer_members")
    private List<User> organizer_members;

    @Field("upcoming_events")
    private List<Event> upcoming_events;

    @Field("group_photo")
    private GroupPhoto group_photo;

    public GroupDTO toDTO() {
        if (this ==null) return new GroupDTO();
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

    public static Group fromDTO(GroupDTO dto) {
        if (dto == null) return null;

        Group group = new Group();

        group.setGroup_id(dto.getGroup_id());
        group.setDescription(dto.getDescription());
        group.setGroup_name(dto.getGroup_name());
        group.setLink(dto.getLink());
        group.setTimezone(dto.getTimezone());
        group.setCreated(dto.getCreated());

        if (dto.getCity() != null) {
            group.setCity(City.fromDTO(dto.getCity()));
        }

        group.setCategories(dto.getCategories() != null ? dto.getCategories() : new ArrayList<>());
        group.setMember_count(dto.getMember_count());
        group.setEvent_count(dto.getEvent_count());

        if (dto.getOrganizer_members() != null) {
            group.setOrganizer_members(
                    dto.getOrganizer_members().stream()
                            .map(User::fromDTO)
                            .toList()
            );
        } else {
            group.setOrganizer_members(new ArrayList<>());
        }

        if (dto.getUpcoming_events() != null) {
            group.setUpcoming_events(
                    dto.getUpcoming_events().stream()
                            .map(Event::fromDTO)
                            .toList()
            );
        } else {
            group.setUpcoming_events(new ArrayList<>());
        }

        group.setGroup_photo(dto.getGroup_photo());

        return group;
    }

}
