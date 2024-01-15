package com.oshrink.devicedoctor.loginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.oshrink.devicedoctor.AppTimeBar;
import com.oshrink.devicedoctor.MainActivity;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.navigation.NavagationEmployeeSection;

public class Sucessfull extends AppCompatActivity {
    private AppTimeBar StatusBarUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucessfull);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Sucessfull.this, EmployeeRegistration.class);
//                Intent intent=new Intent(MainActivity.this, Check.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}