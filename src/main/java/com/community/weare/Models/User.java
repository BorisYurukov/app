package com.community.weare.Models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Size(min = 2, message = "The username must have at least 2 symbols!")
    @Column(name = "username", unique = true, nullable = false, length = 30)
    private String username;

    @JsonBackReference
    @Column(name = "password", nullable = false)
    private String password;

    @JsonBackReference
    @Column(name = "email", nullable = false)
    private String email;


    @ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinTable(
            name = "authorities",
            joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"),
            inverseJoinColumns = @JoinColumn(name = "authority", referencedColumnName = "authority"))
    private Set<Role> authorities = new HashSet<>();

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "personal_profile_id", referencedColumnName = "id")
    private PersonalProfile personalProfile;

    @OneToOne
    @JoinColumn(name = "expertise_profile_id", referencedColumnName = "id")
    private ExpertiseProfile expertiseProfile;

    
    @Column(name = "enabled")
    private int enabled;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "connection_users",
            joinColumns = @JoinColumn(name = "user_a"),
            inverseJoinColumns = @JoinColumn(name = "user_b"))

    private Set<User> friendList = new HashSet<>();

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    @Transient
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Transient
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Transient
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Transient
    @Override
    public boolean isEnabled() {
        if (enabled != 1) {
            return false;
        }
        return true;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PersonalProfile getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(PersonalProfile personalProfile) {
        this.personalProfile = personalProfile;
    }

    public ExpertiseProfile getExpertiseProfile() {
        return expertiseProfile;
    }

    public void setExpertiseProfile(ExpertiseProfile expertiseProfile) {
        this.expertiseProfile = expertiseProfile;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public Set<User> getFriendList() {
        return friendList;
    }

    public void addToFriendList(User friend) {
        friendList.add(friend);
    }

    public void removeFromFriendList(User friend) {
        friendList.remove(friend);
    }

    public boolean isFriend(String username) {
        return friendList.stream().anyMatch(u -> u.getUsername().equals(username));
    }
}
