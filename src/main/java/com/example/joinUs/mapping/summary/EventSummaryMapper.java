package com.example.joinUs.mapping.summary;


import com.example.joinUs.dto.FeeDTO;
import com.example.joinUs.dto.embedded.EventEmbeddedDTO;
import com.example.joinUs.dto.embedded.GroupEmbeddedDTO;
import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.mapping.CategoryMapper;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.mapping.FeeMapper;
import com.example.joinUs.mapping.VenueMapper;
import com.example.joinUs.mapping.embedded.GroupEmbeddedMapper;
import com.example.joinUs.model.mongodb.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;
import java.util.List;

@Mapper(config = CentralMappingConfig.class, uses = {
         VenueMapper.class, FeeMapper.class, GroupEmbeddedMapper.class})
public interface EventSummaryMapper { //TODO fix venueCityName and feeAmount because they are returned as null in the JSON

    @Mapping(source = "fee.amount", target = "feeAmount")
    @Mapping(source = "venue.city.name", target = "venueCityName")
    EventSummaryDTO toDTO(Event event);


    @Mapping(source = "fee.amount", target = "feeAmount")
    @Mapping(source = "venue.city.name", target = "venueCityName")
    List<EventSummaryDTO> toDTOs(List<Event> events);

}
