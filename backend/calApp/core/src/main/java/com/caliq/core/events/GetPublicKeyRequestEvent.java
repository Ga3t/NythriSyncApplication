package com.caliq.core.events;

public class GetPublicKeyRequestEvent {
    private String requestId; // для корреляции

    public GetPublicKeyRequestEvent() {}
    public GetPublicKeyRequestEvent(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

