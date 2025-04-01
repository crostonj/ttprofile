package com.techtwist.profile.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import com.techtwist.profile.models.UserProfile;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private TableClient tableClient;

    private final String accountName = System.getenv("ACCOUNTNAME");
    private final String accountKey = System.getenv("ACCOUNTKEY");
    private final String tableName = System.getenv("TABLENAME");

    @PostConstruct
    public void init() {
        tableClient = new TableClientBuilder()
                .connectionString(String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;TableEndpoint=https://%s.table.core.windows.net;", accountName, accountKey, accountName))
                .tableName(tableName)
                .buildClient();
    }

    // Fetch profile information by partition key and row key
    public TableEntity getProfile(String partitionKey, String rowKey) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        TableEntity entity = tableClient.getEntity(partitionKey, rowKey);
        if (entity.getProperties() == null || entity.getProperties().isEmpty()) {
            throw new RuntimeException("Retrieved entity has no properties.");
        }
        return entity;
    }

    // Fetch all profiles from the table
    public List<TableEntity> listAllProfiles() {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        List<TableEntity> profiles = new ArrayList<>();
        tableClient.listEntities().forEach(profiles::add);
        return profiles;
    }

    // Create a new profile
    public void createProfile(TableEntity profile) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        try {
            tableClient.createEntity(profile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create profile: " + e.getMessage(), e);
        }
    }

    // Update an existing profile
    public void updateProfile(TableEntity profile) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        try {
            tableClient.updateEntity(profile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile: " + e.getMessage(), e);
        }
    }

    public UserProfile mapToUserProfile(TableEntity entity) {
        try {
            UserProfile profile = new UserProfile();
            profile.setPartitionKey(entity.getPartitionKey());
            profile.setRowId(entity.getRowKey());
            profile.setFirstName(entity.getProperties().get("firstName") != null ? entity.getProperties().get("firstName").toString() : null);
            profile.setLastName(entity.getProperties().get("lastName") != null ? entity.getProperties().get("lastName").toString() : null);
            profile.setEmail(entity.getProperties().get("email") != null ? entity.getProperties().get("email").toString() : null);
            profile.setAddressLine1(entity.getProperties().get("addressLine1") != null ? entity.getProperties().get("addressLine1").toString() : null);
            profile.setAddressLine2(entity.getProperties().get("addressLine2") != null ? entity.getProperties().get("addressLine2").toString() : null);
            profile.setCity(entity.getProperties().get("city") != null ? entity.getProperties().get("city").toString() : null);
            profile.setState(entity.getProperties().get("state") != null ? entity.getProperties().get("state").toString() : null);
            profile.setZipCode(entity.getProperties().get("zipCode") != null ? entity.getProperties().get("zipCode").toString() : null);
            profile.setCountry(entity.getProperties().get("country") != null ? entity.getProperties().get("country").toString() : null);
            profile.setProperties(entity.getProperties());
            return profile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map TableEntity to UserProfile: " + e.getMessage(), e);
        }
    }

    public TableEntity mapToTableEntity(UserProfile profile) {
        TableEntity entity = new TableEntity(profile.getPartitionKey(), profile.getRowId());
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
}
