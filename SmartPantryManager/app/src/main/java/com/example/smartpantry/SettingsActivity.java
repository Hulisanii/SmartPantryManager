package com.example.smartpantry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = Ui.page(this, "Settings & Preferences");

        TextView section1 = new TextView(this);
        section1.setText("Preferences");
        section1.setTextSize(18);
        section1.setTypeface(null, Typeface.BOLD);
        section1.setTextColor(Ui.DARK);
        section1.setPadding(0, 4, 0, 8);
        root.addView(section1);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);

        Switch alerts = new Switch(this);
        alerts.setText("Show expiring-soon reminder on pantry screen");
        alerts.setTextSize(15);
        alerts.setPadding(0, 8, 0, 12);
        alerts.setChecked(prefs.getBoolean("expiry_alerts", true));
        alerts.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean("expiry_alerts", checked).apply();
            Toast.makeText(this, "Preference saved.", Toast.LENGTH_SHORT).show();
        });
        root.addView(alerts);

        TextView section2 = new TextView(this);
        section2.setText("\nData Storage Justification");
        section2.setTextSize(18);
        section2.setTypeface(null, Typeface.BOLD);
        section2.setTextColor(Ui.DARK);
        section2.setPadding(0, 12, 0, 6);
        root.addView(section2);

        TextView dbInfo = Ui.text(this, "Storage Method: Local SQLite Database via SQLiteOpenHelper.\n\nJustification: SQLite is ideal for Smart Pantry Manager because it guarantees full offline functionality, fast CRUD operations, and persistent data storage across app restarts without requiring cloud connectivity or server infrastructure.", 14);
        root.addView(dbInfo);

        TextView section3 = new TextView(this);
        section3.setText("\nUnit Matching & Rules");
        section3.setTextSize(18);
        section3.setTypeface(null, Typeface.BOLD);
        section3.setTextColor(Ui.DARK);
        section3.setPadding(0, 12, 0, 6);
        root.addView(section3);

        TextView unitInfo = Ui.text(this, "Automatic unit conversions are supported between metric mass (g <-> kg) and metric volume (ml <-> l). Count-based units (piece, clove, slice, tbsp, pinch, can) are matched directly with plural/singular normalization.", 14);
        root.addView(unitInfo);

        root.addView(Ui.button(this, "Back to Pantry", v -> finish()));

        Ui.nav(this, root);
        setContentView(root);
    }
}
