package com.kairos.service.organization;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.organization.UnitManagerDTO;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.enums.user.UserType;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CompanyCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.UnitTypeGraphRepository;
import com.kairos.persistence.repository.user.region.LevelGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionGraphRepository;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.country.ReasonCodeService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.scheduler.UserSchedulerJobService;
import com.kairos.service.staff.StaffCreationService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.FormatUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.utils.validator.company.OrganizationDetailsValidator.*;

/**
 * CreatedBy vipulpandey on 17/8/18
 **/
@Service
@Transactional
public class CompanyCreationService {

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccountTypeGraphRepository accountTypeGraphRepository;
    @Inject
    private CompanyCategoryGraphRepository companyCategoryGraphRepository;
    @Inject
    private BusinessTypeGraphRepository businessTypeGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private LevelGraphRepository levelGraphRepository;
    @Inject
    private CompanyDefaultDataService companyDefaultDataService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject
    private UnitTypeGraphRepository unitTypeGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private ReasonCodeService reasonCodeService;
    @Inject
    private SchedulerServiceRestClient schedulerRestClient;
    @Inject
    private TreeStructureService treeStructureService;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private StaffCreationService staffCreationService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private TagService tagService;
    @Inject
    private UserSchedulerJobService userSchedulerJobService;

