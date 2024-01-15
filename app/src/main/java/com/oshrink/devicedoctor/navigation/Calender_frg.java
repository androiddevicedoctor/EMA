package com.oshrink.devicedoctor.navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.LatLng;
import com.oshrink.devicedoctor.Check;
import com.oshrink.devicedoctor.MainActivity;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.loginSignup.OTPVerification;
import com.oshrink.devicedoctor.loginSignup.Sucessfull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Calender_frg extends Fragment {
    private TextView out_of_ofice, on_leave, in_time, out_time;
    String formattedDate;
    int i = 0;
    private final static int REQUEST_CODE = 100;
    private LocationRequest locationRequest;
    private final static int REQUEST_CHECK_SETTING = 100;
    private FusedLocationProviderClient fusedLocationClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender_frg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        onclick();

    }
    private void onclick() {
        in_time.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
        out_time.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
        out_of_ofice.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
        on_leave.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);

        /** in time button (storing current date and location on firestore) **/
        in_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formattedDate = gettime();
                Onlocation();

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String currentAddress = "address: " + addresses.get(0).getAddressLine(0);

                                    // set data(current address and time) store on fire store.
                                    DocumentReference userDocRef = db.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                                    userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    Map<String, Object> user = documentSnapshot.getData();

                                                    userDocRef.set(user)
                                                            .addOnSuccessListener(aVoid -> {
                                                                // Data added/updated successfully to the "users" collection
                                                                // Now add data to the "Time" subcollection
                                                                Map<String, Object> timeData = new HashMap<>();
                                                                timeData.put("In time", formattedDate);
                                                                timeData.put("Current address", currentAddress);

                                                                userDocRef.collection("Time")
                                                                        .add(timeData)
                                                                        .addOnSuccessListener(documentReference -> {
                                                                            // when click on button, change background
                                                                            in_time.setText("IN TIME\n\n" + formattedDate);
                                                                            backg();
                                                                            in_time.setBackgroundResource(R.drawable.shape_yellow_roundcorner_starkwhite);
                                                                            in_time.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                                                                            // when time and date set on firestore secessfully
                                                                            popup();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Toast.makeText(getContext(), "Error adding time data", Toast.LENGTH_SHORT).show();
                                                                        });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(getContext(), "Check your internet connection....", Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                                            });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                } else {
                    askpermission();
                }
            }
        });

        /** in out button (storing current date and location on firestore) **/
        out_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formattedDate = gettime();
                Onlocation();

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String currentAddress = "address: " + addresses.get(0).getAddressLine(0);

                                    // set data(current address and time) store on fire store.
                                    DocumentReference userDocRef = db.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                                    userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    Map<String, Object> user = documentSnapshot.getData();

                                                    userDocRef.set(user)
                                                            .addOnSuccessListener(aVoid -> {
                                                                // Data added/updated successfully to the "users" collection
                                                                // Now add data to the "Time" subcollection
                                                                Map<String, Object> timeData = new HashMap<>();
                                                                timeData.put("Out time", formattedDate);
                                                                timeData.put("Current address", currentAddress);

                                                                userDocRef.collection("Time")
                                                                        .add(timeData)
                                                                        .addOnSuccessListener(documentReference -> {
                                                                            // when click on button, change background
                                                                            out_time.setText("OUT TIME\n\n" + formattedDate);
                                                                            backg();
                                                                            out_time.setBackgroundResource(R.drawable.shape_yellow_roundcorner_starkwhite);
                                                                            out_time.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                                                                            // when time and date set on firestore secessfully
                                                                            popup();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Toast.makeText(getContext(), "Error adding time data", Toast.LENGTH_SHORT).show();
                                                                        });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(getContext(), "Check your internet connection....", Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                                            });
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                } else {
                    askpermission();
                }
            }
        });

        /** in out of office / in the office button (storing current date and location on firestore) **/
        out_of_ofice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backg();
                if (i == 0) {
                    i++;
                    formattedDate = gettime();
                    Onlocation();


                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        String currentAddress = "address: " + addresses.get(0).getAddressLine(0);

                                        // set data(current address and time) store on fire store.
                                        DocumentReference userDocRef = db.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                                        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()) {
                                                        Map<String, Object> user = documentSnapshot.getData();

                                                        userDocRef.set(user)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Data added/updated successfully to the "users" collection
                                                                    // Now add data to the "Time" subcollection
                                                                    Map<String, Object> timeData = new HashMap<>();
                                                                    timeData.put("Out from office", formattedDate);
                                                                    timeData.put("Current address", currentAddress);

                                                                    userDocRef.collection("Time")
                                                                            .add(timeData)
                                                                            .addOnSuccessListener(documentReference -> {

                                                                                out_of_ofice.setText("OUT FROM OFFICE\n\n" + formattedDate);
                                                                                out_of_ofice.setBackgroundResource(R.drawable.shape_yellow_roundcorner_starkwhite);
                                                                                out_of_ofice.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                                                                                popup();
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                Toast.makeText(getContext(), "Error adding time data", Toast.LENGTH_SHORT).show();
                                                                            });
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(getContext(), "Check your internet connection....", Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                                                });
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        });
                    } else {
                        askpermission();
                    }
                } else {
                    i = 0;
                    formattedDate = gettime();
                    Onlocation();

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        String currentAddress = "address: " + addresses.get(0).getAddressLine(0);

                                        // set data(current address and time) store on fire store.
                                        DocumentReference userDocRef = db.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                                        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()) {
                                                        Map<String, Object> user = documentSnapshot.getData();

                                                        userDocRef.set(user)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Data added/updated successfully to the "users" collection
                                                                    // Now add data to the "Time" subcollection
                                                                    Map<String, Object> timeData = new HashMap<>();
                                                                    timeData.put("Inside the office", formattedDate);
                                                                    timeData.put("Current address", currentAddress);

                                                                    userDocRef.collection("Time")
                                                                            .add(timeData)
                                                                            .addOnSuccessListener(documentReference -> {

                                                                                out_of_ofice.setText("INSIDE THE OFFICE\n\n" + formattedDate);
                                                                                out_of_ofice.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
                                                                                out_of_ofice.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                                                                                popup();
                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                Toast.makeText(getContext(), "Error adding time data", Toast.LENGTH_SHORT).show();
                                                                            });
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(getContext(), "Check your internet connection....", Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                                                });
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        });
                    } else {
                        askpermission();
                    }
                }
            }
        });

        /** On leave button (storing current date only on firestore) **/
        on_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(); // Assuming you have a Date object
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Define your desired date format
                String formattedDate = dateFormat.format(date); // Format the date
                backg();
                on_leave.setBackgroundResource(R.drawable.shape_yellow_roundcorner_starkwhite);
                on_leave.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                on_leave.setText("ON LEAVE\n\n" + formattedDate);

                // set data(current address and time) store on fire store.
                DocumentReference userDocRef = db.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> user = documentSnapshot.getData();

                                userDocRef.set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            // Data added/updated successfully to the "users" collection
                                            // Now add data to the "Time" subcollection
                                            Map<String, Object> timeData = new HashMap<>();
                                            timeData.put("On leave", formattedDate);

                                            userDocRef.collection("Time")
                                                    .add(timeData)
                                                    .addOnSuccessListener(documentReference -> {
                                                        popup();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(getContext(), "Error adding time data", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Check your internet connection....", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    /** getting current time**/
    private String gettime() {
        Date date = new Date(); // Assuming you have a Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss | dd-MM-yyyy"); // Define your desired date format
        return dateFormat.format(date);
    }

    /** button initionlication**/
    private void init(View view) {
        out_of_ofice = view.findViewById(R.id.out_of_ofice);
        on_leave = view.findViewById(R.id.on_leave);
        in_time = view.findViewById(R.id.in_time);
        out_time = view.findViewById(R.id.out_time);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    /** when click on button then change button color **/
    private void backg() {
        in_time.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
        out_time.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
        out_of_ofice.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);
        on_leave.setBackgroundResource(R.drawable.shape_blue_roundcorner_starkwhite);

        in_time.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        out_time.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        out_of_ofice.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        on_leave.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

//    allow Location permission
    private void askpermission() {
        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void Onlocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response=task.getResult(ApiException.class);
//                    Toast.makeText(getContext(), "a"+response+"location", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                // Prompt the user to adjust location settings
                                resolvableApiException.startResolutionForResult((Activity) getContext(),REQUEST_CHECK_SETTING);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CHECK_SETTING){
            switch (requestCode){
                case MainActivity.RESULT_OK:
                    Toast.makeText(getContext(), "gps is on", Toast.LENGTH_SHORT).show();
                    break;
                case MainActivity.RESULT_CANCELED:
                    Toast.makeText(getContext(), "is required", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /** sucess popup **/
    private void popup(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.buttom_pop_up);
        bottomSheetDialog.show();
    }
}
