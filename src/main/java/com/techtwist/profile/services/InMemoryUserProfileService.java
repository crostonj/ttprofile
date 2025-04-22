package com.techtwist.profile.services;

import com.techtwist.profile.models.UserProfile;

import com.techtwist.profile.services.interfaces.IUserProfileService;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("inMemoryUserProfileService")
public class InMemoryUserProfileService implements IUserProfileService {


    private static final int PARTITION_KEY = 0;
    private static final int  ROW_KEY = 1;

    private final Map<String, UserProfile> inMemoryStore = new ConcurrentHashMap<>();

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
    public void initialize() {
        for (int i = 1; i <= 10; i++) {
            String partitionKey = "userPartition";
            String rowKey = "user" + i;
            UserProfile entity = new UserProfile();
            entity.getProperties().put("name", "User " + i);
            entity.getProperties().put("email", "user" + i + "@example.com");
            entity.getProperties().put("age", 20 + i);
            entity.getProperties().put("address", "Address " + i);
            entity.getProperties().put("city", "City " + i);
            entity.getProperties().put("state", "State " + i);
            entity.getProperties().put("zipCode", "Zip " + i);
            entity.getProperties().put("country", "Country " + i);
            entity.setRowKey(rowKey);
            entity.setPartitionKey(partitionKey);
            inMemoryStore.put( UserProfile.generateKey(partitionKey, rowKey), entity);
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
}
