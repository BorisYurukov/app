package com.community.weare.Repositories;

import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

Set<Role>findByAuthority(String authority);

List<Role> findRoleByUsersIs(User user);


List<Role> findByUsersIs(User user);
}
