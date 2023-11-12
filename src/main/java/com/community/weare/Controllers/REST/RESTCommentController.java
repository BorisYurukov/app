package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.factories.CommentFactory;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class RESTCommentController {

    private CommentService commentService;
    private PostService postService;
    private CommentFactory commentFactory;
    private UserService userService;

    @Autowired
    public RESTCommentController(CommentService commentService, PostService postService,
                                 CommentFactory commentFactory,
                                 UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.commentFactory = commentFactory;
        this.userService = userService;
    }

    @GetMapping
    public List<Comment> findAll(Sort sort) {
        return commentService.findAll(sort);
    }

    @GetMapping("/byPost")
    public List<Comment> findAllCommentsOfPost(@RequestParam int postId, Sort sort, Principal principal) {
        if (!postService.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post does not exists");
        }
        Post post = postService.getOne(postId, principal);
        return commentService.findAllCommentsOfPost(post, sort);
    }

    @GetMapping("/single")
    public Comment getOne(@RequestParam int commentId) {
        try {
            return commentService.getOne(commentId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format
                    ("Comment with id %d does not exists", commentId));
        }
    }

    @PostMapping("/auth/creator")
    public Comment save(@RequestBody CommentDTO commentDTO, Principal principal) {
        Comment newComment = commentFactory.createCommentFromDTO(commentDTO, principal);
        try {
            return commentService.save(newComment, principal);
        } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/auth/likesUp")
    public Comment likeComment(@RequestParam int commentId, Principal principal) {
        User user;
        try {
            user = userService.getUserByUserName(principal.getName());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        Comment commentToLike = commentService.getOne(commentId);
        boolean isCommentLiked = commentToLike.isLiked2(user.getUsername());

        if (isCommentLiked) {
            try {
                commentService.dislikeComment(commentId, principal);
            } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            try {
                commentService.likeComment(commentId, principal);
            } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
        return commentToLike;
    }

    @PutMapping("/auth/editor")
    public void editComment(Principal principal, @RequestParam int commentId, String content) {
        try {
            commentService.editComment(commentId, content, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/auth/manager")
    public void deleteComment(Principal principal, @RequestParam int commentId) {
        try {
            commentService.deleteComment(commentId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
