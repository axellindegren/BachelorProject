package com.example.axel.appproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by johansundstrom on 15-05-21.
 */
public class IssuesNearby extends Activity implements View.OnClickListener {

    TextView textView;
    Button buttonCurrentLocation;
    Button continueButton;
    Button abortButton;
    LatLng coords;
    GPSTracker gps;
    GoogleMap gMap;
    MapFragment mapFragment;
    String adress;
    String activeAdress;
    Marker activeMarker;
    Marker marker;
    Geocoder gCoder;
    List<Address> addressList;
    List<Address> activeAddresList;
    TextView tvAdress;
    JSONArray jsonArray = null;
    JSONObject jsonObject = null;

    LatLng currentPos;
    String lon;
    String lat;
    HashMap<Integer, ArrayList<String>> multiMap = new HashMap<>();
    ImageButton btnHelp;
    ImageButton btnHome;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues_nearby);
        getIssues();

        btnHelp = (ImageButton) findViewById(R.id.help_button);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpDialog();


            }
        });

        btnHome = (ImageButton) findViewById(R.id.home_button);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IssuesNearby.this, MainActivity.class));
            }
        });


    }

    private void helpDialog() {
        AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(this);
        welcomeDialog.setMessage(R.string.issues_nearby);
        welcomeDialog.setTitle(R.string.issues_near_wel);
        welcomeDialog.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        welcomeDialog.show();
    }

    protected void onResume() {
        super.onResume();
        createGPS();
    }

    public void getIssues(){
        try {
            jsonArray = new JSONArray(getIntent().getStringExtra("Issues"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent coordIntent = getIntent();
        Bundle extrasBundle = coordIntent.getExtras();
        lat = extrasBundle.getString("Latitude");
        lon = extrasBundle.getString("Longitude");

        currentPos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

    }

    public void putIssuesOnMap() {
        String longitude = "";
        String latitude = "";
        String description = "";
        String category = "";

        for (int i = 0; i < jsonArray.length(); i++) {

            jsonObject = null;

            try {
                jsonObject = jsonArray.getJSONObject(i);
                latitude = jsonObject.getString("Latitude");
                longitude = jsonObject.getString("Longitude");
                category = jsonObject.getString("Category");
                description = jsonObject.getString("Description");

            } catch (JSONException e) {

                e.printStackTrace();
            }


            LatLng coord = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

            marker = gMap.addMarker(new MarkerOptions()
                    .position(coord)
                    .title("Kategori: " + category)
                    .snippet("Beskrivning: " + description)
                    .visible(true)
                    .draggable(false)
                    .icon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    .alpha(0.6f));

        }
        activeMarker = gMap.addMarker(new MarkerOptions()
                .position(currentPos)
                .title("Din position!")
                .visible(true)
                .draggable(false));

        activeMarker.showInfoWindow();

    }


    public void createGPS() {

        textView = (TextView) findViewById(R.id.textLocation);
        continueButton = (Button) findViewById(R.id.button_continue);
        abortButton = (Button) findViewById(R.id.button_abort);
        gps = new GPSTracker(IssuesNearby.this);
        buttonCurrentLocation = (Button) findViewById(R.id.button_current_location);

        continueButton.setOnClickListener(this);
        abortButton.setOnClickListener(this);
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        gCoder = new Geocoder(this, Locale.getDefault());

        //Första kartvyn över centrala uppsala
        startMap(Double.parseDouble(lat), Double.parseDouble(lon));


    }

    public void startMap(final double lat, final double lon) {
        gMap.setMyLocationEnabled(true);
        coords = new LatLng(lat, lon);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 14));

        final GoogleMap.InfoWindowAdapter window = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.address_window, null);
                double lat = marker.getPosition().latitude;
                double lon = marker.getPosition().longitude;
                double lati = activeMarker.getPosition().latitude;
                double longi = activeMarker.getPosition().longitude;
                tvAdress = (TextView) v.findViewById(R.id.tv_adress);

                String title = "\n" + marker.getTitle();
                String snippet = "\n" + marker.getSnippet();
                activeMarker.getSnippet();
                if (marker.getSnippet() == null) {
                    snippet = "";
                }
                if (marker.getTitle() == null) {
                    title = "";
                }

                try {
                    activeAddresList = gCoder.getFromLocation(lati, longi, 1);
                    activeAdress = activeAddresList.get(0).getAddressLine(0);

                    addressList = gCoder.getFromLocation(lat, lon, 1);
                    adress = addressList.get(0).getAddressLine(0);
                    tvAdress.setText(adress);
                    String adress = addressList.get(0).getAddressLine(0);
                    tvAdress.setText(adress + title + snippet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return v;
            }
        };

        gMap.setInfoWindowAdapter(window);
        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                putIssuesOnMap();
            }
        });


        // Här anges storleken på kartvyn i appen
        ViewGroup.LayoutParams limits = mapFragment.getView().getLayoutParams();
        limits.height = 800;
        mapFragment.getView().setLayoutParams(limits);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_continue:

                Bundle bundle = new Bundle();
                bundle.putString("Latitude", String.valueOf(activeMarker.getPosition().latitude));
                bundle.putString("Longitude", String.valueOf(activeMarker.getPosition().longitude));
                bundle.putString("Address", activeAdress);

                Intent intentBundle = new Intent(IssuesNearby.this, NewSubmitReport.class);
                intentBundle.putExtras(bundle);
                startActivity(intentBundle);
                break;

            case R.id.button_abort:

                startActivity(new Intent(IssuesNearby.this, MainActivity.class));
                break;


        }
    }
}
