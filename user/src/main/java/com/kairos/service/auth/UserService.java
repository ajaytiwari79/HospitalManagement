package com.kairos.service.auth;

import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.*;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.auth.UserDetailsDTO;
import com.kairos.dto.user.staff.staff.UnitWiseStaffPermissionsDTO;
import com.kairos.dto.user.auth.GoogleCalenderTokenDTO;
import com.kairos.dto.user.user.password.FirstTimePasswordUpdateDTO;
import com.kairos.dto.user.user.password.PasswordUpdateDTO;
import com.kairos.persistence.model.access_permission.*;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.SmsService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.country.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.redis.RedisService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.OtpGenerator;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.nio.CharBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.OTP_MESSAGE;
import static com.kairos.constants.CommonConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;



/**
 * Calls UserGraphRepository to perform CRUD operation on  User
 */
@Transactional
@PropertySource("classpath:email-config.properties")
@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private UserDetailService userDetailsService;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private SmsService smsService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private MailService mailService;
    @Inject
    private TokenService tokenService;
    @Inject
    private EnvConfig config;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private RedisService redisService;
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    @Inject
    private TokenStore tokenStore;

    /**
     * Calls UserGraphRepository,
     * creates a new user as provided in method argument
     *
     * @param user
     * @return User
     */
    public User createUser(User user) {
        return userGraphRepository.save(user);
    }


    /**
     * Calls UserGraphRepository and finds User by id given in method argument
     *
     * @param id
     * @return User
     */
    public User getUserById(Long id) {
        return userGraphRepository.findOne(id);
    }


    /**
     * Calls UserGraphRepository and delete user by id given in method argument
     *
     * @param id
     */
    public void deleteUserById(Long id) {
        userGraphRepository.deleteById(id);
    }


    /**
     * Calls UserGraphRepository , find User by id as provided in method argument
     * and return updated User
     *
     * @param user
     * @return User
     */
    public User updateUser(User user) {
        User currentUser = userGraphRepository.findOne(user.getId());
        if (currentUser != null) {
            currentUser = user;
        }
        return userGraphRepository.save(currentUser);
    }


    /**
     * Calls UserGraphRepository and return the list of all User
     *
     * @return List of User- All user from db
     */
    public List<User> getAllUsers() {
        return userGraphRepository.findAll();
    }


    /**
     * Calls UserGraphRepository and find User by name provided in method argument.
     *
     * @param name
     * @return User
     */
    public User getUserByName(String name) {
        return userGraphRepository.findByUserNameIgnoreCase(name);
    }

    public User findOne(Long id) {
        return userGraphRepository.findOne(id, 0);
    }

    /**
     * Calls UserGraphRepository and Check if User with combination of username & password exists.
     *
     * @param user
     * @return User
     */
    public Map<String, Object> authenticateUser(User user) {
        User currentUser = userDetailsService.loadUserByUserName(user.getUserName(), user.getPassword());
        if (!Optional.ofNullable(currentUser).isPresent()) {
            return null;
        }
        int otp = OtpGenerator.generateOtp();
        currentUser.setOtp(otp);
        userGraphRepository.save(currentUser);
        Map<String, Object> map = new HashMap<>();
        map.put("email", currentUser.getEmail());
        map.put("userNameUpdated",currentUser.isUserNameUpdated());
        map.put("otp", otp);
        return map;

    }

    public User findByAccessToken(String token) {
        return userGraphRepository.findByAccessToken(token);
    }

    public User findByForgotPasswordToken(String token) {
        return userGraphRepository.findByForgotPasswordToken(token);
    }

    public User findAndRemoveAccessToken(String accessToken) {
        return userGraphRepository.findAndRemoveAccessToken(accessToken);
    }


    public User generateTokenToUser(User currentUser) {
        currentUser.setAccessToken(UUID.randomUUID().toString().toUpperCase());
        userGraphRepository.save(currentUser);
        return currentUser;
    }

    public boolean removeToken(String accessToken) {
        User user = findByAccessToken(accessToken);
        if (user == null) {
            return false;
        }
        user.setAccessToken(null);
        userGraphRepository.save(user);
        return true;
    }

    public boolean logout(boolean logoutFromAllMachine, HttpServletRequest request) {
        boolean logoutSuccessfull = false;
        Authentication authentication = tokenExtractor.extract(request);
        if (authentication != null) {
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication((String) authentication.getPrincipal());
            if (logoutFromAllMachine) {
                redisService.invalidateAllTokenOfUser(oAuth2Authentication.getUserAuthentication().getName());
            } else {
                redisService.removeUserTokenFromRedisByUserNameAndToken(oAuth2Authentication.getUserAuthentication().getName(),(String) authentication.getPrincipal());
            }
            tokenStore.removeAccessToken(tokenStore.getAccessToken(oAuth2Authentication));
            SecurityContextHolder.clearContext();
            logoutSuccessfull = true;
        } else {
            exceptionService.internalServerError("message.authentication.null");
        }
        return logoutSuccessfull;

    }

    public List<OrganizationWrapper> getOrganizations(long userId) {
        return userGraphRepository.getOrganizations(userId);
    }

    /**
     * @param moduleId       ,some of access page which will be treated as main module like visitator,citizen
     * @param organizationId
     * @param userId
     * @return
     * @author prabjot
     */
    public List<Map<String, Object>> getPermissionForModuleInOrganization(long moduleId, long organizationId, long userId) {
        List<Map<String, Object>> map = userGraphRepository.getPermissionForModuleInOrganization(moduleId, organizationId, userId);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Map<String, Object> result : map) {
            response.add((Map<String, Object>) result.get("result"));
        }
        return response;
    }


    public boolean sendOtp(String email) {

        User user = userGraphRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        int otp = OtpGenerator.generateOtp();
        user.setOtp(otp);
        userGraphRepository.save(user);

        //send otp in sms
        String message = OTP_MESSAGE + otp;
        ContactDetail contactDetail = user.getContactDetail();
        if (contactDetail != null && (contactDetail.getMobilePhone() != null || !contactDetail.getMobilePhone().isEmpty())) {
            smsService.sendSms(user.getContactDetail().getMobilePhone(), message);
            return true;
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_USER_MOBILENUMBER_NOTFOUND);
        }
        return false;
    }

    public Map<String, Object> verifyOtp(int otp, String email) {
        LOGGER.info("OTP::" + email);
        User currentUser = userGraphRepository.findByEmail(email);
        if (currentUser == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", currentUser.getId());
        map.put("userName", currentUser.getUserName());
        map.put("email", currentUser.getEmail());
        map.put("accessToken", currentUser.getAccessToken());
        if (currentUser.getCountryList() != null) {
            map.put("countryId", currentUser.getCountryList().get(0).getId());
        }
        map.put("age", currentUser.getAge());
        map.put("name", currentUser.getFirstName());
        map.put("organizations", getOrganizations(currentUser.getId()));

        return map;
    }

    /**
     * Calls UserGraphRepository and Check if User with combination of username & password exists.
     *
     * @param user
     * @return User
     */
    public Map<String, Object> authenticateUserFromMobileApi(User user) {

        User currentUser = userDetailsService.loadUserByEmail(user.getUserName(), user.getPassword());
        if (currentUser == null) {
            return null;
        }
        currentUser = generateTokenToUser(currentUser);
        Organization org = staffGraphRepository.getStaffOrganization(currentUser.getId());
        if (org == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANISATION_NOTFOUND);

        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", currentUser.getId());
        map.put("accessToken", currentUser.getAccessToken());
        map.put("name", currentUser.getFirstName());
        map.put("appId", org.getEstimoteAppId());
        map.put("appToken", org.getEstimoteAppToken());
        map.put("organization", org.getId());

        return map;

    }

    /**
     * Calls UserGraphRepository and Check if User with combination of username & password exists.
     *
     * @param mbNumber
     * @return User
     */
    public Map<String, Object> authenticateUserFromMobileNumber(String mbNumber) {
        List<User> userList = staffGraphRepository.getStaffByMobileNumber(mbNumber);
        if (userList != null && userList.size() == 1) {
            User currentUser = userList.get(0);
            if (currentUser == null) {
                return null;
            }
            currentUser = generateTokenToUser(currentUser);
            Organization org = staffGraphRepository.getStaffOrganization(currentUser.getId());
            if (org == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANISATION_NOTFOUND);

            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", currentUser.getId());
            map.put("accessToken", currentUser.getAccessToken());
            map.put("name", currentUser.getFirstName());
            map.put("organization", org.getId());
            map.put("appId", org.getEstimoteAppId());
            map.put("appToken", org.getEstimoteAppToken());

            return map;
        }

        return null;

    }

    public boolean updatePassword(FirstTimePasswordUpdateDTO firstTimePasswordUpdateDTO) {
        User user = userGraphRepository.findByEmail("(?i)" + firstTimePasswordUpdateDTO.getEmail());
        if (user == null) {
            LOGGER.error("User not found belongs to this email " + firstTimePasswordUpdateDTO.getEmail());
            exceptionService.dataNotFoundByIdException(MESSAGE_USER_EMAIL_NOTFOUND, firstTimePasswordUpdateDTO.getEmail());

        }
        CharSequence password = CharBuffer.wrap(firstTimePasswordUpdateDTO.getRepeatPassword());
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setPasswordUpdated(true);
        userGraphRepository.save(user);
        return true;
    }


    public Map<String, AccessPageQueryResult> prepareUnitPermissions(List<AccessPageQueryResult> accessPageQueryResults, List<Long> accessibleModules, boolean parentOrganization) {
        Map<String, AccessPageQueryResult> unitPermissionMap = new HashMap<>();
        for (AccessPageQueryResult permission : accessPageQueryResults) {
            if (unitPermissionMap.containsKey(permission.getModuleId()) && parentOrganization) {
                AccessPageQueryResult existingPermission = unitPermissionMap.get(permission.getModuleId());
                existingPermission.setRead(existingPermission.isRead() || permission.isRead());
                existingPermission.setWrite(existingPermission.isWrite() || permission.isWrite());
                existingPermission.setActive(existingPermission.isRead() || existingPermission.isWrite());
                unitPermissionMap.put(permission.getModuleId(), existingPermission);
            } else {
                if (!parentOrganization) {

                    if (accessibleModules.contains(permission.getId()) || !permission.isModule()) {
                        permission.setActive(permission.isRead() || permission.isWrite());
                        unitPermissionMap.put(permission.getModuleId(), permission);
                    } else {
                        permission.setActive(permission.isRead() || permission.isWrite());
                        unitPermissionMap.put(permission.getModuleId(), permission);
                    }
                } else {
                    permission.setActive(permission.isRead() || permission.isWrite());
                    unitPermissionMap.put(permission.getModuleId(), permission);
                }

            }
        }
        return unitPermissionMap;
    }

    public UnitWiseStaffPermissionsDTO getPermission(Long organizationId) {
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(organizationId);
        long currentUserId = UserContext.getUserDetails().getId();
        UnitWiseStaffPermissionsDTO permissionData = new UnitWiseStaffPermissionsDTO();
        permissionData.setHub(accessPageRepository.isHubMember(currentUserId));
        if (permissionData.isHub()) {
            Organization parentHub = accessPageRepository.fetchParentHub(currentUserId);
            List<AccessPageQueryResult> permissions = accessPageRepository.fetchHubUserPermissions(currentUserId, parentHub.getId());
            HashMap<String, Object> unitPermissionMap = new HashMap<>();
            for (AccessPageQueryResult permission : permissions) {
                permission.setActive(permission.isRead() || permission.isWrite());
                unitPermissionMap.put(permission.getModuleId(), permission);
            }
            permissionData.setHubPermissions(unitPermissionMap);

        } else {
            List<UserPermissionQueryResult> unitWisePermissions;
            Long countryId = UserContext.getUserDetails().getCountryId();
            List<DayType> dayTypes = dayTypeService.getCurrentApplicableDayType(countryId);
            Set<Long> dayTypeIds = dayTypes.stream().map(DayType::getId).collect(Collectors.toSet());
            boolean checkDayType = true;
            List<AccessGroup> accessGroups = accessPageRepository.fetchAccessGroupsOfStaffPermission(currentUserId);
            for (AccessGroup currentAccessGroup : accessGroups) {
                if (!currentAccessGroup.isAllowedDayTypes()) {
                    checkDayType = false;
                    break;
                }
            }
            if (checkDayType) {
                unitWisePermissions = accessPageRepository.fetchStaffPermissionsWithDayTypes(currentUserId, dayTypeIds, organizationId);
            } else {
                unitWisePermissions = accessPageRepository.fetchStaffPermissions(currentUserId, organizationId);
            }
            HashMap<Long, Object> unitPermission = new HashMap<>();

            List<AccessPageDTO> modules = accessPageRepository.getMainActiveTabs(countryId);
            List<Long> accessibleModules = modules.stream().map(AccessPageDTO::getId).collect(Collectors.toList());
            for (UserPermissionQueryResult userPermissionQueryResult : unitWisePermissions) {
                unitPermission.put(userPermissionQueryResult.getUnitId(),
                        prepareUnitPermissions(ObjectMapperUtils.copyPropertiesOfListByMapper(userPermissionQueryResult.getPermission(), AccessPageQueryResult.class), accessibleModules, userPermissionQueryResult.isParentOrganization()));
            }
            permissionData.setOrganizationPermissions(unitPermission);
        }
        updateLastSelectedOrganizationIdAndCountryId(organizationId);
         permissionData.setRole((userAccessRoleDTO.getManagement()) ? AccessGroupRole.MANAGEMENT : AccessGroupRole.STAFF);
        return permissionData;
    }


    private void updateLastSelectedOrganizationIdAndCountryId(Long organizationId) {
        User currentUser = userGraphRepository.findOne(UserContext.getUserDetails().getId());
        if (currentUser.getLastSelectedOrganizationId() != organizationId) {
            currentUser.setLastSelectedOrganizationId(organizationId);
            Organization parent = organizationService.fetchParentOrganization(organizationId);
            Long countryId = organizationGraphRepository.getCountryId(parent.getId());
            currentUser.setCountryId(countryId);
            userGraphRepository.save(currentUser);
        }
    }


    public boolean updateDateOfBirthOfUserByCPRNumber() {
        List<User> users = userGraphRepository.findAll();
        users.stream().forEach(user -> {
            user.setDateOfBirth(Optional.ofNullable(user.getCprNumber()).isPresent() ?
                    CPRUtil.fetchDateOfBirthFromCPR(user.getCprNumber()) : null);
        });
        userGraphRepository.saveAll(users);
        return true;
    }

    public boolean updateSelectedLanguageOfUser(Long userLanguageId) {
        User currentUser = userGraphRepository.findOne(UserContext.getUserDetails().getId());
        SystemLanguage systemLanguage = systemLanguageGraphRepository.findOne(userLanguageId);
        currentUser.setUserLanguage(systemLanguage);
        userGraphRepository.save(currentUser);
        return true;
    }

    public Long getUserSelectedLanguageId(Long userId) {
        return userGraphRepository.getUserSelectedLanguageId(userId);
    }

    public boolean forgotPassword(String userEmail) {
        if (userEmail.endsWith("kairos.com") || userEmail.endsWith("kairosplanning.com")) {
            LOGGER.error("Currently email ends with kairos.com or kairosplanning.com are not valid " + userEmail);
            exceptionService.dataNotFoundByIdException(MESSAGE_USER_MAIL_INVALID, userEmail);
        }
        User currentUser = userGraphRepository.findByEmail("(?i)" + userEmail);
        if (!Optional.ofNullable(currentUser).isPresent()) {
            LOGGER.error("No User found by email " + userEmail);
            currentUser = userGraphRepository.findUserByUserName("(?i)" + userEmail);
            if (!Optional.ofNullable(currentUser).isPresent()) {
                LOGGER.error("No User found by userName " + userEmail);
                exceptionService.dataNotFoundByIdException(MESSAGE_USER_USERNAME_NOTFOUND, userEmail);
            }
        }

            String token = tokenService.createForgotPasswordToken(currentUser);
            Map<String, Object> templateParam = new HashMap<>();
            templateParam.put("receiverName", EMAIL_GREETING + currentUser.getFullName());
            templateParam.put("description", AppConstants.MAIL_BODY.replace("{0}", StringUtils.capitalize(currentUser.getFirstName()))+config.getForgotPasswordApiLink()+token);
            templateParam.put("hyperLink", config.getForgotPasswordApiLink() + token);
            templateParam.put("hyperLinkName", RESET_PASSCODE);
            mailService.sendMailWithSendGrid(DEFAULT_EMAIL_TEMPLATE, templateParam, null, AppConstants.MAIL_SUBJECT, currentUser.getEmail());
            return true;
        }


    public boolean resetPassword(String token, PasswordUpdateDTO passwordUpdateDTO) {
        if (!passwordUpdateDTO.isValid()) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_USER_PASSCODE_NOTMATCH);
        }
        User user = findByForgotPasswordToken(token);
        if (!Optional.ofNullable(user).isPresent()) {
            LOGGER.error("No User found by token");
            exceptionService.dataNotFoundByIdException(MESSAGE_USER_TOKEN_NOTFOUND);
        }
        //We are validating password reset token for 2 hours.
        DateTimeInterval interval = new DateTimeInterval(DateUtils.asDate(user.getForgotTokenRequestTime()), DateUtils.asDate(user.getForgotTokenRequestTime().plusHours(2)));
        if (!interval.contains(DateUtils.asDate(DateUtils.getCurrentLocalDateTime()))) {
            LOGGER.error("Password reset token expired");
            exceptionService.dataNotFoundByIdException(MESSAGE_USER_TOKEN_EXPIRED);
        }
        CharSequence password = CharBuffer.wrap(passwordUpdateDTO.getConfirmPassword());
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setForgotPasswordToken(null);
        userGraphRepository.save(user);
        return true;
    }

    public boolean updateUserName(UserDetailsDTO userDetailsDTO) {
        User user = userGraphRepository.findByEmail("(?i)" + userDetailsDTO.getEmail());
        if (ObjectUtils.isNull(user)) {
            LOGGER.error("User not found belongs to this email " + userDetailsDTO.getEmail());
            exceptionService.dataNotFoundByIdException(MESSAGE_USER_EMAIL_NOTFOUND, userDetailsDTO.getEmail());
        } else {
            if (user.getUserName().equalsIgnoreCase(userDetailsDTO.getUserName())) {
                user.setUserNameUpdated(true);
                userGraphRepository.save(user);
                return true;
            }
            User userNameAlreadyExist = userGraphRepository.findUserByUserName("(?i)" + userDetailsDTO.getUserName());
            if (ObjectUtils.isNotNull(userNameAlreadyExist)) {
                LOGGER.error("This userName is already in use " + userDetailsDTO.getUserName());
                exceptionService.dataNotFoundByIdException("message.user.userName.already.use", userDetailsDTO.getUserName());
            }
            user.setUserNameUpdated(true);
            user.setUserName(userDetailsDTO.getUserName());
            userGraphRepository.save(user);
            return true;
        }
        return false;
    }

    public GoogleCalenderTokenDTO updateGoogleCalenderToken(GoogleCalenderTokenDTO googleCalenderTokenDTO) {
        User currentUser = userGraphRepository.findOne(UserContext.getUserDetails().getId());
        currentUser.setGoogleCalenderTokenId(googleCalenderTokenDTO.getGoogleCalenderTokenId());
        currentUser.setGoogleCalenderAccessToken(googleCalenderTokenDTO.getGoogleCalenderAccessToken());
        userGraphRepository.save(currentUser);
        return googleCalenderTokenDTO;
    }
}
