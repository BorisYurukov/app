package com.community.weare.Repositories;

import com.community.weare.Models.PersonalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalProfile, Integer> {


}
