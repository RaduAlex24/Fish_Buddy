package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.CommentForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.util.dateUtils.DateConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class CommentForumService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDcommentsForum = "FORUM_ANSWERS";


    // Constructor
    public CommentForumService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get all comments by post id
    public void getCommentsByForumPostId(int forumPostId, Callback<List<CommentForum>> callback,
                                         String commentsOrder) {
        Callable<List<CommentForum>> callable = new Callable<List<CommentForum>>() {
            @Override
            public List<CommentForum> call() throws Exception {
                List<CommentForum> commentForumList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDcommentsForum + " WHERE forumPostId LIKE ? "
                        + commentsOrder;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, forumPostId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    int forumPostId = resultSet.getInt(3);
                    String creatorUsername = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    boolean isEdited = Boolean.parseBoolean(resultSet.getString(6));
                    int nrLikes = resultSet.getInt(7);
                    int nrDislikes = resultSet.getInt(8);

                    String postDate = resultSet.getString(9);
                    Date date = DateConverter.toDate(postDate);

                    CommentForum commentForum = new CommentForum(id, userId, forumPostId,
                            creatorUsername, content, isEdited, nrLikes, nrDislikes, date);
                    commentForumList.add(commentForum);
                }


                statement.close();
                resultSet.close();
                return commentForumList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get next comment id
    public void getNextCommentId(Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int id = -1;

                String sql = "SELECT forum_answer_id.nextval FROM DUAL";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }

                statement.close();
                resultSet.close();
                return id;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Insert new forum comment
    public void insertNewCommentForum(CommentForum commentForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                String sql = "INSERT INTO " + numeBDcommentsForum + "(id, userId, forumPostId, " +
                        "creatorUsername, content, isEdited, nrLikes, nrDislikes, postDate)" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, commentForum.getId());
                statement.setInt(2, commentForum.getUserId());
                statement.setInt(3, commentForum.getForumPostId());
                statement.setString(4, commentForum.getCreatorUsername());
                statement.setString(5, commentForum.getContent());
                statement.setString(6, String.valueOf(commentForum.isEdited()));
                statement.setInt(7, commentForum.getNrLikes());
                statement.setInt(8, commentForum.getNrDislikes());
                statement.setString(9, DateConverter.toString(commentForum.getPostDate()));
                int nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update nrLikes nrDislikes by comment forum
    public void updatenrLikesnrDislikesByCommentForum(CommentForum commentForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDcommentsForum + " SET nrLikes = ? , nrDislikes= ? " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, String.valueOf(commentForum.getNrLikes()));
                statement.setString(2, String.valueOf(commentForum.getNrDislikes()));
                statement.setInt(3, commentForum.getId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update content by comment
    public void updateContentByCommentForum(CommentForum commentForum, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDcommentsForum + " SET content = ? , isEdited= ? " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, commentForum.getContent());
                statement.setString(2, String.valueOf(commentForum.isEdited()));
                statement.setInt(3, commentForum.getId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete comment by commentId
    public void deleteCommentByCommentId(int commentId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDcommentsForum + "  WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, commentId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Statistici
    // Preluare raspunsuri acordate
    public void getAnswersCountByUserId(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrAparitii = -1;

                String sql = "SELECT COUNT(*) FROM " + numeBDcommentsForum + " WHERE userId LIKE ?";
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
