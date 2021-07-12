package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.homeFragments.ForumFragment;
import com.example.licenta.homeFragments.MapsFragment;
import com.example.licenta.homeFragments.VirtualAssistantFragment;
import com.example.licenta.introTutorialSlider.IntroTutorialSlider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000;
    private static final int PICK_IMAGE = 123;
    public static final String FIRST_TIME_IN_APP_SP = "FIRST_TIME_IN_APP_SP";
    private long mBackPressed;

    private CurrentUser currentUser = CurrentUser.getInstance();
    private SharedPreferences sharedPreferences;

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private ImageView imagineUtilizator;
    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap poza_scalata;
    private boolean wasImgApasata;
    private byte[] imagineByte;

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
                    ForumFragment.newInstance(null)).commit();
        }

        // Tutorial pentru prima intrare in aplicatie
        aplicareTutorial();
    }


    private NavigationView.OnNavigationItemSelectedListener itemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.nav_profile) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }
                    if (item.getItemId() == R.id.nav_setari) {
                        Intent intent = new Intent(getApplicationContext(), Setari.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }
                    if (item.getItemId() == R.id.nav_adauga_peste) {
                        Intent intent = new Intent(getApplicationContext(), VizualizatiPesti.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }
                    if (item.getItemId() == R.id.nav_logout) {
                        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
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
                        case R.id.nav_asistent:
                            selectedFragment = new VirtualAssistantFragment();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapsFragment();
                            break;
                        default:
                            selectedFragment = ForumFragment.newInstance(null);
                    }
                    closeKeyboard();
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
                setareCredintentiale();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
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
                if(wasImgApasata){
                    Toast.makeText(getApplicationContext(), "ASDSDSDSDSDS", Toast.LENGTH_LONG).show();
                    wasImgApasata=false;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    // AM COMENTAT AICI CA CRAPA LA STERGERE POSTARE FORUM SI NU INTELEG DC
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                wasImgApasata=true;
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int currentBitmapWidth = selectedImage.getWidth();
                int currentBitmapHeight = selectedImage.getHeight();
                int ivWidth = imagineUtilizator.getWidth();
                int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) ivWidth / (double) currentBitmapWidth));

                Bitmap newbitMap = Bitmap.createScaledBitmap(selectedImage, ivWidth, newHeight, true);
                imagineUtilizator.setImageBitmap(newbitMap);
                byte[] asd=getBitmapAsByteArray(newbitMap);
                long lengthbmp = asd.length;
                if (lengthbmp / 1024.0 / 1024.0 >= 2) {
                    Toast.makeText(getApplicationContext(), "Dimensiunea imaginii este prea mare", Toast.LENGTH_LONG).show();
                } else {
                    poza_scalata = Bitmap.createScaledBitmap(selectedImage, 280, 280, true);
                    imagineUtilizator.setImageBitmap(poza_scalata);
                    imagineByte = getBitmapAsByteArray(poza_scalata); // Asta trebuie adaugata in baza de date in blob
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return outputStream.toByteArray();
    }

    // Rescriere back pt parasire aplicatie
    //Trebuie inchisa aplicatia de tot
    // Nu se poate
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


    // Functie de aplicare tutorial daca este prima data cand se intra in aplicatie
    private void aplicareTutorial(){
        // Preluare shared preferences
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        boolean firstTimeInApplication = sharedPreferences.getBoolean(FIRST_TIME_IN_APP_SP, true);

        // Verificare aplicare tutorial
        if(firstTimeInApplication){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_TIME_IN_APP_SP, false);
            editor.apply();

            Intent intent = new Intent(getApplicationContext(), IntroTutorialSlider.class);
            startActivity(intent);
        }
    }


    // Inchidere tastatura
    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this
                    .getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}