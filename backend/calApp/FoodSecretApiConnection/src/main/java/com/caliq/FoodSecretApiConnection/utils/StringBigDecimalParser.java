package com.caliq.FoodSecretApiConnection.utils;
import java.math.BigDecimal;
public class StringBigDecimalParser {
   public static BigDecimal parseBigDecimalSafe(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            String normalized = value.trim().replace(",", ".");
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}