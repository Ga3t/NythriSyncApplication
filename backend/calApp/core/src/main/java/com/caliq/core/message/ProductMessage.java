package com.caliq.core.message;
import java.time.LocalDateTime;
import java.util.List;
public class ProductMessage {
    List<String> code;
    Long userId;
    LocalDateTime sendAt;
    public ProductMessage() {
    }
    public ProductMessage(List<String> code, Long userId, LocalDateTime sendAt) {
        this.code = code;
        this.userId = userId;
        this.sendAt = sendAt;
    }
    public List<String> getCode() {
        return code;
    }
    public void setCode(List<String> code) {
        this.code = code;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public LocalDateTime getSendAt() {
        return sendAt;
    }
    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }
}
