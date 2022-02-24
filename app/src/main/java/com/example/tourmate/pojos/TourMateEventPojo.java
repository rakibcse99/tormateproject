package com.example.tourmate.pojos;

public class TourMateEventPojo {
    private String eventID;
    private String eventName;
    private String departure;
    private String destination;
    private int initialBudget;
    private String departureDate;
    private String createEventDate;

    public TourMateEventPojo() {
        //required for firebase
    }

    public TourMateEventPojo(String eventID, String eventName, String departure, String destination, int initialBudget, String departureDate,String createEventDate) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.departure = departure;
        this.destination = destination;
        this.initialBudget = initialBudget;
        this.departureDate = departureDate;
        this.createEventDate = createEventDate;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public void setInitialBudget(int initialBudget) {
        this.initialBudget = initialBudget;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }


    public String getCreateEventDate() {
        return createEventDate;
    }

    public void setCreateEventDate(String createEventDate) {
        this.createEventDate = createEventDate;
    }


}
