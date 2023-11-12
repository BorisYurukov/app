package com.community.weare.Models.dto;

import com.community.weare.Models.Category;
import com.community.weare.Models.Skill;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpertiseProfileDTO {

    int id;

    private List<String > skills =new ArrayList<>();

    private String  skill1;
    private String  skill2;
    private String  skill3;
    private String  skill4;
    private String  skill5;

    private Category category;

    @Positive
    private double availability;


    public ExpertiseProfileDTO() {
    }

    public List<String > getSkills() {
        return skills;
    }

    public void setSkills() {
        this.skills.add(skill1);
        this.skills.add(skill2);
        this.skills.add(skill3);
        this.skills.add(skill4);
        this.skills.add(skill5);
    }


    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getSkill1() {
        return skill1;
    }

    public void setSkill1(String skill1) {
        this.skill1 = skill1;
    }

    public String getSkill2() {
        return skill2;
    }

    public void setSkill2(String skill2) {
        this.skill2 = skill2;
    }

    public String getSkill3() {
        return skill3;
    }

    public void setSkill3(String skill3) {
        this.skill3 = skill3;
    }

    public String getSkill4() {
        return skill4;
    }

    public void setSkill4(String skill4) {
        this.skill4 = skill4;
    }

    public String getSkill5() {
        return skill5;
    }

    public void setSkill5(String skill5) {
        this.skill5 = skill5;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }
}
