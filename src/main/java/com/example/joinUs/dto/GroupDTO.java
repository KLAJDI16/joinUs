package com.example.joinUs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO {

    private String groupId;
    private String description;
    private String groupName;
    private String link;
    private String timezone;
    private Date created;

    private CityDTO city;
    private List<CategoryDTO> categories;

    private Integer memberCount;
    private Integer eventCount;

    private List<UserDTO> organizerMembers; //TODO create UserEmbeddedDTO and replace here ?

    private List<EventDTO> upcomingEvents;

    private GroupPhotoDTO groupPhoto;

}

