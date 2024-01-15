package com.oshrink.devicedoctor.navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.loginSignup.Login;

import java.util.Objects;

public class Profile_frg extends Fragment {
    private TextView employee_id, employee_fullname;
    private ImageView employee_img;
    String userid;
    FirebaseAuth mAuth;
    TextView logout_account;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_frg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout_account= view.findViewById(R.id.logout_account);
        mAuth = FirebaseAuth.getInstance();
        onclick(view);
        init(view);
        getdatafromfirestore();
    }

    private void onclick(View view) {

        logout_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                finish();
                Toast.makeText(getContext(), "Log out Sucessfull", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getdatafromfirestore() {
        userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance().collection("users").document(userid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String Name = task.getResult().getString("First name");
                            String Last = task.getResult().getString("Last name");
                            String UserID=task.getResult().getString("User id");

                            String imageUrl = task.getResult().getString("profile_image_url");
                            Glide.with(requireContext())
                                    .load(imageUrl) // Load the image from the URL
                                    .into(employee_img);

                            employee_id.setText("Emp Id: "+UserID);
                            employee_fullname.setText(Name+" "+Last);


                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void init(View view) {
        employee_id = view.findViewById(R.id.employee_id);
        employee_fullname = view.findViewById(R.id.employee_fullname);
        employee_img = view.findViewById(R.id.employee_img);
    }
    private void finish() {
        finish();
    }
}