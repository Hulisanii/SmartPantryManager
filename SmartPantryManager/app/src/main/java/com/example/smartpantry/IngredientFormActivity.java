package com.example.smartpantry;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class IngredientFormActivity extends Activity {
    private DatabaseHelper db;
    private EditText nameField, quantityField, unitField, expiryField;
    private long ingredientId = -1;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DatabaseHelper(this);
        ingredientId = getIntent().getLongExtra("ingredient_id", -1);

        LinearLayout root = Ui.page(this, ingredientId == -1 ? "Add Ingredient" : "Edit Ingredient");

        root.addView(label("Ingredient Name:"));
        nameField = field("e.g. Tomato, Egg, Oats, Cheese", InputType.TYPE_CLASS_TEXT, root);

        root.addView(label("Quantity:"));
        quantityField = field("e.g. 2, 200, 0.5", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, root);

        root.addView(label("Unit:"));
        unitField = field("e.g. piece, g, kg, ml, l, clove, slice, tbsp, pinch, can", InputType.TYPE_CLASS_TEXT, root);

        // Quick unit chips row
        LinearLayout unitChips = new LinearLayout(this);
        unitChips.setOrientation(LinearLayout.HORIZONTAL);
        unitChips.setPadding(0, 0, 0, 8);
        String[] commonUnits = {"piece", "g", "kg", "ml", "l", "clove", "slice", "tbsp", "pinch", "can"};
        for (String u : commonUnits) {
            Button chip = new Button(this);
            chip.setText(u);
            chip.setTextSize(11);
            chip.setPadding(4, 2, 4, 2);
            chip.setOnClickListener(v -> unitField.setText(u));
            unitChips.addView(chip, new LinearLayout.LayoutParams(0, -2, 1));
        }
        root.addView(unitChips);

        root.addView(label("Expiry Date (Optional):"));
        expiryField = field("Tap to select date (YYYY-MM-DD)", InputType.TYPE_NULL, root);
        expiryField.setFocusable(false);
        expiryField.setOnClickListener(v -> showDatePicker());

        if (ingredientId != -1) {
            Ingredient item = db.getIngredient(ingredientId);
            if (item != null) {
                nameField.setText(item.name);
                quantityField.setText(item.getFormattedQuantity());
                unitField.setText(item.unit);
                expiryField.setText(item.expiryDate);
            }
        }

        root.addView(Ui.button(this, "Save Ingredient", v -> save()));
        root.addView(Ui.button(this, "Cancel", v -> finish()));

        Ui.nav(this, root);
        setContentView(root);
    }

    private TextView label(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(Color.rgb(60, 70, 60));
        tv.setPadding(0, 8, 0, 2);
        return tv;
    }

    private EditText field(String hint, int inputType, LinearLayout root) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setHint(hint);
        e.setInputType(inputType);
        e.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 2, 0, 8);
        root.addView(e, p);
        return e;
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String formatted = String.format(Locale.ROOT, "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    expiryField.setText(formatted);
                }, year, month, day);
        dpd.show();
    }

    private void save() {
        String n = nameField.getText().toString().trim();
        String q = quantityField.getText().toString().trim();
        String u = unitField.getText().toString().trim();
        String exp = expiryField.getText().toString().trim();

        if (n.isEmpty()) {
            Toast.makeText(this, "Validation Error: Ingredient name is required.", Toast.LENGTH_LONG).show();
            nameField.requestFocus();
            return;
        }

        if (q.isEmpty()) {
            Toast.makeText(this, "Validation Error: Quantity is required.", Toast.LENGTH_LONG).show();
            quantityField.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(q);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Validation Error: Quantity must be a valid numeric value.", Toast.LENGTH_LONG).show();
            quantityField.requestFocus();
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            Toast.makeText(this, "Validation Error: Quantity must be greater than zero.", Toast.LENGTH_LONG).show();
            quantityField.requestFocus();
            return;
        }

        if (u.isEmpty()) {
            Toast.makeText(this, "Validation Error: Unit is required.", Toast.LENGTH_LONG).show();
            unitField.requestFocus();
            return;
        }

        long result;
        if (ingredientId == -1) {
            result = db.addIngredient(n, amount, u, exp);
        } else {
            result = db.updateIngredient(ingredientId, n, amount, u, exp) > 0 ? ingredientId : -1;
        }

        if (result >= 0) {
            Toast.makeText(this, "Ingredient saved successfully.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Database Error: Could not save ingredient. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
