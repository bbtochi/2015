package com.ibm.project_traffic.Packages.UI;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ibm.project_traffic.Packages.Adapters.FeedListAdapter;
import com.ibm.project_traffic.Packages.Utils.GetDetailsFromJSON;
import com.ibm.project_traffic.Packages.Utils.OnJSONReturn;
import com.ibm.project_traffic.Packages.Utils.PostDetails;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.aut.utillib.circular.animation.CircularAnimationUtils;


public class RouteFeedFragment extends Fragment implements OnJSONReturn{

    private Activity applicationActivity;
    private RecyclerView datalist;
    private RecyclerView.LayoutManager manager;
    private View myView;
    FloatingActionButton routeInfoBtn;
    List<PostDetails> postData;
    String start = " ";
    String end = " ";
    Bundle bundle;
    private int screenWidth;
    private int screenHeight;
    PostDetails details;
    CircularProgressView progressView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate view
        view = inflater.inflate(R.layout.fragment_route_feed, container, false);

        //Progress dialog
        progressView = (CircularProgressView) view.findViewById(R.id.progress_view);

        //Get route names
        bundle = getActivity().getIntent().getExtras();
        if (bundle.getString("startingPoint") == null
                ||bundle.getString("destinationName") == null ){
            Toast.makeText(getActivity().getApplicationContext(), "Please specify route", Toast.LENGTH_SHORT).show();
        }
        start = bundle.getString("startingPoint");
        end = bundle.getString("destinationName");

        //Get Specific feed data
        GetSpecificFeedRequest specificFeedRequest = new GetSpecificFeedRequest();
        specificFeedRequest.execute("specific", start,end);

        //Get Details for traffic
        GetDetailsFromJSON json = new GetDetailsFromJSON(RouteFeedFragment.this) ;
        json.execute("specific", start, end);

        //Bind Views
        datalist = (RecyclerView)view.findViewById(R.id.datalist);
        routeInfoBtn = (FloatingActionButton)view.findViewById(R.id.route_info_btn);

        //Set recycler view properties
        datalist.setHasFixedSize(true);
        manager = new LinearLayoutManager(applicationActivity);
        datalist.setLayoutManager(manager);

        return view;
    }


    //Animation for revealing sheet with specific traffic data
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }

        myView = view.findViewById(R.id.linear);
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView.setVisibility(View.INVISIBLE);
            }
        });

        routeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myView.getVisibility() == View.VISIBLE) {
                    myView.setVisibility(View.INVISIBLE);
                }else{
                    int[] myViewLocation = new int[2];
                    myView.getLocationInWindow(myViewLocation);

                    float finalRadius = CircularAnimationUtils.hypo(screenWidth - myViewLocation[0], screenHeight - myViewLocation[1]);
                    int[] center = CircularAnimationUtils.getCenter(routeInfoBtn, (View) myView.getParent());

                    ObjectAnimator animator =
                            CircularAnimationUtils.createCircularReveal(myView, center[0], center[1], 0, finalRadius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(1500);
                    animator.start();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        applicationActivity = activity; //Get application activity
    }

    @Override
    public void getTrafficDetails(PostDetails postDetails) {
        details = postDetails;
        //Get elements of the revealing card
        TextView percentage = (TextView)view.findViewById(R.id.percentage_text);
        TextView responseRatio = (TextView)view.findViewById(R.id.response_ratio);
        TextView trafficType = (TextView)view.findViewById(R.id.traffic_type);
        TextView from = (TextView)view.findViewById(R.id.origin_name);
        TextView to = (TextView)view.findViewById(R.id.destination_name);

        //Set the fields
        percentage.setText(String.valueOf(details.getPercentage() + "%"));
        responseRatio.setText(String.valueOf(details.getCongestionResponse()) + "/" + String.valueOf(details.getTotalResponse()) + "users");
        trafficType.setText("posted" + " " + getCongestionType(details.getCongestionRating()));
        from.setText(details.getFrom());
        to.setText(details.getTo());



    }

    private String getCongestionType(int congestion){
        String congestionType;
        switch(congestion){
            case 0:
                congestionType = "CLOSED ROAD";
                break;
            case 1:
                congestionType = "NO CONGESTION";
                break;
            case 2:
                congestionType = "MODERATE TRAFFIC";
                break;
            case 3:
                congestionType = "HEAVY TRAFFIC";
                break;
            case 4:
                congestionType = " STOP AND GO";
                break;
            default:
                congestionType = "no reports";
                break;
        }
        return congestionType;
    }
    //Async task for getting specific feed data

    public class GetSpecificFeedRequest extends AsyncTask<String, String , ArrayList<PostDetails>> {
        public static final String baseURL = "http://169.55.141.28:3000/";
        ArrayList<PostDetails> listOfPosts;
        PostDetails trafficStats = new PostDetails();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.startAnimation();
        }

        @Override
        protected ArrayList<PostDetails> doInBackground(String... feedDetails) {
            String uri = " ";
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

                //Get the "Posts" array
                JSONArray posts = feed.getJSONArray("posts");

                //Get the "Details" object in the JSON
                JSONObject trafficStatsObj = feed.getJSONObject("details");

                //ArrayList of PostDetails
                listOfPosts = new ArrayList<>();

                //JSON object to reference as the iteration goes through every object in the Posts array
                JSONObject elements;

                //Get all posts in the "Posts" array
                    for (int i = 0; i < posts.length(); i++) {
                        elements = posts.getJSONObject(i);

                        //Populate the PostDetails object
                        PostDetails postDetails = new PostDetails();
                        postDetails.setId(elements.getInt("id"));
                        postDetails.setUsername(elements.getString("user"));
                        postDetails.setFrom(elements.getString("from"));
                        postDetails.setTo(elements.getString("to"));
                        postDetails.setComment(elements.getString("comment"));
                        postDetails.setCongestionRating(elements.getInt("congestion"));
                        postDetails.setTimeStamp(elements.getString("timestamp"));
                        postDetails.setReportingAddress(elements.getString("reportinglocation"));

                        //Add post details to the array list
                        listOfPosts.add(postDetails);
                        Log.i("Details", listOfPosts.get(i).getComment());
                    }

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
            return listOfPosts;
        }

        @Override
        protected void onPostExecute(ArrayList<PostDetails> data){
            postData = data;
            Collections.reverse(postData);
            datalist.setAdapter(new FeedListAdapter(applicationActivity, postData));
            progressView.setVisibility(View.INVISIBLE);
        }
    }
}
