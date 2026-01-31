package com.example.joinUs.dto.summary;


import com.example.joinUs.dto.CategoryDTO;
import com.example.joinUs.model.embedded.EventEmbedded;
import com.example.joinUs.model.embedded.UserEmbeddedDTO;
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
    private List<UserEmbeddedDTO> organizers;
    private List<EventEmbedded> upcomingEvents;
    private Integer memberCount;
    private Integer eventCount;
}
