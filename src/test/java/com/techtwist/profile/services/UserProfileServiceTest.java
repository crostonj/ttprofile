package com.techtwist.profile.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.controllers.UserProfileController;
import com.techtwist.profile.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileServiceTest {


    @Mock
    private TableClient tableClient;

    @InjectMocks
    private userProfileService userProfileService;

    private String rowKey;

    @BeforeEach
    void setUp() {

        rowKey = UUID.randomUUID().toString();
    }

    


    @Test
    void testGetProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", rowKey);
        mockEntity.getProperties().put("firstName", "John");
        mockEntity.getProperties().put("lastName", "Doe");

        userProfileService.createProfile(mockEntity);
        TableEntity result = userProfileService.getProfile("partitionKey", rowKey);

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
        TableEntity mockEntity = new TableEntity("partitionKey", rowKey);
        mockEntity.getProperties().put("firstName", "Jane");

        assertDoesNotThrow(() -> userProfileService.createProfile(mockEntity));
    }

    @Test
    void testUpdateProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", rowKey);
        mockEntity.getProperties().put("firstName", "Jane");

        userProfileService.createProfile(mockEntity);
        mockEntity.getProperties().put("firstName", "UpdatedName");

        assertDoesNotThrow(() -> userProfileService.updateProfile(mockEntity));
    }

    @Test
    void testMapToUserProfile() {
        TableEntity mockEntity = new TableEntity("partitionKey", rowKey);
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

        TableEntity entityProperties = userProfileService.mapToTableEntity(profile);
        assertEquals("John", entityProperties.getProperty("firstName"));
    }
}
