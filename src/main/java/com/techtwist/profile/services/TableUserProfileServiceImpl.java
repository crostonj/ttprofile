package com.techtwist.profile.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;
import com.techtwist.profile.services.interfaces.IUserProfileService;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TableUserProfileServiceImpl implements IUserProfileService {
    private TableClient tableClient;

    @Value("${azure.storage.account-name:}")
    private String accountName;
    
    @Value("${azure.storage.account-key:}")
    private String accountKey;
    
    @Value("${azure.storage.table-name:}")
    private String tableName;

    private static final int PARTITION_KEY = 0;
    private static final int  ROW_KEY = 1;

    @Override
    @PostConstruct
    public void initialize() {
        if (accountName.isEmpty() || accountKey.isEmpty() || tableName.isEmpty()) {
            // Skip initialization if any credential is missing
            return;
        }
        
        if (tableClient == null) {
            tableClient = new TableClientBuilder()
                    .connectionString(String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;TableEndpoint=https://%s.table.core.windows.net;", 
                        accountName, accountKey, accountName))
                    .tableName(tableName)
                    .buildClient();
        }
    }

    private String[] splitKey(String key) {
        if (key == null || !key.contains(":")) {
            throw new IllegalArgumentException("Invalid key format. Expected format: 'partitionKey:rowKey'");
        }
        return key.split(":", 2); // Split into two parts: partitionKey and rowKey
    }

    // Fetch profile information by partition key and row key
    @Override
    public UserProfile getProfile(String key) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        String[] keys = splitKey(key);
        TableEntity entity = tableClient.getEntity(keys[PARTITION_KEY], keys[ROW_KEY]); // Use keys[0] as PARTITION_KEY and keys[1] as ROW_KEY
        if (entity.getProperties() == null || entity.getProperties().isEmpty()) {
            throw new RuntimeException("Retrieved entity has no properties.");
        }
        return mapToUserProfile(entity);
    }

    // Fetch all profiles from the table
    public List<UserProfile> listAllProfiles() {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        List<UserProfile> profiles = new ArrayList<>();
        tableClient.listEntities().forEach(entity -> {
            UserProfile profile = mapToUserProfile(entity);
            profiles.add(profile);
        });
        return profiles;
    }

    // Create a new profile
    public UserProfile createProfile(UserProfile profile) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        try {
            TableEntity entity = mapToTableEntity(profile);
            tableClient.createEntity(entity);
            return mapToUserProfile(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create profile: " + e.getMessage(), e);
        }
    }

    // Update an existing profile
    public UserProfile updateProfile(UserProfile profile) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        try {
            TableEntity entity = mapToTableEntity(profile);
            tableClient.updateEntity(entity);
            return mapToUserProfile(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteProfile(String key) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        try {
            String[] keys = splitKey(key);
            tableClient.deleteEntity(keys[PARTITION_KEY], keys[ROW_KEY]); // Use keys[0] as PARTITION_KEY and keys[1] as ROW_KEY
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete profile: " + e.getMessage(), e);
        }
    }


    public boolean profileExists(String key) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        try {
            String[] keys = splitKey(key);

        tableClient.getEntity(keys[PARTITION_KEY], keys[ROW_KEY]); // Use keys[0] as PARTITION_KEY and keys[1] as ROW_KEY
            return true;
        } catch (Exception e) {
            return false; // Return false if the entity does not exist
        }
    }

    public UserProfile mapToUserProfile(TableEntity entity) {
        UserProfile userProfile = new UserProfile();
        Map<String, Object> properties = entity.getProperties();

        userProfile.setPartitionKey(entity.getPartitionKey());
        userProfile.setRowKey(entity.getRowKey());
        userProfile.setFirstName(properties.getOrDefault("firstName", "").toString());
        userProfile.setLastName(properties.getOrDefault("lastName", "").toString());
        userProfile.setEmail(properties.getOrDefault("email", "").toString());
        userProfile.setAddressLine1(properties.getOrDefault("addressLine1", "").toString());
        userProfile.setAddressLine2(properties.getOrDefault("addressLine2", "").toString());
        userProfile.setCity(properties.getOrDefault("city", "").toString());
        userProfile.setState(properties.getOrDefault("state", "").toString());
        userProfile.setZipCode(properties.getOrDefault("zipCode", "").toString());
        userProfile.setCountry(properties.getOrDefault("country", "").toString());
        userProfile.setProperties(properties);
        return userProfile;
    }

    public TableEntity mapToTableEntity(UserProfile profile) {
        TableEntity entity = new TableEntity(profile.getPartitionKey(), profile.getRowKey());
        entity.getProperties().put("firstName", profile.getFirstName());
        entity.getProperties().put("lastName", profile.getLastName());
        entity.getProperties().put("email", profile.getEmail());
        entity.getProperties().put("addressLine1", profile.getAddressLine1());
        entity.getProperties().put("addressLine2", profile.getAddressLine2());
        entity.getProperties().put("city", profile.getCity());
        entity.getProperties().put("state", profile.getState());
        entity.getProperties().put("zipCode", profile.getZipCode());
        entity.getProperties().put("country", profile.getCountry());
        if (profile.getProperties() != null) {
            entity.getProperties().putAll(profile.getProperties());
        }
        return entity;
    }

    @Override
    public UserProfile getProfileByName(String username) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized");
        }
        
        return tableClient.listEntities()
            .stream()
            .map(this::mapToUserProfile)
            .filter(profile -> username.equals(profile.getFirstName()))
            .findFirst()
            .orElse(null);
    }
}
