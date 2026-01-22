package com.example.joinUs.controller;

import com.example.joinUs.dto.GroupDTO;
import com.example.joinUs.exceptions.ApplicationException;
import com.example.joinUs.service.GroupService;
import com.example.joinUs.service.UserService;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

//    @GetMapping("")
//    public List<GroupDTO> getAllGroups() {
//
//      System.out.println(SecurityContextHolder.getContext().getAuthentication());
//        return groupService.getAllGroups();
//    }

    @GetMapping("")
    public JsonObject getAllGroups() {
         JsonObject jsonObject = new JsonObject("{\"result\":\"Successfully hit GET /groups \"}");
         return jsonObject;
    }
    @PostMapping("")
    public JsonObject createGroup() {
        JsonObject jsonObject = new JsonObject("{\"result\":\"Successfully hit POST /groups \"}");
        return jsonObject;
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity editGroup(@PathVariable String id)  {

        try {
            if (userService.userHasPermissionToEditGroup(id)!=null)
            { return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(userService.userHasPermissionToEditGroup(id));}
            else {
            return ResponseEntity.ok(groupService.findById(id));
            }
        } catch (ApplicationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


//    // Example: get group by id
//    @GetMapping("/{id}")
//    public GroupDTO getGroupById(@PathVariable String id) {
//        return groupService.getGroupById(id);
//    }
//
//    // Example: create a new group
//    @PostMapping("")
//    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO) {
//        return groupService.createGroup(groupDTO);
//    }
}

