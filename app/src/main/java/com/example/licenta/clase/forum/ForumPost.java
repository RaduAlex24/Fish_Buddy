package com.example.licenta.clase.forum;

import com.example.licenta.util.dateUtils.DateConverter;

import java.io.Serializable;
import java.util.Date;

public class ForumPost implements Serializable {
    private int id;
    private int userId;
    private String creatorUsername;

    private String title;
    private String content;
    private boolean isEdited;
    private int nrLikes;
    private int nrDislikes;
    private int nrComments;
    private CategoryForum category;
    private Date postDate;


    // Constructori
    public ForumPost(int id, int userId, String creatorUsername, String title, String content, boolean isEdited,
                     int nrLikes, int nrDislikes, int nrComments, CategoryForum category, Date postDate) {
        this.id = id;
        this.userId = userId;
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.isEdited = isEdited;
        this.nrLikes = nrLikes;
        this.nrDislikes = nrDislikes;
        this.nrComments = nrComments;
        this.category = category;
        this.postDate = postDate;
    }

    public ForumPost(int id, int userId, String creatorUsername, String title,
                     String content, CategoryForum category) {
        this.id = id;
        this.userId = userId;
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.category = category;

        this.nrComments = 0;
        this.nrLikes = 0;
        this.nrDislikes = 0;
        this.postDate = DateConverter.toDateFromLong(System.currentTimeMillis());
    }

    public ForumPost(int userId, String creatorUsername, String title, String content, CategoryForum category) {
        this.userId = userId;
        this.creatorUsername = creatorUsername;
        this.title = title;
        this.content = content;
        this.category = category;

        this.isEdited = false;
        this.nrLikes = 0;
        this.nrDislikes = 0;
        this.nrComments = 0;
        this.postDate = DateConverter.toDateFromLong(System.currentTimeMillis());
    }

    // Getteri si setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public int getNrLikes() {
        return nrLikes;
    }

    public void setNrLikes(int nrLikes) {
        this.nrLikes = nrLikes;
    }

    public int getNrDislikes() {
        return nrDislikes;
    }

    public void setNrDislikes(int nrDislikes) {
        this.nrDislikes = nrDislikes;
    }

    public int getNrComments() {
        return nrComments;
    }

    public void setNrComments(int nrComments) {
        this.nrComments = nrComments;
    }

    public CategoryForum getCategory() {
        return category;
    }

    public void setCategory(CategoryForum category) {
        this.category = category;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }


    // Metode

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ForumPost{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", creatorUsername='").append(creatorUsername).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", nrLikes=").append(nrLikes);
        sb.append(", nrDislikes=").append(nrDislikes);
        sb.append(", nrComments=").append(nrComments);
        sb.append(", category=").append(category);
        sb.append(", postDate=").append(postDate);
        sb.append('}');
        return sb.toString();
    }
}
