package com.caliq.core.message;
import com.caliq.core.response.FoodResponse;
import java.time.LocalDateTime;
import java.util.List;
public class InfoAboutProductMessage {
    List<FoodResponse>foodResponses;
    Long userId;
    LocalDateTime sendAt;
    String serviceName;
    public InfoAboutProductMessage() {
    }
    public InfoAboutProductMessage(List<FoodResponse> foodResponses, Long userId, LocalDateTime sendAt) {
        this.foodResponses = foodResponses;
        this.userId = userId;
        this.sendAt = sendAt;
    }
    public List<FoodResponse> getFoodResponses() {
        return foodResponses;
    }
    public void setFoodResponses(List<FoodResponse> foodResponses) {
        this.foodResponses = foodResponses;
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
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
