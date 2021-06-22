package com.example.licenta.clase.vreme;

import java.io.Serializable;
import java.util.Date;

public class Vreme implements Serializable {
    private String data;
    private double tempZi;
    private double tempNoapte;
    private int humidity;
    private float probPrecipit;
    private String imagine;

    public Vreme(String data, double tempZi, double tempNoapte, int humidity, float probPrecipit, String imagine) {
        this.data = data;
        this.tempZi = tempZi;
        this.tempNoapte = tempNoapte;
        this.humidity = humidity;
        this.probPrecipit = probPrecipit;
        this.imagine=imagine;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getTempZi() {
        return tempZi;
    }

    public void setTempZi(double tempZi) {
        this.tempZi = tempZi;
    }

    public double getTempNoapte() {
        return tempNoapte;
    }

    public void setTempNoapte(double tempNoapte) {
        this.tempNoapte = tempNoapte;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getProbPrecipit() {
        return probPrecipit;
    }

    public void setProbPrecipit(float probPrecipit) {
        this.probPrecipit = probPrecipit;
    }

    public String getImagine() {
        return imagine;
    }

    public void setImagine(String imagine) {
        this.imagine = imagine;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Vreme{");
        sb.append("data=").append(data);
        sb.append(", tempZi=").append(tempZi);
        sb.append(", tempNoapte=").append(tempNoapte);
        sb.append(", humidity=").append(humidity);
        sb.append(", probPrecipit=").append(probPrecipit);
        sb.append(", imagine=").append(imagine);
        sb.append('}');
        return sb.toString();
    }
}
