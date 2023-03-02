package com.example.codeseasy.com.firebaseauth;

import android.content.pm.PackageManager;
import android.location.Location;
import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Map extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        // Set default map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            // Get the user's current location
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(),
                                        location.getLongitude());

                                // Add marker for current location
                                googleMap.addMarker(new MarkerOptions()
                                        .position(currentLocation)
                                        .title("My Location"));

                                // Move camera to current location
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

                                // Search for nearby safe locations
                                String placeType = "cafe|department_store|shopping_mall|police";
                                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                                        "?location=" + location.getLatitude() + "," + location.getLongitude() +
                                        "&radius=5000" +
                                        "&type=" + placeType +
                                        "&key=" + getString(R.string.google_maps_api_key);

                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray results = response.getJSONArray("results");
                                                    for (int i = 0; i < results.length(); i++) {
                                                        JSONObject result = results.getJSONObject(i);
                                                        JSONObject location = result.getJSONObject("geometry")
                                                                .getJSONObject("location");
                                                        LatLng latLng = new LatLng(location.getDouble("lat"),
                                                                location.getDouble("lng"));
                                                        String name = result.getString("name");
                                                        googleMap.addMarker(new MarkerOptions()
                                                                .position(latLng)
                                                                .title(name));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                });

                                Volley.newRequestQueue(Map.this).add(request);

                            }
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}

