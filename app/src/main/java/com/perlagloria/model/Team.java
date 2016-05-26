package com.perlagloria.model;

public class Team {
    private int id;
    private String name;
    private String createdDate;
    private boolean isActive;
    private int position;
    private int points;
    private int gamesPlayed;
    private int wins;
    private int ties;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private int avgGoalForPerMatch;
    private int avgGoalAgainstPerMatch;
    private Tactic tactic;
    private boolean isSelected; //checkbox in the recycleview

    public Team(int id, String name, String createdDate, boolean isActive, int position, int points, int gamesPlayed,
                int wins, int ties, int losses, int goalsFor, int goalsAgainst, int goalDifference, int avgGoalForPerMatch,
                int avgGoalAgainstPerMatch, Tactic tactic, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.isActive = isActive;
        this.position = position;
        this.points = points;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.ties = ties;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalDifference = goalDifference;
        this.avgGoalForPerMatch = avgGoalForPerMatch;
        this.avgGoalAgainstPerMatch = avgGoalAgainstPerMatch;
        this.tactic = tactic;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public Team setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Team setName(String name) {
        this.name = name;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public Team setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public Team setIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public Team setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public Team setPoints(int points) {
        this.points = points;
        return this;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public Team setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
        return this;
    }

    public int getWins() {
        return wins;
    }

    public Team setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public int getTies() {
        return ties;
    }

    public Team setTies(int ties) {
        this.ties = ties;
        return this;
    }

    public int getLosses() {
        return losses;
    }

    public Team setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public Team setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
        return this;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public Team setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
        return this;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    public Team setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
        return this;
    }

    public int getAvgGoalForPerMatch() {
        return avgGoalForPerMatch;
    }

    public Team setAvgGoalForPerMatch(int avgGoalForPerMatch) {
        this.avgGoalForPerMatch = avgGoalForPerMatch;
        return this;
    }

    public int getAvgGoalAgainstPerMatch() {
        return avgGoalAgainstPerMatch;
    }

    public Team setAvgGoalAgainstPerMatch(int avgGoalAgainstPerMatch) {
        this.avgGoalAgainstPerMatch = avgGoalAgainstPerMatch;
        return this;
    }

    public Tactic getTactic() {
        return tactic;
    }

    public Team setTactic(Tactic tactic) {
        this.tactic = tactic;
        return this;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Team setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }
}
