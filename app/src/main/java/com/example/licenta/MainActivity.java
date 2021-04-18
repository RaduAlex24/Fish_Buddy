package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.homeFragments.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentManager fragmentManager;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private CurrentUser currentUser = CurrentUser.getInstance();

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initializare componente
        initComponents();

        Toast.makeText(this, currentUser.toString(), Toast.LENGTH_LONG).show();

        // Initializare pager adapter
        initViewPagerAdapter();
        configNavigation();
    }

    private void configNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        TextView tvUsername = findViewById(R.id.tv_main_username);
        TextView tvEmail = findViewById(R.id.tv_main_email);
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