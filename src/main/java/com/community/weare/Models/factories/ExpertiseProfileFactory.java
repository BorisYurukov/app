package com.community.weare.Models.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static com.community.weare.Models.factories.FactoryUtils.getNotNull;

@Service
public class ExpertiseProfileFactory {
    private final SkillService skillService;
    private final SkillCategoryService skillCategoryService;
    private final ModelMapper modelMapper;


    @Autowired
    public ExpertiseProfileFactory(SkillService skillService, SkillCategoryService skillCategoryService,
                                   ModelMapper modelMapper) {
        this.skillService = skillService;
        this.skillCategoryService = skillCategoryService;
        this.modelMapper = modelMapper;

    }

    @Transactional
    public ExpertiseProfile convertDTOtoExpertiseProfile(ExpertiseProfileDTO profileDTO) {

        ExpertiseProfile expertiseProfile = new ExpertiseProfile();

        List<Skill> skillList = new ArrayList<>();

        Category skillCategory = skillCategoryService.createIfNotExist(profileDTO.getCategory());

        expertiseProfile.setCategory(skillCategory);
        expertiseProfile.setId(profileDTO.getId());
        profileDTO.setSkills();

        for (String skillValue : profileDTO.getSkills()) {
            if (skillValue != null && !skillValue.isEmpty()) {
                Skill skill = new Skill();
                skill.setSkill(skillValue);
                skill.setCategory(skillCategory);
                skill = skillService.createIfNotExist(skill);
                skillList.add(skill);
            }
        }
        expertiseProfile.setAvailability(profileDTO.getAvailability());
        expertiseProfile.setSkills(skillList);
        return expertiseProfile;
    }

    public ExpertiseProfile mergeExpertProfile(ExpertiseProfile newProfile, ExpertiseProfile oldProfile) {
        oldProfile.setSkills(newProfile.getSkills());
        oldProfile.setCategory(newProfile.getCategory());
        oldProfile.setAvailability(getNotNull(newProfile.getAvailability(), oldProfile.getAvailability()));
        return oldProfile;
    }

}




