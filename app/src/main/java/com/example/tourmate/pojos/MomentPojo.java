package com.example.tourmate.pojos;

public class MomentPojo {

    private String momentId;
    private String eventId;
    private String downloadUrl;

    public MomentPojo() {
    }

    public MomentPojo(String momentId, String eventId, String downloadUrl) {
        this.momentId = momentId;
        this.eventId = eventId;
        this.downloadUrl = downloadUrl;
    }

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
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
