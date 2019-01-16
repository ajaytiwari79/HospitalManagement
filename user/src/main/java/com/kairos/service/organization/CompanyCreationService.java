package com.kairos.service.organization;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.organization.UnitManagerDTO;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.company.CompanyValidationQueryResult;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CompanyCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.UnitTypeGraphRepository;
import com.kairos.persistence.repository.user.region.LevelGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.country.ReasonCodeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.FormatUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
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


    public OrganizationBasicDTO createCompany(OrganizationBasicDTO orgDetails, long countryId, Long organizationId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }
        if (StringUtils.isEmpty(orgDetails.getName()) || orgDetails.getName().length() < 3) {
            exceptionService.actionNotPermittedException("error.Organization.name.insuffient");
        }
        String kairosCompanyId = validateNameAndDesiredUrlOfOrganization(orgDetails);
        Organization organization = new OrganizationBuilder()
                .setIsParentOrganization(true)
                .setCountry(country)
                .setName(orgDetails.getName())
                .setCompanyType(orgDetails.getCompanyType())
                .setKairosCompanyId(kairosCompanyId)
                .setVatId(orgDetails.getVatId())
                .setTimeZone(ZoneId.of(TIMEZONE_UTC))
                .setShortCompanyName(orgDetails.getShortCompanyName())
                .setDesiredUrl(orgDetails.getDesiredUrl())
                .setDescription(orgDetails.getDescription())
                .createOrganization();


        if (CompanyType.COMPANY.equals(orgDetails.getCompanyType()) && Optional.ofNullable(orgDetails.getAccountTypeId()).isPresent()) {
            AccountType accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(), 0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.notFound");
            }
            organization.setAccountType(accountType);
            accessGroupService.createDefaultAccessGroups(organization, Collections.emptyList());
        }
        organization.setCompanyCategory(getCompanyCategory(orgDetails.getCompanyCategoryId()));
        organization.setBusinessTypes(getBusinessTypes(orgDetails.getBusinessTypeIds()));
        organization.setUnitType(getUnitType(orgDetails.getUnitTypeId()));
        organizationGraphRepository.save(organization);

        orgDetails.setId(organization.getId());
        orgDetails.setKairosCompanyId(kairosCompanyId);
        return orgDetails;
    }

    public OrganizationBasicDTO updateParentOrganization(OrganizationBasicDTO orgDetails, long organizationId) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 1);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", organizationId);

        }
        updateOrganizationDetails(organization, orgDetails, true);
        organizationGraphRepository.save(organization);
        orgDetails.setId(organization.getId());
        return orgDetails;
    }

    private void updateOrganizationDetails(Organization organization, OrganizationBasicDTO orgDetails, boolean parent) {
        if (orgDetails.getDesiredUrl() != null && !orgDetails.getDesiredUrl().trim().equalsIgnoreCase(organization.getDesiredUrl())) {
            Boolean orgExistWithUrl = organizationGraphRepository.checkOrgExistWithUrl(orgDetails.getDesiredUrl());
            if (orgExistWithUrl) {
                exceptionService.dataNotFoundByIdException("error.Organization.desiredUrl.duplicate", orgDetails.getDesiredUrl());
            }
        }
        if (!orgDetails.getName().equalsIgnoreCase(organization.getName())) {
            Boolean orgExistWithName = organizationGraphRepository.checkOrgExistWithName(orgDetails.getName());
            if (orgExistWithName) {
                exceptionService.dataNotFoundByIdException("error.Organization.name.duplicate", orgDetails.getName());
            }
        }
        organization.setName(orgDetails.getName());
        organization.setCompanyType(orgDetails.getCompanyType());
        organization.setVatId(orgDetails.getVatId());
        organization.setShortCompanyName(orgDetails.getShortCompanyName());
        organization.setDesiredUrl(orgDetails.getDesiredUrl());
        organization.setDescription(orgDetails.getDescription());
        organization.setWorkcentre(orgDetails.isWorkcentre());

        if (parent && CompanyType.COMPANY.equals(orgDetails.getCompanyType())) {
            if (!Optional.ofNullable(orgDetails.getAccountTypeId()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.select");
            }
            AccountType accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(), 0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.notFound");
            }
            //accountType is Changed for parent organization We need to add this account type to child organization as well
            if (organization.getAccountType() == null || !organization.getAccountType().getId().equals(orgDetails.getAccountTypeId())) {
                organization.setAccountType(accountType);
                List<Long> organizationIds = new ArrayList<>();
                organizationIds.addAll(organization.getChildren().stream().map(Organization::getId).collect(Collectors.toList()));
                organizationIds.add(organization.getId());
                accessGroupService.removeDefaultCopiedAccessGroup(organizationIds);
                if (!organization.getChildren().isEmpty()) {
                    organizationGraphRepository.updateAccountTypeOfChildOrganization(organization.getId(), accountType.getId());
                }
                accessGroupService.createDefaultAccessGroups(organization, organization.getChildren());
            }
        }
        setCompanyData(organization, orgDetails);
    }

    private void setCompanyData(Organization organization, OrganizationBasicDTO orgDetails) {
        organization.setCompanyCategory(getCompanyCategory(orgDetails.getCompanyCategoryId()));
        organization.setBusinessTypes(getBusinessTypes(orgDetails.getBusinessTypeIds()));
        organization.setUnitType(getUnitType(orgDetails.getUnitTypeId()));
    }

    private String validateNameAndDesiredUrlOfOrganization(OrganizationBasicDTO orgDetails) {
        CompanyValidationQueryResult orgExistWithUrl = organizationGraphRepository.checkOrgExistWithUrlOrName("(?i)" + orgDetails.getDesiredUrl(), "(?i)" + orgDetails.getName(), orgDetails.getName().substring(0, 3));
        if (orgExistWithUrl.getName()) {
            exceptionService.invalidRequestException("error.Organization.name.duplicate", orgDetails.getName());
        }
        if (orgDetails.getDesiredUrl() != null && orgExistWithUrl.getDesiredUrl()) {
            exceptionService.invalidRequestException("error.Organization.desiredUrl.duplicate", orgDetails.getDesiredUrl());
        }
        String kairosId;
        if (orgExistWithUrl.getKairosCompanyId() == null) {
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + KAI + ONE;
        } else {
            int lastSuffix = new Integer(orgExistWithUrl.getKairosCompanyId().substring(8, orgExistWithUrl.getKairosCompanyId().length()));
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + KAI + (++lastSuffix);
        }

        return kairosId;
    }

    public OrganizationBasicResponse getOrganizationDetailsById(Long unitId) {
        OrganizationBasicResponse organization = organizationGraphRepository.getOrganizationDetailsById(unitId);
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
            Organization organization = organizationGraphRepository.findOne(unitId);
            if (!Optional.ofNullable(organization).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
            }
            contactAddress = new ContactAddress();
            prepareAddress(contactAddress, addressDTO);
            organization.setContactAddress(contactAddress);
            organizationGraphRepository.save(organization);
            addressDTO.setId(contactAddress.getId());
        }
        return addressDTO;
    }

    public HashMap<String, Object> getAddressOfCompany(Long unitId) {
        HashMap<String, Object> orgBasicData = new HashMap<>();
        Map<String, Object> organizationContactAddress = organizationGraphRepository.getContactAddressOfParentOrganization(unitId);
        orgBasicData.put("address", organizationContactAddress);
        orgBasicData.put("municipalities", (organizationContactAddress.get("zipCodeId") == null) ? null : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData((long) organizationContactAddress.get("zipCodeId"))));
        return orgBasicData;
    }

    public UnitManagerDTO setUserInfoInOrganization(Long unitId, Organization organization, UnitManagerDTO unitManagerDTO, boolean boardingCompleted, boolean parentOrganization, boolean union) {
        if (organization == null) {
            organization = organizationGraphRepository.findOne(unitId);
            boardingCompleted = organization.isBoardingCompleted();
        }
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        // set all properties
        if (boardingCompleted) {
            User user = userGraphRepository.findUserByCprNumberOrEmail(unitManagerDTO.getCprNumber(), "(?)" + unitManagerDTO.getEmail());

            if (user != null) {
                user.setFirstName(unitManagerDTO.getFirstName());
                user.setLastName(unitManagerDTO.getLastName());
                userGraphRepository.save(user);
            } else {
                if (unitManagerDTO.getCprNumber() != null) {
                    StaffCreationDTO unitManagerData = new StaffCreationDTO(unitManagerDTO.getFirstName(), unitManagerDTO.getLastName(),
                            unitManagerDTO.getCprNumber(),
                            null, unitManagerDTO.getEmail(), null, unitManagerDTO.getEmail(), null, unitManagerDTO.getAccessGroupId());
                    staffService.createUnitManagerForNewOrganization(organization, unitManagerData);
                }

            }
        } else {
            if (unitManagerDTO.getCprNumber() != null && unitManagerDTO.getCprNumber().length() != 10) {
                exceptionService.actionNotPermittedException("message.cprNumber.size");
            }

            // user can fill any random property and we need to fetch
            User user = userGraphRepository.getUserOfOrganization(organization.getId());
            if (user != null) {
                byte anotherUserExistBySameEmailOrCPR = userGraphRepository.validateUserEmailAndCPRExceptCurrentUser("(?)" + unitManagerDTO.getEmail(), unitManagerDTO.getCprNumber(), user.getId());
                if (anotherUserExistBySameEmailOrCPR != 0) {
                    exceptionService.duplicateDataException("message.cprNumberEmail.notNull");
                }
                user.setEmail(unitManagerDTO.getEmail());
                user.setUserName(unitManagerDTO.getEmail());
                user.setCprNumber(unitManagerDTO.getCprNumber());
                user.setFirstName(unitManagerDTO.getFirstName());
                user.setLastName(unitManagerDTO.getLastName());

                setEncryptedPasswordAndAge(unitManagerDTO, user);
                userGraphRepository.save(user);
                if (unitManagerDTO.getAccessGroupId() != null) {
                    staffService.setAccessGroupInUserAccount(user, organization.getId(), unitManagerDTO.getAccessGroupId(), union);
                }
            } else {
                // No user is found its first time so we need to validate email and CPR number
                //validate user email or name
                if (unitManagerDTO.getCprNumber() != null || unitManagerDTO.getEmail() != null) {
                    User userByCprNumberOrEmail = userGraphRepository.findUserByCprNumberOrEmail(unitManagerDTO.getCprNumber(), unitManagerDTO.getEmail()!=null?"(?)" + unitManagerDTO.getEmail():null);
                    if (userByCprNumberOrEmail != null) {
                        user=userByCprNumberOrEmail;
                        reinitializeUserManagerDto(unitManagerDTO,user);
                        userGraphRepository.save(user);
                        staffService.setAccessGroupInUserAccount(user, organization.getId(), unitManagerDTO.getAccessGroupId(), union);
                    } else {
                        user = new User(unitManagerDTO.getCprNumber(), unitManagerDTO.getFirstName(), unitManagerDTO.getLastName(), unitManagerDTO.getEmail(), unitManagerDTO.getEmail());
                        setEncryptedPasswordAndAge(unitManagerDTO, user);
                        userGraphRepository.save(user);
                        staffService.setUserAndEmployment(organization, user, unitManagerDTO.getAccessGroupId(), parentOrganization, union);
                    }
                }
            }
        }
        return unitManagerDTO;
    }

    private void reinitializeUserManagerDto(UnitManagerDTO unitManagerDTO, User user){
        unitManagerDTO.setFirstName(user.getFirstName());
        unitManagerDTO.setLastName(user.getLastName());
        unitManagerDTO.setCprNumber(user.getCprNumber());
        unitManagerDTO.setEmail(user.getEmail());
    }

    //It checks null as well

    private void setEncryptedPasswordAndAge(UnitManagerDTO unitManagerDTO, User user) {
        if (StringUtils.isNotEmpty(unitManagerDTO.getFirstName())) {
            user.setPassword(new BCryptPasswordEncoder().encode(unitManagerDTO.getFirstName().trim() + "@kairos"));
        }
        user.setDateOfBirth(CPRUtil.fetchDateOfBirthFromCPR(unitManagerDTO.getCprNumber()));
        user.setGender(CPRUtil.getGenderFromCPRNumber(unitManagerDTO.getCprNumber()));
    }

    
    public StaffPersonalDetailDTO getUnitManagerOfOrganization(Long unitId) {
        return userGraphRepository.getUnitManagerOfOrganization(unitId);
    }

    public OrganizationBasicDTO setOrganizationTypeAndSubTypeInOrganization(OrganizationBasicDTO organizationBasicDTO, Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        setOrganizationTypeAndSubTypeInOrganization(organization, organizationBasicDTO, null);
        organizationGraphRepository.save(organization);
        return organizationBasicDTO;
    }

    private void setOrganizationTypeAndSubTypeInOrganization(Organization organization, OrganizationBasicDTO organizationBasicDTO, Organization parentOrganization) {
        Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(organizationBasicDTO.getTypeId());
        organization.setOrganizationType(organizationType.get());
        if (parentOrganization != null) {
            organization.setOrganizationType(parentOrganization.getOrganizationType());
            organization.setAccountType(parentOrganization.getAccountType());
        }
        if (organization.isParentOrganization() && organizationBasicDTO.getLevelId() != null) {
                Level level = levelGraphRepository.findOne(organizationBasicDTO.getLevelId(), 0);
                organization.setLevel(level);
        }
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());
        organization.setOrganizationSubTypes(organizationSubTypes);
    }

    public OrganizationTypeAndSubType getOrganizationTypeAndSubTypeByUnitId(Long unitId) {
        return organizationTypeGraphRepository.getOrganizationTypesForUnit(unitId);
    }


    public OrganizationBasicDTO addNewUnit(OrganizationBasicDTO organizationBasicDTO, Long parentOrganizationId) {

        Organization parentOrganization = organizationGraphRepository.findOne(parentOrganizationId);
        if (!Optional.ofNullable(parentOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", parentOrganizationId);
        }

        if (parentOrganization.getName().equalsIgnoreCase(organizationBasicDTO.getName())) {
            exceptionService.duplicateDataException("error.Organization.name.duplicate", organizationBasicDTO.getName());
        }

        String kairosCompanyId = validateNameAndDesiredUrlOfOrganization(organizationBasicDTO);
        Organization unit = new OrganizationBuilder()
                .setName(WordUtils.capitalize(organizationBasicDTO.getName()))
                .setDescription(organizationBasicDTO.getDescription())
                .setDesiredUrl(organizationBasicDTO.getDesiredUrl())
                .setShortCompanyName(organizationBasicDTO.getShortCompanyName())
                .setCompanyType(organizationBasicDTO.getCompanyType())
                .setVatId(organizationBasicDTO.getVatId())
                .setTimeZone(ZoneId.of(TIMEZONE_UTC))
                .setKairosCompanyId(kairosCompanyId)
                .setWorkcentre(organizationBasicDTO.isWorkcentre())
                .createOrganization();
        setDefaultDataFromParentOrganization(unit, parentOrganization, organizationBasicDTO);
        ContactAddress contactAddress = new ContactAddress();
        prepareAddress(contactAddress, organizationBasicDTO.getContactAddress());
        unit.setContactAddress(contactAddress);
        accessGroupService.linkParentOrganizationAccessGroup(unit, parentOrganization.getId());
        organizationGraphRepository.save(unit);
        organizationBasicDTO.setId(unit.getId());
        organizationBasicDTO.setKairosCompanyId(kairosCompanyId);
        if (organizationBasicDTO.getContactAddress() != null) {
            organizationBasicDTO.getContactAddress().setId(unit.getContactAddress().getId());
        }
        reasonCodeService.createDefalutDataForSubUnit(unit, parentOrganization.getId());
        //accessGroupService.createDefaultAccessGroups(unit, Collections.EMPTY_LIST);
        organizationGraphRepository.createChildOrganization(parentOrganizationId, unit.getId());
        setCompanyData(unit, organizationBasicDTO);
        if (doesUnitManagerInfoAvailable(organizationBasicDTO)) {
            setUserInfoInOrganization(null, unit, organizationBasicDTO.getUnitManager(), unit.isBoardingCompleted(), false, false);
        }
        //Assign Parent Organization's level to unit

        return organizationBasicDTO;

    }

    private boolean doesUnitManagerInfoAvailable(OrganizationBasicDTO organizationBasicDTO) {
        if (organizationBasicDTO.getUnitManager() != null
                && (organizationBasicDTO.getUnitManager().getEmail() != null || organizationBasicDTO.getUnitManager().getLastName() != null ||
                organizationBasicDTO.getUnitManager().getFirstName() != null || organizationBasicDTO.getUnitManager().getCprNumber() != null ||
                organizationBasicDTO.getUnitManager().getAccessGroupId() != null)) {
            return true;
        }
        return false;
    }

    private void setDefaultDataFromParentOrganization(Organization unit, Organization parentOrganization, OrganizationBasicDTO organizationBasicDTO) {
        unit.setOrganizationType(parentOrganization.getOrganizationType());
        unit.setAccountType(parentOrganization.getAccountType());
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());
        unit.setOrganizationSubTypes(organizationSubTypes);
        unit.setLevel(parentOrganization.getLevel());


    }

    public OrganizationBasicDTO updateUnit(OrganizationBasicDTO organizationBasicDTO, Long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        unit.setUnitType(getUnitType(organizationBasicDTO.getUnitTypeId()));
        updateOrganizationDetails(unit, organizationBasicDTO, false);
        setAddressInCompany(unitId, organizationBasicDTO.getContactAddress());
        setOrganizationTypeAndSubTypeInOrganization(unit, organizationBasicDTO, null);
        if (doesUnitManagerInfoAvailable(organizationBasicDTO)) {
            setUserInfoInOrganization(unitId, unit, organizationBasicDTO.getUnitManager(), unit.isBoardingCompleted(), false, false);
        }
        organizationGraphRepository.save(unit);
        return organizationBasicDTO;

    }

    private void prepareAddress(ContactAddress contactAddress, AddressDTO addressDTO) {
        if (addressDTO.getZipCodeId() != null) {
            ZipCode zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId(), 0);
            if (zipCode == null) {
                exceptionService.dataNotFoundByIdException("message.zipcode.notFound");
            }
            contactAddress.setCity(zipCode.getName());
            contactAddress.setZipCode(zipCode);
            contactAddress.setCity(zipCode.getName());
        }
        if (addressDTO.getMunicipalityId() != null) {
            Municipality municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId(), 0);
            if (municipality == null) {
                exceptionService.dataNotFoundByIdException("message.municipality.notFound");
            }
            contactAddress.setMunicipality(municipality);
            Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
            if (geographyData == null) {
                exceptionService.dataNotFoundByIdException("message.geographyData.notFound", municipality.getId());
            }
            contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
            contactAddress.setCountry(String.valueOf(geographyData.get("countryName")));
            contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));

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
                exceptionService.dataNotFoundByIdException("message.companyCategory.id.notFound", companyCategoryId);

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

    public QueryResult onBoardOrganization(Long countryId, Long organizationId, Long parentOrgaziationId) throws InterruptedException, ExecutionException {
        Organization organization = organizationGraphRepository.findOne(organizationId, 2);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", organizationId);
        }

        // If it has any error then it will throw exception
        // Here a list is created and organization with all its childrens are sent to function to validate weather any of organization
        //or parent has any missing required details

        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS;
        List<Long> unitIds = new ArrayList<>();
        List<Organization> organizations = new ArrayList<>();
        organizations.addAll(organization.getChildren());
        organizations.add(organization);
        validateBasicDetails(organizations, exceptionService);

        if (parentOrgaziationId != null && CollectionUtils.isNotEmpty(organization.getChildren())) {
            unitIds = organization.getChildren().stream().map(Organization::getId).collect(Collectors.toList());
            staffPersonalDetailDTOS = userGraphRepository.getUnitManagerOfOrganization(unitIds, organizationId);
            unitIds.add(organizationId);
            if (staffPersonalDetailDTOS.size() != unitIds.size()) {
                exceptionService.invalidRequestException("error.Organization.unitmanager.accessgroupid.notnull");
            }
            validateUserDetails(staffPersonalDetailDTOS, exceptionService);
            organization.getChildren().forEach(currentOrg -> currentOrg.setBoardingCompleted(true));
        } else {
            unitIds.add(organizationId);
            staffPersonalDetailDTOS = userGraphRepository.getUnitManagerOfOrganization(unitIds, organizationId);
        }

        List<OrganizationContactAddress> organizationContactAddresses = organizationGraphRepository.getContactAddressOfOrganizations(unitIds);
        validateAddressDetails(organizationContactAddresses, exceptionService);

        organization.setBoardingCompleted(true);
        organizationGraphRepository.save(organization);
        List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY,DayOfWeek.SUNDAY);
        SchedulerPanelDTO schedulerPanelDTO=new SchedulerPanelDTO(days, LocalTime.of(23,59),JobType.FUNCTIONAL, JobSubType.ATTENDANCE_SETTING,String.valueOf(organization.getTimeZone()));
        // create job for auto clock out and create realtime/draft shiftstate
        schedulerRestClient.publishRequest(Arrays.asList(schedulerPanelDTO), organization.getId(), true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
        addStaffsInChatServer(staffPersonalDetailDTOS.stream().map(StaffPersonalDetailDTO::getStaff).collect(Collectors.toList()));
        Map<Long, Long> countryAndOrgAccessGroupIdsMap = accessGroupService.findAllAccessGroupWithParentOfOrganization(organization.getId());
        List<TimeSlot> timeSlots = timeSlotGraphRepository.findBySystemGeneratedTimeSlotsIsTrue();

        List<Long> orgSubTypeIds = organization.getOrganizationSubTypes().stream().map(orgSubType -> orgSubType.getId()).collect(Collectors.toList());
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(organization.getOrganizationType().getId(), orgSubTypeIds,
                countryId,organization.isParentOrganization());
        if (parentOrgaziationId == null) {
            CompletableFuture<Boolean> hasUpdated = companyDefaultDataService
                    .createDefaultDataForParentOrganization(organization, countryAndOrgAccessGroupIdsMap, timeSlots, orgTypeAndSubTypeDTO, countryId);
            CompletableFuture.allOf(hasUpdated).join();

            CompletableFuture<Boolean> createdInUnit = companyDefaultDataService
                    .createDefaultDataInUnit(organization.getId(), organization.getChildren(), countryId, timeSlots);
            CompletableFuture.allOf(createdInUnit).join();


        } else {
            CompletableFuture<Boolean> createdInUnit = companyDefaultDataService
                    .createDefaultDataInUnit(parentOrgaziationId, Arrays.asList(organization), countryId, timeSlots);
            CompletableFuture.allOf(createdInUnit).join();
        }

        QueryResult organizationQueryResult = ObjectMapperUtils.copyPropertiesByMapper(organization, QueryResult.class);
        List<QueryResult> childQueryResults = new ArrayList<>();
        for (Organization childUnits : organization.getChildren()) {
            QueryResult childUnit = ObjectMapperUtils.copyPropertiesByMapper(childUnits, QueryResult.class);
            childQueryResults.add(childUnit);
        }
        organizationQueryResult.setChildren(childQueryResults);
        Map<Long, Long> unitAndStaffIdMap=staffPersonalDetailDTOS.stream().collect(Collectors.toMap(k->k.getOrganizationId(),v->v.getStaff().getId()));
        unitIds.stream().forEach(unitId->{
            if(unitAndStaffIdMap.containsKey(unitId)) {
                activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(unitAndStaffIdMap.get(unitId))), unitId);
            }
        });
        return treeStructureService.getTreeStructure(Arrays.asList(organizationQueryResult));
    }

    private void addStaffsInChatServer(List<Staff> staffList) {
        staffList.forEach(staff -> {
            staffService.addStaffInChatServer(staff);
        });
        staffGraphRepository.saveAll(staffList);
    }


}
