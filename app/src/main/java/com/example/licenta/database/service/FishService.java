package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.peste.Peste;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.util.dateUtils.DateConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class FishService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDfish = "FISH";

    // Constructor
    public FishService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get all fish by user id
    public void getAllFishById(int id, Callback<List<Peste>> callback) {
        Callable<List<Peste>> callable = new Callable<List<Peste>>() {
            @Override
            public List<Peste> call() throws Exception {
                List<Peste> pesteListDinBd = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDfish + " where USERID = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String specii = resultSet.getString(3);
                    int lungime = resultSet.getInt(4);
                    int greutate = resultSet.getInt(5);
                    String dataS = resultSet.getString(6);
                    String locatie = resultSet.getString(7);
                    byte[] imgByte = resultSet.getBytes(8);

                    Date date = DateConverter.toDate(dataS);
                    Peste peste = new Peste(id, greutate, lungime, specii, locatie, date, imgByte);
                    pesteListDinBd.add(peste);
                }

                statement.close();
                resultSet.close();
                return pesteListDinBd;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get favourite fish by id and userId
    public void getFavouriteFishByIdAndUserId(int fishId, int userId, Callback<Peste> callback) {
        Callable<Peste> callable = new Callable<Peste>() {
            @Override
            public Peste call() throws Exception {
                Peste pesteFavorit = new Peste();

                String sql = "SELECT * FROM " + numeBDfish + " where id = ? AND userId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, fishId);
                statement.setInt(2, userId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String specii = resultSet.getString(3);
                    int lungime = resultSet.getInt(4);
                    int greutate = resultSet.getInt(5);
                    String dataS = resultSet.getString(6);
                    String locatie = resultSet.getString(7);
                    byte[] imgByte = resultSet.getBytes(8);
                    Date date = DateConverter.toDate(dataS);

                    pesteFavorit.setId(id);
                    pesteFavorit.setSpecie(specii);
                    pesteFavorit.setLungime(lungime);
                    pesteFavorit.setGreutate(greutate);
                    pesteFavorit.setDataPrindere(date);
                    pesteFavorit.setLocatie(locatie);
                    pesteFavorit.setImagine(imgByte);

                } else {
                    statement.close();
                    resultSet.close();
                    return null;
                }

                statement.close();
                resultSet.close();
                return pesteFavorit;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete fish by fishId
    public void deleteFishByFishId(int fishId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDfish + "  WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, fishId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

    // Delete fish by userId
    public void deleteFishByUserId(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDfish + "  WHERE userId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

}
