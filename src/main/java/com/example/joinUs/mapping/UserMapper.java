package com.example.joinUs.mapping;

import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.model.mongodb.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(config = CentralMappingConfig.class, uses = { CityMapper.class, EventMapper.class, TopicMapper.class })
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "isAdmin", ignore = true)
//    @Mapping(target = "roles",ignore = true)
//    @Mapping(target = "authorities",ignore = true)
    User toEntity(UserDTO dto);

    //roles, authorities
}
