package com.vkohler.gps;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.vkohler.gps.databinding.ActivityWaypointsListBinding;

import java.util.List;

public class WaypointsList extends AppCompatActivity {

    ActivityWaypointsListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaypointsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Addresses addresses = (Addresses)getApplicationContext();
        List<Location> savedLocations = addresses.getMyLocations();

        binding.waypointsList.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocations));
    }
}