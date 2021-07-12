package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.clase.user.User;
import com.example.licenta.database.ConexiuneBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class LikeForumService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDlikeForum = "FORUM_LIKES";

    // Constructor
    public LikeForumService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get like forum by userId
    public void getLikeForumByUserId(int userId, Callback<Map<Integer, LikeForum>> callback) {
        Callable<Map<Integer, LikeForum>> callable = new Callable<Map<Integer, LikeForum>>() {
            @Override
            public Map<Integer, LikeForum> call() throws Exception {
                Map<Integer, LikeForum> likeForumMap = new HashMap<>();

                String sql = "SELECT * FROM " + numeBDlikeForum + " WHERE userID LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    LikeForum likeForum = new LikeForum();

                    likeForum.setUserId(resultSet.getInt(1));
                    likeForum.setForumPostId(resultSet.getInt(2));
                    likeForum.setLiked(Boolean.parseBoolean(resultSet.getString(3)));
                    likeForum.setDisliked(Boolean.parseBoolean(resultSet.getString(4)));

                    likeForumMap.put(likeForum.getForumPostId(), likeForum);
                }

                statement.close();
                resultSet.close();
                return likeForumMap;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Inserare in bd a unui nou like forum
    public void insertNewLikeForum(LikeForum likeForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "INSERT INTO " + numeBDlikeForum + " (userId, postId, isLiked, isDisliked)"
                        + "VALUES(?, ?, ?, ?)";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, likeForum.getUserId());
                statement.setInt(2, likeForum.getForumPostId());
                statement.setString(3, String.valueOf(likeForum.isLiked()));
                statement.setString(4, String.valueOf(likeForum.isDisliked()));
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete like forum by userId and forumPostId
    public void deleteLikeForumByUserIdAndForumPostId(int userId, int forumPostId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDlikeForum + "  WHERE userId = ? AND postId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                statement.setInt(2, forumPostId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update like forum by userId and forumPostId
    public void updateLikeForumByLikeForum(LikeForum likeForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDlikeForum + " SET isLiked = ? , isDisliked= ? " +
                        "WHERE userId = ? AND postId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, String.valueOf(likeForum.isLiked()));
                statement.setString(2, String.valueOf(likeForum.isDisliked()));
                statement.setInt(3, likeForum.getUserId());
                statement.setInt(4, likeForum.getForumPostId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete like forum by forum post id
    public void deleteLikesForumByForumPostId(int forumPostId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDlikeForum + "  WHERE postId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, forumPostId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete like forum by userId
    public void deleteLikesForumByUserId(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDlikeForum + "  WHERE userId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Statistici
    // Preluare numar likeuri forum acordate
    public void getForumLikesCountByUserId(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrAparitii = -1;

                String sql = "SELECT COUNT(*) FROM " + numeBDlikeForum + " WHERE userId = ? " +
                        "AND isliked LIKE 'true'";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
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
