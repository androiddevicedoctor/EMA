package com.oshrink.devicedoctor.navigation;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.loginSignup.EmployeeRegistration;
import com.oshrink.devicedoctor.loginSignup.Login;
import com.oshrink.devicedoctor.loginSignup.Sucessfull;

import java.util.Objects;

public class Home_frg extends Fragment {
    private TextView employee_id, employee_fullname, designation_text, department_text, dob_text, mobile_no_text, Joining_text,email_text;
    private ImageView employee_img;
    String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_frg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        getdatafromfirestore();

        /** ===================================================== **/

        employee_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), EmployeeRegistration.class);
                startActivity(intent);
            }
        });

        /** ===================================================== **/
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

                            String Designation=task.getResult().getString("Desgnation");
                            String Department=task.getResult().getString("Department");
                            String Date_of_birth=task.getResult().getString("Date of birth");
                            String Phone_number=task.getResult().getString("Mobile number");
                            String email=task.getResult().getString("Email");
                            String Date_of_joining=task.getResult().getString("Joining date");

                            String imageUrl = task.getResult().getString("profile_image_url");
                            Glide.with(requireContext())
                                    .load(imageUrl) // Load the image from the URL
                                    .into(employee_img);

                            employee_id.setText("Emp Id: "+UserID);
                            employee_fullname.setText(Name+" "+Last);
                            designation_text.setText(Designation);
                            department_text.setText(Department);
                            dob_text.setText(Date_of_birth);
                            mobile_no_text.setText("+91 "+Phone_number);
                            email_text.setText(email);
                            Joining_text.setText(Date_of_joining);

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
        designation_text = view.findViewById(R.id.designation_text);
        department_text = view.findViewById(R.id.department_text);
        dob_text = view.findViewById(R.id.dob_text);
        mobile_no_text = view.findViewById(R.id.mobile_no_text);
        Joining_text = view.findViewById(R.id.Joining_text);
        email_text = view.findViewById(R.id.email_text);

        employee_img = view.findViewById(R.id.employee_img);
    }
}