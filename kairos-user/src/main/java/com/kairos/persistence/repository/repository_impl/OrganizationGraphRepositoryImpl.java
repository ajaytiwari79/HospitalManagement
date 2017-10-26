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

/**
 * Created by oodles on 26/10/17.
 */
@Repository
public class OrganizationGraphRepositoryImpl implements CustomOrganizationGraphRepository {

    @Inject
    private Session session;

    public List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds, Long organizationId, String imagePath, String skip){
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

        if(citizenIds.isEmpty() && clientFilterDTO.getServicesTypes().isEmpty() && clientFilterDTO.getTimeSlots().isEmpty() && clientFilterDTO.getTaskTypes().isEmpty() && !clientFilterDTO.isNewDemands()){
            query = "MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {unitId} AND ( c.firstName=~{name} OR c.lastName=~{name} ) AND c.cprNumber STARTS WITH {cprNumber} with c,r\n";

        }else{
            query = "MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {unitId} AND id(c) in {citizenIds} AND ( c.firstName=~{name} OR c.lastName=~{name} ) AND c.cprNumber STARTS WITH {cprNumber} with c,r\n";

        }
        query +=   "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n";
        if(StringUtils.isBlank(clientFilterDTO.getPhoneNumber())){
            query += "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) with cd,ca,c,r\n";
        }else{
            query += "MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {phoneNumber} with cd,ca,c,r\n";
        }
        if(clientFilterDTO.getClientStatus().equals("(?i).*")){
            query +=  "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) with cs,cd,ca,c,r\n";
        }else{
            query +=  "MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE cs.name=~{civilianStatus} with cs,cd,ca,c,r\n";
        }
        if(clientFilterDTO.getLocalAreaTags().isEmpty()){
            query +=   "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,cs,cd,ca,c,r\n";
        }
        else{
            query +=   "MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) in {latLngs} with lat,cs,cd,ca,c,r\n";
        }
        query += "return {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {imagePath} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city,";
        query += "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, ";
        query +=   "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName ASC SKIP {skip} LIMIT 20 ";
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());

    }
}
