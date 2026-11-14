package com.example.smartpantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Data Model class representing a single Pantry Ingredient.
 */
public class Ingredient {
    public long id;
    public String name;
    public double quantity;
    public String unit;
    public String expiryDate;

    /**
     * Constructs a new Ingredient instance.
     *
     * @param id Database primary key ID.
     * @param name Ingredient name.
     * @param quantity Quantity amount.
     * @param unit Unit of measurement.
     * @param expiryDate Expiry date string (YYYY-MM-DD).
     */
    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
        this.quantity = quantity;
        this.unit = unit == null ? "" : unit.trim();
        this.expiryDate = expiryDate == null ? "" : expiryDate.trim();
    }

    /**
     * Formats quantity without trailing decimal zeroes for whole numbers.
     *
     * @return Formatted quantity string.
     */
    public String getFormattedQuantity() {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.format(Locale.ROOT, "%.2f", quantity).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /**
     * Checks if the ingredient expiry date is within 3 days or already passed.
     *
     * @return True if expiring soon or expired, false otherwise.
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
