package com.example.licenta.clase.peste;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.R;
import com.example.licenta.util.dateUtils.DateConverter;

import java.util.Date;
import java.util.List;

public class PestiAdaptor extends ArrayAdapter<Peste>  {
        private Context context;
        private List<Peste> pesteList;
        private LayoutInflater inflater;
        private int resource;

        public PestiAdaptor(@NonNull Context context, int resource,
                               @NonNull List<Peste> objects, LayoutInflater inflater) {
            super(context, resource, objects);
            this.context = context;
            this.pesteList = objects;
            this.inflater = inflater;
            this.resource = resource;
        }
    @NonNull
    @Override
    public View getView(int position, @Nullable android.view.View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Peste peste = pesteList.get(position);
        if (peste != null) {
            adaugaLungime(view, peste.getLungime());
            adaugaData(view, peste.getDataPrindere());
            adaugaGreutate(view, peste.getGreutate());
            adaugaSpecie(view, peste.getSpecie());
            adaugaLocatie(view, peste.getLocatie());
        }
        return view;
    }
    private void adaugaLungime(View view, int lungime) {
        TextView textView = view.findViewById(R.id.lvpeste_tv_lungimea_pestelui);
        populateTextViewContent(textView, String.valueOf(lungime));
    }
    private void adaugaGreutate(View view, int greutate) {
        TextView textView = view.findViewById(R.id.lvpeste_tv_greutatea_pestelui);
        populateTextViewContent(textView, String.valueOf(greutate));
    }
    private void adaugaData(View view, Date data){
        TextView textView = view.findViewById(R.id.lvpeste_tv_data_prindere);
        populateTextViewContent(textView, DateConverter.toString(data));
    }
    private void adaugaSpecie(View view, String specie) {
        TextView textView = view.findViewById(R.id.lv_tv_specia_pestelui);
        populateTextViewContent(textView, (specie));
    }
    private void adaugaLocatie(View view, String locatie) {
        TextView textView = view.findViewById(R.id.lv_tv_locatia);
        populateTextViewContent(textView, (locatie));
    }

    private void populateTextViewContent(TextView textView, String value) {
        if (value != null && !value.trim().isEmpty()) {
            textView.setText(value);
        }
    }
}
