package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.homeFragments.ViewPagerAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentManager fragmentManager;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initializare componente
        initComponents();

        Toast.makeText(this, currentUser.toString(), Toast.LENGTH_LONG).show();

        // Initializare pager adapter TEST******************
        initViewPagerAdapter();
    }

    // Functii
    // Initializare componente
    private void initComponents() {
        viewPager = findViewById(R.id.viewPager_main);
        fragmentManager = getSupportFragmentManager();
    }


    // Setare adapter
    private void initViewPagerAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);
    }


    // Rescriere back pt parasire aplicatie
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(),
                    getString(R.string.toast_press_back_toQuit),
                    Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}