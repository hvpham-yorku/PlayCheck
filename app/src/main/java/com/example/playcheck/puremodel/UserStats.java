package com.example.playcheck.puremodel;

public interface UserStats {

    public int gettotallikes();
    public int gettotaldislikes();
    public int gettotalSavings();
    
    /**
     * Get statistics for a specific period and sport.
     * @param period the time period (daily, weekly, monthly, allTime)
     * @param sport the sport name (e.g., Football, Basketball) or "All"
     * @return UserStatsSnapshot containing the max values
     */
    public UserStatsSnapshot getStatsForPeriod(String period, String sport);

    public UserStatsSnapshot getDailyStats(String sport);
    public UserStatsSnapshot getWeeklyStats(String sport);
    public UserStatsSnapshot getMonthlyStats(String sport);
    public UserStatsSnapshot getAllTimeStats(String sport);
}
