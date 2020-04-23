package com.kairos.persistence.repository.repository_impl;

import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.persistence.repository.organization.CustomUnitGraphRepository;
import org.apache.commons.collections.CollectionUtils;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 26/10/17.
 */
@Repository
public class UnitGraphRepositoryImpl implements CustomUnitGraphRepository {

    @Inject
    private Session session;

    //@Override
    public List<Map<String, Object>> getOrganizationHierarchyByFilters(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO) {
        String filterQuery = "";
        final String SUB_ORGANIZATIONS = "subOrganizations";
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("parentOrganizationId", parentOrganizationId);
        filterQuery = addParams(organizationHierarchyFilterDTO, filterQuery, SUB_ORGANIZATIONS, queryParameters);

        String query = "MATCH(o{isEnable:true,boardingCompleted: true}) where id(o)={parentOrganizationId}\n" + filterQuery +
                "OPTIONAL MATCH(o)-[orgRel:"+HAS_SUB_ORGANIZATION+"*]->(org:Organization{isEnable:true,boardingCompleted: true})\n" + filterQuery +
                "OPTIONAL MATCH(o)-[unitRel:"+HAS_UNIT+"]->(u:Unit{isEnable:true,boardingCompleted: true})\n" + filterQuery +
                "OPTIONAL MATCH(org)-[orgUnitRel:"+HAS_UNIT+"]->(un:Unit{isEnable:true,boardingCompleted: true})\n" +
                "RETURN o,org,orgRel,unitRel,u,orgUnitRel,un";

        Iterator<Map> mapIterator = session.query(Map.class, query, queryParameters).iterator();
        List<Map<String, Object>> mapList = new ArrayList<>();
        while (mapIterator.hasNext()) {
            mapList.add(mapIterator.next());
        }
        return mapList;
    }

    private String addParams(OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO, String filterQuery, final String SUB_ORGANIZATIONS, Map<String, Object> queryParameters) {
        if (organizationHierarchyFilterDTO != null) {
            //organizationType Filter
            if (CollectionUtils.isNotEmpty(organizationHierarchyFilterDTO.getOrganizationTypeIds())) {
                filterQuery = filterQuery + " Match(organizationType:OrganizationType)-[:" + TYPE_OF + "]-(" + SUB_ORGANIZATIONS + ") WHERE id(organizationType) IN {organizationTypeIds} ";
                queryParameters.put("organizationTypeIds", organizationHierarchyFilterDTO.getOrganizationTypeIds());
            }
            if (CollectionUtils.isNotEmpty(organizationHierarchyFilterDTO.getOrganizationSubTypeIds())) {
                filterQuery = filterQuery + " Match(organizationSubType:OrganizationType)-[:" + SUB_TYPE_OF + "]-(" + SUB_ORGANIZATIONS + ") WHERE id(organizationSubType) IN {organizationSubTypeIds} ";
                queryParameters.put("organizationSubTypeIds", organizationHierarchyFilterDTO.getOrganizationSubTypeIds());
            }
            //organizationService Filter
            if (CollectionUtils.isNotEmpty(organizationHierarchyFilterDTO.getOrganizationServiceIds())) {
                filterQuery = filterQuery + " Match(organizationService:OrganizationService)-[:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]-(" + SUB_ORGANIZATIONS + ") WHERE id(organizationService) IN {organizationServiceIds} ";
                queryParameters.put("organizationServiceIds", organizationHierarchyFilterDTO.getOrganizationServiceIds());
            }
            if (CollectionUtils.isNotEmpty(organizationHierarchyFilterDTO.getOrganizationSubServiceIds())) {
                filterQuery = filterQuery + " Match(organizationSubService:OrganizationService)-[:" + PROVIDE_SERVICE + "]-(" + SUB_ORGANIZATIONS + ") WHERE id(organizationSubService) IN {organizationSubServiceIds} ";
                queryParameters.put("organizationSubServiceIds", organizationHierarchyFilterDTO.getOrganizationSubServiceIds());
            }

            //accountType Filter
            if (CollectionUtils.isNotEmpty(organizationHierarchyFilterDTO.getOrganizationAccountTypeIds())) {
                filterQuery = filterQuery + " Match(accountType:AccountType)-[:" + HAS_ACCOUNT_TYPE + "]-(" + SUB_ORGANIZATIONS + ") WHERE id(accountType) IN {accountTypeIds} ";
                queryParameters.put("accountTypeIds", organizationHierarchyFilterDTO.getOrganizationAccountTypeIds());
            }
        }
        return filterQuery;
    }
}
