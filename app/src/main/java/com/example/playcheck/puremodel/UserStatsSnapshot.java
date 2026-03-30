package com.example.playcheck.puremodel;

public class UserStatsSnapshot {
    private int maxLikes;
    private int maxDislikes;
    private int maxSavings;

    public UserStatsSnapshot() {}

    public UserStatsSnapshot(int maxLikes, int maxDislikes, int maxSavings) {
        this.maxLikes = maxLikes;
        this.maxDislikes = maxDislikes;
        this.maxSavings = maxSavings;
    }

    public int getMaxLikes() { return maxLikes; }
    public void setMaxLikes(int maxLikes) { this.maxLikes = maxLikes; }

    public int getMaxDislikes() { return maxDislikes; }
    public void setMaxDislikes(int maxDislikes) { this.maxDislikes = maxDislikes; }

    public int getMaxSavings() { return maxSavings; }
    public void setMaxSavings(int maxSavings) { this.maxSavings = maxSavings; }
}
