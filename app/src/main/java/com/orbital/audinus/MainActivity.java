package com.orbital.audinus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout Tablayout;
    private FragmentContainerView fragmentContainerView;


    //BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!checkPermission()) {
            requestPermission();
            if (Build.VERSION.SDK_INT < 30) {
                onCreate(savedInstanceState);
            }
        }
            setContentView(R.layout.activity_main);
            Tablayout = findViewById(R.id.views);
            viewPager = findViewById(R.id.viewpager);
            fragmentContainerView = findViewById(R.id.currently_playing_bar);

            Tablayout.setupWithViewPager(viewPager);

            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragmentAdapter.addFragment(new SongsFragment(), "songs");
            fragmentAdapter.addFragment(new PlaylistsFragment(), "playlist");
            fragmentAdapter.addFragment(new fragment3(), "queue");
            viewPager.setAdapter(fragmentAdapter);
    }


    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
            Toast.makeText(this, "Storage permissions are needed to manage your music", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}