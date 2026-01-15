package com.example.joinUs.configuration;

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import script_transform_csv_to_mongodb_and_neo4j.ConfigurationFileReader;

@Configuration
public class MainConfiguration {
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create(ConfigurationFileReader.getProperty("spring.data.mongodb.uri")), ConfigurationFileReader.getProperty("spring.data.mongodb.database"));
    }
}
