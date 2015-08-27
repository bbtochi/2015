package com.ibm.project_traffic.Packages.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ibm.project_traffic.Packages.Adapters.PlacesAutoCompleteAdapter;
import com.ibm.project_traffic.Packages.Utils.AsyncGeocoding;
import com.ibm.project_traffic.Packages.Utils.OnTaskCompleted;
import com.ibm.project_traffic.R;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnTaskCompleted {

    private static final LatLngBounds BOUNDS_JAMAICA= new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));
    //Variables
    private GoogleApiClient googleApiClient;
    private Intent intent;
    private AutoCompleteTextView userLocationAsText;
    private TextView getUserLocationBtn;
    private String inputUserLocAddress;
    private ImageView startButton;
    private MaterialDialog progressDialog;
    private Typeface getLocationButtonTypeface, locationNameTypeface;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Build and Connect to API clients
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Views
        getUserLocationBtn = (TextView)findViewById(R.id.get_location_view);
        userLocationAsText = (AutoCompleteTextView)findViewById(R.id.enter_location_name);
        userLocationAsText.setSelection(0);

        //get the image view
        startButton = (ImageView)findViewById(R.id.start_button);

        //set the onClicklistener for the image button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the AutoCompleteTextView is empty
                if (userLocationAsText.getText().toString().matches("")) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(findViewById(R.id.enter_location_name));
                    Toast.makeText(getApplicationContext(), "Please enter a location or street name", Toast.LENGTH_SHORT).show();

                } else {
                    inputUserLocAddress = userLocationAsText.getText().toString();
                    AsyncGeocoding asyncGeocoding = new AsyncGeocoding(inputUserLocAddress, MainActivity.this);
                    asyncGeocoding.execute();
                }
            }
        });

        userLocationAsText.setAdapter(new PlacesAutoCompleteAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, googleApiClient, BOUNDS_JAMAICA, null));

        // Set Typeface for Views
        getLocationButtonTypeface = Typeface.createFromAsset(getAssets(), "fonts/texgyreadventor-bold.otf");
        locationNameTypeface = Typeface.createFromAsset(getAssets(), "fonts/texgyreadventor-regular.otf");
        getUserLocationBtn.setTypeface(getLocationButtonTypeface);
        userLocationAsText.setTypeface(locationNameTypeface);
        getUserLocationBtn.setOnClickListener(new View.OnClickListener() {
            static final int ACTION_CODE = 0;
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!enabled) {
                    gpsStatusAlert();
                } else {
                    Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    double currentLng = currentLocation.getLongitude();
                    double currentLat = currentLocation.getLatitude();
                    intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("currentLongitude", currentLng);
                    intent.putExtra("currentLatitude", currentLat);
                    intent.putExtra("requestCode", ACTION_CODE);
                    startActivityForResult(intent, ACTION_CODE);
                }
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Pops up the GPS alert
     */
    private void gpsStatusAlert() {
        new AlertDialogWrapper.Builder(this)
                .setTitle("GPS Settings")
                .setMessage("Your GPS is off. Turn it back on to use your current location")
                .setIcon(R.mipmap.ic_gps_off_black)
                .setPositiveButton("Go To GPS Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Connection failed:", connectionResult.toString());
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(MainActivity.class.getSimpleName(), "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void getResultOfTask(LatLng latLng) {
        intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("manualLongitude",latLng.longitude);
        intent.putExtra("manualLatitude", latLng.latitude);
        intent.putExtra("inputtedLocation", inputUserLocAddress);
        startActivity(intent);
    }
}
