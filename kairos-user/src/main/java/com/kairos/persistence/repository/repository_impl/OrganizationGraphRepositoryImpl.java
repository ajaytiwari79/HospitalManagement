package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.repository.organization.CustomOrganizationGraphRepository;
import com.kairos.response.dto.web.ClientFilterDTO;
import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.persistence.model.constants.RelationshipConstants.PEOPLE_IN_HOUSEHOLD_LIST;
import static com.kairos.persistence.model.enums.CitizenHealthStatus.ALIVE;
import static com.kairos.persistence.model.enums.CitizenHealthStatus.DECEASED;
import static com.kairos.persistence.model.enums.CitizenHealthStatus.TERMINATED;

/**
 * Created by oodles on 26/10/17.
 */
@Repository
public class OrganizationGraphRepositoryImpl implements CustomOrganizationGraphRepository {

    @Inject
    private Session session;

    public List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds,
                                                    Long organizationId, String imagePath, String skip,String moduleId){
        Map<String, Object> queryParameters = new HashMap();
        String query = "";
        queryParameters.put("unitId", organizationId);
        queryParameters.put("name", clientFilterDTO.getName());
        queryParameters.put("cprNumber", clientFilterDTO.getCprNumber());
        queryParameters.put("phoneNumber", clientFilterDTO.getPhoneNumber());
        queryParameters.put("civilianStatus", clientFilterDTO.getClientStatus());
        queryParameters.put("skip", Integer.valueOf(skip));
        queryParameters.put("latLngs", clientFilterDTO.getLocalAreaTags());
        queryParameters.put("citizenIds", citizenIds);

        queryParameters.put("imagePath", imagePath);
        if("module_2".equalsIgnoreCase(moduleId)){
            queryParameters.put("healthStatus",Arrays.asList(ALIVE,DECEASED,TERMINATED));
        } else {
            queryParameters.put("healthStatus",Arrays.asList(ALIVE,DECEASED));
        }

        if(citizenIds.isEmpty() && clientFilterDTO.getServicesTypes().isEmpty() && clientFilterDTO.getTimeSlots().isEmpty() && clientFilterDTO.getTaskTypes().isEmpty() && !clientFilterDTO.isNewDemands()){
            query = "MATCH (c:Client)-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {unitId} AND c.healthStatus IN {healthStatus} AND ( c.firstName=~{name} OR c.lastName=~{name} ) AND c.cprNumber STARTS WITH {cprNumber} with c,r\n";

        }else{
            query = "MATCH (c:Client{healthStatus:'ALIVE'})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {unitId} AND id(c) in {citizenIds} AND c.healthStatus IN {healthStatus} AND ( c.firstName=~{name} OR c.lastName=~{name} ) AND c.cprNumber STARTS WITH {cprNumber} with c,r\n";

        }
        query +=   "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n";
        query+=    "OPTIONAL MATCH (c)-[houseHoldRel:"+PEOPLE_IN_HOUSEHOLD_LIST+"]-(houseHold) with ca,c,r,houseHoldRel,houseHold\n";
        if(StringUtils.isBlank(clientFilterDTO.getPhoneNumber())){
            query += "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) with cd,ca,c,r,houseHoldRel,houseHold\n";
        }else{
            query += "MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {phoneNumber} with cd,ca,c,r,houseHoldRel,houseHold\n";
        }
        if(clientFilterDTO.getClientStatus() == null){
            query +=  "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) with cs,cd,ca,c,r,houseHoldRel,houseHold\n";
        }else{
            query +=  "MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE id(cs) = {civilianStatus} with cs,cd,ca,c,r,houseHoldRel,houseHold\n";
        }
        if(clientFilterDTO.getLocalAreaTags().isEmpty()){
            query +=   "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,cs,cd,ca,c,r,houseHoldRel,houseHold\n";
        }
        else{
            query +=   "MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) WHERE id(lat) in {latLngs} with lat,cs,cd,ca,c,r,houseHoldRel,houseHold\n";
        }
        query += "return {name:c.firstName+\" \" +c.lastName,id:id(c), healthStatus:c.healthStatus,age:c.age, emailId:c.email, profilePic: {imagePath} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city,";
        query += "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, ";
        query +=   "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END,houseHoldList:case when houseHoldRel is null then [] else collect({id:id(houseHold),firstName:houseHold.firstName,lastName:houseHold.lastName}) end}  as Client ORDER BY Client.name ASC SKIP {skip} LIMIT 20 ";
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());

    }
}
