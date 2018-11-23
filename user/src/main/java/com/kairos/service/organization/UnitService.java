package com.kairos.service.organization;

import com.kairos.dto.user.organization.*;
import com.kairos.persistence.model.common.QueryResult;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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


    private Map<String, Object> parentOrgDefaultDetails(Organization parentOrg) {
        Map<String, Object> response = new HashMap<>(5);
        response.put("orgType", parentOrg.getOrganizationType());
        response.put("orgSubType", parentOrg.getOrganizationSubTypes());
        response.put("accountType", parentOrg.getAccountType());
        response.put("accessGroups", accountTypeGraphRepository.getAccessGroupsByAccountTypeId(parentOrg.getAccountType().getId()));
        response.put("businessTypes", parentOrg.getBusinessTypes());
        response.put("companyCategory", parentOrg.getCompanyCategory());
        response.put("level", parentOrg.getLevel());
        return response;
    }


    public Map<String, Object> getManageHierarchyData(long parentOrganizationId) {

        Organization organization = organizationGraphRepository.findOne(parentOrganizationId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", parentOrganizationId);
        }

        Country country = organizationGraphRepository.getCountry(parentOrganizationId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound");
        }
        Map<String, Object> response = new HashMap<>(2);
        response.put("parentInfo", parentOrgDefaultDetails(organization));
        List<OrganizationBasicResponse> units = organizationService.getOrganizationGdprAndWorkcenter(parentOrganizationId, null);
        response.put("units", units.size() != 0 ? units : Collections.emptyList());

        List<Map<String, Object>> groups = organizationGraphRepository.getGroups(parentOrganizationId);
        response.put("groups", groups.size() != 0 ? groups.get(0).get("groups") : Collections.emptyList());

        if (Optional.ofNullable(country.getId()).isPresent()) {
            response.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(country.getId())));
        }

        OrganizationTypeAndSubType organizationTypes = organizationTypeGraphRepository.getOrganizationTypesForUnit(parentOrganizationId);

        List<BusinessType> businessTypes = businessTypeGraphRepository.findBusinesTypesByCountry(country.getId());
        response.put("organizationTypes", organizationTypes);
        response.put("businessTypes", businessTypes);
        response.put("unitTypes", unitTypeGraphRepository.getAllUnitTypeOfCountry(country.getId()));
        response.put("companyTypes", CompanyType.getListOfCompanyType());
        response.put("companyUnitTypes", CompanyUnitType.getListOfCompanyUnitType());
        response.put("companyCategories", companyCategoryGraphRepository.findCompanyCategoriesByCountry(country.getId()));
        response.put("accessGroups", accessGroupService.getOrganizationAccessGroupsForUnitCreation(parentOrganizationId));
        return response;
    }

    public OrganizationBasicDTO onBoardOrganization(OrganizationBasicDTO organizationBasicDTO, Long unitId) throws InterruptedException, ExecutionException {
        if (organizationBasicDTO.getId() == null) {
            companyCreationService.addNewUnit(organizationBasicDTO, unitId);

        } else {
            companyCreationService.updateUnit(organizationBasicDTO, organizationBasicDTO.getId());
        }
        Country country = organizationGraphRepository.getCountry(unitId);
        companyCreationService.onBoardOrganization(country.getId(), organizationBasicDTO.getId(), unitId);
        organizationBasicDTO.setBoardingCompleted(true);
        return organizationBasicDTO;

    }

    public Map<String, Object> getEligibleUnitsForCtaAndWtaCreation(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 2);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO();
        orgTypeAndSubTypeDTO.setOrganizationTypeId(organization.getOrganizationType().getId());
        orgTypeAndSubTypeDTO.setOrganizationTypeName(organization.getOrganizationType().getName());
        orgTypeAndSubTypeDTO.setOrganizationSubTypeId(organization.getOrganizationSubTypes().get(0).getId());
        orgTypeAndSubTypeDTO.setOrganizationSubTypeName(organization.getOrganizationSubTypes().get(0).getName());

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
