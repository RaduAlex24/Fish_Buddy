package com.example.licenta.homeFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.licenta.InfoWindowActivity;
import com.example.licenta.util.MapsUtils.GetNearbyPlacesData;
import com.example.licenta.R;
import com.example.licenta.util.MapsUtils.SingleShotLocationProvider;
import com.example.licenta.util.dateUtils.InfoWindowAdapter;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsFragment extends Fragment {
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location mLastKnownLocation;
    int PROXIMITY_RADIUS = 3000000;
    double latitude, longitude;
    private boolean bool = false;
    private ImageView ancora;
    private Marker markerAncora;
    LatLng latLngPozitieAncora;
    Boolean stopNotificari = false;
    AutocompleteSupportFragment autocompleteFragment;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onMapReady(@NotNull GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(@NotNull Marker marker) {
                    Intent intent = new Intent(getContext(), InfoWindowActivity.class);
                    intent.putExtra("markerTitle", marker.getTitle());
                    intent.putExtra("markerLatLng", marker.getPosition());
                    String[] cutText = Objects.requireNonNull(marker.getTitle()).split("//");
                    String placeId = cutText[1];
                    intent.putExtra("placeID", placeId);
                    startActivity(intent);
                }
            });
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            SettingsClient settingsClient = LocationServices.getSettingsClient(requireContext());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            task.addOnSuccessListener(requireActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    getDeviceLocation();

                }
            });

            task.addOnFailureListener(requireActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        try {
                            resolvable.startResolutionForResult(requireActivity(), 51);
                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            ImageView btnMyLocation = ((View) mapFragment.requireView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            btnMyLocation.setImageResource(R.drawable.navigation);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    btnMyLocation.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(40, 0, 0, 90);
            btnMyLocation.setLayoutParams(layoutParams);

            mMap.setOnMarkerClickListener(onMarkerClick());
            Places.initialize(requireContext(), "AIzaSyBMhKnzEYxZYqEnvnV2cPIv_b5RsV2bdIk");
            autocompleteFragment = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            assert autocompleteFragment != null;
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    String name = place.getName();
                    LatLng latLng = place.getLatLng();

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(name + ": //" + place.getId());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    mMap.addMarker(markerOptions);

                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(getContext(), "Eroare", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @NotNull
    private GoogleMap.OnMarkerClickListener onMarkerClick() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NotNull Marker marker) {
                float[] results = new float[3];
                Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                        marker.getPosition().latitude, marker.getPosition().longitude, results);
                mMap.setInfoWindowAdapter(new InfoWindowAdapter(getContext()));
                marker.setSnippet(String.valueOf(Math.round(results[0] / 1000)));
                return false;

            }
        };
    }

    private String getUrl(double latitude, double longitude) {

        String googlePlaceUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + latitude + "," + longitude +
                "&radius=" + PROXIMITY_RADIUS +
                "&keyword=lake" +
                "&sensor=true" +
                "&key=AIzaSyBMhKnzEYxZYqEnvnV2cPIv_b5RsV2bdIk";
        return googlePlaceUrl;
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).zoom(10).build();
                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));
                                latitude = mLastKnownLocation.getLatitude();
                                longitude = mLastKnownLocation.getLongitude();
                                String url = getUrl(latitude, longitude);
                                Object[] dataTransfer = new Object[2];
                                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                                dataTransfer[0] = mMap;
                                dataTransfer[1] = url;
                                getNearbyPlacesData.execute(dataTransfer);
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(1000);
                                locationRequest.setFastestInterval(500);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).zoom(10).build();
                                        mMap.animateCamera(CameraUpdateFactory
                                                .newCameraPosition(cameraPosition));
                                        fusedLocationClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                            }
                        }
                    }
                });
    }

    private void requestPermision() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestPermision();

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                //call function
                verificaLocatia();
                if (!stopNotificari) {
                    ha.postDelayed(this, 10000);
                }
            }
        }, 10000);

        ancora = view.findViewById(R.id.imageview_maps_ancora);
        ancora.setOnClickListener(ancoraOnClickEvent());
    }

    @NotNull
    private View.OnClickListener ancoraOnClickEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerAncora != null) {
                    markerAncora.remove();
                    markerAncoraNou(getContext());
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.title("ancora");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerAncora = mMap.addMarker(markerOptions);
                    latLngPozitieAncora = new LatLng(markerAncora.getPosition().latitude, markerAncora.getPosition().longitude);
                    stopNotificari = false;
                } else {
                    bool = true;
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    markerOptions.position(latLng);
                    markerOptions.title("ancora");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerAncora = mMap.addMarker(markerOptions);
                    latLngPozitieAncora = new LatLng(markerAncora.getPosition().latitude, markerAncora.getPosition().longitude);
                    stopNotificari = false;
                }
            }
        };
    }

    private void verificaLocatia() {
        if (bool) {
            final Handler ha = new Handler();
            ha.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //call function
                    distAncoraLocN(getContext(), latLngPozitieAncora);
                    if (!stopNotificari) {
                        ha.postDelayed(this, 10000);
                    }
                }
            }, 10000);
        }
    }

    public void distAncoraLocN(Context context, LatLng latLng) {
        if (context != null) {
            SingleShotLocationProvider.requestSingleUpdate(context,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            float[] results = new float[3];
                            Location.distanceBetween(latLng.latitude, latLng.longitude,
                                    location.latitude, location.longitude, results);
                            if (Math.round(results[0] / 1000) > 1) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "My Notification");
                                builder.setContentTitle("Ancora");
                                builder.setContentText("V-ati indepartat prea mult de ancora");
                                builder.setSmallIcon(R.drawable.menu_fish);
                                builder.setAutoCancel(true);
                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
                                managerCompat.notify(1, builder.build());
                                bool = false;
                                stopNotificari = true;
                            }
                        }
                    });
        }
    }

    public void markerAncoraNou(Context context) {
        if (context != null) {
            SingleShotLocationProvider.requestSingleUpdate(context,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            latitude = location.latitude;
                            longitude = location.longitude;
                        }
                    });
        }
    }
}