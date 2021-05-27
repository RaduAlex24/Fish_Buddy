package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.FavouriteForum;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.database.ConexiuneBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FavouriteForumPostService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDfavouriteForum = "FAVOURITE_POSTS";


    // Constructor
    public FavouriteForumPostService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get favourite posts id list by userId
    public void getFavouritePostsIdListByUserId(int userId, Callback<List<Integer>> callback) {
        Callable<List<Integer>> callable = new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() throws Exception {
                List<Integer> favouritePostIdList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDfavouriteForum + " WHERE userID LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int favouritePostId = resultSet.getInt(2);
                    favouritePostIdList.add(favouritePostId);
                }

                statement.close();
                resultSet.close();
                return favouritePostIdList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Inserare in bd a unui nou post favorit
    public void insertNewFavouriteForum(FavouriteForum favouriteForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "INSERT INTO " + numeBDfavouriteForum + " (userId, postId) "
                        + "VALUES(?, ?)";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, favouriteForum.getUserId());
                statement.setInt(2, favouriteForum.getForumPostId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Stergerea forum favorit din bd
    public void deleteFavouriteForumByFavouriteForum(FavouriteForum favouriteForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDfavouriteForum + "  WHERE userId = ? AND postId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, favouriteForum.getUserId());
                statement.setInt(2, favouriteForum.getForumPostId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete favourite forums by forum post id
    public void deleteFavouriteForumsByForumPostId(int forumPostId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDfavouriteForum + "  WHERE postId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, forumPostId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

}
