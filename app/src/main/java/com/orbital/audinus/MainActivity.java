package com.orbital.audinus;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout Tablayout;
    private FragmentContainerView fragmentContainerView;


    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {
            requestPermission();
        }

        Tablayout = findViewById(R.id.views);
        viewPager = findViewById(R.id.viewpager);
        fragmentContainerView = findViewById(R.id.fragmentContainerView2);




        Tablayout.setupWithViewPager(viewPager);


        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentAdapter.addFragment(new fragment1(), "songs");
        fragmentAdapter.addFragment(new fragment2(), "playlist");
        fragmentAdapter.addFragment(new fragment3(), "favorites");
        viewPager.setAdapter(fragmentAdapter);

    }




    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Read permissions are required, please enable in settings", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


}