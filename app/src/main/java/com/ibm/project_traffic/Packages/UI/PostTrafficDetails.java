package com.ibm.project_traffic.Packages.UI;


import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ibm.project_traffic.Packages.Utils.Geolocation;
import com.ibm.project_traffic.Packages.Utils.OnHttpGetCompleted;
import com.ibm.project_traffic.Packages.Utils.OnPostRequestCompleted;
import com.ibm.project_traffic.Packages.Utils.PostDetails;
import com.ibm.project_traffic.Packages.Utils.PostRequest;
import com.ibm.project_traffic.R;

import java.util.ArrayList;
import java.util.List;

public class PostTrafficDetails extends ActionBarActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        OnHttpGetCompleted,
        OnPostRequestCompleted{

    public String trafficPostDetails = " ";
    private Toolbar toolbar;
    private EditText post, userHandle;
    private String userHandleAsString = " ";
    private TextView startingPt;
    private TextView destination;
    private TextView reportedLocation;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = PostTrafficDetails.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private List<PostDetails> list;
    int congestionRating;
    String startingPoint = " ";
    String destinationName = " ";
    PostDetails details = new PostDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_traffic_details);

        // Set up google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        //Binding Views
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        post = (EditText)findViewById(R.id.traffic_post);
        userHandle = (EditText)findViewById(R.id.user_handle);
        startingPt = (TextView)findViewById(R.id.route_start);
        destination = (TextView)findViewById(R.id.route_end);
        reportedLocation = (TextView)findViewById(R.id.reportingLocation);

        //Start username with "@"
        userHandle.setText("@");
        Selection.setSelection(userHandle.getText(), userHandle.getText().length());
        userHandle.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("@")) {
                    userHandle.setText("@");
                    Selection.setSelection(userHandle.getText(), userHandle.getText().length());
                }

            }
        });

        //Set Toolbar as actionbar
        setSupportActionBar(toolbar);

        //Set the text in the TextViews with route details
        startingPoint = getIntent().getStringExtra("startingPoint");
        destinationName = getIntent().getStringExtra("destinationName");

        if (startingPoint == null){
            startingPt.setHint("Make sure you specified a starting point");
        }
        else{
            startingPt.setText(startingPoint);
        }

        if (destinationName == null){
            destination.setHint("Make sure you specified a destination");
        }
        else{
            destination.setText(getIntent().getStringExtra("destinationName"));
        }

        //Set up radio buttons
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.closed:
                        congestionRating = 0;
                        break;

                    case R.id.no_congestion:
                        congestionRating = 1;
                        break;

                    case R.id.moderate_traffic:
                        congestionRating= 2;
                        break;

                    case R.id.heavy_traffic:
                        congestionRating = 3;
                        break;

                    case R.id.stop_and_go:
                        congestionRating = 4;
                        break;

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_traffic_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        trafficPostDetails = post.getText().toString();
        userHandleAsString = userHandle.getText().toString();

        if (id == R.id.action_post) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            PostDetails postDetails = new PostDetails();
            postDetails.setUsername(userHandleAsString);
            postDetails.setReportingAddress(Geolocation.reverseGeocoding(location.getLatitude(), location.getLongitude(), PostTrafficDetails.this));
            postDetails.setFrom(startingPoint);
            postDetails.setTo(destinationName);
            postDetails.setComment(trafficPostDetails);
            postDetails.setCongestionRating(congestionRating);
            PostRequest postRequest = new PostRequest(getApplicationContext(), PostTrafficDetails.this);
            postRequest.execute(postDetails);
            PostTrafficDetails.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else{
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            String reportingLocation = Geolocation.reverseGeocoding(currentLatitude, currentLongitude, this).toString();
            reportedLocation.setText(reportingLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        String reportingLocation = Geolocation.reverseGeocoding(currentLatitude, currentLongitude, this).toString();
        reportedLocation.setText(reportingLocation);
    }



    @Override
    public void getPostASJSON(PostDetails postDetails) {
        details = postDetails;
    }

    @Override
    public void retrievehttpGetResults(ArrayList<PostDetails> data) {
        list = data;
        Log.i("Data :", list.get(0).getTimeStamp());
    }
}
