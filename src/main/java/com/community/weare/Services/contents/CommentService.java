package com.community.weare.Services.contents;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.List;

public interface CommentService {
    List<Comment> findAll();

    List<Comment> findAll(Sort sort);

    List<Comment> findAllCommentsOfPost(Post post, Sort sort);

    boolean existsById(int commentId);

    Comment getOne(int commentId);

    Comment save(Comment comment, Principal principal);

    void likeComment(int commentId, Principal principal);

    void dislikeComment(int commentId, Principal principal);

    void editComment(int commentId, String content, Principal principal);

    void deleteComment(int commentId, Principal principal);

    int deleteCommentByPostPostId(int postId);
}
