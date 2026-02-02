package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.GroupCommunityDTO;
import com.example.joinUs.dto.analytics.*;
import com.example.joinUs.mapping.EventMapper;
import com.example.joinUs.mapping.GroupMapper;
import com.example.joinUs.mapping.TopicMapper;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.*;
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
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    TopicMapper topicMapper;

    @Autowired
    EventMapper eventMapper;

    @Autowired
    GroupMapper groupMapper;

    public List<ActivityScorePerUserAnalytic> topUsersByActivityScore(int limit){
        return userRepository.topUsersByActivityScore(limit);
    }

    public List<CityActivityAnalytic> mostActiveCities(int limit){
        return  userRepository.mostActiveCities();
    }

    public List<GroupsPerCityAnalytic> topCitiesByGroupsLast10Years(int limit){
        return groupRepository.topCitiesByGroupsLast10Years(limit);
    }
    public List<TrendingTopicPerCityAnalytic> topTrendingTopicsPerCity(int topicCount){
        return userRepository.topTrendingTopicsPerCity(topicCount);
    }

    public PaidVsFreeEventAnalytic paidVsFreePopularity(){
        return eventRepository.paidVsFreePopularity();
    }
    

    public List<GroupCommunityDTO> findGroupCommunities(int sharedMembers, int limit){

        User user = Utils.getUserFromContext();

        return groupNeo4JRepository.findGroupCommunities(sharedMembers,limit);
    }
}
