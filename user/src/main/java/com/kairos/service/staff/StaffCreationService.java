package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
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
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
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
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.system_setting.SystemLanguageService;
import com.kairos.utils.CPRUtil;
import com.kairos.dto.user_context.UserContext;
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

import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;

@Transactional
@Service
public class StaffCreationService {
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private PositionService positionService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private StaffService staffService;
    @Inject
    private SystemLanguageService systemLanguageService;
    @Inject
    private CountryService countryService;
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

    private Staff createStaffByUser(User user) {
        Staff staff = new Staff();
        staff.setEmail(user.getEmail());
        staff.setFirstName(user.getFirstName());
        staff.setLastName(user.getLastName());
        staff.setUser(user);
        staff.setContactDetail(user.getContactDetail());
        staffGraphRepository.save(staff);
        return staff;
    }

    private Staff updateStaffDetailsOnCreationOfStaff(Organization organization, StaffCreationDTO payload) {
        StaffQueryResult staffQueryResult;

        staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(organization.getId(), payload.getExternalId());
        Staff staff;
        if (Optional.ofNullable(staffQueryResult).isPresent()) {
            staff = staffQueryResult.getStaff();
        } else {
            staff = new Staff();
        }
        staff.setEmail(payload.getPrivateEmail());
        staff.setInactiveFrom(payload.getInactiveFrom());
        staff.setExternalId(payload.getExternalId());
        staff.setFirstName(payload.getFirstName());
        staff.setLastName(payload.getLastName());
        staff.setFamilyName(payload.getFamilyName());
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(organization);
        staff.setContactAddress(contactAddress);
        ObjectMapper objectMapper = new ObjectMapper();
        ContactDetail contactDetail = objectMapper.convertValue(payload, ContactDetail.class);
        staff.setContactDetail(contactDetail);
        staff.setCurrentStatus(payload.getCurrentStatus());
        staff.setTags(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(payload.getTags(), Tag.class));
        if (Optional.ofNullable(staffQueryResult).isPresent()) {
            contactAddress.setId(staffQueryResult.getContactAddressId());
            contactDetail.setId(staffQueryResult.getContactDetailId());
        }
        if (Optional.ofNullable(payload.getEngineerTypeId()).isPresent()) {
            EngineerType engineerType = engineerTypeGraphRepository.findOne(payload.getEngineerTypeId());
            staff.setEngineerType(engineerType);
        }
        return staff;
    }

