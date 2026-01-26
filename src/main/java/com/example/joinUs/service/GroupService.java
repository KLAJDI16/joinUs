package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.mapping.GroupMapper;
import com.example.joinUs.model.mongodb.Group;
import com.example.joinUs.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;


    @Autowired
    private UserService userService;

    @Autowired
    private GroupMapper groupMapper;

    public List<GroupDTO> getAllGroups() {
        return groupMapper.toDTOs(groupRepository.findAll());
    }

    public GroupDTO getGroupById(String id) {
        Group group = groupRepository.findByGroupId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id));
        return groupMapper.toDTO(group);
    }

    public GroupDTO createGroup(GroupDTO groupDTO)  {
        // Minimal approach: require groupId to be present and unique
        if (Utils.isNullOrEmpty(groupDTO.getGroupId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "groupId must be provided");
        }
        if (groupRepository.existsByGroupId(groupDTO.getGroupId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group already exists: " + groupDTO.getGroupId());
        }

        Group entity = groupMapper.toEntity(groupDTO);
        Group saved = groupRepository.save(entity);
        return groupMapper.toDTO(saved);
    }

    public GroupDTO updateGroup(String id, GroupDTO groupDTO) { // TODO revisit, especially PATCH semantics
        Group existing = groupRepository.findByGroupId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id));

        userService.checkUserHasPermissionToEditGroup(id);
        // Minimal patch semantics:
        // - map incoming DTO to entity and copy non-null fields onto existing
        // If you want "replace" semantics, use PUT and overwrite everything instead.
        Group patch = groupMapper.toEntity(groupDTO);

        // Ensure the identifier stays consistent
        patch.setGroupId(existing.getGroupId());

        // Preserve Mongo internal id if you use it
        patch.setId(existing.getId());

        // If your mapper doesn't handle "merge", then do a simple overwrite:
        // (Minimal) overwrite existing with patch entity:
        Group saved = groupRepository.save(patch);

        return groupMapper.toDTO(saved);
    }

    public void deleteGroup(String id) {
        if (!groupRepository.existsByGroupId(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id);
        }
        userService.checkUserHasPermissionToEditGroup(id);

        groupRepository.deleteByGroupId(id);
    }

    public List<GroupDTO> findAllByAggregation(int offset, int limit){

        long startTime=System.currentTimeMillis();

        List<GroupDTO> page  = groupRepository.findAllByAggregation(offset,limit);
        long endTime=System.currentTimeMillis();

        System.out.println("Process by Aggregation takes "+(endTime-startTime)+" milliseconds");
        return page;
    }

}
