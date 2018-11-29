package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.employment.EmploymentDTO;
import com.kairos.dto.user.employment.employment_dto.EmploymentOverlapDTO;
import com.kairos.dto.user.employment.employment_dto.MainEmploymentResultDTO;
import com.kairos.enums.EmploymentStatus;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.country.EngineerType;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.PartialLeave;
import com.kairos.persistence.model.staff.PartialLeaveDTO;
import com.kairos.persistence.model.staff.employment.*;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitEmpAccessRelationship;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPageRepository;
import com.kairos.persistence.repository.user.access_permission.AccessPermissionGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.scheduler.UserToSchedulerQueueService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.utils.DateConverter;
import com.kairos.utils.DateUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.*;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class EmploymentService {

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private IntegrationService integrationService;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
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
    @Inject
    private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private KafkaProducer kafkaProducer;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private UserToSchedulerQueueService userToSchedulerQueueService;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject private OrganizationService organizationService;

    private static final Logger logger = LoggerFactory.getLogger(EmploymentService.class);

    public Map<String, Object> saveEmploymentDetail(long staffId, StaffEmploymentDetail staffEmploymentDetail) throws ParseException {
        Staff objectToUpdate = staffGraphRepository.findOne(staffId);

        if (!Optional.ofNullable(objectToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");
        } else if (objectToUpdate.getExternalId()!=null && !objectToUpdate.getExternalId().equals(staffEmploymentDetail.getTimeCareExternalId())) {
            exceptionService.actionNotPermittedException("message.staff.externalid.notchanged");
        }

        EmploymentUnitPositionQueryResult employmentUnitPosition = unitPositionGraphRepository.getEarliestUnitPositionStartDateAndEmploymentByStaffId(objectToUpdate.getId());
        Long employmentStartDate = DateUtil.getIsoDateInLong(staffEmploymentDetail.getEmployedSince());
        if(Optional.ofNullable(employmentUnitPosition).isPresent()) {
            if(Optional.ofNullable(employmentUnitPosition.getEarliestUnitPositionStartDateMillis()).isPresent()&& employmentStartDate>employmentUnitPosition.getEarliestUnitPositionStartDateMillis())
                exceptionService.actionNotPermittedException("message.employment.startdate.cantexceed.unitpositionstartdate");

            if(Optional.ofNullable(employmentUnitPosition.getEmploymentEndDateMillis()).isPresent()&&employmentStartDate>employmentUnitPosition.getEmploymentEndDateMillis())
                exceptionService.actionNotPermittedException("message.employment.startdate.cantexceed.enddate");

        }

        EngineerType engineerType = engineerTypeGraphRepository.findOne(staffEmploymentDetail.getEngineerTypeId());
        objectToUpdate.setEmail(staffEmploymentDetail.getEmail());
        objectToUpdate.setCardNumber(staffEmploymentDetail.getCardNumber());
        objectToUpdate.setSendNotificationBy(staffEmploymentDetail.getSendNotificationBy());
        objectToUpdate.setCopyKariosMailToLogin(staffEmploymentDetail.isCopyKariosMailToLogin());
        //objectToUpdate.setEmployedSince(DateConverter.parseDate(staffEmploymentDetail.getEmployedSince()).getTime());
        objectToUpdate.setVisitourId(staffEmploymentDetail.getVisitourId());
        objectToUpdate.setEngineerType(engineerType);
        objectToUpdate.setExternalId(staffEmploymentDetail.getTimeCareExternalId());
        staffGraphRepository.save(objectToUpdate);
        employmentGraphRepository.updateEmploymentStartDate(objectToUpdate.getId(), employmentStartDate);
        StaffEmploymentDTO staffEmploymentDTO = new StaffEmploymentDTO(objectToUpdate,employmentStartDate);
        return retrieveEmploymentDetails(staffEmploymentDTO);
    }

    public Map<String, Object> retrieveEmploymentDetails(StaffEmploymentDTO staffEmploymentDTO) {
        Staff staff = staffEmploymentDTO.getStaff();
        User user = userGraphRepository.getUserByStaffId(staff.getId());
        Map<String, Object> map = new HashMap<>();
        //Date employedSince = Optional.ofNullable(staffEmploymentDTO.getEmploymentStartDate()).isPresent() ? DateConverter.getDate(staffEmploymentDTO.getEmploymentStartDate()) : null;
        String employedSince =  Optional.ofNullable(staffEmploymentDTO.getEmploymentStartDate()).isPresent() ? DateUtil.getDateFromEpoch(staffEmploymentDTO.getEmploymentStartDate()).toString() : null;
        map.put("employedSince", employedSince);
        map.put("cardNumber", staff.getCardNumber());
        map.put("sendNotificationBy", staff.getSendNotificationBy());
        map.put("copyKariosMailToLogin", staff.isCopyKariosMailToLogin());
        map.put("email", user.getEmail());
        map.put("profilePic", envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic());
        map.put("visitourId", staff.getVisitourId());
        map.put("engineerTypeId", staffGraphRepository.getEngineerTypeId(staff.getId()));
        map.put("timeCareExternalId", staff.getExternalId());
        LocalDate dateOfBirth = (user.getDateOfBirth());
        map.put("dateOfBirth", dateOfBirth);

        return map;
    }


    public Map<String, Object> createUnitPermission(long unitId, long staffId, long accessGroupId, boolean created) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if( accessGroup.getEndDate()!=null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate()) && created){
            exceptionService.actionNotPermittedException("error.access.expired",accessGroup.getName());
        }
        Organization unit = organizationGraphRepository.findOne(unitId);
        //Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException("message.unit.notfound",unitId);

        }

        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());

        if (!Optional.ofNullable(parentOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",unitId);

        }
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        }
        Employment employment = employmentGraphRepository.findEmployment(parentOrganization.getId(), staffId);
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound",staffId);

        }

        boolean flsSyncStatus = false;
        List<AccessPageQueryResult> accessPageQueryResults;
        Map<String, Object> response = new HashMap<>();
        UnitPermission unitPermission = null;
        if (created) {

            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), unitId, staffId, accessGroupId);
            if(Optional.ofNullable(unitPermission).isPresent() && unitPermissionGraphRepository.checkUnitPermissionLinkedWithAccessGroup(unitPermission.getId(), accessGroupId)) {
                exceptionService.dataNotFoundByIdException("message.employment.unitpermission.alreadyexist",staffId);

            } else if(!Optional.ofNullable(unitPermission).isPresent()){
                unitPermission = new UnitPermission();
                unitPermission.setOrganization(unit);
                unitPermission.setStartDate(DateUtil.getCurrentDate().getTime());
            }
            unitPermission.setAccessGroup(accessGroup);
            employment.getUnitPermissions().add(unitPermission);
            employmentGraphRepository.save(employment);
            logger.info(unitPermission.getId() + " Currently created Unit Permission ");
            response.put("startDate", unitPermission.getStartDate());
            response.put("endDate", unitPermission.getEndDate());
            response.put("id", unitPermission.getId());


        } else {
            // need to remove unit permission
            if(unitPermissionGraphRepository.getAccessGroupRelationShipCountOfStaff(staffId)<=1){
                exceptionService.actionNotPermittedException("error.permission.remove");
            }
            unitPermissionGraphRepository.updateUnitPermission(parentOrganization.getId(), unitId, staffId, accessGroupId, false);
        }

        StaffAccessGroupQueryResult staffAccessGroupQueryResult=accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId,unitId);
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO= ObjectMapperUtils.copyPropertiesByMapper(staffAccessGroupQueryResult,AccessGroupPermissionCounterDTO.class);
        accessGroupPermissionCounterDTO.setStaffId(staffId);
        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("created",created+""));
        genericRestClient.publishRequest(accessGroupPermissionCounterDTO, unitId, true, IntegrationOperation.CREATE, "/counter/dist/staff/access_group/{accessGroupId}/update_kpi", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {},accessGroupId);

        response.put("organizationId", unitId);
        response.put("synInFls", flsSyncStatus);
        return response;
    }


    public List<Map<String, Object>> getEmployments(long staffId, long unitId, String type) {

        Organization unit=null;

        if (ORGANIZATION.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.findOne(unitId);
        } else if (TEAM.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.getOrganizationByTeamId(unitId);
        } else {
            exceptionService.internalServerError("error.type.notvalid");

        }

        Organization parent;
        if (unit.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
            parent = organizationGraphRepository.getParentOrganizationOfCityLevel(unit.getId());

        } else {
            parent = organizationGraphRepository.getParentOfOrganization(unit.getId());
        }

        List<Map<String, Object>> list = new ArrayList<>();

        if (parent == null) {
            for (Map<String, Object> map : unitPermissionGraphRepository.getUnitPermissionsInAllUnits(staffId, unit.getId(), unit.getId())) {
                list.add((Map<String, Object>) map.get("data"));
            }
        } else {
            for (Map<String, Object> map : unitPermissionGraphRepository.getUnitPermissionsInAllUnits(staffId, parent.getId(), unitId)) {
                list.add((Map<String, Object>) map.get("data"));
            }
        }

        return list;
    }



    public void createEmploymentForUnitManager(Staff staff, Organization parent, Organization unit, long accessGroupId) {

        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (accessGroup == null) {
            exceptionService.internalServerError("error.employment.accessgroup.notfound");

        }
        Employment employment = new Employment();
        employment.setName("Working as unit manager");
        employment.setStaff(staff);
        UnitPermission unitPermission = new UnitPermission();

        unitPermission.setOrganization(unit);

        //set permission in unit employment
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitEmpAccessRelationship unitEmpAccessRelationship = new UnitEmpAccessRelationship(unitPermission, accessPermission);
        unitEmpAccessGraphRepository.save(unitEmpAccessRelationship);
        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        employment.getUnitPermissions().add(unitPermission);
        if (parent == null) {
            unit.getEmployments().add(employment);
            organizationGraphRepository.save(unit);
        } else {
            parent.getEmployments().add(employment);
            organizationGraphRepository.save(parent);
        }

    }


    public List<Map<String, Object>> getWorkPlaces(long staffId, long unitId, String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        Organization unit=null;
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.findOne(unitId);
        } else if (TEAM.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.getOrganizationByTeamId(unitId);

        } else {
            exceptionService.internalServerError("error.type.notvalid");

        }
        if (unit == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",unitId);

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

    public Map<String, Object> addPartialLeave(long staffId, long id, String type, PartialLeaveDTO partialLeaveDTO) throws ParseException {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        }
        Organization unit=null;

        if (ORGANIZATION.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.findOne(id);
        } else if (TEAM.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.getOrganizationByTeamId(id);
        } else {
            exceptionService.internalServerError("error.type.notvalid");

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

            UnitPermission unitPermission;
            if (parent == null) {
                unitPermission = unitPermissionGraphRepository.getUnitPermissions(unit.getId(), staffId, unit.getId(), EmploymentStatus.PENDING);
            } else {
                unitPermission = unitPermissionGraphRepository.getUnitPermissions(parent.getId(), staffId, unit.getId(), EmploymentStatus.PENDING);
            }

            if (unitPermission == null) {
                exceptionService.internalServerError("error.unit.employment.null");

            }

            partialLeave = new PartialLeave();
            partialLeave.setAmount(partialLeaveDTO.getAmount());
            partialLeave.setStartDate(DateConverter.parseDate(partialLeaveDTO.getStartDate()).getTime());
            partialLeave.setEndDate(DateConverter.parseDate(partialLeaveDTO.getEndDate()).getTime());
            partialLeave.setEmploymentId(partialLeaveDTO.getEmploymentId());
            partialLeave.setNote(partialLeaveDTO.getNote());
            partialLeave.setLeaveType(partialLeaveDTO.getLeaveType());
            //  unitPermission.getPartialLeaves().add(partialLeave);
            unitPermissionGraphRepository.save(unitPermission);
        }
        return parsePartialLeaveObj(partialLeave);
    }

    /**
     * @param staffId
     * @param id      {id of unit or team decided by paramter of type}
     * @param type    {type can be an organization or team}
     * @return list of partial leaves
     * @author prabjot
     * to get partial leaves for particular unit
     */
    public Map<String, Object> getPartialLeaves(long staffId, long id, String type) {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        }

        Organization unit=null;

        if (ORGANIZATION.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.findOne(id);
        } else if (TEAM.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.getOrganizationByTeamId(id);
        } else {
            exceptionService.internalServerError("error.type.notvalid");

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
    public Employment updateEmploymentEndDate(Organization unit, Long staffId) throws Exception {
        Long employmentEndDate = getMaxEmploymentEndDate(staffId);
        return saveEmploymentEndDate(unit,employmentEndDate, staffId,null,null,null);
    }

    
    public boolean moveToReadOnlyAccessGroup(List<Long> employmentIds) {
        Long curDateMillisStart = DateUtils.getStartOfDay(DateUtil.getCurrentDate()).getTime();
        Long curDateMillisEnd = DateUtils.getEndOfDay(DateUtil.getCurrentDate()).getTime();
        List<UnitPermission> unitPermissions;
        UnitPermission unitPermission;
        List<ExpiredEmploymentsQueryResult> expiredEmploymentsQueryResults = employmentGraphRepository.findExpiredEmploymentsAccessGroupsAndOrganizationsByEndDate(employmentIds);
        accessGroupRepository.deleteAccessGroupRelationAndCustomizedPermissionRelation(employmentIds);

        List<Organization> organizations;
        List<Employment>  employments = expiredEmploymentsQueryResults.isEmpty() ? null : new ArrayList<Employment>();
        int currentElement;
        Employment employment;

        for(ExpiredEmploymentsQueryResult expiredEmploymentsQueryResult: expiredEmploymentsQueryResults) {
            organizations = expiredEmploymentsQueryResult.getOrganizations();
            employment = expiredEmploymentsQueryResult.getEmployment();
            unitPermissions = expiredEmploymentsQueryResult.getUnitPermissions();
            currentElement = 0;
            List<Long> orgIds =  organizations.stream().map(organization -> organization.getId()).collect(Collectors.toList());

            accessGroupRepository.createAccessGroupUnitRelation(orgIds,employment.getAccessGroupIdOnEmploymentEnd());
            AccessGroup accessGroupDB = accessGroupRepository.findById(employment.getAccessGroupIdOnEmploymentEnd()).get();


            for(Organization organziation:expiredEmploymentsQueryResult.getOrganizations()){
                unitPermission = unitPermissions.get(currentElement);
                if(!Optional.ofNullable(unitPermission).isPresent() ) {
                    unitPermission = new UnitPermission();
                    unitPermission.setOrganization(organizations.get(currentElement));
                    unitPermission.setStartDate(DateUtil.getCurrentDate().getTime());
                }
                unitPermission.setAccessGroup(accessGroupDB);
                employment.getUnitPermissions().add(unitPermission);
                currentElement++;
            }
            employment.setEmploymentStatus(EmploymentStatus.FORMER);
            employments.add(employment);
        }
        if(expiredEmploymentsQueryResults.size()>0) {
            employmentGraphRepository.saveAll(employments);
        }
        return true;
    }

    public Employment updateEmploymentEndDate(Organization unit, Long staffId, Long endDateMillis, Long reasonCodeId, Long accessGroupId) throws Exception {
        Long employmentEndDate = null;
        if(Optional.ofNullable(endDateMillis).isPresent()) {
            employmentEndDate = getMaxEmploymentEndDate(staffId);
        }


        return saveEmploymentEndDate(unit,employmentEndDate,staffId,reasonCodeId,endDateMillis,accessGroupId);
    }

    private Long getMaxEmploymentEndDate(Long staffId) {
        Long employmentEndDate = null;
         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> unitPositionsEndDate = unitPositionGraphRepository.getAllUnitPositionsByStaffId(staffId);
            if(!unitPositionsEndDate.isEmpty()) {
                //java.lang.ClassCastException: java.lang.String cannot be cast to java.time.LocalDate
                LocalDate maxEndDate = LocalDate.parse(unitPositionsEndDate.get(0));
                boolean isEndDateBlank = false;
                //TODO Get unit positions with date more than the sent unitposition's end date at query level itself
                for ( String unitPositionEndDateString : unitPositionsEndDate) {
                    LocalDate unitPositionEndDate=unitPositionEndDateString==null?null:LocalDate.parse(unitPositionEndDateString);
                    if (!Optional.ofNullable(unitPositionEndDate).isPresent()) {
                        isEndDateBlank = true;
                        break;
                    }
                    if (maxEndDate.isBefore( unitPositionEndDate)) {
                        maxEndDate = unitPositionEndDate;
                    }
                }
                employmentEndDate = isEndDateBlank ? null : DateUtils.getLongFromLocalDate(maxEndDate);
            }
        return employmentEndDate;

    }

    private Employment saveEmploymentEndDate(Organization unit, Long employmentEndDate, Long staffId,Long reasonCodeId, Long endDateMillis,Long accessGroupId) throws Exception {

        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        ReasonCode reasonCode = null;
        if (!Optional.ofNullable(parentOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.employment.parentorganization.notfound",unit.getId());

        }

        Employment employment = employmentGraphRepository.findEmployment(parentOrganization.getId(),staffId);
        userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(employmentEndDate,employment.getEndDateMillis(),parentOrganization.getId(),employment.getId(),
               parentOrganization.getTimeZone());
        employment.setEndDateMillis(employmentEndDate);
        if(!Optional.ofNullable(employmentEndDate).isPresent()) {
            employmentGraphRepository.deleteEmploymentReasonCodeRelation(staffId);
            employment.setReasonCode(reasonCode);
        }
        else if(Optional.ofNullable(employmentEndDate).isPresent()&&Objects.equals(employmentEndDate,endDateMillis)) {
            employmentGraphRepository.deleteEmploymentReasonCodeRelation(staffId);
            reasonCode = reasonCodeGraphRepository.findById(reasonCodeId).get();
            employment.setReasonCode(reasonCode);
        }
        if(Optional.ofNullable(accessGroupId).isPresent()) {
            employment.setAccessGroupIdOnEmploymentEnd(accessGroupId);
        }
        employmentGraphRepository.save(employment);

        EmploymentReasonCodeQueryResult employmentReasonCode = employmentGraphRepository.findEmploymentreasonCodeByStaff(staffId);
        employment.setReasonCode(employmentReasonCode.getReasonCode());

        return employment;

    }

    public void endEmploymentProcess(BigInteger schedulerPanelId,Long unitId, Long employmentId,LocalDateTime employmentEndDate) {
       LocalDateTime started = LocalDateTime.now();
        KairosSchedulerLogsDTO schedulerLogsDTO;
        LocalDateTime stopped ;
        String log = null;
        Result result = Result.SUCCESS;


        try{
            List<Long> employmentIds = Stream.of(employmentId).collect(Collectors.toList());

            moveToReadOnlyAccessGroup(employmentIds);
            Long staffId = employmentGraphRepository.findStaffByEmployment(employmentId);
            activityIntegrationService.deleteShiftsAndOpenShift(unitId,staffId,employmentEndDate);
        }
        catch(Exception ex) {
            log = ex.getMessage();
            result = Result.ERROR;
        }
        stopped = LocalDateTime.now();
        schedulerLogsDTO = new KairosSchedulerLogsDTO(result,log,schedulerPanelId,unitId,DateUtils.getMillisFromLocalDateTime(started),DateUtils.getMillisFromLocalDateTime(stopped),JobSubType.EMPLOYMENT_END);

        kafkaProducer.pushToSchedulerLogsQueue(schedulerLogsDTO);
    }

    public MainEmploymentResultDTO updateMainEmployment(Long unitId, Long staffId, EmploymentDTO employmentDTO, Boolean confirmMainEmploymentOverriding) {
        if (employmentDTO.getMainEmploymentStartDate().isBefore(LocalDate.now())) {
            exceptionService.invalidRequestException("message.startdate.notlessthan.currentdate");
        }
        Long mainEmploymentStartDate = DateUtil.getDateFromEpoch(employmentDTO.getMainEmploymentStartDate());
        Long mainEmploymentEndDate = null;
        if (employmentDTO.getMainEmploymentEndDate() != null) {
            mainEmploymentEndDate = DateUtil.getDateFromEpoch(employmentDTO.getMainEmploymentEndDate());
            if (employmentDTO.getMainEmploymentStartDate().isAfter(employmentDTO.getMainEmploymentEndDate())) {
                exceptionService.invalidRequestException("message.lastdate.notlessthan.startdate");
            }
        }
        Organization parentOrganization = organizationService.fetchParentOrganization(unitId);
        Boolean userAccessRoleDTO = accessGroupRepository.getStaffAccessRoles(parentOrganization.getId(), unitId, AccessGroupRole.MANAGEMENT.toString(), staffId);
        if (!userAccessRoleDTO) {
            exceptionService.runtimeException("message.mainemployment.permission");
        }
        List<MainEmploymentQueryResult> mainEmploymentQueryResults = staffGraphRepository.getAllMainEmploymentByStaffId(staffId, mainEmploymentStartDate, mainEmploymentEndDate);
        MainEmploymentResultDTO mainEmploymentResultDTO = new MainEmploymentResultDTO();
        DateTimeInterval newEmploymentInterval = new DateTimeInterval(mainEmploymentStartDate, mainEmploymentEndDate);
        List<Employment> employments = new ArrayList<>();
        if (!mainEmploymentQueryResults.isEmpty()) {
            for (MainEmploymentQueryResult mainEmploymentQueryResult : mainEmploymentQueryResults) {
                Employment employment = mainEmploymentQueryResult.getEmployment();
                EmploymentOverlapDTO employmentOverlapDTO = new EmploymentOverlapDTO();
                if (employment.getMainEmploymentEndDate() != null && employmentDTO.getMainEmploymentEndDate() != null) {
                    DateTimeInterval employmentInterval = new DateTimeInterval(DateUtil.getDateFromEpoch(employment.getMainEmploymentStartDate()), DateUtil.getDateFromEpoch(employment.getMainEmploymentEndDate()));
                    if (newEmploymentInterval.containsInterval(employmentInterval)) {
                        exceptionService.invalidRequestException("message.employment.alreadyexist", mainEmploymentQueryResult.getOrganizationName());
                    } else {
                        if (employmentInterval.contains(newEmploymentInterval.getStartDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employment.setMainEmploymentEndDate(newEmploymentInterval.getStartLocalDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else if (employmentInterval.contains(newEmploymentInterval.getEndDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employment.setMainEmploymentStartDate(newEmploymentInterval.getEndLocalDate().plusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        }
                    }
                } else {
                    if (employment.getMainEmploymentEndDate() == null && employmentDTO.getMainEmploymentEndDate() != null) {
                        if (DateUtil.getDateFromEpoch(employment.getMainEmploymentStartDate()) > mainEmploymentStartDate && DateUtil.getDateFromEpoch(employment.getMainEmploymentStartDate()) <= mainEmploymentEndDate) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employmentDTO.setMainEmploymentEndDate(employment.getMainEmploymentStartDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else if (employment.getMainEmploymentStartDate().isBefore(employmentDTO.getMainEmploymentStartDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employment.setMainEmploymentEndDate(newEmploymentInterval.getStartLocalDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else {
                            exceptionService.invalidRequestException("message.employment.alreadyexist", mainEmploymentQueryResult.getOrganizationName());
                        }
                    } else if (employment.getMainEmploymentEndDate() == null && employmentDTO.getMainEmploymentEndDate() == null) {
                        if (employment.getMainEmploymentStartDate().isBefore(employmentDTO.getMainEmploymentStartDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employment.setMainEmploymentEndDate(employmentDTO.getMainEmploymentStartDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else if (employment.getMainEmploymentStartDate().isAfter(employmentDTO.getMainEmploymentStartDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employmentDTO.setMainEmploymentEndDate(employment.getMainEmploymentStartDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else {
                            exceptionService.invalidRequestException("message.employment.alreadyexist", mainEmploymentQueryResult.getOrganizationName());
                        }
                    } else {
                        if (employment.getMainEmploymentStartDate().isAfter(employmentDTO.getMainEmploymentStartDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employmentDTO.setMainEmploymentEndDate(employment.getMainEmploymentStartDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else if (mainEmploymentStartDate > DateUtil.getDateFromEpoch(employment.getMainEmploymentStartDate()) && mainEmploymentStartDate <= DateUtil.getDateFromEpoch(employment.getMainEmploymentEndDate())) {
                            getOldMainEmployment(employmentOverlapDTO, employment, mainEmploymentQueryResult);
                            employment.setMainEmploymentEndDate(employmentDTO.getMainEmploymentStartDate().minusDays(1));
                            getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                        } else if (employment.getMainEmploymentStartDate().isEqual(employmentDTO.getMainEmploymentStartDate())) {
                            exceptionService.invalidRequestException("message.employment.alreadyexist", mainEmploymentQueryResult.getOrganizationName());
                        }
                    }
                }
                if (employment.getMainEmploymentEndDate() != null && employment.getMainEmploymentEndDate().isBefore(employment.getMainEmploymentStartDate())) {
                    employment.setMainEmploymentStartDate(null);
                    employment.setMainEmploymentEndDate(null);
                    employment.setMainEmployment(false);
                    getAfterChangeMainEmployment(employmentOverlapDTO, employment);
                }
                employments.add(employment);
                mainEmploymentResultDTO.getEmploymentOverlapList().add(employmentOverlapDTO);
            }

            if (!confirmMainEmploymentOverriding) {
                mainEmploymentResultDTO.setUpdatedMainEmployment(employmentDTO);
                return mainEmploymentResultDTO;
            } else {
                Employment employment = getEmployment(staffId, employmentDTO);
                employments.add(employment);
                employmentGraphRepository.saveAll(employments);
            }
        } else {
            Employment employment = getEmployment(staffId, employmentDTO);
            employmentGraphRepository.save(employment);
        }
        mainEmploymentResultDTO.setEmploymentOverlapList(null);
        mainEmploymentResultDTO.setUpdatedMainEmployment(employmentDTO);
        return mainEmploymentResultDTO;
    }

    private Employment getEmployment(Long staffId, EmploymentDTO employmentDTO) {
        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        if (employment.getStartDateMillis() > DateUtils.getLongFromLocalDate(employmentDTO.getMainEmploymentStartDate())) {
            exceptionService.runtimeException("message.mainemployment.startdate.notlessthan");
        }
        if (employment.getEndDateMillis() != null && (employment.getEndDateMillis() < DateUtils.getLongFromLocalDate(employmentDTO.getMainEmploymentEndDate()))) {
            exceptionService.runtimeException("message.mainemployment.enddate.notgreaterthan");
        }
        employment.setMainEmploymentEndDate(employmentDTO.getMainEmploymentEndDate());
        employment.setMainEmploymentStartDate(employmentDTO.getMainEmploymentStartDate());
        employment.setMainEmployment(true);
        employmentDTO.setMainEmployment(true);
        return employment;
    }

    private void getOldMainEmployment(EmploymentOverlapDTO employmentOverlapDTO, Employment employment, MainEmploymentQueryResult mainEmploymentQueryResult) {
        employmentOverlapDTO.setMainEmploymentStartDate(employment.getMainEmploymentStartDate());
        employmentOverlapDTO.setMainEmploymentEndDate(employment.getMainEmploymentEndDate());
        employmentOverlapDTO.setOrganizationName(mainEmploymentQueryResult.getOrganizationName());
    }

    private void getAfterChangeMainEmployment(EmploymentOverlapDTO employmentOverlapDTO, Employment employment) {
        if (employment.getStartDateMillis() > DateUtils.getLongFromLocalDate(employment.getMainEmploymentStartDate())) {
            exceptionService.runtimeException("message.mainemployment.startdate.notlessthan");
        }
        if (employment.getEndDateMillis() != null && (employment.getEndDateMillis() < DateUtils.getLongFromLocalDate(employment.getMainEmploymentEndDate()))) {
            exceptionService.runtimeException("message.mainemployment.enddate.notgreaterthan");
        }
        employmentOverlapDTO.setAfterChangeStartDate(employment.getMainEmploymentStartDate());
        employmentOverlapDTO.setAfterChangeEndDate(employment.getMainEmploymentEndDate());
    }

    public boolean removeMainEmployment(Long staffId) {
        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        employment.setMainEmploymentStartDate(null);
        employment.setMainEmploymentEndDate(null);
        employment.setMainEmployment(false);
        employmentGraphRepository.save(employment);
        return true;
    }
}
