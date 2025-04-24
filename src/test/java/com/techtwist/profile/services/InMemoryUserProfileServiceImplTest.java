package com.techtwist.profile.services;

import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.interfaces.IUserProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryUserProfileServiceImplTest {
    @Qualifier("InMemoryUserProfileService") // Explicitly name the mock to match the qualifier
    private IUserProfileService userProfileService;

    private String rowKey;
    private String partitionKey = "TestCompany";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rowKey = UUID.randomUUID().toString();
        userProfileService = Mockito.mock(IUserProfileService.class);
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

        when(userProfileService.getProfile(key)).thenReturn(userProfile);

        UserProfile result = userProfileService.getProfile(key);
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

        when(userProfileService.listAllProfiles()).thenReturn(mockProfiles);

        List<UserProfile> result = userProfileService.listAllProfiles();
        assertEquals(2, result.size());
        assertEquals("partition1", result.get(0).getPartitionKey());
        assertEquals("row1", result.get(0).getRowKey());
    }

    @Test
    void testCreateProfile() {
        UserProfile newProfile = new UserProfile();
        newProfile.setPartitionKey("partitionKey");
        newProfile.setRowKey("rowKey");

        doNothing().when(userProfileService).createProfile(newProfile);

        assertDoesNotThrow(() -> userProfileService.createProfile(newProfile));
        verify(userProfileService, times(1)).createProfile(newProfile);
    }

    @Test
    void testUpdateProfile() {
        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setPartitionKey("partitionKey");
        updatedProfile.setRowKey("rowKey");

        doNothing().when(userProfileService).updateProfile(updatedProfile);

        assertDoesNotThrow(() -> userProfileService.updateProfile(updatedProfile));
        verify(userProfileService, times(1)).updateProfile(updatedProfile);
    }
}
