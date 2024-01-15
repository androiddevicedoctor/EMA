package com.oshrink.devicedoctor.navigation;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.loginSignup.OTPVerification;
import com.oshrink.devicedoctor.loginSignup.Sucessfull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskList_frg extends Fragment {
    private TextView employee_id, employee_fullname, task_title, description, date_of_assign_task, date_of_complete_task;
    private ImageView employee_img;
    String userid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list_frg, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        getDataFromFirestore();
    }
    private void getDataFromFirestore() {
        userid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseFirestore.getInstance().collection("users").document(userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String UserID=task.getResult().getString("User id");
                            String Name=task.getResult().getString("First name");
                            String Last = task.getResult().getString("Last name");

                            String Title=task.getResult().getString("Task Title");
                            String Description = task.getResult().getString("Description");
                            String assign=task.getResult().getString("Date of assign task");
                            String complete=task.getResult().getString("Date of complete task");

                            String imageUrl = task.getResult().getString("profile_image_url");
                            Glide.with(requireContext())
                                    .load(imageUrl) // Load the image from the URL
                                    .into(employee_img);

                            employee_id.setText("Employee Id: "+UserID);
                            employee_fullname.setText(Name+" "+Last);

                            task_title.setText(Title);
                            description.setText(Description);
                            date_of_assign_task.setText(assign);
                            date_of_complete_task.setText(complete);

                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void init(View view){
        employee_id=view.findViewById(R.id.employee_id);
        employee_fullname=view.findViewById(R.id.employee_fullname);
        task_title=view.findViewById(R.id.task_title);
        description=view.findViewById(R.id.description);
        date_of_assign_task=view.findViewById(R.id.date_of_assign_task);
        date_of_complete_task=view.findViewById(R.id.date_of_complete_task);
        employee_img=view.findViewById(R.id.employee_img);
    }
}