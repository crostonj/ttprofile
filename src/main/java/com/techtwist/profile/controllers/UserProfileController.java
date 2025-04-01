package com.techtwist.profile.controllers;

import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "User Profiles", description = "API for managing user profiles")
@RestController
@CrossOrigin(origins = "*") // Allow all origins or specify multiple origins as needed
@RequestMapping("/userprofiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Operation(summary = "Get a user profile", description = "Retrieve a user profile by partition key and row key")
    @GetMapping("/{partitionKey}/{rowKey}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String partitionKey, @PathVariable String rowKey) {
        try {
            TableEntity entity = userProfileService.getProfile(partitionKey, rowKey);
            return ResponseEntity.ok(userProfileService.mapToUserProfile(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "List all user profiles", description = "Retrieve a list of all user profiles")
    @GetMapping("/list")
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

    @Operation(summary = "Create a new user profile", description = "Create a new user profile with the provided details")
    @RequestBody(description = "User profile details", required = true) 
    @PostMapping
    public ResponseEntity<String> createProfile(@RequestBody UserProfile profile) {
        try {
            TableEntity entity = userProfileService.mapToTableEntity(profile);
            userProfileService.createProfile(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create profile: " + e.getMessage());
        }
    }

    @Operation(summary = "Update an existing user profile", description = "Update an existing user profile with the provided details")
    @RequestBody(description = "Updated user profile details", required = true)
    @PutMapping
    public ResponseEntity<String> updateProfile(@RequestBody UserProfile profile) {
        try {
            TableEntity entity = userProfileService.mapToTableEntity(profile);
            userProfileService.updateProfile(entity);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile: " + e.getMessage());
        }
    }

    // Utility method to map a list of TableEntity to a list of UserProfile
    private List<UserProfile> mapToUserProfiles(List<TableEntity> entities) {
        return entities.stream()
                       .map(userProfileService::mapToUserProfile)
                       .collect(Collectors.toList());
    }
}
