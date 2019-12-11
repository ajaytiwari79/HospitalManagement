package com.kairos.persistence.repository.repository_impl;

import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.enums.Employment;
import com.kairos.enums.FilterType;
import com.kairos.enums.ModuleId;
import com.kairos.persistence.repository.organization.CustomUnitGraphRepository;
import com.kairos.service.organization.GroupService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.enums.CitizenHealthStatus.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 26/10/17.
 */
@Repository
public class UnitGraphRepositoryImpl implements CustomUnitGraphRepository {

    @Inject
    private Session session;

    public List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds,
                                                    Long organizationId, String imagePath, String skip, String moduleId) {
        Map<String, Object> queryParameters = new HashMap();
        String query = "";
        String dynamicWhereQuery = "";
        queryParameters.put("unitId", organizationId);

        if (clientFilterDTO.getName() != null && !StringUtils.isBlank(clientFilterDTO.getName())) {
            queryParameters.put("name", clientFilterDTO.getName());
            dynamicWhereQuery += "AND ( user.firstName=~{name} OR user.lastName=~{name})";
        }
        if (clientFilterDTO.getCprNumber() != null && !StringUtils.isBlank(clientFilterDTO.getCprNumber())) {
            queryParameters.put("cprNumber", clientFilterDTO.getCprNumber());
            dynamicWhereQuery += "AND user.cprNumber STARTS WITH {cprNumber}";
        }
        queryParameters.put("phoneNumber", clientFilterDTO.getPhoneNumber());
        queryParameters.put("civilianStatus", clientFilterDTO.getClientStatus());
        queryParameters.put("skip", Integer.valueOf(skip));
        queryParameters.put("latLngs", clientFilterDTO.getLocalAreaTags());
        queryParameters.put("citizenIds", citizenIds);

        queryParameters.put("imagePath", imagePath);


        if (Arrays.asList("module_2", "tab_1").contains(moduleId)) {
            queryParameters.put("healthStatus", Arrays.asList(ALIVE, DECEASED, TERMINATED));
        } else {
            queryParameters.put("healthStatus", Arrays.asList(ALIVE, DECEASED));
        }

        if (citizenIds.isEmpty() && clientFilterDTO.getServicesTypes().isEmpty() && clientFilterDTO.getTimeSlots().isEmpty() && clientFilterDTO.getTaskTypes().isEmpty() && !clientFilterDTO.isNewDemands()) {
            query = "MATCH (user:User)<-[:" + IS_A + "]-(c:Client)-[r:" + GET_SERVICE_FROM + "]->(o:Unit) WHERE id(o)= {unitId} AND c.healthStatus IN {healthStatus} " + dynamicWhereQuery + "  with c,r,user\n";
        } else {
            query = "MATCH (user:User)<-[:" + IS_A + "]-(c:Client{healthStatus:'ALIVE'})-[r:" + GET_SERVICE_FROM + "]->(o:Unit) WHERE id(o)= {unitId} AND id(c) in {citizenIds} AND c.healthStatus IN {healthStatus} " + dynamicWhereQuery + " with c,r,user\n";

        }
        query += "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r,user\n";
        query += "OPTIONAL MATCH (c)-[houseHoldRel:" + PEOPLE_IN_HOUSEHOLD_LIST + "]-(houseHold) with ca,c,r,houseHoldRel,houseHold,user\n";
        if (clientFilterDTO.getPhoneNumber() == null) {
            query += "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) with cd,ca,c,r,houseHoldRel,houseHold,user\n";
        } else {
            query += "MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {phoneNumber} with cd,ca,c,r,houseHoldRel,houseHold,user\n";
        }
        if (clientFilterDTO.getClientStatus() == null) {
            query += "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) with cs,cd,ca,c,r,houseHoldRel,houseHold,user\n";
        } else {
            query += "MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE id(cs) = {civilianStatus} with cs,cd,ca,c,r,houseHoldRel,houseHold,user\n";
        }
        if (clientFilterDTO.getLocalAreaTags().isEmpty()) {
            query += "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,cs,cd,ca,c,r,houseHoldRel,houseHold,user\n";
        } else {
            query += "MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) WHERE id(lat) in {latLngs} with lat,cs,cd,ca,c,r,houseHoldRel,houseHold,user\n";
        }
        query += "return {name:user.firstName+\" \" +user.lastName,id:id(c), healthStatus:c.healthStatus,age:round ((timestamp()-c.dateOfBirth) / (365*24*60*60*1000)), emailId:user.email, profilePic: {imagePath} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city,";
        query += "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, ";
        query += "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END,houseHoldList:case when houseHoldRel is null then [] else collect({id:id(houseHold),firstName:houseHold.firstName,lastName:houseHold.lastName}) end}  as Client ORDER BY Client.name ASC SKIP {skip} LIMIT 20 ";
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class, query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map>toList());

    }

    //@Override
    public List<Map<String, Object>> getOrganizationHierarchyByFilters(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO) {
        String filterQuery = "";
        final String SUB_ORGANIZATIONS = "subOrganizations";
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("parentOrganizationId", parentOrganizationId);

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
}
