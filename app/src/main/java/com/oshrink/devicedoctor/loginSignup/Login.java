package com.oshrink.devicedoctor.loginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.oshrink.devicedoctor.AppTimeBar;
import com.oshrink.devicedoctor.MainActivity;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.navigation.NavagationEmployeeSection;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private EditText mobile_number;
    private FirebaseAuth mAuth;
    private TextView login_button, creat_account, sign_with_google;
    private AppTimeBar StatusBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        init();    // initialiation.
        send();   // Click Event.
        otplogin();
    }

    private void otplogin() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MobileNumber = mobile_number.getText().toString().trim();

                if (MobileNumber.isEmpty() || MobileNumber.length() != 10) {
                    mobile_number.setError("Enter a valid 10-digit mobile number");
                    return;
                } else {
                    // Implement Firebase Phone Authentication
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + MobileNumber, // Format the phone number with country code
                            60, // Timeout duration
                            TimeUnit.SECONDS,
                            Login.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    // Auto-retrieval or instant verification completed
                                    signInWithPhoneAuthCredential(phoneAuthCredential);
                                }
                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    // Verification failed
                                    Toast.makeText(Login.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    // The SMS verification code has been sent to the provided phone number
                                    // Save the verification ID and token to verify the code later
                                    // You can store these and use them when the user enters the OTP
                                    // For simplicity, handle code verification here itself
                                    // Implement code verification
                                    // Show OTP verification screen
                                    Intent intent = new Intent(Login.this, OTPVerification2.class);
                                    intent.putExtra("mobile_number", MobileNumber);
                                    intent.putExtra("verificationId", verificationId);
                                    startActivity(intent);
                                }
                            }
                    );
                }
            }
        });
        // Other click listeners remain the same
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign in failed
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // Invalid verification code
                                Toast.makeText(Login.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void send() {

        creat_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        sign_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, NavagationEmployeeSection.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        mobile_number = findViewById(R.id.mobile_number);

        login_button = findViewById(R.id.login_button);
        creat_account = findViewById(R.id.creat_account);
        sign_with_google = findViewById(R.id.sign_with_google);
    }
}