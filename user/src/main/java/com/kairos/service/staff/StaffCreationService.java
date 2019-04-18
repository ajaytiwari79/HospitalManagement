package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.UnitManagerDTO;
import com.kairos.persistence.model.staff.StaffQueryResult;
import com.kairos.persistence.model.staff.TimeCareStaffDTO;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionAndAccessPermissionGraphRepository;
import com.kairos.rest_client.TaskServiceRestClient;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.DEFAULT_PASSPHRASE_ENDS_WITH;
import static com.kairos.constants.AppConstants.KAIROS_EMAIL;

@Transactional
@Service
public class StaffCreationService {
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private PositionService positionService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject private StaffService staffService;
    @Inject
    private SystemLanguageService systemLanguageService;
    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private TeamService teamService;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private SkillService skillService;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;
    @Inject
    private UnitPermissionAndAccessPermissionGraphRepository unitPermissionAndAccessPermissionGraphRepository;
    @Inject
    private TaskServiceRestClient taskServiceRestClient;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(StaffService.class);

    private Staff createStaff(User user) {
        Staff staff = new Staff();
        staff.setEmail(user.getEmail());
        staff.setFirstName(user.getFirstName());
        staff.setLastName(user.getLastName());
        staff.setUser(user);
        staff.setContactDetail(user.getContactDetail());
        staffGraphRepository.save(staff);
        return staff;
    }

