package com.perlagloria.model;

public class Scorer {
    private String teamName;
    private String playerName;
    private int goals;

    public Scorer(String teamName, String playerName, int goals) {
        this.teamName = teamName;
        this.playerName = playerName;
        this.goals = goals;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }
}
