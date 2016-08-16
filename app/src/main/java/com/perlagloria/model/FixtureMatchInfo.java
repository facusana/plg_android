package com.perlagloria.model;

public class FixtureMatchInfo {
    private int id;
    private FixtureDate fixtureDate;
    private Team homeTeam;
    private Team awayTeam;
    private String lastUpdateDate;
    private String fieldNumber;
    private String hour;
    private String homeGoals;
    private String awayGoals;
    private String mapCode;
    private String heading;

    public FixtureMatchInfo(int id, FixtureDate fixtureDate, Team homeTeam, Team awayTeam, String lastUpdateDate,
                            String fieldNumber, String hour, String homeGoals, String awayGoals, String mapCode, String heading) {
        this.id = id;
        this.fixtureDate = fixtureDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.lastUpdateDate = lastUpdateDate;
        this.fieldNumber = fieldNumber;
        this.hour = hour;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.mapCode = mapCode;
        this.heading = heading;
    }

    public int getId() {
        return id;
    }

    public FixtureMatchInfo setId(int id) {
        this.id = id;
        return this;
    }

    public FixtureDate getFixtureDate() {
        return fixtureDate;
    }

    public FixtureMatchInfo setFixtureDate(FixtureDate fixtureDate) {
        this.fixtureDate = fixtureDate;
        return this;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public FixtureMatchInfo setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
        return this;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public FixtureMatchInfo setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
        return this;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public FixtureMatchInfo setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public String getFieldNumber() {
        return fieldNumber;
    }

    public FixtureMatchInfo setFieldNumber(String fieldNumber) {
        this.fieldNumber = fieldNumber;
        return this;
    }

    public String getHour() {
        return hour;
    }

    public FixtureMatchInfo setHour(String hour) {
        this.hour = hour;
        return this;
    }

    public String getHomeGoals() {
        return homeGoals;
    }

    public FixtureMatchInfo setHomeGoals(String homeGoals) {
        this.homeGoals = homeGoals;
        return this;
    }

    public String getAwayGoals() {
        return awayGoals;
    }

    public FixtureMatchInfo setAwayGoals(String awayGoals) {
        this.awayGoals = awayGoals;
        return this;
    }

    public String getMapCode() {
        return mapCode;
    }

    public FixtureMatchInfo setMapCode(String mapCode) {
        this.mapCode = mapCode;
        return this;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
