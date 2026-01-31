package com.example.joinUs.mapping.summary;


import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.mapping.FeeMapper;
import com.example.joinUs.mapping.VenueMapper;
import com.example.joinUs.mapping.embedded.GroupEmbeddedMapper;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.neo4j.Event_Neo4J;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Mapper(config = CentralMappingConfig.class, uses = {
         VenueMapper.class, FeeMapper.class,
        GroupEmbeddedMapper.class})
public interface EventSummaryMapper {
    @Mapping(source = "fee.amount", target = "feeAmount")
    @Mapping(source = "venue.city.name", target = "venueCityName")
    @Mapping(source = "id", target = "id")
//    @Mapping(source = "creatorGroup.id", target = "creatorGroup.groupId")
//    @Mapping(source = "creatorGroup.groupName", target = "creatorGroup.groupName")
    EventSummaryDTO toDTO(Event event);

    @Mapping(source = "fee.amount", target = "feeAmount")
    @Mapping(source = "venue.city.name", target = "venueCityName")
    @Mapping(source = "id", target = "id")
//    @Mapping(source = "creatorGroup.id", target = "creatorGroup.groupId")
//    @Mapping(source = "creatorGroup.groupName", target = "creatorGroup.groupName")
    List<EventSummaryDTO> toDTOs(List<Event> events);


    @Mapping(source = "feeAmount", target = "feeAmount")
    @Mapping(source = "venueCity", target = "venueCityName")
    @Mapping(source = "eventId", target = "id")
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "creatorGroup", ignore = true)
    EventSummaryDTO toDTOFromNeo4j(Event_Neo4J event);

    @Mapping(source = "feeAmount", target = "feeAmount")
    @Mapping(source = "venueCity", target = "venueCityName")
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "creatorGroup", ignore = true)
    @Mapping(source = "eventId", target = "id")

    List<EventSummaryDTO> toDTOsFromNeo4j(List<Event_Neo4J> events);

    default Date map(OffsetDateTime value) {
        return value == null ? null : Date.from(value.toInstant());
    }
//    default GroupEmbedded map(Group group) {
//        if (group == null) return null;
//        return GroupEmbedded.builder()
//                .groupId(group.getId())
//                .groupName(group.getGroupName())
//                .build();
//    }

}
