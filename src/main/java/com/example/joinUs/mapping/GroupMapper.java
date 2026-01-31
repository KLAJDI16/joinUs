package com.example.joinUs.mapping;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.mapping.embedded.EventEmbeddedMapper;
import com.example.joinUs.mapping.embedded.UserEmbeddedMapper;
import com.example.joinUs.model.mongodb.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;


@Mapper(config = CentralMappingConfig.class,
        uses = { CityMapper.class, CategoryMapper.class, EventEmbeddedMapper.class,
                GroupPhotoMapper.class, UserEmbeddedMapper.class },
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL // TODO others also
)
public interface GroupMapper {

//    @Mapping(target = "groupId",source = "id")
//    @Mapping(source = "id", ignore = true)
    GroupDTO toDTO(Group group);

//    @Mapping(target = "groupId",source = "id")
//@Mapping(target = "id", ignore = true)
List<GroupDTO> toDTOs(List<Group> groups);

    // Ignore Mongo internal id because the DTO doesn't carry it.
//    @Mapping(target = "id",source = "groupId")
//    @Mapping(target = "id", ignore = true)
    Group toEntity(GroupDTO dto);



    //Unmapped target properties: "id, city, memberStatus, bio, topics,
    // eventCount, groupCount, upcomingEvents, password, isAdmin".
    // Mapping from Collection element
    // "UserEmbeddedDTO organizerMembers" to "User organizerMembers".
}