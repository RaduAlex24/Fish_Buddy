package com.example.licenta.database.service;

import android.content.Intent;
import android.util.Log;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.user.User;
import com.example.licenta.database.ConexiuneBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class UserService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDuser = "USERS";


    // Constructor
    public UserService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get user by username and password
    public void getUserByUsernameAndPassword(String username, String password, Callback<User> callback) {
        Callable<User> callable = new Callable<User>() {
            @Override
            public User call() throws Exception {
                User user = new User();

                String sql = "SELECT * FROM " + numeBDuser + " WHERE username LIKE ? AND password LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    user.setId(resultSet.getInt(1));
                    user.setUsername(resultSet.getString(2));
                    user.setPassword(resultSet.getString(3));
                    user.setEmail(resultSet.getString(4));
                    user.setSurname(resultSet.getString(5));
                    user.setName(resultSet.getString(6));
                    user.setPoints(resultSet.getInt(7));
                } else {
                    statement.close();
                    resultSet.close();
                    return null;
                }

                statement.close();
                resultSet.close();
                return user;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Inserare in bd a unui nou utilizator
    public void insertNewUser(String username, String password, String email, String surname,
                                 String name, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "INSERT INTO " + numeBDuser + " (id, username, password, email, surname, name)"
                        + "VALUES(user_id.nextval, ?, ?, ?, ?, ?)";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, email);
                statement.setString(4, surname);
                statement.setString(5, name);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Verificare existenta username
    public void getCountByUsername(String username, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrAparitii = -1;

                String sql = "SELECT COUNT(*) FROM " + numeBDuser + " WHERE username LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    nrAparitii = resultSet.getInt(1);
                } else {
                    statement.close();
                    resultSet.close();
                    return null;
                }

                statement.close();
                resultSet.close();
                return nrAparitii;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Verificare existenta email
    public void getCountByEmail(String email, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrAparitii = -1;

                String sql = "SELECT COUNT(*) FROM " + numeBDuser + " WHERE email LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    nrAparitii = resultSet.getInt(1);
                } else {
                    statement.close();
                    resultSet.close();
                    return null;
                }

                statement.close();
                resultSet.close();
                return nrAparitii;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }
}
