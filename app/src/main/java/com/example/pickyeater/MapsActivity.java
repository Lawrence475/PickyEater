package com.example.pickyeater;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.CollapsibleActionView;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Toolbar;
//import com.google.android.libraries.places.api.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private Circle Rcircle;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //fusedlocation to grab the user's last known location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //creating button
        final Button LetsEatButton = findViewById(R.id.button_letseat);
        LetsEatButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Code here executes on main thread after user presses button
                // Should call on function when clicked?
                // function should generate random number and then also have the max range be the max num of results that popup
                //should then display the result (place to eat)
                Toast.makeText(MapsActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        final Button FindMeButton = findViewById(R.id.button_findme);
        FindMeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //Toast.makeText(MapsActivity.this, "Find me button clicked", Toast.LENGTH_SHORT).show();
                //can't pass user location to this onclicklistener
                //mMap.moveCamera(CameraUpdateFactory.newLatLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation)); //moving camera to the user's location
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null); //animates camera to zoom in to 16
                mMap.setMaxZoomPreference(20.0f); //setting the max zoom possible for the camera
                mMap.setMinZoomPreference(15f);
            }
        });
        //creating nav bar ???
        //CollapsingToolbarLayout layout = findViewById(R.id.collapsing_toolbar_layout);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //AppBarConfiguration appBarConfiguration =
         //       new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupWithNavController(layout, toolbar, navController, appBarConfiguration);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //should request for permissions first since we'll need their location to help them
        //if the permission is not granted then go ahead and execute the if body
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //showing the user why we need the permission
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //requesting the permission if it has not been granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        //if the permission is granted then we're good
        else
        {
            mMap = googleMap;
            //getting user last location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>()
                    {
                        @Override
                        public void onSuccess(Location location)
                        {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null)
                            {
                                userLocation = new LatLng(location.getLatitude(), location.getLongitude()); //creating latitude and longitude for user location
                                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")); //adding a description to the marker
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation)); //moving camera to the user's location
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null); //animates camera to zoom in to 16
                                mMap.setMaxZoomPreference(20.0f); //setting the max zoom possible for the camera
                                mMap.setMinZoomPreference(15f);
                                //mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null); //animates camera to zoom in to 16

                                //setting radius for the circle overlay for the size of the search
                                Rcircle = mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .radius(1000) //in meters need to change once I prompt user for the mile radius
                                        .strokeWidth(5)
                                        .strokeColor(Color.RED)
                                        .fillColor(Color.argb(128, 5, 5, 5)));
                            }
                            else //if the location of the user is null then we'll show them a toast that says location couldn't be found
                            {
                                Toast.makeText(MapsActivity.this, "Location could not be found...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
