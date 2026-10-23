package com.example.smartpantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ingredient {
    public long id;
    public String name;
    public double quantity;
    public String unit;
    public String expiryDate;

    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
        this.quantity = quantity;
        this.unit = unit == null ? "" : unit.trim();
        this.expiryDate = expiryDate == null ? "" : expiryDate.trim();
    }

    public String getFormattedQuantity() {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.format(Locale.ROOT, "%.2f", quantity).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * Checks if the ingredient expiry date is within 3 days or already passed.
     */
    public boolean isExpiringSoonOrExpired() {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        sdf.setLenient(false);
        try {
            Date exp = sdf.parse(expiryDate.trim());
            if (exp == null) return false;
            long nowMillis = System.currentTimeMillis();
            // Expired or expires within 3 days (259,200,000 ms)
            return exp.getTime() <= (nowMillis + 3L * 24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = name + " — " + getFormattedQuantity() + " " + unit;
        if (!expiryDate.isEmpty()) {
            result += " · Expires " + expiryDate;
        }
        return result;
    }
}
