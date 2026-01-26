package com.example.joinUs.mapping;

//import com.example.joinUs.dto.EventDTO;
//import com.example.joinUs.model.mongodb.Event;

//package com.example.joinUs.dto;
import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.mapping.embedded.GroupEmbeddedMapper;
import com.example.joinUs.mapping.embedded.TopicEmbeddedMapper;
import com.example.joinUs.model.mongodb.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(config = CentralMappingConfig.class, uses = {
        CategoryMapper.class, GroupMapper.class
        , VenueMapper.class, TopicEmbeddedMapper.class}
        // TODO
)
public interface EventMapper {

    EventDTO toDTO(Event event);

    List<EventDTO> toDTOs(List<Event> events);

    // Ignore Mongo internal id because the DTO doesn't carry it.
    @Mapping(target = "id", ignore = true)
    Event toEntity(EventDTO dto);

//    Unmapped target properties: "id, link, description, urlkey".
//    Mapping from Collection element "TopicEmbeddedDTO
//    creatorGroup.organizerMembers[].topics" to "Topic creatorGroup.organizerMembers[].topics".
}