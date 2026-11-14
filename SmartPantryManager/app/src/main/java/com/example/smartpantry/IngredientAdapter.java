package com.example.smartpantry;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom ArrayAdapter implementation for binding Ingredient data models to item_pantry_ingredient XML views.
 */
public class IngredientAdapter extends ArrayAdapter<Ingredient> {
    private final boolean showExpiryAlerts;

    public IngredientAdapter(Context context, List<Ingredient> ingredients, boolean showExpiryAlerts) {
        super(context, R.layout.item_pantry_ingredient, ingredients);
        this.showExpiryAlerts = showExpiryAlerts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_pantry_ingredient, parent, false);
        }

        Ingredient item = getItem(position);
        if (item != null) {
            TextView tvName = view.findViewById(R.id.tv_ingredient_name);
            TextView tvDetails = view.findViewById(R.id.tv_ingredient_details);
            TextView tvBadge = view.findViewById(R.id.tv_expiry_badge);

            tvName.setText(item.name);

            String detailStr = "Quantity: " + item.getFormattedQuantity() + " " + item.unit;
            if (item.expiryDate != null && !item.expiryDate.isEmpty()) {
                detailStr += "  |  Expires: " + item.expiryDate;
            }
            tvDetails.setText(detailStr);

            if (showExpiryAlerts && item.isExpiringSoonOrExpired()) {
                tvBadge.setVisibility(View.VISIBLE);
                tvBadge.setText("⚠️ Expiring");
                tvBadge.setTextColor(Color.parseColor("#C62828"));
                tvBadge.setBackgroundColor(Color.parseColor("#FFEBEE"));
            } else {
                tvBadge.setVisibility(View.GONE);
            }
        }

        return view;
    }
}
