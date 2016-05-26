package com.perlagloria.model;

public class FixtureDate {
    private int id;
    private Division division;
    private String date;
    private String dateNumber;
    private Boolean isSuspended;
    private String suspendedReason;

    public FixtureDate(int id, Division division, String date, String dateNumber, Boolean isSuspended, String suspendedReason) {
        this.id = id;
        this.division = division;
        this.date = date;
        this.dateNumber = dateNumber;
        this.isSuspended = isSuspended;
        this.suspendedReason = suspendedReason;
    }

    public int getId() {
        return id;
    }

    public FixtureDate setId(int id) {
        this.id = id;
        return this;
    }

    public Division getDivision() {
        return division;
    }

    public FixtureDate setDivision(Division division) {
        this.division = division;
        return this;
    }

    public String getDate() {
        return date;
    }

    public FixtureDate setDate(String date) {
        this.date = date;
        return this;
    }

    public String getDateNumber() {
        return dateNumber;
    }

    public FixtureDate setDateNumber(String dateNumber) {
        this.dateNumber = dateNumber;
        return this;
    }

    public Boolean getIsSuspended() {
        return this.isSuspended;
    }

    public FixtureDate setIsSuspended(Boolean isSuspended) {
        this.isSuspended = isSuspended;
        return this;
    }

    public String getSuspendedReason() {
        return suspendedReason;
    }

    public FixtureDate setSuspendedReason(String suspendedReason) {
        this.suspendedReason = suspendedReason;
        return this;
    }
}
