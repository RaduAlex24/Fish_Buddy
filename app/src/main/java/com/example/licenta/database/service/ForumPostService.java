package com.example.licenta.database.service;

import com.example.licenta.asyncTask.AsyncTaskRunner;
import com.example.licenta.asyncTask.Callback;
import com.example.licenta.clase.forum.CategoryForum;
import com.example.licenta.clase.forum.ForumPost;
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
    public static final String numeBDforum = "FORUM_POST";

    // Constructor
    public ForumPostService() {
        conexiuneBD = ConexiuneBD.getInstance();
        asyncTaskRunner = new AsyncTaskRunner();
    }


    // Metode
    // Get all
    public void getAllForumPosts(Callback<List<ForumPost>> callback) {
        Callable<List<ForumPost>> callable = new Callable<List<ForumPost>>() {
            @Override
            public List<ForumPost> call() throws Exception {
                List<ForumPost> forumPostList = new ArrayList<>();

                String sql = "SELECT * FROM " + numeBDforum;
                PreparedStatement statement = conexiuneBD.getConexiune().prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    int userId = resultSet.getInt(2);
                    String creatorUsername = resultSet.getString(3);
                    String title = resultSet.getString(4);
                    String content = resultSet.getString(5);
                    int nrLikes = resultSet.getInt(6);
                    int nrDislikes = resultSet.getInt(7);
                    int nrComments = resultSet.getInt(8);

                    String category = resultSet.getString(9);
                    CategoryForum categoryForum = CategoryForum.valueOf(category);

                    String postDate = resultSet.getString(10);
                    Date date = DateConverter.toDate(postDate);

                    ForumPost forumPost = new ForumPost(id, userId, creatorUsername, title, content,
                            nrLikes, nrDislikes, nrComments, categoryForum, date);
                    forumPostList.add(forumPost);
                }

                return forumPostList;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

}
