package com.example.myapplication;

public class PetFoodOrder {
    private String petType;
    private String foodType;
    private int quantity;
    private String address;

    public PetFoodOrder() {
        // Default constructor required for calls to DataSnapshot.getValue(PetFoodOrder.class)
    }

    public PetFoodOrder(String petType, String foodType, int quantity, String address) {
        this.petType = petType;
        this.foodType = foodType;
        this.quantity = quantity;
        this.address = address;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
