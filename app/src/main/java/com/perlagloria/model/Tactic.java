package com.perlagloria.model;

public class Tactic {
    private int id;
    private String code;
    private String description;

    public Tactic(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Tactic setId(int id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Tactic setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Tactic setDescription(String description) {
        this.description = description;
        return this;
    }
}
