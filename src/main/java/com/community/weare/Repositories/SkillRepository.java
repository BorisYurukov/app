package com.community.weare.Repositories;

import com.community.weare.Models.Skill;
import com.community.weare.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Integer> {

    Optional<Skill>findSkillBySkill(String name);
    List<Skill>findByCategory(Category category);
}
