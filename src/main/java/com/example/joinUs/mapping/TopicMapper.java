package com.example.joinUs.mapping;

import com.example.joinUs.dto.TopicDTO;
import com.example.joinUs.model.mongodb.Topic;
import org.mapstruct.Mapper;


@Mapper(config = CentralMappingConfig.class)
public interface TopicMapper {

    TopicDTO toDTO(Topic topic);

    Topic toEntity(TopicDTO dto);

}
