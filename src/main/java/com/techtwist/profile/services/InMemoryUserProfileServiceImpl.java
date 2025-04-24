package com.techtwist.profile.services;

import com.techtwist.profile.models.UserProfile;

import com.techtwist.profile.services.interfaces.IUserProfileService;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("InMemoryUserProfileService")
public class InMemoryUserProfileServiceImpl implements IUserProfileService {


    private static final int PARTITION_KEY = 0;
    private static final int  ROW_KEY = 1;

    private final Map<String, UserProfile> inMemoryStore = new ConcurrentHashMap<>();

    public InMemoryProductService() {
        initialize();
    }

    @Override
    public UserProfile getProfile(String key) {
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
    }

    @Override
    public List<UserProfile> listAllProfiles() {
        return new ArrayList<>(inMemoryStore.values());
    }

    @Override
    public void createProfile(UserProfile entity) {
        String key =  UserProfile.generateKey(entity.getPartitionKey(), entity.getRowKey());
        if (inMemoryStore.containsKey(key)) {
            throw new IllegalArgumentException("Profile already exists with the given partitionKey and rowKey.");
        }
        inMemoryStore.put(key, entity);
    }

    @Override
    public void updateProfile(UserProfile entity) {
        String key = UserProfile.generateKey(entity.getPartitionKey(), entity.getRowKey());
        if (!inMemoryStore.containsKey(key)) {
            throw new IllegalArgumentException("Profile does not exist with the given partitionKey and rowKey.");
        }
        inMemoryStore.put(key, entity);
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
            String rowKey = "user" + i;
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
    }

    @Override
    public boolean deleteProfile(String key) {
        if (key == null || !key.contains(":")) {
            throw new IllegalArgumentException("Invalid key format. Expected format: 'partitionKey:rowKey'");
        }
        if (!inMemoryStore.containsKey(key)) {
            throw new IllegalArgumentException("Profile does not exist with the given key.");
        }
        inMemoryStore.remove(key);
        return true;
    }

    public Map<String, UserProfile> getUserProfileStore() {
        return inMemoryStore; // Expose internal store for testing
    }
}
