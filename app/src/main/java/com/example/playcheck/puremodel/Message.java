package com.example.playcheck.puremodel;

/**
 * Message model for message board communication between organizers and referees
 */
public class Message {
    private String messageId;
    private String senderId;
    private String senderName;
    private String senderType; // "organizer" or "referee"
    private String content;
    private long timestamp;
    private String gameId; // Optional - if message is about a specific game
    private String gameName; // Optional - for display
    private boolean isRead;
    
    public Message() {
        // Required for Firebase
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }
    
    public Message(String senderId, String senderName, String senderType, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderType = senderType;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }
    
    public Message(String senderId, String senderName, String senderType, String content, 
                   String gameId, String gameName) {
        this(senderId, senderName, senderType, content);
        this.gameId = gameId;
        this.gameName = gameName;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getSenderType() { return senderType; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public String getGameId() { return gameId; }
    public String getGameName() { return gameName; }
    public boolean isRead() { return isRead; }
    
    // Setters
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public void setRead(boolean read) { isRead = read; }
}
