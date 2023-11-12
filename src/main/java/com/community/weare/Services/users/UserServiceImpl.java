package com.community.weare.Services.users;


import com.community.weare.Exceptions.*;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserModel;
import com.community.weare.Repositories.ExpertiseRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Models.factories.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Collection;

import static com.community.weare.utils.ErrorMessages.*;

@Service
public class UserServiceImpl implements UserService {
    private static final String TYPE = "USER";
    private final UserRepository userRepository;
    private final UserFactory mapperHelper;
    private final ExpertiseRepository expertiseRepository;
    private final PersonalInfoRepository personalInfoRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserFactory mapperHelper,
                           ExpertiseRepository expertiseRepository, PersonalInfoRepository personalInfoRepository) {
        this.userRepository = userRepository;
        this.mapperHelper = mapperHelper;
        this.expertiseRepository = expertiseRepository;
        this.personalInfoRepository = personalInfoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(new EntityNotFoundException());
    }

    @Transactional
    @Override
    public int registerUser(User user, Category category) {
        if (isUserDuplicate(user)) {
            throw new DuplicateEntityException(DUPLICATE);
        }
        PersonalProfile personalProfile
                = personalInfoRepository.saveAndFlush(new PersonalProfile());
        ExpertiseProfile expertiseProfile
                = expertiseRepository.saveAndFlush(new ExpertiseProfile());
        if (expertiseProfile != null) {
            expertiseProfile.setCategory(category);
        }
        user.setExpertiseProfile(expertiseProfile);
        user.setPersonalProfile(personalProfile);
        userRepository.saveAndFlush(user);
        return user.getUserId();

    }

    @Override
    public void editPictureUser(User userToUpdate, String principal, User userToCheck, MultipartFile file,
                                PersonalProfile personalProfile) throws IOException {
        userToUpdate.getPersonalProfile().setPicture(Base64.getEncoder().encodeToString(file.getBytes()));
        userToUpdate.getPersonalProfile().setPicturePrivacy(personalProfile.isPicturePrivacy());
        updateUser(userToUpdate,principal,userToCheck);
    }

    @Override
    public User getUserByUserName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(new EntityNotFoundException("User", "username", username));
        return user;
    }

    @Override
    public Slice<User> getUserByFirstNameLastName(Pageable pageable, String name) {
        String param[] = name.split(" ");
        if (param.length == 2) {
            return userRepository.getByFirstNameLastName(pageable, param[0], param[1]);
        } else {
            return userRepository.getByFirstName(pageable, param[0]);
        }
    }

    @Override
    public Slice<User> getUsersByExpertise(Pageable pageable, String expertise) {
        return userRepository.getAllByExpertise(pageable, expertise);
    }

    @Override
    public Slice<User> getUserByFirstNameLastNameExpertise(Pageable pageable, String expertise, String name) {
        String param[] = name.split(" ");
        if (param.length > 1) {
            return userRepository.
                    getUsersByFirstNameLastNameExpertise
                            (pageable, expertise, param[0], param[1]);
        } else {
            return userRepository.
                    getUsersByFirstNameAndExpertise(pageable, expertise, param[0]);
        }
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(new EntityNotFoundException(NOT_FOUND));
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Slice<User> findSliceWithUsers(int index, int size, String param) {
        if (size == 0 && index == 0) {
            throw new EntityNotFoundException(NOT_FOUND);
        }
        Pageable page = PageRequest.of(index, size, Sort.by(param).descending());
        return userRepository.findAllBy(page);
    }

    @Override
    public Slice<User> findSliceWithLatestUsers(Pageable pageable) {
        return userRepository.findAllBy(pageable);
    }

    @Override
    public User disableEnableUser(String principal, int userId) {
        User user = userRepository.getOne(userId);
        ifNotAdminThrow(principal, user);
        if (!user.isEnabled()) {
            user.setEnabled(1);
        } else {
            user.setEnabled(0);
        }
        return userRepository.saveAndFlush(user);
    }

    @Override
    public boolean isUserDuplicate(User user) {
        return userRepository.findByUsernameIs(user.getUsername()).isPresent();
    }

    @Transactional
    @Override
    public User updateUser(User userToUpdate, String principal, User userToCheck) {
        ifNotProfileOrAdminOwnerThrow(principal, userToCheck);
        return userRepository.saveAndFlush(userToUpdate);
    }

    @Transactional
    @Override
    public ExpertiseProfile updateExpertise(User user,
                                            ExpertiseProfile expertiseProfileMerged,
                                            String principal,
                                            User userToCheck) {
        ifNotProfileOrAdminOwnerThrow(principal, userToCheck);
        ExpertiseProfile profileDB = expertiseRepository.
                findById(user.getExpertiseProfile().getId()).orElseThrow(new EntityNotFoundException());
        expertiseProfileMerged.setId(profileDB.getId());
        return expertiseRepository.saveAndFlush(expertiseProfileMerged);
    }

    @Override
    public UserModel getUserModelById(int id) {
        User user = userRepository.getOne(id);
        UserModel model = mapperHelper.convertUSERtoModel(user);
        return model;
    }

    @Transactional
    @Override
    public void addToFriendList(Request approvedRequest) {
        if (approvedRequest.isApproved()) {
            User receiver = userRepository.getOne(approvedRequest.getReceiver().getUserId());
            receiver.addToFriendList(approvedRequest.getSender());
            userRepository.saveAndFlush(receiver);
            User sender = userRepository.getOne(approvedRequest.getSender().getUserId());
            sender.addToFriendList(approvedRequest.getReceiver());
            userRepository.saveAndFlush(sender);
        }
    }

    @Transactional
    @Override
    public void removeFromFriendsList(Request request) {
        User receiver = userRepository.getOne(request.getReceiver().getUserId());
        User sender = userRepository.getOne(request.getSender().getUserId());
        receiver.removeFromFriendList(sender);
        sender.removeFromFriendList(receiver);
        userRepository.saveAndFlush(receiver);
        userRepository.saveAndFlush(sender);
    }

    @Override
    public void ifNotProfileOwnerThrow(String principal, User user) {
        if (!principal.equals(user.getUsername())) {
            throw new InvalidOperationException(NOT_AUTHORISED);
        }
    }

    @Override
    public void ifNotProfileOrAdminOwnerThrow(String principal, User user) {
        User admin = userRepository.findByUsername(principal)
                .orElseThrow(new EntityNotFoundException(NOT_FOUND));
        if (!(admin.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
                user.getUsername().equals(principal))) {
            throw new InvalidOperationException(NOT_AUTHORISED);
        }
    }

    @Override
    public void ifNotAdminThrow(String name, User user) {
        User admin = userRepository.findByUsername(name)
                .orElseThrow(new EntityNotFoundException(NOT_FOUND));
        if (admin.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new InvalidOperationException(NOT_AUTHORISED);
        }
    }

    @Override
    public boolean isOwner(String principal, User user) {
        return principal.equals(user.getUsername());
    }

    @Override
    public boolean isAdmin(Principal principal) {
        User admin = userRepository.findByUsername(principal
                .getName()).orElseThrow(new EntityNotFoundException(NOT_FOUND));
        return admin.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public Slice<User> getAllUsersByCriteria(int index, int size, String name, String expertise) {

        if (index==0&&size==0){
            throw new EntityNotFoundException(RESULT_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(index, size, Sort.by("username"));

        if (!name.isEmpty() && expertise.isEmpty()) {
            return getUserByFirstNameLastName(pageable, name);

        } else if (name.isEmpty() && !expertise.isEmpty()) {
            return getUsersByExpertise(pageable, expertise);

        } else if (!name.isEmpty() && !expertise.isEmpty()) {
            return getUserByFirstNameLastNameExpertise(pageable, name,expertise);

        } else {
            return findSliceWithLatestUsers(pageable);
        }
    }
}

