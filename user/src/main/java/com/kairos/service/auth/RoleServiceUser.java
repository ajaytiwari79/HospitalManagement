package com.kairos.service.auth;

import com.kairos.persistence.model.auth.Role;
import com.kairos.persistence.repository.user.auth.RoleGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 *  Calls RoleGraphRepository to perform CRUD operation on  Role
 */
@Transactional
@Service
public class RoleServiceUser{

    @Inject
    RoleGraphRepository roleGraphRepository;



    /**
     * Call RoleGraphRepository to create a Role based on Arguments and return a new created Role
     * @param role
     * @return Role
     */
    public Role createRole(Role role){
        return roleGraphRepository.save(role);
    }




    /**
     *
     * @param role
     * @return
     */
    public Role updateRole(Role role){
        return roleGraphRepository.save(role);
    }




    /**
     * Call RoleGraphRepository for List of All Roles
     * @return List of Roles
     */
    public List<Role> getAllRoles(){
        return roleGraphRepository.findAll();
    }




    /**
     *
     * @param id
     */
    public void deleteRole(Long id){
        roleGraphRepository.deleteById(id);
    }




    /**
     *
     * @param id
     * @return
     */
    public Role getRolebyId(Long id) {
        return roleGraphRepository.findOne(id);
    }







    /**
     * Call RoleGraphRepository and find Role of a user based on Username
     * @param name
     * @return Role
     */
    public Role findRoleByAuthority(String name){
        return roleGraphRepository.findByAuthority(name);
    }




}
