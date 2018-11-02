package project.cse5236.parleypirate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class SelectLocationActivity extends AppCompatActivity {

    private static final String TAG = "SetLocationActivity";
    private static final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 1;
    private MapView mMapView;

    //default latitude and longitude is Ohio Stadium :^)
    private static final double DEFAULT_LAT = 40.001633;
    private static final double DEFAULT_LONG = -83.019707;

    private LocationComponent locationComponent;

    private double latitude = DEFAULT_LAT;
    private double longitude = DEFAULT_LONG;

    private MapboxMap mMapboxMap;

    private static final double zoom = 14.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_select_location);
        mMapView = findViewById(R.id.location_map_view);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            locationComponent = mapboxMap.getLocationComponent();
            activateLocationComponent();
            /*
            mMapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(getString(R.string.title_meeting_location))
                    .snippet(getString(R.string.x_marks_the_spot));
                    */
            mMapboxMap.setCameraPosition(
                    new CameraPosition.Builder().target(
                            new LatLng(latitude,longitude)).zoom(zoom).build());
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void activateLocationComponent() {
        //check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationComponent.activateLocationComponent(this);
            Location lastKnowLoc = locationComponent.getLastKnownLocation();
            if(lastKnowLoc!=null) {
                setLatLong(lastKnowLoc.getLatitude(), lastKnowLoc.getLongitude());
            }
            locationComponent.setLocationComponentEnabled(true);
        } else {
            // Show rationale and request permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CODE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    activateLocationComponent();
                }
                break;
            }
        }
    }

    private void setLatLong(double lat, double longi){
        latitude = lat;
        longitude = longi;
    }
}
