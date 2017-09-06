package com.kairos.service.auth;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.SmsService;
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.util.OtpGenerator;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.OTP_MESSAGE;


/**
 * Calls UserGraphRepository to perform CRUD operation on  User
 */
@Transactional
@PropertySource("classpath:email-config.properties")
@Service
public class UserService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    UserGraphRepository userGraphRepository;
    @Inject
    private UserDetailService userDetailsService;
    @Inject
    StaffGraphRepository staffGraphRepositoy;
    @Inject
    SmsService smsService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private AccessGroupService accessGroupService;


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
        userGraphRepository.delete(id);
    }


    /**
     * SafeDelete--> makes BaseEntity class property(isDelete) = true
     * Calls UserGraphRepository and Safe delete user by id given in method argument
     *
     * @param id
     */
    public void safeDeleteUserById(Long id) {
        userGraphRepository.safeDelete(id);
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
        return userGraphRepository.findByUserName(name);
    }


    /**
     * Calls UserGraphRepository and Check if User with combination of username & password exists.
     *
     * @param user
     * @return User
     */
    public Map<String, Object> authenticateUser(User user) {

        User currentUser = userDetailsService.loadUserByUsername(user.getUserName(), user.getPassword());
        if (currentUser == null) {
            return null;
        }


        int otp = OtpGenerator.generateOtp();
        user.setOtp(otp);
        userGraphRepository.save(user);
        Map<String, Object> map = new HashMap<>();
        map.put("email", currentUser.getEmail());
        //map.put("isPasswordUpdated", currentUser.isPasswordUpdated());
        map.put("otp",otp);
        return map;

    }


    public UserAuthentication authenticateUser(String username, String password) {

        User currentUser = userDetailsService.loadUserByUsername(username, password);
        if (currentUser == null) {
            return null;
        }
        generateTokenToUser(currentUser);
        return new UserAuthentication(currentUser);
       /* *//*ContactDetail contactDetail = user.getContactDetail();
        if(contactDetail == null && contactDetail.getMobilePhone() != null){
            throw new InternalError("phone number is null");
        }*//*
        int otp = OtpGenerator.generateOtp();
        user.setOtp(otp);
        userGraphRepository.save(user);
        //send otp in sms
        String message = OTP_MESSAGE + otp;
        smsService.sendSms("+919643042678", message);*/
        //return true;

    }

    public User findByAccessToken(String token) {
        return userGraphRepository.findByAccessToken(token);
    }

    public User findAndRemoveAccessToken(String accessToken) {
        return userGraphRepository.findAndRemoveAccessToken(accessToken);
    }

    public User findByUserName(String userName) {
        return userGraphRepository.findByUserName(userName);
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

    public User validateUserToken(String token) {

        return findByAccessToken(token);

    }

    public List<Map<String, Object>> getOrganizations(long userId) {

        long startTime = System.currentTimeMillis();

        Map<String, Object> orgData;
        List<Map<String, Object>> organizationList = new ArrayList<>();
        Set<Long> accessPageModuleIds;
        Set<Long> accessPageTabIds;
        List<Map<String, Object>> validAccessPage;
        boolean isDuplicateElementExist = false;
        for (Map<String, Object> parentOrganization : userGraphRepository.getOrganizations(userId)) {

            orgData = (Map<String, Object>) parentOrganization.get("result");

            Map<String, Object> organization = new HashMap<>();
            organization.put("id", orgData.get("id"));
            organization.put("name", orgData.get("name"));
/*
            accessPageModuleIds = new HashSet<>();
            validAccessPage = new ArrayList<>();
            for (Map<String, Object> accessModule : (List<Map<String, Object>>) orgData.get("accessPage")) {
                if (!accessPageModuleIds.add((long) accessModule.get("id")) && (boolean) accessModule.get("read")) {
                    for (Iterator<Map<String, Object>> iter = validAccessPage.iterator(); iter.hasNext(); ) {
                        Map<String, Object> map = iter.next();
                        if ((long) map.get("id") == (long) accessModule.get("id"))
                            iter.remove();
                    }
                }
                accessPageTabIds = new HashSet<>();
                List<Map<String, Object>> tabs = new ArrayList<>();
                for (Map<String, Object> accessTab : (List<Map<String, Object>>) accessModule.get("tabPermissions")) {
                    if (!accessPageTabIds.add((long) accessTab.get("id")) && (boolean) accessTab.get("read")) {
                        for (Iterator<Map<String, Object>> iter = tabs.iterator(); iter.hasNext(); ) {
                            Map<String, Object> map = iter.next();
                            if ((long) map.get("id") == (long) accessTab.get("id") && !(boolean) map.get("write")) {
                                iter.remove();
                                isDuplicateElementExist = true;
                            }
                        }
                    }
                    if (!isDuplicateElementExist)
                        tabs.add(accessTab);
                }
                Map<String, Object> modules = new HashMap<>();
                modules.put("id", accessModule.get("id"));
                modules.put("name", accessModule.get("name"));
                modules.put("read", accessModule.get("read"));
                modules.put("tabPermissions", tabs);
                validAccessPage.add(modules);
            }
            organization.put("modulePermissions", validAccessPage);*/
            organizationList.add(organization);
        }
        long endTime = System.currentTimeMillis();
        logger.info("Execution Time :(UserService:getOrganizations) " + (endTime - startTime) + " ms");
        return organizationList;
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
            throw new InternalError("Mobile number not found");
        }
    }

    public Map<String, Object> verifyOtp(int otp, String email) {
        logger.info("OTP::" + email);
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


        //TODO
        /*User user = userGraphRepository.findByOtp(otp);
        if (user == null) {
            return false;
        }
        user.setOtp(-1);
        userGraphRepository.save(user);*/
    }

    /**
     * Calls UserGraphRepository and Check if User with combination of username & password exists.
     *
     * @param user
     * @return User
     */
    public Map<String, Object> authenticateUserFromMobileApi(User user) {

        User currentUser = userDetailsService.loadUserByUsername(user.getUserName(), user.getPassword());
        if (currentUser == null) {
            return null;
        }
        currentUser = generateTokenToUser(currentUser);
        Organization org = staffGraphRepositoy.getStaffOrganization(currentUser.getId());
        if (org == null) {
            throw new InternalError("Couldn't find any  organization");
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
        List<User> userList = staffGraphRepositoy.getStaffByMobileNumber(mbNumber);
        if (userList != null && userList.size() == 1) {
            User currentUser = userList.get(0);
            if (currentUser == null) {
                return null;
            }
            currentUser = generateTokenToUser(currentUser);
            Organization org = staffGraphRepositoy.getStaffOrganization(currentUser.getId());
            if (org == null) {
                throw new InternalError("Couldn't find any organization");
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

    public boolean updatePassword(String email, String password) {
        User user = userGraphRepository.findByEmail(email);
        if (user == null) {
            throw new InternalError("user is null");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setPasswordUpdated(true);
        userGraphRepository.save(user);
        return true;
    }

    /**
     * @author prabjot
     * This method provides array of supported operations that user can perform.
     * @param organizationId
     * @return list of permissions.
     */
    public List<Map<String,Object>> getPermissions(long organizationId) {

        Organization organization = organizationGraphRepository.findOne(organizationId,0);
        if(organization == null){
            return Collections.emptyList();
        }

        long loggedinUserId = UserContext.getUserDetails().getId();
        List<Organization> units = organizationGraphRepository.getUnitsWithBasicInfo(organizationId);
        List<AccessPageQueryResult> mainModulePermissions = accessPageRepository.getPermissionOfMainModule(organizationId, loggedinUserId);
        List<AccessPageQueryResult> unionOfPermissionOfModule = getUnionOfPermissions(mainModulePermissions);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> unitPermissionMap;
        for (Organization unit : units) {
            List<AccessPageQueryResult> accessPageQueryResults;
             if(organization.isKairosHub()){
                accessPageQueryResults = accessPageRepository.getTabsPermissionForHubMember();
             } else {
               accessPageQueryResults = accessPageRepository.getTabPermissionForUnit(unit.getId(), loggedinUserId);
             }
            unitPermissionMap = new HashMap<>();
            unitPermissionMap.put("id", unit.getId());
            unitPermissionMap.put("permissions", getUnionOfPermissions(accessPageQueryResults));
            list.add(unitPermissionMap);
        }

        List<Map<String, Object>> permissionList = new ArrayList<>();

        unionOfPermissionOfModule.forEach(mainModule -> {
            HashMap<String, Object> response = new HashMap<>();
            response.put("id", mainModule.getId());
            response.put("name", mainModule.getName());
            response.put("read", mainModule.isRead());
            response.put("write", mainModule.isWrite());
            response.put("moduleId",mainModule.getModuleId());
            List<Map<String, Object>> unitPermissionList = new ArrayList<>();
            for (Map<String, Object> unitPermission : list) {
                Map<String, Object> unitPermissionForModule = new HashMap<>();
                unitPermissionForModule.put("unitId", unitPermission.get("id"));
                List<AccessPageQueryResult> allModulePermission = (List<AccessPageQueryResult>) unitPermission.get("permissions");
                Optional<AccessPageQueryResult> modulePermission = allModulePermission.stream().filter(permission -> permission.getId() == mainModule.getId()).findFirst();
                if(modulePermission.isPresent()){
                    unitPermissionForModule.put("tabPermissions", modulePermission.get().getChildren());
                } else {
                    unitPermissionForModule.put("tabPermissions",Collections.emptyList());
                }

                unitPermissionList.add(unitPermissionForModule);
            }
            response.put("unitPermissions", unitPermissionList);
            permissionList.add(response);
        });
        return permissionList;

    }


    /**
     * Return the union of permissions, suppose user have permission of tab A and tab B and Tab c having different roles
     * {unit manager,planner,visitator}, then final permissions will count as union of permissions of three roles.
     * @param accessPageQueryResults
     * @return List of tabs with permission parameter {tab name(String),read(boolean),write(boolean)}
     */
    private List<AccessPageQueryResult> getUnionOfPermissions(List<AccessPageQueryResult> accessPageQueryResults) {


        List<AccessPageQueryResult> filteredPages = new ArrayList<>();
        HashMap<Long, Boolean> moduleToProceed = new HashMap<>();
        HashMap<Long, Boolean> subPageToProceed = new HashMap<>();
        for (AccessPageQueryResult accessPageQueryResult : accessPageQueryResults) {

            if (moduleToProceed.get(accessPageQueryResult.getId()) == null) {
                AccessPageQueryResult module = new AccessPageQueryResult();
                if (accessPageQueryResult.isRead() && accessPageQueryResult.isWrite()) {
                    moduleToProceed.put(accessPageQueryResult.getId(), true);
                    BeanUtils.copyProperties(accessPageQueryResult, module);
                } else if (accessPageQueryResult.isRead()) {
                    Optional<AccessPageQueryResult> page = accessPageQueryResults.stream().filter(accessPage -> accessPage.getId() == accessPageQueryResult.getId() && accessPage.isWrite()).findFirst();
                    if (page.isPresent()) {
                        BeanUtils.copyProperties(page.get(), module);
                    } else {
                        BeanUtils.copyProperties(accessPageQueryResult, module);
                    }
                    moduleToProceed.put(accessPageQueryResult.getId(), true);
                } else {
                    moduleToProceed.put(accessPageQueryResult.getId(), true);
                    BeanUtils.copyProperties(accessPageQueryResult, module);
                }

                List<AccessPageQueryResult> subPageList = new ArrayList<>();
                BeanUtils.copyProperties(module.getChildren(),subPageList);

                for (AccessPageQueryResult subPage : subPageList) {
                    AccessPageQueryResult children = new AccessPageQueryResult();
                    if (subPageToProceed.get(subPage.getId()) == null && !subPageToProceed.get(subPage.getId())) {
                        if (subPage.isRead() && subPage.isWrite()) {
                            BeanUtils.copyProperties(accessPageQueryResult, children);
                        } else if (subPage.isRead()) {
                            List<AccessPageQueryResult> subPagesToSearch = getSubPagesToSearch(module, accessPageQueryResults);
                            Optional<AccessPageQueryResult> page = subPagesToSearch.stream().filter(accessPage -> accessPage.getId() == subPage.getId() && accessPage.isWrite()).findFirst();
                            if (page.isPresent()) {
                                BeanUtils.copyProperties(page.get(), children);
                            } else {
                                BeanUtils.copyProperties(subPage, children);
                            }
                        } else {
                            BeanUtils.copyProperties(subPage, children);
                        }
                    }
                    module.getChildren().add(children);
                }
                filteredPages.add(module);
            }

        }
        return filteredPages;
    }


    private List<AccessPageQueryResult> getSubPagesToSearch(AccessPageQueryResult accessPageQueryResult, List<AccessPageQueryResult> accessPageQueryResults) {

        List<AccessPageQueryResult> subPages = new ArrayList<>();
        accessPageQueryResults.stream().filter(module -> module.getId() == accessPageQueryResult.getId()).forEach(module -> {
            subPages.addAll(accessPageQueryResult.getChildren());
        });
        return subPages;
    }


}
