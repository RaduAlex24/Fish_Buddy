package com.example.licenta.clase.user;

import android.util.Log;

public class CurrentUser {
    private int id;
    private String username;
    private String password;
    private String email;
    private String surname;
    private String name;
    private int points;

    // Log unic pentru debug
    private static final String tagLog = "ClasaCurrentUser";

    // Instanta unica
    private static CurrentUser currentUser = null;

    // Constructor privat
    private CurrentUser() {
    }

    // Metode pentru acces
    // Pentru initializare dupa log in
    public synchronized static void initInstance(User user){
        if(currentUser == null){
            currentUser = new CurrentUser();
            currentUser.id = user.getId();
            currentUser.username = user.getUsername();
            currentUser.password = user.getPassword();
            currentUser.email = user.getEmail();
            currentUser.surname = user.getSurname();
            currentUser.name = user.getName();
            currentUser.points = user.getPoints();
        }
    }

    // Pentru accesare in restul activitatiilor
    public static CurrentUser getInstance(){
        if(currentUser == null){
            //currentUser = new CurrentUser();
            Log.e(tagLog,"Current user nu este initializat");
        }

        return currentUser;
    }

    // Pentru a sterge instanta curent
    public static void delelteInstance(){
        if(currentUser != null){
            currentUser = null;
        }
    }



    // Setteri si getteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    // Metode

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CurrentUser{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", surname='").append(surname).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", points=").append(points);
        sb.append('}');
        return sb.toString();
    }
}
