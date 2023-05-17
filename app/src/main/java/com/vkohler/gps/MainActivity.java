package com.vkohler.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vkohler.gps.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setLocationRequest();
        setLocationCallBack();
        setListeners();
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(getApplicationContext(), "This app requires permission to use GPS", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void setLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void setLocationCallBack() {
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(Objects.requireNonNull(locationResult.getLastLocation()));
            }
        };
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    private void updateUIValues(Location location) {
        binding.lat.setText(String.valueOf(location.getLatitude()));
        binding.lon.setText(String.valueOf(location.getLongitude()));
        binding.accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()) {
            binding.altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            binding.altitude.setText("Not availabe");
        }

        if(location.hasSpeed()) {
            binding.speed.setText(String.valueOf(location.getSpeed()));
        } else {
            binding.speed.setText("Not availabe");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            binding.address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            binding.address.setText("Unable to get street address");
        }
    }

    private void setListeners() {
        binding.gps.setOnClickListener(v -> {
            TextView sensor = binding.sensor;
            if (binding.gps.isChecked()) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                sensor.setText("Using GPS sensors");
            } else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                sensor.setText("Using Towers + WIFI");
            }
        });

        binding.locationsUpdates.setOnClickListener(v -> {
            if(binding.locationsUpdates.isChecked()) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        });
    }

    private void stopLocationUpdates() {
        binding.updates.setText("Location is not being tracked");
        binding.lat.setText("Not tracking location");
        binding.lon.setText("Not tracking location");
        binding.speed.setText("Not tracking location");
        binding.accuracy.setText("Not tracking location");
        binding.address.setText("Not tracking location");
        binding.accuracy.setText("Not tracking location");
        binding.altitude.setText("Not tracking location");
        binding.sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        binding.updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }
}