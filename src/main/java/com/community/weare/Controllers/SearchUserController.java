package com.community.weare.Controllers;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Page;
import com.community.weare.Models.User;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.community.weare.utils.ErrorMessages.NOT_SEARCH_RESULT;


@Controller
public class SearchUserController {
    private static final String TYPE = "USER";
    private final UserService userService;

    @Autowired
    public SearchUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public String getUsers(@ModelAttribute(name = "page") Page page,
                           Model model, Principal principal) {
        try {
            if (principal != null) {
                User user = userService.getUserByUserName(principal.getName());
                model.addAttribute("user", user);
            }
            Slice<User> userSlice = userService.getAllUsersByCriteria(page.getIndex(),
                    page.getSize(), page.getSearchParam2(), page.getSearchParam1());
            List<User> users = new ArrayList<>();
            if (userSlice.hasContent() && userSlice.getContent().size() == page.getSize()) {
                page.setIndex(userSlice.nextOrLastPageable().getPageNumber());
                page.setSize(userSlice.nextOrLastPageable().getPageSize());
                model.addAttribute("hasNext", userSlice.hasNext());
            } else if (userSlice.getNumberOfElements() >= 1) {
                userSlice = userService.getAllUsersByCriteria(page.getIndex(),
                        userSlice.getNumberOfElements(), page.getSearchParam2(), page.getSearchParam1());
            }

            users = userSlice.getContent();
            model.addAttribute("users", users);

            if (users.isEmpty()) {
                throw new EntityNotFoundException(NOT_SEARCH_RESULT);
            }

        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index_users";
    }
}
