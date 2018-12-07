package com.kairos.controller.auth;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.user.user.password.PasswordUpdateDTO;
import com.kairos.persistence.model.auth.OrganizationSelectionDTO;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.service.auth.UserService;
import com.kairos.service.country.CountryService;
import com.kairos.dto.user.user.password.FirstTimePasswordUpdateDTO;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.utils.user_context.UserContext;
import com.twilio.sdk.TwilioRestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.PARENT_ORGANIZATION_URL;


/**
 * Controller to authenticate User
 */
@RestController
@RequestMapping(API_V1)
@Api(value = API_V1)
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private UserService userService;

    @Inject
    CountryService countryService;

    @Inject
    com.kairos.service.organization.OrganizationService organizationService;

    /**
     * Calls userService and Check if user exists
     * If user exists , send access token to user user.
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "Authenticate User")
    ResponseEntity<Map<String, Object>> checkUser(@RequestBody User user) {

        logger.info("user info is {}",user);

        Map<String,Object> response = userService.authenticateUser(user);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, false, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);

    }

    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    @ApiOperation(value = "Authenticate User")
    ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam("email") String email) {
        logger.info("user info is {}",email);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,userService.forgotPassword(email));

    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    @ApiOperation(value = "Authenticate User")
    ResponseEntity<Map<String, Object>> resetPassword(@RequestParam("token") String token,@RequestBody PasswordUpdateDTO passwordUpdateDTO ) {
        logger.info("user info is {}",token);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,userService.resetPassword(token,passwordUpdateDTO));

    }

    @RequestMapping(value = "/login/mobile", method = RequestMethod.POST)
    @ApiOperation(value = "Authenticate User")
    ResponseEntity<Map<String, Object>> checkUserMobileUser(@RequestBody User user) {
        logger.info("Data:\n Username:" + user.getUserName() + "\n Password:" + user.getPassword());
        Map<String, Object> responseData = userService.authenticateUserFromMobileApi(user);
        if (responseData == null) {
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, false, null);

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responseData);
    }

    @RequestMapping(value = "/login/mobile_number", method = RequestMethod.POST)
    @ApiOperation(value = "Authenticate User")
    ResponseEntity<Map<String, Object>> checkUserByMobileNumber(@Param("mobileNumber") String mobileNumber) {
        Map<String, Object> responseData = userService.authenticateUserFromMobileNumber(mobileNumber);
        if (responseData == null) {
            Organization organization = organizationService.getByPublicPhoneNumber(mobileNumber);
            if (organization != null) {
                responseData = new HashMap<String, Object>();
                responseData.put("isPublic", true);
                responseData.put("organization", organization.getId());
            } else {
                return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, false, null);
            }
        } else {
            responseData.put("isPublic", false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responseData);
    }

    @RequestMapping(value = "/login/otp", method = RequestMethod.POST)
    @ApiOperation(value = "send otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String,Object> loginDetails) throws TwilioRestException {
        String email = (String) loginDetails.get("email");

        if (userService.sendOtp(email)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, true);
    }

    @RequestMapping(value = "/login/verify/otp", method = RequestMethod.POST)
    @ApiOperation(value = "verify otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, Object> otpDetail) {

        String otp = (String) otpDetail.get("verificationCode");

        int verificationCode = 0;
        if(otp != null && !otp.isEmpty()){
            verificationCode = Integer.parseInt(otp);
        }
        String email = (String) otpDetail.get("email");
        Map<String,Object> response =  userService.verifyOtp(verificationCode,email);

        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, false, Collections.emptyMap());
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    /**
     * Calls tokenService to delete access token and logout user.
     * /logout is mapped in logoutRequestMatcher as logout URL
     *
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ApiOperation(value = "logout User from System")
    ResponseEntity<Map<String, Object>> logoutUser(@RequestHeader(value = "authtoken") String accessToken) {
        logger.info("Removing token  " + accessToken + "\n");
        if (userService.removeToken(accessToken)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED, false, false);
    }

    @RequestMapping(value = "/country_code_list_for_login", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getCountryNameAndCodeList() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getCountryNameAndCodeList());
    }

    /* @PreAuthorize("@customPermissionEvaluator.isAuthorized(#organizationId,#tabId,#httpServletRequest)")*/
    @RequestMapping(value = "/user/password", method = RequestMethod.PUT)
    ResponseEntity<Map<String,Object>> updatePassword(@Valid @RequestBody FirstTimePasswordUpdateDTO firstTimePasswordUpdateDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.updatePassword(firstTimePasswordUpdateDTO));
    }

    @RequestMapping(value = "/user/organizations", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getCurrentUserOrganizationList() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.getLoggedInUserOrganizations());
    }

    @RequestMapping(value = PARENT_ORGANIZATION_URL+ "/user/permissions", method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getPermissions(@PathVariable long organizationId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.getPermission(organizationId));
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.getPermissions(organizationId));
    }

    @PreAuthorize("hasPermission()")
    @RequestMapping(value = { "/user/{unitId}" }, produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        userInfo.put("credentials", UserContext.getUserDetails().getId());
        userInfo.put("clientId", user.getOAuth2Request().getClientId());
        userInfo.put("user12", user.getPrincipal());

        return userInfo;
    }

    @RequestMapping(value = "/user/selected_organizations", method = RequestMethod.PUT)
    public ResponseEntity<Map<String,Object>> updateLastOrganizationSelectedByUser(@Valid @RequestBody OrganizationSelectionDTO organizationSelectionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, userService.updateLastSelectedChildAndParentId(organizationSelectionDTO));
    }
}
