package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.query_wrapper.ClientContactPersonQueryResult;
import com.kairos.persistence.model.user.client.*;
import com.kairos.persistence.model.user.country.CitizenStatus;
import org.springframework.data.neo4j.annotation.Depth;
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

    @Depth(value = 1)
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
            "OPTIONAL MATCH (client)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,staffId,client,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,ca,staffId,client,r\n" +
            "return id(client) as id,client.firstName+\" \" +client.lastName as name,client.gender as gender,client.profilePic as profilePic,client.age as age, ca.houseNumber+\" \" +ca.street1 as address," +
            "CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END as localAreaTag, collect({id:staffId,type:case when r is null then 'NONE' else r.type end}) as staff")
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
            "Match (nextToKin)-[:CIVILIAN_STATUS]->(citizenStatus:CitizenStatus) with c, nextToKin,citizenStatus\n" +
            "Match (nextToKin)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) with c, contactDetail,nextToKin,citizenStatus\n" +
            "Match (nextToKin)-[:HAS_HOME_ADDRESS]->(homeAddress:ContactAddress) with c, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "Match (c)-[:HAS_RELATION_OF]->(clientRelationType:ClientRelationType) with clientRelationType, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "Match (nextToKin)<-[:RELATION_WITH_NEXT_TO_KIN]-(clientRelationType)-[:RELATION_TYPE]->(relationType:RelationType) with relationType, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "Match (municipality:Municipality)<-[:MUNICIPALITY]-(homeAddress)-[:ZIP_CODE]->(zipCode:ZipCode) with municipality, zipCode, relationType, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "Match (municipality)-[:PROVINCE]->(province:Province)-[:REGION]->(region:Region)-[:BELONGS_TO]->(country:Country) with collect({id:id(municipality),name:municipality.name,province:{name:province.name,id:id(province),region:{id:id(region),name:region.name,country:{id:id(country),name:country.name}}}}) as result, municipality, zipCode, relationType, homeAddress,contactDetail,nextToKin,citizenStatus\n" +
            "return id(nextToKin) as id, id(relationType) as relationTypeId,nextToKin.age as age,nextToKin.firstName as firstName,nextToKin.lastName as lastName,nextToKin.nickName as nickName,{1}+ nextToKin.profilePic as profilePic,nextToKin.cprNumber as cprNumber,id(citizenStatus) as civilianStatusId,contactDetail as contactDetail,{municipalityId:id(municipality),zipCodeId:id(zipCode),street1:homeAddress.street1,floorNumber:homeAddress.floorNumber,houseNumber:homeAddress.houseNumber,city:homeAddress.city,longitude:homeAddress.longitude\n" +
            ",latitude:homeAddress.latitude,municipalities:result} as homeAddress")
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

    @Query( "MATCH (client:Client)-[:"+NEXT_TO_KIN+"]->(nextToKin:Client) where id(client)= {0} AND id(nextToKin)= {1} with nextToKin, client\n"+
            "MATCH (client)-[r1:"+HAS_RELATION_OF+"]->(clientRelationType:ClientRelationType) with r1, nextToKin, client, clientRelationType \n"+
            "MATCH (clientRelationType)-[r2:"+RELATION_TYPE+"]->(relationType:RelationType) with r1, r2, nextToKin, client, clientRelationType \n"+
            "MATCH (clientRelationType)-[r3:"+RELATION_WITH_NEXT_TO_KIN+"]->(nextToKin) delete r1, r2, r3 ")
    void removeClientRelationType(long clientId, long nextToKinId);

    @Query(  "MATCH (client:Client)-[:"+NEXT_TO_KIN+"]->(nextToKin:Client) where id(client)= {0} AND id(nextToKin)= {1} with nextToKin, client\n"+
            "MATCH (client)-[r1:"+HAS_RELATION_OF+"]->(clientRelationType:ClientRelationType) with  nextToKin, client, clientRelationType \n"+
            "MATCH (clientRelationType)-[r2:"+RELATION_TYPE+"]->(relationType:RelationType) with nextToKin, client, clientRelationType \n"+
            "MATCH (clientRelationType)-[r3:"+RELATION_WITH_NEXT_TO_KIN+"]->(nextToKin) return clientRelationType ")
    ClientRelationType getClientRelationType(long clientId, long nextToKinId);

    @Query("MATCH (clientRelationType:ClientRelationType) where id(clientRelationType)={0} delete clientRelationType ")
    void removeClientRelationById(long clientRelationTypeId);





    @Query("Match (nextToKin:Client)-[:"+HAS_HOME_ADDRESS+"]->(homeAddress:ContactAddress) where id(nextToKin)={0} return id(homeAddress)")
    Long getIdOfHomeAddress(Long nextToKinId);

    @Query("Match (nextToKin:Client)-[:"+HAS_CONTACT_DETAIL+"]->(contactDetail:ContactDetail) where id(nextToKin)={0} return contactDetail")
    ContactDetail getContactDetailOfNextToKin(Long nextToKinId);

    @Query("Match (citizen:Client) where id(citizen)={0} with citizen\n" +
            "Match (nextToKin:Client) where id(nextToKin)={1} with nextToKin,citizen\n" +
            "Match (citizen)-[r:NEXT_TO_KIN]->(nextToKin) return count(r)>0")
    Boolean hasAlreadyNextToKin(Long clientId,Long nextToKinId);

    @Query("MATCH (client:Client)-[r:"+CIVILIAN_STATUS+"]->(citizenStatus:CitizenStatus) where id(client)={0} delete r")
    void deleteCivilianStatus(Long clientId);

    @Query("MATCH (c:Client{citizenDead:false})-[r:"+GET_SERVICE_FROM+"]-(o:Organization) where id(o)in {0} with id(c) as citizenId, id(o) as organizationId\n" +
            "return citizenId, organizationId")
    List<ClientOrganizationIdsDTO> getCitizenIdsByUnitIds(List<Long> unitIds);

    @Query("MATCH (c:Client)-[r:"+SERVED_BY_STAFF+"]->(s:Staff) WHERE id(c)={0} AND r.type='PREFERRED' " +
            "RETURN " +
            " DISTINCT{ " +
            "id:id(s), " +
            "clientId:id(c), " +
            " firstName:s.firstName, " +
            " lastName:s.lastName, " +
            "type:r.type " +
            "} As result ")
    List<Map<String,Object>> findClientStaff(Long id);

    @Query("Match (client:Client)-[:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:{1}}]->(clientContactPerson:ClientContactPerson) where id(client) in {0} with clientContactPerson\n"+
            "MATCH (clientContactPerson)-[r1:"+CLIENT_CONTACT_PERSON_STAFF+"]->(staff:Staff) with r1,clientContactPerson \n"+
            "MATCH (clientContactPerson)-[r2:"+CLIENT_CONTACT_PERSON_SERVICE+"]->(organizationService:OrganizationService) delete r1,r2 \n")
     void removeClientContactPersonRelations(List<Long> clientIds, ClientContactPersonRelationship.ContactPersonRelationType contactPersonRelationType);

    @Query("Match (client:Client)-[r:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:{1}}]->(clientContactPerson:ClientContactPerson) where id(client) in {0} delete r")
     void removeClientContactPersonRelationship(List<Long> clientIds, ClientContactPersonRelationship.ContactPersonRelationType contactPersonRelationType);

    @Query("Match (client:Client)-[r:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:{1}}]->(clientContactPerson:ClientContactPerson) where id(client)={0} return clientContactPerson")
     ClientContactPerson getClientContactPerson(Long clientId, ClientContactPersonRelationship.ContactPersonRelationType contactPersonRelationType);

    @Query("Match (clientContactPerson:ClientContactPerson) where id(clientContactPerson)={0} delete clientContactPerson")
    void removeClientContactPerson(Long clientContactPersonId);

    @Query("MATCH (c:Client)-[:"+PEOPLE_IN_HOUSEHOLD_LIST+"]-(ps:Client) where id(c)={0}  return id(ps)")
    List<Long> getPeopleInHouseholdIdList(Long id);

    @Query("Match (client:Client)-[:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:\"PRIMARY\"}]->(primaryClientContactPerson:ClientContactPerson) where id(client)={0} with client,primaryClientContactPerson\n" +
            "OPTIONAL Match (client)-[:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:\"SECONDARY_ONE\"}]->(seconDaryclientContactPerson:ClientContactPerson)  with client,primaryClientContactPerson,seconDaryclientContactPerson\n" +
            "OPTIONAL Match (client)-[:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:\"SECONDARY_TWO\"}]->(secondaryTwoClientContactPerson:ClientContactPerson) with client,primaryClientContactPerson,secondaryTwoClientContactPerson,seconDaryclientContactPerson\n" +
            "OPTIONAL Match (client)-[:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"{contactPersonRelationType:\"SECONDARY_THREE\"}]->(seconDaryThreeclientContactPerson:ClientContactPerson)  with client,primaryClientContactPerson,seconDaryclientContactPerson, secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson\n" +
            "OPTIONAL MATCH (client)-[:"+PEOPLE_IN_HOUSEHOLD_LIST+"]-(ps:Client) with ps,primaryClientContactPerson,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson\n" +
            "Match (ps)-[:"+CLIENT_CONTACT_PERSON_RELATION_TYPE+"]->(primaryClientContactPerson)  with ps, primaryClientContactPerson,seconDaryclientContactPerson, secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson\n" +
            "Match (primaryClientContactPerson)-[:"+CLIENT_CONTACT_PERSON_STAFF+"]->(primaryStaff:Staff)  with primaryStaff, ps,primaryClientContactPerson,seconDaryclientContactPerson, secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson\n" +
            "Match (primaryClientContactPerson)-[:"+CLIENT_CONTACT_PERSON_SERVICE+"]->(primaryOrganizationService:OrganizationService)  with primaryStaff, primaryOrganizationService, ps,primaryClientContactPerson,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson\n" +
            "OPTIONAL Match (secondaryTwoClientContactPerson)-[:"+CLIENT_CONTACT_PERSON_STAFF+"]->(secondaryTwoStaff:Staff)  with primaryStaff, ps,primaryClientContactPerson,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson, primaryOrganizationService,secondaryTwoStaff\n" +
            "OPTIONAL Match (secondaryTwoClientContactPerson)-[:"+CLIENT_CONTACT_PERSON_SERVICE+"]->(secondaryTwoOrganizationService:OrganizationService)  with primaryStaff, ps,primaryClientContactPerson,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson, primaryOrganizationService, secondaryTwoOrganizationService,secondaryTwoStaff\n" +
            "OPTIONAL Match (seconDaryThreeclientContactPerson)-[:"+CLIENT_CONTACT_PERSON_STAFF+"]->(secondaryThreeStaff:Staff)  with primaryStaff, ps, primaryClientContactPerson,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson, primaryOrganizationService, secondaryTwoOrganizationService, secondaryThreeStaff,secondaryTwoStaff\n" +
            "OPTIONAL Match (seconDaryThreeclientContactPerson)-[:"+CLIENT_CONTACT_PERSON_SERVICE+"]->(secondaryThreeOrganizationService:OrganizationService)  with primaryStaff, ps, primaryClientContactPerson,primaryOrganizationService,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson, secondaryTwoOrganizationService, secondaryThreeStaff,secondaryThreeOrganizationService,secondaryTwoStaff\n" +
            "OPTIONAL Match (seconDaryclientContactPerson)-[:"+CLIENT_CONTACT_PERSON_STAFF+"]->(secondaryStaff:Staff)  with secondaryStaff, primaryStaff, ps, primaryClientContactPerson,primaryOrganizationService,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson, secondaryTwoOrganizationService, secondaryThreeStaff, secondaryThreeOrganizationService,secondaryTwoStaff\n" +
            "OPTIONAL Match (seconDaryclientContactPerson)-[:"+CLIENT_CONTACT_PERSON_SERVICE+"]->(secondaryOrganizationService:OrganizationService)  with secondaryOrganizationService, secondaryStaff, primaryStaff, ps, primaryClientContactPerson,seconDaryclientContactPerson,secondaryTwoClientContactPerson, seconDaryThreeclientContactPerson, primaryOrganizationService, secondaryTwoOrganizationService, secondaryThreeStaff,secondaryThreeOrganizationService,secondaryTwoStaff \n" +
            "return id(primaryStaff) as primaryStaffId, id(secondaryStaff) as secondaryStaffId, id(secondaryTwoStaff) as secondaryTwoStaffId, id(secondaryThreeStaff) as secondaryThreeStaffId, id(primaryOrganizationService) as serviceId, collect(id(ps)) as households")
    List<ClientContactPersonQueryResult> getClientContactPersonDataList(Long clientId);


}
