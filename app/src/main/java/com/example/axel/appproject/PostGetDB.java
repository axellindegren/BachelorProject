package com.example.axel.appproject;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by johansundstrom on 15-05-19.
 */
public class PostGetDB {


    String url;
    InputStream is;
    List<NameValuePair> idList;
    Context MyReports;

    public PostGetDB(Context context, List<NameValuePair> ids) {
        idList = ids;
        MyReports = context;
    }

        public JSONArray getAllPosts() {

        is = null;
        //HttpEntity httpEntity = null;
            JSONArray jsonArray = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://stsitkand.student.it.uu.se/app/db/getmyreports.php");
//            HttpPost httpPost = new HttpPost("http://stsitkand.student.it.uu.se/app/db/morereports.php");


            httpPost.setEntity(new UrlEncodedFormEntity(idList, HTTP.UTF_8));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            //System.out.println("Response: " + httpResponse.getEntity().getContent());

            String entityResponse = EntityUtils.toString(httpResponse.getEntity());
            System.out.println("Respons" + entityResponse);
            jsonArray = new JSONArray(entityResponse);

            //is = httpEntity.getContent();

        } catch (ClientProtocolException e) {

            Log.e("ClientProtocol", "Log tag");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Log tag", "IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
