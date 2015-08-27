package com.ibm.project_traffic.Packages.Utils;

import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by larry on 8/17/15.
 */
public class GetDetailsFromJSON extends AsyncTask< String, String , PostDetails> {

    OnJSONReturn jsonReturn;
    public static final String baseURL = "http://169.55.141.28:3000/";

    public GetDetailsFromJSON(OnJSONReturn onJSONReturn){
        jsonReturn = onJSONReturn;
    }

    @Override
    protected PostDetails doInBackground(String... feedDetails) {
        String uri = " ";
        PostDetails trafficStats = new PostDetails();

        try {
            uri = baseURL + "?" + "feed=" + feedDetails[0] + "&" + "from=" + URLEncoder.encode(feedDetails[1], "UTF-8") + "&" + "to=" + URLEncoder.encode(feedDetails[2], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("URL: ", uri);
        //Initialize a get request with the HttpGet method
        HttpGet httpGet = new HttpGet(uri);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //Execute request via client
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            //Get response content
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
            //Log.i("Info", stringBuilder.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Get the data in whole as a JSONObject
            JSONObject feed = new JSONObject(stringBuilder.toString());

            //Get the "Details" object in the JSON
            JSONObject trafficStatsObj = feed.getJSONObject("details");

            //Populate PostDetails object, trafficStats, with data
            trafficStats.setFrom(trafficStatsObj.getString("from"));
            trafficStats.setTo(trafficStatsObj.getString("to"));
            trafficStats.setPercentage(trafficStatsObj.getInt("percentage"));
            trafficStats.setEst_time(trafficStatsObj.getDouble("est_time"));
            trafficStats.setCongestionRating(trafficStatsObj.getInt("congestion"));
            trafficStats.setCongestionResponse(trafficStatsObj.getInt("cong_resp"));
            trafficStats.setTotalResponse(trafficStatsObj.getInt("tot_resp"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trafficStats;
    }

    @Override
    protected void onPostExecute(PostDetails postDetails) {
        jsonReturn.getTrafficDetails(postDetails);
    }
}