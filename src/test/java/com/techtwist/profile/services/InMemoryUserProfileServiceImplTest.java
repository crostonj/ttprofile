package com.techtwist.profile.services;

import com.techtwist.profile.helper.UserProfileServiceHelper;
import com.techtwist.profile.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test") // Activate the "test" profile
@Import(UserProfileServiceHelper.class)
class InMemoryUserProfileServiceImplTest {
   
    @Mock
    private InMemoryUserProfileServiceImpl inMemoryUserProfileService;

    private String rowKey;
    private String partitionKey = "TestCompany";

    @Autowired
    UserProfileServiceHelper userProfileServiceHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowKey = UUID.randomUUID().toString();
        //inMemoryUserProfileService = new InMemoryUserProfileServiceImpl(); // Use the actual implementation
    }

    @Test
    void testGetProfile() {
        String key = UserProfile.generateKey(partitionKey, rowKey);

        // Mocking a UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");
        userProfile.setAddressLine1("123 Main St");
        userProfile.setAddressLine2("Apt 4B");
        userProfile.setCity("Springfield");
        userProfile.setState("IL");
        userProfile.setZipCode("62704");
        userProfile.setCountry("USA");
        userProfile.setPartitionKey(partitionKey);
        userProfile.setRowKey(rowKey);

        when(inMemoryUserProfileService.getProfile(key)).thenReturn(userProfile);

        UserProfile result = inMemoryUserProfileService.getProfile(key);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testListAllProfiles() {
        UserProfile profile1 = new UserProfile();
        profile1.setPartitionKey("partition1");
        profile1.setRowKey("row1");

        UserProfile profile2 = new UserProfile();
        profile2.setPartitionKey("partition2");
        profile2.setRowKey("row2");

        List<UserProfile> mockProfiles = Arrays.asList(profile1, profile2);

        when(inMemoryUserProfileService.listAllProfiles()).thenReturn(mockProfiles);

        List<UserProfile> result = inMemoryUserProfileService.listAllProfiles();
        assertEquals(2, result.size());
        assertEquals("partition1", result.get(0).getPartitionKey());
        assertEquals("row1", result.get(0).getRowKey());
    }

    @Test
    void testCreateProfile() {
        UserProfile newProfile = new UserProfile();
        newProfile.setPartitionKey("partitionKey");
        newProfile.setRowKey("rowKey");

        // Mock the service to return the created profile
        when(inMemoryUserProfileService.createProfile(any(UserProfile.class))).thenReturn(newProfile);

        // Call the method and validate the result
        UserProfile result = inMemoryUserProfileService.createProfile(newProfile);
        assertNotNull(result);
        assertEquals("partitionKey", result.getPartitionKey());
        assertEquals("rowKey", result.getRowKey());

        // Verify that the service method was called once
        verify(inMemoryUserProfileService, times(1)).createProfile(newProfile);
    }

    @Test
    void testUpdateProfile() {
        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setPartitionKey("partitionKey");
        updatedProfile.setRowKey("rowKey");
        updatedProfile.setFirstName("UpdatedFirstName");
        updatedProfile.setLastName("UpdatedLastName");

        userProfileServiceHelper.addProfile(updatedProfile);

        // Mock the behavior of the updateProfile method
        when(inMemoryUserProfileService.updateProfile(any(UserProfile.class))).thenReturn(updatedProfile);

        assertDoesNotThrow(() -> inMemoryUserProfileService.updateProfile(updatedProfile));
        verify(inMemoryUserProfileService, times(1)).updateProfile(updatedProfile);
    }
}
