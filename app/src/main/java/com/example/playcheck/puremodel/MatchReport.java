package com.example.playcheck.puremodel;

import java.util.Map;

public class MatchReport {
    private String score;
    private String notes;
    private Map<String, Object> detailedStats;

    public MatchReport() {}

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Map<String, Object> getDetailedStats() {
        return detailedStats;
    }

    public void setDetailedStats(Map<String, Object> detailedStats) {
        this.detailedStats = detailedStats;
    }
}
