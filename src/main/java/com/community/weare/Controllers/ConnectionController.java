package com.community.weare.Controllers;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Services.connections.RequestService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.security.Principal;
import java.util.*;

import static com.community.weare.utils.ErrorMessages.ERROR_NOT_FOUND_MESSAGE_FORMAT;
import static com.community.weare.utils.ErrorMessages.NOT_AUTHORISED;

@Controller
@RequestMapping("/auth/connection")
public class ConnectionController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private RequestService requestService;

    @Autowired
    public ConnectionController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @Transactional
    @PostMapping("/request")
    public String sendRequest(@ModelAttribute("userRequest") UserDtoRequest userToConnect, Principal principal, Model model) {

        try {
            User userReceiver = userService.getUserById(userToConnect.getId());
            model.addAttribute("user", userReceiver);
            User userSender = userService.getUserByUserName(principal.getName());

            if (!userReceiver.isFriend(userSender.getUsername()) &&
                    requestService.getByUsers(userReceiver, userSender) == null) {
                requestService.createRequest(userSender, userReceiver);

            } else if (userReceiver.isFriend(userSender.getUsername())) {
                Request request = requestService.getByUsersApproved(userReceiver, userSender);
                userService.removeFromFriendsList(request);
                requestService.deleteRequest(request);
            }
        } catch (EntityNotFoundException e) {
            model.addAttribute("error",
                    String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, "Request"));
        }
        return "redirect:/auth/users/" + userToConnect.getId() + "/profile";
    }

    @GetMapping("/{id}/request")
    public String getUserRequests(@PathVariable(name = "id") int id, Model model, Principal principal,
                                  @ModelAttribute(name = "pageRequest") Page page) {

        try {
            User user = userService.getUserById(id);
            List<Request> requestList = new ArrayList<>();
            Slice<Request> requestSlice = requestService.findSliceWithRequest
                    (page.getIndex(), page.getSize(), "timeStamp", principal.getName(), user);

            if (requestSlice.hasContent()) {
                requestList = requestSlice.getContent();
                page.setSize(requestSlice.nextOrLastPageable().getPageSize());
                page.setIndex(requestSlice.nextOrLastPageable().getPageNumber());
                model.addAttribute("requests", requestList);
            }
            model.addAttribute("user", user);
            model.addAttribute("requestN", new Request());
            model.addAttribute("hasNext", requestSlice.hasNext());

            if (requestList.isEmpty()) {
                model.addAttribute("error", "There are no requests");
            }

        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "request-list";
    }

    @Transactional
    @PostMapping("/request/approve")
    public String approveRequests(@ModelAttribute(name = "requestN") Request request,
                                  Principal principal, Model model) {


        try {
            if (request.isApproved()) {
                User receiver = userService.getUserById(request.getReceiver().getUserId());
                Request approvedRequest = requestService.approveRequest(request.getId(), receiver, principal.getName());
                userService.addToFriendList(approvedRequest);
                model.addAttribute("user", receiver);
                model.addAttribute("requests", requestService.getAllRequestsForUser(approvedRequest.getReceiver(), principal.getName()));
            }
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        }
        return "redirect:/auth/connection/ " + request.getReceiver().getUserId() + "/request?index=0&size=4";
    }

}
