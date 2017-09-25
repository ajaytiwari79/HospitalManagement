package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.*;
import com.kairos.persistence.model.user.country.CitizenStatus;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 28/9/16.
 */

@Repository
public interface ClientGraphRepository extends GraphRepository<Client>{


    @Query("MATCH (client:Client)-[r:"+GET_SERVICE_FROM+"]->(org:Organization) where id(client)={0} and id(org)={1} return client")
    Client findOne(long clientId,long unitId);

    @Query("MATCH (c:Client) return c order by c.firstName")
    List<Client> findAll();

    @Query("MATCH (c:Client)-[:HAS_TEMPORARY_ADDRESS]->(ca:ClientTemporaryAddress) where id(c)={0} " +
            "with ca as ca  " +
            "MATCH (ca)-[:ZIP_CODE]->(zc:ZipCode)  " +
            "with zc as zc , ca as ca " +
            "OPTIONAL MATCH (ca)-[:TYPE_OF_HOUSING]->(hs:HousingType) " +
            "RETURN {  " +
            "  id:id(ca),  " +
            "  country:ca.country,  " +
            "  endDate:ca.endDate,  " +
            "  city:ca.city, " +
            "  regionName:ca.regionName, " +
            "  latitude:ca.latitude, " +
            "  houseNumber:ca.houseNumber, " +
            "  isVerifiedByVisitour:ca.isVerifiedByVisitour, " +
            "  municipalityName:ca.municipalityName,  " +
            "  province:ca.province, " +
            "  privateAddress:ca.privateAddress, " +
            "  isEnabled:ca.isEnabled, " +
            "  floorNumber:ca.floorNumber,  " +
            "  street1:ca.street1, " +
            "  startDate:ca.startDate,  " +
            "  longitude:ca.longitude, " +
            "  isAddressProtected:ca.isAddressProtected, " +
            "  description:ca.description, " +
            "  locationName:ca.locationName, " +
            "  zipCode:{  " +
            "        name:zc.name, " +
            "        zipCode:zc.zipCode, " +
            "        id:id(zc) " +
            "  },  " +
            "   typeOfHousing :{ " +
            "        name:hs.name, " +
            "        id:id(hs) " +
            "  } " +
            "} as result")
    List<Map<String,Object>> getClientTemporaryAddressById(Long id);

    @Query("MATCH (c:Client)-[rel:IS_RELATIVE_OF]->(ur:User) where id(c)={0}  " +
            "with ur AS user, rel AS r " +
            "OPTIONAL MATCH (user)-[:CONTACT_DETAIL]-(cd:ContactDetail), " +
            "(user)-[:HOME_ADDRESS]-(ca:ContactAddress) " +
            "return  " +
            "collect({ " +
            "firstName:user.firstName, " +
            "lastName:user.lastName, " +
            "age:user.age, " +
            "relativeId:id(user), " +
            "contactId:id(cd), " +
            "relationId:id(r), "+
            "priority:r.priority, "+
            "isFullGuardian:r.isFullGuardian, " +
            "relation:r.relation, " +
            "canUpdateOnPublicPortal:r.canUpdateOnPublicPortal, " +
            "distanceToRelative:r.distanceToRelative, " +
            "remarks:r.remarks, " +
            "workEmail:cd.workEmail, " +
            "workPhone:cd.workPhone , " +
            "mobilePhone:cd.mobilePhone, " +
            "privateEmail:cd.privateEmail ,  " +
            "privatePhone:cd.privatePhone , " +
            "facebookAccount:cd.facebookAccount , " +
            "twitterAccount:cd.twitterAccount,  " +
            " addressId:id(ca), " +
            "street1:ca.street1, " +
            "zipCode:ca.zipCode, " +
            "state:ca.state, " +
            "area:ca.area, " +
            "country:ca.country, " +
            "longitude:ca.longitude, " +
            "latitude:ca.latitude " +
            "}) as RelativeList ")
    List<Map<String,Object>> getRelativesListByClientId(Long clientId);

    @Query("MATCH (c:Client)-[r:GET_SERVICE_FROM]->(org:Organization) where id(c)={0} return org")
    List<Organization> getClientOrganizationIdList(Long clientId);

