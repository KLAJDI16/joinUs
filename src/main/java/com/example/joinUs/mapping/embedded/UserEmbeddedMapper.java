package com.example.joinUs.mapping.embedded;

import com.example.joinUs.model.embedded.UserEmbeddedDTO;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.mapping.UserMapper;
import com.example.joinUs.model.mongodb.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(config = CentralMappingConfig.class,uses = {UserMapper.class})
public interface UserEmbeddedMapper {

    UserEmbeddedDTO toDTO(User user);

    List<UserEmbeddedDTO> toDTO(List<User> user);

    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "memberStatus", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "groupCount", ignore = true)
    @Mapping(target = "eventCount", ignore = true)
    @Mapping(target = "upcomingEvents", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isAdmin", ignore = true)
    @Mapping(target = "id", source = "id")
    User toEntity(UserEmbeddedDTO userEmbeddedDTO);
}
