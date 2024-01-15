package com.oshrink.devicedoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.oshrink.devicedoctor.loginSignup.AdminEmployee;
import com.oshrink.devicedoctor.loginSignup.OTPVerification;
import com.oshrink.devicedoctor.navigation.NavagationEmployeeSection;

public class MainActivity extends AppCompatActivity {
    private AppTimeBar StatusBarUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
                    Intent is=new Intent(MainActivity.this, NavagationEmployeeSection.class);
                    startActivity(is);
                    finish();
                    return;
                }
                Intent intent=new Intent(MainActivity.this, AdminEmployee.class);
//                Intent intent=new Intent(MainActivity.this, Check.class);
//                Intent intent=new Intent(MainActivity.this, OTPVerification.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }
}