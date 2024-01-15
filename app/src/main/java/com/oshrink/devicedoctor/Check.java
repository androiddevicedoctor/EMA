package com.oshrink.devicedoctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Check extends AppCompatActivity {
    private Button button;
    private LocationRequest locationRequest;
    private final static int REQUEST_CHECK_SETTING = 100;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(2000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                        .checkLocationSettings(builder.build());

                result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response=task.getResult(ApiException.class);
                            Toast.makeText(Check.this, "a"+response+"location ", Toast.LENGTH_SHORT).show();
                        } catch (ApiException e) {

                            switch (e.getStatusCode()) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                        // Prompt the user to adjust location settings
                                        resolvableApiException.startResolutionForResult(Check.this,REQUEST_CHECK_SETTING);
                                    } catch (IntentSender.SendIntentException sendEx) {
                                        // Ignore the error
                                    }
                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;

                            }

                        }
                    }
                });

            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==REQUEST_CHECK_SETTING){
//            switch (requestCode){
//                case MainActivity.RESULT_OK:
//                    Toast.makeText(this, "gps is on", Toast.LENGTH_SHORT).show();
//                    break;
//                case MainActivity.RESULT_CANCELED:
//                    Toast.makeText(this, "is required", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    }
}