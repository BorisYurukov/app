package com.community.weare.Controllers;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private CommentService commentService;
    private UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/editor/{id}")
    public String editCommentData(Model model, @PathVariable(name = "id") int commentId) {
        model.addAttribute("commentDTO", new CommentDTO());
        try {
            commentService.getOne(commentId);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "commentEdit";
        }
        return "commentEdit";
    }

    @PostMapping("/editor/{id}")
    public String editComment(Model model, Principal principal, @PathVariable(name = "id") int commentId,
                              @ModelAttribute("commentDTO") CommentDTO commentDTO) {
        Comment commentToEdit = commentService.getOne(commentId);
        try {
            commentService.editComment(commentId, commentDTO.getContent(), principal);
        } catch (IllegalArgumentException | DuplicateEntityException | InvalidOperationException |
                EntityNotFoundException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "commentEdit";
        }
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "redirect:/posts/" + commentToEdit.getPost().getPostId() + "#leaveComment";
    }

    @GetMapping("/manager/{id}")
    public String deleteCommentData(Model model, Principal principal, @PathVariable(name = "id") int commentId) {
        Comment commentToDelete;
        try {
            commentToDelete = commentService.getOne(commentId);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "commentDelete";
        }
        model.addAttribute("comment", commentToDelete);
        model.addAttribute("commentDTO", new CommentDTO());
        return "commentDelete";
    }

    @PostMapping("/manager/{id}")
    public String deleteComment(Model model, Principal principal, @PathVariable(name = "id") int commentId,
                                @ModelAttribute("commentDTO") CommentDTO commentDTO,
                                @ModelAttribute("comment") Comment comment) {
        Comment commentToDelete = commentService.getOne(commentId);
        if (!commentDTO.isDeletedConfirmed()) {
            if (principal != null) {
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            }
            return "redirect:/posts/" + commentToDelete.getPost().getPostId() + "#leaveComment";
        }
        try {
            commentService.deleteComment(commentId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException | InvalidOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "commentDelete";
        }
        return "commentDeleteConfirmation";
    }
}
