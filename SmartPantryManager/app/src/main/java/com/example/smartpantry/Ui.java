package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class Ui {
    private Ui() {}

    public static final int GREEN = Color.rgb(46, 125, 50);
    public static final int DARK = Color.rgb(27, 94, 32);
    public static final int PAD = 18;

    public static LinearLayout page(Activity a, String title) {
        LinearLayout root = new LinearLayout(a);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(PAD, PAD, PAD, PAD);
        root.setBackgroundColor(Color.rgb(248, 250, 247));

        TextView heading = new TextView(a);
        heading.setText(title);
        heading.setTextSize(24);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setTextColor(DARK);
        heading.setPadding(0, 0, 0, 12);
        root.addView(heading, new LinearLayout.LayoutParams(-1, -2));

        return root;
    }

    public static TextView text(Activity a, String value, int size) {
        TextView t = new TextView(a);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.rgb(40, 50, 40));
        t.setPadding(0, 6, 0, 6);
        return t;
    }

    public static Button button(Activity a, String label, View.OnClickListener listener) {
        Button b = new Button(a);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(ColorStateList.valueOf(GREEN));
        b.setOnClickListener(listener);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);
        b.setLayoutParams(p);
        return b;
    }

    public static void nav(Activity a, LinearLayout root) {
        View line = new View(a);
        line.setBackgroundColor(Color.LTGRAY);
        root.addView(line, new LinearLayout.LayoutParams(-1, 2));

        LinearLayout row = new LinearLayout(a);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 0);

        String[] labels = {"Pantry", "Recipes", "Settings"};
        Class<?>[] targets = {MainActivity.class, SuggestedRecipesActivity.class, SettingsActivity.class};

        for (int i = 0; i < labels.length; i++) {
            final Class<?> target = targets[i];
            Button b = new Button(a);
            b.setText(labels[i]);
            b.setAllCaps(false);
            b.setTextSize(13);

            boolean isCurrent = a.getClass().equals(target);
            if (isCurrent) {
                b.setTypeface(null, Typeface.BOLD);
                b.setTextColor(DARK);
                b.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(220, 237, 220)));
            } else {
                b.setTextColor(Color.DKGRAY);
                b.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(240, 240, 240)));
            }

            b.setOnClickListener(v -> {
                if (!isCurrent) {
                    Intent intent = new Intent(a, target);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    a.startActivity(intent);
                }
            });

            row.addView(b, new LinearLayout.LayoutParams(0, -2, 1));
        }
        root.addView(row, new LinearLayout.LayoutParams(-1, -2));
    }
}
