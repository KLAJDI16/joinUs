package com.example.joinUs.dto.summary;


import com.example.joinUs.dto.CategoryDTO;
import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.dto.embedded.EventEmbeddedDTO;
import com.example.joinUs.dto.embedded.UserEmbeddedDTO;
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
public class GroupSummaryDTO {
    private String id;
    private String groupName;
    private List<CategoryDTO> categories;
    private String cityName;
    private List<UserEmbeddedDTO> organizerMembers;
    private List<EventEmbeddedDTO> upcomingEvents;
    private Integer memberCount;
    private Integer eventCount;
}
