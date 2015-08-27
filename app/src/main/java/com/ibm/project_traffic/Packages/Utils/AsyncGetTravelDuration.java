package com.ibm.project_traffic.Packages.Utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by larry on 8/18/15.
 */
public class AsyncGetTravelDuration extends AsyncTask<String, Void, String> {
    OnMapsQuery onMapsQuery;

    public AsyncGetTravelDuration(OnMapsQuery query){
        onMapsQuery = query;
    }

    @Override
    protected String doInBackground(String... params) {
        String feedBaseURL = "http://maps.googleapis.com/maps/api/directions/json?";
        String uri = " ";
        String timeTaken = " ";
        try {
            uri = feedBaseURL + "origin="+URLEncoder.encode(params[0], "UTF-8") + "&" + "destination=" + URLEncoder.encode(params[1], "UTF-8") + "&" + "sensor=true&mode=driving";
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

        try{
            JSONObject jsonFeed = new JSONObject(stringBuilder.toString());
            JSONObject jsonRoute = jsonFeed.getJSONObject("routes");
            JSONObject legData = jsonRoute.getJSONArray("legs").getJSONObject(0);
            JSONObject duration = legData.getJSONObject("duration");
            timeTaken = duration.getString("text");
            return timeTaken;
        }catch (JSONException jsonEx){
            jsonEx.printStackTrace();
        }
        return timeTaken;
    }

    @Override
    protected void onPostExecute(String s) {
        onMapsQuery.returnTimeTaken(s);
    }
}
