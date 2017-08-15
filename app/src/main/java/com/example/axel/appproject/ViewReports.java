package com.example.axel.appproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * Created by Axel on 2015-05-04.
 */

public class ViewReports extends Activity implements AdapterView.OnItemSelectedListener {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Geocoder geocoder;
    ProgressDialog progressDialog;

    Spinner spinner_sort_categories;
    String selectedItem = "Nyast först";

    public Integer chosenURL;

    int addIdxForHeader;
    int dbIdxUpdate;
    int updateListOnScrollNumber;

    String actionString;
    Boolean isLoadingData;

    ImageButton btnHelp;
    ImageButton btnHome;

    List<NameValuePair> idxGetMore = new ArrayList<NameValuePair>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_reports);

        spinner_sort_categories = (Spinner) findViewById(R.id.spinner_sort_categories);
        createSpinnerDropDown();

        chosenURL = 1;
        isLoadingData = false;
        updateListOnScrollNumber = 2;

        //INDEX FOR HEADER AND FOR DB UPDATE
        addIdxForHeader = 0;
        dbIdxUpdate = 10;

        btnHelp = (ImageButton) findViewById(R.id.help_button);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog();
            }
        });

        btnHome = (ImageButton) findViewById(R.id.home_button);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReports.this, MainActivity.class));
            }
        });


        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expandableListView = (ExpandableListView) findViewById(R.id.mainExpandableListView);

        geocoder = new Geocoder(this, Locale.getDefault());

        expandableListView.setOnScrollListener(new InfiniteScrollListener(updateListOnScrollNumber) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                if (listDataHeader.size() > 9 && !isLoadingData) {
                    addIdxForHeader += 10;
                    idxGetMore.add(new BasicNameValuePair("updateIndex", Integer.toString(dbIdxUpdate)));
                    new getMyReports().execute(new PostGetDB(ViewReports.this, idxGetMore));

                    dbIdxUpdate += 10;
                    isLoadingData = true;
                }

            }
        });
        expandableListAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(expandableListAdapter);




    }

    public void setTextToTextView(JSONArray jsonArray) {

        String timestamp_category = "";
        String description  = "";
        String status = "";
        List<Address> addressResponse;
        String address = "";
        double latitude;
        double longitude;

        for (int i=0; i<jsonArray.length();i++) {

            JSONObject jsonObject = null;
            List<String> information = new ArrayList<>();
            try {
                jsonObject = jsonArray.getJSONObject(i);
                timestamp_category = jsonObject.getString("Timestamp") + "\n" +"Kategori: " + jsonObject.getString("Category");
                description = "Beskrivning: " + jsonObject.getString("Description");
                latitude = jsonObject.getDouble("Latitude");
                longitude = jsonObject.getDouble("Longitude");
                status = "Status: " + jsonObject.getString("Status_muni");
                addressResponse = geocoder.getFromLocation(latitude,longitude,1);
                address = addressResponse.get(0).getAddressLine(0) + ", " +
                        addressResponse.get(0).getPostalCode() + " " + addressResponse.get(0).getLocality();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            listDataHeader.add(timestamp_category);
            information.add(address);
            information.add(description);
            information.add(status);
            System.out.println("addIdxForHeader: " + addIdxForHeader);
            listDataChild.put(listDataHeader.get(i+addIdxForHeader), information);

            expandableListAdapter.notifyDataSetChanged();
            expandableListAdapter.notifyDataSetInvalidated();
            isLoadingData = false;

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = parent.getItemAtPosition(position).toString();
        if (selectedItem.equals("Nyast först")) {
            chosenURL = 1;
            actionString = "loadReports";
            clearListGetNewPosts();
            actionString = "moreReports";
            clearBasicNameValue();


        }
        else if (selectedItem.equals("Cykel")) {
            chosenURL = 2;
            actionString = "loadBike";
            clearListGetNewPosts();
            actionString = "moreBike";
            clearBasicNameValue();

        }
        else if (selectedItem.equals("Renhållning")) {
            chosenURL = 3;
            actionString = "loadClean";
            clearListGetNewPosts();
            actionString = "moreClean";
            clearBasicNameValue();

        }
        else if (selectedItem.equals("Allmänna platser")) {
            chosenURL = 4;
            actionString = "loadPublic";
            clearListGetNewPosts();
            actionString = "morePublic";
            clearBasicNameValue();
        }
        else if (selectedItem.equals("Övrigt")) {
            chosenURL = 5;
            actionString = "loadOther";
            clearListGetNewPosts();
            actionString="moreOther";
            clearBasicNameValue();

        }
        else if (selectedItem.equals("Vägar")) {
            chosenURL = 6;
            actionString = "loadRoad";
            clearListGetNewPosts();
            actionString="moreRoad";
            clearBasicNameValue();

        }
        else if (selectedItem.equals("Vegetation")) {
            chosenURL = 7;
            actionString = "loadVegetation";
            clearListGetNewPosts();
            actionString = "moreVegetation";
            clearBasicNameValue();

        }
        else if (selectedItem.equals("Trafik")) {
            chosenURL = 8;
            actionString = "loadTraffic";
            clearListGetNewPosts();
            actionString = "moreTraffic";
            clearBasicNameValue();

        }
        else if (selectedItem.equals("Klotter")) {
            chosenURL = 9;
            actionString = "loadKlotter";
            clearListGetNewPosts();
            actionString = "moreKlotter";
            clearBasicNameValue();

        }
        Toast.makeText(parent.getContext(), "Sorterar efter " + selectedItem,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private class getAllPostsTask extends AsyncTask<GetPostsFromDB, Integer, JSONArray> {
        Context ViewReports;

        private getAllPostsTask(Context context) {
            ViewReports = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewReports);
            progressDialog.setMessage("Laddar rapporter...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
        @Override
        protected JSONArray doInBackground(GetPostsFromDB... params) {
            return params[0].GetAllPosts(chosenURL);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setTextToTextView(jsonArray);
            progressDialog.dismiss();
        }
    }



    private void createSpinnerDropDown() {

        //String list of issue categories to display in the spinner
        String[] categoriesTEST = new String[] {"Nyast först", "Cykel", "Trafik",
                "Klotter", "Vägar", "Renhållning", "Vegetation", "Allmänna platser", "Övrigt"};

        //Create image list
        Integer[] categoryIcons = {R.drawable.hourglass_icon, R.drawable.bike_icon, R.drawable.traffic_icon,
                R.drawable.spray_icon, R.drawable.road_icon, R.drawable.garbage_icon,
                R.drawable.tree_icon, R.drawable.park_icon ,R.drawable.other_icon};

        spinner_sort_categories.setAdapter(new SpinnerAdapter(this, R.layout.view_reports_spinner_icon,
                categoriesTEST, categoryIcons));

        spinner_sort_categories.setOnItemSelectedListener(this);

    }

    public void clearListGetNewPosts() {
        addIdxForHeader = 0;
        dbIdxUpdate = 10;
        listDataChild.clear();
        listDataHeader.clear();
        expandableListAdapter.notifyDataSetChanged();
        expandableListAdapter.notifyDataSetInvalidated();

        idxGetMore.add(new BasicNameValuePair("Action", actionString));
        new getMyReports().execute(new PostGetDB(ViewReports.this, idxGetMore));
    }

    public void clearBasicNameValue() {
        idxGetMore.clear();
        idxGetMore.add(new BasicNameValuePair("Action", actionString));
    }

    private class getMyReports extends AsyncTask<PostGetDB, Long, JSONArray> {

        @Override
        protected JSONArray doInBackground(PostGetDB... params) {
            return params[0].getAllPosts();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) { setTextToTextView(jsonArray);}
    }



    private void helpDialog() {
        AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(this);
        welcomeDialog.setMessage(R.string.view_reports_help);
        welcomeDialog.setTitle(R.string.help);
        welcomeDialog.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        welcomeDialog.show();
    }

}

