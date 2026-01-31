package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.dto.ResponseMessage;
import com.example.joinUs.dto.summary.GroupSummaryDTO;
import com.example.joinUs.mapping.*;
import com.example.joinUs.mapping.embedded.UserEmbeddedMapper;
import com.example.joinUs.mapping.summary.GroupSummaryMapper;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.model.mongodb.User;
import com.example.joinUs.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CityService cityService;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupSummaryMapper groupSummaryMapper;

    @Autowired
    private UserEmbeddedMapper userEmbeddedMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GroupPhotoMapper groupPhotoMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public ResponseMessage joinGroup(String id){
        Group group = geGroupOrThrow(id);
        try {

            User user = Utils.getUserFromContext();

            user.setGroupCount(user.getGroupCount() + 1);
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(id)),
                    new Update().inc( "member_count", 1),
                    Group.class
            );// This is atomic  operation per document ,so it is thread safe if 2 users join the same group simultaneously

            userService.saveUser(user);
            return new ResponseMessage("successful","Your attendance in the group "+group.getGroupName()+" is confirmed");
            //TODO complete the part for the Neo4J too
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ResponseMessage leaveGroup(String id){
        Group group = geGroupOrThrow(id);
        try {

            User user = Utils.getUserFromContext();

            user.setGroupCount(user.getGroupCount() - 1);
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(id)),
                    new Update().inc( "member_count", -1),
                    Group.class
            );// This is atomic  operation per document ,so it is thread safe if 2 users join the same group simultaneously

            userService.saveUser(user);
            return new ResponseMessage("successful","You left the group "+group.getGroupName()+" ");
            //TODO complete the part for the Neo4J too
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ResponseMessage addOrganizer(String groupId,String organizerId){
        Group group = geGroupOrThrow(groupId);

        if(group.getOrganizerMembers()
                .stream()
                .anyMatch(u -> u.getId().equals(organizerId))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User is  already an organizer at this group "+groupId);
        }

        userService.checkUserHasPermissionToEditGroup(groupId);
        User user = userService.findUserById(organizerId).orElseThrow(
                () ->    new ResponseStatusException(NOT_FOUND,"No user exists with id "+organizerId)
        );
        group.getOrganizerMembers().add(userEmbeddedMapper.toEntity(userEmbeddedMapper.toDTO(user)));
        groupRepository.save(group);
    return new ResponseMessage("successful","Organizer with id"+organizerId+" was added successfully");
    }



    public Page<GroupSummaryDTO> getAllGroups(int page, int size) {
        Page<Group> groups = groupRepository.findAll(PageRequest.of(page, size ));
        return groups.map(e -> groupSummaryMapper.toDTO(e));
    }

    public GroupDTO getGroupById(String id) {
        Group group = geGroupOrThrow(id);
        return groupMapper.toDTO(group);
    }
    public PageImpl<GroupSummaryDTO> search(
            String name,
            String city,
            String category,
            Integer minMembers,
            Integer maxMembers,
            Integer minEvents,
            Integer maxEvents,
            int page,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Query query = new Query();

        if (name != null)
            query.addCriteria(Criteria.where("group_name").regex(name, "i"));

        if (city != null)
            query.addCriteria(Criteria.where("city.name").regex(city,"i"));

        if (minMembers != null)
            query.addCriteria(Criteria.where("member_count").gte(minMembers));

        if (maxMembers != null)
            query.addCriteria(Criteria.where("member_count").lte(maxMembers));

        if (minEvents != null)
            query.addCriteria(Criteria.where("event_count").gte(minEvents));

        if (maxEvents != null)
            query.addCriteria(Criteria.where("event_count").lte(maxEvents));


        if (category != null)
            query.addCriteria(Criteria.where("categories.name").is(category));

        query.with(pageable);


        List<GroupSummaryDTO> results = mongoTemplate.find(query, Group.class)
                .stream()
                .map(e -> groupSummaryMapper.toDTO(e))
                .toList();

        return new PageImpl<GroupSummaryDTO>(results, pageable, -1);
//        return results;

    }

    public GroupDTO createGroup(GroupDTO groupDTO)  {

        User user = Utils.getUserFromContext();

        // Minimal approach: require groupId to be present and unique
        if (Utils.isNullOrEmpty(groupDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must be provided");
        }
        if (groupRepository.existsById(groupDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group already exists: " + groupDTO.getId());
        }
        groupDTO.setCreated(new Date());
        groupDTO.setUpcomingEvents(new ArrayList<>());
        groupDTO.setMemberCount(1);
        groupDTO.setEventCount(0);
        groupDTO.getOrganizerMembers().add(userEmbeddedMapper.toDTO(user));
        cityService.parseCityDTO(groupDTO.getCity());
        Group entity = groupMapper.toEntity(groupDTO);
        Group saved = groupRepository.save(entity);
        return groupMapper.toDTO(saved);
    }

    public GroupDTO updateGroup(String id, GroupDTO groupDTO) { // TODO revisit, especially PATCH semantics

        Group existing =geGroupOrThrow(id);

        userService.checkUserHasPermissionToEditGroup(id);
        // Minimal patch semantics:
        // - map incoming DTO to entity and copy non-null fields onto existing
        if (Utils.isNullOrEmpty(groupDTO.getGroupName())) groupDTO.setGroupName(existing.getGroupName());
        if (Utils.isNullOrEmpty(groupDTO.getLink())) groupDTO.setLink(existing.getLink());
        if (Utils.isNullOrEmpty(groupDTO.getGroupPhoto())) groupDTO.setGroupPhoto(groupPhotoMapper.toDTO(existing.getGroupPhoto()));
        if (Utils.isNullOrEmpty(groupDTO.getDescription())) groupDTO.setDescription(existing.getDescription());
        if (Utils.isNullOrEmpty(groupDTO.getTimezone())) groupDTO.setTimezone(existing.getTimezone());
        if (Utils.isNullOrEmpty(groupDTO.getCategories())) groupDTO.setCategories(categoryMapper.toDTOs(existing.getCategories()));

        if (Utils.isNullOrEmpty(groupDTO.getCity())){
            groupDTO.setCity(cityMapper.toDTO(existing.getCity()));
        } else cityService.parseCityDTO(groupDTO.getCity());

        // If you want "replace" semantics, use PUT and overwrite everything instead.
        Group patch = groupMapper.toEntity(groupDTO);

        // Ensure the identifier stays consistent ,and other fields
        patch.setId(existing.getId());
        patch.setEventCount(existing.getEventCount());
        patch.setMemberCount(existing.getMemberCount());
        patch.setCreated(existing.getCreated());
        patch.setUpcomingEvents(existing.getUpcomingEvents());
        patch.setOrganizerMembers(existing.getOrganizerMembers());


        // Preserve Mongo internal id if you use it
//        patch.setGroupId(existing.getId());

        // If your mapper doesn't handle "merge", then do a simple overwrite:
        // (Minimal) overwrite existing with patch entity:
        Group saved = groupRepository.save(patch);

        return groupMapper.toDTO(saved);
    }

    public void deleteGroup(String id) {
        geGroupOrThrow(id);
        userService.checkUserHasPermissionToEditGroup(id);

        groupRepository.deleteById(id);
    }

    private Group geGroupOrThrow(String id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Event not found: " + id));
    }


}
