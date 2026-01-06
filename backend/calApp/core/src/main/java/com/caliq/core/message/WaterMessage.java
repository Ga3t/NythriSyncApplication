package com.caliq.core.message;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class WaterMessage {
    Long userId;
    BigDecimal waterMl;
    LocalDateTime sendAt;
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public BigDecimal getWaterMl() {
        return waterMl;
    }
    public void setWaterMl(BigDecimal waterMl) {
        this.waterMl = waterMl;
    }
    public LocalDateTime getSendAt() {
        return sendAt;
    }
    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }
}
