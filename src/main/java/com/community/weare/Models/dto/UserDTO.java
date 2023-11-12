package com.community.weare.Models.dto;


import com.community.weare.Models.Category;
import com.community.weare.utils.ValidationMessages;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

import static com.community.weare.utils.ValidationMessages.*;
import static com.community.weare.utils.ValidationPatterns.*;

public class UserDTO {

    @Pattern(regexp = USER_NAME_VALID_PATTERN, message = USERNAME_NO_WHITE_SPACES)
    private String username;

    @Size(min = PASSWORD_MIN_LEN, message = PASSWORD_SIZE_MSG)
    private String password;

    private String confirmPassword;

    @Pattern(regexp = EMAIL_VALID_PATTERN, message = VALID_EMAIL)
    private String email;

    private Category category;

    private List<String> authorities;

    public UserDTO(String username, String password, List<String> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
