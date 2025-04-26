package com.techtwist.profile.services.interfaces;

import com.techtwist.profile.models.UserProfile;
import java.util.List;


/**
 * Interface for User Profile Service.
 * This interface defines the methods for managing user profiles.
 */
public interface IUserProfileService {
    void initialize();

    UserProfile getProfile(String Key);
    List<UserProfile> listAllProfiles();
    UserProfile createProfile(UserProfile profile);
    UserProfile updateProfile(UserProfile profile);
    boolean deleteProfile(String key);
    UserProfile getProfileByName(String username);
    
}
