package com.example.joinUs.dto;

import com.example.joinUs.dto.embedded.EventEmbeddedDTO;
import com.example.joinUs.dto.embedded.TopicEmbeddedDTO;
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
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private String memberId;
    private String memberName;
    private String password; // TODO discuss

    private CityDTO city;

    private String memberStatus;
    private String bio;

    private List<TopicEmbeddedDTO> topics;

    private Integer eventCount;
    private Integer groupCount;

    private List<EventEmbeddedDTO> upcomingEvents;


}
