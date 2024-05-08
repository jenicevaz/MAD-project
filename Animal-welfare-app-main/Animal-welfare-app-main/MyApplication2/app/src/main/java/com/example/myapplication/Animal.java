package com.example.myapplication;

public class Animal {
    private String breed;
    private String id;
    private String color;
    private String image; // Add the image URL field

    public Animal() {
        // Default constructor required for Firebase
    }

    public Animal(String breed, String id, String color, String image) {
        this.breed = breed;
        this.id = id;
        this.color = color;
        this.image = image;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Breed: " + breed + ", ID: " + id + ", Color: " + color;
    }
}
