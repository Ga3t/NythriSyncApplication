package com.caliq.core.message;
import com.caliq.core.dto.MealDto;
import java.time.LocalDateTime;
public class MealMessage {
    private Long userId;
    private MealDto meal;
    private LocalDateTime sentAt;
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public MealDto getMeal() {
        return meal;
    }
    public void setMeal(MealDto meal) {
        this.meal = meal;
    }
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
