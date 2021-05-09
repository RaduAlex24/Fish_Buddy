package com.example.licenta.clase.forum;

import java.io.Serializable;

public class LikeForum implements Serializable {
    private int userId;
    private int forumPostId;
    private boolean isLiked;
    private boolean isDisliked;


    // Constructori
    public LikeForum(int userId, int forumPostId, boolean isLiked, boolean isDisliked) {
        this.userId = userId;
        this.forumPostId = forumPostId;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
    }

    public LikeForum() {
    }

    // Getteri si setteri
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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }


    // Metode
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Likes{");
        sb.append("userId=").append(userId);
        sb.append(", forumPostId=").append(forumPostId);
        sb.append(", isLiked=").append(isLiked);
        sb.append(", isDisliked=").append(isDisliked);
        sb.append('}');
        return sb.toString();
    }
}
