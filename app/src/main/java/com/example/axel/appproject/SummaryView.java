package com.example.axel.appproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Viktor on 2015-05-12.
 */
public class SummaryView extends View {

    View rootView;
    ImageView image;
    ImageView map_screenshot;
    TextView address;
    TextView category;
    TextView desc;
    Button sendButton;
    List<NameValuePair> nameValuePairs;
    long uniqueName;
    ProgressDialog progressDialog;

    //Allt som skickas till databasen
    String email;
    String description;
    String longi;
    String lati;
    String stringTime;
    String cat;
    String imageBase;
    Bitmap cameraBitmap;

    public SummaryView(final Context context, ViewFlipper container, final LayoutInflater inflater) {

        super(context);
        rootView = inflater.inflate(R.layout.summary_view,container,false);
        image = (ImageView) rootView.findViewById(R.id.summary_photo);
        map_screenshot = (ImageView) rootView.findViewById(R.id.map_thumbnail);
        address = (TextView) rootView.findViewById(R.id.adress);
        category = (TextView) rootView.findViewById(R.id.category);
        desc = (TextView) rootView.findViewById(R.id.summary_desc);
        sendButton = (Button) rootView.findViewById(R.id.button_send);

        imageBase = "http://stsitkand.student.it.uu.se/app/";
        nameValuePairs = new ArrayList<NameValuePair>();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Laddar...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(desc.getText().toString().isEmpty() && category.getText()=="") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.summary);
                    builder.setTitle(R.string.summary_title);
                    builder.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

                else if((desc.getText().toString().isEmpty())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Du måste ange en beskrivning!");
                    builder.setTitle(R.string.summary_title);
                    builder.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                }
                else if(category.getText()=="") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Du måste ha en kategori!");
                    builder.setTitle(R.string.summary_title);
                    builder.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.show();
                }
                else {

                    uniqueName = System.currentTimeMillis();
                    generateValues(String.valueOf(uniqueName));
                    File dir = context.getDir("MyReports", Context.MODE_PRIVATE);
                    //deleteRecursive(dir);
                    OutputStream fout = null;
                    try {
                        File report = new File(dir,"Report nr " + uniqueName);
                        fout = new FileOutputStream(report);
                        fout.write(nameValuePairs.get(6).getValue().getBytes());
                        fout.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Serverconnection runner = new Serverconnection(nameValuePairs,context);
                    runner.execute();

                    UploadImage uploadImage = new UploadImage(context,uniqueName);
                    uploadImage.execute(cameraBitmap);
                }
            }
        });
    }

    public void setDesc(String text) {
        desc.setText(text);
        description = text;
    }

    public void setCategory(String text) {
        category.setText(text);
        cat = text;
        if(category.getText()==null | category.getText()=="Välj En Kategori" | category.getText()=="") {
            category.setText("");
            cat = "";
        }
    }

    public void setImage(Bitmap img) {
        image.setImageBitmap(img);
        cameraBitmap = img;
    }

    public void setMapImage(Bitmap img) {
        map_screenshot.setImageBitmap(img);
    }
    public void setDefaultValues() {
        image.setImageResource(R.drawable.photo_grey);
        map_screenshot.setImageResource(R.drawable.pinmarker_grey);
        setDesc("");
        setCategory("");
    }
    public void setAddress(String text,String lat,String lon) {
        lati = lat;
        longi = lon;
        address.setText(text);
    }

    public View getRootView() {
        return rootView;
    }

    public Button getSendButton() {
        return sendButton;
    }

    private String getImageName(String unique) {
        return imageBase + unique + ".png";
    }

    private String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        return format;
    }

    private void generateValues(String name) {
        stringTime = currentTime();

        nameValuePairs.add(new BasicNameValuePair("Description", description));
        nameValuePairs.add(new BasicNameValuePair("Longitude", longi));
        nameValuePairs.add(new BasicNameValuePair("Latitude", lati));
        nameValuePairs.add(new BasicNameValuePair("IssueCategory", cat));
        nameValuePairs.add(new BasicNameValuePair("Timestamp", stringTime));
        nameValuePairs.add(new BasicNameValuePair("Picture",getImageName(name)));
        nameValuePairs.add(new BasicNameValuePair("UniqueID",name));
    }

    void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }
}
