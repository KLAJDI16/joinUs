package com.example.joinUs.config;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.neo4j.driver.GraphDatabase;

@Configuration
public class MainConfiguration {

    @Bean
    public FieldNamingStrategy fieldNamingStrategy() {
        return new SnakeCaseFieldNamingStrategy();
    }

    @Bean
    public MongoMappingContext mongoMappingContext(FieldNamingStrategy fieldNamingStrategy) {
        MongoMappingContext context = new MongoMappingContext();
        context.setFieldNamingStrategy(fieldNamingStrategy);
        return context;
    }



}
