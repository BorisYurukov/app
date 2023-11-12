package com.community.weare.Models.factories;

import com.community.weare.Models.Post;
import com.community.weare.Models.dto.PostDTO;
import org.springframework.stereotype.Component;

@Component
public class PostFactory {
    public Post createPostFromDTO(PostDTO postDTO) {
        Post newPost = new Post();
        newPost.setPublic(postDTO.isPublic());
        newPost.setContent(postDTO.getContent());
        newPost.setPicture(postDTO.getPicture());
        return newPost;
    }
}
