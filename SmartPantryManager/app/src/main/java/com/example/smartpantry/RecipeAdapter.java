package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom ArrayAdapter implementation for binding Recipe data models to item_recipe XML views.
 */
public class RecipeAdapter extends ArrayAdapter<Recipe> {

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, R.layout.item_recipe, recipes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_recipe, parent, false);
        }

        Recipe item = getItem(position);
        if (item != null) {
            TextView tvTitle = view.findViewById(R.id.tv_recipe_title);
            TextView tvSubtitle = view.findViewById(R.id.tv_recipe_subtitle);

            tvTitle.setText(item.name);
            tvSubtitle.setText(R.string.badge_ready_to_cook);
            tvSubtitle.setTextColor(getContext().getResources().getColor(R.color.colorSuccessText));
        }

        return view;
    }
}
