package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.vreme.Vreme;
import com.example.licenta.clase.vreme.VremeAdaptor;
import com.example.licenta.util.VremeUtils.VremeJsonParser;
import com.example.licenta.util.VremeUtils.preluareHttp;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PlanificareCalatorieActivity extends AppCompatActivity {

    private LatLng latLngPozitie;
    private ListView lvVreme;
    private List<Vreme> vremeList = new ArrayList<>();
    private Button planificaCalatorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificare_calatorie);
        Intent intent = getIntent();
        initComponents();
        addAdapter();
        getVreme();
    }

    private void initComponents() {
        latLngPozitie = getIntent().getExtras().getParcelable("latLng");
        lvVreme=findViewById(R.id.lv_vreme);
        planificaCalatorie=findViewById(R.id.button_calendar);
    }

    private String getUrl(double latitude, double longitude) {
        return "https://api.openweathermap.org/data/2.5/onecall?" + "lat=" +
                latitude + "&lon=" + longitude + "&exclude=minutely,hourly,alerts" + "&appid=ee0a08c2bf32b2bf876ac03721f47be1";
    }
    private void getVreme() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String adresaUrl = getUrl(latLngPozitie.latitude, latLngPozitie.longitude);
                final String result = new preluareHttp(adresaUrl).call();
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        vremeList.addAll(VremeJsonParser.fromJson(result));
                        notifyAdapter();
                    }
                });
            }
        };
        thread.start();
    }
    private void addAdapter() {
        VremeAdaptor adaptor = new VremeAdaptor(getApplicationContext(), R.layout.listview_vreme,
                vremeList, getLayoutInflater());
        lvVreme.setAdapter(adaptor);
    }

    private void notifyAdapter() {
        VremeAdaptor adapter = (VremeAdaptor) lvVreme.getAdapter();
        adapter.notifyDataSetChanged();
    }
}