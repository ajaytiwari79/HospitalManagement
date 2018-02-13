package com.kairos.service.organization;

import com.kairos.persistence.model.organization.OrganizationQueryResult;
import com.kairos.persistence.model.organization.UnionQueryWrapper;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

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

    public UnionQueryWrapper getAllUnionOfCountry(Long countryId) {
        UnionQueryWrapper unionQueryWrapper = new UnionQueryWrapper();

        OrganizationQueryResult organizationQueryResult = organizationGraphRepository.getAllUnionOfCountry(countryId);
        OrganizationCreationData organizationCreationData = organizationGraphRepository.getOrganizationCreationData(countryId);
        List<Map<String, Object>> zipCodes = FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId));
        organizationCreationData.setZipCodes(zipCodes);
        List<Map<String, Object>> orgData = new ArrayList<>();
        for (Map<String, Object> organizationData : organizationQueryResult.getOrganizations()) {
            HashMap<String, Object> orgBasicData = new HashMap<>();
            orgBasicData.put("orgData", organizationData);
            Map<String, Object> address = (Map<String, Object>) organizationData.get("homeAddress");
            orgBasicData.put("municipalities", (address.get("zipCode") == null) ? Collections.emptyMap() : FormatUtil.formatNeoResponse(regionGraphRepository.getGeographicTreeData((long) address.get("zipCode"))));
            orgData.add(orgBasicData);
        }
        unionQueryWrapper.setGlobalData(organizationCreationData);
        unionQueryWrapper.setUnions(orgData);

        return unionQueryWrapper;
    }


}
