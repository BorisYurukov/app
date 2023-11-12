package com.community.weare.Models.dto;

public class PostDTO {

    private boolean isPublic;
    private String content;
    private String picture;

    public PostDTO(boolean isPublic, String content, String picture) {
        this.isPublic = isPublic;
        this.content = content;
        this.picture = picture;
    }

    public PostDTO() {
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
