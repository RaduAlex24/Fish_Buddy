package com.example.licenta.util.MapsUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.licenta.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View view;
    private Context context;
    private TextView distanta;
    private TextView titlu;
    public InfoWindowAdapter(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.info_window,null);
    }

    private void CustomWindow(Marker marker,View view){
        titlu=view.findViewById(R.id.tv_info_window_title);
        distanta =view.findViewById(R.id.tv_infoWindow_distanta);

        String title = marker.getTitle();

        if(!title.equals("")){
            String[] cutText = title.split(":");
            titlu.setText(cutText[0]);
        }
        String snippet = marker.getSnippet();
        if(!snippet.equals("")){
            distanta.setText(snippet);
        }
    }
    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        CustomWindow(marker, view);
        return view;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        CustomWindow(marker,view);
        return view;
    }
}
