package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.android.material.textfield.TextInputEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Collections;

public class AdaugaPeste extends AppCompatActivity {
    private TextInputEditText tietLungime;
    private TextInputEditText tietGreutate;
    private SearchableSpinner specie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_peste);
        initcomponents();
        ArrayList<String> speciiPesti = new ArrayList<>();
        Collections.addAll(speciiPesti, getResources().getStringArray(R.array.SpeciiPesti));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, speciiPesti);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specie.setAdapter(arrayAdapter);
    }

    private void initcomponents() {
        tietLungime = findViewById(R.id.tiet_lungime_peste);
        tietGreutate = findViewById(R.id.tiet_greutate_peste);
        specie =findViewById(R.id.spinner);
        specie.setTitle("Specia:");
    }
}