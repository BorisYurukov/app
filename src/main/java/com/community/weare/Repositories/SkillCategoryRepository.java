package com.community.weare.Repositories;

import com.community.weare.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface SkillCategoryRepository extends JpaRepository<Category,Integer> {
    Optional<Category>findByName(String category);


//    boolean exists(SkillCategory category);
}
