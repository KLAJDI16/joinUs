package com.example.joinUs.controller;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.service.GroupService;
import com.example.joinUs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

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
    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO)  {
        return groupService.createGroup(groupDTO);
    }

    // UPDATE (partial): update an existing group
    @PatchMapping("/{id}")
    public ResponseEntity updateGroup(@PathVariable String id, @RequestBody GroupDTO groupDTO) {

        return ResponseEntity.ok(groupService.updateGroup(id, groupDTO));
    }

    // DELETE: delete a group
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable String id) {
        groupService.deleteGroup(id);
    }
}