    @Query("MATCH (c:Client)-[r:"+SERVED_BY_STAFF+"]->(s:Staff) WHERE id(c)={0} AND r.type='PREFERRED' " +
            "RETURN " +
            " DISTINCT{ " +
            "id:id(s), " +
            "clientId:id(c), " +
            " firstName:s.firstName, " +
            " lastName:s.lastName, " +
            "type:r.type " +
            "} As result ")
    List<Map<String,Object>> findPreferredStaff(Long id);


    @Query("MATCH (c:Client)-[r:"+SERVED_BY_STAFF+"]->(s:Staff) WHERE id(c)={0} AND r.type='FORBIDDEN'  " +
            "RETURN " +
            " DISTINCT { " +
            "id:id(s), " +
            "clientId:id(c), " +
            "firstName:s.firstName, " +
            "lastName:s.lastName, " +
            "type:r.type " +
            "} As result ")
    List<Map<String,Object>> findForbidStaff(Long id);

    @Query("MATCH (c:Client)-[r:"+SERVED_BY_TEAM+"]->(t:Team) WHERE id(c)={0} AND r.type='FORBIDDEN'   RETURN t")
    List<Team> findForbidTeam(Long id);

    @Query("MATCH (c:Client) where c.cprNumber={0}  return c")
    Client findByCPRNumber(String cprNumber);

    @Query("MATCH (t:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(s:Staff)-[:BELONGS_TO]->(u:User) where id(t)={0} \n" +
            "             with s AS staff , u as user\n" +
            "            OPTIONAL MATCH (c:Client)-[r:SERVED_BY_STAFF]->(staff) where id(c)={1} \n" +
            "             with staff, r as served_by_staff,user  \n" +
            "             OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS{isEnabled:true}]-(s:Skill)\n" +
            "             with staff, served_by_staff, s as skill, r as r,user\n" +
            "            return  DISTINCT { id:id(staff), staffType:served_by_staff.type, lastName:staff.lastName , firstName:staff.firstName, profilePic: {2} + staff.profilePic, gender:user.gender, isActive:staff.isActive,  skillList: collect ({name: skill.name,level:r.skillLevel})  }as staffList" )
    List<Map<String,Object>> getTeamMembers(Long teamID, Long clientId, String imageUrl);



    @Query("MATCH (c:Client)-[:"+PEOPLE_IN_HOUSEHOLD_LIST+"]-(ps:Client) where id(c)={0}  return id(ps) as id, ps.firstName as firstName,ps.lastName as lastName,ps.firstName +' '+ ps.lastName as name,ps.cprNumber as cprNumber")
    List<ClientMinimumDTO> getPeopleInHouseholdList(Long id);

    @Query("MATCH (c:Client)-[r:"+SERVED_BY_STAFF+"]->(s:Staff) WHERE id(c)={0} AND r.type='PREFERRED' " +
            "RETURN {visitourId:s.visitourId} as ids")
    List<Map<String,Object>>  findPreferredStaffVisitourIds(Long id);


    @Query("MATCH (c:Client)-[r:"+SERVED_BY_STAFF+"]->(s:Staff) WHERE id(c)={0} AND r.type='FORBIDDEN' " +
            "RETURN {visitourId:s.visitourId} as ids ")
    List<Map<String,Object>> findForbidStaffVisitourIds(Long id);

    @Query("MATCH (client:Client{importFromKMD:true}) RETURN {kmdNexusExternalId:client.kmdNexusExternalId} as data")
    List<Map<String, Object>> findAllCitizensFromKMD();

    @Query("Match (client:Client),(staff:Staff) where id(client) = {0} AND id(staff) = {1}\n" +
            "MERGE (client)-[r:"+SERVED_BY_STAFF+"]->(staff) \n" +
            "ON CREATE SET r.type = {2},r.creationDate = {3},r.lastModificationDate = {4} \t\n" +
            "ON MATCH SET r.type = {2},r.lastModificationDate = {4} return true")
    void  assignStaffToClient(long clientId, long staffId, ClientStaffRelation.StaffType staffType, long creationDate, long lastModificationDate);

