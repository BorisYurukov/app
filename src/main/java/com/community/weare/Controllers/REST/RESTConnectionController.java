package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Services.connections.RequestService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.*;

import static com.community.weare.utils.ErrorMessages.NOT_AUTHORISED;

@RestController
@RequestMapping("/api/auth")
public class RESTConnectionController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private RequestService requestService;

    @Autowired
    public RESTConnectionController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @Transactional
    @PostMapping("/request")
    public String sendRequest(@RequestBody UserDtoRequest userToConnect, Principal principal, Model model) {

        try {
            User userReceiver = userService.getUserById(userToConnect.getId());
            User userSender = userService.getUserByUserName(principal.getName());

            if (!userReceiver.isFriend(userSender.getUsername()) &&
                    requestService.getByUsers(userReceiver, userSender) == null) {

                requestService.createRequest(userSender, userReceiver);
                return String.format("%s send friend request to %s",
                        userSender.getUsername(), userReceiver.getUsername());

            } else if (userReceiver.isFriend(userSender.getUsername())) {
                Request request = requestService.getByUsersApproved(userReceiver, userSender);
                userService.removeFromFriendsList(request);
                requestService.deleteRequest(request);
                return String.format("%s disconnected from %s",
                        userSender.getUsername(), userReceiver.getUsername());
            }
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return String.format("%s can't connect to %s",
                principal.getName(), userToConnect.getUsername());
    }

    @GetMapping("/users/{id}/request/")
    public Collection<Request> getUserRequests(@PathVariable(name = "id") int id, Principal principal) {

        try {
            User user = userService.getUserById(id);
            return requestService.getAllRequestsForUser(user, principal.getName());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @Transactional
    @PostMapping("/users/{id}/request/approve")
    public String approveRequests(@PathVariable(name = "id") int userId,
                                  @RequestParam(name = "requestId") int requestId,
                                  Principal principal) {
        try {
            User receiver = userService.getUserById(userId);
            Request approvedRequest =
                    requestService.approveRequest(requestId, receiver, principal.getName());
            userService.addToFriendList(approvedRequest);
            return String.format("%s approved request of %s",
                    approvedRequest.getReceiver().getUsername(), approvedRequest.getSender().getUsername());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_AUTHORISED);
        }
    }

}
