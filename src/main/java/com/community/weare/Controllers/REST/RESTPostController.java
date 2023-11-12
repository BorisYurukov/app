package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Models.factories.PostFactory;
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
@RequestMapping("/api/post")
public class RESTPostController {
    private PostService postService;
    private UserService userService;
    private PostFactory postFactory;

    @Autowired
    public RESTPostController(PostService postService, UserService userService, PostFactory postFactory) {
        this.postService = postService;
        this.userService = userService;
        this.postFactory = postFactory;
    }

    @GetMapping("/")
    public List<Post> findAll(Sort sort, Principal principal) {
        return postService.filterPostsByPublicity
                (postService.findAllPostsByAlgorithm(sort, principal), true);
    }

    @GetMapping("/Comments")
    public List<Comment> showComments(@RequestParam int postId) {
        return postService.showComments(postId);
    }

    @PostMapping("/auth/creator")
    public Post save(@RequestBody PostDTO postDTO, Principal principal) {
        Post newPost = postFactory.createPostFromDTO(postDTO);
        newPost.setUser(userService.getUserByUserName(principal.getName()));
        try {
            return postService.save(newPost, principal);
        } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/auth/likesUp")
    public Post likeAPost(@RequestParam int postId, Principal principal) {
        User user;
        try {
            user = userService.getUserByUserName(principal.getName());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        Post postToLike;
        try {
            postToLike = postService.getOne(postId, principal);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        boolean isPostLiked = postToLike.isLiked2(user.getUsername());

        if (isPostLiked) {
            try {
                postService.dislikePost(postId, principal);
            } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            try {
                postService.likePost(postId, principal);
            } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
        return postToLike;
    }

    @PutMapping("/auth/editor")
    public void editPost(Principal principal, @RequestParam int postId, @RequestBody PostDTO postDTO) {
        try {
            postService.editPost(postId, postDTO, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/auth/manager")
    public void deletePost(Principal principal, @RequestParam int postId) {
        try {
            postService.deletePost(postId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