    private Staff createStaffObject(Organization parent, Organization unit, StaffCreationDTO payload) {
        StaffQueryResult staffQueryResult;
        if(Optional.ofNullable(parent).isPresent()) {
            staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(parent.getId(), payload.getExternalId());
        } else {
            staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(unit.getId(), payload.getExternalId());
        }
        Staff staff;
        if(Optional.ofNullable(staffQueryResult).isPresent()) {
            staff = staffQueryResult.getStaff();
        } else {
            LOGGER.info("Creating new staff with kmd external id " + payload.getExternalId() + " in unit " + unit.getId());
            staff = new Staff();
        }
        staff.setUserName(payload.getUserName());
        staff.setEmail(payload.getPrivateEmail());
        staff.setInactiveFrom(payload.getInactiveFrom());
        staff.setExternalId(payload.getExternalId());
        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setFamilyName(payload.getFamilyName());
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);
        staff.setContactAddress(contactAddress);
        ObjectMapper objectMapper = new ObjectMapper();
        ContactDetail contactDetail = objectMapper.convertValue(payload, ContactDetail.class);
        staff.setContactDetail(contactDetail);
        staff.setCurrentStatus(payload.getCurrentStatus());
        if(Optional.ofNullable(staffQueryResult).isPresent()) {
            contactAddress.setId(staffQueryResult.getContactAddressId());
            contactDetail.setId(staffQueryResult.getContactDetailId());
        }
        if(Optional.ofNullable(payload.getEngineerTypeId()).isPresent()) {
            EngineerType engineerType = engineerTypeGraphRepository.findOne(payload.getEngineerTypeId());
            staff.setEngineerType(engineerType);
        }
        return staff;
    }

    public Map createUnitManager(long unitId, UnitManagerDTO unitManagerDTO) {
        User user = userGraphRepository.findByEmail(unitManagerDTO.getEmail());
        Organization unit = organizationGraphRepository.findOne(unitId);
        Organization parent;
        if(unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        final String password = unitManagerDTO.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH;
        ObjectMapper mapper = new ObjectMapper();
        Map unitManagerDTOMap = mapper.convertValue(unitManagerDTO, Map.class);
        if(user == null) {
            LOGGER.info("Unit manager is null..creating new user first");
            user = new User();
            user.setUserName(unitManagerDTO.getEmail());
            user.setEmail(unitManagerDTO.getEmail());
            user.setFirstName(unitManagerDTO.getFirstName().trim());
            user.setLastName(unitManagerDTO.getLastName().trim());
            user.setContactDetail(unitManagerDTO.getContactDetail());
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            userGraphRepository.save(user);
            Staff staff = createStaff(user);
            unitManagerDTOMap.put("id", staff.getId());
            positionService.createPositionForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
            staffService.sendEmailToUnitManager(unitManagerDTO, password);
            return unitManagerDTOMap;
        } else {
            long organizationId = (parent == null) ? unitId : parent.getId();
            if(staffGraphRepository.countOfUnitEmployment(organizationId, unitId, user.getEmail()) == 0) {
                Staff staff = createStaff(user);
                unitManagerDTOMap.put("id", staff.getId());
                positionService.createPositionForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
                userGraphRepository.save(user);
                staffService.sendEmailToUnitManager(unitManagerDTO, password);
                return unitManagerDTOMap;
            } else {
                return null;
            }
        }
    }

    public void createEmployment(Organization organization, Organization unit, Staff staff, Long accessGroupId, Long employedSince, boolean employmentAlreadyExist) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if(!Optional.ofNullable(accessGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.staff.accessgroup.notfound", accessGroupId);

        }
        if(accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate())) {
            exceptionService.actionNotPermittedException("error.access.expired", accessGroup.getName());
        }
        Position position;
        if(employmentAlreadyExist) {
            position = (Optional.ofNullable(organization).isPresent()) ? positionGraphRepository.findPosition(organization.getId(), staff.getId()) : positionGraphRepository.findPosition(unit.getId(), staff.getId());
        } else {
            position = new Position();
        }
        position.setName("Working as staff");
        position.setStaff(staff);
        position.setStartDateMillis(employedSince);
        UnitPermission unitPermission = new UnitPermission();
        unitPermission.setOrganization(unit);
        unitPermission.setAccessGroup(accessGroup);
        position.getUnitPermissions().add(unitPermission);
        positionGraphRepository.save(position);
        if(Optional.ofNullable(organization).isPresent()) {
            if(Optional.ofNullable(organization.getPositions()).isPresent()) {
                organization.getPositions().add(position);
                organizationGraphRepository.save(organization);
            } else {
                List<Position> positions = new ArrayList<>();
                positions.add(position);
                organization.setPositions(positions);
                organizationGraphRepository.save(organization);
            }
        } else {
            if(Optional.ofNullable(unit.getPositions()).isPresent()) {
                unit.getPositions().add(position);
                organizationGraphRepository.save(unit);
            } else {
                List<Position> positions = new ArrayList<>();
                positions.add(position);
                unit.setPositions(positions);
                organizationGraphRepository.save(unit);
            }

        }
    }

    public Staff createStaffObject(User user, Staff staff, Long engineerTypeId, Organization unit) {
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(unit);
        if(contactAddress != null) {
            staff.setContactAddress(contactAddress);
        }
        if(engineerTypeId != null) {
            staff.setEngineerType(engineerTypeGraphRepository.findOne(engineerTypeId));
        }
        staff.setUser(user);
        staff.setOrganizationId(unit.getId());
        staff = staffGraphRepository.save(staff);
        Organization parent = null;
        if(!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
        } else if(!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        if(parent == null) {
            if(positionGraphRepository.findPosition(unit.getId(), staff.getId()) == null) {
                positionGraphRepository.createPositions(unit.getId(), Collections.singletonList(staff.getId()), unit.getId());
            }
        } else {
            if(positionGraphRepository.findPosition(parent.getId(), staff.getId()) == null) {
                positionGraphRepository.createPositions(parent.getId(), Collections.singletonList(staff.getId()), unit.getId());
            }
        }
        return staff;
    }

    private Staff createStaff(Staff staff, Long unitId) {
        if(staffService.checkStaffEmailConstraint(staff)) {
            LOGGER.info("Creating Staff.......... " + staff.getFirstName() + " " + staff.getLastName());
            LOGGER.info("Creating User for Staff");
            SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(unitId);
            User user = new User();
            user.setEmail(staff.getEmail());
            user.setUserLanguage(systemLanguage);
            staff.setUser(userGraphRepository.save(user));
            staffGraphRepository.save(staff);
            return staff;
        }
        LOGGER.info("Not Creating Staff.......... " + staff.getFirstName() + " " + staff.getLastName());
        return null;
    }

    public User createCountryAdmin(User admin) {
        User user = userGraphRepository.findByEmail(admin.getEmail());
        if (user != null) {
            return null;
        }
        admin.setPassword(new BCryptPasswordEncoder().encode(admin.getPassword()));
        userGraphRepository.save(admin);
        Staff adminAsStaff = new Staff();
        adminAsStaff.setGeneralNote("Will manage the platform");
        adminAsStaff.setUser(admin);
        adminAsStaff.setFirstName(admin.getFirstName());
        adminAsStaff.setLastName(admin.getLastName());
        adminAsStaff.setCurrentStatus(StaffStatusEnum.ACTIVE);
        adminAsStaff.setEmail(admin.getEmail());
        adminAsStaff.setUserName(admin.getEmail());
        staffGraphRepository.save(adminAsStaff);

        List<Organization> organizations = organizationGraphRepository.findByOrganizationLevel(OrganizationLevel.COUNTRY);
        Organization organization = null;
        if (!organizations.isEmpty()) {
            organization = organizations.get(0);
        }
        if (organization != null) {
            Position position = new Position("working as country admin", adminAsStaff);
            organization.getPositions().add(position);
            organizationGraphRepository.save(organization);

            AccessGroup accessGroup = accessGroupRepository.findAccessGroupByName(organization.getId(), AppConstants.COUNTRY_ADMIN);
            UnitPermission unitPermission = new UnitPermission();
            unitPermission.setOrganization(organization);
            AccessPermission accessPermission = new AccessPermission(accessGroup);
            UnitPermissionAccessPermissionRelationship unitPermissionAccessPermissionRelationship = new UnitPermissionAccessPermissionRelationship(unitPermission, accessPermission);
            unitPermissionAccessPermissionRelationship.setEnabled(true);
            unitPermissionAndAccessPermissionGraphRepository.save(unitPermissionAccessPermissionRelationship);
            accessPageService.setPagePermissionToAdmin(accessPermission);
            position.getUnitPermissions().add(unitPermission);
            organization.getPositions().add(position);
            organizationGraphRepository.save(organization);
        } else {
            return null;
        }
        return admin;
    }


    public Staff createStaffFromPlanningWorkflow(StaffDTO data, long unitId) {
        if (data == null) {
            return null;
        }
        Staff staff = new Staff();
        staff.setFirstName(data.getFirstName());
        staff.setLastName(data.getLastName());
        staff.setFamilyName(data.getFamilyName());
        staff.setCurrentStatus(data.getCurrentStatus());
        staff = createStaff(staff, unitId);
        if (staff != null) {
            if (data.getTeamId() != null) {
                //TODO hardcoded unit id to removes
                boolean result = teamService.addStaffInTeam(staff.getId(), data.getTeamId(), false, unitId);
                LOGGER.info("Assigning team to staff: " + result);
            }
            if (data.getSkills() != null) {
                List<Map<String, Object>> result = skillService.assignSkillToStaff(staff.getId(), data.getSkills(), false, unitId);
                LOGGER.info("Assigned Number of Skills to staff: " + result.size());
            }
            taskServiceRestClient.updateTaskForStaff(staff.getId(), data.getAnonymousStaffId());
            return staff;
        }
        return null;
    }


    public StaffDTO createStaffFromWeb(Long unitId, StaffCreationDTO payload) {
        if (payload.getCprNumber().length() != 10) {
            exceptionService.invalidSize("message.cprNumber.size");
        }
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        Organization parent = null;
        if (!unit.isParentOrganization() && OrganizationLevel.CITY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else if (!unit.isParentOrganization() && OrganizationLevel.COUNTRY.equals(unit.getOrganizationLevel())) {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }
        if (staffGraphRepository.findStaffByEmailInOrganization(payload.getPrivateEmail(), unitId) != null) {
            exceptionService.duplicateDataException("message.email.alreadyExist", "Staff", payload.getPrivateEmail());
        }
        // Check if Staff exists in organization with CPR Number
        if (staffGraphRepository.isStaffExistsByCPRNumber(payload.getCprNumber(), Optional.ofNullable(parent).isPresent() ? parent.getId() : unitId)) {
            exceptionService.invalidRequestException("error.staff.exists.same.cprNumber", payload.getCprNumber());
        }
        User user = userGraphRepository.findUserByCprNumber(payload.getCprNumber());

        if (!Optional.ofNullable(user).isPresent()) {
            user = Optional.ofNullable(userGraphRepository.findByEmail(payload.getPrivateEmail().trim())).orElse(new User());
        }

        Staff staff = staffGraphRepository.findByExternalId(payload.getExternalId());
        if (Optional.ofNullable(staff).isPresent()) {
            exceptionService.duplicateDataException("message.staff.externalid.alreadyexist");

        }
        User userWithExistingUserName = userGraphRepository.findUserByUserName("(?i)" +payload.getUserName());
        if(Optional.ofNullable(userWithExistingUserName).isPresent()){
            exceptionService.duplicateDataException("message.staff.userName.alreadyexist");
        }

        setBasicDetailsOfUser(user, payload);
        // Set default language of User
        Long countryId = UserContext.getUserDetails().getCountryId();
        SystemLanguage systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(countryId);
        user.setUserLanguage(systemLanguage);
        staff = createStaffObject(parent, unit, payload);
        boolean isEmploymentExist = (staff.getId()) != null;
        staff.setUser(user);
        staffService.addStaffInChatServer(staff);
        staffGraphRepository.save(staff);
        createEmployment(parent, unit, staff, payload.getAccessGroupId(), DateUtils.getCurrentDateMillis(), isEmploymentExist);
        activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(staff.getId())), unitId);
        return new StaffDTO(staff.getId(), staff.getFirstName(), staff.getLastName(), user.getGender(), user.getAge(

        ));
    }

    public User createUnitManagerForNewOrganization(Organization organization, StaffCreationDTO staffCreationData) {
        User user = userGraphRepository.findByEmail(staffCreationData.getPrivateEmail().trim());
        if (!Optional.ofNullable(user).isPresent()) {
            SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(organization.getId());
            user = new User();
            user.setUserLanguage(systemLanguage);
            setBasicDetailsOfUser(user, staffCreationData);
            userGraphRepository.save(user);
        }
        staffService.setUnitManagerAndEmployment(organization, user, staffCreationData.getAccessGroupId());
        return user;
    }

    public boolean importStaffFromTimeCare(List<TimeCareStaffDTO> timeCareStaffDTOS, String externalId) {
        Organization organization = organizationGraphRepository.findByExternalId(externalId);
        if(organization == null) {
            exceptionService.dataNotFoundByIdException("message.externalid.notfound");
        }
        List<TimeCareStaffDTO> timeCareStaffByWorkPlace = timeCareStaffDTOS.stream().filter(timeCareStaffDTO -> timeCareStaffDTO.getParentWorkPlaceId().equals(externalId)).
                collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();
        AccessGroup accessGroup = accessGroupRepository.findTaskGiverAccessGroup(organization.getId());
        if(accessGroup == null) {
            exceptionService.dataNotFoundByIdException("message.taskgiver.accesgroup.notPresent");

        }
        SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(organization.getId());
        for (TimeCareStaffDTO timeCareStaffDTO : timeCareStaffByWorkPlace) {
            String email = (timeCareStaffDTO.getEmail() == null) ? timeCareStaffDTO.getFirstName() + KAIROS_EMAIL : timeCareStaffDTO.getEmail();
            User user = Optional.ofNullable(userGraphRepository.findByEmail(email.trim())).orElse(new User());
            user.setUserLanguage(systemLanguage);
            if(staffGraphRepository.staffAlreadyInUnit(Long.valueOf(timeCareStaffDTO.getId()), organization.getId())) {
                exceptionService.duplicateDataException("message.staff.alreadyexist");
            }
            if(timeCareStaffDTO.getGender().equalsIgnoreCase("m")) {
                timeCareStaffDTO.setGender(Gender.MALE.toString());
            } else if(timeCareStaffDTO.getGender().equalsIgnoreCase("f")) {
                timeCareStaffDTO.setGender(Gender.FEMALE.toString());
            } else {
                timeCareStaffDTO.setGender(null);
            }
            StaffCreationDTO payload = objectMapper.convertValue(timeCareStaffDTO, StaffCreationDTO.class);
            payload.setAccessGroupId(accessGroup.getId());
            payload.setPrivateEmail(email);
            setBasicDetailsOfUser(user, payload);
            Staff staff = mapDataInStaffObject(timeCareStaffDTO, organization, email);
            boolean isEmploymentExist = (staff.getId()) != null;
            staff.setUser(user);
            staffGraphRepository.save(staff);
            createEmployment(organization, organization, staff, payload.getAccessGroupId(), null, isEmploymentExist);
        }
        return true;
    }

    public Staff mapDataInStaffObject(TimeCareStaffDTO timeCareStaffDTO, Organization organization, String email) {
        StaffQueryResult staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(organization.getId(), Long.valueOf(timeCareStaffDTO.getId()));
        Staff staff = (Optional.ofNullable(staffQueryResult).isPresent()) ? staffQueryResult.getStaff() : new Staff();
        ContactAddress contactAddress;
        if(timeCareStaffDTO.getZipCode() == null) {
            contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(organization);
            if(staffQueryResult != null) {
                contactAddress.setId(staffQueryResult.getContactAddressId());
            }
        } else {
            contactAddress = new ContactAddress();
            contactAddress.setStreet(timeCareStaffDTO.getAddress());
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(timeCareStaffDTO.getZipCode());
            if(matcher.find()) {
                ZipCode zipCode = zipCodeGraphRepository.findByZipCode(Integer.valueOf(matcher.group(0)));
                contactAddress.setZipCode(zipCode);
            }
            if(staffQueryResult != null) {
                contactAddress.setId(staffQueryResult.getContactAddressId());
            }
            matcher = pattern.matcher(timeCareStaffDTO.getAddress());
            if(matcher.find()) {
                contactAddress.setHouseNumber(matcher.group(0));
            }
        }
        staff.setContactAddress(contactAddress);
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setPrivatePhone(timeCareStaffDTO.getCellPhoneNumber());
        contactDetail.setLandLinePhone(timeCareStaffDTO.getTelephoneNumber());
        contactDetail.setPrivateEmail(email);
        if(staffQueryResult != null) {
            contactDetail.setId(staffQueryResult.getContactDetailId());
        }
        staff.setContactDetail(contactDetail);
        staff.setEmail(email);
        staff.setExternalId(Long.valueOf(timeCareStaffDTO.getId()));
        staff.setFirstName(timeCareStaffDTO.getFirstName());
        staff.setLastName(timeCareStaffDTO.getLastName());
        return staff;
    }

    public void setBasicDetailsOfUser(User user, StaffCreationDTO staffCreationDTO) {
        user.setEmail(staffCreationDTO.getPrivateEmail());
        user.setUserName(staffCreationDTO.getUserName());
        user.setFirstName(staffCreationDTO.getFirstName());
        user.setLastName(staffCreationDTO.getLastName());
        user.setPassword(Optional.ofNullable(user.getFirstName()).isPresent() ? new BCryptPasswordEncoder().encode(user.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH) : null);
        user.setCprNumber(staffCreationDTO.getCprNumber());
        if(!StringUtils.isBlank(staffCreationDTO.getCprNumber())) {
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffCreationDTO.getCprNumber()));
            user.setGender(CPRUtil.getGenderFromCPRNumber(staffCreationDTO.getCprNumber()));
        }
    }
}
