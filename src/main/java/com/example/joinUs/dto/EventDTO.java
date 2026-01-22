package com.example.joinUs.dto;

import com.example.joinUs.model.mongodb.Category;
import com.example.joinUs.model.mongodb.Venue;
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

    private String event_id;
    private String event_name;
    private String event_url;
    private String description;
    private String event_status;

    private Date created;
    private Date event_time;
    private Date updated;

    private Long duration;
    private Long utc_offset;

    private Double member_count;

    private List<Category> categories;
    private GroupDTO creator_group;
    private Venue venue;
}
