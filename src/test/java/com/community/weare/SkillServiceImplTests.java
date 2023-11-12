package com.community.weare;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.Skill;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.SkillRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.models.SkillServiceImpl;
import com.community.weare.Services.users.PersonalInfoServiceImpl;
import com.community.weare.Services.users.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.Optional;

import static com.community.weare.Factory.createUser;
import static com.community.weare.Factory.setAuthorities;

@RunWith(MockitoJUnitRunner.class)
public class SkillServiceImplTests {

    @InjectMocks
    SkillServiceImpl skillMockService;

    @Mock
    SkillRepository skillRepository;

    @Mock
    PersonalInfoRepository personalInfoRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @Test
    public void getAllSkillsShould_callRepository() {
        //arrange
         Sort sort=Sort.by(Sort.Direction.ASC, "skill");

        //act
        skillMockService.findAll(sort);

        //assert
        Mockito.verify(skillRepository, Mockito.times(1)).findAll(Sort.by(Sort.Direction.ASC, "skill"));
    }
    @Test
    public void getOneShould_callRepository() {
        //arrange
        Skill skill=new Skill();
        skill.setSkill("ssss");
        skill.setSkillId(1);

        Mockito.when(skillRepository.getOne(1)).thenReturn(skill);
        Mockito.when(skillRepository.existsById(1))
                .thenReturn(true);
        //act
        skillMockService.getOne(1);

        //assert
        Mockito.verify(skillRepository, Mockito.times(1)).getOne(1);
    }

    @Test
    public void editShould_callRepository() {
        //arrange
        Skill skill=new Skill();
        skill.setSkill("ssss");
        skill.setSkillId(1);

        Mockito.when(skillRepository.getOne(1)).thenReturn(skill);
        Mockito.when(skillRepository.existsById(1))
                .thenReturn(true);
        //act
        skillMockService.editSkill(1,"ggg");

        //assert
        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
    }
    @Test
    public void findOrCreateShould_callRepository() {
        //arrange
        Skill skill=new Skill();
        skill.setSkill("ssss");
        skill.setSkillId(1);

        Mockito.when(skillRepository.findSkillBySkill(skill.getSkill())).thenReturn(Optional.of(skill));

        //act
        skillMockService.createIfNotExist(skill);

        //assert
        Mockito.verify(skillRepository, Mockito.times(2)).findSkillBySkill(skill.getSkill());
    }

    @Test
    public void editShould_ThrowInvalidExc() {
        Skill skill=new Skill();
        skill.setSkill("ssss");
        skill.setSkillId(1);

        Mockito.when(skillRepository.existsById(1))
                .thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            skillMockService.editSkill(1,"hhh");
        });
    }

    @Test
    public void getOneShould_ThrowInvalidExc() {

        Mockito.when(skillRepository.existsById(1))
                .thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            skillMockService.getOne(1);
        });
    }


}
