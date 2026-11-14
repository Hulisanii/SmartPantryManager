package com.example.smartpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Main Activity representing the Pantry Inventory List screen.
 * Displays all pantry ingredients from the SQLite database using a custom IngredientAdapter.
 */
public class MainActivity extends Activity {
    private DatabaseHelper db;
    private ListView pantryListView;
    private TextView alertBanner;
    private TextView emptyStateText;
    private List<Ingredient> ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        // Bind XML views
        alertBanner = findViewById(R.id.alert_banner);
        emptyStateText = findViewById(R.id.empty_state_text);
        pantryListView = findViewById(R.id.pantry_list);

        Button btnAdd = findViewById(R.id.btn_add_ingredient);
        Button btnFindRecipes = findViewById(R.id.btn_find_recipes);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, IngredientFormActivity.class)));
        btnFindRecipes.setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        setupNavigation();

        // Handle item tap for Edit / Delete actions
        pantryListView.setOnItemClickListener((parent, view, position, id) -> {
            if (ingredientList != null && position < ingredientList.size()) {
                Ingredient item = ingredientList.get(position);
                String[] actions = {getString(R.string.action_edit), getString(R.string.action_delete), getString(R.string.btn_cancel)};

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
            }
        });
    }

    private void confirmDelete(Ingredient item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, item.name))
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.action_delete, (d, w) -> {
                    db.deleteIngredient(item.id);
                    refresh();
                    Toast.makeText(this, getString(R.string.msg_ingredient_deleted, item.name), Toast.LENGTH_SHORT).show();
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
        ingredientList = db.getAllIngredients();

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean showExpiryAlerts = prefs.getBoolean("expiry_alerts", true);

        int expiringCount = 0;
        if (showExpiryAlerts) {
            for (Ingredient item : ingredientList) {
                if (item.isExpiringSoonOrExpired()) {
                    expiringCount++;
                }
            }
        }

        if (expiringCount > 0) {
            alertBanner.setText(getString(R.string.expiry_alert_banner, expiringCount));
            alertBanner.setVisibility(View.VISIBLE);
        } else {
            alertBanner.setVisibility(View.GONE);
        }

        if (ingredientList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            pantryListView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            pantryListView.setVisibility(View.VISIBLE);
        }

        // Custom Adapter with layout inflation
        IngredientAdapter adapter = new IngredientAdapter(this, ingredientList, showExpiryAlerts);
        pantryListView.setAdapter(adapter);
    }

    private void setupNavigation() {
        Button navPantry = findViewById(R.id.nav_btn_pantry);
        Button navRecipes = findViewById(R.id.nav_btn_recipes);
        Button navSettings = findViewById(R.id.nav_btn_settings);

        if (navPantry != null) navPantry.setOnClickListener(v -> {});
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
