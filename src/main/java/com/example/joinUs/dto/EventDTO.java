package com.example.joinUs.dto;

import com.example.joinUs.dto.embedded.GroupEmbeddedDTO;
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
public class EventDTO {

    private String eventId;
    private String eventName;
    private String eventUrl;
    private String description;
    private String eventStatus;

    private Date created;
    private Date eventTime;
    private Date updated;

    private Integer duration;
    private Integer utcOffset;

    private FeeDTO fee;
    private VenueDTO venue;
    private List<CategoryDTO> categories;

    private Integer memberCount;

    private GroupDTO creatorGroup;



}
