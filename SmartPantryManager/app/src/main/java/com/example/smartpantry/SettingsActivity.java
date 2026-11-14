package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Activity for App Settings & Preferences, Expiry Alerts Toggle, and Database Justification.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        Switch switchAlerts = findViewById(R.id.switch_expiry_alerts);
        switchAlerts.setChecked(prefs.getBoolean("expiry_alerts", true));
        switchAlerts.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean("expiry_alerts", checked).apply();
            Toast.makeText(this, R.string.msg_ingredient_saved, Toast.LENGTH_SHORT).show();
        });

        Button btnBack = findViewById(R.id.btn_back_to_pantry);
        btnBack.setOnClickListener(v -> finish());

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
        if (navSettings != null) navSettings.setOnClickListener(v -> {});
    }
}
