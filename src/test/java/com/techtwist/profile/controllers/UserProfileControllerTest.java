package com.techtwist.profile.controllers;

import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.userProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @Mock
    private userProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitialize() {
        assertDoesNotThrow(() -> userProfileController.initialize("fake-connection-string", "testTable"));
        verify(userProfileService, times(1)).initialize("fake-connection-string", "testTable");
    }

    @Test
    void testGetProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", "rowKey");

        when(userProfileService.getProfile("partitionKey", "rowKey")).thenReturn(mockEntity);

        ResponseEntity<UserProfile> response = userProfileController.getProfile("partitionKey", "rowKey");
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("partitionKey", response.getBody().getPartitionKey());
    }

    @Test
    void testListAllProfiles() {
        when(userProfileService.listAllProfiles()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserProfile>> response = userProfileController.listAllProfiles();
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testCreateProfile() {
        UserProfile mockProfile = new UserProfile();
        mockProfile.setPartitionKey("partitionKey");
        mockProfile.setRowId("rowKey");

        doNothing().when(userProfileService).createProfile(any());

        ResponseEntity<String> response = userProfileController.createProfile(mockProfile);
        assertEquals(201, response.getStatusCode());
        assertEquals("Profile created successfully.", response.getBody());
    }

    @Test
    void testUpdateProfile() {
        UserProfile mockProfile = new UserProfile();
        mockProfile.setPartitionKey("partitionKey");
        mockProfile.setRowId("rowKey");

        doNothing().when(userProfileService).updateProfile(any());

        ResponseEntity<String> response = userProfileController.updateProfile(mockProfile);
        assertEquals(200, response.getStatusCode());
        assertEquals("Profile updated successfully.", response.getBody());
    }
}
