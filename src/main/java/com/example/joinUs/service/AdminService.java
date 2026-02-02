package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.GroupCommunityDTO;
import com.example.joinUs.mapping.EventMapper;
import com.example.joinUs.mapping.GroupMapper;
import com.example.joinUs.mapping.TopicMapper;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.EventNeo4JRepository;
import com.example.joinUs.repository.GroupNeo4JRepository;
import com.example.joinUs.repository.TopicNeo4JRepository;
import com.example.joinUs.repository.UserNeo4JRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AdminService {
    @Autowired
    private EventNeo4JRepository eventNeo4JRepository;
    @Autowired
    private GroupNeo4JRepository groupNeo4JRepository;
    @Autowired
    private UserNeo4JRepository userNeo4JRepository;
    @Autowired
    private TopicNeo4JRepository topicNeo4JRepository;

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    EventMapper eventMapper;

    @Autowired
    GroupMapper groupMapper;




    public List<GroupCommunityDTO> findGroupCommunities(int sharedMembers, int limit){

        User user = Utils.getUserFromContext();

        return groupNeo4JRepository.findGroupCommunities(sharedMembers,limit);
    }
}
