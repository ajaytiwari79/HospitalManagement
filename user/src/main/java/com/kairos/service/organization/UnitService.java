package com.kairos.service.organization;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.organization.*;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.filter.FilterSelectionQueryResult;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CompanyCategoryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.UnitTypeGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_ORGANIZATION_ID_NOTFOUND;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@Transactional
public class UnitService {
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private BusinessTypeGraphRepository businessTypeGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private CompanyCategoryGraphRepository companyCategoryGraphRepository;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private AccountTypeGraphRepository accountTypeGraphRepository;
    @Inject
    private UnitTypeGraphRepository unitTypeGraphRepository;
    @Inject
    private CompanyCreationService companyCreationService;

    private final Logger logger = LoggerFactory.getLogger(UnitService.class);


    private Map<String, Object> parentOrgDefaultDetails(Organization parentOrg) {
        Map<String, Object> response = new HashMap<>(5);
        response.put("orgType", parentOrg.getOrganizationType());
        response.put("orgSubType", parentOrg.getOrganizationSubTypes());
        response.put("accountType", parentOrg.getAccountType());
        response.put("accessGroups", accessGroupService.getOrganizationManagementAccessGroups(parentOrg.getId(), AccessGroupRole.MANAGEMENT));
        response.put("businessTypes", parentOrg.getBusinessTypes());
        response.put("companyCategory", parentOrg.getCompanyCategory());
        response.put("level", parentOrg.getLevel());
        return response;
    }


    public Map<String, Object> getManageHierarchyData(long organizationId) {

        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, organizationId);
        }
        Long countryId = organization.getCountry().getId();

        Map<String, Object> response = new HashMap<>(2);
        response.put("parentInfo", parentOrgDefaultDetails(organization));
        List<OrganizationBasicResponse> units = organizationService.getOrganizationGdprAndWorkcenter(organizationId);
        response.put("units", units.size() != 0 ? units : Collections.emptyList());

        response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));

        OrganizationTypeAndSubType organizationTypes = organizationTypeGraphRepository.getOrganizationTypesForUnit(organizationId);

        List<BusinessType> businessTypes = businessTypeGraphRepository.findBusinessTypesByCountry(countryId);
        response.put("organizationTypes", organizationTypes);
        response.put("businessTypes", businessTypes);
        response.put("unitTypes", unitTypeGraphRepository.getAllUnitTypeOfCountry(countryId));
        response.put("companyTypes", CompanyType.getListOfCompanyType());
        response.put("companyUnitTypes", CompanyUnitType.getListOfCompanyUnitType());
        response.put("companyCategories", companyCategoryGraphRepository.findCompanyCategoriesByCountry(countryId));
        response.put("accessGroups", accessGroupService.getOrganizationManagementAccessGroups(organizationId, AccessGroupRole.MANAGEMENT));
        return response;
    }

    /**
     * @param organizationBasicDTO
     * @param parentOrgaziationId  is the ID of the Organization in which new Unit is added.
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public OrganizationBasicDTO onBoardOrganization(OrganizationBasicDTO organizationBasicDTO, Long parentOrgaziationId) {
        if (organizationBasicDTO.getId() == null) {
            companyCreationService.addNewUnit(organizationBasicDTO, parentOrgaziationId);
        } else {
            companyCreationService.updateUnit(organizationBasicDTO, organizationBasicDTO.getId());
        }
        Country country = countryGraphRepository.getCountryByUnitId(parentOrgaziationId);
        companyCreationService.onBoardOrganization(country.getId(), organizationBasicDTO.getId(), parentOrgaziationId);
        organizationBasicDTO.setBoardingCompleted(true);
        return organizationBasicDTO;

    }

    public Map<String, Object> getEligibleUnitsForCtaAndWtaCreation(Long unitId) {
        OrganizationBaseEntity organization = organizationBaseRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO();
        if (isNotNull(organization.getOrganizationType()) && isNotEmpty(organization.getOrganizationSubTypes())) {
            orgTypeAndSubTypeDTO.setOrganizationTypeId(organization.getOrganizationType().getId());
            orgTypeAndSubTypeDTO.setOrganizationTypeName(organization.getOrganizationType().getName());
            orgTypeAndSubTypeDTO.setOrganizationSubTypeId(organization.getOrganizationSubTypes().get(0).getId());
            orgTypeAndSubTypeDTO.setOrganizationSubTypeName(organization.getOrganizationSubTypes().get(0).getName());
        } else {
            logger.info("Organization Type and Organization Sub Type is not present for {} " , organization.getName());
        }


        List<OrganizationCommonDTO> organizationCommonDTOS = new ArrayList<>();
        if (organization instanceof Organization) {
            organization=organizationGraphRepository.findOne(unitId);
            for (Unit unit : ((Organization) organization).getUnits()) {
                if(unit.isWorkcentre()) {
                    organizationCommonDTOS.add(new OrganizationCommonDTO(unit.getId(), unit.getName()));
                }
            }
        }else {
            organizationCommonDTOS.add(new OrganizationCommonDTO(organization.getId(),organization.getName()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("eligibleUnits", organizationCommonDTOS);
        response.put("orgTypeAndSubTypeDTO", orgTypeAndSubTypeDTO);
        return response;
    }

    public boolean isUnit(Long organisationId){
        return organizationBaseRepository.findOne(organisationId) instanceof Unit;
    }

    public List<FilterSelectionQueryResult> getAllAccessGroupByUnitIdForFilter(Long unitId){
        List<FilterSelectionQueryResult> accessGroupFilters = new ArrayList<>();
        Unit unit = unitGraphRepository.findOne(unitId);
        if(isNotNull(unit)){
            unit.getAccessGroups().forEach(accessGroup -> accessGroupFilters.add(new FilterSelectionQueryResult(accessGroup.getId().toString(), accessGroup.getName())));
        }
        return accessGroupFilters;
    }

}
