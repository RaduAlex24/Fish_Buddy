package com.example.licenta.clase.user;

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
    public static void actualiareFishingTitle(CurrentUser currentUser){
        for(FishingTitleEnum fishingTitleEnum: FishingTitleEnum.values()){
            if(currentUser.getPoints() < fishingTitleEnum.getLimitaSuperioaraPuncte()){
                currentUser.setFishingTitle(fishingTitleEnum);
                break;
            }
        }

        if(currentUser.getFishingTitle() == null){
            currentUser.setFishingTitle(FishingTitleEnum.CINCI);
        }
    }

    public static FishingTitleEnum preluareTitluInFunctieDePuncte(int nrPuncte){
        for(FishingTitleEnum fishingTitleEnum: FishingTitleEnum.values()){
            if(nrPuncte < fishingTitleEnum.getLimitaSuperioaraPuncte()){
                return fishingTitleEnum;
            }
        }

        return FishingTitleEnum.CINCI;
    }

    public static FishingTitleEnum preluareTitluInFunctieDeUsername(String username){
        String vect[] = username.split(":");
        return FishingTitleEnum.valueOf(vect[1]);
    }

}
