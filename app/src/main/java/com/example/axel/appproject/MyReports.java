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
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Viktor on 2015-05-15.
 */
public class MyReports extends Activity {

    ExpandableListView scrollview;
    ExpandableListAdapter scrolladapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<NameValuePair> idList;
    List<Address> addresses;
    Geocoder geocoder;
    ProgressDialog progressDialog;

    String id;
    String header;
    String address;
    String status;
    String description;
    String feedback;

    double longitude, latitude;

    ImageButton btnHelp;
    ImageButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_reports);

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
                startActivity(new Intent(MyReports.this, IssueReportMapLocation.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        idList = new ArrayList<NameValuePair>();
        idList.add(new BasicNameValuePair("Action","getMyReports"));
        geocoder = new Geocoder(this, Locale.getDefault());

        scrollview = (ExpandableListView) findViewById(R.id.scrollview);
        scrolladapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        scrollview.setAdapter(scrolladapter);

        StringBuffer buffer = new StringBuffer("");

        try {
            File dir = getDir("MyReports", MODE_PRIVATE);
            File[] files = dir.listFiles();
            int i = 0;
            for (File f:files) {
                FileInputStream fis = new FileInputStream(dir.toString() + "/" + f.getName());
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr);

                String readString = reader.readLine();
                while (readString != null) {
                    buffer.append(readString);
                    readString = reader.readLine();
                }
                idList.add(new BasicNameValuePair(String.valueOf(i), buffer.toString()));
                isr.close();
                buffer = new StringBuffer("");
                i++;
            }
            idList.add(new BasicNameValuePair("Length",String.valueOf(files.length)));
            new getMyReports(this).execute(new PostGetDB(this,idList));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAllReports(JSONArray jsonArray) {
        for (int i=0; i<jsonArray.length();i++) {

            JSONObject jsonObject = null;
            List<String> information = new ArrayList<>();
            try {
                jsonObject = jsonArray.getJSONObject(i);
                id = "ID: " + jsonObject.getString("UniqueID");
                description = "Beskrivning: " + jsonObject.getString("Description");
                status = jsonObject.getString("Status_muni");
                feedback = "Återkoppling: " + jsonObject.getString("Comment_muni");
                header = jsonObject.getString("Timestamp") + "\n"
                        +"Kategori: " + jsonObject.getString("Category") + "\n"
                        +"Status: " + status;
                longitude = Double.valueOf(jsonObject.getString("Longitude"));
                latitude = Double.valueOf(jsonObject.getString("Latitude"));
                addresses = geocoder.getFromLocation(latitude,longitude,1);
                address = "Adress: " + addresses.get(0).getAddressLine(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }  catch (IOException e) {
                e.printStackTrace();
            }

            listDataHeader.add(header);
            information.add(address);
            information.add(feedback);
            information.add(description);

            listDataChild.put(listDataHeader.get(i), information);

            scrolladapter.notifyDataSetChanged();
            scrolladapter.notifyDataSetInvalidated();

        }
    }

    private class getMyReports extends AsyncTask<PostGetDB,Void,JSONArray> {

        Context MyReports;

        private getMyReports(Context context) {
            MyReports = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MyReports);
            progressDialog.setMessage("Laddar dina rapporter...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
        @Override
        protected JSONArray doInBackground(PostGetDB... params) {
            return params[0].getAllPosts();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            getAllReports(jsonArray);
            progressDialog.dismiss();
        }
    }

    private void helpDialog() {
        AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(this);
        welcomeDialog.setMessage(R.string.my_reports_help);
        welcomeDialog.setTitle(R.string.help);
        welcomeDialog.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        welcomeDialog.show();
    }
}
