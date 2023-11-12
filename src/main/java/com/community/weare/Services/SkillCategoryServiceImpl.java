package com.community.weare.Services;

import com.community.weare.Models.Category;
import com.community.weare.Repositories.SkillCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class SkillCategoryServiceImpl implements SkillCategoryService {
    private final SkillCategoryRepository categoryRepository;

    @Autowired
    public SkillCategoryServiceImpl(SkillCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    @Override
    public Category createIfNotExist(Category category1) {
        boolean exists = categoryRepository.findByName(category1.getName()).isPresent();
        Category categoryDB ;
        if (exists == true) {
            categoryDB = categoryRepository.findByName(category1.getName()).get();
        } else {
            categoryDB = categoryRepository.saveAndFlush(category1);
        }
        return categoryDB;
    }

}
