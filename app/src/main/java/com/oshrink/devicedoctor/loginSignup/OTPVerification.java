package com.oshrink.devicedoctor.loginSignup;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oshrink.devicedoctor.AppTimeBar;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.navigation.NavagationEmployeeSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {
    private EditText editTextNumber, editTextNumber2, editTextNumber3, editTextNumber4, editTextNumber5, editTextNumber6;
    private TextView resend_otp;
    String getotp, number, firstname, lastname, gender, dob;
    private AppTimeBar StatusBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        number = getIntent().getStringExtra("mobile_number");
        getotp = getIntent().getStringExtra("verify_otp");
        gender = getIntent().getStringExtra("gender");
        dob = getIntent().getStringExtra("dob");
//        textView4.setText(getIntent().getStringExtra("dob"));

        init();      //      initialiation
        send();      //   Click event
        numbermove();  // when otp enter
    }

    private void numbermove() {

        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editTextNumber2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                autofill();
            }
        });

        editTextNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(s.length() < 1)) {
                    editTextNumber.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editTextNumber3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                autofill();
            }
        });

        editTextNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(s.length() < 1)) {
                    editTextNumber2.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editTextNumber4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                autofill();
            }
        });

        editTextNumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(s.length() < 1)) {
                    editTextNumber3.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editTextNumber5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                autofill();
            }
        });

        editTextNumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(s.length() < 1)) {
                    editTextNumber4.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editTextNumber6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                autofill();
            }
        });

        editTextNumber6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!(s.length() < 1)) {
                    editTextNumber5.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                autofill();
            }
        });
    }

    private void autofill() {
        String Userid=generateRandomSixDigitId();
        String Text1 = editTextNumber.getText().toString();
        String Text2 = editTextNumber2.getText().toString();
        String Text3 = editTextNumber3.getText().toString();
        String Text4 = editTextNumber4.getText().toString();
        String Text5 = editTextNumber5.getText().toString();
        String Text6 = editTextNumber6.getText().toString();
        String enteredotp = Text1 + Text2 + Text3 + Text4 + Text5 + Text6; // marge 4 text in one string

        if (Text1.isEmpty() || Text2.isEmpty() || Text3.isEmpty() || Text4.isEmpty() || Text5.isEmpty() || Text6.isEmpty() || enteredotp.length() < 6 || enteredotp.length() > 6) {
            Toast.makeText(OTPVerification.this, "Check Your OTP", Toast.LENGTH_SHORT).show();
        } else {
            if (getotp != null) {
                Toast.makeText(this, "getotp != null", Toast.LENGTH_SHORT).show();
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        getotp, enteredotp
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(OTPVerification.this, "Verify OTP Sucessfully", Toast.LENGTH_SHORT).show();
                                    // Push data to Firestore here
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("First name", firstname);
                                    user.put("Last name", lastname);
                                    user.put("Mobile number", number);
                                    user.put("Gender name", gender);
                                    user.put("Date of birth", dob);
                                    user.put("User id", Userid);
                                    // Add a new document with a generated ID
                                    db.collection("users")
                                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) // Set the document ID as the user's UID
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(OTPVerification.this, "sucessfull", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(OTPVerification.this, Sucessfull.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(OTPVerification.this, "Error adding document", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(OTPVerification.this, "Enter correct otp", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
                Toast.makeText(OTPVerification.this, "Check Your internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void send() {

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendotp();
                resend_otp.setEnabled(false);
                resend_otp.setAlpha(0.5f);
                int count = 60;
            }
        });
    }

    private void resendotp() {
        Toast.makeText(this, "resend_1", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,
                60,
                TimeUnit.SECONDS,
                OTPVerification.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Toast.makeText(OTPVerification.this, "OTP send sucessfully....", Toast.LENGTH_SHORT).show();
                        getotp = s; // old OTP replaced by new OTP
                    }
                }
        );
    }

    private void init() {
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        editTextNumber3 = findViewById(R.id.editTextNumber3);
        editTextNumber4 = findViewById(R.id.editTextNumber4);
        editTextNumber5 = findViewById(R.id.editTextNumber5);
        editTextNumber6 = findViewById(R.id.editTextNumber6);

        resend_otp = findViewById(R.id.resend_otp);
    }
    private String generateRandomSixDigitId() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(randomNumber);
    }
}