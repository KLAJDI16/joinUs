package com.example.joinUs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private String memberId;
    private String memberName;

    private CityDTO city;

    private String memberStatus;
    private String bio;

    private List<TopicDTO> topics;

    private Integer eventCount;
    private Integer groupCount;

    private List<EventDTO> upcomingEvents;

}
