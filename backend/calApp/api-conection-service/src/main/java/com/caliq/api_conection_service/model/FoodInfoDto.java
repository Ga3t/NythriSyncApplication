package com.caliq.api_conection_service.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodInfoDto {
    @JsonProperty("code")
    private String code;
    @JsonProperty("product")
    private Product product;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        @JsonProperty("product_name")
        private String productName;
        private Nutriments nutriments;
        @JsonProperty("nutriscore_data")
        private NutriscoreData nutriscoreData;
        @JsonProperty("allergens_tags")
        private String[] allergens;
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Nutriments getNutriments() { return nutriments; }
        public void setNutriments(Nutriments nutriments) { this.nutriments = nutriments; }
        public NutriscoreData getNutriscoreData() { return nutriscoreData; }
        public void setNutriscoreData(NutriscoreData nutriscoreData) { this.nutriscoreData = nutriscoreData; }
        public String[] getAllergens() { return allergens; }
        public void setAllergens(String[] allergens) { this.allergens = allergens;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Nutriments {
        @JsonProperty("fat_100g")
        private BigDecimal fat_100g;
        @JsonProperty("proteins_100g")
        private BigDecimal proteins_100g;
        @JsonProperty("carbohydrates_100g")
        private BigDecimal carbohydrates_100g;
        @JsonProperty("fiber_100g")
        private BigDecimal fiber_100g;
        @JsonProperty("sugars_100g")
        private BigDecimal sugars_100g;
        @JsonProperty("cholesterol_100g")
        private BigDecimal cholesterol_100g;
        @JsonProperty("energy-kcal_100g")
        private BigDecimal energyKcal_100g;
        public BigDecimal getFat_100g() {
            return fat_100g;
        }
        public void setFat_100g(BigDecimal fat_100g) {
            this.fat_100g = fat_100g;
        }
        public BigDecimal getProteins_100g() {
            return proteins_100g;
        }
        public void setProteins_100g(BigDecimal proteins_100g) {
            this.proteins_100g = proteins_100g;
        }
        public BigDecimal getCarbohydrates_100g() {
            return carbohydrates_100g;
        }
        public void setCarbohydrates_100g(BigDecimal carbohydrates_100g) {
            this.carbohydrates_100g = carbohydrates_100g;
        }
        public BigDecimal getFiber_100g() {
            return fiber_100g;
        }
        public void setFiber_100g(BigDecimal fiber_100g) {
            this.fiber_100g = fiber_100g;
        }
        public BigDecimal getSugars_100g() {
            return sugars_100g;
        }
        public void setSugars_100g(BigDecimal sugars_100g) {
            this.sugars_100g = sugars_100g;
        }
        public BigDecimal getCholesterol_100g() {
            return cholesterol_100g;
        }
        public void setCholesterol_100g(BigDecimal cholesterol_100g) {
            this.cholesterol_100g = cholesterol_100g;
        }
        public BigDecimal getEnergyKcal_100g() {
            return energyKcal_100g;
        }
        public void setEnergyKcal_100g(BigDecimal energyKcal_100g) {
            this.energyKcal_100g = energyKcal_100g;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NutriscoreData {
        private String grade;
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
    }
    public boolean isEmpty(){
        if(product.getProductName().isEmpty() && code.isEmpty())
            return true;
        else
            return false;
    }
}