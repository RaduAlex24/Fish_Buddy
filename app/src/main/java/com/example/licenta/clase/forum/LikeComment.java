package com.example.licenta.clase.forum;

import java.io.Serializable;

public class LikeComment implements Serializable {
    private int userId;
    private int commentId;
    private int forumPostId;
    private boolean isLiked;
    private boolean isDisliked;


    // Constructori
    public LikeComment(int userId, int commentId, int forumPostId, boolean isLiked, boolean isDisliked) {
        this.userId = userId;
        this.commentId = commentId;
        this.forumPostId = forumPostId;
        this.isLiked = isLiked;
        this.isDisliked = isDisliked;
    }

    public LikeComment() {
    }


    // Getteri si setteri
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
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
        final StringBuilder sb = new StringBuilder("LikeComment{");
        sb.append("userId=").append(userId);
        sb.append(", commentId=").append(commentId);
        sb.append(", forumPostId=").append(forumPostId);
        sb.append(", isLiked=").append(isLiked);
        sb.append(", isDisliked=").append(isDisliked);
        sb.append('}');
        return sb.toString();
    }

}
