package com.community.weare.Services.connections;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import org.springframework.data.domain.Slice;

import java.util.Collection;

public interface RequestService {

    Request createRequest(User sender, User receiver);

    Request approveRequest(int id, User user, String principal);

    Collection<Request> getAllRequestsForUser(User receiver, String principal);

    Request getByUsers(User receiver, User sender);

    Request getByUsersApproved(User receiver, User sender);

    void deleteRequest(Request request);

    Slice<Request> findSliceWithRequest(int index, int size, String date, String principal, User receiver);
}
