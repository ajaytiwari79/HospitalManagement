package com.kairos.service.auth;

import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.user.staff.staff.UnitWiseStaffPermissionsDTO;
import com.kairos.dto.user.user.password.FirstTimePasswordUpdateDTO;
import com.kairos.dto.user.user.password.PasswordUpdateDTO;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.access_permission.UnitModuleAccess;
import com.kairos.persistence.model.access_permission.UserPermissionQueryResult;
import com.kairos.persistence.model.auth.*;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.UnitTypeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.SmsService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.country.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.OtpGenerator;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.nio.CharBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.OTP_MESSAGE;


/**
 * Calls UserGraphRepository to perform CRUD operation on  User
 */
@Transactional
@PropertySource("classpath:email-config.properties")
@Service
public class UserService {
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
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private UnitTypeGraphRepository unitTypeGraphRepository;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private MailService mailService;
    @Inject
    private TokenService tokenService;
    @Inject
    EnvConfig config;
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
     *
     */
    public Map<String, Object> authenticateUser(User user) {

        User currentUser = userDetailsService.loadUserByEmail(user.getUserName(), user.getPassword());
        if (currentUser == null) {
            return null;
        }
        int otp = OtpGenerator.generateOtp();
        currentUser.setOtp(otp);
        userGraphRepository.save(currentUser);
        Map<String, Object> map = new HashMap<>();
        map.put("email", currentUser.getEmail());
        //map.put("isPasswordUpdated", currentUser.isPasswordUpdated());
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
            exceptionService.dataNotFoundByIdException("message.user.mobileNumber.notFound");
        }
        return false;
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

