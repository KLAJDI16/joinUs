package com.example.joinUs.mapping;

import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.mapping.embedded.EventEmbeddedMapper;
import com.example.joinUs.mapping.embedded.TopicEmbeddedMapper;
import com.example.joinUs.model.mongodb.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(config = CentralMappingConfig.class, uses = { CityMapper.class,
        EventEmbeddedMapper.class, TopicEmbeddedMapper.class })
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    UserDTO toDTO(User user);

//    @Mapping(target = "roles",ignore = true)
//    @Mapping(target = "authorities",ignore = true)
//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAdmin",ignore = true)
    @Mapping(target = "id", source = "id")
    User toEntity(UserDTO dto);

    //roles, authorities
}
