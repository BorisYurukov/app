package com.community.weare.Models.dto;

public class PostDTO2 {
    private int postId;
    private boolean isDeletedConfirmed;
    private String publicity;

    public PostDTO2() {
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public boolean isDeletedConfirmed() {
        return isDeletedConfirmed;
    }

    public void setDeletedConfirmed(boolean deletedConfirmed) {
        isDeletedConfirmed = deletedConfirmed;
    }

    public String getPublicity() {
        return publicity;
    }

    public void setPublicity(String publicity) {
        this.publicity = publicity;
    }
}
