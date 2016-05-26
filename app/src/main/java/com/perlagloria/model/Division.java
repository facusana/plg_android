package com.perlagloria.model;

public class Division {
    private int id;
    private String name;
    private String createdDate;
    private boolean isActive;
    private boolean isSelected; //checkbox in the recycleview

    public Division(int id, String name, String createdDate, boolean isActive, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.isActive = isActive;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public Division setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Division setName(String name) {
        this.name = name;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public Division setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public Division setIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Division setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }
}
