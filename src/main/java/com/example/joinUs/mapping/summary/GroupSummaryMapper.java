package com.example.joinUs.mapping.summary;

import com.example.joinUs.dto.CategoryDTO;
import com.example.joinUs.dto.embedded.UserEmbeddedDTO;
import com.example.joinUs.dto.summary.EventSummaryDTO;
import com.example.joinUs.dto.summary.GroupSummaryDTO;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.mapping.FeeMapper;
import com.example.joinUs.mapping.UserMapper;
import com.example.joinUs.mapping.VenueMapper;
import com.example.joinUs.mapping.embedded.EventEmbeddedMapper;
import com.example.joinUs.mapping.embedded.GroupEmbeddedMapper;
import com.example.joinUs.mapping.embedded.UserEmbeddedMapper;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.neo4j.Event_Neo4J;
import com.example.joinUs.model.neo4j.Group_Neo4J;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Mapper(config = CentralMappingConfig.class, uses = {
        UserEmbeddedMapper.class, EventEmbeddedMapper.class, GroupEmbeddedMapper.class,
        UserMapper.class})

public interface GroupSummaryMapper {

    @Mapping(source = "city.name", target = "cityName")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "organizerMembers",target = "organizerMembers")
    GroupSummaryDTO toDTO(Group group);
//groupId
    @Mapping(source = "city.name", target = "cityName")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "organizerMembers",target = "organizerMembers")
    List<GroupSummaryDTO> toDTOs(List<Group> group);


    @Mapping(source = "groupId", target = "id")
    @Mapping(source = "groupName", target = "groupName")
    @Mapping(source = "cityName", target = "cityName")

    @Mapping(source = "organizerId", target = "organizerMembers")
    @Mapping(source = "categoryName", target = "categories")

    @Mapping(target = "upcomingEvents", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "eventCount", ignore = true)
    GroupSummaryDTO toDTOFromNeo4j(Group_Neo4J group);

    List<GroupSummaryDTO> toDTOsFromNeo4j(List<Group_Neo4J> groupNeo4JS);

    default List<UserEmbeddedDTO> mapOrganizer(String organizerId) {
        if (organizerId == null) return null;

        return List.of(
                UserEmbeddedDTO.builder()
                        .id(organizerId)
                        .build()
        );
    }

    default List<CategoryDTO> mapCategory(String categoryName) {
        if (categoryName == null) return null;

        return List.of(
                CategoryDTO.builder()
                        .name(categoryName)
                        .build()
        );
    }

}
