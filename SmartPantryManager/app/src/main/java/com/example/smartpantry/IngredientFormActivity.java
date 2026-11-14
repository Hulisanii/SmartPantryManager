package com.example.smartpantry;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for Adding or Editing an Ingredient in the Pantry SQLite database.
 */
public class IngredientFormActivity extends Activity {
    private DatabaseHelper db;
    private EditText etName, etQuantity, etUnit, etExpiry;
    private TextView tvTitle;
    private long ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        db = new DatabaseHelper(this);
        ingredientId = getIntent().getLongExtra("ingredient_id", -1);

        tvTitle = findViewById(R.id.tv_form_title);
        etName = findViewById(R.id.et_name);
        etQuantity = findViewById(R.id.et_quantity);
        etUnit = findViewById(R.id.et_unit);
        etExpiry = findViewById(R.id.et_expiry);

        Button btnSave = findViewById(R.id.btn_save);
        Button btnCancel = findViewById(R.id.btn_cancel);

        if (ingredientId != -1) {
            tvTitle.setText(R.string.title_edit_ingredient);
            Ingredient item = db.getIngredient(ingredientId);
            if (item != null) {
                etName.setText(item.name);
                etQuantity.setText(item.getFormattedQuantity());
                etUnit.setText(item.unit);
                etExpiry.setText(item.expiryDate);
            }
        } else {
            tvTitle.setText(R.string.title_add_ingredient);
        }

        // Setup quick units buttons
        LinearLayout quickUnitsContainer = findViewById(R.id.ll_quick_units_container);
        String[] commonUnits = {"piece", "g", "kg", "ml", "l", "clove", "slice", "tbsp", "pinch", "can"};
        for (String u : commonUnits) {
            Button chip = new Button(this);
            chip.setText(u);
            chip.setTextSize(11);
            chip.setOnClickListener(v -> etUnit.setText(u));
            quickUnitsContainer.addView(chip);
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> save());
        btnCancel.setOnClickListener(v -> finish());

        setupNavigation();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String formatted = String.format(Locale.ROOT, "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    etExpiry.setText(formatted);
                }, year, month, day);
        dpd.show();
    }

    private void save() {
        String n = etName.getText().toString().trim();
        String q = etQuantity.getText().toString().trim();
        String u = etUnit.getText().toString().trim();
        String exp = etExpiry.getText().toString().trim();

        if (n.isEmpty()) {
            Toast.makeText(this, R.string.err_name_required, Toast.LENGTH_LONG).show();
            etName.requestFocus();
            return;
        }

        if (q.isEmpty()) {
            Toast.makeText(this, R.string.err_quantity_required, Toast.LENGTH_LONG).show();
            etQuantity.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(q);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.err_quantity_numeric, Toast.LENGTH_LONG).show();
            etQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            Toast.makeText(this, R.string.err_quantity_positive, Toast.LENGTH_LONG).show();
            etQuantity.requestFocus();
            return;
        }

        if (u.isEmpty()) {
            Toast.makeText(this, R.string.err_unit_required, Toast.LENGTH_LONG).show();
            etUnit.requestFocus();
            return;
        }

        long result;
        if (ingredientId == -1) {
            result = db.addIngredient(n, amount, u, exp);
        } else {
            result = db.updateIngredient(ingredientId, n, amount, u, exp) > 0 ? ingredientId : -1;
        }

        if (result >= 0) {
            Toast.makeText(this, R.string.msg_ingredient_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.err_save_failed, Toast.LENGTH_LONG).show();
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
