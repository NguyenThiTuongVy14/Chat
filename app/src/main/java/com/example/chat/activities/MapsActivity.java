package com.example.chat.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.chat.ImageProcessing;
import com.example.chat.Preference.PreferencManager;
import com.example.chat.R;
import com.example.chat.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;
    private com.example.chat.Model.Location locationOfFriend;
    private PreferencManager preferencManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar =findViewById(R.id.processbar);
        preferencManager = new PreferencManager(this);
        locationOfFriend =(com.example.chat.Model.Location) getIntent().getSerializableExtra("location");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                int w =150,h=150;
                                if (location != null) {
                                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    Bitmap bmIMG = ImageProcessing.base64ToBitmap(preferencManager.getString("image"));
                                    BitmapDescriptor customIcon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmIMG, w, h, false));
                                    mMap.addMarker(new MarkerOptions()
                                            .position(currentLocation)
                                            .title("Your Location")
                                            .icon(customIcon));
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                    if (locationOfFriend != null) {
                                        LatLng currentFriendLocation = new LatLng(locationOfFriend.getLocationX(), locationOfFriend.getLocationY());
                                        Bitmap bmIMGFr = ImageProcessing.base64ToBitmap(locationOfFriend.getImgMarker());
                                        BitmapDescriptor customIconFr = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmIMGFr, w, h, false));
                                        mMap.addMarker(new MarkerOptions()
                                                .position(currentFriendLocation)
                                                .title("Location of your friend")
                                                .icon(customIconFr));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentFriendLocation, 15));
                                    }
                                }
                                else
                                {
                                    Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
