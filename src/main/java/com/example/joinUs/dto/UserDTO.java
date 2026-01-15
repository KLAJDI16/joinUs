package com.example.joinUs.dto;

import com.example.joinUs.model.mongodb.Topic;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private String member_id;
    private String member_name;

    private CityDTO city;

    private String member_status;
    private String bio;

    private List<Topic> topics;

    private double event_count;
    private double group_count;

    private List<EventDTO> upcoming_events;
}
