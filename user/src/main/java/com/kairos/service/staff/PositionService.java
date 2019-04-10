package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.staff.unit_position.EmploymentDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.employment_type.EmploymentStatus;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.PartialLeave;
import com.kairos.persistence.model.staff.PartialLeaveDTO;
import com.kairos.persistence.model.staff.position.*;
import com.kairos.persistence.model.staff.permission.AccessPermission;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.unit_position.query_result.EmploymentQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.*;
import com.kairos.persistence.repository.user.unit_position.EmploymentGraphRepository;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.scheduler.queue.producer.KafkaProducer;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.utils.DateConverter;
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

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.*;


/**
 * Created by prabjot on 19/5/17.
 */
@Transactional
@Service
public class PositionService {

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionService.class);

    public Map<String, Object> savePositionDetail(long unitId, long staffId, StaffPositionDetail staffPositionDetail){
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        Staff objectToUpdate = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(objectToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");
        } else if (objectToUpdate.getExternalId() != null && !objectToUpdate.getExternalId().equals(staffPositionDetail.getTimeCareExternalId()) && userAccessRoleDTO.getStaff()) {
            exceptionService.actionNotPermittedException("message.staff.externalid.notchanged");
        }
        if (isNotNull(objectToUpdate.getExternalId()) && !objectToUpdate.getExternalId().equals(staffPositionDetail.getTimeCareExternalId())) {
            Staff staff = staffGraphRepository.findByExternalId(staffPositionDetail.getTimeCareExternalId());
            if (Optional.ofNullable(staff).isPresent()) {
                exceptionService.duplicateDataException("message.staff.externalid.alreadyexist");
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

        return map;
    }


    public Map<String, Object> createUnitPermission(long unitId, long staffId, long accessGroupId, boolean created) {
        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);

        if (accessGroup.getEndDate() != null && accessGroup.getEndDate().isBefore(DateUtils.getCurrentLocalDate()) && created) {
            exceptionService.actionNotPermittedException("error.access.expired", accessGroup.getName());
        }
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException("message.unit.notfound", unitId);

        }

        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        if (!Optional.ofNullable(parentOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound", unitId);

        }
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        }
        Position position = positionGraphRepository.findPosition(parentOrganization.getId(), staffId);
        if (!Optional.ofNullable(position).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound", staffId);

        }
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO;

        boolean flsSyncStatus = false;
        Map<String, Object> response = new HashMap<>();
        UnitPermission unitPermission = null;
        StaffAccessGroupQueryResult staffAccessGroupQueryResult;
        if (created) {

            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), unitId, staffId, accessGroupId);
            if (Optional.ofNullable(unitPermission).isPresent() && unitPermissionGraphRepository.checkUnitPermissionLinkedWithAccessGroup(unitPermission.getId(), accessGroupId)) {
                exceptionService.dataNotFoundByIdException("message.position.unitpermission.alreadyexist");

            } else if (!Optional.ofNullable(unitPermission).isPresent()) {
                unitPermission = new UnitPermission();
                unitPermission.setOrganization(unit);
                unitPermission.setStartDate(DateUtils.getCurrentDate().getTime());
            }
            unitPermission.setAccessGroup(accessGroup);
            position.getUnitPermissions().add(unitPermission);
            positionGraphRepository.save(position);
            LOGGER.info(unitPermission.getId() + " Currently created Unit Permission ");
            response.put("startDate", DateConverter.getDate(unitPermission.getStartDate()));
            response.put("endDate", DateConverter.getDate(unitPermission.getEndDate()));
            response.put("id", unitPermission.getId());
            staffAccessGroupQueryResult = accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, unitId);


        } else {
            staffAccessGroupQueryResult = accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, unitId);
            // need to remove unit permission
            if (unitPermissionGraphRepository.getAccessGroupRelationShipCountOfStaff(staffId) <= 1) {
                exceptionService.actionNotPermittedException("error.permission.remove");
            }
            unitPermissionGraphRepository.updateUnitPermission(parentOrganization.getId(), unitId, staffId, accessGroupId, false);
        }
        accessGroupPermissionCounterDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAccessGroupQueryResult, AccessGroupPermissionCounterDTO.class);
        accessGroupPermissionCounterDTO.setStaffId(staffId);
        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("created", created + ""));
        genericRestClient.publishRequest(accessGroupPermissionCounterDTO, unitId, true, IntegrationOperation.CREATE, "/counter/dist/staff/access_group/{accessGroupId}/update_kpi", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {
        }, accessGroupId);

        response.put("organizationId", unitId);
        response.put("synInFls", flsSyncStatus);
        return response;
    }


    public List<Map<String, Object>> getPositions(long staffId, long unitId, String type) {

        Organization unit = null;

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


    public void createPositionForUnitManager(Staff staff, Organization parent, Organization unit, long accessGroupId) {

        AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
        if (accessGroup == null) {
            exceptionService.internalServerError("error.position.accessgroup.notfound");

        }
        Position position = new Position();
        position.setName("Working as unit manager");
        position.setStaff(staff);
        UnitPermission unitPermission = new UnitPermission();

        unitPermission.setOrganization(unit);

        //set permission in unit position
        AccessPermission accessPermission = new AccessPermission(accessGroup);
        UnitPermissionAccessPermissionRelationship unitPermissionAccessPermissionRelationship = new UnitPermissionAccessPermissionRelationship(unitPermission, accessPermission);
        unitPermissionAndAccessPermissionGraphRepository.save(unitPermissionAccessPermissionRelationship);
        accessPageService.setPagePermissionToStaff(accessPermission, accessGroup.getId());
        position.getUnitPermissions().add(unitPermission);
        if (parent == null) {
            unit.getPositions().add(position);
            organizationGraphRepository.save(unit);
        } else {
            parent.getPositions().add(position);
            organizationGraphRepository.save(parent);
        }

    }


    public List<Map<String, Object>> getWorkPlaces(long staffId, long unitId, String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        Organization unit = null;
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.findOne(unitId);
        } else if (TEAM.equalsIgnoreCase(type)) {
            unit = organizationGraphRepository.getOrganizationByTeamId(unitId);

        } else {
            exceptionService.internalServerError("error.type.notvalid");

        }
        if (unit == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);

        }
        List<AccessGroup> accessGroups;
        List<Map<String, Object>> units;

        Organization parentOrganization = unit.isParentOrganization() ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        accessGroups = accessGroupRepository.getAccessGroups(parentOrganization.getId());
        units = organizationGraphRepository.getSubOrgHierarchy(parentOrganization.getId());
        List<Map<String, Object>> employments;
        List<Map<String, Object>> workPlaces = new ArrayList<>();
        // This is for parent organization i.e if unit is itself parent organization
        if (units.isEmpty() && unit.isParentOrganization()) {
            employments = new ArrayList<>();
            for (AccessGroup accessGroup : accessGroups) {
                QueryResult queryResult = new QueryResult();
                queryResult.setId(unit.getId());
                queryResult.setName(unit.getName());
                Map<String, Object> employment = positionGraphRepository.getPositionOfParticularRole(staffId, unit.getId(), accessGroup.getId());
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
                            employment = positionGraphRepository.getPositionOfParticularRole(staffId, child.getId(), accessGroup.getId());
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
                    employment = positionGraphRepository.getPositionOfParticularRole(staffId, child.getId(), accessGroup.getId());
                    if (employment != null && !employment.isEmpty()) {
                        employments.add(employment);
                        child.setAccessable(true);
                    } else {
                        child.setAccessable(false);
                    }
                    queryResults.add(child);
                    QueryResult queryResult = new QueryResult((String) parentUnit.get("name"), id, queryResults);
                    employment = positionGraphRepository.getPositionOfParticularRole(staffId, queryResult.getId(), accessGroup.getId());
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
        Organization unit = null;

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
                exceptionService.internalServerError("error.unit.permission.null");

            }

            partialLeave = new PartialLeave();
            partialLeave.setAmount(partialLeaveDTO.getAmount());
            partialLeave.setStartDate(DateConverter.parseDate(partialLeaveDTO.getStartDate()).getTime());
            partialLeave.setEndDate(DateConverter.parseDate(partialLeaveDTO.getEndDate()).getTime());
            partialLeave.setEmploymentId(partialLeaveDTO.getEmploymentId());
            partialLeave.setNote(partialLeaveDTO.getNote());
            partialLeave.setLeaveType(partialLeaveDTO.getLeaveType());
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

        Organization unit = null;

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

    public Position updateEmploymentEndDate(Organization unit, Long staffId) throws Exception {
        Long employmentEndDate = getMaxEmploymentEndDate(staffId);
        return saveEmploymentEndDate(unit, employmentEndDate, staffId, null, null, null);
    }


    public boolean moveToReadOnlyAccessGroup(List<Long> positionIds) {
        List<UnitPermission> unitPermissions;
        UnitPermission unitPermission;
        List<ExpiredPositionsQueryResult> expiredPositionsQueryResults = positionGraphRepository.findExpiredPositionsAccessGroupsAndOrganizationsByEndDate(positionIds);
        accessGroupRepository.deleteAccessGroupRelationAndCustomizedPermissionRelation(positionIds);

        List<Organization> organizations;
        List<Position> positions = new ArrayList<>();
        Position position;

        for (ExpiredPositionsQueryResult expiredPositionsQueryResult : expiredPositionsQueryResults) {
            organizations = expiredPositionsQueryResult.getOrganizations();
            position = expiredPositionsQueryResult.getPosition();
            unitPermissions = expiredPositionsQueryResult.getUnitPermissions();
            List<Long> orgIds = organizations.stream().map(organization -> organization.getId()).collect(Collectors.toList());

            accessGroupRepository.createAccessGroupUnitRelation(orgIds, position.getAccessGroupIdOnPositionEnd());
            AccessGroup accessGroupDB = accessGroupRepository.findById(position.getAccessGroupIdOnPositionEnd()).get();
            for (int currentElement = 0; currentElement< expiredPositionsQueryResult.getOrganizations().size(); currentElement++) {
                unitPermission = unitPermissions.get(currentElement);
                if (!Optional.ofNullable(unitPermission).isPresent()) {
                    unitPermission = new UnitPermission();
                    unitPermission.setOrganization(organizations.get(currentElement));
                    unitPermission.setStartDate(DateUtils.getCurrentDate().getTime());
                }
                unitPermission.setAccessGroup(accessGroupDB);
                position.getUnitPermissions().add(unitPermission);
                currentElement++;
            }
            position.setEmploymentStatus(EmploymentStatus.FORMER);
            positions.add(position);
        }
        if (expiredPositionsQueryResults.size() > 0) {
            positionGraphRepository.saveAll(positions);
        }
        return true;
    }

    public Position updateEmploymentEndDate(Organization unit, Long staffId, Long endDateMillis, Long reasonCodeId, Long accessGroupId) throws Exception {
        Long employmentEndDate = null;
        if (Optional.ofNullable(endDateMillis).isPresent()) {
            employmentEndDate = getMaxEmploymentEndDate(staffId);
        }


        return saveEmploymentEndDate(unit, employmentEndDate, staffId, reasonCodeId, endDateMillis, accessGroupId);
    }

    private Long getMaxEmploymentEndDate(Long staffId) {
        Long employmentEndDate = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> unitPositionsEndDate = employmentGraphRepository.getAllUnitPositionsByStaffId(staffId);
        if (!unitPositionsEndDate.isEmpty()) {
            //java.lang.ClassCastException: java.lang.String cannot be cast to java.time.LocalDate
            LocalDate maxEndDate = LocalDate.parse(unitPositionsEndDate.get(0));
            boolean isEndDateBlank = false;
            //TODO Get unit positions with date more than the sent unitposition's end date at query level itself
            for (String unitPositionEndDateString : unitPositionsEndDate) {
                LocalDate unitPositionEndDate = unitPositionEndDateString == null ? null : LocalDate.parse(unitPositionEndDateString);
                if (!Optional.ofNullable(unitPositionEndDate).isPresent()) {
                    isEndDateBlank = true;
                    break;
                }
                if (maxEndDate.isBefore(unitPositionEndDate)) {
                    maxEndDate = unitPositionEndDate;
                }
            }
            employmentEndDate = isEndDateBlank ? null : DateUtils.getLongFromLocalDate(maxEndDate);
        }
        return employmentEndDate;

    }

    private Position saveEmploymentEndDate(Organization unit, Long employmentEndDate, Long staffId, Long reasonCodeId, Long endDateMillis, Long accessGroupId) throws Exception {

        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        ReasonCode reasonCode = null;
        if (!Optional.ofNullable(parentOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.parentorganization.notfound", unit.getId());
        }

        Position position = positionGraphRepository.findPosition(parentOrganization.getId(), staffId);
        //TODO Commented temporary due to kafka down on QA server
//         userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(employmentEndDate, position.getEndDateMillis(), parentOrganization.getId(), position.getId(),
//             parentOrganization.getTimeZone());
        position.setEndDateMillis(employmentEndDate);
        if (!Optional.ofNullable(employmentEndDate).isPresent()) {
            positionGraphRepository.deletePositionReasonCodeRelation(staffId);
            position.setReasonCode(reasonCode);
        } else if (Optional.ofNullable(employmentEndDate).isPresent() && Objects.equals(employmentEndDate, endDateMillis)) {
            positionGraphRepository.deletePositionReasonCodeRelation(staffId);
            reasonCode = reasonCodeGraphRepository.findById(reasonCodeId).get();
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

    public void endEmploymentProcess(BigInteger schedulerPanelId, Long unitId, Long positionId, LocalDateTime positionEndDate) {
        LocalDateTime started = LocalDateTime.now();
        KairosSchedulerLogsDTO schedulerLogsDTO;
        LocalDateTime stopped;
        String log = null;
        Result result = Result.SUCCESS;


        try {
            List<Long> positionIds = Stream.of(positionId).collect(Collectors.toList());

            moveToReadOnlyAccessGroup(positionIds);
            Long staffId = positionGraphRepository.findStaffByPositionId(positionId);
            activityIntegrationService.deleteShiftsAndOpenShift(unitId, staffId, positionEndDate);
        } catch (Exception ex) {
            log = ex.getMessage();
            result = Result.ERROR;
        }
        stopped = LocalDateTime.now();
        schedulerLogsDTO = new KairosSchedulerLogsDTO(result, log, schedulerPanelId, unitId, DateUtils.getMillisFromLocalDateTime(started), DateUtils.getMillisFromLocalDateTime(stopped), JobSubType.EMPLOYMENT_END);

        kafkaProducer.pushToSchedulerLogsQueue(schedulerLogsDTO);
    }


    public boolean eligibleForMainUnitPosition(EmploymentDTO employmentDTO, long unitPositionId) {
        EmploymentQueryResult employmentQueryResult = employmentGraphRepository.findAllByStaffIdAndBetweenDates(employmentDTO.getStaffId(), employmentDTO.getStartDate().toString(), employmentDTO.getEndDate() == null ? null : employmentDTO.getEndDate().toString(), unitPositionId);
        if (employmentQueryResult != null) {
            if (employmentQueryResult.getEndDate() == null) {
                exceptionService.actionNotPermittedException("message.main_unit_position.exists", employmentQueryResult.getUnitName(), employmentQueryResult.getStartDate());
            } else {
                exceptionService.actionNotPermittedException("message.main_unit_position.exists_with_end_date", employmentQueryResult.getUnitName(), employmentQueryResult.getStartDate(), employmentQueryResult.getEndDate());
            }
        }
        return true;
    }
}
