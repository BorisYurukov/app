package com.community.weare.Services;

import com.community.weare.Models.Category;

import java.util.List;
import java.util.Optional;

public interface SkillCategoryService {

    List<Category> getAll();

    Category createIfNotExist(Category category1);
}
