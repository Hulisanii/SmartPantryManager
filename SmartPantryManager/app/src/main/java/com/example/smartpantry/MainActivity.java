package com.example.smartpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {
    private DatabaseHelper db;
    private LinearLayout root;
    private ListView list;
    private TextView alertBanner;
    private TextView emptyStateText;
    private List<Ingredient> items;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DatabaseHelper(this);

        root = Ui.page(this, "Smart Pantry Manager");

        root.addView(Ui.text(this, "Track what you have. Suggested recipes use strictly ingredients available in your pantry.", 14));

        alertBanner = new TextView(this);
        alertBanner.setTextSize(13);
        alertBanner.setPadding(16, 12, 16, 12);
        alertBanner.setBackgroundColor(Color.rgb(255, 235, 238));
        alertBanner.setTextColor(Color.rgb(198, 40, 40));
        alertBanner.setTypeface(null, Typeface.BOLD);
        alertBanner.setVisibility(View.GONE);
        LinearLayout.LayoutParams alertParams = new LinearLayout.LayoutParams(-1, -2);
        alertParams.setMargins(0, 4, 0, 8);
        root.addView(alertBanner, alertParams);

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

        Button addBtn = Ui.button(this, "+ Add Ingredient", v ->
                startActivity(new Intent(this, IngredientFormActivity.class)));
        Button recipesBtn = Ui.button(this, "Find Recipes I Can Make", v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        buttonRow.addView(addBtn, new LinearLayout.LayoutParams(0, -2, 1));
        buttonRow.addView(recipesBtn, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(buttonRow);

        emptyStateText = Ui.text(this, "Your pantry is currently empty. Tap '+ Add Ingredient' above to get started!", 15);
        emptyStateText.setTextColor(Color.GRAY);
        emptyStateText.setPadding(16, 32, 16, 32);
        emptyStateText.setVisibility(View.GONE);
        root.addView(emptyStateText);

        list = new ListView(this);
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));

        Ui.nav(this, root);
        setContentView(root);

        list.setOnItemClickListener((parent, view, position, id) -> {
            Ingredient item = items.get(position);
            String[] actions = {"Edit Ingredient", "Delete Ingredient", "Cancel"};
            new AlertDialog.Builder(this)
                    .setTitle(item.name)
                    .setItems(actions, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(this, IngredientFormActivity.class);
                            intent.putExtra("ingredient_id", item.id);
                            startActivity(intent);
                        } else if (which == 1) {
                            confirmDelete(item);
                        }
                    }).show();
        });
    }

    private void confirmDelete(Ingredient item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Are you sure you want to remove " + item.name + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteIngredient(item.id);
                    refresh();
                    Toast.makeText(this, item.name + " deleted", Toast.LENGTH_SHORT).show();
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db != null) {
            refresh();
        }
    }

    private void refresh() {
        items = db.getAllIngredients();

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean showExpiryAlerts = prefs.getBoolean("expiry_alerts", true);

        int expiringCount = 0;
        if (showExpiryAlerts) {
            for (Ingredient item : items) {
                if (item.isExpiringSoonOrExpired()) {
                    expiringCount++;
                }
            }
        }

        if (expiringCount > 0) {
            alertBanner.setText("⚠️ Alert: " + expiringCount + " ingredient(s) in your pantry are expiring soon or expired!");
            alertBanner.setVisibility(View.VISIBLE);
        } else {
            alertBanner.setVisibility(View.GONE);
        }

        if (items.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }

        // Custom Adapter with custom views for each pantry row
        ArrayAdapter<Ingredient> adapter = new ArrayAdapter<Ingredient>(this, 0, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.VERTICAL);
                row.setPadding(18, 16, 18, 16);

                Ingredient item = getItem(position);
                if (item != null) {
                    TextView title = new TextView(getContext());
                    title.setText(item.name);
                    title.setTextSize(17);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setTextColor(Color.rgb(30, 40, 30));

                    TextView detail = new TextView(getContext());
                    String detailStr = "Quantity: " + item.getFormattedQuantity() + " " + item.unit;
                    if (!item.expiryDate.isEmpty()) {
                        detailStr += "  |  Expires: " + item.expiryDate;
                    }
                    detail.setText(detailStr);
                    detail.setTextSize(14);

                    if (showExpiryAlerts && item.isExpiringSoonOrExpired()) {
                        detail.setTextColor(Color.rgb(198, 40, 40));
                        detail.setText(detailStr + " ⚠️");
                    } else {
                        detail.setTextColor(Color.rgb(100, 100, 100));
                    }

                    row.addView(title);
                    row.addView(detail);
                }
                return row;
            }
        };

        list.setAdapter(adapter);
    }
}
