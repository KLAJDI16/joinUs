package com.example.joinUs.mapping.embedded;

import com.example.joinUs.model.embedded.TopicEmbedded;
import com.example.joinUs.mapping.CentralMappingConfig;
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
    TopicEmbedded toDTO(Topic topic);

    List<TopicEmbedded> toDTOs(List<Topic> topics);

//    @Mapping(target = "id", ignore = true)
     @Mapping(target = "link", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "urlkey", ignore = true)
    Topic toEntity(TopicEmbedded dto);

}
