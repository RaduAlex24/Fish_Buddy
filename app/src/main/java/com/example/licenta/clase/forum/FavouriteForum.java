package com.example.licenta.clase.forum;

public class FavouriteForum {
    private int userId;
    private int forumPostId;


    // Constructori
    public FavouriteForum(int userId, int forumPostId) {
        this.userId = userId;
        this.forumPostId = forumPostId;
    }

    public FavouriteForum() {
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


    // Metode
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FavouriteForum{");
        sb.append("userId=").append(userId);
        sb.append(", forumPostId=").append(forumPostId);
        sb.append('}');
        return sb.toString();
    }

}
