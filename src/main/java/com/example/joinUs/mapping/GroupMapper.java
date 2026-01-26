package com.example.joinUs.mapping;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.model.mongodb.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;


@Mapper(config = CentralMappingConfig.class,
        uses = { CityMapper.class, CategoryMapper.class, EventMapper.class,
                GroupPhotoMapper.class, UserMapper.class },
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT // TODO others also

)
public interface GroupMapper {

    GroupDTO toDTO(Group group);

    List<GroupDTO> toDTOs(List<Group> groups);

    // Ignore Mongo internal id because the DTO doesn't carry it.
    @Mapping(target = "id", ignore = true)
    Group toEntity(GroupDTO dto);

}