package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Activity for displaying Suggested Recipes that strictly match available pantry stock,
 * as well as "Almost There" recipes missing only 1 ingredient.
 */
public class SuggestedRecipesActivity extends Activity {
    private DatabaseHelper db;
    private LinearLayout llStrictList;
    private LinearLayout llAlmostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = new DatabaseHelper(this);

        llStrictList = findViewById(R.id.ll_strict_matches_list);
        llAlmostList = findViewById(R.id.ll_almost_there_list);

        Button btnRefresh = findViewById(R.id.btn_refresh_suggestions);
        btnRefresh.setOnClickListener(v -> refresh());

        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        llStrictList.removeAllViews();
        llAlmostList.removeAllViews();

        // 1. Fetch Strict Matches
        List<Recipe> strictMatches = db.getStrictMatches();
        if (strictMatches.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText(R.string.empty_strict_matches);
            emptyView.setTextSize(14);
            emptyView.setTextColor(Color.parseColor("#E65100"));
            emptyView.setBackgroundColor(Color.parseColor("#FFF3E0"));
            emptyView.setPadding(16, 16, 16, 16);
            llStrictList.addView(emptyView);
        } else {
            for (Recipe recipe : strictMatches) {
                View card = LayoutInflater.from(this).inflate(R.layout.item_recipe, llStrictList, false);
                TextView tvTitle = card.findViewById(R.id.tv_recipe_title);
                TextView tvSub = card.findViewById(R.id.tv_recipe_subtitle);

                tvTitle.setText(recipe.name);
                tvSub.setText(R.string.badge_ready_to_cook);
                tvSub.setTextColor(Color.parseColor("#2E7D32"));

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(this, RecipeDetailActivity.class);
                    intent.putExtra("recipe_id", recipe.id);
                    startActivity(intent);
                });
                llStrictList.addView(card);
            }
        }

        // 2. Fetch Almost There Matches
        List<Recipe.AlmostThereRecipe> almostMatches = db.getAlmostThereMatches();
        if (almostMatches.isEmpty()) {
            TextView emptyAlmost = new TextView(this);
            emptyAlmost.setText(R.string.empty_almost_there);
            emptyAlmost.setTextSize(14);
            emptyAlmost.setTextColor(Color.GRAY);
            emptyAlmost.setPadding(8, 8, 8, 8);
            llAlmostList.addView(emptyAlmost);
        } else {
            for (Recipe.AlmostThereRecipe item : almostMatches) {
                View card = LayoutInflater.from(this).inflate(R.layout.item_recipe, llAlmostList, false);
                TextView tvTitle = card.findViewById(R.id.tv_recipe_title);
                TextView tvSub = card.findViewById(R.id.tv_recipe_subtitle);

                tvTitle.setText(item.recipe.name);
                tvSub.setText(getString(R.string.badge_missing_prefix, item.missingInfo));
                tvSub.setTextColor(Color.parseColor("#D84315"));

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(this, RecipeDetailActivity.class);
                    intent.putExtra("recipe_id", item.recipe.id);
                    startActivity(intent);
                });
                llAlmostList.addView(card);
            }
        }
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
        if (navRecipes != null) navRecipes.setOnClickListener(v -> {});
        if (navSettings != null) navSettings.setOnClickListener(v -> {
            Intent i = new Intent(this, SettingsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        });
    }
}
