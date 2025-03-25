package com.techtwist.profile.controllers;

import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.userProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    @Autowired
    private userProfileService userProfileService;

    // Initialize the service
    @PostMapping("/initialize")
    public void initialize(@RequestParam String connectionString, @RequestParam String tableName) {
        userProfileService.initialize(connectionString, tableName);
    }

    // Get a profile by partition key and row key
    @GetMapping("/{partitionKey}/{rowKey}")
    public UserProfile getProfile(@PathVariable String partitionKey, @PathVariable String rowKey) {
        TableEntity entity = userProfileService.getProfile(partitionKey, rowKey);
        return mapToUserProfile(entity);
    }

    // List all profiles
    @GetMapping
    public List<TableEntity> listAllProfiles() {
        return userProfileService.listAllProfiles();
    }

    // Create a new profile
    @PostMapping
    public void createProfile(@RequestBody UserProfile profile) {
        TableEntity entity = mapToTableEntity(profile);
        userProfileService.createProfile(entity);
    }

    // Update an existing profile
    @PutMapping
    public void updateProfile(@RequestBody UserProfile profile) {
        TableEntity entity = mapToTableEntity(profile);
        userProfileService.updateProfile(entity);
    }

    // Utility method to map TableEntity to UserProfile
    private UserProfile mapToUserProfile(TableEntity entity) {
        UserProfile profile = new UserProfile();
        profile.setPartitionKey(entity.getPartitionKey());
        profile.setRowId(entity.getRowKey());
        profile.setProperties(entity.getProperties());
        return profile;
    }

    // Utility method to map UserProfile to TableEntity
    private TableEntity mapToTableEntity(UserProfile profile) {
        TableEntity entity = new TableEntity(profile.getPartitionKey(), profile.getRowId());
        if (profile.getProperties() != null) {
            entity.getProperties().putAll(profile.getProperties());
        }
        return entity;
    }
}
