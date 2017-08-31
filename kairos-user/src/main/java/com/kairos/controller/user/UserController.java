package com.kairos.controller.user;

import com.kairos.persistence.model.user.auth.User;
import com.kairos.service.auth.UserService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;


/**
 * UserController
 * 1. Calls LocationService
 * 2. Call for  CRUD operation on User using UserService.
 **/

@RestController
@RequestMapping(API_V1 + "/user")
@Api(value = API_V1 + "/user")
public class UserController {
    @Inject
    UserService userService;

   /* @Inject
    TaskReportService taskReportService;*/
    /**
     * @return List of Users- All Users in db
     */
    @ApiOperation(value = "Get all Users")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    /**
     * find a user by id given in URL and return if found
     *
     * @param id
     * @return User
     */
    @ApiOperation(value = "Get User by Id")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    User getUserById(@PathVariable Long id) {

        return userService.getUserById(id);
    }


    /**
     * Creates a New User and return it.
     *
     * @param user
     * @return User
     */
    @ApiOperation(value = "Create a New User")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    User addUser(@Validated @RequestBody User user) {
        return userService.createUser(user);

    }


    /**
     * find a user by id given in URL
     * and updates it's properties provided in request body and return the updates User
     *
     * @param user
     * @return User
     */
    @ApiOperation(value = "Update a User by Id")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }


    /**
     * deletes a User by id given in URL
     *
     * @param id
     */
    @ApiOperation(value = "Delete User by Id")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @RequestMapping(value = "/{userId}/parent/organizations", method = RequestMethod.GET)
    @ApiOperation("get organizations of user")
    ResponseEntity<Map<String, Object>> getOrganizations(@PathVariable long userId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.getOrganizations(userId));
    }

    @RequestMapping(value = "/{userId}/organization/{orgId}/module/{moduleId}/access_permissions", method = RequestMethod.GET)
    @ApiOperation("get organizations of user")
    ResponseEntity<Map<String, Object>> getPermissionForModuleInOrganization(@PathVariable long userId, @PathVariable long orgId, @PathVariable long moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.getPermissionForModuleInOrganization(moduleId,orgId, userId));
    }

   //TODO move in task micro service
    //Not in used currently
   /* @RequestMapping(value = "/generateExcels/citizenFile",method = RequestMethod.GET)
    public void createCitizenExcelfile(@RequestParam long unitId){
        taskReportService.generateCitizenList(unitId);
    }

    @RequestMapping(value = "/generateExcels/staffFile",method = RequestMethod.GET)
    public void createStaffExcelfile(@RequestParam long unitId){
        taskReportService.generateStaffList(unitId);
    }*/


}
