package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
import com.example.licenta.clase.forum.LikeForum;
import com.example.licenta.database.ConexiuneBD;
import com.example.licenta.util.dateUtils.DateConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class ForumPostService {
    private ConexiuneBD conexiuneBD;
    private AsyncTaskRunner asyncTaskRunner;
    public static final String numeBDforum = "FORUM_POSTS";

    // Constructor
    public ForumPostService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get all
    public void getAllForumPosts(String postsOrder, Callback<List<ForumPost>> callback) {
        Callable<List<ForumPost>> callable = new Callable<List<ForumPost>>() {
            @Override
            public List<ForumPost> call() throws Exception {
                List<ForumPost> forumPostList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDforum + " " + postsOrder;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    String creatorUsername = resultSet.getString(3);
                    String title = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    boolean isEdited = Boolean.parseBoolean(resultSet.getString(6));
                    int nrLikes = resultSet.getInt(7);
                    int nrDislikes = resultSet.getInt(8);
                    int nrComments = resultSet.getInt(9);

                    String category = resultSet.getString(10);
                    CategoryForum categoryForum = CategoryForum.valueOf(category);

                    String postDate = resultSet.getString(11);
                    Date date = DateConverter.toDate(postDate);

                    ForumPost forumPost = new ForumPost(id, userId, creatorUsername, title, content,
                            isEdited, nrLikes, nrDislikes, nrComments, categoryForum, date);
                    forumPostList.add(forumPost);
                }


                statement.close();
                resultSet.close();
                return forumPostList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get all by search words
    public void getAllForumPostsBySearchWords(String postsOrder, String searchWords,
                                              Callback<List<ForumPost>> callback) {
        Callable<List<ForumPost>> callable = new Callable<List<ForumPost>>() {
            @Override
            public List<ForumPost> call() throws Exception {
                List<ForumPost> forumPostList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDforum + " WHERE LOWER(title) LIKE LOWER(?)" +
                        " OR LOWER(content) LIKE LOWER(?)" + " " + postsOrder;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, searchWords);
                statement.setString(2, searchWords);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    String creatorUsername = resultSet.getString(3);
                    String title = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    boolean isEdited = Boolean.parseBoolean(resultSet.getString(6));
                    int nrLikes = resultSet.getInt(7);
                    int nrDislikes = resultSet.getInt(8);
                    int nrComments = resultSet.getInt(9);

                    String category = resultSet.getString(10);
                    CategoryForum categoryForum = CategoryForum.valueOf(category);

                    String postDate = resultSet.getString(11);
                    Date date = DateConverter.toDate(postDate);

                    ForumPost forumPost = new ForumPost(id, userId, creatorUsername, title, content,
                            isEdited, nrLikes, nrDislikes, nrComments, categoryForum, date);
                    forumPostList.add(forumPost);
                }


                statement.close();
                resultSet.close();
                return forumPostList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get all by category
    public void getAllForumPostsByCategory(String category, String postsOrder, Callback<List<ForumPost>> callback) {
        Callable<List<ForumPost>> callable = new Callable<List<ForumPost>>() {
            @Override
            public List<ForumPost> call() throws Exception {
                List<ForumPost> forumPostList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDforum + " WHERE category LIKE ? " + postsOrder;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, category);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    String creatorUsername = resultSet.getString(3);
                    String title = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    boolean isEdited = Boolean.parseBoolean(resultSet.getString(6));
                    int nrLikes = resultSet.getInt(7);
                    int nrDislikes = resultSet.getInt(8);
                    int nrComments = resultSet.getInt(9);

                    String category = resultSet.getString(10);
                    CategoryForum categoryForum = CategoryForum.valueOf(category);

                    String postDate = resultSet.getString(11);
                    Date date = DateConverter.toDate(postDate);

                    ForumPost forumPost = new ForumPost(id, userId, creatorUsername, title, content,
                            isEdited, nrLikes, nrDislikes, nrComments, categoryForum, date);
                    forumPostList.add(forumPost);
                }


                statement.close();
                resultSet.close();
                return forumPostList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get all forum post by user id
    public void getAllForumPostsByUserId(int id, String postsOrder, Callback<List<ForumPost>> callback) {
        Callable<List<ForumPost>> callable = new Callable<List<ForumPost>>() {
            @Override
            public List<ForumPost> call() throws Exception {
                List<ForumPost> forumPostList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDforum + " WHERE userId LIKE ? " + postsOrder;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    String creatorUsername = resultSet.getString(3);
                    String title = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    boolean isEdited = Boolean.parseBoolean(resultSet.getString(6));
                    int nrLikes = resultSet.getInt(7);
                    int nrDislikes = resultSet.getInt(8);
                    int nrComments = resultSet.getInt(9);

                    String category = resultSet.getString(10);
                    CategoryForum categoryForum = CategoryForum.valueOf(category);

                    String postDate = resultSet.getString(11);
                    Date date = DateConverter.toDate(postDate);

                    ForumPost forumPost = new ForumPost(id, userId, creatorUsername, title, content,
                            isEdited, nrLikes, nrDislikes, nrComments, categoryForum, date);
                    forumPostList.add(forumPost);
                }


                statement.close();
                resultSet.close();
                return forumPostList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get all forum post by favourite list
    public void getFavouriteForumPostsByIdList(List<Integer> idList, String postsOrder, Callback<List<ForumPost>> callback) {
        Callable<List<ForumPost>> callable = new Callable<List<ForumPost>>() {
            @Override
            public List<ForumPost> call() throws Exception {
                List<ForumPost> forumPostList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDforum + " WHERE id IN (?";
                for (int i = 1; i < idList.size(); i++) {
                    sql += ", ?";
                }
                sql += ") " + postsOrder;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                for (int i = 0; i < idList.size(); i++) {
                    statement.setInt(i + 1, idList.get(i));
                }
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    String creatorUsername = resultSet.getString(3);
                    String title = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    boolean isEdited = Boolean.parseBoolean(resultSet.getString(6));
                    int nrLikes = resultSet.getInt(7);
                    int nrDislikes = resultSet.getInt(8);
                    int nrComments = resultSet.getInt(9);

                    String category = resultSet.getString(10);
                    CategoryForum categoryForum = CategoryForum.valueOf(category);

                    String postDate = resultSet.getString(11);
                    Date date = DateConverter.toDate(postDate);

                    ForumPost forumPost = new ForumPost(id, userId, creatorUsername, title, content,
                            isEdited, nrLikes, nrDislikes, nrComments, categoryForum, date);
                    forumPostList.add(forumPost);
                }


                statement.close();
                resultSet.close();
                return forumPostList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get next id
    public void getNextForumPostId(Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int id = -1;

                String sql = "SELECT forum_post_id.nextval FROM DUAL";
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


    // Insert new forum post
    public void insertNewForumPost(ForumPost forumPost, int id, Callback<ForumPost> callback) {
        Callable<ForumPost> callable = new Callable<ForumPost>() {
            @Override
            public ForumPost call() throws Exception {
                forumPost.setId(id);

                String sql = "INSERT INTO " + numeBDforum + "(id, userId, creatorUsername, title, content, " +
                        "isEdited, nrLikes, nrDislikes, nrComments, category, postDate)" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, forumPost.getId());
                statement.setInt(2, forumPost.getUserId());
                statement.setString(3, forumPost.getCreatorUsername());
                statement.setString(4, forumPost.getTitle());
                statement.setString(5, forumPost.getContent());
                statement.setString(6, String.valueOf(forumPost.isEdited()));
                statement.setInt(7, forumPost.getNrLikes());
                statement.setInt(8, forumPost.getNrDislikes());
                statement.setInt(9, forumPost.getNrComments());
                statement.setString(10, forumPost.getCategory().toString());
                statement.setString(11, DateConverter.toString(forumPost.getPostDate()));
                statement.executeUpdate();


                statement.close();
                return forumPost;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update nrLikes nrDislikes by Forum Post
    public void updatenrLikesnrDislikesByForumPost(ForumPost forumPost, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDforum + " SET nrLikes = ? , nrDislikes= ? " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, String.valueOf(forumPost.getNrLikes()));
                statement.setString(2, String.valueOf(forumPost.getNrDislikes()));
                statement.setInt(3, forumPost.getId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update numar comments by Forum Post
    public void updateNrCommentsByForumPostAndNrComments(ForumPost forumPost, int nrComments, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDforum + " SET nrComments = ? + nrComments " +
                        "WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, nrComments);
                statement.setInt(2, forumPost.getId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Update title content si category by forumPost
    public void updateForumPostByForumPost(ForumPost forumPost, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "UPDATE " + numeBDforum + " SET title = ?, content = ?, category = ?, " +
                        "isEdited = ? WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setString(1, forumPost.getTitle());
                statement.setString(2, forumPost.getContent());
                statement.setString(3, forumPost.getCategory().toString());
                statement.setString(4, String.valueOf(forumPost.isEdited()));
                statement.setInt(5, forumPost.getId());
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Delete forum post by forum post id
    public void deleteForumPostByForumPostId(int forumPostId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrRanduriAfectate = -1;

                String sql = "DELETE " + numeBDforum + "  WHERE id = ?";
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                statement.setInt(1, forumPostId);
                nrRanduriAfectate = statement.executeUpdate();


                statement.close();
                return nrRanduriAfectate;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Statistici
    // Preluare numar postari forum create
    public void getForumPostCountByUserId(int userId, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int nrAparitii = -1;

                String sql = "SELECT COUNT(*) FROM " + numeBDforum + " WHERE userId = ?";
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
