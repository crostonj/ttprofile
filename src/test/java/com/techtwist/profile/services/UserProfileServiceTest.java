package com.techtwist.profile.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.azure.core.http.rest.PagedIterable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private TableClient tableClient;

    @InjectMocks
    private UserProfileService userProfileService; // Fixed naming and initialization

    private String rowKey;
    private String partitionKey = "TestCompany";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Ensure mocks are initialized
        rowKey = UUID.randomUUID().toString();
        userProfileService = new UserProfileService(); // Assign the mocked TableClient
    }

    private UserProfile createMockUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setPartitionKey(partitionKey);
        userProfile.setRowId(rowKey);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        return userProfile;
    }

    @Test
    void testGetProfile() {
        TableEntity mockEntity = new TableEntity(partitionKey, rowKey);
        mockEntity.getProperties().put("firstName", "John");
        mockEntity.getProperties().put("lastName", "Doe");

        // Mock the behavior of tableClient to return the mockEntity
        when(tableClient.getEntity(partitionKey, rowKey)).thenReturn(mockEntity);

        TableEntity result = userProfileService.getProfile(partitionKey, rowKey);

        assertNotNull(result);
        assertEquals("John", result.getProperties().get("firstName"));
        assertEquals("Doe", result.getProperties().get("lastName"));
    }

 

    @Test
    void testCreateProfile() {
        TableEntity mockEntity = new TableEntity(partitionKey, rowKey);
        mockEntity.getProperties().put("firstName", "Jane");

        assertDoesNotThrow(() -> userProfileService.createProfile(mockEntity));
    }

    @Test
    void testUpdateProfile() {
        TableEntity mockEntity = new TableEntity(partitionKey, rowKey);
        mockEntity.getProperties().put("firstName", "Jane");

        userProfileService.createProfile(mockEntity);
        mockEntity.getProperties().put("firstName", "UpdatedName");

        assertDoesNotThrow(() -> userProfileService.updateProfile(mockEntity));
    }

    @Test
    void testMapToUserProfile() {
        TableEntity mockEntity = new TableEntity(partitionKey, rowKey);
        mockEntity.getProperties().put("firstName", "John");
        mockEntity.getProperties().put("lastName", "Doe");
        mockEntity.getProperties().put("rowId", rowKey); // Add rowId property
        mockEntity.getProperties().put("partitionKey", partitionKey); // Add partitionKey property

        UserProfile profile = userProfileService.mapToUserProfile(mockEntity);
        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getLastName());
        assertEquals(rowKey, profile.getRowId()); // Add assertion for rowId
        assertEquals(partitionKey, profile.getPartitionKey()); // Add assertion for partitionKey
    }

    @Test
    void testMapToTableEntity() {
        UserProfile profile = new UserProfile();
        profile.setPartitionKey(partitionKey);
        profile.setRowId("rowKey");
        profile.setFirstName("John");

        TableEntity entityProperties = userProfileService.mapToTableEntity(profile);
        assertEquals("John", entityProperties.getProperty("firstName"));
    }
}
