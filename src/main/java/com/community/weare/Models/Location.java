package com.community.weare.Models;

import javax.persistence.*;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    public Location() {
    }

    public Integer getId() {
        return id;
    }


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
