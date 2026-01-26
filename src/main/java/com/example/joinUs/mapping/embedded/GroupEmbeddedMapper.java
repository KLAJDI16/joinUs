package com.example.joinUs.mapping.embedded;

import com.example.joinUs.dto.embedded.GroupEmbeddedDTO;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.mapping.UserMapper;
import com.example.joinUs.model.mongodb.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMappingConfig.class,uses = {UserMapper.class})
public interface GroupEmbeddedMapper {


//    @Mapping(source = "groupId", target = "groupId")
//    @Mapping(source = "groupName", target = "groupName")
//    // Explicitly ignored fields
//    @Mapping(target = "link", ignore = true)
//    @Mapping(target = "timezone", ignore = true)
//    @Mapping(target = "created", ignore = true)
//    @Mapping(target = "city", ignore = true)
//    @Mapping(target = "categories", ignore = true)
//    @Mapping(target = "memberCount", ignore = true)
//    @Mapping(target = "eventCount", ignore = true)
    GroupEmbeddedDTO toDTO(Group group);


    @Mapping(target = "link", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "eventCount", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "groupPhoto", ignore = true)
    @Mapping(target = "organizerMembers", ignore = true)
    @Mapping(target = "upcomingEvents", ignore = true)
    @Mapping(target = "id", ignore = true)
    Group toEntity(GroupEmbeddedDTO groupEmbeddedDTO);


}
