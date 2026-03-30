package com.example.playcheck.puremodel;

import com.example.playcheck.database.EventLinkToDatabase;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Event {

    private String eventId;
    private String eventTitle;
    private String eventDescription;

    private static EventLinkToDatabase databaseService = new EventLinkToDatabase();

    public Event(){
        this.eventId = "";
        this.eventTitle = "";
        this.eventDescription = "";
    }

    public Event(String eventId, String eventTitle, String eventDescription){
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
    }

    //-------------------------------------------------------------------------------------------
    // Business Logic Methods
    //-------------------------------------------------------------------------------------------

    public CompletableFuture<String> save() {
        return databaseService.saveEvent(this);
    }

    public static CompletableFuture<List<Event>> fetchAll() {
        return databaseService.getAllEvents();
    }

    public static CompletableFuture<Event> fetchById(String id) {
        return databaseService.getEventById(id);
    }

    //-------------------------------------------------------------------------------------------
    // Getters and Setters
    //-------------------------------------------------------------------------------------------

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getEventId() {
        return this.eventId;
    }
    public String getEventTitle() {
        return this.eventTitle;
    }
    public String getEventDescription() {
        return this.eventDescription;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
