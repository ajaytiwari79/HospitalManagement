package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.employment_type.EmploymentStatus;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.staff.PartialLeave;
import com.kairos.persistence.model.staff.PartialLeaveDTO;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.position.*;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.redis.RedisService;
import com.kairos.service.scheduler.UserSchedulerJobService;
import com.kairos.service.tree_structure.TreeStructureService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.dto.user.access_permission.AccessGroupRole.MANAGEMENT;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class PositionService {

    public static final String CHILD = "child";
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private AccessGroupService accessGroupService;
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
    private UnitPermissionAndAccessPermissionGraphRepository unitPermissionAndAccessPermissionGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
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
    private GenericRestClient genericRestClient;
    @Inject
    private RedisService redisService;
    @Inject
    private UserSchedulerJobService userSchedulerJobService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionService.class);

    public Map<String, Object> savePositionDetail(long unitId, long staffId, StaffPositionDetail staffPositionDetail) {
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        Staff objectToUpdate = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(objectToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNITID_NOTFOUND);
        } else if (objectToUpdate.getExternalId() != null && !objectToUpdate.getExternalId().equals(staffPositionDetail.getTimeCareExternalId()) && userAccessRoleDTO.getStaff()) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_EXTERNALID_NOTCHANGED);
        }
        if (isNotNull(objectToUpdate.getExternalId()) && !objectToUpdate.getExternalId().equals(staffPositionDetail.getTimeCareExternalId())) {
            Staff staff = staffGraphRepository.findByExternalId(staffPositionDetail.getTimeCareExternalId());
            if (Optional.ofNullable(staff).isPresent()) {
                exceptionService.duplicateDataException(MESSAGE_STAFF_EXTERNALID_ALREADYEXIST);
            }
        }
        Long positionStartDate = DateUtils.getIsoDateInLong(staffPositionDetail.getEmployedSince());
        EngineerType engineerType = engineerTypeGraphRepository.findOne(staffPositionDetail.getEngineerTypeId());
        objectToUpdate.setEmail(staffPositionDetail.getEmail());
        objectToUpdate.setCardNumber(staffPositionDetail.getCardNumber());
        objectToUpdate.setSendNotificationBy(staffPositionDetail.getSendNotificationBy());
        objectToUpdate.setCopyKariosMailToLogin(staffPositionDetail.isCopyKariosMailToLogin());
        objectToUpdate.setEngineerType(engineerType);
        objectToUpdate.setExternalId(staffPositionDetail.getTimeCareExternalId());
        staffGraphRepository.save(objectToUpdate);
        positionGraphRepository.updatePositionStartDateOfStaff(objectToUpdate.getId(), positionStartDate);
        StaffPositionDTO staffPositionDTO = new StaffPositionDTO(objectToUpdate, positionStartDate);
        return retrieveEmploymentDetails(staffPositionDTO);
    }

    public Map<String, Object> retrieveEmploymentDetails(StaffPositionDTO staffPositionDTO) {
        Staff staff = staffPositionDTO.getStaff();
        User user = userGraphRepository.getUserByStaffId(staff.getId());
        Map<String, Object> map = new HashMap<>();
        String employedSince = Optional.ofNullable(staffPositionDTO.getPositionStartDate()).isPresent() ? DateUtils.getDateFromEpoch(staffPositionDTO.getPositionStartDate()).toString() : null;
        map.put("employedSince", employedSince);
        map.put("cardNumber", staff.getCardNumber());
        map.put("sendNotificationBy", staff.getSendNotificationBy());
        map.put("copyKariosMailToLogin", staff.isCopyKariosMailToLogin());
        map.put("email", user.getEmail());
        map.put("profilePic", (isNotNull(staff.getProfilePic())) ? envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic() : staff.getProfilePic());
        map.put("engineerTypeId", staffGraphRepository.getEngineerTypeId(staff.getId()));
        map.put("timeCareExternalId", staff.getExternalId());
        LocalDate dateOfBirth = (user.getDateOfBirth());
        map.put("dateOfBirth", dateOfBirth);
        map.put("tags", staff.getTags());
        return map;
    }


    public Map<String, Object> createUnitPermission(Long unitId, Long staffId, Long accessGroupId, boolean created,LocalDate startDate,LocalDate endDate) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate()) && created) {
            exceptionService.actionNotPermittedException(ERROR_ACCESS_EXPIRED, accessGroup.getName());
        }
        OrganizationBaseEntity unit = organizationBaseRepository.findById(unitId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_UNIT_NOTFOUND, unitId)));
        Organization parentUnit = organizationService.fetchParentOrganization(unitId);
        Position position = positionGraphRepository.findPosition(parentUnit.getId(), staffId);
        if (!Optional.ofNullable(position).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_EMPLOYMENT_NOTFOUND, staffId);
        }
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO;
        boolean flsSyncStatus = false;
        Map<String, Object> response = new HashMap<>();
        StaffAccessGroupQueryResult staffAccessGroupQueryResult;
        if (created) {
            staffAccessGroupQueryResult = setUnitPermission(unitId, staffId, accessGroup, unit, parentUnit, position, response,startDate,endDate);
        } else {
            staffAccessGroupQueryResult = removeUnitPermisssion(unitId, staffId, accessGroupId, parentUnit);
        }
        accessGroupPermissionCounterDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAccessGroupQueryResult, AccessGroupPermissionCounterDTO.class);
        accessGroupPermissionCounterDTO.setStaffId(staffId);
        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("created", created + ""));
        genericRestClient.publishRequest(accessGroupPermissionCounterDTO, unitId, true, IntegrationOperation.CREATE, "/counter/dist/staff/access_group/{accessGroupId}/update_kpi", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {
        }, accessGroupId);
        setUnitWiseAccessRole(unitId, staffId);
        response.put("organizationId", unitId);
        response.put("synInFls", flsSyncStatus);
        return response;
    }

    private StaffAccessGroupQueryResult removeUnitPermisssion(Long unitId, Long staffId, Long accessGroupId, Organization parentUnit) {
        StaffAccessGroupQueryResult staffAccessGroupQueryResult = accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, unitId);
        // need to remove unit permission
        if (unitPermissionGraphRepository.getAccessGroupRelationShipCountOfStaff(staffId) <= 1) {
            exceptionService.actionNotPermittedException(ERROR_PERMISSION_REMOVE);
        }
        unitPermissionGraphRepository.updateUnitPermission(parentUnit.getId(), unitId, staffId, accessGroupId, false);
        return staffAccessGroupQueryResult;
    }

    private StaffAccessGroupQueryResult setUnitPermission(Long unitId, Long staffId, AccessGroup accessGroup, OrganizationBaseEntity unit, Organization parentUnit, Position position, Map<String, Object> response, LocalDate startDate, LocalDate endDate) {
        UnitPermission unitPermission;
        StaffAccessGroupQueryResult staffAccessGroupQueryResult;
        unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentUnit.getId(), unitId, staffId, accessGroup.getId());
        if (!Optional.ofNullable(unitPermission).isPresent()) {
            unitPermission = new UnitPermission();
            if (unit instanceof Organization) {
                unitPermission.setOrganization((Organization) unit);
            } else {
                unitPermission.setUnit((Unit) unit);
            }
            unitPermission.setStartDate(DateUtils.getCurrentDate().getTime());
            unitPermission.setAccessGroup(accessGroup);
            position.getUnitPermissions().add(unitPermission);
            positionGraphRepository.save(position, 2);
        } else {
            unitPermissionGraphRepository.createPermission(accessGroup.getId(), unitPermission.getId());
        }
        unitPermissionGraphRepository.updateDatesInUnitPermission(accessGroup.getId(), unitPermission.getId(),startDate.toString(),endDate==null?null:endDate.toString());
        LOGGER.info(" Currently created Unit Permission ");
        response.put("startDate", getDate(unitPermission.getStartDate()));
        response.put("endDate", getDate(unitPermission.getEndDate()));
        response.put("id", unitPermission.getId());
        staffAccessGroupQueryResult = accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, unitId);
        return staffAccessGroupQueryResult;
    }

    public void setUnitWiseAccessRole(Long unitId, Long staffId) {
        boolean onlyStaff = unitPermissionGraphRepository.isOnlyStaff(unitId, staffId);
        User user = userGraphRepository.getUserByStaffId(staffId);
        user.getUnitWiseAccessRole().put(unitId.toString(), !onlyStaff ? MANAGEMENT.name() : AccessGroupRole.STAFF.name());
        userGraphRepository.save(user);

    }


    public List<Map<String, Object>> getPositions(long staffId, long unitId) {

        OrganizationBaseEntity unit = organizationBaseRepository.findOne(unitId);
        Organization parent = unit instanceof Organization ? (Organization) unit : organizationService.fetchParentOrganization(unitId);
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


    public void createPositionForUnitManager(Staff staff, Organization parent, Unit unit, long accessGroupId) {

        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (accessGroup == null) {
            exceptionService.internalServerError(ERROR_POSITION_ACCESSGROUP_NOTFOUND);

        }
        Position position = new Position();
        position.setName("Working as unit manager");
        position.setStaff(staff);
        UnitPermission unitPermission = new UnitPermission();

        unitPermission.setUnit(unit);

        //set permission in employment
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitPermissionAccessPermissionRelationship unitPermissionAccessPermissionRelationship = new UnitPermissionAccessPermissionRelationship(unitPermission, accessPermission);
        unitPermissionAndAccessPermissionGraphRepository.save(unitPermissionAccessPermissionRelationship);
        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        position.getUnitPermissions().add(unitPermission);
        if (parent == null) {
            unitGraphRepository.save(unit);
        } else {
            parent.getPositions().add(position);
            organizationGraphRepository.save(parent);
        }

    }


    public List<Map<String, Object>> getWorkPlaces(long staffId, long unitId) {
        Organization organization = organizationGraphRepository.findOrganizationOfStaff(staffId);
        OrganizationBaseEntity unit = organizationBaseRepository.findById(unitId).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId)));
        List<AccessGroup> accessGroups = accessGroupRepository.getAccessGroups(organization.getId());
        List<Map<String, Object>> units= unitGraphRepository.getSubOrgHierarchy(organization.getId());
        List<Map<String, Object>> positions;
        List<Map<String, Object>> workPlaces = new ArrayList<>();
        // This is for parent organization i.e if unit is itself parent organization
        if (units.isEmpty() && unit instanceof Organization) {
            positions = new ArrayList<>();
            for (AccessGroup accessGroup : accessGroups) {
                QueryResult queryResult = new QueryResult();
                queryResult.setId(unit.getId());
                queryResult.setName(unit.getName());
                Map<String, Object> employment = positionGraphRepository.getPositionOfParticularRole(staffId, unit.getId(), accessGroup.getId());
                if (employment != null && !employment.isEmpty()) {
                    positions.add(employment);
                    queryResult.setAccessable(true);
                    queryResult.setStartDate((LocalDate) employment.get("startDate"));
                    queryResult.setEndDate((LocalDate) employment.get("endDate"));
                } else {
                    queryResult.setAccessable(false);
                }
                Map<String, Object> workPlace = new HashMap<>();
                workPlace.put("id", accessGroup.getId());
                workPlace.put("name", accessGroup.getName());
                workPlace.put("tree", queryResult);
                workPlace.put("positions", positions);
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
            positions = new ArrayList<>();
            for (Map<String, Object> unitData : units) {
                Map<String, Object> parentUnit = (Map<String, Object>) ((Map<String, Object>) unitData.get("data")).get("parent");
                long id = (long) parentUnit.get("id");
                Map<String, Object> position;
                if (ids.contains(id)) {
                    for (QueryResult queryResult : list) {
                        if (queryResult.getId() == id) {
                            List<QueryResult> childs = queryResult.getChildren();
                            QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get(CHILD), QueryResult.class);
                            position = positionGraphRepository.getPositionOfParticularRole(staffId, child.getId(), accessGroup.getId());
                            if (position != null && !position.isEmpty()) {
                                positions.add(position);
                                child.setAccessable(true);
                                child.setStartDate((LocalDate) position.get("startDate"));
                                child.setEndDate((LocalDate) position.get("endDate"));
                            } else {
                                child.setAccessable(false);
                            }
                            childs.add(child);
                            break;
                        }
                    }
                } else {
                    List<QueryResult> queryResults = new ArrayList<>();
                    QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get(CHILD), QueryResult.class);
                    position = positionGraphRepository.getPositionOfParticularRole(staffId, child.getId(), accessGroup.getId());
                    if (position != null && !position.isEmpty()) {
                        positions.add(position);
                        child.setAccessable(true);
                        child.setStartDate((LocalDate) position.get("startDate"));
                        child.setEndDate((LocalDate) position.get("endDate"));

                    } else {
                        child.setAccessable(false);
                    }
                    queryResults.add(child);
                    QueryResult queryResult = new QueryResult((String) parentUnit.get("name"), id, queryResults);
                    position = positionGraphRepository.getPositionOfParticularRole(staffId, queryResult.getId(), accessGroup.getId());
                    if (position != null && !position.isEmpty()) {
                        positions.add(position);
                        queryResult.setAccessable(true);
                        queryResult.setStartDate((LocalDate) position.get("startDate"));
                        queryResult.setEndDate((LocalDate) position.get("endDate"));
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
            workPlace.put("positions", positions);
            workPlaces.add(workPlace);
        }
        return workPlaces;
    }


    private QueryResult setInfoInChild(long staffId, List<Map<String, Object>> positions, ObjectMapper objectMapper, AccessGroup accessGroup, Map<String, Object> unitData) {
        Map<String, Object> position;
        QueryResult child = objectMapper.convertValue(((Map<String, Object>) unitData.get("data")).get(CHILD), QueryResult.class);
        position = positionGraphRepository.getPositionOfParticularRole(staffId, child.getId(), accessGroup.getId());
        if (position != null && !position.isEmpty()) {
            positions.add(position);
            child.setAccessable(true);
        } else {
            child.setAccessable(false);
        }
        return child;
    }

    public Staff editWorkPlace(long staffId, List<Long> teamId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        staffGraphRepository.removeStaffFromAllTeams(staffId);
        return staffGraphRepository.editStaffWorkPlaces(staffId, teamId);
    }

    public Map<String, Object> addPartialLeave(long staffId, long id, PartialLeaveDTO partialLeaveDTO) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNITID_NOTFOUND);
        }
        Unit unit = unitGraphRepository.findOne(id);
        PartialLeave partialLeave;
        if (partialLeaveDTO.getId() != null) {
            partialLeave = savePartialLeave(partialLeaveDTO);
        } else {
            Organization parent = organizationService.fetchParentOrganization(unit.getId());
            UnitPermission unitPermission;
            unitPermission = unitPermissionGraphRepository.getUnitPermissions(parent.getId(), staffId, unit.getId(), EmploymentStatus.PENDING);
            if (unitPermission == null) {
                exceptionService.internalServerError(ERROR_UNIT_PERMISSION_NULL);
            }
            partialLeave = new PartialLeave();
            partialLeave.setAmount(partialLeaveDTO.getAmount());
            partialLeave.setStartDate(parseDate(partialLeaveDTO.getStartDate()).getTime());
            partialLeave.setEndDate(parseDate(partialLeaveDTO.getEndDate()).getTime());
            partialLeave.setEmploymentId(partialLeaveDTO.getEmploymentId());
            partialLeave.setNote(partialLeaveDTO.getNote());
            partialLeave.setLeaveType(partialLeaveDTO.getLeaveType());
            unitPermissionGraphRepository.save(unitPermission);
        }
        return parsePartialLeaveObj(partialLeave);
    }

    private PartialLeave savePartialLeave(PartialLeaveDTO partialLeaveDTO) {
        PartialLeave partialLeave;
        partialLeave = partialLeaveGraphRepository.findOne(partialLeaveDTO.getId());
        partialLeave.setAmount(partialLeaveDTO.getAmount());
        partialLeave.setStartDate(parseDate(partialLeaveDTO.getStartDate()).getTime());
        partialLeave.setEndDate(parseDate(partialLeaveDTO.getEndDate()).getTime());
        partialLeave.setEmploymentId(partialLeaveDTO.getEmploymentId());
        partialLeave.setNote(partialLeaveDTO.getNote());
        partialLeave.setLeaveType(partialLeaveDTO.getLeaveType());
        partialLeaveGraphRepository.save(partialLeave);
        return partialLeave;
    }

    /**
     * @param staffId
     * @param id      {id of unit or team decided by paramter of type}
     * @return list of partial leaves
     * @author prabjot
     * to get partial leaves for particular unit
     */
    public Map<String, Object> getPartialLeaves(long staffId, long id) {

        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNITID_NOTFOUND);

        }
        OrganizationBaseEntity unit = organizationBaseRepository.findOne(id);
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
        map.put("startDate", getDate(partialLeave.getStartDate()));
        map.put("endDate", getDate(partialLeave.getEndDate()));
        map.put("leaveType", partialLeave.getLeaveType());
        map.put("amount", partialLeave.getAmount());
        map.put("note", partialLeave.getNote());
        return map;
    }

    public Position updatePositionEndDate(Organization organization, Long staffId) throws Exception {
        Long employmentEndDate = getMaxEmploymentEndDate(staffId);
        return saveEmploymentEndDate(organization, employmentEndDate, staffId, null, null, null);
    }

    public Position updatePositionEndDate(Organization organization, Long staffId, Long endDateMillis, Long reasonCodeId, Long accessGroupId,boolean saveAsDraft) throws Exception {
        Long employmentEndDate = null;
        if (Optional.ofNullable(endDateMillis).isPresent() && !saveAsDraft) {
            employmentEndDate = getMaxEmploymentEndDate(staffId);
        }else {
            employmentEndDate=endDateMillis;
        }
        return saveEmploymentEndDate(organization, employmentEndDate, staffId, reasonCodeId, endDateMillis, accessGroupId);
    }

    private Long getMaxEmploymentEndDate(Long staffId) {
        Long positionEndDate = null;
        List<String> employmentsEndDate = employmentGraphRepository.getAllEmploymentsByStaffId(staffId);
        if (!employmentsEndDate.isEmpty() && !employmentsEndDate.contains(null)) {
            //java.lang.ClassCastException: java.lang.String cannot be cast to java.time.LocalDate
            LocalDate maxEndDate = LocalDate.parse(employmentsEndDate.get(0));
            //TODO Get employments with date more than the sent employment's end date at query level itself
            for (String employmentEndDateString : employmentsEndDate) {
                LocalDate employmentEndDate = LocalDate.parse(employmentEndDateString);
                if (maxEndDate.isBefore(employmentEndDate)) {
                    maxEndDate = employmentEndDate;
                }
            }
            positionEndDate = DateUtils.getLongFromLocalDate(maxEndDate);
        }
        return positionEndDate;

    }

    private Position saveEmploymentEndDate(Organization organization, Long employmentEndDate, Long staffId, Long reasonCodeId, Long endDateMillis, Long accessGroupId) throws Exception {

        Organization parentUnit = organizationService.fetchParentOrganization(organization.getId());
        ReasonCode reasonCode = null;
        Position position = positionGraphRepository.findPosition(parentUnit.getId(), staffId);
        //TODO Commented temporary due to kafka down on QA server
//         userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(employmentEndDate, position.getEndDateMillis(), parentOrganization.getId(), position.getId(),
//             parentOrganization.getTimeZone());
        position.setEndDateMillis(employmentEndDate);
        if (!Optional.ofNullable(employmentEndDate).isPresent()) {
            positionGraphRepository.deletePositionReasonCodeRelation(staffId);
            position.setReasonCode(reasonCode);
        } else if (Optional.ofNullable(employmentEndDate).isPresent() && Objects.equals(employmentEndDate, endDateMillis)) {
            positionGraphRepository.deletePositionReasonCodeRelation(staffId);
            if(isNotNull(reasonCodeId)) {
                reasonCode = reasonCodeGraphRepository.findById(reasonCodeId).get();
            }
            position.setReasonCode(reasonCode);
        }
        if (Optional.ofNullable(accessGroupId).isPresent()) {
            position.setAccessGroupIdOnPositionEnd(accessGroupId);
        }
        positionGraphRepository.save(position);

        PositionReasonCodeQueryResult employmentReasonCode = positionGraphRepository.findEmploymentreasonCodeByStaff(staffId);
        position.setReasonCode(employmentReasonCode.getReasonCode());

        return position;

    }

    public boolean eligibleForMainEmployment(EmploymentDTO employmentDTO, long employmentId) {
        EmploymentQueryResult employmentQueryResult = employmentGraphRepository.findAllByStaffIdAndBetweenDates(employmentDTO.getStaffId(), employmentDTO.getStartDate().toString(), employmentDTO.getEndDate() == null ? null : employmentDTO.getEndDate().toString(), employmentId, employmentDTO.getEmploymentSubType());
        if (employmentQueryResult != null) {
            if (employmentQueryResult.getEndDate() == null) {
                exceptionService.actionNotPermittedException(MESSAGE_MAIN_EMPLOYMENT_EXISTS, employmentQueryResult.getUnitName(), employmentQueryResult.getStartDate());
            } else {
                exceptionService.actionNotPermittedException(MESSAGE_MAIN_EMPLOYMENT_EXISTS_WITH_END_DATE, employmentQueryResult.getUnitName(), employmentQueryResult.getStartDate(), employmentQueryResult.getEndDate());
            }
        }
        return true;
    }

    public void createPosition(Organization organization, Staff staff, Long accessGroupId, Long employedSince, Long unitId) {
        Position staffPosition =positionGraphRepository.findByStaffId(staff.getId());
        if(isNull(staffPosition)) {
            Position position = new Position();
            position.setName("Working as staff");
            position.setStaff(staff);
            position.setStartDateMillis(employedSince);
            createStaffPermission(organization, accessGroupId, position, unitId);
            positionGraphRepository.save(position);
            organization.getPositions().add(position);
            organizationGraphRepository.save(organization);
        }else{
            createStaffPermission(organization, accessGroupId, staffPosition, unitId);
            organization.getPositions().add(staffPosition);
            organizationGraphRepository.save(organization);
        }

    }


    private void createStaffPermission(Organization organization, Long accessGroupId, Position position, Long unitId) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (!Optional.ofNullable(accessGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_STAFF_ACCESSGROUP_NOTFOUND, accessGroupId);
        }
        if (accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate())) {
            exceptionService.actionNotPermittedException(ERROR_ACCESS_EXPIRED, accessGroup.getName());
        }
        Long oldAccessGroupId = positionGraphRepository.findAccessGroupIdByPositionId(position.getId());
        boolean isExist =positionGraphRepository.isunitPermissionExist(position.getId(),organization.getId());
        if(!isExist) {
            UnitPermission unitPermission = new UnitPermission();
            unitPermission.setOrganization(organization);
            unitPermission.setAccessGroup(accessGroup);
            position.getUnitPermissions().add(unitPermission);
        }
        if(accessGroupId != oldAccessGroupId){
           positionGraphRepository.updateAccessGroup(accessGroupId,organization.getId(),position.getId());
        }

        Unit unit = organization.getUnits().stream().filter(k -> k.getId().equals(unitId)).findAny().orElse(null);
        if (unit != null) {
            UnitPermission permissionForUnit = new UnitPermission();
            permissionForUnit.setUnit(unit);
            permissionForUnit.setAccessGroup(accessGroup);
            position.getUnitPermissions().add(permissionForUnit);
        }

    }

    public void endPositionProcess() {
        List<Long> positionIds = positionGraphRepository.findAllPositionsIdByEndDate(getCurrentDateMillis());
        if (isCollectionNotEmpty(positionIds)) {
            List<ExpiredPositionsQueryResult> expiredPositionsQueryResults = positionGraphRepository.findExpiredPositionsAccessGroupsAndOrganizationsByEndDate(positionIds);
            accessGroupRepository.deleteAccessGroupRelationAndCustomizedPermissionRelation(positionIds);
            deleteAuthTokenOfUsersByPositionIds(positionIds);
            for (ExpiredPositionsQueryResult expiredPositionsQueryResult : expiredPositionsQueryResults) {
                for (OrganizationBaseEntity unit : expiredPositionsQueryResult.getUnits()) {
                    createUnitPermission(unit.getId(), expiredPositionsQueryResult.getPosition().getStaff().getId(), expiredPositionsQueryResult.getPosition().getAccessGroupIdOnPositionEnd(), true,null,null);
                }
            }
        }
    }

    private void deleteAuthTokenOfUsersByPositionIds(List<Long> positionIds) {
        List<String> userNames = positionGraphRepository.getAllUserByPositionIds(positionIds);
        userNames.forEach(userName -> redisService.invalidateAllTokenOfUser(userName));
    }
}

