package com.ibm.project_traffic.Packages.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ibm.project_traffic.Packages.Adapters.FeedListAdapter;
import com.ibm.project_traffic.Packages.Utils.PostDetails;
import com.ibm.project_traffic.Packages.Utils.UserFeedback;
import com.ibm.project_traffic.R;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendingTrafficFeedFragment extends Fragment {

    private FloatingActionButton postBtn;
    private Activity appActivity;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private UserFeedback feedback;
    private ArrayList<UserFeedback> userFeedbacks;
    private String post = " ";
    private SwipeRefreshLayout refreshLayout;
    private Bundle bundle;
    List<PostDetails> postData;
    CircularProgressView progressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_feed, container, false);

        //Get data from database
          GetRequest request = new GetRequest();
          request.execute("general");

        //Bind Progress indicator
        progressView = (CircularProgressView)view.findViewById(R.id.trending_progress);

        //Get route details from hosting Activity
        bundle = getActivity().getIntent().getExtras();
        final String startingPoint = bundle.getString("startingPoint");
        final String destinationName = bundle.getString("destinationName");

        //Binding views
        postBtn = (FloatingActionButton)view.findViewById(R.id.feed_btn_item);
        recyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        //Set Recycler view properties
        refreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.c_blue_gray), getActivity().getResources().getColor(R.color.c_green));
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(appActivity);
        recyclerView.setLayoutManager(manager);

        //Refresh layout and update data
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                GetRequest newDataRequest = new GetRequest();
                newDataRequest.execute("general");
                refreshLayout.setRefreshing(false);
            }
        });

        //Stop refreshing
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }

        //Trigger PostTrafficDetailsActivity on button click
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(appActivity, PostTrafficDetails.class);
                intent.putExtra("startingPoint", startingPoint);
                intent.putExtra("destinationName", destinationName);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        appActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class GetRequest extends AsyncTask< String, String , ArrayList<PostDetails>> {
        public static final String baseURL = "http://169.55.141.28:3000/";
        ArrayList<PostDetails> listOfPosts;

        @Override
        protected ArrayList<PostDetails> doInBackground(String... feedDetails) {

            String uri = " ";
            //If the argument for GetRequest#execute() is just 1 (i.e the feed type), set it as the query argument
            if (feedDetails.length == 1) {
                uri = baseURL + "?" + "feed=" + feedDetails[0];
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
                Toast.makeText(appActivity, "Oops, something went wrong. Make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject feed = new JSONObject(stringBuilder.toString());
                JSONArray posts = feed.getJSONArray("posts");
                listOfPosts = new ArrayList<>();
                JSONObject elements;
                if (feed.getString("feed").compareTo("general") == 0) {
                    for (int i = 0; i < posts.length(); i++) {
                        elements = posts.getJSONObject(i);
                        PostDetails postDetails = new PostDetails();
                        postDetails.setId(elements.getInt("id"));
                        postDetails.setUsername(elements.getString("user"));
                        postDetails.setFrom(elements.getString("from"));
                        postDetails.setTo(elements.getString("to"));
                        postDetails.setComment(elements.getString("comment"));
                        postDetails.setCongestionRating(elements.getInt("congestion"));
                        postDetails.setTimeStamp(elements.getString("timestamp"));
                        postDetails.setReportingAddress(elements.getString("reportinglocation"));
                        listOfPosts.add(postDetails);
                        Log.i("Comment: ", listOfPosts.get(i).getUsername());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return listOfPosts;
        }

        @Override
        protected void onPostExecute(ArrayList<PostDetails> data){
             postData = data;
             Collections.reverse(postData);
             recyclerView.setAdapter(new FeedListAdapter(appActivity, postData));
            progressView.setVisibility(View.INVISIBLE);
        }
    }


    


}


