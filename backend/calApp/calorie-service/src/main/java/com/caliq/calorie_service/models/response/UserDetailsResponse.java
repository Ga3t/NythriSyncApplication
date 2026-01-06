package com.caliq.calorie_service.models.response;
import java.math.BigDecimal;
public class UserDetailsResponse {
    BigDecimal avgweight;
    BigDecimal bmr;
    public BigDecimal getAvgweight() {
        return avgweight;
    }
    public void setAvgweight(BigDecimal avgweight) {
        this.avgweight = avgweight;
    }
    public BigDecimal getBmr() {
        return bmr;
    }
    public void setBmr(BigDecimal bmr) {
        this.bmr = bmr;
    }
}