package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.user.UserType;
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
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
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


    private Staff updateStaffDetailsOnCreationOfStaff(Organization organization, StaffCreationDTO payload) {
        StaffQueryResult staffQueryResult;
        staffQueryResult = staffGraphRepository.getStaffByExternalIdInOrganization(organization.getId(), payload.getExternalId());
        Staff staff = Optional.ofNullable(staffQueryResult).isPresent() ? staffQueryResult.getStaff() : new Staff();
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
        staff.setCurrentStatus(payload.getCurrentStatus()==null?StaffStatusEnum.ACTIVE:payload.getCurrentStatus());
        staff.setTags(ObjectMapperUtils.copyCollectionPropertiesByMapper(payload.getTags(), Tag.class));
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
        Organization parent = organizationService.fetchParentOrganization(organization.getId());
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
        }
        return admin;
    }

    public StaffDTO createStaff(Long unitId, StaffCreationDTO payload) {
        Staff staff;
        Organization organization = organizationService.fetchParentOrganization(unitId);
        if (StaffStatusEnum.ACTIVE.equals(payload.getCurrentStatus())) {
            validateRequireFieldOfStaff(unitId, payload, organization);
        }
        User user = Optional.ofNullable(userGraphRepository.findUserByCprNumberOrEmail(payload.getCprNumber(),payload.getPrivateEmail().trim())).orElse(new User(payload.getCprNumber(), payload.getFirstName().trim(), payload.getLastName().trim(), payload.getPrivateEmail(), payload.getUserName()));
        updateUserDetails(payload, user, organization);
        staff = updateStaffDetailsOnCreationOfStaff(organization, payload);
        staff.setUser(user);
        staffGraphRepository.save(staff);
        Long organizationAccessGroupId = accessGroupRepository.accessGroupByOrganizationIdAndParentAccessGroupId(organization.getId(),payload.getAccessGroupId());
        positionService.createPosition(organization, staff, organizationAccessGroupId, DateUtils.getCurrentDateMillis(), unitId);
        if (StaffStatusEnum.ACTIVE.equals(payload.getCurrentStatus())) {
            staffService.addStaffInChatServer(staff);
            DefaultKPISettingDTO defaultKPISettingDTO = new DefaultKPISettingDTO(Arrays.asList(staff.getId()));
            defaultKPISettingDTO.setParentUnitId(organization.getId());
            activityIntegrationService.createDefaultKPISettingForStaff(defaultKPISettingDTO, unitId);
        }
        StaffDTO staffDTO = ObjectMapperUtils.copyPropertiesByMapper(staff, StaffDTO.class);
        staffDTO.setGender(user.getGender());
        staffDTO.setAge(user.getAge());
        return staffDTO;
    }

    private void updateUserDetails(StaffCreationDTO payload, User user, Organization organization) {
        SystemLanguage systemLanguage = systemLanguageGraphRepository.getSystemLanguageOfCountry(UserContext.getUserDetails().getCountryId());
        user.setUserLanguage(systemLanguage);
        user.setUserName(payload.getUserName());
        final String password = payload.getFirstName().replaceAll("\\s+", "") + DEFAULT_PASSPHRASE_ENDS_WITH;
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setCountryId(organization.getCountry().getId());
        user.setUserType(UserType.USER_ACCOUNT);
        user.setGender(CPRUtil.getGenderFromCPRNumber(payload.getCprNumber()));
    }


    private void validateRequireFieldOfStaff(Long unitId, StaffCreationDTO staffCreationDTO, Organization organization) {
        if (ObjectUtils.isNull(staffCreationDTO.getCprNumber())) {
            exceptionService.dataNotFoundByIdException(ERROR_STAFF_CPRNUMBER_NOTNULL);
        }
        if (ObjectUtils.isNull(staffCreationDTO.getPrivateEmail())) {
            exceptionService.dataNotFoundByIdException(ERROR_EMAIL_VALID);
        }
        if (ObjectUtils.isNull(staffCreationDTO.getUserName())) {
            exceptionService.dataNotFoundByIdException(ERROR_STAFF_USERNAME_NOTNULL);
        }
        if (staffCreationDTO.getCprNumber().length() != 10) {
            exceptionService.invalidSize(MESSAGE_CPRNUMBER_SIZE);
        }
        if (staffGraphRepository.findStaffByEmailInOrganization(staffCreationDTO.getPrivateEmail(), unitId) != null) {
            exceptionService.duplicateDataException(MESSAGE_EMAIL_ALREADYEXIST, "Staff", staffCreationDTO.getPrivateEmail());
        }
        // Check if Staff exists in organization with CPR Number
        if (staffGraphRepository.isStaffExistsByCPRNumber(staffCreationDTO.getCprNumber(), organization.getId())) {
            exceptionService.invalidRequestException(ERROR_STAFF_EXISTS_SAME_CPRNUMBER, staffCreationDTO.getCprNumber());
        }
        Staff staff = staffGraphRepository.findByExternalId(staffCreationDTO.getExternalId());
        if (Optional.ofNullable(staff).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_STAFF_EXTERNALID_ALREADYEXIST);
        }

    }


    public boolean importStaffFromTimeCare(List<TimeCareStaffDTO> timeCareStaffDTOS, String externalId) {
        Organization organization = organizationGraphRepository.findByExternalId(externalId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_EXTERNALID_NOTFOUND)));
        List<TimeCareStaffDTO> timeCareStaffByWorkPlace = timeCareStaffDTOS.stream().filter(timeCareStaffDTO -> timeCareStaffDTO.getParentWorkPlaceId().equals(externalId)).collect(Collectors.toList());
        AccessGroup accessGroup = accessGroupRepository.findTaskGiverAccessGroup(organization.getId());
        for (TimeCareStaffDTO timeCareStaffDTO : timeCareStaffByWorkPlace) {
            StaffCreationDTO staffCreationDTO=ObjectMapperUtils.copyPropertiesByMapper(timeCareStaffDTO,StaffCreationDTO.class);
            staffCreationDTO.setAccessGroupId(accessGroup.getId());
            String email = (timeCareStaffDTO.getEmail() == null) ? timeCareStaffDTO.getFirstName() + KAIROS_EMAIL : timeCareStaffDTO.getEmail();
            staffCreationDTO.setPrivateEmail(email);
            staffCreationDTO.setExternalId(Long.valueOf(timeCareStaffDTO.getId()));
            createStaff(organization.getId(),staffCreationDTO);
        }
        return true;
    }
}
