package com.techtwist.profile.models;

import java.util.Map;

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
    private String rowId;
    private String partitionKey;
    private Map<String, Object> properties;

    // Default constructor (no-argument constructor)
    public UserProfile() {
    }

    // Constructor with all fields (optional, but often useful)
    public UserProfile(String firstName, String lastName, String email, String addressLine1, String addressLine2, String city, String state, String zipCode, String country, String rowId, String partitionKey) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.rowId = rowId;
        this.partitionKey = partitionKey;
    }

    // Getters and Setters for each field

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", addressLine1='" + addressLine1 + '\'' +
               ", addressLine2='" + addressLine2 + '\'' +
               ", city='" + city + '\'' +
               ", state='" + state + '\'' +
               ", zipCode='" + zipCode + '\'' +
               ", country='" + country + '\'' +
               ", rowId='" + rowId + '\'' +
               ", partitionKey='" + partitionKey + '\'' +
               ", properties=" + properties +
               '}';
    }
}