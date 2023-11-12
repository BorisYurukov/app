package com.community.weare.Repositories;

import com.community.weare.Models.ExpertiseProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertiseRepository extends JpaRepository<ExpertiseProfile,Integer> {
}
