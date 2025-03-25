package com.techtwist.profile.controllers;

import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.userProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserProfile> getProfile(@PathVariable String partitionKey, @PathVariable String rowKey) {
        try {
            TableEntity entity = userProfileService.getProfile(partitionKey, rowKey);
            return ResponseEntity.ok(mapToUserProfile(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // List all profiles
    @GetMapping
    public ResponseEntity<List<UserProfile>> listAllProfiles() {
        try {
            // Ensure the service returns TableEntity objects
            List<TableEntity> tableEntities = userProfileService.listAllProfiles();
            // Convert TableEntity objects to UserProfile objects
            List<UserProfile> userProfiles = mapToUserProfiles(tableEntities);
            return ResponseEntity.ok(userProfiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Create a new profile
    @PostMapping
    public ResponseEntity<String> createProfile(@RequestBody UserProfile profile) {
        try {
            TableEntity entity = mapToTableEntity(profile);
            userProfileService.createProfile(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create profile: " + e.getMessage());
        }
    }

    // Update an existing profile
    @PutMapping
    public ResponseEntity<String> updateProfile(@RequestBody UserProfile profile) {
        try {
            TableEntity entity = mapToTableEntity(profile);
            userProfileService.updateProfile(entity);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile: " + e.getMessage());
        }
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

    // Utility method to map a list of TableEntity to a list of UserProfile
    private List<UserProfile> mapToUserProfiles(List<TableEntity> entities) {
        return entities.stream()
                       .map(this::mapToUserProfile)
                       .collect(Collectors.toList());
    }
}
