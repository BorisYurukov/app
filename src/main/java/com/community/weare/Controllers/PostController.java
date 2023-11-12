package com.community.weare.Controllers;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Category;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Models.dto.PostDTO2;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Models.factories.CommentFactory;
import com.community.weare.Models.factories.PostFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private PostService postService;
    private UserService userService;
    private CommentService commentService;
    private PostFactory postFactory;
    private SkillCategoryService skillCategoryService;
    private CommentFactory commentFactory;

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService,
                          PostFactory postFactory, SkillCategoryService skillCategoryService,
                          CommentFactory commentFactory) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.postFactory = postFactory;
        this.skillCategoryService = skillCategoryService;
        this.commentFactory = commentFactory;
    }

    @GetMapping("")
    public String showFeed(Model model, Sort sort, Principal principal) {
        model.addAttribute("postDTO2", new PostDTO2());
        model.addAttribute("usersCount", userService.getAllUsers().size());
        model.addAttribute("postsCount", postService.findAll().size());
        model.addAttribute("postDTO", new PostDTO());
        model.addAttribute("category", new Category("Marketing"));
        model.addAttribute("posts", postService.findPostsByAuthority(sort, principal));
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "allPosts";
    }

    @ModelAttribute("allCategories")
    public List<Category> populateCategories() {
        return skillCategoryService.getAll();
    }

    @PostMapping("")
    public String likeDislikeFilterPost(@ModelAttribute("postDTO2") PostDTO2 postDTO2,
                                        @ModelAttribute("category") Category category,
                                        @ModelAttribute("allCategories") List<Category> allCategories,
                                        @ModelAttribute("UserPrincipal") User userPrincipal,
                                        Model model, Principal principal, Sort sort) {
        if (postDTO2.getPostId() != 0) {
            if (principal == null) {
                model.addAttribute("error", "User isn't authorised");
                return "redirect:/";
            }
            model.addAttribute("posts", postService.findPostsPersonalFeed(principal));
            boolean isPostLiked = postService.getOne(postDTO2.getPostId(), principal).isLiked2(principal.getName());

            if (isPostLiked) {
                try {
                    postService.dislikePost(postDTO2.getPostId(), principal);
                } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
                    model.addAttribute("error", e.getMessage());
                    model.addAttribute("UserPrincipal",
                            userService.getUserByUserName(principal.getName()));
                    return "allPosts";
                }
            } else {
                try {
                    postService.likePost(postDTO2.getPostId(), principal);
                } catch (DuplicateEntityException | EntityNotFoundException | InvalidOperationException e) {
                    model.addAttribute("error", e.getMessage());
                    model.addAttribute("UserPrincipal",
                            userService.getUserByUserName(principal.getName()));
                    return "allPosts";
                }
            }
        }

        //filter isPublic
        if (postDTO2.getPublicity() != null) {
            if (!postDTO2.getPublicity().equals("all")) {
                boolean isPublic = Boolean.parseBoolean(postDTO2.getPublicity());
                List<Post> filteredPosts =
                        postService.filterPostsByPublicity
                                (postService.findAllPostsByAlgorithm(sort, principal), isPublic);
                model.addAttribute("posts", filteredPosts);
            } else {
                model.addAttribute("posts", postService.findPostsByAuthority(sort, principal));
            }
        }

        //filter Category
        if (category.getName() != null) {
            if (!category.getName().equals("All")) {
                List<Post> filteredPosts =
                        postService.filterPostsByPublicity
                                (postService.filterPostsByCategory
                                        (postService.findAllPostsByAlgorithm(sort, principal), category.getName()), true);
                model.addAttribute("posts", filteredPosts);
            } else {
                model.addAttribute("posts", postService.findPostsByAuthority(sort, principal));
            }
        }
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "allPosts";
    }

    @GetMapping("/{id}")
    public String showPost(Model model, @PathVariable(name = "id") int postId, Principal principal) {
        Post post01;
        try {
            post01 = postService.getOne(postId, principal);
        } catch (EntityNotFoundException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "post-single";
        }
        model.addAttribute("post", post01);
        model.addAttribute("comment", new CommentDTO());
        model.addAttribute("User", new UserDtoRequest());
        model.addAttribute("commentDTOlike", new CommentDTO());
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            model.addAttribute("isAdmin", userService.isAdmin(principal));
        }
        return "post-single";
    }

    @PostMapping("/{id}")
    public String leaveCommentAndShowPosts(@ModelAttribute("comment") CommentDTO commentDTO,
                                           @ModelAttribute("User") UserDtoRequest userDto,
                                           @ModelAttribute("postDTO") PostDTO postDTO,
                                           @ModelAttribute("postDTO2") PostDTO2 postDTO2,
                                           @ModelAttribute("category") Category category,
                                           @ModelAttribute("UserPrincipal") User userPrincipal,
                                           @PathVariable(name = "id") int postId, Principal principal, Model model) {
        if (commentDTO.getContent() != null) {
            Comment newComment = commentFactory.createCommentFromInput(commentDTO, postId, principal);
            try {
                commentService.save(newComment, principal);
            } catch (InvalidOperationException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("post", postService.getOne(postId, principal));
                model.addAttribute("commentDTOlike", new CommentDTO());
                model.addAttribute("comment", new CommentDTO());
                model.addAttribute("User", new UserDtoRequest());
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
                model.addAttribute("isAdmin", userService.isAdmin(principal));
                return "post-single";
            }
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            return "redirect:/posts/" + postId + "#leaveComment";
        }

        //All posts of user
        if (userDto.getId() != 0) {
            User user = userService.getUserById(userDto.getId());
            List<Post> postsOfUser = postService.findAllByUser(user.getUsername(), principal);
            model.addAttribute("posts", postsOfUser);
            if (principal != null) {
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            }
            return "allPosts";
        }
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/auth/newPost")
    public String newPostData(Model model) {
        PostDTO post = new PostDTO();
        model.addAttribute("post", post);
        return "newPost";
    }

    @GetMapping("/{id}/postImage")
    public void renderPostImageFormDB(@PathVariable int id, HttpServletResponse response,
                                      Principal principal) throws IOException {
        Post post = postService.getOne(id, principal);
        if (post.getPicture() != null) {
            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(post.getPicture()));
            IOUtils.copy(is, response.getOutputStream());
        }
    }

    @Transactional
    @PostMapping("/auth/newPost")
    public String newPost(Model model, @ModelAttribute("post") PostDTO post, BindingResult errors,
                          @RequestParam("imagefile") MultipartFile file, Principal principal) throws IOException {
        if (errors.hasErrors()) {
            return "newPost";
        }
        Post newPost = postFactory.createPostFromDTO(post);
        newPost.setUser(userService.getUserByUserName(principal.getName()));
        newPost.setPicture(Base64.getEncoder().encodeToString(file.getBytes()));

        try {
            postService.save(newPost, principal);
        } catch (IllegalArgumentException | DuplicateEntityException | InvalidOperationException |
                EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "newPost";
        }
        model.addAttribute("posts", postService.findAllByUser(principal.getName(), principal));
        model.addAttribute("postDTO2", new PostDTO2());
        model.addAttribute("postDTO", new PostDTO());
        model.addAttribute("category", new Category("Marketing"));
        model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        return "allPosts";
    }

    @GetMapping("/auth/editor/{id}")
    public String editPostData(Model model, @PathVariable(name = "id") int postId, Principal principal) {
        Post post;
        try {
            post = postService.getOne(postId, principal);
        } catch (EntityNotFoundException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "postEdit";
        }
        model.addAttribute("post", post);
        model.addAttribute("postDTO", new PostDTO());
        return "postEdit";
    }

    @PostMapping("/auth/editor/{id}")
    public String editPost(Model model, @PathVariable(name = "id") int postId,
                           @ModelAttribute("post") Post postToEdit,
                           @ModelAttribute("postDTO") PostDTO postDTO, Principal principal,
                           @RequestParam("imagefile") MultipartFile file) throws IOException {
        postDTO.setPicture(Base64.getEncoder().encodeToString(file.getBytes()));
        try {
            postService.editPost(postId, postDTO, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "postEdit";
        }
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/auth/manager/{id}")
    public String deletePostData(Model model, Principal principal, @PathVariable(name = "id") int postId) {
        Post post;
        try {
            post = postService.getOne(postId, principal);
        } catch (EntityNotFoundException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "postDelete";
        }
        model.addAttribute("post", post);
        model.addAttribute("postDTO2", new PostDTO2());
        return "postDelete";
    }

    @PostMapping("/auth/manager/{id}")
    public String deletePost(Model model, Principal principal, @PathVariable(name = "id") int postId,
                             @ModelAttribute("postDTO2") PostDTO2 postDTO2,
                             @ModelAttribute("post") Post postToDelete) {
        if (!postDTO2.isDeletedConfirmed()) {
            if (principal != null) {
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            }
            return "redirect:/posts/" + postId;
        }
        try {
            postService.deletePost(postId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "postDelete";
        }
        return "postDeleteConfirmation";
    }
}

