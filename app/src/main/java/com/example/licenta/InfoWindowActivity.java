package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.protobuf.FloatValue;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class InfoWindowActivity extends AppCompatActivity {
    private Button goToMapsLocatie;
    private TextView titlu;
    private RatingBar ratingBar;
    private Button planificareCalatorie;
    private ListView lvPestiPrinsiBalta;
    private ImageView imageViewPeste;
    private LatLng latLng;
    private String placeId;
    private TextView numarRating;
    private TextView tvRatingFloat;
    private String TAG = "InfoWindowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);
        Intent intent = getIntent();
        initComponents();

        latLng = getIntent().getExtras().getParcelable("markerLatLng");
        placeId = getIntent().getExtras().getString("placeID");

        String title = (intent.getExtras().getString("markerTitle"));
        goToMapsLocatie.setOnClickListener(pornesteCalatoria());
        if (!title.equals("")) {
            String[] cutText = title.split(":");
            titlu.setText(cutText[0]);
        }

        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        Places.initialize(getApplicationContext(), "AIzaSyBMhKnzEYxZYqEnvnV2cPIv_b5RsV2bdIk");
        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            if (place.getRating() != null) {
                ratingBar.setRating(place.getRating().floatValue());
                tvRatingFloat.setText(place.getRating().toString());
            }
            if (place.getUserRatingsTotal() != null) {
                String ratingDatUsers=place.getUserRatingsTotal().toString()+" au dat review";
                numarRating.setText(ratingDatUsers);
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
    }

    @NotNull
    private View.OnClickListener pornesteCalatoria() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?daddr=" + latLng.latitude + "," + latLng.longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        };
    }

    private void initComponents() {
        goToMapsLocatie = findViewById(R.id.button_info_window_poniti_calatoria);
        titlu = findViewById(R.id.tv_activity_info_window_title);
        ratingBar = findViewById(R.id.rating);
        planificareCalatorie = findViewById(R.id.button_go_to_planificare_drumetie);
        imageViewPeste =  findViewById(R.id.infoWindowPesteImagine);
        lvPestiPrinsiBalta = findViewById(R.id.lv_pesti_locatie);
        numarRating = findViewById(R.id.tvNumarRating);
        tvRatingFloat = findViewById(R.id.tvRatingDouble);
    }
}