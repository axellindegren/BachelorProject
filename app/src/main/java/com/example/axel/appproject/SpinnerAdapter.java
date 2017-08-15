package com.example.axel.appproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class SpinnerAdapter extends ArrayAdapter {

    private Context context;
    private int textViewResourceId;
    private String[] spinnerText;
    private Integer[] categoryIcons;

    public SpinnerAdapter(Context context, int textViewResourceId,
                         String[] spinnerText, Integer[] categoryIcons) {
        super(context, textViewResourceId, spinnerText);
        this.textViewResourceId = textViewResourceId;
        this.context = context;
        this.spinnerText = spinnerText;
        this.categoryIcons = categoryIcons;
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            // Inflating the layout for the custom Spinner
            LayoutInflater spinnerInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = spinnerInflater.inflate(R.layout.view_reports_spinner_icon, parent, false);

            // Declaring and Typecasting the textview in the inflated layout
            TextView tvLanguage = (TextView) layout
                    .findViewById(R.id.tvLanguage);

            // Setting the text using the array
            tvLanguage.setText(spinnerText[position]);

            // Setting the color of the text
            tvLanguage.setTextColor(Color.rgb(75, 180, 225));

            // Declaring and Typecasting the imageView in the inflated layout
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);

            // Setting an image using the id's in the array
            img.setImageResource(categoryIcons[position]);

            tvLanguage.setTextSize(18f);

            return layout;
        }

        // It gets a View that displays in the drop down popup the data at the specified position
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // It gets a View that displays the data at the specified position
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }


}

