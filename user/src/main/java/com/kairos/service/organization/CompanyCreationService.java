package com.kairos.service.organization;

import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBuilder;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.company.CompanyValidationQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CompanyCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.persistence.repository.user.region.LevelGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.staff.StaffService;
import com.kairos.user.organization.AddressDTO;
import com.kairos.user.organization.CompanyType;
import com.kairos.user.organization.OrganizationBasicDTO;
import com.kairos.user.organization.UnitManagerDTO;
import com.kairos.user.staff.staff.StaffCreationDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZoneId;
import java.util.*;

import static com.kairos.constants.AppConstants.*;

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
    @Inject private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject private LevelGraphRepository levelGraphRepository;

    public OrganizationBasicDTO createCompany(OrganizationBasicDTO orgDetails, long countryId, Long organizationId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        }

        CompanyValidationQueryResult orgExistWithUrl = organizationGraphRepository.checkOrgExistWithUrlOrUrl("(?i)" + orgDetails.getDesiredUrl(), "(?i)" + orgDetails.getName(), orgDetails.getName().substring(0, 3));

        if (orgExistWithUrl.getName()) {
            exceptionService.invalidRequestException("error.Organization.name.duplicate", orgDetails.getName());
        }
        if (orgDetails.getDesiredUrl() != null && orgExistWithUrl.getDesiredUrl()) {
            exceptionService.invalidRequestException("error.Organization.desiredUrl.duplicate", orgDetails.getDesiredUrl());
        }

        String kairosId;
        if (orgExistWithUrl.getKairosId() == null) {
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + ONE;
        } else {
            int lastSuffix = new Integer(orgExistWithUrl.getKairosId().substring(4, orgExistWithUrl.getKairosId().length()));
            kairosId = StringUtils.upperCase(orgDetails.getName().substring(0, 3)) + HYPHEN + (++lastSuffix);
        }

        Organization organization = new OrganizationBuilder()
                .setIsParentOrganization(true)
                .setCountry(country)
                .setName(orgDetails.getName())
                .setCompanyType(orgDetails.getCompanyType())
                .setKairosId(kairosId)
                .setVatId(orgDetails.getVatId())
                .setTimeZone(ZoneId.of(TIMEZONE_UTC))
                .setShortCompanyName(orgDetails.getShortCompanyName())
                .setDesiredUrl(orgDetails.getDesiredUrl())
                .setDescription(orgDetails.getDescription())
                .createOrganization();


        if (CompanyType.COMPANY.equals(orgDetails.getCompanyType())) {
            AccountType accountType = accountTypeGraphRepository.findOne(orgDetails.getAccountTypeId(), 0);
            if (!Optional.ofNullable(accountType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.accountType.notFound");
            }
            organization.setAccountType(accountType);
        }

        organization.setCompanyCategory(getCompanyCategory(orgDetails.getCompanyCategoryId()));
        organization.setBusinessTypes(getBusinessTypes(orgDetails.getBusinessTypeIds()));
        organizationGraphRepository.save(organization);

        orgDetails.setId(organization.getId());
        return orgDetails;
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

    public ContactAddress getAddressOfCompany(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        return organization.getContactAddress();
    }

    public UnitManagerDTO setUserInfoInOrganization(Long unitId, UnitManagerDTO unitManagerDTO) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        User user = userGraphRepository.findUserByCprNumber(unitManagerDTO.getCprNumber());
        if (user != null) {
            user.setFirstName(unitManagerDTO.getFirstName());
            user.setLastName(unitManagerDTO.getLastName());
            userGraphRepository.save(user);
        } else {
            StaffCreationDTO unitManagerData = new StaffCreationDTO(unitManagerDTO.getFirstName(), unitManagerDTO.getLastName(),
                    unitManagerDTO.getCprNumber(),
                    null, unitManagerDTO.getEmail(), null, unitManagerDTO.getEmail(), null, unitManagerDTO.getAccessGroupId());
            staffService.createUnitManagerForNewOrganization(organization, unitManagerData);
        }
        return unitManagerDTO;
    }

    public StaffPersonalDetailDTO getUnitManagerOfOrganization(Long unitId) {
        return userGraphRepository.getUnitManagerOfOrganization(unitId);
    }
    public OrganizationBasicDTO setOrganizationTypeAndSubTypeInOrganization(OrganizationBasicDTO organizationBasicDTO,Long unitId){
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }

        Optional<OrganizationType> organizationType = organizationTypeGraphRepository.findById(organizationBasicDTO.getTypeId());
        List<OrganizationType> organizationSubTypes = organizationTypeGraphRepository.findByIdIn(organizationBasicDTO.getSubTypeId());
        if (organizationBasicDTO.getLevelId()!=null) {
            Level level = levelGraphRepository.findOne(organizationBasicDTO.getLevelId(), 0);
            organization.setLevel(level);
        }
        organization.setOrganizationType(organizationType.get());
        organization.setOrganizationSubTypes(organizationSubTypes);
        organizationGraphRepository.save(organization);
        return organizationBasicDTO;
    }
    Map<String,Object> getOrganizationTypeAndSubTypeByUnitId(Long unitId){
        return organizationTypeGraphRepository.getOrganizationTypesForUnit(unitId);
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

}
