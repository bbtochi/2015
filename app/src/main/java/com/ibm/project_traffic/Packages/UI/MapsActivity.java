package com.ibm.project_traffic.Packages.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ibm.project_traffic.Packages.Adapters.PlacesAutoCompleteAdapter;
import com.ibm.project_traffic.Packages.Utils.Geolocation;
import com.ibm.project_traffic.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        RoutingListener{

    public static final String TAG = MapsActivity.class.getSimpleName();
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final LatLngBounds BOUNDS_JAMAICA= new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));
    private static Typeface startTypeface;
    private static Typeface destinationTypeface;
    protected LatLng start;
    protected LatLng end;
    private  LatLng currentStart;
    private LatLng currentDestination;
    private double userLocationLongitude;
    private double userLocationLatitude;
    private double currentLocationLongitude;
    private double currentLocationLatitude;
    private LatLng userLocLatLng = new LatLng(userLocationLatitude,userLocationLongitude);
    private LatLng currentLocLatLng = new LatLng(userLocationLatitude,userLocationLongitude);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Bundle extra;
    private AutoCompleteTextView startingPoint;
    private AutoCompleteTextView destination;
    private Polyline polyline;
    private ImageView getRouteButton;
    private String LOG_TAG = "MyActivity";
    private String userInputtedLocation;
    private FloatingActionButton routeFab;
    private CardView cardView;
    private ProgressDialog progressDialog;
    private PlacesAutoCompleteAdapter mAdapter;
    private String currentLocationName;
    private static  NiftyNotificationView notificationView;
    private FloatingActionMenu menu;
    private FloatingActionButton showRouteInfoCard;
    private FloatingActionButton toFeedBtn;
    private String startingPointName;
    private String destinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //Binding Views
        startingPoint = (AutoCompleteTextView) findViewById(R.id.start);
        destination = (AutoCompleteTextView) findViewById(R.id.destination);
        getRouteButton = (ImageView)findViewById(R.id.getRouteBtn);
        cardView = (CardView)findViewById(R.id.cardview);
        showRouteInfoCard = (FloatingActionButton)findViewById(R.id.route_menu_item);
        toFeedBtn = (FloatingActionButton)findViewById(R.id.feed_item);
        showRouteInfoCard.setOnClickListener(onFABClickListener);
        toFeedBtn.setOnClickListener(onFABClickListener);


        //Show animated menus
        showAnimatedFABs();

        //Hide Card View
        cardView.setVisibility(View.INVISIBLE);

        //Set Text Color for views
        startingPoint.setTextColor(Color.BLACK);
        startingPoint.setTextColor(Color.BLACK);

        //Set font
        startTypeface = Typeface.createFromAsset(getAssets(), "fonts/texgyreadventor-regular.otf");
        destinationTypeface = Typeface.createFromAsset(getAssets(), "fonts/texgyreadventor-regular.otf");
        startingPoint.setTypeface(startTypeface);
        destination.setTypeface(destinationTypeface);

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        // Map UI interactions
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //Set up GoogleAPI client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Set up adapter
        mAdapter = new PlacesAutoCompleteAdapter(MapsActivity.this, android.R.layout.simple_dropdown_item_1line, mGoogleApiClient, BOUNDS_JAMAICA, null);

        //Add adapters to AutocompleteTextViews
        startingPoint.setAdapter(mAdapter);
        destination.setAdapter(mAdapter);

        //Set View listeners
        startingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                startingPointName = item.description.toString();
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                new  Thread(){
                    @Override
                    public void run() {
                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (!places.getStatus().isSuccess()) {
                                    // Request did not complete successfully
                                    Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                                    places.release();
                                    return;
                                }
                                // Get the Place object from the buffer.
                                final Place place = places.get(0);
                                start = place.getLatLng();
                                currentStart = place.getLatLng();
                                places.release();
                            }
                        });
                    }
                }.start();
            }
        });

        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                destinationName = item.description.toString();
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);


            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                new Thread(){
                    @Override
                    public void run() {
                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (!places.getStatus().isSuccess()) {
                                    // Request did not complete successfully
                                    Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                                    places.release();
                                    return;
                                }
                                // Get the Place object from the buffer.
                                final Place place = places.get(0);
                                end = place.getLatLng();
                                currentDestination = place.getLatLng();
                                places.release();
                            }
                        });
                    }
                }.start();
            }
        });

        getRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startingPoint.getText().toString().matches("") || destination.getText().toString().matches("")) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(findViewById(R.id.cardview));
                    Toast.makeText(getApplicationContext(), "Please complete both fields", Toast.LENGTH_SHORT).show();
                } else {
                    route();
                }
            }
        });


        //Add text watchers
        startingPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(end!=null)
                {
                    end=null;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showAnimatedFABs() {
        final FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.menu);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menu.getMenuIconView().setImageResource(menu.isOpened()
                        ? R.mipmap.ic_close : R.mipmap.ic_compass);
                if (!menu.isOpened() && cardView.isShown()){
                    notificationView.hide();
                    cardView.setVisibility(View.INVISIBLE);
                }
            }
        });
        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));
        menu.setIconToggleAnimatorSet(set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //setUpMap();
            }
        }
    }

    private void setUpMap() {

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Location userLocation = new Location("notUpdatedYet");

        //Get the bundle
        extra = getIntent().getExtras();
        Intent intent = getIntent();
        if ((extra != null) && (intent.getIntExtra("requestCode", -1) == 0)) {
            currentLocationLatitude = extra.getDouble("currentLatitude");
            currentLocationLongitude = extra.getDouble("currentLongitude");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentLocationName = Geolocation.reverseGeocoding(currentLocationLatitude, currentLocationLongitude, MapsActivity.this);
                    startingPoint.setText(currentLocationName);
                    startingPoint.setSelection(currentLocationName.length());
                    userLocLatLng = new LatLng(currentLocationLatitude, currentLocationLongitude);
                }
            });
        }
        else{
            Location inputLocation = new Location("location");
            userLocationLongitude = extra.getDouble("manualLongitude");
            userLocationLatitude = extra.getDouble("manualLatitude");
            userInputtedLocation = extra.getString("inputtedLocation");
            userLocLatLng = new LatLng(userLocationLatitude, userLocationLongitude);
            inputLocation.setLongitude(userLocationLongitude);
            inputLocation.setLatitude(userLocationLatitude);
            placeMarker(inputLocation);
            startingPoint.setText(userInputtedLocation);
        }
        //Initialize long and lat if the bundle is not null
        userLocation.setLongitude(userLocLatLng.longitude);
        userLocation.setLatitude(userLocLatLng.latitude);
        // Check if the last known location is available
        if (location == null) {
            //Request to update user location
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        if (location != userLocation){
            handleNewLocation(userLocation);
        }
        else {
            handleNewLocation(location);
        }
    }



    private View.OnClickListener onFABClickListener = new View.OnClickListener() {
        final Context context = MapsActivity.this;
        final Activity activity = (Activity) context;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.feed_item:
                        Intent toFeed = new Intent(getBaseContext(), FeedActivity.class);
                        Bundle toFeedActivity = new Bundle();
                        toFeedActivity.putString("startingPoint", startingPointName);
                        toFeedActivity.putString("destinationName", destinationName);
                        toFeed.putExtras(toFeedActivity);
                        startActivity(toFeed);
                        break;
                case R.id.route_menu_item:
                        Configuration cfg = new Configuration.Builder()
                            .setAnimDuration(1000)
                            .setDispalyDuration(3000)
                            .setBackgroundColor("#FFFFFF")
                            .setTextColor("#2a36b1")
                            .setTextPadding(8)
                            .setViewHeight(48)
                            .setTextLines(2)
                            .setTextGravity(Gravity.LEFT)
                            .build();
                        notificationView = NiftyNotificationView.build(activity, "Please press the space bar to confirm your pre-set starting location", Effects.slideIn, R.id.mLyout, cfg);
                         if (!cardView.isShown()) {
                             cardView.setVisibility(View.VISIBLE);
                             notificationView.show();
                      }else {
                        cardView.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

        }
    };

    private Marker placeMarker(Location location){
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng));
        return marker;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        placeMarker(googleMap.getMyLocation());
    }

    public void route()
    {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);

        Routing routing = new Routing(Routing.TravelMode.DRIVING);
        routing.registerListener(this);
        routing.execute(start, end);
    }

    @Override
    public void onRoutingFailure() {
        progressDialog.dismiss();
        Toast.makeText(this,"Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);

        if(polyline!=null)
            polyline.remove();

        polyline=null;
        //adds route to the map.
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.md_light_blue_900));
        polyOptions.width(10);
        polyOptions.addAll(mPolyOptions.getPoints());
        polyline = mMap.addPolyline(polyOptions);

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_starting_pt));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_destination));
        mMap.addMarker(options);
    }
}