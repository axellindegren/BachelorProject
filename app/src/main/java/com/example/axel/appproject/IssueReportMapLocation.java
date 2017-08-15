package com.example.axel.appproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jaxel on 2015-04-15.
 */

public class IssueReportMapLocation extends Activity implements View.OnClickListener {

    TextView textView;
    Button buttonCurrentLocation;
    Button button_view_next;
    ImageButton btnHome;
    ImageButton btnHelp;
    double latitude;
    double longitude;
    LatLng coords;
    GPSTracker gps;
    GoogleMap gMap;
    MapFragment mapFragment;
    String adress;
    Marker activeMarker;
    Geocoder gCoder;
    List<Address> addressList;
    TextView tvAdress;
    EditText search;
    Button btnSearch;
    EditText etLocation;

    ArrayList<Marker> Markers = new ArrayList<>();
    HashMap<Integer, ArrayList<String>> multiMap = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);

        btnHelp = (ImageButton) findViewById(R.id.help_button);
        btnHelp.setOnClickListener(this);
        btnHome = (ImageButton) findViewById(R.id.home_button);
        btnHome.setOnClickListener(this);
    }

    protected void onResume() {
        super.onResume();
        createGPS();
    }

    public void createGPS() {

        textView = (TextView) findViewById(R.id.textLocation);
        button_view_next = (Button) findViewById(R.id.button_view_next);
        gps = new GPSTracker(IssueReportMapLocation.this);
        buttonCurrentLocation = (Button) findViewById(R.id.button_current_location);

        button_view_next.setOnClickListener(this);
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        gCoder = new Geocoder(this, Locale.getDefault());

        etLocation = (EditText) findViewById(R.id.search_field);
        etLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String location = etLocation.getText().toString() + "Uppsala";
                    if(location!=null && !location.equals("")) {
                        new SearchTask().execute(location);
                    }
                }
                return false;
            }
        });

        //Första kartvyn över centrala uppsala
        startMap(59.85856, 17.63893);


    }

    private class SearchTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... params) {

            Geocoder coder = new Geocoder((getBaseContext()));
            List<Address> addresses = null;

            try {
                addresses = coder.getFromLocationName(params[0],3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        protected void onPostExecute(List<Address> addresses) {

            if(addresses.size()!=0) {
                Address address = (Address) addresses.get(0);
                LatLng latlong = new LatLng(address.getLatitude(), address.getLongitude());
                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());
                setMarkerPos(latlong);
            }
            else {
                Toast toast = Toast.makeText(getBaseContext(), "Ingen adress hittad!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

    public void startMap(final double lat, final double lon) {
        gMap.setMyLocationEnabled(true);
        coords = new LatLng(lat, lon);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 14));

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                setMarkerPos(marker.getPosition());
            }
        });

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
                tvAdress = (TextView) v.findViewById(R.id.tv_adress);

                try {
                    addressList = gCoder.getFromLocation(lat, lon, 1);
                    adress = addressList.get(0).getAddressLine(0);
                    tvAdress.setText(adress);
                    String adress = addressList.get(0).getAddressLine(0);
                    tvAdress.setText(adress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return v;
            }
        };

        gMap.setInfoWindowAdapter(window);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                setMarkerPos(point);

            }
        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setMarkerPos(latLng);
            }
        });

        buttonCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    coords = new LatLng(latitude, longitude);
                    setMarkerPos(coords);

                //    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 14));

                } else {
                    gps.showSettingAlert();
                }
            }

        });

        // Här anges storleken på kartvyn i appen
        ViewGroup.LayoutParams limits = mapFragment.getView().getLayoutParams();
        limits.height = 800;
        mapFragment.getView().setLayoutParams(limits);

        }

    public void setMarkerPos(LatLng pos) {

        if(activeMarker==null) {

            activeMarker = gMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .visible(true)
                    .draggable(true));
            activeMarker.showInfoWindow();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
        }

        else {

            activeMarker.setPosition(pos);
            activeMarker.setVisible(true);
            activeMarker.showInfoWindow();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
        }
    }

    // screen shot over the map
    public void captureScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                Bitmap bitmap = snapshot;

                OutputStream fout = null;

                String filePath = System.currentTimeMillis() + ".jpeg";

                try {
                    fout = openFileOutput("BITMAP_MAP", Context.MODE_PRIVATE);

                    // Write the string to the file
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                    fout.flush();
                    fout.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "FileNotFoundException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "IOException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }


            }
        };

        gMap.snapshot(callback);
    }


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_view_next:
                captureScreen();

                if(activeMarker==null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.add_position);
                    builder.setTitle(R.string.pos_title);
                    builder.setPositiveButton("Uppfattat!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                    break;
                }
                else {

                    Bundle bundle = new Bundle();
                    bundle.putString("Latitude", String.valueOf(activeMarker.getPosition().latitude));
                    bundle.putString("Longitude", String.valueOf(activeMarker.getPosition().longitude));
                    bundle.putString("Address", adress);

                    List<NameValuePair> coordList = new ArrayList<NameValuePair>();
                    coordList.add(new BasicNameValuePair("Action", "getNearbyReports"));
                    coordList.add(new BasicNameValuePair("Latitude", String.valueOf(activeMarker.getPosition().latitude)));
                    coordList.add(new BasicNameValuePair("Longitude", String.valueOf(activeMarker.getPosition().longitude)));
                    new getNearbyReports().execute(new PostGetDB(this, coordList));
                    break;
                }
            case R.id.help_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.map_location_help);
                builder.setTitle(R.string.help);
                builder.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.show();
                break;

            case R.id.home_button:
                startActivity(new Intent(IssueReportMapLocation.this, MainActivity.class));
                break;
        }

    }

    public void getAllReports(JSONArray jsonArray) {

        final JSONArray jsonArray1 = jsonArray;
        int nrOfReports = jsonArray.length();
        if(nrOfReports != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Det finns " + nrOfReports + " rapporter nära dig. Vill du se dessa?");
            builder.setTitle("Närliggande Rapporter");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Bundle bundle = new Bundle();
                    bundle.putString("Issues", jsonArray1.toString());
                    bundle.putString("Latitude", String.valueOf(activeMarker.getPosition().latitude));
                    bundle.putString("Longitude", String.valueOf(activeMarker.getPosition().longitude));
                    Intent intent = new Intent(IssueReportMapLocation.this, IssuesNearby.class);
                    intent.putExtras(bundle);
                    startActivity(intent);


                }
            });
            builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    toNewSubmitReport();
                }
            });
            builder.show();
        }else{
            toNewSubmitReport();

            }
    }

    public void toNewSubmitReport() {
        Bundle bundle = new Bundle();
        bundle.putString("Latitude", String.valueOf(activeMarker.getPosition().latitude));
        bundle.putString("Longitude", String.valueOf(activeMarker.getPosition().longitude));
        bundle.putString("Address", adress);

        Intent intentBundle = new Intent(IssueReportMapLocation.this, NewSubmitReport.class);
        intentBundle.putExtras(bundle);
        startActivity(intentBundle);

    }

    private class getNearbyReports extends AsyncTask<PostGetDB, Long, JSONArray> {

        @Override
        protected JSONArray doInBackground(PostGetDB... params) {
            return params[0].getAllPosts();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            getAllReports(jsonArray);
        }
    }


}
