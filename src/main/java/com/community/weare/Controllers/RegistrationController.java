package com.community.weare.Controllers;


import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Models.Category;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

import static com.community.weare.utils.ErrorMessages.PASSWORD_NOT_CONFIRMED;

@Controller
public class RegistrationController {
    private final UserService userService;
    private final SkillCategoryService skillCategoryService;
    private final UserFactory userFactory;

    @Autowired
    public RegistrationController(UserService userService, SkillCategoryService skillCategoryService,
                                  UserFactory userFactory) {
        this.userService = userService;
        this.skillCategoryService = skillCategoryService;
        this.userFactory = userFactory;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register-new";
    }

    @ModelAttribute(name = "categories")
    public void addExpertiseList(Model model) {
        List<Category> expertise = skillCategoryService.getAll();
        model.addAttribute("categories", expertise);
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@ModelAttribute @Valid UserDTO user, BindingResult bindingResult) {
        ModelAndView modelAndViewError = new ModelAndView("register-new");
        modelAndViewError.addObject("user", user);
        ModelAndView modelAndView = new ModelAndView("confirmation-new");

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            modelAndViewError.addObject("error", PASSWORD_NOT_CONFIRMED);
            modelAndViewError.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndViewError;
        }
        if (bindingResult.hasErrors()) {
            modelAndViewError.addObject("error", bindingResult.getFieldError().getDefaultMessage());
            modelAndViewError.setStatus(HttpStatus.BAD_GATEWAY);
            return modelAndViewError;
        }
        try {
            User userDB = userFactory.convertDTOtoUSER(user);
            int id = userService.registerUser(userDB, user.getCategory());
            modelAndView.addObject("id", id);
            return modelAndView;
        } catch (DuplicateEntityException e) {
            modelAndViewError.addObject("error", e.getMessage());
            modelAndViewError.setStatus(HttpStatus.CONFLICT);
            return modelAndViewError;
        }

    }
}
