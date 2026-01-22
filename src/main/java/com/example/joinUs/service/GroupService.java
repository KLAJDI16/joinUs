package com.example.joinUs.service;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.mapping.GroupMapper;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMapper groupMapper;

    public GroupDTO getGroupById(String id) {
        Group group = groupRepository.findGroupById(id);
        return groupMapper.toDTO(group);
    }

    public List<GroupDTO> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groupMapper.toDTOs(groups);
    }
}
