package com.mycompany.datagame;

public class Player {
    private String username;
    private int score;
    private int level;

    public Player(String username, int score, int level) {
        this.username = username;
        this.score = score;
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", score=" + score +
                ", level=" + level +
                '}';
    }
}
