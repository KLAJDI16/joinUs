package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.Topic;
import com.example.joinUs.model.mongodb.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TopicRepository extends MongoRepository<Topic,String> {
}
