package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "ingredients TEXT NOT NULL, " +
                "steps TEXT NOT NULL)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        String[][] data = {
            {"Tomato Egg Scramble", "egg|2|piece; tomato|1|piece; salt|1|pinch", "1. Chop the tomato.\n2. Beat the eggs with salt.\n3. Cook the tomato, add eggs, and stir until set."},
            {"Garlic Tomato Pasta", "pasta|200|g; tomato|2|piece; garlic|1|clove; salt|1|pinch", "1. Boil pasta.\n2. Cook chopped tomato and garlic.\n3. Mix with drained pasta and salt."},
            {"Banana Oat Porridge", "oats|50|g; milk|200|ml; banana|1|piece", "1. Simmer oats in milk.\n2. Slice banana.\n3. Serve together."},
            {"Cheese Omelette", "egg|2|piece; cheese|30|g; salt|1|pinch", "1. Beat eggs and salt.\n2. Pour into a pan.\n3. Add cheese, fold, and cook through."},
            {"Potato Egg Hash", "potato|2|piece; egg|1|piece; onion|1|piece; salt|1|pinch", "1. Dice potato and onion.\n2. Cook until tender.\n3. Add egg and cook until set."},
            {"Peanut Banana Toast", "bread|2|slice; peanut butter|2|tbsp; banana|1|piece", "1. Toast bread.\n2. Spread peanut butter.\n3. Add sliced banana."},
            {"Simple Fried Rice", "rice|200|g; egg|1|piece; carrot|1|piece; soy sauce|1|tbsp", "1. Cook rice if not already cooked.\n2. Dice carrot and cook.\n3. Add rice, egg, and soy sauce; stir-fry."},
            {"Cheesy Potato Bake", "potato|3|piece; cheese|60|g; milk|100|ml; salt|1|pinch", "1. Slice potatoes.\n2. Arrange with milk and salt in a baking dish.\n3. Top with cheese and bake until tender."},
            {"Apple Oat Bowl", "apple|1|piece; oats|40|g; yoghurt|100|g", "1. Chop apple.\n2. Add oats and yoghurt.\n3. Mix and serve."},
            {"Tuna Pasta", "pasta|200|g; tuna|1|can; tomato|1|piece; salt|1|pinch", "1. Boil pasta.\n2. Warm tuna with chopped tomato.\n3. Combine and season."},
            {"Carrot Potato Soup", "carrot|2|piece; potato|2|piece; onion|1|piece; water|500|ml; salt|1|pinch", "1. Chop vegetables.\n2. Simmer in water until soft.\n3. Mash or blend and season."},
            {"Yoghurt Banana Smoothie", "banana|1|piece; yoghurt|150|g; milk|100|ml", "1. Add all ingredients to a blender.\n2. Blend until smooth.\n3. Serve immediately."},
            {"Garlic Cheese Toast", "bread|2|slice; cheese|40|g; garlic|1|clove", "1. Mince garlic.\n2. Add garlic and cheese to bread.\n3. Toast until cheese melts."},
            {"Onion Tomato Salad", "onion|1|piece; tomato|2|piece; salt|1|pinch", "1. Wash and chop vegetables.\n2. Mix in a bowl.\n3. Add salt and serve."},
            {"Egg Fried Rice", "rice|200|g; egg|2|piece; onion|1|piece; soy sauce|1|tbsp", "1. Cook and cool rice.\n2. Cook chopped onion and scrambled egg.\n3. Add rice and soy sauce and stir-fry."},
            {"Apple Banana Fruit Bowl", "apple|1|piece; banana|1|piece; yoghurt|100|g", "1. Chop the fruit.\n2. Place in a bowl.\n3. Add yoghurt and mix."},
            {"Cheese Tomato Sandwich", "bread|2|slice; cheese|30|g; tomato|1|piece", "1. Slice tomato.\n2. Place cheese and tomato on bread.\n3. Serve as is or toast."},
            {"Carrot Oat Patties", "carrot|1|piece; oats|50|g; egg|1|piece; salt|1|pinch", "1. Grate carrot.\n2. Mix with oats, egg, and salt.\n3. Shape into patties and pan-cook thoroughly."}
        };

        for (String[] item : data) {
            ContentValues values = new ContentValues();
            values.put("name", item[0]);
            values.put("ingredients", item[1]);
            values.put("steps", item[2]);
            db.insertWithOnConflict("recipes", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    // --- Pantry CRUD Operations ---

    public long addIngredient(String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim());
        v.put("quantity", quantity);
        v.put("unit", normalizeUnit(unit));
        v.put("expiry_date", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().insert("pantry", null, v);
    }

    public int updateIngredient(long id, String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim());
        v.put("quantity", quantity);
        v.put("unit", normalizeUnit(unit));
        v.put("expiry_date", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().update("pantry", v, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteIngredient(long id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    public Ingredient getIngredient(long id) {
        try (Cursor c = getReadableDatabase().query("pantry", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) return ingredientFromCursor(c);
        }
        return null;
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                result.add(ingredientFromCursor(c));
            }
        }
        return result;
    }

    private Ingredient ingredientFromCursor(Cursor c) {
        return new Ingredient(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry_date"))
        );
    }

    // --- Recipe Operations ---

    public List<Recipe> getAllRecipes() {
        List<Recipe> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("recipes", null, null, null, null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                result.add(recipeFromCursor(c));
            }
        }
        return result;
    }

    public Recipe getRecipe(long id) {
        try (Cursor c = getReadableDatabase().query("recipes", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) return recipeFromCursor(c);
        }
        return null;
    }

    private Recipe recipeFromCursor(Cursor c) {
        return new Recipe(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("ingredients")),
                c.getString(c.getColumnIndexOrThrow("steps"))
        );
    }

    // --- Strict & Almost There Recipe Matching Logic ---

    /**
     * Strict Matching Rule (Section 2.3):
     * A recipe qualifies ONLY if EVERY single required ingredient is present in the pantry in AT LEAST the required quantity.
     */
    public List<Recipe> getStrictMatches() {
        List<Ingredient> pantry = getAllIngredients();
        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : getAllRecipes()) {
            boolean qualifies = true;
            for (Recipe.Requirement req : recipe.ingredients) {
                double available = getAvailableQuantity(pantry, req);
                if (available + 1e-9 < req.quantity) {
                    qualifies = false;
                    break;
                }
            }
            if (qualifies) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /**
     * Stretch Goal (Section 2.3):
     * Returns recipes missing exactly ONE required ingredient (or lacking quantity for only one).
     */
    public List<Recipe.AlmostThereRecipe> getAlmostThereMatches() {
        List<Ingredient> pantry = getAllIngredients();
        List<Recipe.AlmostThereRecipe> matches = new ArrayList<>();

        for (Recipe recipe : getAllRecipes()) {
            int missingCount = 0;
            String missingDetail = "";

            for (Recipe.Requirement req : recipe.ingredients) {
                double available = getAvailableQuantity(pantry, req);
                if (available + 1e-9 < req.quantity) {
                    missingCount++;
                    double diff = req.quantity - available;
                    String diffStr = (diff == Math.floor(diff)) ? String.valueOf((long) diff) : String.format(Locale.ROOT, "%.1f", diff);
                    missingDetail = diffStr + " " + req.unit + " " + req.name;
                }
            }

            if (missingCount == 1) {
                matches.add(new Recipe.AlmostThereRecipe(recipe, missingDetail));
            }
        }
        return matches;
    }

    private double getAvailableQuantity(List<Ingredient> pantry, Recipe.Requirement req) {
        double total = 0;
        for (Ingredient item : pantry) {
            if (sameIngredient(item.name, req.name) && compatibleUnit(item.unit, req.unit)) {
                total += convert(item.quantity, item.unit, req.unit);
            }
        }
        return total;
    }

    // --- Robust Ingredient & Unit Normalization ---

    public static boolean sameIngredient(String a, String b) {
        return normalizeName(a).equals(normalizeName(b));
    }

    public static String normalizeName(String value) {
        if (value == null) return "";
        String n = value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();

        // Direct common mappings
        if (n.equalsIgnoreCase("eggs") || n.equalsIgnoreCase("egg")) return "egg";
        if (n.equalsIgnoreCase("tomatoes") || n.equalsIgnoreCase("tomato")) return "tomato";
        if (n.equalsIgnoreCase("potatoes") || n.equalsIgnoreCase("potato")) return "potato";
        if (n.equalsIgnoreCase("onions") || n.equalsIgnoreCase("onion")) return "onion";
        if (n.equalsIgnoreCase("carrots") || n.equalsIgnoreCase("carrot")) return "carrot";
        if (n.equalsIgnoreCase("apples") || n.equalsIgnoreCase("apple")) return "apple";
        if (n.equalsIgnoreCase("bananas") || n.equalsIgnoreCase("banana")) return "banana";
        if (n.equalsIgnoreCase("oats") || n.equalsIgnoreCase("oat")) return "oat";
        if (n.equalsIgnoreCase("yoghurt") || n.equalsIgnoreCase("yogurt")) return "yoghurt";

        if (n.endsWith("ies") && n.length() > 4) return n.substring(0, n.length() - 3) + "y";
        if (n.endsWith("oes") && n.length() > 4) return n.substring(0, n.length() - 2);
        if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 3) return n.substring(0, n.length() - 1);
        return n;
    }

    public static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("g") || u.equals("gram") || u.equals("grams")) return "g";
        if (u.equals("kg") || u.equals("kilogram") || u.equals("kilograms") || u.equals("kilo") || u.equals("kilos")) return "kg";
        if (u.equals("ml") || u.equals("millilitre") || u.equals("millilitres") || u.equals("milliliter") || u.equals("milliliters")) return "ml";
        if (u.equals("l") || u.equals("litre") || u.equals("litres") || u.equals("liter") || u.equals("liters")) return "l";
        if (u.equals("piece") || u.equals("pieces") || u.equals("pc") || u.equals("pcs")) return "piece";
        if (u.equals("clove") || u.equals("cloves")) return "clove";
        if (u.equals("slice") || u.equals("slices")) return "slice";
        if (u.equals("tbsp") || u.equals("tablespoon") || u.equals("tablespoons")) return "tbsp";
        if (u.equals("tsp") || u.equals("teaspoon") || u.equals("teaspoons")) return "tsp";
        if (u.equals("pinch") || u.equals("pinches")) return "pinch";
        if (u.equals("can") || u.equals("cans")) return "can";
        if (u.equals("cup") || u.equals("cups")) return "cup";
        return u;
    }

    private static String unitFamily(String unit) {
        String u = normalizeUnit(unit);
        if (u.equals("g") || u.equals("kg")) return "mass";
        if (u.equals("ml") || u.equals("l")) return "volume";
        return u;
    }

    public static boolean compatibleUnit(String a, String b) {
        return unitFamily(a).equals(unitFamily(b));
    }

    public static double convert(double amount, String from, String to) {
        String f = normalizeUnit(from);
        String t = normalizeUnit(to);
        if (f.equals("kg") && t.equals("g")) return amount * 1000.0;
        if (f.equals("g") && t.equals("kg")) return amount / 1000.0;
        if (f.equals("l") && t.equals("ml")) return amount * 1000.0;
        if (f.equals("ml") && t.equals("l")) return amount / 1000.0;
        return amount;
    }
}
