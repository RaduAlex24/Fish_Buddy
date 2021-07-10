package com.example.licenta.clase.user;

import android.graphics.Color;

public enum FishingTitleEnum {
    // Elemente
    UNU("Pescar invatacel", 100),
    DOI("Pescar incepator", 200),
    TREI("Pescar intermediar", 300),
    PATRU("Pescar avansat", 400),
    CINCI("Pescar profesionist", 500);


    // Atribute
    private String label;
    private int limitaSuperioaraPuncte;


    // Constructori
    FishingTitleEnum(String label, int limitaSuperioaraPuncte) {
        this.label = label;
        this.limitaSuperioaraPuncte = limitaSuperioaraPuncte;
    }


    // Getteri si setteri
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLimitaSuperioaraPuncte() {
        return limitaSuperioaraPuncte;
    }

    public void setLimitaSuperioaraPuncte(int limitaSuperioaraPuncte) {
        this.limitaSuperioaraPuncte = limitaSuperioaraPuncte;
    }


    // Metode
    // Preluare titlu in functie de username
    public static FishingTitleEnum preluareTitluInFunctieDeUsername(String username) {
        String vect[] = username.split(":");
        return FishingTitleEnum.valueOf(vect[1]);
    }

    // Preluare titlu culoare in functie de titlu
    public static int preluareCuloareInFunctieDeTitlu(FishingTitleEnum fishingTitleEnum) {
        int culoare = Color.BLACK;

        switch (fishingTitleEnum) {
            case UNU:
                culoare = Color.GRAY;
                break;
            case DOI:
                culoare = Color.BLUE;
                break;
            case TREI:
                culoare = Color.CYAN;
                break;
            case PATRU:
                culoare = Color.YELLOW;
                break;
            case CINCI:
                culoare = Color.RED;
                break;
        }

        return culoare;
    }

}
