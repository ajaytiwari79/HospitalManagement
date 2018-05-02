package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.enums.FilterEntityType;
import com.kairos.persistence.repository.organization.CustomOrganizationGraphRepository;
import com.kairos.response.dto.web.client.ClientFilterDTO;
import com.kairos.service.user_filter.UserFilterService;
import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kairos.persistence.model.constants.RelationshipConstants.ENGINEER_TYPE;
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


    public String getMatchQueryForPropertiesOfStaffByFilters(Map<FilterEntityType, List<String>> filters){
        String matchQueryForStaff = "";
//        Boolean enable
        if(Optional.ofNullable(filters.get(FilterEntityType.STAFF_STATUS)).isPresent()){
            matchQueryForStaff+= " WHERE staff.currentStatus IN {staffStatusList} ";
        }
        if(Optional.ofNullable(filters.get(FilterEntityType.GENDER)).isPresent()){
            matchQueryForStaff+= " AND user.gender IN {genderList} ";

        }
        return matchQueryForStaff;
    }

    public String getMatchQueryForRelationshipOfStaffByFilters(Map<FilterEntityType, List<String>> filters){
        String matchRelationshipQueryForStaff = "";
        if(Optional.ofNullable(filters.get(FilterEntityType.EMPLOYMENT_TYPE)).isPresent()){
//            queryParameters.puervice.convertListOfStringIntoLong(filters.get(FilterEntityType.EMPLOYMENT_TYPE)));
            matchRelationshipQueryForStaff+= " MATCH (unitPos)-[HAS_EMPLOYMENT_TYPE]-(employmentType:EmploymentType) "+
                    "WHERE id(employmentType) IN {employmentTypeIds} with user, staff, unitPos";
        }
        if(Optional.ofNullable(filters.get(FilterEntityType.EXPERTISE)).isPresent()){
//            queryParameters.puervice.convertListOfStringIntoLong(filters.get(FilterEntityType.EMPLOYMENT_TYPE)));
            matchRelationshipQueryForStaff+= " MATCH (unitPos)-[HAS_EXPERTISE_IN]-(expertise:Expertise) "+
                    "WHERE id(expertise) IN {expertiseIds} with user, staff, unitPos";
        }

        return matchRelationshipQueryForStaff;
    }

    public List<Long> convertListOfStringIntoLong(List<String> listOfString){
        return listOfString.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public List<Map> getStaffWithFilters(Long unitId, Long parentOrganizationId, Boolean fetchStaffHavingUnitPosition,
                                         Map<FilterEntityType, List<String>> filters, String imagePath){

        Map<String, Object> queryParameters = new HashMap();

        queryParameters.put("unitId", unitId);
        queryParameters.put("parentOrganizationId", parentOrganizationId);
        if(Optional.ofNullable(filters.get(FilterEntityType.STAFF_STATUS)).isPresent()){
            queryParameters.put("staffStatusList",
                    filters.get(FilterEntityType.STAFF_STATUS));
        }
        if(Optional.ofNullable(filters.get(FilterEntityType.GENDER)).isPresent()){
            queryParameters.put("genderList",
                    filters.get(FilterEntityType.GENDER));
        }
        if(fetchStaffHavingUnitPosition && Optional.ofNullable(filters.get(FilterEntityType.EMPLOYMENT_TYPE)).isPresent()){
            queryParameters.put("employmentTypeIds",
                    convertListOfStringIntoLong(filters.get(FilterEntityType.EMPLOYMENT_TYPE)));
        }
        if(Optional.ofNullable(filters.get(FilterEntityType.ENGINEER_TYPE)).isPresent()){
            queryParameters.put("engineerTypeIds",
                    convertListOfStringIntoLong(filters.get(FilterEntityType.ENGINEER_TYPE)));
        }
        if(Optional.ofNullable(filters.get(FilterEntityType.EXPERTISE)).isPresent()){
            queryParameters.put("expertiseIds",
                    convertListOfStringIntoLong(filters.get(FilterEntityType.EXPERTISE)));
        }
        queryParameters.put("imagePath", imagePath);


        String query = "";
        if(fetchStaffHavingUnitPosition){
            query+= " MATCH (staff:Staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization:Organization) where id(organization)={unitId}"+
                    " MATCH (staff)-[:BELONGS_TO]->(user:User) " + getMatchQueryForPropertiesOfStaffByFilters(filters)+
                    " with user, staff, unitPos";
        } else {
            query+= " MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff) where id(organization)={parentOrganizationId} "+
                    " MATCH (staff)-[:BELONGS_TO]->(user:User)  "+ getMatchQueryForPropertiesOfStaffByFilters(filters)+
                    " with user, staff OPTIONAL MATCH (staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization:Organization) where id(organization)={unitId} with user, staff, unitPos";

        }
        query+= getMatchQueryForRelationshipOfStaffByFilters(filters);

        query+= " Optional MATCH (staff)-[:HAS_CONTACT_ADDRESS]-(contactAddress:ContactAddress) ";
        if(Optional.ofNullable(filters.get(FilterEntityType.ENGINEER_TYPE)).isPresent()){
            query+= " OPTIONAL Match (staff)-[:ENGINEER_TYPE]->(engineerType:EngineerType) WHERE id(engineerType) IN {engineerTypeIds} with engineerType,contactAddress, staff, user";

        } else {
            query+= " OPTIONAL Match (staff)-[:ENGINEER_TYPE]->(engineerType:EngineerType) with engineerType,contactAddress, staff, user";
        }

        query+= " return {id:id(staff), city:contactAddress.city,province:contactAddress.province, "+
                "firstName:staff.firstName,lastName:staff.lastName,employedSince :staff.employedSince,"+
                "badgeNumber:staff.badgeNumber, userName:staff.userName,externalId:staff.externalId,"+
                "cprNumber:staff.cprNumber, visitourTeamId:staff.visitourTeamId, familyName: staff.familyName, "+
                "gender:user.gender, profilePic:{imagePath} + staff.profilePic, engineerType:id(engineerType) } as staff\n";


        /*query+= " return distinct id(staff) as id, contactAddress.city as city,contactAddress.province as province ,"+
                "staff.firstName as firstName,staff.lastName as lastName,staff.employedSince as employedSince,"+
                "staff.badgeNumber as badgeNumber, staff.userName as userName,staff.externalId as externalId,"+
                "staff.cprNumber as cprNumber,staff.visitourTeamId as visitourTeamId,staff.familyName as familyName, "+
                "user.gender as gender, {imagePath} + staff.profilePic as profilePic, id(engineerType) as engineerType with params\n";
*/
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(session.query(Map.class , query, queryParameters).iterator(), Spliterator.ORDERED), false).collect(Collectors.<Map> toList());
    }

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


        if(Arrays.asList("module_2","tab_1").contains(moduleId)){
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
