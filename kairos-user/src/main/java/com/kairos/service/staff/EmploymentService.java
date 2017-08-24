package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.enums.EmploymentStatus;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.user.client.ContactAddress;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.model.user.country.EngineerType;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.staff.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.util.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.text.ParseException;
import java.util.*;

import static com.kairos.constants.AppConstants.TEAM;
import static com.kairos.constants.AppConstants.ORGANIZATION;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class EmploymentService extends UserBaseService {

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private IntegrationService integrationService;
    @Inject
    private UnitEmploymentGraphRepository unitEmploymentGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private AccessPermissionGraphRepository accessPermissionGraphRepository;
    @Inject
    private AccessPageRepository accessPageRepository;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private Scheduler scheduler;
    @Inject
    private AccessPageService accessPageService;
    @Inject
    private TreeStructureService treeStructureService;
    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private PartialLeaveGraphRepository partialLeaveGraphRepository;
    @Inject
    private UnitEmpAccessGraphRepository unitEmpAccessGraphRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmploymentService.class);

    public Map<String, Object> saveEmploymentDetail(long staffId, StaffEmploymentDetail staffEmploymentDetail) throws ParseException {
        Staff objectToUpdate = staffGraphRepository.findOne(staffId);
        if (objectToUpdate == null) {
            return null;
        }
        EngineerType engineerType = engineerTypeGraphRepository.findOne(staffEmploymentDetail.getEngineerTypeId());
        objectToUpdate.setEmail(staffEmploymentDetail.getEmail());
        objectToUpdate.setCardNumber(staffEmploymentDetail.getCardNumber());
        objectToUpdate.setSendNotificationBy(staffEmploymentDetail.getSendNotificationBy());
        objectToUpdate.setCopyKariosMailToLogin(staffEmploymentDetail.isCopyKariosMailToLogin());
        objectToUpdate.setEmployedSince(DateConverter.parseDate(staffEmploymentDetail.getEmployedSince()).getTime());
        objectToUpdate.setVisitourId(staffEmploymentDetail.getVisitourId());
        objectToUpdate.setEngineerType(engineerType);
        objectToUpdate.setExternalId(staffEmploymentDetail.getTimeCareExternalId());
        save(objectToUpdate);
        return retrieveEmploymentDetails(objectToUpdate);
    }

    public Map<String, Object> retrieveEmploymentDetails(Staff staff) {
        Map<String, Object> map = new HashMap<>();
        map.put("employedSince", DateConverter.getDate(staff.getEmployedSince()));
        map.put("cardNumber", staff.getCardNumber());
        map.put("sendNotificationBy", staff.getSendNotificationBy());
        map.put("copyKariosMailToLogin", staff.isCopyKariosMailToLogin());
        map.put("email", staff.getEmail());
        map.put("profilePic", envConfig.getServerHost() + File.separator + staff.getProfilePic());
        map.put("visitourId", staff.getVisitourId());
        map.put("engineerTypeId", (staff.getEngineerType() == null) ? null : staff.getEngineerType().getId());
        map.put("timeCareExternalId",staff.getExternalId());
        return map;
    }



    public Map<String, Object> createEmployment(long unitId, long staffId, long accessGroupId, boolean created) {

        Organization unit = organizationGraphRepository.findOne(unitId);
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        if(unit == null){
            throw  new InternalError("unit is null");
        }
        Organization parentOrganization;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parentOrganization = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parentOrganization = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        UnitEmployment unitEmployment;
        Employment employment;
        if(parentOrganization == null){
            unitEmployment = unitEmploymentGraphRepository.getUnitEmployment(unitId, staffId,unitId);
            employment =  employmentGraphRepository.findEmployment(unit.getId(), staffId);
        } else {
            unitEmployment = unitEmploymentGraphRepository.getUnitEmployment(parentOrganization.getId(), staffId,unitId);
            employment =  employmentGraphRepository.findEmployment(parentOrganization.getId(), staffId);
        }

        Staff staff = staffGraphRepository.findOne(staffId);
        boolean flsSyncStatus = false;
        List<AccessPageQueryResult> accessPageQueryResults;
        if (created) {
            if (employment == null) {
                employment = new Employment();
                employment.setStaff(staff);
            }
            if (unitEmployment == null) {
                AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
                unitEmployment = new UnitEmployment();
                unitEmployment.setOrganization(unit);
                unitEmployment.setStartDate(new Date().getTime());
                employment.getUnitEmployments().add(unitEmployment);
                employmentGraphRepository.save(employment);
                AccessPermission accessPermission = new AccessPermission(accessGroup);
                accessPermissionGraphRepository.save(accessPermission);
                unitEmploymentGraphRepository.linkUnitEmploymentWithAccessPermission(unitEmployment.getId(),accessPermission.getId());
                accessPageRepository.setDefaultPermission(accessPermission.getId(),accessGroupId);
                accessPageQueryResults = getAccessPages(accessPermission);
                if(parentOrganization == null){
                    unit.getEmployments().add(employment);
                    organizationGraphRepository.save(unit);
                }  else {
                    parentOrganization.getEmployments().add(employment);
                    organizationGraphRepository.save(parentOrganization);
                }
                if(accessGroup.isTypeOfTaskGiver())
                    flsSyncStatus = syncStaffInVisitour(staff,unitId, flsCredentials);
            } else {
                AccessPermission accessPermission;
                if(parentOrganization == null){
                    accessPermission = unitEmploymentGraphRepository.getAccessPermission(unit.getId(), unitId, staffId, accessGroupId);
                } else {
                    accessPermission = unitEmploymentGraphRepository.getAccessPermission(parentOrganization.getId(), unitId, staffId, accessGroupId);
                }
                AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
                if (accessPermission == null) {
                    accessPermission = new AccessPermission(accessGroup);
                    accessPermissionGraphRepository.save(accessPermission);
                    unitEmploymentGraphRepository.linkUnitEmploymentWithAccessPermission(unitEmployment.getId(),accessPermission.getId());
                    accessPageRepository.setDefaultPermission(accessPermission.getId(),accessGroupId);
                    accessPageQueryResults = getAccessPages(accessPermission);
                    if(accessGroup.isTypeOfTaskGiver())
                        flsSyncStatus = syncStaffInVisitour(staff,unitId, flsCredentials);
                } else {
                    if(parentOrganization == null){
                        unitEmploymentGraphRepository.updateUnitEmployment(unit.getId(),unitId,staffId,accessGroupId,true);
                    } else {
                        unitEmploymentGraphRepository.updateUnitEmployment(parentOrganization.getId(),unitId,staffId,accessGroupId,true);
                    }
                    accessPageQueryResults = Collections.emptyList();
                    if(accessGroup.isTypeOfTaskGiver())
                        flsSyncStatus = syncStaffInVisitour(staff,unitId, flsCredentials);
                }
            }
        } else {
            if(parentOrganization == null){
                unitEmploymentGraphRepository.updateUnitEmployment(unit.getId(), unitId, staffId, accessGroupId, false);
            } else {
                unitEmploymentGraphRepository.updateUnitEmployment(parentOrganization.getId(), unitId, staffId, accessGroupId, false);
            }
            flsSyncStatus = removeStaffFromFls(staff, flsCredentials);
            accessPageQueryResults = Collections.emptyList();

        }
        if(flsSyncStatus){
            staff.setVisitourId(staff.getId());
            staffGraphRepository.save(staff);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("startDate", DateConverter.getDate(unitEmployment.getStartDate()));
        response.put("endDate", DateConverter.getDate(unitEmployment.getEndDate()));
        response.put("organizationId", unitId);
        response.put("status", unitEmployment.getEmploymentStatus());
        response.put("id", unitEmployment.getId());
        response.put("synInFls",flsSyncStatus);
        response.put("accessPage",accessPageQueryResults);
        return response;
    }

    public Map<String, Object> updateEmployment(long unitEmploymentId, Map<String, Object> wageDetails) throws ParseException {

        wageDetails.put("startDate", DateConverter.parseDate(((String) wageDetails.get("startDate"))).getTime());
        wageDetails.put("endDate", DateConverter.parseDate(((String) wageDetails.get("endDate"))).getTime());

        ObjectMapper objectMapper = new ObjectMapper();
        Wage wage = objectMapper.convertValue(wageDetails, Wage.class);
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);
        unitEmployment.getWages().add(wage);
        unitEmploymentGraphRepository.save(unitEmployment);
        wageDetails.put("id", wage.getId());
        return wageDetails;

    }

    public List<Map<String, Object>> getEmployments(long staffId, long unitId,String type) {

        Organization unit;

        if(ORGANIZATION.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.findOne(unitId);
        } else if(TEAM.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.getOrganizationByTeamId(unitId);
        } else {
            throw new InternalError("Type can't be recognised");
        }

        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        List<Map<String, Object>> list = new ArrayList<>();

        if(parent == null){
            for (Map<String, Object> map : unitEmploymentGraphRepository.getUnitEmploymentsInAllUnits(staffId, unit.getId(), unit.getId())) {
                list.add((Map<String, Object>) map.get("data"));
            }
        } else {
            for (Map<String, Object> map : unitEmploymentGraphRepository.getUnitEmploymentsInAllUnits(staffId, parent.getId(), unitId)) {
                list.add((Map<String, Object>) map.get("data"));
            }
        }

        return list;
    }

    public void createEmploymentForUnitManager(Staff staff, Organization parent, Organization unit, long accessGroupId) {

        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if(accessGroup == null){
            throw new InternalError("Access group not found");
        }
        Employment employment = new Employment();
        employment.setName("Working as unit manager");
        employment.setStaff(staff);
        UnitEmployment unitEmployment = new UnitEmployment();
        unitEmployment.setUnitManagerEmployment(true);
        unitEmployment.setOrganization(unit);

        //set permission in unit employment
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitEmployment,accessPermission);
        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
        accessPageService.setPagePermissionToStaff(accessPermission,accessGroup.getId());
        employment.getUnitEmployments().add(unitEmployment);
        if (parent == null) {
            unit.getEmployments().add(employment);
            organizationGraphRepository.save(unit);
        } else {
            parent.getEmployments().add(employment);
            organizationGraphRepository.save(parent);
        }

    }

    private List<AccessPageQueryResult> getAccessPages(AccessPermission accessPermission){
        List<Map<String, Object>> accessPages = accessPageRepository.getStaffPermission(accessPermission.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessPageQueryResult> queryResults = new ArrayList<>();
        for (Map<String, Object> accessPage : accessPages) {
            AccessPageQueryResult accessPageQueryResult = objectMapper.convertValue((Map<String, Object>) accessPage.get("data"), AccessPageQueryResult.class);
            queryResults.add(accessPageQueryResult);
        }
        List<AccessPageQueryResult> treeData = accessGroupService.getAccessPageHierarchy(queryResults,queryResults);

        List<AccessPageQueryResult> modules = new ArrayList<>();
        for(AccessPageQueryResult accessPageQueryResult : treeData){
            if(accessPageQueryResult.isModule()){
                modules.add(accessPageQueryResult);
            }
        }
        return modules;
    }

    /**
     * @author prabjot
     * TODO for visitour testing,i am going to keep contact address of staff same as office address,after i will update it
     * @param staff
     * @param unitId
     * @return
     */
    private boolean syncStaffInVisitour(Staff staff,long unitId, Map<String, String> flsCredentials){
        logger.info("Syncing staff in fls");
        ContactDetail staffContactDetail = staffGraphRepository.getContactDetail(staff.getId());

        OrganizationContactAddress organizationContactData = organizationGraphRepository.getContactAddressOfOrg(unitId);
        ContactAddress officeAddress = organizationContactData.getContactAddress();
        if(officeAddress == null){
            throw  new InternalError("organization address is null");
        }
        ZipCode officeZipCode = organizationContactData.getZipCode();

        if(officeZipCode == null){
            throw new InternalError("office zip code can not null");
        }

        List<String> skillsToUpdate = staffGraphRepository.getStaffVisitourIdWithLevel(unitId,staff.getId());

        String visitourSkillRequestData = "";
        for(String skill:skillsToUpdate){
            visitourSkillRequestData = skill + "," + visitourSkillRequestData;
        }

        Map<String, Object> engineerMetaData = new HashMap<>();

        //personal details
        if(staff.getEngineerType() != null) {
            String type = staff.getEngineerType().getVisitourCode();
            engineerMetaData.put("type", type);
        }

        engineerMetaData.put("fmvtid", staff.getId());
        engineerMetaData.put("fmextID", staff.getId());
        engineerMetaData.put("active", true);
        engineerMetaData.put("prename", staff.getLastName());
        engineerMetaData.put("name",  staff.getFirstName());

        //staff contact address
        engineerMetaData.put("scountry", "DK");
        engineerMetaData.put("szip", officeZipCode.getZipCode());
        engineerMetaData.put("scity", officeAddress.getCity());
        engineerMetaData.put("sstreet", officeAddress.getStreet1()+" " +officeAddress.getHouseNumber());

        //personal details
        engineerMetaData.put("email", staff.getEmail());
        if(staff.getContactDetail() != null){
            engineerMetaData.put("phone", staffContactDetail.getPrivatePhone());
            engineerMetaData.put("mobile", staffContactDetail.getMobilePhone());
        }
        //office address
        engineerMetaData.put("ecountry", "DK");
        engineerMetaData.put("ezip", officeZipCode.getZipCode());
        engineerMetaData.put("ecity", officeAddress.getCity());
        engineerMetaData.put("estreet", officeAddress.getStreet1()+" " +officeAddress.getHouseNumber());

        //skills of staff
        engineerMetaData.put("lskills", visitourSkillRequestData);

        logger.info("AddressDTO to verify from fls" + engineerMetaData);
        int code = scheduler.createEngineer(engineerMetaData, flsCredentials);
        logger.info("FLS staff sync status-->" + code);
        if (code == 0) {
            return true;
        }
        return false;

    }

    private boolean removeStaffFromFls(Staff staff, Map<String, String> flsCredentials) {
        Map<String, Object> engineerMetaData = new HashMap<>();
        engineerMetaData.put("fmvtid", staff.getId());
        engineerMetaData.put("fmextID", staff.getId());
        engineerMetaData.put("active", false);
        int code = scheduler.createEngineer(engineerMetaData, flsCredentials);
        logger.info("FLS staff sync status-->" + code);
        if (code == 0) {
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getWorkPlaces(long staffId, long unitId,String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        Organization unit;
        if(ORGANIZATION.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.findOne(unitId);
        } else if(TEAM.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.getOrganizationByTeamId(unitId);
            System.out.println("getting unit from team" + unit.getId());
        } else {
            throw new InternalError("type can not be null");
        }
        if (unit == null) {
            throw new InternalError("Organization not found");
        }
        List<AccessGroup> accessGroups;
        List<Map<String, Object>> units;

        Organization parentOrganization;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parentOrganization = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parentOrganization = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        if (parentOrganization != null) {
            accessGroups = accessGroupRepository.getAccessGroups(parentOrganization.getId());
            units = organizationGraphRepository.getSubOrgHierarchy(parentOrganization.getId());
        } else {

            accessGroups = accessGroupRepository.getAccessGroups(unit.getId());
            units = organizationGraphRepository.getSubOrgHierarchy(unit.getId());
        }

        List<Map<String, Object>> employments;
        List<Map<String, Object>> workPlaces = new ArrayList<>();
        if (units.isEmpty() && unit.isParentOrganization()) {
            employments = new ArrayList<>();
            for (AccessGroup accessGroup : accessGroups) {
                QueryResult queryResult = new QueryResult();
                queryResult.setId(unit.getId());
                queryResult.setName(unit.getName());
                Map<String, Object> employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, unit.getId(), accessGroup.getId());
                if (employment != null && !employment.isEmpty()) {
                    employments.add(employment);
                    queryResult.setAccessable(true);
                } else {
                    queryResult.setAccessable(false);
                }
                Map<String, Object> workPlace = new HashMap<>();
                workPlace.put("id", accessGroup.getId());
                workPlace.put("name", accessGroup.getName());
                workPlace.put("tree", queryResult);
                workPlace.put("employments", employments);
                workPlaces.add(workPlace);
            }
            return workPlaces;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<QueryResult> list;
        List<Long> ids;
        for (AccessGroup accessGroup : accessGroups) {
            list = new ArrayList<>();
            ids = new ArrayList<>();
            employments = new ArrayList<>();
            for (Map<String, Object> unitData : units) {
                Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unitData.get("data")).get("parent");
                long id = (long) parentUnit.get("id");
                Map<String, Object> employment;
                if (ids.contains(id)) {
                    for (QueryResult queryResult : list) {
                        if (queryResult.getId() == id) {
                            List<QueryResult> childs = queryResult.getChildren();
                            QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get("child"), QueryResult.class);
                            employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, child.getId(), accessGroup.getId());
                            if (employment != null && !employment.isEmpty()) {
                                employments.add(employment);
                                child.setAccessable(true);
                            } else {
                                child.setAccessable(false);
                            }
                            childs.add(child);
                            break;
                        }
                    }
                } else {
                    List<QueryResult> queryResults = new ArrayList<>();
                    QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get("child"), QueryResult.class);
                    employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, child.getId(), accessGroup.getId());
                    if (employment != null && !employment.isEmpty()) {
                        employments.add(employment);
                        child.setAccessable(true);
                    } else {
                        child.setAccessable(false);
                    }
                    queryResults.add(child);
                    QueryResult queryResult = new QueryResult((String) parentUnit.get("name"), id, queryResults);
                    employment = employmentGraphRepository.getEmploymentOfParticularRole(staffId, queryResult.getId(), accessGroup.getId());
                    if (employment != null && !employment.isEmpty()) {
                        employments.add(employment);
                        queryResult.setAccessable(true);
                    } else {
                        queryResult.setAccessable(false);
                    }
                    list.add(queryResult);
                }
                ids.add(id);
            }
            Map<String, Object> workPlace = new HashMap<>();
            workPlace.put("id", accessGroup.getId());
            workPlace.put("name", accessGroup.getName());
            workPlace.put("tree", treeStructureService.getTreeStructure(list));
            workPlace.put("employments", employments);
            workPlaces.add(workPlace);
        }
        return workPlaces;
    }

    public Staff editWorkPlace(long staffId, List<Long> teamId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        staffGraphRepository.removeStaffFromAllTeams(staffId);
        return staffGraphRepository.editStaffWorkPlaces(staffId, teamId);
    }

    public Map<String, Object> addPartialLeave(long staffId,long id,String type,PartialLeaveDTO partialLeaveDTO) throws ParseException {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            throw new InternalError("Staff is null");
        }
        Organization unit;

        if(ORGANIZATION.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.findOne(id);
        } else if(TEAM.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.getOrganizationByTeamId(id);
        } else {
            throw new InternalError("Type can't be recognised");
        }
        PartialLeave partialLeave;
        if (partialLeaveDTO.getId() != null) {
            partialLeave = partialLeaveGraphRepository.findOne(partialLeaveDTO.getId());
            partialLeave.setAmount(partialLeaveDTO.getAmount());
            partialLeave.setStartDate(DateConverter.parseDate(partialLeaveDTO.getStartDate()).getTime());
            partialLeave.setEndDate(DateConverter.parseDate(partialLeaveDTO.getEndDate()).getTime());
            partialLeave.setEmploymentId(partialLeaveDTO.getEmploymentId());
            partialLeave.setNote(partialLeaveDTO.getNote());
            partialLeave.setLeaveType(partialLeaveDTO.getLeaveType());
            partialLeaveGraphRepository.save(partialLeave);
        } else {

            Organization parent;
            if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
                parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());
            } else {
                parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
            }

            UnitEmployment unitEmployment;
            if(parent == null){
                unitEmployment = unitEmploymentGraphRepository.getUnitEmployment(unit.getId(), staffId, unit.getId(), EmploymentStatus.PENDING);
            } else {
                unitEmployment = unitEmploymentGraphRepository.getUnitEmployment(parent.getId(), staffId, unit.getId(), EmploymentStatus.PENDING);
            }

            if (unitEmployment == null) {
                throw new InternalError("unit employment is null");
            }

            partialLeave = new PartialLeave();
            partialLeave.setAmount(partialLeaveDTO.getAmount());
            partialLeave.setStartDate(DateConverter.parseDate(partialLeaveDTO.getStartDate()).getTime());
            partialLeave.setEndDate(DateConverter.parseDate(partialLeaveDTO.getEndDate()).getTime());
            partialLeave.setEmploymentId(partialLeaveDTO.getEmploymentId());
            partialLeave.setNote(partialLeaveDTO.getNote());
            partialLeave.setLeaveType(partialLeaveDTO.getLeaveType());
            unitEmployment.getPartialLeaves().add(partialLeave);
            unitEmploymentGraphRepository.save(unitEmployment);
        }
        return parsePartialLeaveObj(partialLeave);
    }

    /**
     * @author prabjot
     * to get partial leaves for particular unit
     * @param staffId
     * @param id {id of unit or team decided by paramter of type}
     * @param type {type can be an organization or team}
     * @return list of partial leaves
     */
    public Map<String, Object> getPartialLeaves(long staffId, long id,String type) {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            throw new InternalError("Staff is null");
        }

        Organization unit;

        if(ORGANIZATION.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.findOne(id);
        } else if(TEAM.equalsIgnoreCase(type)){
            unit = organizationGraphRepository.getOrganizationByTeamId(id);
        } else {
            throw new InternalError("Type can't be recognised");
        }
        List<PartialLeave> partialLeaves = staffGraphRepository.getPartialLeaves(unit.getId(), staffId);
        List<Map<String, Object>> response = new ArrayList<>(partialLeaves.size());
        for (PartialLeave partialLeave : partialLeaves) {
            response.add(parsePartialLeaveObj(partialLeave));
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("partialLeaves", response);
        map.put("leaveTypes", Arrays.asList(PartialLeave.LeaveType.EMERGENCY_LEAVE, PartialLeave.LeaveType.HOLIDAY_LEAVE));
        return map;
    }

    private Map<String, Object> parsePartialLeaveObj(PartialLeave partialLeave) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", partialLeave.getId());
        map.put("startDate", DateConverter.getDate(partialLeave.getStartDate()));
        map.put("endDate", DateConverter.getDate(partialLeave.getEndDate()));
        map.put("leaveType", partialLeave.getLeaveType());
        map.put("amount", partialLeave.getAmount());
        map.put("note", partialLeave.getNote());
        return map;
    }

}
