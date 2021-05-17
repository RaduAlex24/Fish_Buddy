package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.licenta.clase.peste.Peste;
import com.google.android.material.textfield.TextInputEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaugaPeste extends AppCompatActivity {

    public static final String Peste_key = "peste_key";
    private static final int PICK_IMAGE = 1234;
    private TextInputEditText tietLungime;
    private TextInputEditText tietGreutate;
    private SearchableSpinner specie;
    private Button adaugarePeste;
    private ImageView poza_peste;
    private TextInputEditText dataPeste;
    private Date date2;
    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_peste);
        initcomponents();
        ArrayList<String> speciiPesti = new ArrayList<>();
        Collections.addAll(speciiPesti, getResources().getStringArray(R.array.SpeciiPesti));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speciiPesti);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specie.setAdapter(arrayAdapter);
        adaugarePeste.setOnClickListener(adaugarePeste());
        poza_peste.setOnClickListener(pozaPeste());
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        dataPeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AdaugaPeste.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @NotNull
    private View.OnClickListener pozaPeste() {
        return new View.OnClickListener() {
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
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                poza_peste.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AdaugaPeste.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(AdaugaPeste.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    @NotNull
    private View.OnClickListener adaugarePeste() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Peste peste = createPeste();
                //transfer de parametri intre activitati - VEZI Seminar 3
                //(pasul 3 din schema - intoarcerea rezultatului catre apelator)
                Intent intent=new Intent(getApplicationContext(), ListaPesti.class);
                intent.putExtra(Peste_key, peste);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }
    private Peste createPeste()  {
        //preluare informatii din componentele vizuale pentru a construi un obiect de tip BankAccount
        int lungime = Integer.parseInt(tietLungime.getText().toString());
        int greutate = Integer.parseInt(tietGreutate.getText().toString());
        String species = specie.getSelectedItem().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

        try {
            date2 = formatter.parse(dataPeste.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Peste(greutate, lungime, species, "dsa",date2);
    }

    private void initcomponents() {
        tietLungime = findViewById(R.id.tiet_lungime_peste);
        tietGreutate = findViewById(R.id.tiet_greutate_peste);
        specie = findViewById(R.id.spinner);
        specie.setTitle("Specia:");
        adaugarePeste = findViewById(R.id.button_adauga_peste);
        poza_peste = findViewById(R.id.adaugare_poza_peste);
        dataPeste=findViewById(R.id.tietDatePeste);
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        dataPeste.setText(sdf.format(myCalendar.getTime()));
    }
}