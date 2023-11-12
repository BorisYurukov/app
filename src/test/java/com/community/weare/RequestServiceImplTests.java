package com.community.weare;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.RequestRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.connections.RequestServiceImpl;
import com.community.weare.Services.users.PersonalInfoServiceImpl;
import com.community.weare.Services.users.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.community.weare.Factory.*;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceImplTests {

    @InjectMocks
    RequestServiceImpl requestService;

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserService userService;

    @Test
    public void getRequstsShould_Throw() {
        User sender = createUser();
        User receiver = createUser2();
        Set<User> users = new HashSet<>();
        users.add(sender);
        users.add(receiver);

        Mockito.when(requestRepository.findRequestByUsers(users)).thenReturn(new Request());

        Assert.assertThrows(EntityNotFoundException.class,
                () -> requestService.getByUsersApproved(receiver, sender));
    }

    @Test
    public void findSliceWithRequestShould_ThrowIfPageAndIndexAreZero() {
        //arrange
        int startIndex = 0;
        int pageSize = 0;
        String sortParam = "param1";
        String username = "param2";
        User user = Factory.createUser();
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> requestService.findSliceWithRequest(startIndex, pageSize, sortParam, principal.getName(),user));
    }

    @Test
    public void findSliceWithRequestShould_CallRepository() {
        //arrange
        int startIndex = 0;
        int pageSize = 4;
        String sortParam = "param1";
        String username = "param2";
        User user = Factory.createUser();
        Principal principal = () -> "tedi";

        Pageable page = PageRequest.of(startIndex, pageSize, Sort.by(sortParam).descending());
        //Act, Assert
       requestService.findSliceWithRequest(startIndex,pageSize,sortParam,principal.getName(),user);

        Mockito.verify(requestRepository,
                times(1)).findRequestsByReceiverIsAndApprovedIsFalse(page, user);
    }

}
