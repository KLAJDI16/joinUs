package com.example.joinUs.repository;

import com.example.joinUs.model.mongodb.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {


}