    public OrganizationBasicDTO createCompany(OrganizationBasicDTO orgDetails, long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        if (StringUtils.isEmpty(orgDetails.getName()) || orgDetails.getName().length() < 3) {
            exceptionService.actionNotPermittedException(ERROR_ORGANIZATION_NAME_INSUFFIENT);
        }
        String kairosCompanyId = validateNameAndDesiredUrlOfOrganization(orgDetails);
        Organization organization = new OrganizationBuilder().setIsParentOrganization(true).setCountry(country).setName(orgDetails.getName()).setCompanyType(orgDetails.getCompanyType()).setKairosCompanyId(kairosCompanyId).setVatId(orgDetails.getVatId()).setTimeZone(ZoneId.of(TIMEZONE_UTC)).setShortCompanyName(orgDetails.getShortCompanyName()).setDesiredUrl(orgDetails.getDesiredUrl()).setDescription(orgDetails.getDescription()).createOrganization();
        if (CompanyType.COMPANY.equals(orgDetails.getCompanyType()) && Optional.ofNullable(orgDetails.getAccountTypeId()).isPresent()) {
            AccountType accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(), 0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACCOUNTTYPE_NOTFOUND);
            }
            organization.setAccountType(accountType);
            accessGroupService.createDefaultAccessGroups(organization, Collections.emptyList());
        }
        organization.setCompanyCategory(getCompanyCategory(orgDetails.getCompanyCategoryId()));
        organization.setBusinessTypes(getBusinessTypes(orgDetails.getBusinessTypeIds()));
        organization.setUnitType(getUnitType(orgDetails.getUnitTypeId()));
        Organization hubOrOrgToBeLinked = organizationGraphRepository.findById(orgDetails.getHubId(), 0).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("message.hub.notFound", orgDetails.getHubId())));
        organizationGraphRepository.save(organization);
        //Linking organization to the selected hub/organization
        organizationGraphRepository.linkOrganizationToHub(organization.getId(), hubOrOrgToBeLinked.getId());
        orgDetails.setId(organization.getId());
        orgDetails.setKairosCompanyId(kairosCompanyId);
        return orgDetails;
    }

    public OrganizationBasicDTO updateParentOrganization(OrganizationBasicDTO orgDetails, long organizationId) {
        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, organizationId);

        }
        updateOrganizationDetails(organization, orgDetails, true);
        organizationGraphRepository.save(organization);
        orgDetails.setId(organization.getId());
        return orgDetails;
    }

    private <T extends OrganizationBaseEntity> void updateOrganizationDetails(T unit, OrganizationBasicDTO orgDetails, boolean parent) {
        validateDetails(unit, orgDetails);
        unit.setName(orgDetails.getName());
        unit.setVatId(orgDetails.getVatId());
        unit.setShortCompanyName(orgDetails.getShortCompanyName());
        unit.setDesiredUrl(orgDetails.getDesiredUrl());
        unit.setDescription(orgDetails.getDescription());
        if (unit instanceof Unit)
            ((Unit) unit).setWorkcentre(orgDetails.isWorkcentre());
        if (parent && CompanyType.COMPANY.equals(orgDetails.getCompanyType())) {
            if (!Optional.ofNullable(orgDetails.getAccountTypeId()).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACCOUNTTYPE_SELECT);
            }
            AccountType accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(), 0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACCOUNTTYPE_NOTFOUND);
            }
            //accountType is Changed for parent organization We need to add this account type to child organization as well
            addAccountTypeToChild(unit, orgDetails, accountType);
        }
        setCompanyData(unit, orgDetails);
    }

    private <T extends OrganizationBaseEntity> void addAccountTypeToChild(T unit, OrganizationBasicDTO orgDetails, AccountType accountType) {
        if (unit.getAccountType() == null || !unit.getAccountType().getId().equals(orgDetails.getAccountTypeId())) {
            unit.setAccountType(accountType);
            List<Long> organizationIds = new ArrayList<>();
            if (unit instanceof Organization) {
                organizationIds.addAll(((Organization) unit).getChildren().stream().map(Organization::getId).collect(Collectors.toList()));
                if (!((Organization) unit).getChildren().isEmpty()) {
                    unitGraphRepository.updateAccountTypeOfChildOrganization(unit.getId(), accountType.getId());
                }
            }
            organizationIds.add(unit.getId());
            accessGroupService.removeDefaultCopiedAccessGroup(organizationIds);

            if (unit instanceof Organization) {
                accessGroupService.createDefaultAccessGroups(((Organization) unit), ((Organization) unit).getUnits());
            }
        }
    }

    private <T extends OrganizationBaseEntity> void validateDetails(T unit, OrganizationBasicDTO orgDetails) {
        if (orgDetails.getDesiredUrl() != null && !orgDetails.getDesiredUrl().trim().equalsIgnoreCase(unit.getDesiredUrl())) {
            boolean orgExistWithUrl = unitGraphRepository.checkOrgExistWithUrl(orgDetails.getDesiredUrl());
            if (orgExistWithUrl) {
                exceptionService.dataNotFoundByIdException(ERROR_ORGANIZATION_DESIREDURL_DUPLICATE, orgDetails.getDesiredUrl());
            }
        }
        if (!orgDetails.getName().equalsIgnoreCase(unit.getName())) {
            boolean orgExistWithName = unitGraphRepository.checkOrgExistWithName(orgDetails.getName());
            if (orgExistWithName) {
                exceptionService.dataNotFoundByIdException(ERROR_ORGANIZATION_NAME_DUPLICATE, orgDetails.getName());
            }
        }
    }

    private <T extends OrganizationBaseEntity> void setCompanyData(T unit, OrganizationBasicDTO orgDetails) {
        unit.setCompanyCategory(getCompanyCategory(orgDetails.getCompanyCategoryId()));
        unit.setBusinessTypes(getBusinessTypes(orgDetails.getBusinessTypeIds()));
        unit.setUnitType(getUnitType(orgDetails.getUnitTypeId()));
    }

    private String validateNameAndDesiredUrlOfOrganization(OrganizationBasicDTO orgDetails) {
        if (unitGraphRepository.checkOrgExistWithName("(?i)" + orgDetails.getName())) {
            exceptionService.invalidRequestException(ERROR_ORGANIZATION_NAME_DUPLICATE, orgDetails.getName());
        }
        if (unitGraphRepository.checkOrgExistWithUrl("(?i)" + orgDetails.getDesiredUrl())) {
            exceptionService.invalidRequestException(ERROR_ORGANIZATION_DESIREDURL_DUPLICATE, orgDetails.getDesiredUrl());
        }
        String kairosId;
        String kairosCompanyId = unitGraphRepository.getkairosCompanyId(orgDetails.getName().substring(0, 3));
        if (isNull(kairosCompanyId)) {
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + KAI + ONE;
        } else {
            int lastSuffix = new Integer(kairosCompanyId.substring(8));
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + KAI + (++lastSuffix);
        }
        return kairosId;
    }

    public OrganizationBasicResponse getOrganizationDetailsById(Long unitId) {
        OrganizationBasicResponse organization = unitGraphRepository.getOrganizationDetailsById(unitId);
        organization.setUnitManager(getUnitManagerOfOrganization(unitId));
        return organization;
    }

    public AddressDTO setAddressInCompany(Long unitId, AddressDTO addressDTO) {
        ContactAddress contactAddress;
        if (addressDTO.getId() != null) {
            contactAddress = contactAddressGraphRepository.findOne(addressDTO.getId());
            prepareAddress(contactAddress, addressDTO);
            contactAddressGraphRepository.save(contactAddress);
        } else {
            OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findById(unitId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId)));

            contactAddress = new ContactAddress();
            prepareAddress(contactAddress, addressDTO);
            organizationBaseEntity.setContactAddress(contactAddress);
            organizationBaseRepository.save(organizationBaseEntity);
            addressDTO.setId(contactAddress.getId());
        }
        return addressDTO;
    }

    public Map<String, Object> getAddressOfCompany(Long unitId) {
        HashMap<String, Object> orgBasicData = new HashMap<>();
        ContactAddress address = contactAddressGraphRepository.getContactAddressOfOrganization(unitId);
        AddressDTO addressDTO = ObjectMapperUtils.copyPropertiesByMapper(address, AddressDTO.class);
        orgBasicData.put("address", addressDTO);
        orgBasicData.put("municipalities", (address == null || address.getZipCode() == null) ? null : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData(address.getZipCode().getId())));
        return orgBasicData;
    }

    public UnitManagerDTO setUserInfoInOrganization(Long unitId, OrganizationBaseEntity organizationBaseEntity, UnitManagerDTO unitManagerDTO) {
        if (organizationBaseEntity == null) {
            organizationBaseEntity = organizationBaseRepository.findById(unitId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId)));
        }
        Organization organization = organizationService.fetchParentOrganization(organizationBaseEntity.getId());
        // set all properties
        User user = userGraphRepository.findUserByCprNumberOrEmail(unitManagerDTO.getCprNumber(), "(?)" + unitManagerDTO.getEmail());
        if(user==null){
            StaffCreationDTO staffCreationDTO=ObjectMapperUtils.copyPropertiesByMapper(unitManagerDTO,StaffCreationDTO.class);
            staffCreationService.createStaff(unitId,staffCreationDTO);
        }
        else {
            setUserDetails(unitId, unitManagerDTO, organization, user, organizationBaseEntity);
        }
        return unitManagerDTO;
    }

    private void setUserDetails(Long unitId, UnitManagerDTO unitManagerDTO, Organization organization, User user, OrganizationBaseEntity organizationBaseEntity) {
        byte anotherUserExistBySameEmailOrCPR = userGraphRepository.validateUserEmailAndCPRExceptCurrentUser("(?)" + unitManagerDTO.getEmail(), unitManagerDTO.getCprNumber(), user.getId());
        if (anotherUserExistBySameEmailOrCPR != 0) {
            exceptionService.duplicateDataException(MESSAGE_CPRNUMBEREMAIL_NOTNULL);
        }
        user.setEmail(unitManagerDTO.getEmail());
        user.setUserName(unitManagerDTO.getUserName());
        user.setCprNumber(unitManagerDTO.getCprNumber());
        user.setFirstName(unitManagerDTO.getFirstName());
        user.setLastName(unitManagerDTO.getLastName());
        setEncryptedPasswordAndAge(unitManagerDTO, user);
        user.setUserNameUpdated(true);
        user.setLastSelectedOrganizationId(isNotNull(unitId) ? unitId : organization.getId());
        user.setUserType(UserType.USER_ACCOUNT);
        userGraphRepository.save(user);
//        if(unitManagerDTO.getAccessGroupId() != null) {
//            //setAccessGroupInUserAccount(user, organizationBaseEntity.getId(), unitManagerDTO.getAccessGroupId());
//        }
    }

