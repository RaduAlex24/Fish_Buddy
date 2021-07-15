package com.example.licenta;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;
import com.example.licenta.clase.vreme.Vreme;
import com.example.licenta.clase.vreme.VremeAdaptor;
import com.example.licenta.util.VremeUtils.VremeJsonParser;
import com.example.licenta.util.VremeUtils.preluareHttp;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class PlanificareCalatorieActivity extends AppCompatActivity {

    private LatLng latLngPozitie;
    private ListView lvVreme;
    private String numeLocatie;
    private List<Vreme> vremeList = new ArrayList<>();
    private Button planificaCalatorie;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificare_calatorie);
        Intent intent = getIntent();
        initComponents();
        addAdapter();
        getVreme();
        planificaCalatorie.setOnClickListener(goToCalendar());
    }

    @NotNull
    private View.OnClickListener goToCalendar() {
        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int mYear = myCalendar.get(Calendar.YEAR);
                int mMonth = myCalendar.get(Calendar.MONTH);
                int mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(PlanificareCalatorieActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                calIntent.setData(CalendarContract.Events.CONTENT_URI);
                                startActivity(calIntent);
                                calIntent.setType("vnd.android.cursor.item/event");
                                calIntent.putExtra(CalendarContract.Events.TITLE, "Zi de pescuit");
                                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, numeLocatie);
                                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Zi de pescuit");
                                GregorianCalendar startDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                                GregorianCalendar endDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                        endDate.getTimeInMillis());
                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                        startDate.getTimeInMillis());

                                startActivity(calIntent);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        };
    }

    private void initComponents() {
        latLngPozitie = getIntent().getExtras().getParcelable("latLng");
        numeLocatie = getIntent().getExtras().getString("numeLocatie");
        lvVreme = findViewById(R.id.lv_vreme);
        planificaCalatorie = findViewById(R.id.button_calendar);
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