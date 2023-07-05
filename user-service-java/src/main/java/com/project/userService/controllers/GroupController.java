package com.project.userService.controllers;

import com.project.userService.models.Group;
import com.project.userService.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService service;

    @Autowired
    public GroupController(GroupService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllGroups(@RequestParam("title") String title) {
        return ResponseEntity.ok(service.getGroupsByTitle(title));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable("groupId") long groupId) {
        return ResponseEntity.ok(service.getGroupById(groupId));
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        service.createGroup(group);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable("groupId") long groupId,
                                         @RequestBody Group group) {
        group.setId(groupId);
        service.updateGroup(group);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroupById(@PathVariable("groupId") long groupId) {
        service.deleteGroupById(groupId);
        return ResponseEntity.ok().build();
    }
}
