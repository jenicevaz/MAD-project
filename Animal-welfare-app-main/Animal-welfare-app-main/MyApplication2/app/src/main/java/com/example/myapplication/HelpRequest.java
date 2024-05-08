package com.example.myapplication;

public class HelpRequest {
    private String name;
    private String phone;
    private String location;
    private String details;

    public HelpRequest() {
        // Default constructor required for Firebase
    }

    public HelpRequest(String name, String phone, String location, String details) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

