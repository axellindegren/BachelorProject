package com.example.axel.appproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Viktor on 2015-05-05.
 */
public class Serverconnection extends AsyncTask<String,String,String> {

    ProgressDialog progressDialog;
    InputStream is;
    List<NameValuePair> nameValuePairs;
    Context SubmitReport;

    public Serverconnection(List<NameValuePair> values, Context context) {
        nameValuePairs = values;
        SubmitReport = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(SubmitReport);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("New Report...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
    }


    @Override
    protected String doInBackground(String... params) {
        publishProgress("Working...");
        is = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://stsitkand.student.it.uu.se/app/ReportIssue.php");

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity entity = httpResponse.getEntity();

            is = entity.getContent();

        } catch (ClientProtocolException e) {

            Log.e("ClientProtocol", "Log tag");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Log tag", "IOException");
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
        } catch (UnsupportedEncodingException e) {
            Log.e("log_tag", "Error converting result " + e.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    protected void onPostExecute(String args) {
        // dismiss the dialog after getting all products
        progressDialog.dismiss();
    }
}
