package com.community.weare.Services.models;

import com.community.weare.Models.Skill;
import com.community.weare.Models.Category;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface SkillService {

    List<Skill> findAll(Sort sort);

    Skill getOne(int skillId);

    Skill save(Skill skill);

    void editSkill(int skillId, String skill);

    void deleteSkill(int skillId);

    Optional<Skill> getByName(String name);

    List<Skill>getAllByCategory(Category category);

    Skill createIfNotExist(Skill skill);

}
