package project.cse5236.parleypirate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class SelectLocationActivity extends AppCompatActivity {

    private static final String TAG = "SetLocationActivity";
    private static final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 1;
    private static final String LAYER_ID = "location_selection_layer";
    private static final String SOURCE_ID = "vector-source";
    private MapView mMapView;

    //default latitude and longitude is Ohio Stadium :^)
    private static final double DEFAULT_USER_LAT = 40.001633;
    private static final double DEFAULT_USER_LONG = -83.019707;

    private LocationComponent locationComponent;

    private double latitude = DEFAULT_USER_LAT;
    private double longitude = DEFAULT_USER_LONG;

    private MapboxMap mMapboxMap;
    private Button mSetLocationButton;
    private ImageView dropPinView;

    private static final double zoom = 14.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSetLocationButton = findViewById(R.id.button_set_location);
        mSetLocationButton.setOnClickListener(v-> {
            if (v.getId() == R.id.button_set_location) {
                setLocation();
            }
        });

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_select_location);
        mMapView = findViewById(R.id.location_map_view);

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            locationComponent = mapboxMap.getLocationComponent();
            activateLocationComponent();
            mMapboxMap.setCameraPosition(
                    new CameraPosition.Builder().target(
                            new LatLng(latitude,longitude)).zoom(zoom).build());

            // Create drop pin using custom image
            dropPinView = new ImageView(this);
            dropPinView.setImageResource(R.drawable.ic_x_marks_the_spot);

            // Statically Set drop pin in center of screen
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            float density = getResources().getDisplayMetrics().density;
            params.bottomMargin = (int) (12 * density);
            dropPinView.setLayoutParams(params);
            mMapView.addView(dropPinView);
        });
    }

    private void setLocation() {
        // Get LatLng of selected location
        LatLng position = mMapboxMap.getProjection().fromScreenLocation(
                new PointF(dropPinView.getLeft() + (dropPinView.getWidth() / 2),
                        dropPinView.getBottom()));
        GeoPoint location = new GeoPoint(position.getLatitude(),position.getLongitude());
        meeting.setLocation(location);

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
                setUserLatLong(lastKnowLoc.getLatitude(), lastKnowLoc.getLongitude());
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

    private void setUserLatLong(double lat, double longi){
        latitude = lat;
        longitude = longi;
    }
}
