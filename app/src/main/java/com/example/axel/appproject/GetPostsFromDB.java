package com.example.axel.appproject;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;

/**
 * Created by Axel on 2015-05-05.
 */
public class GetPostsFromDB {


    Integer chosenURL;
    String url;

    public JSONArray GetAllPosts(Integer chosenURL) {

        this.chosenURL = chosenURL;

        switch (chosenURL) {
            case 1:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortLatest.php";
                break;
            case 2:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortBike.php";
                break;
            case 3:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortClean.php";
                break;
            case 4:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortPublicPlaces.php";
                break;
            case 5:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortOther.php";
                break;
            case 6:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortRoads.php";
                break;
            case 7:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortVegetation.php";
                break;
            case 8:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortTraffic.php";
                break;
            case 9:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortGrafitti.php";
                break;
            case 10:
                url = "http://stsitkand.student.it.uu.se/app/db/mongodbSortFixed.php";
                break;
        }


        // Get HttpResponse Object from url.
        // Get HttpEntity from Http Response Object

        HttpEntity httpEntity = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();  // Default HttpClient
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert HttpEntity into JSON Array
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response  : ", entityResponse);
                jsonArray = new JSONArray(entityResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

}
