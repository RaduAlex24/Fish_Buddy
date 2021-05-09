package com.example.licenta.clase.forum;

import com.example.licenta.util.dateUtils.DateConverter;

import java.util.Date;

public class CommentForum {
    // Atribute
    private int id;
    private int userId;
    private int forumPostId;
    private String creatorUsername;

    private String content;
    private int nrLikes;
    private int nrDislikes;
    private Date postDate;


    // Constructori
    public CommentForum(int id, int userId, int forumPostId, String creatorUsername, String content,
                        int nrLikes, int nrDislikes, Date postDate) {
        this.id = id;
        this.userId = userId;
        this.forumPostId = forumPostId;
        this.creatorUsername = creatorUsername;
        this.content = content;
        this.nrLikes = nrLikes;
        this.nrDislikes = nrDislikes;
        this.postDate = postDate;
    }

    public CommentForum(int id, int userId, int forumPostId, String creatorUsername, String content) {
        this.id = id;
        this.userId = userId;
        this.forumPostId = forumPostId;
        this.creatorUsername = creatorUsername;
        this.content = content;

        this.nrLikes = 0;
        this.nrDislikes = 0;
        this.postDate = DateConverter.toDateFromLong(System.currentTimeMillis());
    }

    public CommentForum() {
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

    public int getForumPostId() {
        return forumPostId;
    }

    public void setForumPostId(int forumPostId) {
        this.forumPostId = forumPostId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }


    // Metode
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommentForum{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", forumPostId=").append(forumPostId);
        sb.append(", creatorUsername='").append(creatorUsername).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", nrLikes=").append(nrLikes);
        sb.append(", nrDislikes=").append(nrDislikes);
        sb.append(", postDate=").append(postDate);
        sb.append('}');
        return sb.toString();
    }

}