//    private void setUserDetailsAndCreateStaff(Long unitId, UnitManagerDTO unitManagerDTO, Organization organization, User user) {
//        if(user != null) {
//            user.setFirstName(unitManagerDTO.getFirstName());
//            user.setLastName(unitManagerDTO.getLastName());
//            user.setUserName(unitManagerDTO.getUserName());
//            user.setLastSelectedOrganizationId(isNotNull(unitId) ? unitId : organization.getId());
//            user.setUserNameUpdated(true);
//            user.setUserType(UserType.USER_ACCOUNT);
//            userGraphRepository.save(user);
//        } else {
//            if(unitManagerDTO.getCprNumber() != null) {
//                StaffCreationDTO unitManagerData = new StaffCreationDTO(unitManagerDTO.getFirstName(), unitManagerDTO.getLastName(), unitManagerDTO.getCprNumber(), null, unitManagerDTO.getEmail(), null, unitManagerDTO.getUserName(), null, unitManagerDTO.getAccessGroupId());
//                staffCreationService.createStaff(organization.getId(), unitManagerData);
//            }
//
//        }
//    }

//    private void createUserAndValidateDetails(Long unitId, OrganizationBaseEntity organizationBaseEntity, UnitManagerDTO unitManagerDTO, boolean parentOrganization, boolean union, Organization organization) {
//        User user;
//        if(unitManagerDTO.getCprNumber() != null || unitManagerDTO.getEmail() != null) {
//            User userByCprNumberOrEmail = userGraphRepository.findUserByCprNumberOrEmail(unitManagerDTO.getCprNumber(), unitManagerDTO.getEmail() != null ? "(?)" + unitManagerDTO.getEmail() : null);
//            if(userByCprNumberOrEmail != null) {
//                user = userByCprNumberOrEmail;
//                reinitializeUserManagerDto(unitManagerDTO, user);
//            } else {
//                user = new User(unitManagerDTO.getCprNumber(), unitManagerDTO.getFirstName(), unitManagerDTO.getLastName(), unitManagerDTO.getEmail(), unitManagerDTO.getUserName(), true);
//                user.setUserType(UserType.USER_ACCOUNT);
//                setEncryptedPasswordAndAge(unitManagerDTO, user);
//            }
//            user.setLastSelectedOrganizationId(isNotNull(unitId) ? unitId : organization.getId());
//            userGraphRepository.save(user);
//            staffService.setUserAndPosition(organizationBaseEntity, user, unitManagerDTO.getAccessGroupId(), parentOrganization, union);
//        }
//    }

    private void reinitializeUserManagerDto(UnitManagerDTO unitManagerDTO, User user) {
        unitManagerDTO.setFirstName(user.getFirstName());
        unitManagerDTO.setLastName(user.getLastName());
        unitManagerDTO.setCprNumber(user.getCprNumber());
        unitManagerDTO.setEmail(user.getEmail());
        unitManagerDTO.setUserName(user.getUserName());
    }
    //It checks null as well

    private void setEncryptedPasswordAndAge(UnitManagerDTO unitManagerDTO, User user) {
        if (StringUtils.isNotEmpty(unitManagerDTO.getFirstName())) {
            user.setPassword(new BCryptPasswordEncoder().encode(unitManagerDTO.getFirstName().replaceAll("\\s+", "") + "@kairos"));
        }
        user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(unitManagerDTO.getCprNumber()));
        user.setGender(CPRUtil.getGenderFromCPRNumber(unitManagerDTO.getCprNumber()));
    }

    public StaffPersonalDetailQueryResult getUnitManagerOfOrganization(Long unitId) {
        return userGraphRepository.getUnitManagerOfOrganization(unitId);
    }

    public OrganizationBasicDTO setOrganizationTypeAndSubTypeInOrganization(OrganizationBasicDTO organizationBasicDTO, Long unitId) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findById(unitId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId)));
        setOrganizationTypeAndSubTypeInOrganization(organizationBaseEntity, organizationBasicDTO);
        organizationBaseRepository.save(organizationBaseEntity);
        return organizationBasicDTO;
    }

    private void setOrganizationTypeAndSubTypeInOrganization(OrganizationBaseEntity organizationBaseEntity, OrganizationBasicDTO organizationBasicDTO) {
        if (organizationBasicDTO.getTypeId() != null) {
            OrganizationType organizationType = organizationTypeGraphRepository.findOne(organizationBasicDTO.getTypeId());
            organizationBaseEntity.setOrganizationType(organizationType);
        }
        if (organizationBasicDTO.getLevelId() != null) {
            Level level = levelGraphRepository.findOne(organizationBasicDTO.getLevelId(), 0);
            organizationBaseEntity.setLevel(level);
        }
        if (organizationBasicDTO.getSubTypeId() != null) {
            List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());
            organizationBaseEntity.setOrganizationSubTypes(organizationSubTypes);
        }

    }

    public OrganizationTypeAndSubType getOrganizationTypeAndSubTypeByUnitId(Long unitId) {
        return organizationTypeGraphRepository.getOrganizationTypesForUnit(unitId);
    }

    public OrganizationBasicDTO addNewUnit(OrganizationBasicDTO organizationBasicDTO, Long parentOrganizationId) {
        Organization parentUnit = organizationGraphRepository.findOne(parentOrganizationId);
        validateDetails(organizationBasicDTO, parentOrganizationId, parentUnit);
        Country country = parentUnit.getCountry();
        String kairosCompanyId = validateNameAndDesiredUrlOfOrganization(organizationBasicDTO);
        Unit unit = new OrganizationBuilder().setName(WordUtils.capitalize(organizationBasicDTO.getName())).setDescription(organizationBasicDTO.getDescription())
                .setCountry(country).setDesiredUrl(organizationBasicDTO.getDesiredUrl()).setShortCompanyName(organizationBasicDTO.getShortCompanyName()).setWorkCentre(organizationBasicDTO.isWorkcentre()).setCompanyType(organizationBasicDTO.getCompanyType()).setVatId(organizationBasicDTO.getVatId()).setTimeZone(ZoneId.of(TIMEZONE_UTC)).setKairosCompanyId(kairosCompanyId).createUnit();
        setDefaultDataFromParentOrganization(unit, parentUnit, organizationBasicDTO);
        ContactAddress contactAddress = new ContactAddress();
        prepareAddress(contactAddress, organizationBasicDTO.getContactAddress());
        unit.setContactAddress(contactAddress);
        accessGroupService.linkParentOrganizationAccessGroup(unit, parentUnit.getId());
        unitGraphRepository.save(unit);
        organizationBasicDTO.setId(unit.getId());
        organizationBasicDTO.setKairosCompanyId(kairosCompanyId);
        if (organizationBasicDTO.getContactAddress() != null) {
            organizationBasicDTO.getContactAddress().setId(unit.getContactAddress().getId());
        }
        reasonCodeService.createReasonCodeForUnit(unit, country.getId());
        unitGraphRepository.createChildOrganization(parentOrganizationId, unit.getId());
        setCompanyData(unit, organizationBasicDTO);
        if (doesUnitManagerInfoAvailable(organizationBasicDTO)) {
            setUserInfoInOrganization(null, unit, organizationBasicDTO.getUnitManager());
        }
        //Assign Parent Organization's level to unit
        return organizationBasicDTO;
    }

    private void validateDetails(OrganizationBasicDTO organizationBasicDTO, Long parentOrganizationId, Organization parentUnit) {
        if (!Optional.ofNullable(parentUnit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, parentOrganizationId);
        }
        if (parentUnit.getName().equalsIgnoreCase(organizationBasicDTO.getName())) {
            exceptionService.duplicateDataException(ERROR_ORGANIZATION_NAME_DUPLICATE, organizationBasicDTO.getName());
        }
        if (unitGraphRepository.existsByName("(?i)" + organizationBasicDTO.getName())) {
            exceptionService.duplicateDataException(ERROR_ORGANIZATION_NAME_DUPLICATE, organizationBasicDTO.getName());
        }
        if (organizationBasicDTO.getName().length() < 3) {
            exceptionService.actionNotPermittedException(ERROR_UNIT_NAME_INSUFFIENT);
        }
    }

    private boolean doesUnitManagerInfoAvailable(OrganizationBasicDTO organizationBasicDTO) {
        boolean isExist = false;
        if (organizationBasicDTO.getUnitManager() != null && (organizationBasicDTO.getUnitManager().getEmail() != null || organizationBasicDTO.getUnitManager().getLastName() != null || organizationBasicDTO.getUnitManager().getFirstName() != null || organizationBasicDTO.getUnitManager().getCprNumber() != null || organizationBasicDTO.getUnitManager().getAccessGroupId() != null)) {
            isExist = true;
        }
        return isExist;
    }

    private void setDefaultDataFromParentOrganization(Unit unit, Organization parentUnit, OrganizationBasicDTO organizationBasicDTO) {
        unit.setOrganizationType(parentUnit.getOrganizationType());
        unit.setAccountType(parentUnit.getAccountType());
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());
        unit.setOrganizationSubTypes(organizationSubTypes);
        unit.setLevel(parentUnit.getLevel());

    }

    public OrganizationBasicDTO updateUnit(OrganizationBasicDTO organizationBasicDTO, Long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        unit.setUnitType(getUnitType(organizationBasicDTO.getUnitTypeId()));
        updateOrganizationDetails(unit, organizationBasicDTO, false);
        setAddressInCompany(unitId, organizationBasicDTO.getContactAddress());
        setOrganizationTypeAndSubTypeInOrganization(unit, organizationBasicDTO);
        if (doesUnitManagerInfoAvailable(organizationBasicDTO)) {
            setUserInfoInOrganization(unitId, unit, organizationBasicDTO.getUnitManager());
        }
        unitGraphRepository.save(unit);
        return organizationBasicDTO;

    }

    private void prepareAddress(ContactAddress contactAddress, AddressDTO addressDTO) {
        if (addressDTO.getZipCode() != null) {
            ZipCode zipCode = zipCodeGraphRepository.findById(addressDTO.getZipCode().getId(), 0).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ZIPCODE_NOTFOUND)));
            contactAddress.setCity(zipCode.getName());
            contactAddress.setZipCode(zipCode);
        }
        if (addressDTO.getMunicipality() != null) {
            if (isNull(addressDTO.getMunicipality().getId())) {
                exceptionService.dataNotFoundByIdException(MESSAGE_MUNICIPALITY_NOTFOUND);
            }
            Municipality municipality = municipalityGraphRepository.findById(addressDTO.getMunicipality().getId(), 0).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_MUNICIPALITY_NOTFOUND)));
            contactAddress.setMunicipality(municipality);
            Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
            if (geographyData != null) {
                contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
                contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
                contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));
            }
        }
        contactAddress.setCity(addressDTO.getCity());
        contactAddress.setFloorNumber(addressDTO.getFloorNumber());
        contactAddress.setHouseNumber(addressDTO.getHouseNumber());
        contactAddress.setStreet(addressDTO.getStreet());
        contactAddress.setVerifiedByVisitour(false);

    }

    private List<BusinessType> getBusinessTypes(List<Long> businessTypeIds) {
        List<BusinessType> businessTypes = new ArrayList<>();
        if (!businessTypeIds.isEmpty()) {
            businessTypes = businessTypeGraphRepository.findByIdIn(businessTypeIds);
        }
        return businessTypes;
    }

    private CompanyCategory getCompanyCategory(Long companyCategoryId) {
        CompanyCategory companyCategory = null;
        if (companyCategoryId != null) {
            companyCategory = companyCategoryGraphRepository.findOne(companyCategoryId, 0);
            if (!Optional.ofNullable(companyCategory).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_COMPANYCATEGORY_ID_NOTFOUND, companyCategoryId);

            }
        }
        return companyCategory;
    }

    private UnitType getUnitType(Long unitTypeId) {
        UnitType unitType = null;
        if (unitTypeId != null) {
            unitType = unitTypeGraphRepository.findOne(unitTypeId, 0);
            if (!Optional.ofNullable(unitType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.unitType.id.notFound", unitTypeId);

            }
        }
        return unitType;
    }

    public QueryResult onBoardOrganization(Long countryId, Long organizationId, Long parentOrganizationId) {
        OrganizationBaseEntity organization = organizationBaseRepository.findById(organizationId, 2).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, organizationId)));
        Organization parent = prepareHierarchy(organization);
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults;
        List<OrganizationBaseEntity> units = getOrganizationBaseEntities(organization, parent);
        validateBasicDetails(units, exceptionService);
        List<Long> unitIds = getAllUnitIds(organizationId, parentOrganizationId, parent);
        staffPersonalDetailQueryResults = userGraphRepository.getUnitManagerOfOrganization(unitIds, parent.getId());
        if (ObjectUtils.isCollectionEmpty(staffPersonalDetailQueryResults)) {
            exceptionService.invalidRequestException(ERROR_USER_DETAILS_MISSING);
        }
        validateUserDetails(staffPersonalDetailQueryResults, exceptionService);
        List<OrganizationContactAddress> organizationContactAddresses = unitGraphRepository.getContactAddressOfOrganizations(unitIds);
        validateAddressDetails(organizationContactAddresses, exceptionService);
        organization.setBoardingCompleted(true);
        organization.setTags(tagService.getCountryTagByOrgSubTypes(countryId, organization.getOrganizationSubTypes().stream().map(UserBaseEntity::getId).collect(Collectors.toList())));
        organizationBaseRepository.save(organization);
        userSchedulerJobService.createJobForAddPlanningPeriod(organization);
        addStaffsInChatServer(staffPersonalDetailQueryResults.stream().map(StaffPersonalDetailQueryResult::getStaff).collect(Collectors.toList()));
        Map<Long, Long> countryAndOrgAccessGroupIdsMap = accessGroupService.findAllAccessGroupWithParentOfOrganization(parent.getId());
        List<TimeSlot> timeSlots = timeSlotGraphRepository.findBySystemGeneratedTimeSlotsIsTrue();
        List<Long> orgSubTypeIds = organization.getOrganizationSubTypes().stream().map(UserBaseEntity::getId).collect(Collectors.toList());
        List<Long> employmentIds = employmentTypeGraphRepository.getEmploymentTypeIdsByCountryId(countryId);
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(organization.getOrganizationType().getId(), orgSubTypeIds, countryId, organization instanceof Organization, employmentIds);
        createDefaultDataForOrganizationAndUnit(countryId, parentOrganizationId, organization, parent, countryAndOrgAccessGroupIdsMap, timeSlots, orgTypeAndSubTypeDTO);
        QueryResult organizationQueryResult = generateOrgHierarchyQueryResult(organization, parent);
        createDefaultKPISettings(staffPersonalDetailQueryResults, unitIds);
        organizationQueryResult.setHubId(unitGraphRepository.getHubIdByOrganizationId(organizationId));
        return treeStructureService.getTreeStructure(Arrays.asList(organizationQueryResult));
    }

    private QueryResult generateOrgHierarchyQueryResult(OrganizationBaseEntity organization, Organization parent) {
        QueryResult organizationQueryResult = ObjectMapperUtils.copyPropertiesByMapper(organization, QueryResult.class);
        List<QueryResult> childQueryResults = new ArrayList<>();
        for (Unit childUnits : parent.getUnits()) {
            QueryResult childUnit = ObjectMapperUtils.copyPropertiesByMapper(childUnits, QueryResult.class);
            childQueryResults.add(childUnit);
        }
        organizationQueryResult.setChildren(childQueryResults);
        return organizationQueryResult;
    }

    private List<OrganizationBaseEntity> getOrganizationBaseEntities(OrganizationBaseEntity organization, Organization parent) {
        List<OrganizationBaseEntity> units = new ArrayList<>();
        units.add(organization);
        if (organization instanceof Organization) {
            units.addAll(parent.getUnits());
        }
        return units;
    }

    private void createDefaultDataForOrganizationAndUnit(Long countryId, Long parentOrganizationId, OrganizationBaseEntity organization, Organization parent, Map<Long, Long> countryAndOrgAccessGroupIdsMap, List<TimeSlot> timeSlots, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {
        if (parentOrganizationId == null) {
            companyDefaultDataService.createDefaultDataForParentOrganization(parent, countryAndOrgAccessGroupIdsMap, timeSlots, orgTypeAndSubTypeDTO, countryId);
            companyDefaultDataService.createDefaultDataInUnit(organization.getId(), parent.getUnits(), countryId, timeSlots);
        } else {
            companyDefaultDataService.createDefaultDataInUnit(parentOrganizationId, Arrays.asList((Unit) organization), countryId, timeSlots);
        }
    }

    private void createDefaultKPISettings(List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults, List<Long> unitIds) {
        Map<Long, Long> unitAndStaffIdMap = staffPersonalDetailQueryResults.stream().filter(distinctByKey(StaffPersonalDetailQueryResult::getOrganizationId)).collect(Collectors.toMap(staffPersonalDetailDTO -> staffPersonalDetailDTO.getOrganizationId(), v -> v.getStaff().getId()));
        unitIds.stream().forEach(unitId -> {
            if (unitAndStaffIdMap.containsKey(unitId)) {
                activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(unitAndStaffIdMap.get(unitId))), unitId);
            }
        });
    }

    private List<Long> getAllUnitIds(Long organizationId, Long parentOrganizationId, Organization parent) {
        List<Long> unitIds = new ArrayList<>();
        if (parentOrganizationId == null && CollectionUtils.isNotEmpty(parent.getUnits())) {
            unitIds = parent.getUnits().stream().map(Unit::getId).collect(Collectors.toList());
            unitIds.add(organizationId);
            parent.getUnits().forEach(currentOrg -> currentOrg.setBoardingCompleted(true));
        } else {
            unitIds.add(organizationId);
        }
        return unitIds;
    }

    private Organization prepareHierarchy(OrganizationBaseEntity organization) {
        Organization parent = organizationService.fetchParentOrganization(organization.getId());
        List<Long> allUnitIds = parent.getUnits().stream().map(Unit::getId).collect(Collectors.toList());
        List<Unit> unitList = unitGraphRepository.findAllById(allUnitIds);
        parent.setUnits(unitList);
        return parent;
    }

    private void addStaffsInChatServer(List<Staff> staffList) {
        staffList.forEach(staff -> staffService.addStaffInChatServer(staff));
        staffGraphRepository.saveAll(staffList);
    }

}
