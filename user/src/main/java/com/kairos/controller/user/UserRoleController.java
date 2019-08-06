package com.kairos.controller.user;

import com.kairos.persistence.model.auth.*;
import com.kairos.service.auth.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstants.API_V1;


/**
 * Created by oodles on 30/8/16.
 */


/**
 * UserRole Controller - Store Reference to User & his role
 */
@RestController
@RequestMapping(value = API_V1 +"/userrole")
@Api(value = API_V1 +"/userrole")
public class UserRoleController {

    @Inject
    UserRoleServiceUser userRoleService;

    @Inject
    UserService userService;

    @Inject
    RoleServiceUser roleService;


    /**
     * Find a Userrole by id given in URL
     * @param id
     * @param role
     * @return UserRole
     */
    @ApiOperation(value = "Get User by Id")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    UserRole getUserById(@PathVariable Long id, @RequestBody Role role){
        UserRole userRole = new UserRole((User) userService.findOne(id),roleService.findRoleByAuthority("ROLE_EMPLOYEE"));
        return (UserRole) userRoleService.save(userRole);
    }


}
