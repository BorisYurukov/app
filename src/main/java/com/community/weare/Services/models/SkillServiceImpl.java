package com.community.weare.Services.models;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Skill;
import com.community.weare.Models.Category;
import com.community.weare.Repositories.SkillRepository;
import com.community.weare.Services.SkillCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.community.weare.utils.ErrorMessages.SKILL_NOT_FOUND;

@Transactional
@Service
public class SkillServiceImpl implements SkillService {
    private SkillRepository skillRepository;
    private SkillCategoryService categorySkillService;

    @Autowired
    public SkillServiceImpl(SkillRepository skillRepository, SkillCategoryService categorySkillService) {
        this.skillRepository = skillRepository;
        this.categorySkillService = categorySkillService;
    }

    @Override
    public List<Skill> findAll(Sort sort) {
        return skillRepository.findAll(Sort.by(Sort.Direction.ASC, "skill"));
    }

    @Override
    public Skill getOne(int skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException(SKILL_NOT_FOUND);
        }
        return skillRepository.getOne(skillId);
    }

    @Transactional
    @Override
    public Skill save(Skill skill) {
        return skillRepository.saveAndFlush(skill);
    }

    @Override
    public void editSkill(int skillId, String skill) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException(SKILL_NOT_FOUND);
        }
        Skill skillToEdit = skillRepository.getOne(skillId);
        skillToEdit.setSkill(skill);
        skillRepository.save(skillToEdit);
    }

    @Override
    public void deleteSkill(int skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException(SKILL_NOT_FOUND);
        }
        Skill skillToDelete = skillRepository.getOne(skillId);
        skillRepository.delete(skillToDelete);
    }

    @Override
    public Optional<Skill> getByName(String name) {
        return skillRepository.findSkillBySkill(name);
    }

    @Override
    public List<Skill> getAllByCategory(Category category) {
        return skillRepository.findByCategory(category);
    }

    @Transactional
    @Override
    public Skill createIfNotExist(Skill skill) {
        boolean exists = skillRepository.findSkillBySkill(skill.getSkill()).isPresent();
        Skill skillDB = new Skill();

        if (exists == true) {
            skillDB = skillRepository.findSkillBySkill(skill.getSkill()).get();
        } else {
            skillDB = skillRepository.saveAndFlush(skill);
        }
        return skillDB;

    }
}

