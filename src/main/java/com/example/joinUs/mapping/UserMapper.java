package com.example.joinUs.mapping;

import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.model.mongodb.User;
import org.mapstruct.Mapper;


@Mapper(config = CentralMappingConfig.class, uses = { CityMapper.class, EventMapper.class, TopicMapper.class })
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO dto);

}
