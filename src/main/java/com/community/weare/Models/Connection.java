package com.community.weare.Models;

import javax.persistence.*;

@Entity
@Table(name = "connections_table")
public class Connection {
//Connection	userId Requester	userId

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private int connectionId;

    @ManyToOne
    @JoinColumn(name = "userRequesterId")
    private User userRequester;

    @Column(name = "userAccepterId")
    private int userAccepter;

    public Connection() {
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public User getUserRequester() {
        return userRequester;
    }

    public void setUserRequester(User userRequester) {
        this.userRequester = userRequester;
    }

    public int getUserAccepter() {
        return userAccepter;
    }

    public void setUserAccepter(int userAccepter) {
        this.userAccepter = userAccepter;
    }
}
