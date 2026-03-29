package com.example.playcheck.puremodel;

/**
 * Model class for Announcements
 * Stored in Firebase under /announcements
 */
public class Announcement {
    private String announcementId;
    private String title;
    private String description;
    private long timestamp;

    public Announcement() {
        // Required empty constructor for Firebase
    }

    public Announcement(String title, String description, long timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters
    public String getAnnouncementId() {
        return announcementId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}