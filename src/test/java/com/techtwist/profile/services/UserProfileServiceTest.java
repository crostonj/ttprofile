package com.techtwist.profile.services;

import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileServiceTest {

    private userProfileService userProfileService;

    @BeforeEach
    void setUp() {
        userProfileService = new userProfileService();
        userProfileService.initialize("fake-connection-string", "testTable");
    }

    @Test
    void testInitialize() {
        assertDoesNotThrow(() -> userProfileService.initialize("fake-connection-string", "testTable"));
    }

    @Test
    void testGetProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", "rowKey");
        mockEntity.getProperties().put("firstName", "John");
        mockEntity.getProperties().put("lastName", "Doe");

        userProfileService.createProfile(mockEntity);
        TableEntity result = userProfileService.getProfile("partitionKey", "rowKey");

        assertNotNull(result);
        assertEquals("John", result.getProperties().get("firstName"));
    }

    @Test
    void testListAllProfiles() {
        TableEntity mockEntity1 = new TableEntity("partitionKey1", "rowKey1");
        TableEntity mockEntity2 = new TableEntity("partitionKey2", "rowKey2");

        userProfileService.createProfile(mockEntity1);
        userProfileService.createProfile(mockEntity2);

        List<TableEntity> profiles = userProfileService.listAllProfiles();
        assertEquals(2, profiles.size());
    }

    @Test
    void testCreateProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", "rowKey");
        mockEntity.getProperties().put("firstName", "Jane");

        assertDoesNotThrow(() -> userProfileService.createProfile(mockEntity));
    }

    @Test
    void testUpdateProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", "rowKey");
        mockEntity.getProperties().put("firstName", "Jane");

        userProfileService.createProfile(mockEntity);
        mockEntity.getProperties().put("firstName", "UpdatedName");

        assertDoesNotThrow(() -> userProfileService.updateProfile(mockEntity));
    }

    @Test
    void testMapToUserProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", "rowKey");
        mockEntity.getProperties().put("firstName", "John");

        UserProfile profile = userProfileService.mapToUserProfile(mockEntity);
        assertEquals("John", profile.getFirstName());
    }

    @Test
    void testMapToTableEntity() {
        UserProfile profile = new UserProfile();
        profile.setPartitionKey("partitionKey");
        profile.setRowId("rowKey");
        profile.setFirstName("John");

        Map<String, Object> entityProperties = userProfileService.mapToTableEntity(profile);
        assertEquals("John", entityProperties.get("firstName"));
    }
}
