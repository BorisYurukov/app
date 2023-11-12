package com.community.weare.Models.dto;


import com.community.weare.Models.City;
import com.community.weare.Models.Sex;
import com.community.weare.utils.ValidationMessages;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.community.weare.utils.ValidationMessages.*;
import static com.community.weare.utils.ValidationPatterns.EMAIL_VALID_PATTERN;
import static com.community.weare.utils.ValidationPatterns.NAMES_MIN_LEN;

public class UserModel {
    private int id;
    @Size(min = NAMES_MIN_LEN, message = USERNAME_SIZE_MSG)
    private String username;
    private List<String> authorities;
    @Pattern(regexp =EMAIL_VALID_PATTERN , message = VALID_EMAIL)
    private String email;
    @Size(min = NAMES_MIN_LEN, message = FIRST_NAME_SIZE_MSG)
    private String firstName;
    @Size(min = NAMES_MIN_LEN, message = LAST_NAME_SIZE_MSG)
    private String lastNAme;
    private Sex gender;
    private City city;


    private String birthInput;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthYear;

    @Size(max = 250, message = PERSONAL_REVIEW_MSG)
    private String personalReview;

    private String expertise;
    private List<String> skills = new ArrayList<>();


    public UserModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNAme() {
        return lastNAme;
    }

    public void setLastNAme(String lastNAme) {
        this.lastNAme = lastNAme;
    }

    public Sex getGender() {
        return gender;
    }

    public void setGender(Sex gender) {
        this.gender = gender;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public LocalDate getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(LocalDate birthYear) {
        this.birthYear = birthYear;
    }

    public String getPersonalReview() {
        return personalReview;
    }

    public void setPersonalReview(String personalReview) {
        this.personalReview = personalReview;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setSkill(String skill) {
        this.skills.add(skill);
    }

}
