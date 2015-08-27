package com.ibm.project_traffic.Packages.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ibm.project_traffic.Packages.UI.TrendingTrafficFeedFragment;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by larryasante on 7/31/15.
 */
public class PostRequest extends AsyncTask<PostDetails, Void, PostDetails> {
    OnPostRequestCompleted onPostRequestCompleted;
    Context mContext;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private String baseUrl = TrendingTrafficFeedFragment.GetRequest.baseURL;

    public PostRequest(Context context, OnPostRequestCompleted completed) {
        onPostRequestCompleted = completed;
        mContext = context;
    }

    @Override
    protected PostDetails doInBackground(PostDetails... postDetails) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String toJson = gson.toJson(postDetails[0]);
        System.out.println(toJson);

        RequestBody body = RequestBody.create(JSON, toJson);
        Request request = new Request.Builder()
                .url(baseUrl)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Log.i("HTTP POST Response:", response.body().string());
        } catch (IOException e) {
            Toast.makeText(mContext, "Something went from. Please try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return postDetails[0];
    }

}
