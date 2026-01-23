package com.example.joinUs.controller;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // READ: list all groups
    @GetMapping
    public List<GroupDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    // READ: get one group by groupId
    @GetMapping("/{id}")
    public GroupDTO getGroupById(@PathVariable String id) {
        return groupService.getGroupById(id);
    }

    // CREATE: create a new group
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    // UPDATE (partial): update an existing group
    @PatchMapping("/{id}")
    public GroupDTO updateGroup(@PathVariable String id, @RequestBody GroupDTO groupDTO) {
        return groupService.updateGroup(id, groupDTO);
    }

    // DELETE: delete a group
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable String id) {
        groupService.deleteGroup(id);
    }
}
