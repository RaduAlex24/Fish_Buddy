package com.example.licenta.clase.vreme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.licenta.R;
import com.example.licenta.util.dateUtils.DateConverter;

import java.util.Date;
import java.util.List;

public class VremeAdaptor extends ArrayAdapter<Vreme> {
    private Context context;
    private List<Vreme> vremeList;
    private LayoutInflater inflater;
    private int resource;

    public VremeAdaptor(@NonNull Context context, int resource,
                        @NonNull List<Vreme> objects, LayoutInflater inflater) {
        super(context,resource,objects);
        this.context = context;
        this.vremeList = objects;
        this.inflater = inflater;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable android.view.View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Vreme vreme = vremeList.get(position);
        if (vreme != null) {
            adaugaData(view, vreme.getData());
            adaugaTempZi(view, vreme.getTempZi());
            adaugaTempNoapte(view, vreme.getTempNoapte());
            adaugaUmiditate(view, vreme.getHumidity());
            adaugaPrecipitatii(view, vreme.getProbPrecipit());
            ImageView imageView = view.findViewById(R.id.lv_vreme_imageview);
            Glide.with(context).load(vreme.getImagine()).into(imageView);
        }
        return view;
    }

    private void adaugaData(View view, String data) {
        TextView textView = view.findViewById(R.id.lv_vreme_data);
        populateTextViewContent(textView,(data));
        // DateConverter.toString
    }

    private void adaugaTempZi(View view, double tempZi) {
        TextView textView = view.findViewById(R.id.lv_vreme_tempZi);
        populateTextViewContent(textView, String.valueOf(tempZi));
    }

    private void adaugaTempNoapte(View view, double tempNoapte) {
        TextView textView = view.findViewById(R.id.lv_vreme_tempNoapte);
        populateTextViewContent(textView, String.valueOf(tempNoapte));
    }

    private void adaugaUmiditate(View view, int umiditate) {
        TextView textView = view.findViewById(R.id.lv_vreme_umiditate);
        populateTextViewContent(textView, String.valueOf(umiditate));
    }

    private void adaugaPrecipitatii(View view, float precipitatii) {
        TextView textView = view.findViewById(R.id.lv_vreme_precipitatii);
        populateTextViewContent(textView, String.valueOf(precipitatii));
    }


    private void populateTextViewContent(TextView textView, String value) {
        if (value != null && !value.trim().isEmpty()) {
            textView.setText(value);
        }
    }
}
