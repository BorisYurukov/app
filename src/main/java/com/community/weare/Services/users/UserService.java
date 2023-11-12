package com.community.weare.Services.users;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;


public interface UserService extends UserDetailsService {

    int registerUser(User user, Category category);

    User getUserByUserName(String username);

    User getUserById(int id);

    Collection<User> getAllUsers();

    User disableEnableUser(String principal, int userId);

    boolean isUserDuplicate(User user);



    User updateUser(User user, String principal, User userToCheck);

    UserModel getUserModelById(int id);

    void addToFriendList(Request request);

    ExpertiseProfile updateExpertise(User user, ExpertiseProfile expertiseProfileMerged, String principal, User userToCheck);

    void ifNotProfileOwnerThrow(String principal, User user);

    void ifNotProfileOrAdminOwnerThrow(String principal, User user);

    void ifNotAdminThrow(String name, User user);

    boolean isOwner(String principal, User user);

    boolean isAdmin(Principal principal);

    Slice<User> getAllUsersByCriteria(int index, int size, String name, String expertise);

    void removeFromFriendsList(Request request);

    Slice<User> getUserByFirstNameLastName(Pageable pageable, String param);

    Slice<User> getUsersByExpertise(Pageable pageable, String expertise);

    Slice<User> getUserByFirstNameLastNameExpertise(Pageable pageable, String expertise, String name);

    Slice<User> findSliceWithUsers(int index, int size, String username);

    Slice<User> findSliceWithLatestUsers(Pageable pageable);

    void editPictureUser(User userToCheck, String name, User userToCheck1,
                         MultipartFile file, PersonalProfile personalProfile) throws IOException;
}
