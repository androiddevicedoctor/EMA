package com.oshrink.devicedoctor.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.oshrink.devicedoctor.AppTimeBar;
import com.oshrink.devicedoctor.MainActivity;
import com.oshrink.devicedoctor.R;
import com.oshrink.devicedoctor.adapter.AdapterViewPager;
import com.oshrink.devicedoctor.employeeSectionCommunication.Communication;
import com.oshrink.devicedoctor.loginSignup.AdminEmployee;

import java.util.ArrayList;

public class NavagationEmployeeSection extends AppCompatActivity {
    private AppTimeBar StatusBarUtil;
    ViewPager2 pager_main;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    BottomNavigationView bottomNav;
    private ImageView communication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navagation_employee_section);

        if (Build.VERSION.SDK_INT >= 10) {
            StatusBarUtil.setStatusBarColor(this, R.color.blue_primary);
        }

        pager_main = findViewById(R.id.pager_main);
        bottomNav = findViewById(R.id.bottomNav);
        communication = findViewById(R.id.communication);

        communication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NavagationEmployeeSection.this, Communication.class);
                startActivity(intent);
                finish();
            }
        });

        fragmentArrayList.add(new Home_frg());
        fragmentArrayList.add(new PersonalStatistics_frg());
        fragmentArrayList.add(new Calender_frg());
        fragmentArrayList.add(new TaskList_frg());
        fragmentArrayList.add(new Profile_frg());

        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentArrayList);

        pager_main.setAdapter(adapterViewPager);

        pager_main.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.navigation_watch);
                        break;
                    case 2:
                        bottomNav.setSelectedItemId(R.id.navigation_calender);
                        break;
                    case 3:
                        bottomNav.setSelectedItemId(R.id.navigation_list);
                        break;
                    case 4:
                        bottomNav.setSelectedItemId(R.id.navigation_profile);
                        break;

                }
                super.onPageSelected(position);
            }
        });
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNav.getMenu().findItem(R.id.navigation_home).setIcon(R.drawable.home_white_nav_24);
                bottomNav.getMenu().findItem(R.id.navigation_home).setChecked(true);

                if (item.getItemId() == R.id.navigation_home) {
                    pager_main.setCurrentItem(0);
                } else if (item.getItemId() == R.id.navigation_watch) {
                    pager_main.setCurrentItem(1);
                } else if (item.getItemId() == R.id.navigation_calender) {
                    pager_main.setCurrentItem(2);
                } else if (item.getItemId() == R.id.navigation_list) {
                    pager_main.setCurrentItem(3);
                } else if (item.getItemId() == R.id.navigation_profile) {
                    pager_main.setCurrentItem(4);
                }
                return true;
            }
        });
    }
}