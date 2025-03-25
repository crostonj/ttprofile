package com.techtwist.profile.services;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableTransactionAction;
import com.azure.data.tables.models.TableTransactionActionType;
import com.techtwist.profile.models.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class userProfileService {
    private TableClient tableClient;

    // Initialize the connection to Azure Table Storage
    public void initialize(String connectionString, String tableName) {
        tableClient = new TableClientBuilder()
            .connectionString(connectionString)
            .tableName(tableName)
            .buildClient();
    }

    // Fetch profile information by partition key and row key
    public TableEntity getProfile(String partitionKey, String rowKey) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        return tableClient.getEntity(partitionKey, rowKey);
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
        tableClient.createEntity(profile);
    }

    // Update an existing profile
    public void updateProfile(TableEntity profile) {
        if (tableClient == null) {
            throw new IllegalStateException("TableClient is not initialized. Call initialize() first.");
        }
        tableClient.updateEntity(profile);
    }

    public UserProfile mapToUserProfile(TableEntity entity) {
        UserProfile profile = new UserProfile();
        profile.setPartitionKey(entity.getPartitionKey());
        profile.setRowId(entity.getRowKey());
        profile.setFirstName(entity.getProperties().get("firstName").toString());
        profile.setLastName(entity.getProperties().get("lastName").toString());
        profile.setEmail(entity.getProperties().get("email").toString());
        profile.setAddressLine1(entity.getProperties().get("addressLine1").toString());
        profile.setAddressLine2(entity.getProperties().get("addressLine2").toString());
        profile.setCity(entity.getProperties().get("city").toString());
        profile.setState(entity.getProperties().get("state").toString());
        profile.setZipCode(entity.getProperties().get("zipCode").toString());
        profile.setCountry(entity.getProperties().get("country").toString());
        profile.setProperties(entity.getProperties());

        return profile;
    }

    public Map<String, Object> mapToTableEntity(UserProfile profile) {
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
        return entity.getProperties();
    }
}
