package com.kairos.service.auth;
import com.kairos.persistence.model.auth.UserRole;
import com.kairos.persistence.repository.user.auth.UserRoleGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;


/**
 *  Calls UserRoleGraphRepository to perform CRUD operation on  UserRole.
 */
@Transactional
@Service
public class UserRoleServiceUser {

    @Inject
    UserRoleGraphRepository userRoleGraphRepository;

    /**
     * Calls UserRoleGraphRepository,
     * creates a new UserRole as provided in method arguments and
     * return newly created UserRole.
     * @param userRole
     * @return User
     */
    public UserRole createUserRole(UserRole userRole){
        return  userRoleGraphRepository.save(userRole);
    }

    public UserRole save(UserRole userRole){
        return  userRoleGraphRepository.save(userRole);
    }



    /**
     * Calls UserRoleGraphRepository ,and delete a UserRole by id given in method argument.
     * @param id
     */
    public void deleteUserRoleById(Long id){
        userRoleGraphRepository.deleteById(id);
    }




    /**
     * Calls UserRoleGraphRepository , and find a UserRole by id given in method argument.
     * @param id
     * @return UserRole
     */
    public UserRole getUserRoleByUserId(Long id){
        return userRoleGraphRepository.getUserRoleByUserId(id);
    }












    /**
     * Calls UserRoleGraphRepository, and return the list of all UserRole
     * @return List of UserRole
     */
    public List<UserRole> getAllUserRole(){
        return  userRoleGraphRepository.findAll();
    }



    /** Not being used currently.
     * Calls UserRoleGraphRepository and Create a Unique constraint for Privileges.
     */
    public void createConstraint(){
        userRoleGraphRepository.createConstraintOnPrivilege();
    }


}
