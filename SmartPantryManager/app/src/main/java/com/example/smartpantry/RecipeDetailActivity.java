package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity for displaying complete Recipe details including full ingredient list,
 * step-by-step preparation method, and strict match verification status.
 */
public class RecipeDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        DatabaseHelper db = new DatabaseHelper(this);
        long id = getIntent().getLongExtra("recipe_id", -1);
        Recipe recipe = db.getRecipe(id);

        TextView tvTitle = findViewById(R.id.tv_recipe_detail_title);
        LinearLayout llIngredients = findViewById(R.id.ll_ingredients_list);
        TextView tvSteps = findViewById(R.id.tv_steps_content);

        Button btnBack = findViewById(R.id.btn_back_to_suggestions);
        btnBack.setOnClickListener(v -> finish());

        if (recipe == null) {
            tvTitle.setText(R.string.err_recipe_not_found);
        } else {
            tvTitle.setText(recipe.name);

            // Dynamically populate required ingredients list
            for (Recipe.Requirement req : recipe.ingredients) {
                TextView item = new TextView(this);
                item.setText("• " + req.toString());
                item.setTextSize(15);
                item.setTextColor(Color.parseColor("#283228"));
                item.setPadding(12, 4, 0, 4);
                llIngredients.addView(item);
            }

            tvSteps.setText(recipe.steps.replace("\\n", "\n"));
        }

        setupNavigation();
    }

    private void setupNavigation() {
        Button navPantry = findViewById(R.id.nav_btn_pantry);
        Button navRecipes = findViewById(R.id.nav_btn_recipes);
        Button navSettings = findViewById(R.id.nav_btn_settings);

        if (navPantry != null) navPantry.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        });
        if (navRecipes != null) navRecipes.setOnClickListener(v -> {
            Intent i = new Intent(this, SuggestedRecipesActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        });
        if (navSettings != null) navSettings.setOnClickListener(v -> {
            Intent i = new Intent(this, SettingsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        });
    }
}
