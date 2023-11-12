package com.community.weare.Models.factories;

import com.community.weare.Models.Comment;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class CommentFactory {
    private PostService postService;
    private UserService userService;

    @Autowired
    public CommentFactory(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public Comment createCommentFromDTO(CommentDTO commentDTO, Principal principal) {
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setPost(postService.getOne(commentDTO.getPostId(), principal));
        newComment.setUser(userService.getUserById(commentDTO.getUserId()));
        return newComment;
    }

    public Comment createCommentFromInput(CommentDTO commentDTO, int postId, Principal principal) {
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setPost(postService.getOne(postId, principal));
        newComment.setUser(userService.getUserByUserName(principal.getName()));
        return newComment;
    }
}
