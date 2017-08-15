package com.example.axel.appproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viktor on 2015-05-08.
 */
public class descView extends View {

    View rootView;
    RadioGroup radio;
    EditText descText;
    Button done;
    String description = "";
    Spinner spinner;
    String selectedItem;

    public descView(Context context, ViewFlipper container, LayoutInflater inflater) {

        super(context);
        rootView = inflater.inflate(R.layout.desc_view,container,false);
        descText = (EditText) rootView.findViewById(R.id.desc_text);
        radio = (RadioGroup) rootView.findViewById(R.id.radio);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        createSpinnerDropDown(context, spinner);
    }

    private void createSpinnerDropDown(Context context, Spinner spinner) {

        //Array list of issue categories to display in the spinner
        List<String> categories = new ArrayList<String>();
        categories.add("Övrigt");
        categories.add("Trafik");
        categories.add("Klotter");
        categories.add("Vägar");
        categories.add("Renhållning");
        categories.add("Cykel");
        categories.add("Vegetation");
        categories.add("Allmänna platser");
        categories.add("Välj En Kategori");
        //create an ArrayAdaptar from the String Array
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position,convertView,parent);
                if(position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        //set the view for the Drop down list
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the ArrayAdapter to the spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getCount());
        //attach the listener to the spinner

    }

    public String getDesc() {
        return descText.getText().toString();
    }

    public View getRootView() {
        return rootView;
    }

    public String getSelectedItem() {
        return selectedItem;
    }
}