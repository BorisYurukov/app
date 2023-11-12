package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Page;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.community.weare.utils.ErrorMessages.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("")
    public String showAdminHomePage(Principal principal, Model model) {

        if (principal != null) {
            User user = userService.getUserByUserName(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("page", new Page());
        }
        return "admin";
    }

    @PostMapping("")
    public String refreshRankOfAllPosts(Principal principal, Model model, Sort sort) {
        userService.ifNotAdminThrow(principal.getName(), new User());
        List<Post> allPosts = postService.findAllSortedByUserAndTime(sort);
        postService.refreshRankOfGroupOfPosts(allPosts);
        return "redirect:/admin";
    }

    @GetMapping("/users")
    public String showAllUsersPage(@ModelAttribute(name = "page") Page page,
                                   Model model, Principal principal) {
        try {
            User user = userService.getUserByUserName(principal.getName());
            model.addAttribute("user", user);
            List<User> userList = new ArrayList<>();
            Slice<User> userSlice = userService.findSliceWithUsers
                    (page.getIndex(), page.getSize(), "username");

            if (userSlice.hasContent()) {
                userList = userSlice.getContent();
                page.setSize(userSlice.nextOrLastPageable().getPageSize());
                page.setIndex(userSlice.nextOrLastPageable().getPageNumber());
                model.addAttribute("users", userList);
            }
            if (userList.isEmpty()) {
                model.addAttribute("error", NOT_SEARCH_RESULT);
            }
            model.addAttribute("hasNext", userSlice.hasNext());

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "admin_users";
    }

    @PostMapping("/status")
    public ModelAndView disableEnableUser(@ModelAttribute User user, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("admin_users");
        try {
            userService.disableEnableUser(principal.getName(), user.getUserId());
        } catch (EntityNotFoundException e) {
            modelAndView.addObject("error", NOT_FOUND);
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        } catch (InvalidOperationException e) {
            modelAndView.addObject("error", NOT_AUTHORISED);
            modelAndView.setStatus(HttpStatus.UNAUTHORIZED);
            return modelAndView;
        }
        return new ModelAndView("redirect:/auth/users/" + user.getUserId() + "/profile");
    }

}
