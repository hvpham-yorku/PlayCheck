package com.example.playcheck.puremodel;

import android.os.Build;

import com.example.playcheck.database.UserLinkToDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class User implements UserStats {

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String username;
    private String uid;
    private String classType;

    // Stats
    private int totallikes;
    private int totaldislikes;
    private int totalSavings;
    
    // New stats snapshots for the interface
    private UserStatsSnapshot dailyStats = new UserStatsSnapshot();
    private UserStatsSnapshot weeklyStats = new UserStatsSnapshot();
    private UserStatsSnapshot monthlyStats = new UserStatsSnapshot();
    private UserStatsSnapshot allTimeStats = new UserStatsSnapshot();

    protected static UserLinkToDatabase databaseService = null;

    public User() {
        this.classType = this.getClass().getSimpleName();
    }

    public User(String email, String password) {
        this();
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String dob, String gender) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setDateOfBirth(LocalDate.parse(dob));
        this.gender = gender;
    }

    // -------------------------------------------------------------------------------------------
    // Core Business Logic Methods
    // -------------------------------------------------------------------------------------------

    public CompletableFuture<String> register() {
        return getDatabaseService().createUser(this, this.password);
    }

    public static CompletableFuture<User> login(String email, String password) {
        return getDatabaseService().loginUser(email, password);
    }

    public static void logout() {
        getDatabaseService().logout();
    }

    public static CompletableFuture<User> getCurrentUser() {
        return getDatabaseService().getCurrentUser();
    }

    public CompletableFuture<Void> saveProfile() {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(new Exception("Cannot save profile: User UID is null."));
            }
        }
        return getDatabaseService().updateUser(this);
    }

    public CompletableFuture<Void> saveProfileFields(Map<String, Object> fields) {
        if (this.uid == null || this.uid.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return CompletableFuture.failedFuture(new Exception("Cannot save profile fields: User UID is null."));
            }
        }
        return getDatabaseService().updateUserFields(this.uid, fields)
                .thenAccept(v -> updateLocalFields(fields));
    }

    protected void updateLocalFields(Map<String, Object> fields) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            switch (entry.getKey()) {
                case "firstName": this.firstName = (String) entry.getValue(); break;
                case "lastName": this.lastName = (String) entry.getValue(); break;
                case "email": this.email = (String) entry.getValue(); break;
                case "gender": this.gender = (String) entry.getValue(); break;
                case "username": this.username = (String) entry.getValue(); break;
                case "totallikes": this.totallikes = (int) entry.getValue(); break;
                case "totaldislikes": this.totaldislikes = (int) entry.getValue(); break;
                case "totalSavings": this.totalSavings = (int) entry.getValue(); break;
                case "dateOfBirth":
                    if (entry.getValue() instanceof String) {
                        this.dateOfBirth = LocalDate.parse((String) entry.getValue());
                    }
                    break;
                case "dailyStats":
                    this.dailyStats = (UserStatsSnapshot) entry.getValue();
                    break;
                case "weeklyStats":
                    this.weeklyStats = (UserStatsSnapshot) entry.getValue();
                    break;
                case "monthlyStats":
                    this.monthlyStats = (UserStatsSnapshot) entry.getValue();
                    break;
                case "allTimeStats":
                    this.allTimeStats = (UserStatsSnapshot) entry.getValue();
                    break;
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    // Stats Implementation with Self-Action Prevention
    // -------------------------------------------------------------------------------------------

    protected boolean isSelfAction() {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        return current != null && current.getUid().equals(this.uid);
    }

    @Override
    public int gettotallikes() { return totallikes; }
    
    @Override
    public int gettotaldislikes() { return totaldislikes; }
    
    @Override
    public int gettotalSavings() { return totalSavings; }

    @Override
    public UserStatsSnapshot getStatsForPeriod(String period, String sport) {
        if (period == null) return new UserStatsSnapshot();
        switch (period.toLowerCase()) {
            case "daily": return getDailyStats(sport);
            case "weekly": return getWeeklyStats(sport);
            case "monthly": return getMonthlyStats(sport);
            case "alltime": return getAllTimeStats(sport);
            default: return new UserStatsSnapshot();
        }
    }

    @Override
    public UserStatsSnapshot getDailyStats(String sport) { return dailyStats; }
    
    @Override
    public UserStatsSnapshot getWeeklyStats(String sport) { return weeklyStats; }
    
    @Override
    public UserStatsSnapshot getMonthlyStats(String sport) { return monthlyStats; }
    
    @Override
    public UserStatsSnapshot getAllTimeStats(String sport) { return allTimeStats; }

    public void setDailyStats(UserStatsSnapshot stats) { this.dailyStats = stats; }
    public void setWeeklyStats(UserStatsSnapshot stats) { this.weeklyStats = stats; }
    public void setMonthlyStats(UserStatsSnapshot stats) { this.monthlyStats = stats; }
    public void setAllTimeStats(UserStatsSnapshot stats) { this.allTimeStats = stats; }

    public void addLike() { if (!isSelfAction()) totallikes++; }
    public void removeLike() { if (!isSelfAction()) totallikes--; }
    public void addDislike() { if (!isSelfAction()) totaldislikes++; }
    public void removeDislike() { if (!isSelfAction()) totaldislikes--; }
    public void addSavings() { if (!isSelfAction()) totalSavings++; }
    public void removeSavings() { if (!isSelfAction()) totalSavings--; }

    // -------------------------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------------------------

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return firstName + " " + lastName; }
    public void setName(String firstName, String lastName) { this.firstName = firstName; this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getClassType() { return classType; }
    public void setUsername(String username) { this.username = username; }
    public String getUsername() { return username; }

    public static void setDatabaseService(UserLinkToDatabase service) { databaseService = service; }
    private static UserLinkToDatabase getDatabaseService() {
        if (databaseService == null) databaseService = new UserLinkToDatabase();
        return databaseService;
    }
}
