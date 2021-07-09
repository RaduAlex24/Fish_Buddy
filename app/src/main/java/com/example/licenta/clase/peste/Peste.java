package com.example.licenta.clase.peste;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.Date;

public class Peste implements Serializable {
    private int id;
    private int greutate;
    private int lungime;
    private String specie;
    private String locatie;
    private Date dataPrindere;
    private byte[] imagine;

    public Peste(int greutate, int lungime, String specie, String locatie, Date dataPrindere, byte[] imagine) {
        this.greutate = greutate;
        this.lungime = lungime;
        this.specie = specie;
        this.locatie = locatie;
        this.dataPrindere = dataPrindere;
        this.imagine = imagine;
    }

    public Peste(int greutate, int lungime, String specie, String locatie, Date dataPrindere) {
        this.greutate = greutate;
        this.lungime = lungime;
        this.specie = specie;
        this.locatie = locatie;
        this.dataPrindere = dataPrindere;
    }

    public Peste(int id, int greutate, int lungime, String specie, String locatie, Date dataPrindere, byte[] imagine) {
        this.id = id;
        this.greutate = greutate;
        this.lungime = lungime;
        this.specie = specie;
        this.locatie = locatie;
        this.dataPrindere = dataPrindere;
        this.imagine = imagine;
    }

    public Peste(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGreutate() {
        return greutate;
    }

    public void setGreutate(int greutate) {
        this.greutate = greutate;
    }

    public int getLungime() {
        return lungime;
    }

    public void setLungime(int lungime) {
        this.lungime = lungime;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getLocatie() {
        return locatie;
    }

    public void setLocatie(String locatie) {
        this.locatie = locatie;
    }

    public Date getDataPrindere() {
        return dataPrindere;
    }

    public void setDataPrindere(Date dataPrindere) {
        this.dataPrindere = dataPrindere;
    }

    public byte[] getImagine() {
        return imagine;
    }

    public void setImagine(byte[] imagine) {
        this.imagine = imagine;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Peste{");
        sb.append("id=").append(id);
        sb.append(", greutate=").append(greutate);
        sb.append(", lungime=").append(lungime);
        sb.append(", specie='").append(specie).append('\'');
        sb.append(", locatie='").append(locatie).append('\'');
        sb.append(", dataPrindere=").append(dataPrindere);
      //  sb.append(", imagine=").append(imagine);
        sb.append('}');
        return sb.toString();
    }
}
