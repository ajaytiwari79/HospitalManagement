package com.kairos.service.organization;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationQueryResult;
import com.kairos.persistence.model.organization.union.UnionQueryWrapper;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.CompanyType;
import com.kairos.dto.user.organization.CompanyUnitType;
import com.kairos.utils.DateUtil;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vipul on 13/2/18.
 */
@Service
@Transactional
public class UnionService {
    private final Logger logger = LoggerFactory.getLogger(UnionService.class);
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private AccessGroupService accessGroupService;

    public UnionQueryWrapper getAllUnionOfCountry(Long countryId) {
        UnionQueryWrapper unionQueryWrapper = new UnionQueryWrapper();

        OrganizationQueryResult organizationQueryResult = organizationGraphRepository.getAllUnionOfCountry(countryId);
        OrganizationCreationData organizationCreationData = organizationGraphRepository.getOrganizationCreationData(countryId);
        List<Map<String, Object>> zipCodes = FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
        organizationCreationData.setZipCodes(zipCodes);
        organizationCreationData.setCompanyTypes(CompanyType.getListOfCompanyType());
        organizationCreationData.setCompanyUnitTypes(CompanyUnitType.getListOfCompanyUnitType());
        organizationCreationData.setAccessGroups(accessGroupService.getCountryAccessGroupsForOrganizationCreation(countryId));
        List<Map<String, Object>> orgData = new ArrayList<>();
        for (Map<String, Object> organizationData : organizationQueryResult.getOrganizations()) {
            HashMap<String, Object> orgBasicData = new HashMap<>();
            orgBasicData.put("orgData", organizationData);
            Map<String, Object> address = (Map<String, Object>) organizationData.get("contactAddress");
            orgBasicData.put("municipalities", (address.get("zipCode") == null) ? Collections.emptyMap() : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData((long) address.get("zipCode"))));
            orgData.add(orgBasicData);
        }
        unionQueryWrapper.setGlobalData(organizationCreationData);
        unionQueryWrapper.setUnions(orgData);

        return unionQueryWrapper;
    }

    // TODO USED IN FUTURE
    public List<UnionResponseDTO> getAllUnionByOrganization(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
           exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        List<Long> organizationSubTypeIds = organization.getOrganizationSubTypes().parallelStream().map(organizationType -> organizationType.getId()).collect(Collectors.toList());
        List<UnionResponseDTO> organizationQueryResult = organizationGraphRepository.getAllUnionsByOrganizationSubType(organizationSubTypeIds);
        return organizationQueryResult;
    }



    public List<UnionResponseDTO> getAllApplicableUnionsForOrganization(Long unitId) {
        List<UnionResponseDTO> allUnions = new ArrayList<>();
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        List<Long> organizationSubTypeIds = organization.getOrganizationSubTypes().parallelStream().map(organizationType -> organizationType.getId()).collect(Collectors.toList());

        allUnions = organizationGraphRepository.getAllUnionsByOrganizationSubType(organizationSubTypeIds);
        return allUnions;

    }

    public boolean addUnionInOrganization(Long unionId, Long organizationId, boolean joined) {
        Organization organization = organizationGraphRepository.findOne(organizationId);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organisation.notFound");

        }
        Organization union = organizationGraphRepository.findOne(unionId);
        if (!Optional.ofNullable(union).isPresent() || union.isUnion() == false || union.isEnable() == false) {
    exceptionService.dataNotFoundByIdException("message.union.id.notFound");

        }
        if (joined)
            organizationGraphRepository.addUnionInOrganization(organizationId, unionId, DateUtil.getCurrentDate().getTime());
        else
            organizationGraphRepository.removeUnionFromOrganization(organizationId, unionId, DateUtil.getCurrentDate().getTime());

        return joined;
    }

}
