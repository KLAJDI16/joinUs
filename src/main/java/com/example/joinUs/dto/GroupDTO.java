package com.example.joinUs.dto;

import com.example.joinUs.model.mongodb.Category;
import com.example.joinUs.model.mongodb.City;
import com.example.joinUs.model.mongodb.GroupPhoto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO {

    private String group_id;
    private String description;
    private String group_name;
    private String link;
    private String timezone;
    private Date created;

    private CityDTO city;
    private List<Category> categories;

    private double member_count;
    private double event_count;

    private List<UserDTO> organizer_members;
    private List<EventDTO> upcoming_events;

    private GroupPhoto group_photo;
}

