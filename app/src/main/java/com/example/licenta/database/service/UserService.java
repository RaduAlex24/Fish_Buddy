package com.example.licenta.database.service;

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

                String sql = "SELECT * FROM USERS WHERE username LIKE ? AND password LIKE ?";
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
}
