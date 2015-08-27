package com.ibm.project_traffic.Packages.Utils;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

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

/**
 * Created by larryasante on 7/10/15.
 */
public class AsyncGeocoding extends AsyncTask<String, Void, LatLng> {
    OnTaskCompleted onTaskCompletedListener;
    String locAddress;
    double lat = 0;
    double lng = 0;

    public AsyncGeocoding(String address, OnTaskCompleted onTaskCompletedListener){
        this.locAddress = address;
        this.onTaskCompletedListener = onTaskCompletedListener;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                locAddress.replace(" ", "%20") + "&sensor=false";
        double dummyLat = 0;
        double dummyLng = 0;


        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        LatLng latLngFromAddress = new LatLng(dummyLat, dummyLng);

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject ;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
            latLngFromAddress = new LatLng(lat, lng);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latLngFromAddress;
    }

    @Override
    protected void onPostExecute(LatLng s) {
        onTaskCompletedListener.getResultOfTask(s);
    }

}
