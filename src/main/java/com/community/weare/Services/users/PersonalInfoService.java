package com.community.weare.Services.users;

import com.community.weare.Models.City;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;

import java.util.List;

public interface PersonalInfoService {

    PersonalProfile upgradeProfile(String principal, User user, PersonalProfile personalProfile);
    List<City> getAllCities();
}
