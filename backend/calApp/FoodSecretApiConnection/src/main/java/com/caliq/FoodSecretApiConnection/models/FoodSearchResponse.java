package com.caliq.FoodSecretApiConnection.models;

import java.util.List;

public class FoodSearchResponse {
    private final List<FoodItem> items;
    private final String totalResults;

    public FoodSearchResponse(List<FoodItem> items, String totalResults) {
        this.items = items;
        this.totalResults = totalResults;
    }

    public List<FoodItem> getItems() {
        return items;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public static class FoodItem {
        private final String id;
        private final String name;
        private final String description;

        public FoodItem(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
