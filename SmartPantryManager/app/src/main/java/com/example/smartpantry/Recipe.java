package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Model class representing a Recipe, including its required ingredients and preparation steps.
 */
public class Recipe {
    public long id;
    public String name;
    public String ingredientsText;
    public String steps;
    public List<Requirement> ingredients = new ArrayList<>();

    /**
     * Constructs a new Recipe instance from database fields.
     *
     * @param id Database primary key ID.
     * @param name Unique recipe name.
     * @param ingredientsText Formatted string of required ingredients (name|quantity|unit;).
     * @param steps Step-by-step preparation instructions.
     */
    public Recipe(long id, String name, String ingredientsText, String steps) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
        this.ingredientsText = ingredientsText == null ? "" : ingredientsText.trim();
        this.steps = steps == null ? "" : steps.trim();

        if (!this.ingredientsText.isEmpty()) {
            for (String part : this.ingredientsText.split(";")) {
                String[] fields = part.trim().split("\\|");
                if (fields.length == 3) {
                    try {
                        ingredients.add(new Requirement(fields[0].trim(),
                                Double.parseDouble(fields[1].trim()), fields[2].trim()));
                    } catch (NumberFormatException ignored) { }
                }
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Data class representing an individual ingredient requirement for a recipe.
     */
    public static class Requirement {
        public String name;
        public double quantity;
        public String unit;

        public Requirement(String name, double quantity, String unit) {
            this.name = name == null ? "" : name.trim();
            this.quantity = quantity;
            this.unit = unit == null ? "" : unit.trim();
        }

        public String getFormattedQuantity() {
            if (quantity == Math.floor(quantity)) {
                return String.valueOf((long) quantity);
            }
            return String.valueOf(quantity);
        }

        @Override
        public String toString() {
            return name + " — " + getFormattedQuantity() + " " + unit;
        }
    }

    /**
     * Data wrapper class representing an "Almost There" recipe missing 1 ingredient.
     */
    public static class AlmostThereRecipe {
        public Recipe recipe;
        public String missingInfo;

        public AlmostThereRecipe(Recipe recipe, String missingInfo) {
            this.recipe = recipe;
            this.missingInfo = missingInfo;
        }

        @Override
        public String toString() {
            return recipe.name + " (Missing: " + missingInfo + ")";
        }
    }
}
