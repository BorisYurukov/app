package com.community.weare.Repositories;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<Request,Integer> {

    Collection<Request>findRequestsByReceiverIsAndSeenFalse(User receiver);

    Collection<Request>findRequestsByReceiverIsAndSeenTrue(User receiver);

    @Query(value = "SELECT r FROM Request r  where r.receiver.userId  = ?1 and r.sender.userId= ?2 and r.approved = false")
    Request findRequestsByUsersUnApproved(int  receiver, int  sender);

    Slice<Request>findRequestsByReceiverIsAndApprovedIsFalse(Pageable pageable, User receiver);

    Collection<Request>findRequestsByReceiverIsAndApprovedIsFalse(User user);

    @Query(value = "SELECT r FROM Request r  where r.receiver IN :users and r.sender in :users")
    Request findRequestByUsers(@Param("users") Set<User> users);
}
