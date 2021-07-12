package com.example.licenta.clase.user;

import android.graphics.Color;

import com.example.licenta.asyncTask.Callback;
import com.example.licenta.database.service.CommentForumService;
import com.example.licenta.database.service.ForumPostService;
import com.example.licenta.database.service.UserService;

import org.jetbrains.annotations.NotNull;

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
                culoare = Color.parseColor("#A7C957");
                break;
            case TREI:
                culoare = Color.parseColor("#0FA3B1");
                break;
            case PATRU:
                culoare = Color.parseColor("#BC4B51");
                break;
            case CINCI:
                culoare = Color.parseColor("#F4A259");
                break;
        }

        return culoare;
    }


    // Modificare titlu in functie de id si puncte
    public static void verificaSiActualieazaTitlu(int userId, String username) {
        UserService userService = new UserService();
        userService.getPointsForCurrentUser(userId, callbackPreluareNumarPuncteUtilizator(userId, username, userService));
    }


    // Callback preluare numar puncte utilizator
    @NotNull
    private static Callback<Integer> callbackPreluareNumarPuncteUtilizator(int userId, String username, UserService userService) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                FishingTitleEnum fishingTitleCurent = FishingTitleEnum.preluareTitluInFunctieDeUsername(username);

                for (FishingTitleEnum fishingTitle : FishingTitleEnum.values()) {
                    if (fishingTitle.getLimitaSuperioaraPuncte() >= result &&
                            fishingTitleCurent != fishingTitle &&
                            fishingTitle.getLimitaSuperioaraPuncte() - result < 100) {

                        String updatedUsername = username.split(":")[0] + ":" + fishingTitle.toString();

                        // Schimbare nume in baza de date
                        userService.updateFishingTitleByIdAndNewTitle(userId, updatedUsername, callbackSchimbareNume(userId, updatedUsername));

                        // Verificare current user
                        CurrentUser currentUser = CurrentUser.getInstance();
                        if (currentUser.getId() == userId) {
                            CurrentUser.changeCurrentUsername(updatedUsername);
                        }

                        break;
                    }
                }
            }
        };
    }


    // Callback schimbare nume in baza de date
    @NotNull
    private static Callback<Integer> callbackSchimbareNume(int userId, String updatedUsername) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result == 1) {
                    ForumPostService forumPostService = new ForumPostService();
                    forumPostService.updateCreatorUsernameById(userId, updatedUsername, callbackUpdateCreatorUsernameForum(userId, updatedUsername));
                }
            }
        };
    }

    // Callback pt schimbare creatorusername pentru forum
    @NotNull
    private static Callback<Integer> callbackUpdateCreatorUsernameForum(int userId, String updatedUsername) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                CommentForumService commentForumService = new CommentForumService();
                commentForumService.updateCreatorUsernameById(userId, updatedUsername, callbackUpdateCreatorUsernameComment());
            }
        };
    }


    // Callback update creatorusername penntru comment
    @NotNull
    private static Callback<Integer> callbackUpdateCreatorUsernameComment() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {

            }
        };
    }

}
