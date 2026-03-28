package com.example.playcheck.puremodel;

public class Event {


    private String eventId;
    private String eventTitle;
    private String eventDescription;


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


    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }




}
