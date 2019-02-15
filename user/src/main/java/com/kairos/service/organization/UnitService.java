package com.kairos.service.organization;

import com.kairos.dto.user.organization.*;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
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
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@Transactional
public class UnitService {
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private OrganizationService organizationService;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private Map<String, Object> parentOrgDefaultDetails(Organization parentOrg) {
        Map<String, Object> response = new HashMap<>(5);
        response.put("orgType", parentOrg.getOrganizationType());
        response.put("orgSubType", parentOrg.getOrganizationSubTypes());
        response.put("accountType", parentOrg.getAccountType());
        response.put("accessGroups",accessGroupService.getOrganizationManagementAccessGroups(parentOrg.getId()));
        //response.put("accessGroups", accountTypeGraphRepository.getAccessGroupsByAccountTypeId(parentOrg.getAccountType().getId()));
        response.put("businessTypes", parentOrg.getBusinessTypes());
        response.put("companyCategory", parentOrg.getCompanyCategory());
        response.put("level", parentOrg.getLevel());
        return response;
    }


    public Map<String, Object> getManageHierarchyData(long organizationId) {

        Organization organization = organizationGraphRepository.findOne(organizationId);

        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", organizationId);
        }

        Organization parentOrganization = organization.isParentOrganization()? organization : organizationService.fetchParentOrganization(organizationId);
        Long countryId = organizationGraphRepository.getCountryId(parentOrganization.getId());
        if (!Optional.ofNullable(countryId).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
        }

        Map<String, Object> response = new HashMap<>(2);
        response.put("parentInfo", parentOrgDefaultDetails(parentOrganization));
        List<OrganizationBasicResponse> units = organizationService.getOrganizationGdprAndWorkcenter(organizationId);
        response.put("units", units.size() != 0 ? units : Collections.emptyList());

        List<Map<String, Object>> groups = organizationGraphRepository.getGroups(organizationId);
        response.put("groups", groups.size() != 0 ? groups.get(0).get("groups") : Collections.emptyList());

        response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));

        OrganizationTypeAndSubType organizationTypes = organizationTypeGraphRepository.getOrganizationTypesForUnit(organizationId);

        List<BusinessType> businessTypes = businessTypeGraphRepository.findBusinessTypesByCountry(countryId);
        response.put("organizationTypes", organizationTypes);
        response.put("businessTypes", businessTypes);
        response.put("unitTypes", unitTypeGraphRepository.getAllUnitTypeOfCountry(countryId));
        response.put("companyTypes", CompanyType.getListOfCompanyType());
        response.put("companyUnitTypes", CompanyUnitType.getListOfCompanyUnitType());
        response.put("companyCategories", companyCategoryGraphRepository.findCompanyCategoriesByCountry(countryId));
        response.put("accessGroups", accessGroupService.getOrganizationManagementAccessGroups(organizationId));
        return response;
    }

    /**
     *
     * @param organizationBasicDTO
     * @param parentOrgaziationId is the ID of the Organization in which new Unit is added.
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public OrganizationBasicDTO onBoardOrganization(OrganizationBasicDTO organizationBasicDTO, Long parentOrgaziationId) throws InterruptedException, ExecutionException {
        if (organizationBasicDTO.getId() == null) {
            companyCreationService.addNewUnit(organizationBasicDTO, parentOrgaziationId);

        } else {
            companyCreationService.updateUnit(organizationBasicDTO, organizationBasicDTO.getId());
        }
        Country country = organizationGraphRepository.getCountry(parentOrgaziationId);
        companyCreationService.onBoardOrganization(country.getId(), organizationBasicDTO.getId(), parentOrgaziationId);
        organizationBasicDTO.setBoardingCompleted(true);
        return organizationBasicDTO;

    }

    public Map<String, Object> getEligibleUnitsForCtaAndWtaCreation(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 2);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO();
        if(isNotNull(organization.getOrganizationType()) && isNotEmpty(organization.getOrganizationSubTypes())){
            orgTypeAndSubTypeDTO.setOrganizationTypeId(organization.getOrganizationType().getId());
            orgTypeAndSubTypeDTO.setOrganizationTypeName(organization.getOrganizationType().getName());
            orgTypeAndSubTypeDTO.setOrganizationSubTypeId(organization.getOrganizationSubTypes().get(0).getId());
            orgTypeAndSubTypeDTO.setOrganizationSubTypeName(organization.getOrganizationSubTypes().get(0).getName());
        }else{
            logger.info("Organization Type and Organization Sub Type is not present for "+organization.getName());
        }


        OrganizationCommonDTO organizationCommonDTO;
        List<OrganizationCommonDTO> organizationCommonDTOS = new ArrayList<>();
        for(Organization unit : organization.getChildren()){
            organizationCommonDTO = new OrganizationCommonDTO();
            organizationCommonDTO.setId(unit.getId());
            organizationCommonDTO.setName(unit.getName());
            organizationCommonDTOS.add(organizationCommonDTO);
        }
        organizationCommonDTO = new OrganizationCommonDTO();
        organizationCommonDTO.setId(organization.getId());
        organizationCommonDTO.setName(organization.getName());
        organizationCommonDTOS.add(organizationCommonDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("eligibleUnits",organizationCommonDTOS);
        response.put("orgTypeAndSubTypeDTO",orgTypeAndSubTypeDTO);
        return response;
    }


}
