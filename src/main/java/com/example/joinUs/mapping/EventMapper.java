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
//    @Mapping(source = "creatorGroup.id", target = "creatorGroup.groupId")
//    @Mapping(source = "creatorGroup.groupName", target = "creatorGroup.groupName")
    @Mapping(source = "creatorGroup", target = "creatorGroup")
    EventDTO toDTO(Event event);

    // Ignore Mongo internal id because the DTO doesn't carry it.
    @Mapping(target = "id", source = "id")
    @Mapping(source = "creatorGroup", target = "creatorGroup")
//    @Mapping(source = "creatorGroup.groupId", target = "creatorGroup.id")
//    @Mapping(source = "creatorGroup.groupName", target = "creatorGroup.groupName")
    Event toEntity(EventDTO dto);
//Unmapped target properties: "description, link, timezone, created, city, categories,
// groupPhoto, memberCount, eventCount, organizers, upcomingEvents".
// Mapping from property "GroupEmbedded creatorGroup" to "Group creatorGroup".
    default Date map(OffsetDateTime value) {
        return value == null ? null : Date.from(value.toInstant());
    }
//    default GroupEmbedded map(Group group) {
//        if (group == null) return null;
//        return GroupEmbedded.builder()
//                .id(group.getId())
//                .groupName(group.getGroupName())
//                .build();
//    }
//    default Group map(GroupEmbedded groupEmbeddedDTO) {
//        if (groupEmbeddedDTO == null) return null;
//        return Group.builder()
//                .id(groupEmbeddedDTO.getId())
//                .groupName(groupEmbeddedDTO.getGroupName())
//                .build();
//    }

}