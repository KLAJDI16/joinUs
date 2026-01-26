package com.example.joinUs.mapping.embedded;

import com.example.joinUs.dto.TopicDTO;
import com.example.joinUs.dto.embedded.EventEmbeddedDTO;
import com.example.joinUs.dto.embedded.TopicEmbeddedDTO;
import com.example.joinUs.mapping.CentralMappingConfig;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = CentralMappingConfig.class)
public interface TopicEmbeddedMapper {

//    @Mapping(source = "topicId", target = "topicId")
//    @Mapping(source = "topicName", target = "topicName")
//
//    // Ignore the rest
//    @Mapping(target = "link", ignore = true)
//    @Mapping(target = "description", ignore = true)
//    @Mapping(target = "urlkey", ignore = true)
    TopicEmbeddedDTO toDTO(Topic topic);

    List<TopicEmbeddedDTO> toDTOs(List<Topic> topics);

    @Mapping(target = "id", ignore = true)
     @Mapping(target = "link", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "urlkey", ignore = true)
    Topic toEntity(TopicEmbeddedDTO dto);



}
