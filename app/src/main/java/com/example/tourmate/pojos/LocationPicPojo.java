package com.example.tourmate.pojos;

public class LocationPicPojo {
    private String locationPicId;
    private String eventId;
    private String downloadUrl;


    public LocationPicPojo() {
    }

    public LocationPicPojo(String locationPicId, String eventId, String downloadUrl) {
        this.locationPicId = locationPicId;
        this.eventId = eventId;
        this.downloadUrl = downloadUrl;
    }

    public String getLocationPicId() {
        return locationPicId;
    }

    public void setLocationPicId(String locationPicId) {
        this.locationPicId = locationPicId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
