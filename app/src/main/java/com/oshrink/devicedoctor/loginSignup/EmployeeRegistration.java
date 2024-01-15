package com.oshrink.devicedoctor.loginSignup;

import static java.lang.reflect.Array.set;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.navigation.NavagationEmployeeSection;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EmployeeRegistration extends AppCompatActivity {

    private EditText desgnation, department, email, Joining_Date;
    private TextView Register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_registration);

        init();

        Register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Desgnation = desgnation.getText().toString();
                String Department = department.getText().toString();
                String Email = email.getText().toString();
                String joining_Date = Joining_Date.getText().toString();

                if (Desgnation.isEmpty()) {
                    desgnation.setError("Enter your designation");
                    return;
                } else if (Department.isEmpty()) {
                    department.setError("Enter your department");
                    return;
                } else if (Email.isEmpty()) {
                    email.setError("Enter your email");
                    return;
                } else if (joining_Date.isEmpty()) {
                    Joining_Date.setError("Enter your Joining_Date");
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference userDocRef = db.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> user = documentSnapshot.getData();
                                user.put("Desgnation", Desgnation);
                                user.put("Department", Department);
                                user.put("Email", Email);
                                user.put("Joining date", joining_Date);
                                userDocRef.set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Intent intent=new Intent(EmployeeRegistration.this, NavagationEmployeeSection.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(EmployeeRegistration.this, "Check your internet connection....", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(EmployeeRegistration.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EmployeeRegistration.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    private void init() {
        desgnation = findViewById(R.id.desgnation);
        department = findViewById(R.id.department);
        email = findViewById(R.id.email);
        Joining_Date = findViewById(R.id.Joining_Date);
        Register_button = findViewById(R.id.Register_button);

    }
}