        User currentUser = userDetailsService.loadUserByEmail(user.getUserName(), user.getPassword());
        if (currentUser == null) {
            return null;
        }
        currentUser = generateTokenToUser(currentUser);
        Organization org = staffGraphRepositoy.getStaffOrganization(currentUser.getId());
        if (org == null) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

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
                exceptionService.dataNotFoundByIdException("message.organisation.notFound");

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
        User user = userGraphRepository.findByEmail("(?i)"+firstTimePasswordUpdateDTO.getEmail());
        if (user == null) {
            logger.error("User not found belongs to this email " + firstTimePasswordUpdateDTO.getEmail());
            exceptionService.dataNotFoundByIdException("message.user.email.notFound", firstTimePasswordUpdateDTO.getEmail());

        }
        CharSequence password = CharBuffer.wrap(firstTimePasswordUpdateDTO.getRepeatPassword());
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setPasswordUpdated(true);
        userGraphRepository.save(user);
        return true;
    }

    public UserOrganizationsDTO getLoggedInUserOrganizations() {
        User currentUser = userGraphRepository.findOne(UserContext.getUserDetails().getId());
        Long userLanguageId = Optional.ofNullable(currentUser.getUserLanguage()).isPresent() ? currentUser.getUserLanguage().getId() : null;
        UserOrganizationsDTO userOrganizationsDTO = new UserOrganizationsDTO(userGraphRepository.getOrganizations(UserContext.getUserDetails().getId()),
                currentUser.getLastSelectedChildOrgId(), currentUser.getLastSelectedParentOrgId(), userLanguageId);
        return userOrganizationsDTO;
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
                        permission.setActive(false);
                        permission.setRead(false);
                        permission.setWrite(false);
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
            Long countryId = organizationGraphRepository.getCountryId(organizationId);
            List<DayType> dayTypes=dayTypeService.getDayTypeByDate(countryId,DateUtils.getDate());
            Set<Long> dayTypeIds=dayTypes.stream().map(DayType::getId).collect(Collectors.toSet());
            boolean checkDayType=true;
            List<AccessGroup> accessGroups=accessPageRepository.fetchAccessGroupsOfStaffPermission(currentUserId);
            for (AccessGroup currentAccessGroup:accessGroups){
                if(!currentAccessGroup.isAllowedDayTypes()){
                    checkDayType=false;
                    break;
                }
            }
            if(checkDayType){
                unitWisePermissions = accessPageRepository.fetchStaffPermissionsWithDayTypes(currentUserId,dayTypeIds);
            } else {
                unitWisePermissions = accessPageRepository.fetchStaffPermissions(currentUserId);
            }
            HashMap<Long, Object> unitPermission = new HashMap<>();

            List<Long> unitIds = unitWisePermissions.stream()
                    .filter(userPermissionQueryResult -> !userPermissionQueryResult.isParentOrganization())
                    .map(u -> u.getUnitId())
                    .collect(Collectors.toList());
            List<UnitModuleAccess> unitModuleAccesses = unitTypeGraphRepository.getAccessibleModulesByUnits(unitIds);
            for (UserPermissionQueryResult userPermissionQueryResult : unitWisePermissions) {
                List<Long> accessibleModules = unitModuleAccesses.stream()
                        .filter(unitModuleAccess -> unitModuleAccess.getUnitId().equals(userPermissionQueryResult.getUnitId()))
                        .findAny().map(u -> u.getAccessibleModules())
                        .orElse(new ArrayList<>());
                unitPermission.put(userPermissionQueryResult.getUnitId(),
                        prepareUnitPermissions(ObjectMapperUtils.copyPropertiesOfListByMapper(userPermissionQueryResult.getPermission(), AccessPageQueryResult.class), accessibleModules, userPermissionQueryResult.isParentOrganization()));
            }


            permissionData.setOrganizationPermissions(unitPermission);
        }
        return permissionData;
    }

    /**
     * @param organizationId
     * @return list of permissions.
     * @author prabjot
     * This method provides array of supported operations that user can perform.
     */
    public Set<HashMap<String, Object>> getPermissions(long organizationId) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            return Collections.emptySet();
        }

        long loggedinUserId = UserContext.getUserDetails().getId();
        Boolean isCountryAdmin = userGraphRepository.checkIfUserIsCountryAdmin(loggedinUserId, AppConstants.AG_COUNTRY_ADMIN);
        List<Organization> units = organizationGraphRepository.getUnitsWithBasicInfo(organizationId);

        List<AccessPageQueryResult> mainModulePermissions = (isCountryAdmin) ? accessPageRepository.getPermissionOfMainModuleForHubMembers() :
                accessPageRepository.getPermissionOfMainModule(organizationId, loggedinUserId);
        Set<AccessPageQueryResult> unionOfPermissionOfModule = getUnionOfPermissions(mainModulePermissions);
        // USER HAS NO main module permission check his permission in current unit only via parent employment id
        Organization parentOrganization = (organization.isParentOrganization()) ? organization : organizationGraphRepository.getParentOfOrganization(organization.getId());
        if (unionOfPermissionOfModule.isEmpty()) {
            List<Long> organizationIds =
                    units.parallelStream().map(Organization::getId).collect(Collectors.toList());

            mainModulePermissions = accessPageRepository.getPermissionOfMainModule(organizationIds, loggedinUserId, parentOrganization.getId());
            unionOfPermissionOfModule = getUnionOfPermissions(mainModulePermissions);
        }
        Set<Map<String, Object>> list = new HashSet<>();
        Map<String, Object> unitPermissionMap;
        for (Organization unit : units) {
            List<AccessPageQueryResult> accessPageQueryResults;
            if (isCountryAdmin) {
                accessPageQueryResults = accessPageRepository.getTabsPermissionForHubMember();
            } else {
                accessPageQueryResults = accessPageRepository.getTabPermissionForUnit(unit.getId(), loggedinUserId, parentOrganization.getId());
            }
            unitPermissionMap = new HashMap<>();
            unitPermissionMap.put("id", unit.getId());
            unitPermissionMap.put("permissions", getUnionOfPermissions(accessPageQueryResults));
            list.add(unitPermissionMap);
        }

        Set<HashMap<String, Object>> permissionList = new HashSet<>();


        unionOfPermissionOfModule.forEach(mainModule -> {
            HashMap<String, Object> response = new HashMap<>();
            response.put("id", mainModule.getId());
            response.put("name", mainModule.getName());
            response.put("read", mainModule.isRead());
            response.put("write", mainModule.isWrite());
            response.put("moduleId", mainModule.getModuleId());
            response.put("active", mainModule.isActive());
            Set<HashMap<String, Object>> unitPermissionList = new HashSet<>();
            for (Map<String, Object> unitPermission : list) {
                HashMap<String, Object> unitPermissionForModule = new HashMap<>();
                unitPermissionForModule.put("unitId", unitPermission.get("id"));
                Set<AccessPageQueryResult> allModulePermission = (Set<AccessPageQueryResult>) unitPermission.get("permissions");
                Optional<AccessPageQueryResult> modulePermission =
                        allModulePermission.stream().filter(permission -> permission.getId() == mainModule.getId()).findFirst();
                if (modulePermission.isPresent()) {
                    unitPermissionForModule.put("tabPermissions", modulePermission.get().getChildren());
                } else {
                    unitPermissionForModule.put("tabPermissions", Collections.emptyList());
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
     *
     * @param accessPageQueryResults
     * @return List of tabs with permission parameter {tab name(String),read(boolean),write(boolean)}
     */
    private Set<AccessPageQueryResult> getUnionOfPermissions(List<AccessPageQueryResult> accessPageQueryResults) {

        Set<AccessPageQueryResult> filteredPageUniqueData = new HashSet<>();
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
                BeanUtils.copyProperties(module.getChildren(), subPageList);

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
                filteredPageUniqueData.add(module);
            }

        }
        filteredPages.addAll(filteredPageUniqueData);
        return filteredPageUniqueData;
    }


    private List<AccessPageQueryResult> getSubPagesToSearch(AccessPageQueryResult accessPageQueryResult, List<AccessPageQueryResult> accessPageQueryResults) {

        List<AccessPageQueryResult> subPages = new ArrayList<>();
        accessPageQueryResults.stream().filter(module -> module.getId() == accessPageQueryResult.getId()).forEach(module -> {
            subPages.addAll(accessPageQueryResult.getChildren());
        });
        return subPages;
    }

    public List<GrantedAuthority> getTabPermission(Long userId) {
        long startTime = System.currentTimeMillis();
        Set<TabPermission> tabPermissions = userGraphRepository.getAccessPermissionsOfUser(userId);
        Map<Long, List<TabPermission>> tabPermissionsByUnit = tabPermissions.stream().collect(Collectors.groupingBy(TabPermission::getUnitId));
        Set<Map.Entry<Long, List<TabPermission>>> entries = tabPermissionsByUnit.entrySet();
        Iterator<Map.Entry<Long, List<TabPermission>>> entryIterator = entries.iterator();
        List<GrantedAuthority> permissions = new ArrayList<>();
        while (entryIterator.hasNext()) {
            Map.Entry<Long, List<TabPermission>> unitPermissions = entryIterator.next();
            Map<String, TabPermission> processedTabs = new HashMap<>();
            unitPermissions.getValue().stream().forEach(tabPermission -> {
                if (processedTabs.containsKey(tabPermission.getTabId())) {
                    if (tabPermission.isWrite() || !processedTabs.get(tabPermission.getTabId()).isRead() && tabPermission.isRead()) {
                        processedTabs.put(tabPermission.getTabId(), tabPermission);
                    }
                } else {
                    processedTabs.put(tabPermission.getTabId(), tabPermission);
                }
            });
            permissions.addAll(getAuthoritiesList(processedTabs, unitPermissions.getKey()));
        }
        long endTime = System.currentTimeMillis();
        logger.info("Total time taken by : UserService:getTabPermission() " + (endTime - startTime) + " ms");
        return permissions;
    }

    private List<GrantedAuthority> getAuthoritiesList(Map<String, TabPermission> permissionByUnit, Long unitId) {

        Set<Map.Entry<String, TabPermission>> entries = permissionByUnit.entrySet();
        List<GrantedAuthority> permissionList = entries.stream().map(stringTabPermissionEntry -> {
            String permission = "";
            if (stringTabPermissionEntry.getValue().isRead() && stringTabPermissionEntry.getValue().isWrite()) {
                permission = unitId + "_" + stringTabPermissionEntry.getValue().getTabId()
                        + "_" + "rw";
            } else if (stringTabPermissionEntry.getValue().isRead()) {
                permission = unitId + "_" + stringTabPermissionEntry.getValue().getTabId() +
                        "_" + "r";
            } else {
                permission = unitId + "_" + stringTabPermissionEntry.getValue().getTabId();
            }
            return new SimpleGrantedAuthority(permission);
        }).collect(Collectors.toList());
        return permissionList;
    }

    public Boolean updateLastSelectedChildAndParentId(OrganizationSelectionDTO organizationSelectionDTO) {
        User currentUser = userGraphRepository.findOne(UserContext.getUserDetails().getId());
        if (Optional.ofNullable(organizationSelectionDTO.getLastSelectedParentOrgId()).isPresent()) {
            currentUser.setLastSelectedParentOrgId(organizationSelectionDTO.getLastSelectedParentOrgId());
        }
        if (Optional.ofNullable(organizationSelectionDTO.getLastSelectedChildOrgId()).isPresent()) {
            currentUser.setLastSelectedChildOrgId(organizationSelectionDTO.getLastSelectedChildOrgId());
        }
        userGraphRepository.save(currentUser);
        return true;
    }


    public boolean updateDateOfBirthOfUserByCPRNumber() {
        List<User> users = userGraphRepository.findAll();

        users.stream().forEach(user -> {
            String cprNumber = user.getCprNumber();
            Date dateOfBirth = Optional.ofNullable(user.getCprNumber()).isPresent() ? CPRUtil.fetchDateOfBirthFromCPR(user.getCprNumber()) : DateUtils.getCurrentDate();
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

    public boolean forgotPassword(String userEmail){
        if(userEmail.endsWith("kairos.com")||userEmail.endsWith("kairosplanning.com")){
            logger.error("Currently email ends with kairos.com or kairosplanning.com are not valid " + userEmail);
            exceptionService.dataNotFoundByIdException("message.user.mail.invalid", userEmail);
        }
        User currentUser = userGraphRepository.findByEmail(userEmail);
        if (!Optional.ofNullable(currentUser).isPresent()) {
            logger.error("No User found by email " + userEmail);
            exceptionService.dataNotFoundByIdException("message.user.email.notFound", userEmail);
        }
        String token = tokenService.createForgotPasswordToken(currentUser);
        mailService.sendPlainMailWithSendGrid(userEmail,AppConstants.MAIL_BODY.replace("{0}", StringUtils.capitalize(currentUser.getFirstName()))+config.getForgotPasswordApiLink()+token,AppConstants.MAIL_SUBJECT,config.getSendGridApiKey());
        return true;
    }

    public boolean resetPassword(String token ,PasswordUpdateDTO passwordUpdateDTO) {
        if(!passwordUpdateDTO.isValid()){
            exceptionService.actionNotPermittedException("message.staff.user.password.notmatch");
        }
        User user = findByForgotPasswordToken(token);
        if (!Optional.ofNullable(user).isPresent()) {
            logger.error("No User found by token");
            exceptionService.dataNotFoundByIdException("message.user.token.notFound");
        }
        //We are validating password reset token for 2 hours.
        DateTimeInterval interval = new DateTimeInterval(DateUtils.asDate(user.getForgotTokenRequestTime()), DateUtils.asDate(user.getForgotTokenRequestTime().plusHours(2)));
        if (!interval.contains(DateUtils.asDate(DateUtils.getCurrentLocalDateTime()))) {
            logger.error("Password reset token expired");
            exceptionService.dataNotFoundByIdException("message.user.token.expired");
        }
        CharSequence password = CharBuffer.wrap(passwordUpdateDTO.getConfirmPassword());
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setForgotPasswordToken(null);
        userGraphRepository.save(user);
        return true;
    }


}
