package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.licenta.clase.peste.Peste;
import com.example.licenta.clase.peste.PestiAdaptor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListaPesti extends AppCompatActivity {

    private ListView lvPesti;
    private List<Peste> pesteList = new ArrayList<>();
    private Button adaugaPeste;
    private int ReqPeste = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pesti);
        initcomponents();
        addAdapter();
        adaugaPeste.setOnClickListener(goToAdaugarePeste());
    }

    @NotNull
    private View.OnClickListener goToAdaugarePeste() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdaugaPeste.class);
                startActivityForResult(intent, ReqPeste);
            }
        };
    }

    private void initcomponents() {
        lvPesti = findViewById(R.id.lv_peste);
        adaugaPeste = findViewById(R.id.button_adaugare_peste);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqPeste && resultCode == RESULT_OK && data != null) {
            Peste peste = (Peste) data.getSerializableExtra(AdaugaPeste.Peste_key);
            if (peste != null) {
                pesteList.add(peste);
                notifyAdapter();
            }
        }
    }

    private void addAdapter() {
        PestiAdaptor adaptor = new PestiAdaptor(getApplicationContext(), R.layout.listview_pesti,
                pesteList, getLayoutInflater());
        lvPesti.setAdapter(adaptor);
    }

    private void notifyAdapter() {
        PestiAdaptor adapter = (PestiAdaptor) lvPesti.getAdapter();
        adapter.notifyDataSetChanged();
    }

}