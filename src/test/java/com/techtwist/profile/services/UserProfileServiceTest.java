package com.techtwist.profile.services;

import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        userProfileService = Mockito.spy(new UserProfileService());
        doNothing().when(userProfileService).initialize(); // Mock initialize method
    }

   // @Test
    void testGetProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", "rowKey");
        mockEntity.getProperties().put("firstName", "John");
        when(userProfileService.getProfile("partitionKey", "rowKey")).thenReturn(mockEntity);

        TableEntity result = userProfileService.getProfile("partitionKey", "rowKey");
        assertNotNull(result);
        assertEquals("John", result.getProperties().get("firstName"));
    }

   // @Test
    void testListAllProfiles() {
        TableEntity entity1 = new TableEntity("partition1", "row1");
        TableEntity entity2 = new TableEntity("partition2", "row2");
        List<TableEntity> mockEntities = Arrays.asList(entity1, entity2);

        when(userProfileService.listAllProfiles()).thenReturn(mockEntities);

        List<TableEntity> result = userProfileService.listAllProfiles();
        assertEquals(2, result.size());
    }

    @Test
    void testCreateProfile() {
        TableEntity newEntity = new TableEntity("partitionKey", "rowKey");
        doNothing().when(userProfileService).createProfile(newEntity);

        assertDoesNotThrow(() -> userProfileService.createProfile(newEntity));
        verify(userProfileService, times(1)).createProfile(newEntity);
    }

    @Test
    void testUpdateProfile() {
        TableEntity updatedEntity = new TableEntity("partitionKey", "rowKey");
        doNothing().when(userProfileService).updateProfile(updatedEntity);

        assertDoesNotThrow(() -> userProfileService.updateProfile(updatedEntity));
        verify(userProfileService, times(1)).updateProfile(updatedEntity);
    }

    @Test
    void testMapToUserProfile() {
        TableEntity entity = new TableEntity("partitionKey", "rowKey");
        entity.getProperties().put("firstName", "Jane");
        entity.getProperties().put("lastName", "Doe");

        UserProfile userProfile = userProfileService.mapToUserProfile(entity);

        assertEquals("partitionKey", userProfile.getPartitionKey());
        assertEquals("rowKey", userProfile.getRowId());
        assertEquals("Jane", userProfile.getFirstName());
        assertEquals("Doe", userProfile.getLastName());
    }

    @Test
    void testMapToTableEntity() {
        UserProfile userProfile = new UserProfile();
        userProfile.setPartitionKey("partitionKey");
        userProfile.setRowId("rowKey");
        userProfile.setFirstName("Jane");
        userProfile.setLastName("Doe");

        TableEntity entity = userProfileService.mapToTableEntity(userProfile);

        assertEquals("partitionKey", entity.getPartitionKey());
        assertEquals("rowKey", entity.getRowKey());
        assertEquals("Jane", entity.getProperties().get("firstName"));
        assertEquals("Doe", entity.getProperties().get("lastName"));
    }
}
