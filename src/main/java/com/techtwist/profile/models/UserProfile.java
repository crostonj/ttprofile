package com.techtwist.profile.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private String firstName;
    private String lastName;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state; // Using abbreviation as requested
    private String zipCode;
    private String country;
    private String rowKey;
    private String partitionKey;
    private Map<String, Object> properties;

    public static String generateKey(String partitionKey, String rowKey) {
        return partitionKey + ":" + rowKey;
    }
}