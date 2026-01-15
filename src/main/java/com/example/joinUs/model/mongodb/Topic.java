package com.example.joinUs.model.mongodb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
//@Document(collection = "topic")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {

    @Id
    private String topic_id;
    private String topic_name;
    private String link;
    private String description;
    private String urlkey;

}
