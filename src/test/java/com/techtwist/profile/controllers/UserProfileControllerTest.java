package com.techtwist.profile.controllers;

import com.techtwist.profile.helper.UserProfileServiceHelper;
import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.interfaces.IUserProfileService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test") // Activate the "test" profile
@Import(UserProfileServiceHelper.class)
class UserProfileControllerTest {

    @Mock
    @Qualifier("InMemoryUserProfileService") // Explicitly name the mock to match the qualifier
    private IUserProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController; // Use @InjectMocks to initialize the controller

    @Autowired
    private UserProfileServiceHelper userProfileServiceHelper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private String rowKey;
    private String partitionKey = "TestCompany";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        rowKey = UUID.randomUUID().toString();
    }

    @Test
    void testListAllProfiles() throws Exception {
        // Sample data
        UserProfile profile1 = new UserProfile();
        profile1.setPartitionKey("CompanyA");
        profile1.setRowKey("User1");
        profile1.setFirstName("John");
        profile1.setLastName("Doe");

        UserProfile profile2 = new UserProfile();
        profile2.setPartitionKey("CompanyB");
        profile2.setRowKey("User2");
        profile2.setFirstName("Jane");
        profile2.setLastName("Smith");

        List<UserProfile> sampleProfiles = List.of(profile1, profile2);

        // Mock the service to return the sample data
        when(userProfileService.listAllProfiles()).thenReturn(sampleProfiles);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/profile/list"))
            .andExpect(status().isOk());
           // .andExpect(jsonPath("$.length()").value(10)) // Validate the size of the list
           // .andExpect(jsonPath("$[0].rowKey").value("User1")) // Validate first item's rowKey
          //  .andExpect(jsonPath("$[0].firstName").value("John")) // Validate first item's firstName
           // .andExpect(jsonPath("$[0].lastName").value("Doe")) // Validate first item's lastName
          //  .andExpect(jsonPath("$[1].rowKey").value("User2")) // Validate second item's rowKey
          //  .andExpect(jsonPath("$[1].firstName").value("Jane")) // Validate second item's firstName
          //  .andExpect(jsonPath("$[1].lastName").value("Smith")); // Validate second item's lastName
    }

    @Test
    void testCreateProfile() {
        // Create a mock profile
        UserProfile mockProfile = new UserProfile();
        mockProfile.setPartitionKey(partitionKey);
        mockProfile.setRowKey(rowKey);
        mockProfile.setFirstName("John1");
        mockProfile.setLastName("Doe1");
        mockProfile.setUsername("johndoe");

        // Mock the service to return the created profile
        when(userProfileService.createProfile(any())).thenReturn(mockProfile);

        // Call the controller's createProfile method
        ResponseEntity<UserProfile> response = userProfileController.createProfile(mockProfile);

        // Validate the response
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(mockProfile.getPartitionKey(), response.getBody().getPartitionKey());
        assertEquals(mockProfile.getRowKey(), response.getBody().getRowKey());
        assertEquals(mockProfile.getFirstName(), response.getBody().getFirstName());
        assertEquals(mockProfile.getLastName(), response.getBody().getLastName());
        assertEquals(mockProfile.getUsername(), response.getBody().getUsername());
    }

    @Test
    void testUpdateProfile() {
        // Create a mock profile
        UserProfile mockProfile = new UserProfile();
        mockProfile.setPartitionKey(partitionKey);
        mockProfile.setRowKey(rowKey);
        mockProfile.setFirstName("John1");
        mockProfile.setLastName("Doe1");

        // Mock the service to return the updated profile
        when(userProfileService.updateProfile(any())).thenReturn(mockProfile);

        // Call the controller's updateProfile method
        ResponseEntity<UserProfile> response = userProfileController.updateProfile(mockProfile);

        // Validate the response
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(mockProfile.getPartitionKey(), response.getBody().getPartitionKey());
        assertEquals(mockProfile.getRowKey(), response.getBody().getRowKey());
        assertEquals(mockProfile.getFirstName(), response.getBody().getFirstName());
        assertEquals(mockProfile.getLastName(), response.getBody().getLastName());
    }
}
