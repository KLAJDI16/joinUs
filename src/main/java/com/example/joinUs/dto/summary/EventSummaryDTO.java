package com.example.joinUs.dto.summary;


import com.example.joinUs.dto.CategoryDTO;
import com.example.joinUs.dto.FeeDTO;
import com.example.joinUs.dto.VenueDTO;
import com.example.joinUs.dto.embedded.GroupEmbeddedDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

//        return "{'event_id':1,'event_name':1,'venue.city.name':1,
//        'member_count':1,'creator_group':1,'event_time':1}";
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventSummaryDTO { //TODO if used needs to be fixed cause venueCityName and feeAmount show as null

    private String eventId;
    private String eventName;
    private Date eventTime;
    private Date updated;
    private Integer memberCount;
    private Integer feeAmount;
    private String venueCityName;
    private GroupEmbeddedDTO creatorGroup;
}
