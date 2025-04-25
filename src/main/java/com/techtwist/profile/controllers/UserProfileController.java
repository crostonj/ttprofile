package com.techtwist.profile.controllers;

import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.interfaces.IUserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "User Profiles", description = "API for managing user profiles")
@RestController
@CrossOrigin(origins = "*") // Allow all origins or specify multiple origins as needed
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    @Qualifier("InMemoryUserProfileService") // Specify the desired implementation
    private IUserProfileService userProfileService;

    @Operation(summary = "Get a user profile by name", description = "Retrieve a user profile by first name and last name")
    @GetMapping("/name")
    public ResponseEntity<UserProfile> getProfileByName(
            @RequestParam String username) {
        try {
            UserProfile profile = userProfileService.getProfileByName(username);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @Operation(summary = "Get a user profile", description = "Retrieve a user profile by partition key and row key")
    @GetMapping("/{partitionKey}/{rowKey}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String partitionKey, @PathVariable String rowKey) {
        try {
            String key = generateKey(partitionKey, rowKey);
            UserProfile entity = userProfileService.getProfile(key);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "List all user profiles", description = "Retrieve a list of all user profiles")
    @GetMapping("/list")
    public ResponseEntity<List<UserProfile>> listAllProfiles() {
        try {
            // Ensure the service returns TableEntity objects
            List<UserProfile> profiles = userProfileService.listAllProfiles();
 
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Create a new user profile", description = "Create a new user profile with the provided details")
    @RequestBody(description = "User profile details", required = true) 
    @PostMapping
    public ResponseEntity<String> createProfile(@RequestBody UserProfile profile) {
        try {
           
            userProfileService.createProfile(profile);
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
 
            userProfileService.updateProfile(profile);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile: " + e.getMessage());
        }
    }

    // Add this method to the UserProfileController class
    /**
     * Utility method to generate a key by combining partition key and row key.
     *
     * @param partitionKey The partition key.
     * @param rowKey The row key.
     * @return A combined key in the format "partitionKey:rowKey".
     */
    private String generateKey(String partitionKey, String rowKey) {
        return partitionKey + ":" + rowKey;
    }
}
