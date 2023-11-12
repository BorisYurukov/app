package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Exceptions.ValidationEntityException;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Models.factories.PersonalProfileFactory;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.PersonalInfoService;
import com.community.weare.Services.users.UserService;
import com.community.weare.Models.factories.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;

import java.security.Principal;
import java.util.*;

import static com.community.weare.utils.ErrorMessages.*;

@RestController
@RequestMapping("/api/users")
public class RESTUserController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final PersonalInfoService personalInfoService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final PersonalProfileFactory personalProfileFactory;
    private final UserFactory mapperHelper;
    private final PostService postService;

    @Autowired
    public RESTUserController(UserService userService, PersonalInfoService personalInfoService, ExpertiseProfileFactory expertiseProfileFactory,
                              PersonalProfileFactory personalProfileFactory, UserFactory mapperHelper, PostService postService) {
        this.userService = userService;
        this.personalInfoService = personalInfoService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.personalProfileFactory = personalProfileFactory;
        this.mapperHelper = mapperHelper;
        this.postService = postService;
    }

    @PostMapping("/")
    public String registerUser(@RequestBody @Valid UserDTO userDTO) {

        try {
            User userDB = mapperHelper.convertDTOtoUSER(userDTO);
            userService.registerUser(userDB, userDTO.getCategory());
            User user = userService.getUserByUserName(userDTO.getUsername());
            return String.format(USER_WAS_CREATED,
                    user.getUsername(), user.getUserId());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ValidationEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @PostMapping("/auth/{id}/personal")
    public PersonalProfile upgradeUserPersonalProfile(@PathVariable(name = "id") int id,
                                                      @RequestBody @Valid PersonalProfile personalProfile,
                                                      Principal principal) {
        try {
            User user = userService.getUserById(id);
            PersonalProfile personalProfileDB = personalProfileFactory.mergePersonalProfiles(personalProfile, user.getPersonalProfile());
            return personalInfoService.upgradeProfile(principal.getName(), user, personalProfileDB);
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_AUTHORISED);
        } catch (ValidationEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ("User not found"));
        }

    }

    @PostMapping("/auth/{id}/expertise")
    public ExpertiseProfile upgradeUserExpertiseProfile(@PathVariable(name = "id") int id,
                                                        @RequestBody @Valid ExpertiseProfileDTO expertiseProfileDTO,
                                                        Principal principal) {
        try {
            User user = userService.getUserById(id);
            ExpertiseProfile expertiseProfile = expertiseProfileFactory.
                    convertDTOtoExpertiseProfile(expertiseProfileDTO);
            ExpertiseProfile expertiseProfileDb = expertiseProfileFactory.
                    mergeExpertProfile(expertiseProfile, user.getExpertiseProfile());
            return userService.updateExpertise(user, expertiseProfileDb, principal.getName(), user);
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_AUTHORISED);
        } catch (ValidationEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND);
        }

    }

    @GetMapping("/auth/{id}")
    public UserModel getUserById(@PathVariable(name = "id") int id, String principal) {
        try {
            User user = userService.getUserById(id);
            userService.ifNotProfileOrAdminOwnerThrow(principal, user);
            return mapperHelper.convertUSERtoModel(user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_AUTHORISED);
        }
    }

    @GetMapping("/{id}/posts")
    public List<Post> showProfilePosts(@PathVariable(name = "id") int id, Principal principal,
                                       @RequestBody Page page) {

        try {
            User user = userService.getUserById(id);
            List<Post> postsOfUser = new ArrayList<>();
            Slice<Post> postSlice = postService.findSliceWithPosts
                    (page.getIndex(), page.getSize(), "date", user, principal.getName());

            if (postSlice.hasContent()) {
                postsOfUser = postSlice.getContent();
                page.setSize(postSlice.nextOrLastPageable().getPageSize());
                page.setIndex(postSlice.nextOrLastPageable().getPageNumber());
                page.setNext(postSlice.hasNext());
            }
            return postsOfUser;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("")
    public List<User> getUsers(@RequestBody Page page) {
        try {
            List<User> users = new ArrayList<>();
            Slice<User> userSlice = userService.getAllUsersByCriteria(page.getIndex(),
                    page.getSize(), page.getSearchParam2(), page.getSearchParam1());

            if (userSlice.hasContent()) {
                users = userSlice.getContent();
            }

            if (users.isEmpty()) {
                throw new EntityNotFoundException
                        (NOT_SEARCH_RESULT);
            } else {
                return users;
            }

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        }
    }

}