    public Map createUnitManager(long unitId, UnitManagerDTO unitManagerDTO) {
        User user = userGraphRepository.findByEmail(unitManagerDTO.getEmail());
        Unit unit = unitGraphRepository.findOne(unitId);
        Organization parent = organizationService.fetchParentOrganization(unitId);
        final String password = unitManagerDTO.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH;
        ObjectMapper mapper = new ObjectMapper();
        Map unitManagerDTOMap = mapper.convertValue(unitManagerDTO, Map.class);
        if (user == null) {
            LOGGER.info("Unit manager is null..creating new user first");
            user = new User(unitManagerDTO.getEmail(), unitManagerDTO.getFirstName().trim(), unitManagerDTO.getLastName().trim(), unitManagerDTO.getEmail(), unitManagerDTO.getContactDetail(), new BCryptPasswordEncoder().encode(password));
            userGraphRepository.save(user);
            Staff staff = createStaffByUser(user);
            unitManagerDTOMap.put("id", staff.getId());
            positionService.createPositionForUnitManager(staff, parent, unit, unitManagerDTO.getAccessGroupId());
            staffService.sendEmailToUnitManager(unitManagerDTO, password);
            return unitManagerDTOMap;
        } else {
            long organizationId = (parent == null) ? unitId : parent.getId();
            if(staffGraphRepository.countOfUnitEmployment(organizationId, unitId, user.getEmail()) == 0) {
                Staff staff = createStaffByUser(user);
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


    public Staff updateStaffDetailsOnCreationOfStaff(User user, Staff staff, Long engineerTypeId, Organization organization) {
        ContactAddress contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(organization);
        if (contactAddress != null) {
            staff.setContactAddress(contactAddress);
        }
        if (engineerTypeId != null) {
            staff.setEngineerType(engineerTypeGraphRepository.findOne(engineerTypeId));
        }
        staff.setUser(user);
        staff.setOrganizationId(organization.getId());
        staff = staffGraphRepository.save(staff);
        Organization parent =organizationService.fetchParentOrganization(organization.getId());
        if (parent == null) {
            if (positionGraphRepository.findPosition(organization.getId(), staff.getId()) == null) {
                positionGraphRepository.createPositions(organization.getId(), Collections.singletonList(staff.getId()), organization.getId());
            }
        } else {
            if (positionGraphRepository.findPosition(parent.getId(), staff.getId()) == null) {
                positionGraphRepository.createPositions(parent.getId(), Collections.singletonList(staff.getId()), organization.getId());
            }
        }
        return staff;
    }

    public User createCountryAdmin(User admin) {
        User user = userGraphRepository.findByEmail(admin.getEmail());
        if(user != null) {
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
        staffGraphRepository.save(adminAsStaff);
        Organization organization = organizationGraphRepository.findHub();
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

    public StaffDTO createStaff(Long unitId, StaffCreationDTO payload) {
        if(payload.getCprNumber().length() != 10) {
            exceptionService.invalidSize(MESSAGE_CPRNUMBER_SIZE);
        }
        Organization organization = organizationService.fetchParentOrganization(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        if (staffGraphRepository.findStaffByEmailInOrganization(payload.getPrivateEmail(), unitId) != null) {
            exceptionService.duplicateDataException(MESSAGE_EMAIL_ALREADYEXIST, "Staff", payload.getPrivateEmail());
        }
        // Check if Staff exists in organization with CPR Number
        if (staffGraphRepository.isStaffExistsByCPRNumber(payload.getCprNumber(), organization.getId())) {
            exceptionService.invalidRequestException(ERROR_STAFF_EXISTS_SAME_CPRNUMBER, payload.getCprNumber());
        }
        User user = userGraphRepository.findUserByCprNumber(payload.getCprNumber());
        if(!Optional.ofNullable(user).isPresent()) {
            user = Optional.ofNullable(userGraphRepository.findByEmail(payload.getPrivateEmail().trim())).orElse(new User( payload.getCprNumber(),payload.getFirstName().trim(), payload.getLastName().trim(),payload.getPrivateEmail(),payload.getUserName()));
        }
        Staff staff = staffGraphRepository.findByExternalId(payload.getExternalId());
        if(Optional.ofNullable(staff).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_STAFF_EXTERNALID_ALREADYEXIST);

        }
        User userWithExistingUserName = userGraphRepository.findUserByUserName("(?i)" + payload.getUserName());
        if (Optional.ofNullable(userWithExistingUserName).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_STAFF_USERNAME_ALREADYEXIST);
        }
        // Set default language of User
        Long countryId = UserContext.getUserDetails().getCountryId();
        SystemLanguage systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(countryId);
        user.setUserLanguage(systemLanguage);
        user.setUserName(payload.getUserName());
        final String password = payload.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH;
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setCountryId(organization.getCountry().getId());
        staff = updateStaffDetailsOnCreationOfStaff(organization, payload);
        staff.setUser(user);
        staffService.addStaffInChatServer(staff);
        staffGraphRepository.save(staff);
        positionService.createPosition(organization, staff, payload.getAccessGroupId(), DateUtils.getCurrentDateMillis());
        activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(staff.getId())), unitId);
        return new StaffDTO(staff.getId(), staff.getFirstName(), staff.getLastName(), user.getGender(), user.getAge());
    }

    public User createUnitManagerForNewOrganization(Organization organization, StaffCreationDTO staffCreationData) {
        User user = userGraphRepository.findByEmail(staffCreationData.getPrivateEmail().trim());
        if(!Optional.ofNullable(user).isPresent()) {
            SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(organization.getId());
            user = new User();
            user.setUserLanguage(systemLanguage);
            user.setCountryId(countryService.getCountryIdByUnitId(organization.getId()));
            userGraphRepository.save(user);
        }
        Staff staff = new Staff(user.getEmail(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getFirstName(), StaffStatusEnum.ACTIVE, null, user.getCprNumber());
        Position position = new Position();
        position.setStaff(staff);
        staff.setUser(user);
        position.setName(UNIT_MANAGER_EMPLOYMENT_DESCRIPTION);
        position.setStaff(staff);
        position.setStartDateMillis(DateUtils.getCurrentDateMillis());
        organization.getPositions().add(position);
        organizationGraphRepository.save(organization);
        if(staffCreationData.getAccessGroupId() != null) {
            UnitPermission unitPermission = new UnitPermission();
            unitPermission.setOrganization(organization);
            AccessGroup accessGroup = accessGroupRepository.findOne(staffCreationData.getAccessGroupId());
            if(Optional.ofNullable(accessGroup).isPresent()) {
                unitPermission.setAccessGroup(accessGroup);
            }
            position.getUnitPermissions().add(unitPermission);
        }
        staff.setContactAddress(staffAddressService.getStaffContactAddressByOrganizationAddress(organization));
        positionGraphRepository.save(position);
        activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(position.getStaff().getId())), organization.getId());
        return user;
    }

    public boolean importStaffFromTimeCare(List<TimeCareStaffDTO> timeCareStaffDTOS, String externalId) {
        Organization organization = organizationGraphRepository.findByExternalId(externalId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EXTERNALID_NOTFOUND);
        }
        List<TimeCareStaffDTO> timeCareStaffByWorkPlace = timeCareStaffDTOS.stream().filter(timeCareStaffDTO -> timeCareStaffDTO.getParentWorkPlaceId().equals(externalId)).
                collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();
        AccessGroup accessGroup = accessGroupRepository.findTaskGiverAccessGroup(organization.getId());
        if (accessGroup == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TASKGIVER_ACCESGROUP_NOTPRESENT);

        }
        SystemLanguage systemLanguage = systemLanguageService.getDefaultSystemLanguageForUnit(organization.getId());
        for (TimeCareStaffDTO timeCareStaffDTO : timeCareStaffByWorkPlace) {
            String email = (timeCareStaffDTO.getEmail() == null) ? timeCareStaffDTO.getFirstName() + KAIROS_EMAIL : timeCareStaffDTO.getEmail();
            User user = Optional.ofNullable(userGraphRepository.findByEmail(email.trim())).orElse(new User());
            user.setUserLanguage(systemLanguage);
            if (staffGraphRepository.staffAlreadyInUnit(Long.valueOf(timeCareStaffDTO.getId()), organization.getId())) {
                exceptionService.duplicateDataException(MESSAGE_STAFF_ALREADYEXIST);
            }
            if (timeCareStaffDTO.getGender().equalsIgnoreCase("m")) {
                timeCareStaffDTO.setGender(Gender.MALE.toString());
            } else if (timeCareStaffDTO.getGender().equalsIgnoreCase("f")) {
                timeCareStaffDTO.setGender(Gender.FEMALE.toString());
            } else {
                timeCareStaffDTO.setGender(null);
            }
            StaffCreationDTO payload = objectMapper.convertValue(timeCareStaffDTO, StaffCreationDTO.class);
            payload.setAccessGroupId(accessGroup.getId());
            payload.setPrivateEmail(email);
            Staff staff = updateStaffDetailsOnImportingFromTimeCare(timeCareStaffDTO, organization, email);
            staff.setUser(user);
            staffGraphRepository.save(staff);
            positionService.createPosition(organization, staff, payload.getAccessGroupId(), null);
        }
        return true;
    }

    public Staff updateStaffDetailsOnImportingFromTimeCare(TimeCareStaffDTO timeCareStaffDTO, Organization organization, String email) {
        StaffQueryResult staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(organization.getId(), Long.valueOf(timeCareStaffDTO.getId()));
        Staff staff = (Optional.ofNullable(staffQueryResult).isPresent()) ? staffQueryResult.getStaff() : new Staff();
        ContactAddress contactAddress = getContactAddressOnImportStaffFromTimeCare(timeCareStaffDTO, organization, staffQueryResult);
        staff.setContactAddress(contactAddress);
        ContactDetail contactDetail = new ContactDetail(email, timeCareStaffDTO.getTelephoneNumber(), timeCareStaffDTO.getCellPhoneNumber());
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

    private ContactAddress getContactAddressOnImportStaffFromTimeCare(TimeCareStaffDTO timeCareStaffDTO, Organization organization, StaffQueryResult staffQueryResult) {
        ContactAddress contactAddress;
        if (timeCareStaffDTO.getZipCode() == null) {
            contactAddress = staffAddressService.getStaffContactAddressByOrganizationAddress(organization);
            if (staffQueryResult != null) {
                contactAddress.setId(staffQueryResult.getContactAddressId());
            }
        } else {
            contactAddress = new ContactAddress();
            contactAddress.setStreet(timeCareStaffDTO.getAddress());
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(timeCareStaffDTO.getZipCode());
            if (matcher.find()) {
                ZipCode zipCode = zipCodeGraphRepository.findByZipCode(Integer.valueOf(matcher.group(0)));
                contactAddress.setZipCode(zipCode);
            }
            if (staffQueryResult != null) {
                contactAddress.setId(staffQueryResult.getContactAddressId());
            }
            matcher = pattern.matcher(timeCareStaffDTO.getAddress());
            if (matcher.find()) {
                contactAddress.setHouseNumber(matcher.group(0));
            }
        }
        return contactAddress;
    }

    public void setBasicDetailsOfUser(User user, StaffCreationDTO staffCreationDTO) {
        user.setUserNameUpdated(true);
        user.setEmail(staffCreationDTO.getPrivateEmail());
        user.setUserName(staffCreationDTO.getUserName());
        user.setFirstName(staffCreationDTO.getFirstName());
        user.setLastName(staffCreationDTO.getLastName());
        user.setPassword(Optional.ofNullable(user.getFirstName()).isPresent() ? new BCryptPasswordEncoder().encode(user.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH) : null);
        user.setCprNumber(staffCreationDTO.getCprNumber());
        if (!StringUtils.isBlank(staffCreationDTO.getCprNumber())) {
            user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(staffCreationDTO.getCprNumber()));
            user.setGender(CPRUtil.getGenderFromCPRNumber(staffCreationDTO.getCprNumber()));
        }
    }
}
