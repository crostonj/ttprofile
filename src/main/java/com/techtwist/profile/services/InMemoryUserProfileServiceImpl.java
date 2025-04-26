package com.techtwist.profile.services;

import com.techtwist.profile.models.UserProfile;

import com.techtwist.profile.services.interfaces.IUserProfileService;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("InMemoryUserProfileService")
@Primary 
public class InMemoryUserProfileServiceImpl implements IUserProfileService {


    private static final int PARTITION_KEY = 0;
    private static final int  ROW_KEY = 1;

    private final Map<String, UserProfile> inMemoryStore = new ConcurrentHashMap<>();

    public InMemoryUserProfileServiceImpl() {

        // Skip initialization if the active profile is "test"
        String activeProfile = System.getProperty("spring.profiles.active", "");
        if ("test".equalsIgnoreCase(activeProfile)) {
            return;
        }

        initialize();
    }

    @Override
    public UserProfile getProfile(String key) {
        try {
            if (key == null || !key.contains(":")) {
                throw new IllegalArgumentException("Invalid key format. Expected format: 'partitionKey:rowKey'");
            }
            String[] keys = key.split(":", 2);
            String partitionKey = keys[PARTITION_KEY];
            String rowKey = keys[ROW_KEY];
            if (partitionKey == null || rowKey == null) {
                throw new IllegalArgumentException("Invalid key format. Expected format: 'partitionKey:rowKey'");
            }
            if (!inMemoryStore.containsKey(key)) {
                throw new IllegalArgumentException("Profile does not exist with the given partitionKey and rowKey.");
            }

            return inMemoryStore.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null or handle the exception as needed
        }
    }

    @Override
    public List<UserProfile> listAllProfiles() {
        try {
            inMemoryStore.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> inMemoryStore.put(entry.getKey(), entry.getValue()));

            return new ArrayList<>(inMemoryStore.values());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list in case of an error
        }
    }

    @Override
    public UserProfile createProfile(UserProfile entity) {
        try {
            String key = UserProfile.generateKey(entity.getPartitionKey(), entity.getRowKey());
            if (inMemoryStore.containsKey(key)) {
                return inMemoryStore.get(key); // Return existing profile if it already exists
            }
            UserProfile profile = inMemoryStore.put(key, entity);
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null or handle the exception as needed
        }
    }

    @Override
    public UserProfile updateProfile(UserProfile entity) {
        try {
            String key = UserProfile.generateKey(entity.getPartitionKey(), entity.getRowKey());
            if (!inMemoryStore.containsKey(key)) {
                throw new IllegalArgumentException("Profile does not exist with the given partitionKey and rowKey.");
            }
            UserProfile profile = inMemoryStore.put(key, entity);
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null or handle the exception as needed
        }
    }

    public List<UserProfile> getAllUsers() {
        List<UserProfile> users = new ArrayList<>();
        return users;
    }

    @Override
    public UserProfile getProfileByName(String username) {
        return inMemoryStore.values().stream()
                .filter(profile -> profile.getUsername().equalsIgnoreCase(username))            
                .findFirst()
                .orElse(null);
    }

    @Override
    public void initialize() {
        for (int i = 1; i <= 10; i++) {
            String partitionKey = "userPartition";
            String rowKey = "User" + i;
            UserProfile entity = new UserProfile();
            entity.setPartitionKey(partitionKey);
            entity.setRowKey(rowKey);
            entity.setFirstName("John" + i);
            entity.setLastName("Doe" + i);
            entity.setEmail("john.doe" + i + "@example.com");
            entity.setAddressLine1("123 Main St " + i);
            entity.setAddressLine2("Apt 4B " + i);
            entity.setCity("Springfield " + i);
            entity.setState("IL");
            entity.setZipCode("62704");
            entity.setCountry("USA");
            entity.setId(i);
            entity.setUsername("User" + i);

            createProfile(entity);
        }

        //order the map by partitionKey and rowKey
     
    }

    @Override
    public boolean deleteProfile(String key) {
        try {
            if (key == null || !key.contains(":")) {
                throw new IllegalArgumentException("Invalid key format. Expected format: 'partitionKey:rowKey'");
            }
            if (!inMemoryStore.containsKey(key)) {
                throw new IllegalArgumentException("Profile does not exist with the given key.");
            }
            inMemoryStore.remove(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false or handle the exception as needed
        }
    }

    public Map<String, UserProfile> getUserProfileStore() {
        return inMemoryStore; // Expose internal store for testing
    }
}
