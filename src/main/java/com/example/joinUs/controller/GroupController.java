package com.example.joinUs.controller;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("")
    public List<GroupDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public GroupDTO getGroupById(@PathVariable String id) {
        return groupService.getGroupById(id);
    }

    //
    //    // Example: create a new group
    //    @PostMapping("")
    //    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO) {
    //        return groupService.createGroup(groupDTO);
    //    }
}

