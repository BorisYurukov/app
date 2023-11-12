package com.community.weare;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Repositories.ExpertiseRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.connections.RequestServiceImpl;
import com.community.weare.Services.users.PersonalInfoServiceImpl;
import com.community.weare.Services.users.UserServiceImpl;
import org.junit.jupiter.api.Assertions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.security.Principal;
import java.util.*;

import static com.community.weare.Factory.*;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTests {
    @InjectMocks
    UserServiceImpl mockUserService;


    @Mock
    PersonalInfoServiceImpl profileService;
    @Mock
    RequestServiceImpl requestService;
    @Mock
    UserFactory userFactory ;
    @Mock
    PersonalInfoRepository personalRepo;
    @Mock
    ExpertiseRepository expertiseRepository;
    @Mock
    UserRepository userRepository;

    @Test
    public void getUserByIdShould_CallRepository() {
        //arrange
        User user = createUser();
        user.setUserId(1);

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user));
        //act
        mockUserService.getUserById(1);
        //assert
        Mockito.verify(userRepository, Mockito.times(1)).findById(1);
    }
    @Test
    public void getUserModelByIdShould_CallRepository() {
        //arrange
        User user = createUser();
        user.setUserId(1);

        Mockito.when(userRepository.getOne(1)).thenReturn(user);
        //act
        mockUserService.getUserModelById(1);
        //assert
        Mockito.verify(userRepository, Mockito.times(1)).getOne(1);
    }
    @Test
    public void getUserModelByIdShould_Return() {
        //arrange
        User user = createUser();
        user.setUserId(1);
        UserModel userModel=new UserModel();
        userModel.setId(1);

        Mockito.when(userRepository.getOne(1)).thenReturn(user);
        Mockito.when(userFactory.convertUSERtoModel(user)).thenReturn(userModel);

        //act
        mockUserService.getUserModelById(1);
        //assert
      Assert.assertEquals(userModel,mockUserService.getUserModelById(1));
    }

    @Test
    public void getUserByIdShould_ReturnUser_WhenUserExists() {
        User originalUser = createUser();

        Mockito.when(userRepository.findByUsername(originalUser.getUsername()))
                .thenReturn(Optional.of(originalUser));

        Assert.assertSame(originalUser, mockUserService.getUserByUserName(originalUser.getUsername()));
    }

    @Test
    public void disableUserShould_DisableUser() {
        //arrange
        User user = createUser();
        user.setUserId(1);
        Principal principal = () -> "tedi";
        user = setAuthorities(user);
        user.setEnabled(1);
        User UserToTest =createUser();
        UserToTest.setEnabled(0);

        Mockito.when(userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getOne(1))
                .thenReturn(user);
        Mockito.when(userRepository.saveAndFlush(user))
                .thenReturn(UserToTest);
        //act
        mockUserService.disableEnableUser("tedi", 1);
        user.setEnabled(0);

        //assert
        Assert.assertEquals(user.isEnabled(), UserToTest.isEnabled());
    }
    @Test
    public void disableUserShould_EnableUser() {
        //arrange
        User user = createUser();
        user.setUserId(1);
        Principal principal = () -> "tedi";
        user = setAuthorities(user);
        user.setEnabled(0);
        User UserToTest =createUser();
        UserToTest.setEnabled(1);

        Mockito.when(userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getOne(1))
                .thenReturn(user);
        Mockito.when(userRepository.saveAndFlush(user))
                .thenReturn(UserToTest);
        //act
        mockUserService.disableEnableUser("tedi", 1);
        user.setEnabled(1);

        //assert
        Assert.assertEquals(user.isEnabled(), UserToTest.isEnabled());
    }

    @Test
    public void disableUserShould_CallRepository() {
        //arrange
        User user = createUser();
        Principal principal = () -> "tedi";
        user = setAuthorities(user);
        user.setEnabled(1);
        User UserToTest =createUser();
        UserToTest.setEnabled(0);

        Mockito.when(userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.getOne(1))
                .thenReturn(user);


        //act
        mockUserService.disableEnableUser("tedi", 1);
        //assert
        Mockito.verify(userRepository, Mockito.times(1)).saveAndFlush(user);
    }

    @Test
    public void getAllUsersShould_ReturnUsers_WhenUsersExists() {

        Mockito.when(userRepository.findAll())
                .thenReturn(Collections.singletonList(createUser()));

        Assert.assertEquals(1, mockUserService.getAllUsers().size());
    }

    @Test
    public void getUserByNameShould_CallRepository() {
        Pageable pageable=PageRequest.of(1,10);
        //Act
        mockUserService.getUserByFirstNameLastName(pageable, "Teodora");
        mockUserService.getUserByFirstNameLastName(pageable, "Teodora Ivanova");

        Mockito.verify(userRepository,
                times(1)).getByFirstName(pageable,"Teodora");

        Mockito.verify(userRepository,
                times(1)).getByFirstNameLastName(pageable,"Teodora","Ivanova");
    }

    @Test
    public void getUserByUserNameShould_CallRepository() {
        User user = createUser();

        Mockito.when(userRepository.findByUsername("tedi"))
                .thenReturn(Optional.of(user));

        //Act
        mockUserService.getUserByUserName("tedi");

        Mockito.verify(userRepository,
                times(1)).findByUsername("tedi");
    }

    @Test
    public void loadUserByUserNameShould_CallRepository() {
        User user = createUser();

        Mockito.when(userRepository.findByUsername("tedi"))
                .thenReturn(Optional.of(user));

        //Act
        mockUserService.loadUserByUsername("tedi");

        Mockito.verify(userRepository,
                times(1)).findByUsername("tedi");
    }

    @Test
    public void getUsersByExpertiseShould_CallRepository() {

        Pageable pageable=PageRequest.of(1,10);

        //Act
        mockUserService.getUsersByExpertise(pageable,"Marketing");

        Mockito.verify(userRepository,
                times(1)).getAllByExpertise(pageable, "Marketing");
    }



    @Test
    public void updateUserShould_CallRepository() {
        User user = createUser();
        user.setUserId(1);
        Principal principal = () -> "tedi";

        Mockito.when(userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));

        mockUserService.updateUser(user,"tedi" ,user );

        Mockito.verify(userRepository,
                times(1)).saveAndFlush(user);
    }

    @Test
    public void registerUserShould_CallRepository() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("jiji");
        userDTO.setCategory(new Category("Dentist"));
        userDTO.setConfirmPassword("123");
        userDTO.setPassword("123");
        userDTO.setEmail("jiji@abv.bg");
        User user=createUser();


        mockUserService.registerUser(user,userDTO.getCategory());

        Mockito.verify(userRepository,
                times(1)).saveAndFlush(user);

    }
    @Test
    public void isUserDuplicateShould_ThrowDuplicateExc() {
      User user1=createUser();

        Mockito.when(userRepository.findByUsernameIs("tedi"))
                .thenReturn(Optional.of(user1));

        Assertions.assertThrows(DuplicateEntityException.class, () -> {
           mockUserService.registerUser(user1,new Category("Marketing"));
        });
    }

    @Test
    public void updateExpertise_CallRepository() {
        User user = createUser();
        user.setUserId(1);
        ExpertiseProfile expertiseProfile=new ExpertiseProfile();
        expertiseProfile.setId(1);
        user.setExpertiseProfile(expertiseProfile);
        Principal principal = () -> "tedi";

        Mockito.when(userRepository.findByUsername(principal.getName())).thenReturn(Optional.of(user));
        Mockito.when(expertiseRepository.findById(user.getExpertiseProfile().getId()))
                .thenReturn(Optional.of(expertiseProfile));
        mockUserService.updateExpertise(user,expertiseProfile,principal.getName() ,user );


        Mockito.verify(expertiseRepository,
                times(1)).saveAndFlush(expertiseProfile);
    }

    @Test
    public void updateExpertise_ThrowUserNotExist() {
        User user = createUser();
        user.setUserId(1);
        ExpertiseProfile expertiseProfile=new ExpertiseProfile();
        expertiseProfile.setId(1);
        user.setExpertiseProfile(expertiseProfile);
        Principal principal = () -> "tedi";


        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockUserService.updateExpertise(user,expertiseProfile,principal.getName(),user));

    }

    @Test
    public void addToFriendList_CallRequestRepository(){
        Request request=new Request();
        request.setId(1);
        request.setApproved(true);
        User user1=createUser();
        user1.setUserId(1);
        User user2=createUser2();
        user2.setUserId(2);
        request.setSender(user1);
        request.setReceiver(user2);

        Mockito.when(userRepository.getOne(1)).thenReturn(user1);
        Mockito.when(userRepository.getOne(2)).thenReturn(user2);

        mockUserService.addToFriendList(request);

        Mockito.verify(userRepository,times(1)).saveAndFlush(user1);
    }
    @Test
    public void addToFriendList_CallRequestRepositorySender(){
        Request request=new Request();
        request.setId(1);
        request.setApproved(true);
        User user1=createUser();
        user1.setUserId(1);
        User user2=createUser2();
        user2.setUserId(2);
        request.setSender(user1);
        request.setReceiver(user2);

        Mockito.when(userRepository.getOne(1)).thenReturn(user1);
        Mockito.when(userRepository.getOne(2)).thenReturn(user2);

        mockUserService.addToFriendList(request);

        Mockito.verify(userRepository,times(1)).saveAndFlush(user2);
    }

    @Test
    public void removeFromFriendList_CallRequestRepository(){
        Request request=new Request();
        request.setId(1);
        request.setApproved(true);
        User user1=createUser();
        user1.setUserId(1);
        User user2=createUser2();
        user2.setUserId(2);
        user1.addToFriendList(user2);
        user2.addToFriendList(user1);
        request.setSender(user1);
        request.setReceiver(user2);

        Mockito.when(userRepository.getOne(1)).thenReturn(user1);
        Mockito.when(userRepository.getOne(2)).thenReturn(user2);

        mockUserService.removeFromFriendsList(request);

        Mockito.verify(userRepository,times(1)).saveAndFlush(user2);
    }

    @Test
    public void isProfileOwnerShould_ThrowInvalidExc() {
        //arrange
        User user = createUser();
        String principal="Principal";

        //assert
        Assertions.assertThrows(InvalidOperationException.class, () -> {
           mockUserService.ifNotProfileOwnerThrow(principal,user);
        });
    }

    @Test
    public void ifNotAdminShould_ThrowInvalidExc() {
        User user1=createUser();
        String principal="neli";

        Mockito.when(userRepository.findByUsername("neli"))
                .thenThrow(new EntityNotFoundException());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            mockUserService.ifNotAdminThrow(principal,user1);
        });
    }
}
