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
import android.widget.ViewFlipper;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.util.dateUtils.DateConverter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.protobuf.FloatValue;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class InfoWindowActivity extends AppCompatActivity {
    private Button goToMapsLocatie;
    private TextView titlu;
    private RatingBar ratingBar;
    private Button planificareCalatorie;
    private ListView lvPestiPrinsiBalta;
    private ViewFlipper imageViewPeste;
    private LatLng latLng;
    private String placeId;
    private TextView numarRating;
    private List<Peste> pesteList = new ArrayList<>();
    private ConexiuneBD conexiuneBD=ConexiuneBD.getInstance();
    private CurrentUser currentUser = CurrentUser.getInstance();
    private AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();
    private TextView tvRatingFloat;
    private LatLng latLngLocatie;
    private final String TAG = "InfoWindowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);

        Intent intent = getIntent();
        initComponents();
        addAdapter();
        latLng = getIntent().getExtras().getParcelable("markerLatLng");
        placeId = getIntent().getExtras().getString("placeID");

        String title = (intent.getExtras().getString("markerTitle"));
        goToMapsLocatie.setOnClickListener(pornesteCalatoria());
        planificareCalatorie.setOnClickListener(pornestePlanificare());
        if (!title.equals("")) {
            String[] cutText = title.split(":");
            titlu.setText(cutText[0]);
        }

        getAllFishById(titlu.getText().toString(),callbackGetPestiById());
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.PHOTO_METADATAS,Place.Field.LAT_LNG);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        Places.initialize(getApplicationContext(), "AIzaSyBMhKnzEYxZYqEnvnV2cPIv_b5RsV2bdIk");
        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            latLngLocatie=place.getLatLng();
            if (place.getRating() != null) {
                ratingBar.setRating(place.getRating().floatValue());
                tvRatingFloat.setText(place.getRating().toString());
            }
            if (place.getUserRatingsTotal() != null) {
                String ratingDatUsers = place.getUserRatingsTotal().toString() + " au dat review";
                numarRating.setText(ratingDatUsers);
            }
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }
            for(int i=0;i<metadata.size();i++) {
                final PhotoMetadata photoMetadata = metadata.get(i);

                // Get the attribution text.
                final String attributions = photoMetadata.getAttributions();

                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(300) // Optional.
                        .setMaxHeight(200) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    ImageView image = new ImageView ( getApplicationContext() );
                    image.setImageBitmap(bitmap);
                    imageViewPeste.addView(image);
                    imageViewPeste.setFlipInterval(3000);
                    imageViewPeste.startFlipping();
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });
            }
        });
    }

    @NotNull
    private View.OnClickListener pornestePlanificare() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PlanificareCalatorieActivity.class);
                intent.putExtra("latLng",latLng);
                startActivity(intent);
            }
        };
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
    public void getAllFishById(String locatie, Callback<List<Peste>> callback) {
        Callable<List<Peste>> callable = new Callable<List<Peste>>() {
            @Override
            public List<Peste> call() throws Exception {
                List<Peste> pesteListDinBd= new ArrayList<>();
                String sql = "SELECT * FROM " + "FISH where FISHINGPONDNAME LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                String locatieModificata = modifiareTextPentruCautare(locatie);
                statement.setString(1,locatieModificata);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String specii = resultSet.getString(3);
                    int lungime =resultSet.getInt(4);
                    int greutate = resultSet.getInt(5);
                    String dataS = resultSet.getString(6);
                    String locatie = resultSet.getString(7);
                    byte[] imgByte = resultSet.getBytes(8);

                    Date date = DateConverter.toDate(dataS);
                    Peste peste = new Peste(greutate,lungime,specii,locatie,date,imgByte);
                    pesteListDinBd.add(peste);
                }
                statement.close();
                resultSet.close();
                return pesteListDinBd;
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }
    private Callback<List<Peste>> callbackGetPestiById() {
        return new Callback<List<Peste>>() {
            @Override
            public void runResultOnUiThread(List<Peste> result) {
                pesteList.clear();
                pesteList.addAll(result);
                notifyAdapter();
            }
        };
    }
    private void addAdapter() {
        PestiAdaptor adaptor = new PestiAdaptor(getApplicationContext(), R.layout.listview_pesti,
                pesteList, getLayoutInflater());
        lvPestiPrinsiBalta.setAdapter(adaptor);
    }
    private void notifyAdapter() {
        PestiAdaptor adapter = (PestiAdaptor) lvPestiPrinsiBalta.getAdapter();
        adapter.notifyDataSetChanged();
    }
    private String modifiareTextPentruCautare(String locatie) {
        String locatieModificata = locatie.trim();
        locatieModificata = locatieModificata.replaceAll(" ","%");
        locatieModificata = "%" + locatieModificata + "%";
        return  locatieModificata;
    }
}