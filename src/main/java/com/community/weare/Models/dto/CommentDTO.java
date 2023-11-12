package com.community.weare.Models.dto;

public class CommentDTO {

    private int commentId;
    private int postId;
    private int userId;
    private String content;
    private boolean isDeletedConfirmed;

    public CommentDTO(int postId, int userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public CommentDTO() {
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDeletedConfirmed() {
        return isDeletedConfirmed;
    }

    public void setDeletedConfirmed(boolean deletedConfirmed) {
        isDeletedConfirmed = deletedConfirmed;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
