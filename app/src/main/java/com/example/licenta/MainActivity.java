package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.homeFragments.ForumFragment;
import com.example.licenta.homeFragments.MapsFragment;
import com.example.licenta.homeFragments.ViewPagerAdapter;
import com.example.licenta.homeFragments.VirtualAssistantFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private CurrentUser currentUser = CurrentUser.getInstance();

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configNavigation();
        setareCredintentiale();
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navigationView.setNavigationItemSelectedListener(itemSelectedListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ForumFragment()).commit();
        }
    }

    private NavigationView.OnNavigationItemSelectedListener itemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId()== R.id.nav_profile){
                        Toast.makeText(MainActivity.this, "Optiunea profil a fost apasata", Toast.LENGTH_SHORT).show();
                    }
                    if(item.getItemId()==R.id.nav_setari){
                        Toast.makeText(MainActivity.this, "Optiunea setari a fost apasata", Toast.LENGTH_SHORT).show();
                    }
                    if(item.getItemId()==R.id.nav_adauga_peste){
                        Toast.makeText(MainActivity.this, "Optiunea adauga un peste a fost apasata", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

            };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_forum:
                            selectedFragment = new ForumFragment();
                            break;
                        case R.id.nav_asistent:
                            selectedFragment = new VirtualAssistantFragment();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapsFragment();
                            break;
                        default:
                            selectedFragment = new ForumFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    private void setareCredintentiale() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = headerView.findViewById(R.id.tv_main_username);
        TextView tvEmail = headerView.findViewById(R.id.tv_main_email);
        tvEmail.setText(currentUser.getEmail());
        tvUsername.setText(currentUser.getUsername());
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
    }


    // Rescriere back pt parasire aplicatie
    //Trebuie inchisa aplicatia de tot
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