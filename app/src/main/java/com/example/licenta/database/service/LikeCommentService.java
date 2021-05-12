package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.LikeComment;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.database.ConexiuneBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class LikeCommentService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDlikeComment = "ANSWER_LIKES";

    // Constructor
    public LikeCommentService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get like comment map by userId and forumPostId
    public void getLikeCommentMapByUserIdAndForumPostId(int userId, int forumPostId,
                                                        Callback<Map<Integer, LikeComment>> callback) {
        Callable<Map<Integer, LikeComment>> callable = new Callable<Map<Integer, LikeComment>>() {
            @Override
            public Map<Integer, LikeComment> call() throws Exception {
                Map<Integer, LikeComment> likeCommentMap = new HashMap<>();

                String sql = "SELECT * FROM " + numeBDlikeComment + " WHERE userID LIKE ? AND forumPostId LIKE ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                statement.setInt(2, forumPostId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    LikeComment likeComment = new LikeComment();

                    likeComment.setUserId(resultSet.getInt(1));
                    likeComment.setCommentId(resultSet.getInt(2));
                    likeComment.setForumPostId(resultSet.getInt(3));
                    likeComment.setLiked(Boolean.parseBoolean(resultSet.getString(4)));
                    likeComment.setDisliked(Boolean.parseBoolean(resultSet.getString(5)));

                    likeCommentMap.put(likeComment.getCommentId(), likeComment);
                }

                statement.close();
                resultSet.close();
                return likeCommentMap;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Inserare in bd a unui nou like comment
    public void insertNewLikeComment(LikeComment likeComment, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "INSERT INTO " + numeBDlikeComment + " (userId, answerId, forumPostId, isLiked, isDisliked)"
                        + "VALUES(?, ?, ?, ?, ?)";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, likeComment.getUserId());
                statement.setInt(2, likeComment.getCommentId());
                statement.setInt(3, likeComment.getForumPostId());
                statement.setString(4, String.valueOf(likeComment.isLiked()));
                statement.setString(5, String.valueOf(likeComment.isDisliked()));
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete like comment by userId and commentId
    public void deleteLikeCommentByUserIdAndCommentId(int userId, int commentId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDlikeComment + "  WHERE userId = ? AND answerId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, userId);
                statement.setInt(2, commentId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update like comment by userId and commentId
    public void updateLikeCommentByLikeComment(LikeComment likeComment, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDlikeComment + " SET isLiked = ? , isDisliked= ? " +
                        "WHERE userId = ? AND answerId = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, String.valueOf(likeComment.isLiked()));
                statement.setString(2, String.valueOf(likeComment.isDisliked()));
                statement.setInt(3, likeComment.getUserId());
                statement.setInt(4, likeComment.getCommentId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

}
