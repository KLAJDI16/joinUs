package com.example.joinUs.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "topics")
public class Topic {

    @Id
    private String topicId;
    private String topicName;
    private String link;
    private String description;
    private String urlkey;

}
