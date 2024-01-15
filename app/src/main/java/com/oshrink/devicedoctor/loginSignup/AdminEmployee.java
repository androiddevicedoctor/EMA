package com.oshrink.devicedoctor.loginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.oshrink.devicedoctor.adminNavigation.AdminSection;
import com.oshrink.devicedoctor.AppTimeBar;
import com.oshrink.devicedoctor.R;

public class AdminEmployee extends AppCompatActivity {
    private Button employee_button_click, admin_button_click;
    private AppTimeBar StatusBarUtil;
    Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        // employee and admin Button inilization
        employee_button_click=findViewById(R.id.employee_button_click);
        admin_button_click=findViewById(R.id.admin_button_click);


        // employee and admin button click event
        employee_button_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employee=new Intent(AdminEmployee.this, CreateAccount.class);
                startActivity(employee);
            }
        });

        admin_button_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent admin=new Intent(AdminEmployee.this, AdminSection.class);
                startActivity(admin);
            }
        });
    }
}