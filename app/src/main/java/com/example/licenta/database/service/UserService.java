package com.example.licenta.database.service;

import android.content.Intent;
import android.util.Log;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.clase.user.CurrentUser;
import com.example.licenta.clase.user.FishingTitleEnum;
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

                String sql = "SELECT * FROM " + numeBDuser + " WHERE username IN (?, ?, ?, ?, ?) AND password LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);

                int i = 1;
                for (FishingTitleEnum fishingTitleEnum : FishingTitleEnum.values()) {
                    String nume = username + ":" + fishingTitleEnum.toString();
                    statement.setString(i, nume);
                    i++;
                }
                statement.setString(6, password);
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
                statement.setString(1, username + ":UNU");
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


                String sql = "SELECT COUNT(*) FROM " + numeBDuser + " WHERE username IN (?, ?, ?, ?, ?)";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);

                int i = 1;
                for (FishingTitleEnum fishingTitleEnum : FishingTitleEnum.values()) {
                    String nume = username + ":" + fishingTitleEnum.toString();
                    statement.setString(i, nume);
                    i++;
                }

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


    // Update points by user id and points
    public void updatePointsByUserIdAndPoints(int id, int points, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDuser + " SET points = points + ? " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, points);
                statement.setInt(2, id);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update user by currentuser
    public void updateUserByCurrentuser(CurrentUser currentUser, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDuser + " SET username = ? , password = ? , email = ? " +
                        ", surname = ? , name = ? WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, currentUser.getUsername());
                statement.setString(2, currentUser.getPassword());
                statement.setString(3, currentUser.getEmail());
                statement.setString(4, currentUser.getSurname());
                statement.setString(5, currentUser.getName());
                statement.setInt(6, currentUser.getId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete user by userId
    public void deleteUserByUserId(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDuser + "  WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Preluare numar puncte user curent
    public void getPointsForCurrentUser(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrPuncte;

                String sql = "SELECT points FROM " + numeBDuser + " WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    nrPuncte = resultSet.getInt(1);
                } else {
                    statement.close();
                    resultSet.close();
                    return null;
                }

                statement.close();
                resultSet.close();
                return nrPuncte;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update nume utiliator dupa schimbare titlu
    public void updateFishingTitleByIdAndNewTitle(int id, String updatedUsername, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDuser + " SET username = ? " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, updatedUsername);
                statement.setInt(2, id);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update poza utilizator dupa id
    public void updateUSerPhotoById(int id, byte[] bytePhoto, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDuser + " SET user_photo = ? " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setBytes(1, bytePhoto);
                statement.setInt(2, id);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // byte[] bytePhoto
    // Preluare poza utiliator din baza de date dupa id
    public void getUserPhotoById(int id, Callback<byte[]> callback) {
        Callable<byte[]> callable = new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                byte[] photoByte;

                String sql = "SELECT user_photo FROM " + numeBDuser + " WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    photoByte = resultSet.getBytes(1);
                } else {
                    statement.close();
                    resultSet.close();
                    return null;
                }

                statement.close();
                resultSet.close();
                return photoByte;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

}
