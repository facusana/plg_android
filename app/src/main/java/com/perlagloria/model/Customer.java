package com.perlagloria.model;

public class Customer {
    private int id;
    private String name;
    private String createdDate;
    private boolean isActive;
    private boolean isSelected; //checkbox in the recycleview

    public Customer(int id, String name, String createdDate, boolean isActive, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.isActive = isActive;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public Customer setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public Customer setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public Customer setIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Customer setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }
}
