package com.caliq.calorie_service.models.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WeightLogsResponse {
    List<Weight> weightLogs;

    public class Weight{
        BigDecimal weight;
        LocalDate date;

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }


    public List<Weight> getWeightLogs() {
        return weightLogs;
    }

    public void setWeightLogs(List<Weight> weightLogs) {
        this.weightLogs = weightLogs;
    }
}
