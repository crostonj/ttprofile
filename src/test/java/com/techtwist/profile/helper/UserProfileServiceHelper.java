package com.techtwist.profile.helper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.InMemoryUserProfileServiceImpl;

public class UserProfileServiceHelper {
        @Autowired
    private InMemoryUserProfileServiceImpl inMemoryUserProfileService;

    public void addProfile(UserProfile userProfile) {
        inMemoryUserProfileService.createProfile(userProfile);
    }

    public Map<String, UserProfile> getUserProfileStoreStore() {
        return inMemoryUserProfileService.getUserProfileStore(); // Expose internal store for testing
    }

}
