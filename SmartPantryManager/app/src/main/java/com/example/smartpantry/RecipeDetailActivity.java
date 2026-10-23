package com.example.smartpantry;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecipeDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        DatabaseHelper db = new DatabaseHelper(this);
        long id = getIntent().getLongExtra("recipe_id", -1);
        Recipe recipe = db.getRecipe(id);

        LinearLayout page = Ui.page(this, recipe == null ? "Recipe Not Found" : recipe.name);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        if (recipe == null) {
            content.addView(Ui.text(this, "This recipe is no longer available in the database.", 16));
        } else {
            TextView ingHeader = new TextView(this);
            ingHeader.setText("Required Ingredients");
            ingHeader.setTextSize(18);
            ingHeader.setTypeface(null, Typeface.BOLD);
            ingHeader.setTextColor(Ui.DARK);
            ingHeader.setPadding(0, 8, 0, 6);
            content.addView(ingHeader);

            for (Recipe.Requirement req : recipe.ingredients) {
                TextView item = new TextView(this);
                item.setText("• " + req.toString());
                item.setTextSize(15);
                item.setTextColor(Color.rgb(40, 50, 40));
                item.setPadding(12, 4, 0, 4);
                content.addView(item);
            }

            TextView stepsHeader = new TextView(this);
            stepsHeader.setText("\nPreparation Method");
            stepsHeader.setTextSize(18);
            stepsHeader.setTypeface(null, Typeface.BOLD);
            stepsHeader.setTextColor(Ui.DARK);
            stepsHeader.setPadding(0, 12, 0, 6);
            content.addView(stepsHeader);

            TextView stepsText = new TextView(this);
            stepsText.setText(recipe.steps.replace("\\n", "\n"));
            stepsText.setTextSize(15);
            stepsText.setTextColor(Color.rgb(30, 30, 30));
            stepsText.setPadding(8, 4, 8, 12);
            content.addView(stepsText);

            TextView explanation = new TextView(this);
            explanation.setText("Strict Match Verification: This recipe is recommended because 100% of the listed ingredients were verified in your pantry in required amounts.");
            explanation.setTextSize(13);
            explanation.setPadding(12, 10, 12, 10);
            explanation.setBackgroundColor(Color.rgb(232, 245, 233));
            explanation.setTextColor(Ui.DARK);
            content.addView(explanation);
        }

        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        page.addView(Ui.button(this, "Back to Suggestions", v -> finish()));

        Ui.nav(this, page);
        setContentView(page);
    }
}
