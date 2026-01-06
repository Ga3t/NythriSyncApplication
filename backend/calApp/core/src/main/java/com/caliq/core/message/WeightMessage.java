package com.caliq.core.message;
import java.math.BigDecimal;
import java.time.LocalDate;
public class WeightMessage {
    Long userId;
    BigDecimal weight;
    LocalDate sendAt;
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public LocalDate getSendAt() {
        return sendAt;
    }
    public void setSendAt(LocalDate sendAt) {
        this.sendAt = sendAt;
    }
}
