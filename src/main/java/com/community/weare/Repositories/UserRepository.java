package com.community.weare.Repositories;

import com.community.weare.Models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u,a FROM User as u join u.authorities as a where a.authority = ?1")
    List<User> findByAuthorities(String role);

    Optional<User> findByUsernameIs(String name);

    @Query(value = "SELECT u,e from User as u join u.expertiseProfile as e where e.category.name like :expertise")
    List<User> getAllByExpertise(@Param("expertise") String expertise);

    @Query(value = "SELECT u from User as u where u.expertiseProfile.category.name like %:expertise%")
    Slice<User> getAllByExpertise(Pageable page,@Param("expertise") String expertise);

    @Query(value = "SELECT u,p from User as u join u.personalProfile " +
            "as p where p.firstName like :firstName and p.lastName like :lastName")
    List<User> getByFirstNameLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query(value = "SELECT u,p from User as u join u.personalProfile " +
            "as p where p.firstName like :firstName")
    List<User> getByFirstName(@Param("firstName") String firstName);


    @Query(value = "SELECT u from User as u where u.personalProfile.firstName like %:firstName% and u.personalProfile.lastName like %:lastName%")
    Slice<User> getByFirstNameLastName(Pageable page,@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query(value = "SELECT u from User as u where u.personalProfile.firstName like %:firstName%")
    Slice<User> getByFirstName(Pageable page,@Param("firstName") String firstName);

    @Query(value = "SELECT u from User as u order by u.personalProfile.memberSince desc ")
    Slice<User>findAllBy(Pageable page);

    @Query(value = "SELECT u from User as u where u.personalProfile.firstName like %:firstName% and  u.personalProfile.lastName like %:lastName% " +
            "and u.expertiseProfile.category.name like %:expertise%")
    Slice<User> getUsersByFirstNameLastNameExpertise(Pageable pageable, @Param("expertise") String expertise,
                                                     @Param("firstName") String firstName, @Param("lastName")String lastName);
    @Query(value = "SELECT u from User as u where u.personalProfile.firstName like :firstName  " +
            "and u.expertiseProfile.category.name like :expertise")
    Slice<User> getUsersByFirstNameAndExpertise(Pageable pageable, @Param("expertise") String expertise,
                                                @Param("firstName") String firstName);


}
