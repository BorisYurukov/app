package com.community.weare;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;

public class FactoryPostComment {
    static int idPost = 1;
    static int idComment = 1;

    public FactoryPostComment() {
    }

    public static Post createPost() {
        Post post = new Post();
        post.setPostId(idPost);
        idPost++;
        post.setUser(Factory.createUser());
        StringBuilder sb = new StringBuilder("TestContent");
        post.setContent(sb.toString());
        sb.append("a");
        post.setPublic(true);
        return post;
    }

    public static Comment createComment() {
        Comment comment = new Comment();
        comment.setContent("this is a comment");
        comment.setCommentId(idComment);
        idComment++;
        return comment;
    }
}
