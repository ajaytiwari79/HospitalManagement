package com.kairos.service.organization;

import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.organization.UnitManagerDTO;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.company.CompanyValidationQueryResult;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
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
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.staff.StaffService;

import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.utils.FormatUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    private static final Logger logger = LoggerFactory.getLogger(CompanyCreationService.class);

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

        if (parent && CompanyType.COMPANY.equals(orgDetails.getCompanyType())) {
            if (!Optional.ofNullable(orgDetails.getAccountTypeId()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.select");
            }
            AccountType accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(), 0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.notFound");
            }
            organization.setAccountType(accountType);
            //accountType is Changed for parent organization We need to add this account type to child organization as well
            if (!organization.getChildren().isEmpty())

                organizationGraphRepository.updateAccountTypeOfChildOrganization(organization.getId(), accountType.getId());

        }
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

    // tab 1 in FE
    public OrganizationBasicResponse getOrganizationDetailsById(Long unitId) {
        return organizationGraphRepository.getOrganizationDetailsById(unitId);

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

    public UnitManagerDTO setUserInfoInOrganization(Long unitId, Organization organization, UnitManagerDTO unitManagerDTO, boolean boardingCompleted, boolean parentOrganization) {
        if (organization == null) {
            organization = organizationGraphRepository.findOne(unitId);
            boardingCompleted = organization.isBoardingCompleted();
        }
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        // set all properties
        if (boardingCompleted) {
            User user = userGraphRepository.findUserByCprNumber(unitManagerDTO.getCprNumber());
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
                byte isAnotherExist = userGraphRepository.validateUserEmailAndCPRExceptCurrentUser("(?)" + unitManagerDTO.getEmail(), unitManagerDTO.getCprNumber(), user.getId());
                if (isAnotherExist != 0) {
                    exceptionService.duplicateDataException("user already exist by email or cpr");
                }
                user.setEmail(unitManagerDTO.getEmail());
                user.setFirstName(unitManagerDTO.getEmail());
                user.setCprNumber(unitManagerDTO.getCprNumber());
                user.setFirstName(unitManagerDTO.getFirstName());
                user.setLastName(unitManagerDTO.getLastName());
                if (unitManagerDTO.getFirstName() != null || StringUtils.isEmpty(unitManagerDTO.getFirstName())) {
                    user.setPassword(new BCryptPasswordEncoder().encode(unitManagerDTO.getFirstName().trim() + "@kairos"));
                }
                userGraphRepository.save(user);
                if (unitManagerDTO.getAccessGroupId() != null) {
                    staffService.setAccessGroupInUserAccount(user, organization.getId(), unitManagerDTO.getAccessGroupId());
                }
            } else {
                // No user is found its first time so we need to validate email and CPR number
                //validate user email or name
                if (unitManagerDTO.getCprNumber() != null || unitManagerDTO.getEmail() != null) {
                    byte userBySameEmailOrCPR = userGraphRepository.findByEmailIgnoreCaseOrCprNumber("(?i)" + unitManagerDTO.getEmail(), unitManagerDTO.getCprNumber());
                    if (userBySameEmailOrCPR != 0) {
                        exceptionService.duplicateDataException("user already exist by email or cpr");
                    }
                }
                user = new User(unitManagerDTO.getCprNumber(), unitManagerDTO.getFirstName(), unitManagerDTO.getLastName(), unitManagerDTO.getEmail());
                user.setFirstName(unitManagerDTO.getEmail());
                if (unitManagerDTO.getFirstName() != null || StringUtils.isEmpty(unitManagerDTO.getFirstName())) {
                    user.setPassword(new BCryptPasswordEncoder().encode(unitManagerDTO.getFirstName().trim() + "@kairos"));
                }
                userGraphRepository.save(user);
                staffService.setUserAndEmployment(organization, user, unitManagerDTO.getAccessGroupId(), parentOrganization);

            }
        }
        return unitManagerDTO;
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
        if (organization.getOrganizationType() != null && !organization.getOrganizationType().getId().equals(organizationBasicDTO.getTypeId())) {
            Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(organizationBasicDTO.getTypeId());
            organization.setOrganizationType(organizationType.get());
        } else {
            Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(organizationBasicDTO.getTypeId());
            organization.setOrganizationType(organizationType.get());
        }
        if (parentOrganization != null) {
            organization.setOrganizationType(parentOrganization.getOrganizationType());
            organization.setAccountType(parentOrganization.getAccountType());
        }
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());

        if (organization.isParentOrganization()) {
            if (organizationBasicDTO.getLevelId() != null) {
                Level level = levelGraphRepository.findOne(organizationBasicDTO.getLevelId(), 0);
                organization.setLevel(level);
            }
        }

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
                .createOrganization();
        setDefaultDataFromParentOrganization(unit, parentOrganization, organizationBasicDTO);
        ContactAddress contactAddress = new ContactAddress();
        prepareAddress(contactAddress, organizationBasicDTO.getContactAddress());
        unit.setContactAddress(contactAddress);
        if (organizationBasicDTO.getUnitManager() != null
                && (organizationBasicDTO.getUnitManager().getEmail() != null || organizationBasicDTO.getUnitManager().getLastName() != null ||
                organizationBasicDTO.getUnitManager().getFirstName() != null || organizationBasicDTO.getUnitManager().getCprNumber() != null ||
                organizationBasicDTO.getUnitManager().getAccessGroupId() != null)) {
            setUserInfoInOrganization(null, unit, organizationBasicDTO.getUnitManager(), unit.isBoardingCompleted(), false);
        }
        //Assign Parent Organization's level to unit

        organizationGraphRepository.save(unit);
        organizationBasicDTO.setId(unit.getId());
        organizationBasicDTO.setKairosCompanyId(kairosCompanyId);
        if (organizationBasicDTO.getContactAddress() != null) {
            organizationBasicDTO.getContactAddress().setId(unit.getContactAddress().getId());
        }
        organizationGraphRepository.createChildOrganization(parentOrganizationId, unit.getId());
        return organizationBasicDTO;

    }

    private void setDefaultDataFromParentOrganization(Organization unit, Organization parentOrganization, OrganizationBasicDTO organizationBasicDTO) {
        unit.setOrganizationType(parentOrganization.getOrganizationType());
        unit.setAccountType(parentOrganization.getAccountType());
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());
        unit.setOrganizationSubTypes(organizationSubTypes);
        unit.setLevel(parentOrganization.getLevel());

    }

    public OrganizationBasicDTO updateUnit(OrganizationBasicDTO organizationBasicDTO, Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        updateOrganizationDetails(organization, organizationBasicDTO, false);
        setAddressInCompany(unitId, organizationBasicDTO.getContactAddress());
        setOrganizationTypeAndSubTypeInOrganization(organization, organizationBasicDTO, null);
        if (Optional.ofNullable(organizationBasicDTO.getUnitManager()).isPresent()) {
            setUserInfoInOrganization(unitId, organization, organizationBasicDTO.getUnitManager(), organization.isBoardingCompleted(), false);
        }
        organizationGraphRepository.save(organization);
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

    public boolean onBoardOrganization(Long countryId, Long organizationId) throws InterruptedException, ExecutionException {
        Organization organization = organizationGraphRepository.findOne(organizationId, 2);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", organizationId);
        }
        // If it has any error then it will throw exception
        // Here a list is created and organization with all its childrens are sent to function to validate weather any of organization
        //or parent has any missing required details

        List<Organization> organizations = new ArrayList<>();
        organizations.addAll(organization.getChildren());
        organizations.add(organization);
        validateBasicDetails(organizations, exceptionService);

        List<Long> unitIds = organization.getChildren().stream().map(Organization::getId).collect(Collectors.toList());
        unitIds.add(organizationId);

        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = userGraphRepository.getUnitManagerOfOrganization(unitIds);
        if (staffPersonalDetailDTOS.size() != unitIds.size()) {
            exceptionService.invalidRequestException("error.Organization.unitmanager.accessgroupid.notnull");
        }
        validateUserDetails(staffPersonalDetailDTOS, exceptionService);

        List<OrganizationContactAddress> organizationContactAddresses = organizationGraphRepository.getContactAddressOfOrganizations(unitIds);
        validateAddressDetails(organizationContactAddresses, exceptionService);

        organization.getChildren().forEach(currentOrg -> currentOrg.setBoardingCompleted(true));
        organization.setBoardingCompleted(true);
        organizationGraphRepository.save(organization);

        // if more than 2 default things needed make a  async service Please

        Map<Long, Long> countryAndOrgAccessGroupIdsMap = accessGroupService.createDefaultAccessGroups(organization);
        List<TimeSlot> timeSlots = timeSlotGraphRepository.findBySystemGeneratedTimeSlotsIsTrue();

        List<Long> orgSubTypeIds = organization.getOrganizationSubTypes().stream().map(orgSubType -> orgSubType.getId()).collect(Collectors.toList());
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(organization.getOrganizationType().getId(), orgSubTypeIds, organization.getCountry().getId());

        CompletableFuture<Boolean> hasUpdated = companyDefaultDataService
                .createDefaultDataForParentOrganization(organization, countryAndOrgAccessGroupIdsMap, timeSlots, orgTypeAndSubTypeDTO);
        CompletableFuture.allOf(hasUpdated).join();

        CompletableFuture<Boolean> createdInUnit = companyDefaultDataService
                .createDefaultDataInUnit(organization.getId(), organization.getChildren(), countryId, timeSlots);
        CompletableFuture.allOf(createdInUnit).join();

        return true;
    }

}
