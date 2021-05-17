package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.homeFragments.ForumFragment;
import com.example.licenta.homeFragments.MapsFragment;
import com.example.licenta.homeFragments.VirtualAssistantFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000;
    private static final int PICK_IMAGE = 123;
    private long mBackPressed;

    private CurrentUser currentUser = CurrentUser.getInstance();

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private ImageView imagineUtilizator;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configNavigation();

        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navigationView.setNavigationItemSelectedListener(itemSelectedListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ForumFragment()).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.button, menu);
        return true;
    }

    private NavigationView.OnNavigationItemSelectedListener itemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.nav_profile) {
                        Toast.makeText(MainActivity.this, "Optiunea profil a fost apasata", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Profil.class);
                        startActivity(intent);
                    }
                    if (item.getItemId() == R.id.nav_setari) {
                        Toast.makeText(MainActivity.this, "Optiunea setari a fost apasata", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Setari.class);
                        startActivity(intent);
                    }
                    if (item.getItemId() == R.id.nav_adauga_peste) {
                        Toast.makeText(MainActivity.this, "Optiunea adauga un peste a fost apasata", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ListaPesti.class);
                        startActivity(intent);
                    }
                    if (item.getItemId() == R.id.nav_logout) {
                        Toast.makeText(MainActivity.this, "Optiunea Logout a fost apasata", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                        startActivity(intent);
                        currentUser.delelteInstance();
                        finish();
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
        TextView tvPoints = headerView.findViewById(R.id.tv_points_mainNavHeader);
        tvEmail.setText(currentUser.getEmail());
        tvUsername.setText(currentUser.getUsername());
        tvPoints.setText("Puncte: " + currentUser.getPoints());
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
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setareCredintentiale();
                imagineUtilizator = findViewById(R.id.imagineUtilizator);
                imagineUtilizator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, PICK_IMAGE);
                    }
                });
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                int currentBitmapWidth = selectedImage.getWidth();
                int currentBitmapHeight = selectedImage.getHeight();

                int ivWidth = imagineUtilizator.getWidth();
                int ivHeight = imagineUtilizator.getHeight();
                int newWidth = ivWidth;
                int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

                Bitmap newbitMap = Bitmap.createScaledBitmap(selectedImage, newWidth, newHeight, true);

                imagineUtilizator.setImageBitmap(newbitMap);
              //  imagineUtilizator.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            //Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    // Rescriere back pt parasire aplicatie
    //Trebuie inchisa aplicatia de tot
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            currentUser.delelteInstance();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(),
                    getString(R.string.toast_press_back_toQuit),
                    Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }
}