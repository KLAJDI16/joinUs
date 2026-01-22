package com.example.joinUs.service;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
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
    public GroupDTO findById(String id) throws ApplicationException {
        Group group = groupRepository.findGroupById(id);
        if (group != null) return group.toDTO();
        else {
            throw new ApplicationException("Could not find that group");
        }
    }
}

