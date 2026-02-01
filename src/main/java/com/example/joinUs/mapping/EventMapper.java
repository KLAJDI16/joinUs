package com.example.joinUs.mapping;

//import com.example.joinUs.dto.EventDTO;
//import com.example.joinUs.model.mongodb.Event;

//package com.example.joinUs.dto;
import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.mapping.embedded.GroupEmbeddedMapper;
import com.example.joinUs.mapping.embedded.TopicEmbeddedMapper;
import com.example.joinUs.model.mongodb.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.util.Date;


@Mapper(config = CentralMappingConfig.class, uses = {
        CategoryMapper.class
        , GroupEmbeddedMapper.class
        , VenueMapper.class, TopicEmbeddedMapper.class}
        // TODO
)
public interface EventMapper {

    @Mapping(target = "id", source = "id")
    EventDTO toDTO(Event event);

    @Mapping(target = "id", source = "id")
    Event toEntity(EventDTO dto);
    default Date map(OffsetDateTime value) {
        return value == null ? null : Date.from(value.toInstant());
    }

}