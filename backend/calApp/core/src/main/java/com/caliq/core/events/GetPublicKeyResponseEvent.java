package com.caliq.core.events;
public class GetPublicKeyResponseEvent {
        private String requestId;
        private String publicKey;
        public GetPublicKeyResponseEvent() {}
        public GetPublicKeyResponseEvent(String requestId, String publicKey) {
            this.requestId = requestId;
            this.publicKey = publicKey;
        }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getPublicKey() {
        return publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
