package com.community.weare.Models;

import javax.persistence.*;

@Entity
@Table(name = "feedBack")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}
