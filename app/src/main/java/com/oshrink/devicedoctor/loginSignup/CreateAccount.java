package com.oshrink.devicedoctor.loginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.oshrink.devicedoctor.AppTimeBar;
import com.oshrink.devicedoctor.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateAccount extends AppCompatActivity {
    private EditText first_name, last_name, mobile_number;
    private Spinner spinner_day, spinner_month, spinner_year;
    private TextView signup_button, already_account, textView8;
    private String selectedRadioButtonText="";
    private AppTimeBar StatusBarUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        init();    // initialiation.
        termpolicy();  // term and policy intent
        send();
        DOBSpinner();  // user enter date of birth.
    }

    private void send() {
        RadioGroup genderRadioGroup = findViewById(R.id.gender_radio_group);
        RadioButton male_radio_button = findViewById(R.id.male_radio_button);
        RadioButton female_radio_button = findViewById(R.id.female_radio_button);
        RadioButton custom_radio_button = findViewById(R.id.custom_radio_button);
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Perform actions based on the selected RadioButton
            male_radio_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_unchacked_24, 0);
            female_radio_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_unchacked_24, 0);
            custom_radio_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_unchacked_24, 0);
            if (checkedId == R.id.male_radio_button) {
                male_radio_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checked_24, 0);
                selectedRadioButtonText = male_radio_button.getText().toString();
            } else if (checkedId == R.id.female_radio_button) {
                // Handle selection of Female option
                female_radio_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checked_24, 0);
                selectedRadioButtonText = female_radio_button.getText().toString();

            } else if (checkedId == R.id.custom_radio_button) {
                // Handle selection of Female option
                custom_radio_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checked_24, 0);
                selectedRadioButtonText = custom_radio_button.getText().toString();
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get text from textview.
                String Name = first_name.getText().toString();
                String LastName = last_name.getText().toString();
                String MobileNumber = mobile_number.getText().toString();

                String SelectedDay = spinner_day.getSelectedItem().toString();
                String SelectedMonth = spinner_month.getSelectedItem().toString();
                String SelectedYeay = spinner_year.getSelectedItem().toString();

                String DOB = SelectedDay + "-" + SelectedMonth + "-" + SelectedYeay;

                if (Name.isEmpty()) {
                    first_name.setError("Enter your good Name");
                    return;
                } else if (Name.length() > 10) {
                    first_name.setError("Your name Length is to big.");
                    return;
                } else if (LastName.isEmpty()) {
                    last_name.setError("Enter your last name");
                    return;
                } else if (LastName.length() > 10) {
                    last_name.setError("Your last name length is to big.");
                    return;
                } else if (MobileNumber.isEmpty()) {
                    mobile_number.setError("Enter your mobile number");
                    return;
                } else if (MobileNumber.length() > 10) {
                    mobile_number.setError("Enter correct mobile number");
                    return;
                } else if (MobileNumber.length() < 10) {
                    mobile_number.setError("Enter correct mobile number");
                    return;
                }
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + MobileNumber,
                        60,
                        TimeUnit.SECONDS,
                        CreateAccount.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(CreateAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Toast.makeText(CreateAccount.this, "OTP send sucessfully....", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateAccount.this, OTPVerification.class);
                                intent.putExtra("mobile_number", MobileNumber);
                                intent.putExtra("verify_otp", s);
                                intent.putExtra("firstname", Name);
                                intent.putExtra("lastname", LastName);
                                intent.putExtra("dob", DOB);
                                intent.putExtra("gender", selectedRadioButtonText);
                                startActivity(intent);
                            }
                        }
                );
            }
        });

        already_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccount.this, Login.class);
                startActivity(intent);
            }
        });
    }
    private void DOBSpinner() {
        // Populate day spinner
        List<String> dayList = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            dayList.add(String.valueOf(i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner, dayList);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setAdapter(dayAdapter);

        // Populate month spinner
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(monthAdapter);

        // Populate year spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> yearList = new ArrayList<>();
        for (int i = 2005; i >= 1970; i--) {
            yearList.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(yearAdapter);
    }
    private void init() {
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        mobile_number = findViewById(R.id.mobile_number);

        spinner_day = findViewById(R.id.spinner_day);
        spinner_month = findViewById(R.id.spinner_month);
        spinner_year = findViewById(R.id.spinner_year);

        signup_button = findViewById(R.id.signup_button);
        already_account = findViewById(R.id.already_account);

        textView8 = findViewById(R.id.textView8);
    }
    private void termpolicy() {
        String Text = "By clicking Sign Up, you agreed to our Terms, Privacy and Cookies Policy You may receive SMS notifications from us and can otp out at any time.";

        SpannableString spannableString = new SpannableString(Text);

        ClickableSpan termAndCondation = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(CreateAccount.this, "Term & condition page is not available.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.YELLOW);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan privacyAndPolicy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(CreateAccount.this, "Privacy & policy page is not available.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.YELLOW);
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(termAndCondation, 39, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyAndPolicy, 46, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView8.setText(spannableString);
        textView8.setMovementMethod(LinkMovementMethod.getInstance());
    }
}