package com.example.playcheck.puremodel;

public class Event {


    private String eventId;
    private String eventTitle;
    private String eventDescription;


    Event(){
        this.eventId = "";
        this.eventTitle = "";
        this.eventDescription = "";
    }

    // TODO: 2026-03-05  Work on this as it is used in the OgranizerLinkToDatabase class
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
}
