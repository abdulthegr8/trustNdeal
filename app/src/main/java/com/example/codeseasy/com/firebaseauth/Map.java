package com.example.codeseasy.com.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize MapView
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        // Set default map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add markers to the map
        LatLng location1 = new LatLng(37.4220, -122.0841);
        googleMap.addMarker(new MarkerOptions().position(location1).title("Location 1"));

        LatLng location2 = new LatLng(37.7749, -122.4194);
        googleMap.addMarker(new MarkerOptions().position(location2).title("Location 2"));

        // Set camera position
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 10));
    }

    // Handle MapView lifecycle
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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
