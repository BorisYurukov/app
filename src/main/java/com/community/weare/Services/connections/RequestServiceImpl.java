package com.community.weare.Services.connections;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Repositories.RequestRepository;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.community.weare.utils.ErrorMessages.REQUEST_NOT_FOUND;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Request createRequest(User sender, User receiver) {
        Request request = new Request();
        request.setSender(sender);
        request.setReceiver(receiver);
        return requestRepository.saveAndFlush(request);
    }

    @Transactional
    @Override
    public Request approveRequest(int id, User user, String principal) {
        userService.ifNotProfileOwnerThrow(principal, user);
        Request requestApproved = requestRepository.getOne(id);
        requestApproved.setApproved(true);
        return requestRepository.saveAndFlush(requestApproved);

    }

    @Transactional
    @Override
    public void deleteRequest(Request request) {
        requestRepository.delete(request);
    }


    @Override
    public Collection<Request> getAllRequestsForUser(User receiver, String principal) {
        userService.ifNotProfileOwnerThrow(principal, receiver);
        return requestRepository.findRequestsByReceiverIsAndApprovedIsFalse(receiver);
    }

    @Override
    public Slice<Request> findSliceWithRequest(int index, int size, String param, String principal, User receiver) {
        userService.ifNotProfileOwnerThrow(principal, receiver);
        if (size == 0 && index == 0) {
            throw new EntityNotFoundException();
        }
        Pageable page = PageRequest.of(index, size, Sort.by(param).descending());
        return requestRepository.findRequestsByReceiverIsAndApprovedIsFalse(page, receiver);
    }

    @Override
    public Request getByUsers(User receiver, User sender) {
        return requestRepository.
                findRequestsByUsersUnApproved(receiver.getUserId(), sender.getUserId());
    }

    @Override
    public Request getByUsersApproved(User receiver, User sender) {
        Set<User> users = new HashSet<>();
        users.add(receiver);
        users.add(sender);
        if (requestRepository.findRequestByUsers(users).isApproved()) {
            return requestRepository.findRequestByUsers(users);
        }
        throw new EntityNotFoundException(REQUEST_NOT_FOUND);
    }
}
