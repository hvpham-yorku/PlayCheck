package com.example.playcheck.puremodel;

public interface UserStats {

    public int gettotallikes();
    public int gettotaldislikes();
    public int gettotalSavings();
    
    // New methods for snapshot-based max stats
    public UserStatsSnapshot getDailyStats();
    public UserStatsSnapshot getWeeklyStats();
    public UserStatsSnapshot getMonthlyStats();
    public UserStatsSnapshot getAllTimeStats();
}
