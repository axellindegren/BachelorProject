package com.example.axel.appproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Viktor on 2015-05-05.
 */
public class UploadImage extends AsyncTask<Bitmap, Void, Void> {

    Context submitreport;
    Bitmap bitmap;
    long uniqueName;
    ProgressDialog progressDialog;

    public UploadImage(Context context, long fileName) {
        submitreport = context;
        uniqueName = fileName;
    }

    private static final String TAG = "upload";

    protected Void doInBackground(Bitmap... bitmaps) {
        if (bitmaps[0] == null) {
            return null;
        }
        //setProgress(0);
        bitmap = bitmaps[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
        InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost("http://stsitkand.student.it.uu.se/app/sendimg.php"); // server
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("myFile",
                    uniqueName + ".png", in);
            httppost.setEntity(reqEntity);
            System.out.println("currentimemillis: " + uniqueName);
            Log.i(TAG, "request " + httppost.getRequestLine());
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (response != null)
                    Log.i(TAG, "response " + response.getStatusLine().toString());
            } finally {
            }
        } finally {
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(submitreport);
        progressDialog.setMessage("Skickar Rapport...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(Void result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(submitreport);
        builder.setMessage("Tack för ditt bidrag! Du kan nu följa ditt ärende under fliken Visa Mina Rapporter och se återkoppling från kommunen och status");
        builder.setTitle("Tack!");
        builder.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitreport.startActivity(new Intent(submitreport, MainActivity.class));
            }
        });
        builder.show();
        //Toast.makeText(submitreport, R.string.uploaded, Toast.LENGTH_LONG).show();
    }
}
