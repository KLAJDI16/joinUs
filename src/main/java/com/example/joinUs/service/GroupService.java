package com.example.joinUs.service;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(e -> e.toDTO())
                .toList();
    }
}

