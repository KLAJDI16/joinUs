package com.example.joinUs.mapping.embedded;

import com.example.joinUs.dto.embedded.EventEmbeddedDTO;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.model.mongodb.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMappingConfig.class,uses = {GroupEmbeddedMapper.class})
public interface EventEmbeddedMapper {


    EventEmbeddedDTO toDTO(Event event);


        @Mapping(target = "description", ignore = true)
    @Mapping(target = "eventUrl", ignore = true)
    @Mapping(target = "eventStatus", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "utcOffset", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "creatorGroup", ignore = true)
    @Mapping(target = "id", ignore = true)
    Event toEntity(EventEmbeddedDTO eventEmbeddedDTO);

//    @Mapping(source = "eventId", target = "eventId")
//    @Mapping(source = "eventName", target = "eventName")
//    @Mapping(source = "eventTime", target = "eventTime")
//    List<EventEmbeddedDTO> toDTOs(List<Event> events);

}
