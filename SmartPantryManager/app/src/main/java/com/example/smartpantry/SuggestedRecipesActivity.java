package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class SuggestedRecipesActivity extends Activity {
    private DatabaseHelper db;
    private LinearLayout contentContainer;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DatabaseHelper(this);

        LinearLayout page = Ui.page(this, "Suggested Recipes");

        ScrollView scroll = new ScrollView(this);
        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(contentContainer);

        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        page.addView(Ui.button(this, "Refresh Suggestions", v -> refresh()));

        Ui.nav(this, page);
        setContentView(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        contentContainer.removeAllViews();

        TextView intro = Ui.text(this, "Strict Matching Rule: Suggestions include ONLY recipes where 100% of required ingredients are present in your pantry in sufficient quantity.", 14);
        contentContainer.addView(intro);

        // --- SECTION 1: STRICT MATCHES ---
        TextView strictHeader = new TextView(this);
        strictHeader.setText("Suggested Recipes (Ready to Cook)");
        strictHeader.setTextSize(18);
        strictHeader.setTypeface(null, Typeface.BOLD);
        strictHeader.setTextColor(Ui.DARK);
        strictHeader.setPadding(0, 12, 0, 8);
        contentContainer.addView(strictHeader);

        List<Recipe> strictMatches = db.getStrictMatches();

        if (strictMatches.isEmpty()) {
            TextView emptyText = Ui.text(this, "No recipes match your pantry yet — add more ingredients or increase quantities to see suggestions.", 15);
            emptyText.setTextColor(Color.rgb(180, 80, 0));
            emptyText.setPadding(12, 12, 12, 12);
            emptyText.setBackgroundColor(Color.rgb(255, 243, 224));
            contentContainer.addView(emptyText);
        } else {
            for (Recipe recipe : strictMatches) {
                LinearLayout card = createRecipeCard(recipe.name, "✓ All ingredients available in pantry", Ui.GREEN, v -> {
                    Intent intent = new Intent(this, RecipeDetailActivity.class);
                    intent.putExtra("recipe_id", recipe.id);
                    startActivity(intent);
                });
                contentContainer.addView(card);
            }
        }

        // --- SECTION 2: ALMOST THERE (MISSING 1 INGREDIENT) ---
        TextView almostHeader = new TextView(this);
        almostHeader.setText("\nAlmost There (Missing 1 Ingredient)");
        almostHeader.setTextSize(18);
        almostHeader.setTypeface(null, Typeface.BOLD);
        almostHeader.setTextColor(Color.rgb(180, 100, 0));
        almostHeader.setPadding(0, 16, 0, 8);
        contentContainer.addView(almostHeader);

        List<Recipe.AlmostThereRecipe> almostMatches = db.getAlmostThereMatches();

        if (almostMatches.isEmpty()) {
            TextView emptyAlmost = Ui.text(this, "No 'Almost There' recipes found.", 14);
            emptyAlmost.setTextColor(Color.GRAY);
            contentContainer.addView(emptyAlmost);
        } else {
            for (Recipe.AlmostThereRecipe item : almostMatches) {
                LinearLayout card = createRecipeCard(item.recipe.name, "Missing: " + item.missingInfo, Color.rgb(216, 67, 21), v -> {
                    Intent intent = new Intent(this, RecipeDetailActivity.class);
                    intent.putExtra("recipe_id", item.recipe.id);
                    startActivity(intent);
                });
                contentContainer.addView(card);
            }
        }
    }

    private LinearLayout createRecipeCard(String title, String subtitle, int accentColor, View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 14, 18, 14);
        card.setBackgroundColor(Color.WHITE);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(listener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 4, 0, 8);
        card.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextColor(Color.rgb(30, 40, 30));

        TextView subView = new TextView(this);
        subView.setText(subtitle);
        subView.setTextSize(13);
        subView.setTextColor(accentColor);

        card.addView(titleView);
        card.addView(subView);

        return card;
    }
}
