package com.techtwist.profile.controllers;

import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.interfaces.IUserProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @Mock
    @Qualifier("InMemoryUserProfileService") // Explicitly name the mock to match the qualifier
    private IUserProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;
    
    private String rowKey;
    private String partitionKey = "TestCompany";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowKey = UUID.randomUUID().toString();
    }

    @Test
    void testListAllProfiles() {
        when(userProfileService.listAllProfiles()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserProfile>> response = userProfileController.listAllProfiles();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testCreateProfile() {
        UserProfile mockProfile = new UserProfile();
        mockProfile.setPartitionKey(partitionKey);
        mockProfile.setRowKey(rowKey);

        doNothing().when(userProfileService).createProfile(any());

        ResponseEntity<String> response = userProfileController.createProfile(mockProfile);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Profile created successfully.", response.getBody());
    }

    @Test
    void testUpdateProfile() {
        UserProfile mockProfile = new UserProfile();
        mockProfile.setPartitionKey(partitionKey);
        mockProfile.setRowKey(rowKey);

        doNothing().when(userProfileService).updateProfile(any());

        ResponseEntity<String> response = userProfileController.updateProfile(mockProfile);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Profile updated successfully.", response.getBody());
    }
}
