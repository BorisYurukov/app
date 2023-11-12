package com.community.weare.Controllers;


import com.community.weare.Models.Category;
import com.community.weare.Models.Page;
import com.community.weare.Models.User;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller

public class HomeController {
    private final UserService userService;
    private final SkillCategoryService skillCategoryService;

    @Autowired
    public HomeController(UserService userService, SkillCategoryService skillCategoryService) {
        this.userService = userService;
        this.skillCategoryService = skillCategoryService;
    }

    @GetMapping("/about-us")
    public String aboutUs(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("UserPrincipal",
                    userService.getUserByUserName(principal.getName()));
        }
        return "aboutUs";
    }


    @RequestMapping(value = {"/", "/auth"}, method = RequestMethod.GET)
    public String showAuthPage(Principal principal, Model model) {
        model.addAttribute("page", new Page());
        model.addAttribute("latestUsers",
                userService.findSliceWithLatestUsers(PageRequest.of(0, 5)));
        if (principal != null) {
            User user = userService.getUserByUserName(principal.getName());
            model.addAttribute("user", user);
        }
        return "index_new";
    }

}