    @Query("MATCH (client:Client)-[:GET_SERVICE_FROM]-(org:Organization),(staff:Staff) where id(org)={0} AND id(staff) IN {1}\n" +
            "MERGE (client)-[r:SERVED_BY_STAFF]->(staff)\n" +
            "ON CREATE SET r.type = {2},r.creationDate = {3},r.lastModificationDate = {4}\n" +
            "ON MATCH SET r.type = {2},r.lastModificationDate = {4} return true")
    void assignMultipleStaffToClient(long unitId, List<Long> staffId, ClientStaffRelation.StaffType staffType, long creationDate, long lastModificationDate);

    @Query("Match (client:Client{citizenDead:false})-[:"+GET_SERVICE_FROM+"]->(organization:Organization) where id(organization)={0} with client\n" +
            "Match (staff:Staff) where id(staff) in {1} with staff,client\n" +
            "optional Match (client)-[r:"+SERVED_BY_STAFF+"]->(staff) with id(staff) as staffId,client,r\n" +
            "return id(client) as id,client.firstName+\" \" +client.lastName as name,client.gender as gender,client.profilePic as profilePic,client.age as age,collect({id:staffId,type:case when r is null then 'NONE' else r.type end}) as staff")
    List<ClientStaffQueryResult> getClientStaffRel(long unitId, List<Long> staffId);

    @Query("MATCH (client:Client) where client.kmdNexusExternalId={0} RETURN client")
    Client findByKmdNexusExternalId(String kmdNexusExternalId);

    @Query("Match (n)-[:HAS_HOME_ADDRESS]->(homeAddress:ContactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) where id(n)={0} with zipCode,homeAddress\n" +
            "Match (homeAddress)-[:MUNICIPALITY]->(Municipality:Municipality) with Municipality,zipCode,homeAddress\n" +
            "return Municipality,zipCode,homeAddress")
    ClientHomeAddressQueryResult getHomeAddress(long clientId);

    @Query("Match (client:Client) where id(client)={0}\n" +
            "optional Match (client)-[:"+HAS_HOME_ADDRESS+"]->(homeAddress:ContactAddress)-[:"+ZIP_CODE+"]->(homeZipCode:ZipCode)\n" +
            "optional match (homeAddress)-[:"+MUNICIPALITY+"]->(homeAddressMunicipality:Municipality)\n" +
            "optional match (homeAddress)-[:"+TYPE_OF_HOUSING+"]->(homeAddressHousingType:HousingType)\n" +
            "optional match (client)-[:"+HAS_SECONDARY_ADDRESS+"]->(secondaryAddress:ContactAddress)-[:"+ZIP_CODE+"]->(secondaryZipCode:ZipCode)\n" +
            "optional match (secondaryAddress)-[:"+MUNICIPALITY+"]->(secondaryAddressMunicipality:Municipality)\n" +
            "optional match (secondaryAddress)-[:"+TYPE_OF_HOUSING+"]->(secondaryAddressHousingType:HousingType)\n" +
            "optional match (client)-[:"+HAS_PARTNER_ADDRESS+"]->(partnerAddress:ContactAddress)-[:"+ZIP_CODE+"]->(partnerZipCode:ZipCode)\n" +
            "optional match (partnerAddress)-[:"+MUNICIPALITY+"]->(partnerAddressMunicipality:Municipality)\n" +
            "optional match (partnerAddress)-[:"+TYPE_OF_HOUSING+"]->(partnerAddressHousingType:HousingType)\n" +
            "return homeAddress,homeAddressMunicipality,homeAddressHousingType,homeZipCode,secondaryAddress,secondaryZipCode,secondaryAddressMunicipality,secondaryAddressHousingType,partnerAddress,partnerZipCode,partnerAddressMunicipality,partnerAddressHousingType\n")
    ClientAddressQueryResult getAllAddress(long clientId);

    @Query("Match (client:Client) where id(client)={0}\n" +
            "match (client)-[:"+HAS_TEMPORARY_ADDRESS+"]->(temporaryAddress:ContactAddress)-[:"+ZIP_CODE+"]->(temporaryZipCode:ZipCode)\n" +
            "optional match (temporaryAddress)-[:"+MUNICIPALITY+"]->(temporaryAddressMunicipality:Municipality)\n" +
            "optional match (temporaryAddress)-[:"+TYPE_OF_HOUSING+"]->(temporaryAddressHousingType:HousingType)\n" +
            "return temporaryAddress,temporaryAddressMunicipality,temporaryZipCode,temporaryAddressHousingType")
    List<ClientTempAddressQueryResult> getTemporaryAddress(long clientId);

    @Query("MATCH (c:Client)-[r:CIVILIAN_STATUS]->(cs:CitizenStatus) where id(c)= {0}  return cs")
    CitizenStatus findCitizenCivilianStatus(long citizenId);

    @Query("MATCH (c:Client)-[:NEXT_TO_KIN]->(n:Client) where id(c)={0}  return n")
    Client getNextToKin(Long id);

    @Query("MATCH (c:Client{citizenDead:false})-[r:"+GET_SERVICE_FROM+"]-(o:Organization) where id(o)= {0} return id(c) as id")
    List<Long> getCitizenIds(long unitId);

    @Query("Match (n:Client) where id(n) in {0} return n order by id(n)")
    List<Client> findByIdIn(List<Long> ids);

    @Query("MATCH (c:Client)-[r:GET_SERVICE_FROM]->(o:Organization) where id(c)={0} and id(o)={1}  return c")
    Client getClientByClientIdAndUnitId(Long clientId, Long unitId);

    @Query("Match (c:Client)-[:NEXT_TO_KIN]->(nextToKin:Client) where id(c)={0}\n" +
            "Match (nextToKin)-[:CIVILIAN_STATUS]->(citizenStatus:CitizenStatus) with nextToKin,citizenStatus\n" +
            "Match (nextToKin)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) with contactDetail,nextToKin,citizenStatus\n" +
            "Match (nextToKin)-[:HAS_HOME_ADDRESS]->(homeAddress:ContactAddress) with homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "Optional Match (nextToKin)-[:HAS_RELATION_OF]->(relationType:RelationType) with relationType, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "Match (municipality:Municipality)<-[:MUNICIPALITY]-(homeAddress)-[:ZIP_CODE]->(zipCode:ZipCode) with municipality, zipCode, relationType, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "return id(nextToKin) as id,nextToKin.age as age,nextToKin.firstName as firstName,nextToKin.lastName as lastName,nextToKin.nickName as nickName,{1} + nextToKin.profilePic as profilePic,nextToKin.cprNumber as cprNumber,homeAddress as homeAddress,citizenStatus as citizenStatus,contactDetail as contactDetail,municipality as municipality,zipCode as zipCode, id(relationType) as relationTypeId")
    List<NextToKinQueryResult> getNextToKinDetail(long clientId,String imageUrl);

    @Query("Match (client:Client) where id(client)={0} with client\n" +
            "Match (houseHoldPeople:Client) where id(houseHoldPeople)={1} with client,houseHoldPeople\n" +
            "Merge (client)-[r:"+PEOPLE_IN_HOUSEHOLD_LIST+"]-(houseHoldPeople)\n" +
            "ON CREATE SET r.creationDate={2},r.lastModificationDate={3}\n" +
            "ON MATCH SET r.lastModificationDate={3} return true")
    void createHouseHoldRelationship(long clientId,long houseHoldPeopleId,long creationDate,long lastModificationDate);

    @Query("MATCH (citizen:Client{citizenDead:false})-[:GET_SERVICE_FROM]->(o:Organization)  where id(o)= {0} with citizen\n"+
            "MATCH (citizen)-[:HAS_HOME_ADDRESS]->(homeAddress:ContactAddress) WHERE homeAddress IS NOT NULL return citizen, homeAddress")
    List<ClientHomeAddressQueryResult> getClientsAndHomeAddressByUnitId(long unitId);

    @Query("MATCH (c:Client{citizenDead:false})-[r:"+HAS_LOCAL_AREA_TAG+"]-(lat:LocalAreaTag) where id(lat)= {0} return c")
    List<Client> getClientsByLocalAreaTagId(long localAreaTagId);

    @Query("MATCH (clientRelationType:ClientRelationType)-[:"+RELATION_TYPE+"]-(relationType:RelationType) where id(relationType)= {0} with clientRelationType\n"+
         "MATCH (clientRelationType)-[:"+RELATION_WITH_NEXT_TO_KIN+"]-(client:Client) where id(client)= {1} return clientRelationType")
    ClientRelationType getClientRelationType(long relationTypeId, long clientId);

}
