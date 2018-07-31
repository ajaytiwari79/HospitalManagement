package com.kairos.service.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.KairosScheduleJobDTO;
import com.kairos.dto.KairosSchedulerLogsDTO;
import com.kairos.enums.EmploymentStatus;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.OrganizationLevel;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.enums.scheduler.Result;
import com.kairos.kafka.producer.KafkaProducer;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessPageQueryResult;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.country.EngineerType;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.*;
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
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.fls_visitour.schedule.Scheduler;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.integration.IntegrationService;
import com.kairos.service.scheduler.IntegrationJobsExecutorService;
import com.kairos.service.tree_structure.TreeStructureService;
import com.kairos.util.DateConverter;
import com.kairos.util.DateUtil;
import com.kairos.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.AppConstants.*;


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

    private static final Logger logger = LoggerFactory.getLogger(EmploymentService.class);

    public Map<String, Object> saveEmploymentDetail(long staffId, StaffEmploymentDetail staffEmploymentDetail) throws ParseException {
        Staff objectToUpdate = staffGraphRepository.findOne(staffId);
        EmploymentUnitPositionQueryResult employmentUnitPosition = unitPositionGraphRepository.getEarliestUnitPositionStartDateAndEmploymentByStaffId(objectToUpdate.getId());
        Long employmentStartDate = DateUtil.getIsoDateInLong(staffEmploymentDetail.getEmployedSince());
        if(Optional.ofNullable(employmentUnitPosition).isPresent()) {
            if(Optional.ofNullable(employmentUnitPosition.getEarliestUnitPositionStartDateMillis()).isPresent()&&employmentStartDate>employmentUnitPosition.getEarliestUnitPositionStartDateMillis())
                exceptionService.actionNotPermittedException("message.employment.startdate.cantexceed.unitpositionstartdate");

            if(Optional.ofNullable(employmentUnitPosition.getEmploymentEndDateMillis()).isPresent()&&employmentStartDate>employmentUnitPosition.getEmploymentEndDateMillis())
                exceptionService.actionNotPermittedException("message.employment.startdate.cantexceed.enddate");

        }

        if (objectToUpdate == null) {
            logger.info("Staff does not found by id {}", staffId);
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        } else if (!objectToUpdate.getExternalId().equals(staffEmploymentDetail.getTimeCareExternalId())) {
           exceptionService.actionNotPermittedException("message.staff.externalid.notchanged");

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
        save(objectToUpdate);
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
        map.put("email", staff.getEmail());
        map.put("profilePic", envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic());
        map.put("visitourId", staff.getVisitourId());
        map.put("engineerTypeId", staffGraphRepository.getEngineerTypeId(staff.getId()));
        map.put("timeCareExternalId", staff.getExternalId());
        LocalDate dateOfBirth = (user.getDateOfBirth()) == null ? null : DateUtils.getLocalDateFromDate(user.getDateOfBirth());
        map.put("dateOfBirth", dateOfBirth);

        return map;
    }


    public Map<String, Object> createUnitPermission(long unitId, long staffId, long accessGroupId, boolean created) {

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
            AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
//            unitPermission = new UnitPermission();
//            unitPermission.setOrganization(unit);
//            unitPermission.setStartDate(DateUtil.getCurrentDate().getTime());
            unitPermission.setAccessGroup(accessGroup);
            employment.getUnitPermissions().add(unitPermission);
            employmentGraphRepository.save(employment);
//            AccessPermission accessPermission = new AccessPermission(accessGroup);
//            accessPermissionGraphRepository.save(accessPermission);
            logger.info(unitPermission.getId() + " Currently created Unit Permission ");
//            unitPermissionGraphRepository.linkUnitPermissionWithAccessPermission(unitPermission.getId(), accessPermission.getId());
//            accessPageRepository.setDefaultPermission(accessPermission.getId(), accessGroupId);
//            accessPageQueryResults = getAccessPages(accessPermission);
//            response.put("accessPage", accessPageQueryResults);
            response.put("startDate", DateConverter.getDate(unitPermission.getStartDate()));
            response.put("endDate", DateConverter.getDate(unitPermission.getEndDate()));
            response.put("id", unitPermission.getId());


            /*unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), unitId, staffId);
            if (Optional.ofNullable(unitPermission).isPresent()) {
                throw new DataNotFoundByIdException("Unit permission already exist" + staffId);
            }
            AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
            unitPermission = new UnitPermission();
            unitPermission.setOrganization(unit);
            unitPermission.setStartDate(DateUtil.getCurrentDate().getTime());
            employment.getUnitPermissions().add(unitPermission);
            employmentGraphRepository.save(employment);
            AccessPermission accessPermission = new AccessPermission(accessGroup);
            accessPermissionGraphRepository.save(accessPermission);
            logger.info(unitPermission.getId() + " Currently created Unit Permission ");
            unitPermissionGraphRepository.linkUnitPermissionWithAccessPermission(unitPermission.getId(), accessPermission.getId());
            accessPageRepository.setDefaultPermission(accessPermission.getId(), accessGroupId);
            accessPageQueryResults = getAccessPages(accessPermission);
            response.put("accessPage", accessPageQueryResults);
            response.put("startDate", DateConverter.getDate(unitPermission.getStartDate()));
            response.put("endDate", DateConverter.getDate(unitPermission.getEndDate()));
            response.put("id", unitPermission.getId());*/

        } else {
            // need to remove unit permission
            unitPermissionGraphRepository.updateUnitPermission(parentOrganization.getId(), unitId, staffId, accessGroupId, false);

        }
//                if (parentOrganization == null) {
//                    unit.getEmployments().add(employment);
//                    organizationGraphRepository.save(unit);
//                } else {
//                    parentOrganization.getEmployments().add(employment);
//                    organizationGraphRepository.save(parentOrganization);
//                }
//                if (accessGroup.isTypeOfTaskGiver())
//                    flsSyncStatus = syncStaffInVisitour(staff, unitId, flsCredentials);
        //     }
            /*else {
                AccessPermission accessPermission;
                if (parentOrganization == null) {
                    accessPermission = unitPermissionGraphRepository.getAccessPermission(unit.getId(), unitId, staffId, accessGroupId);
                } else {
                    accessPermission = unitPermissionGraphRepository.getAccessPermission(parentOrganization.getId(), unitId, staffId, accessGroupId);
                }
                AccessGroup accessGroup = accessGroupRepository.findOne(accessGroupId);
                if (accessPermission == null) {
                    accessPermission = new AccessPermission(accessGroup);
                    accessPermissionGraphRepository.save(accessPermission);
                    unitPermissionGraphRepository.linkUnitPermissionWithAccessPermission(unitPermission.getId(), accessPermission.getId());
                    accessPageRepository.setDefaultPermission(accessPermission.getId(), accessGroupId);
                    accessPageQueryResults = getAccessPages(accessPermission);
                    if (accessGroup.isTypeOfTaskGiver())
                        flsSyncStatus = syncStaffInVisitour(staff, unitId, flsCredentials);
                } else {
                    if (parentOrganization == null) {
                        unitPermissionGraphRepository.updateUnitPermission(unit.getId(), unitId, staffId, accessGroupId, true);
                    } else {
                        unitPermissionGraphRepository.updateUnitPermission(parentOrganization.getId(), unitId, staffId, accessGroupId, true);
                    }
                    accessPageQueryResults = Collections.emptyList();
                    if (accessGroup.isTypeOfTaskGiver())
                        flsSyncStatus = syncStaffInVisitour(staff, unitId, flsCredentials);
                }
            }
        } else {
            if (parentOrganization == null) {
                unitPermissionGraphRepository.updateUnitPermission(unit.getId(), unitId, staffId, accessGroupId, false);
            } else {
                unitPermissionGraphRepository.updateUnitPermission(parentOrganization.getId(), unitId, staffId, accessGroupId, false);
            }
            flsSyncStatus = removeStaffFromFls(staff, flsCredentials);
            accessPageQueryResults = Collections.emptyList();

        }
        if (flsSyncStatus) {
            staff.setVisitourId(staff.getId());
            staffGraphRepository.save(staff);
        }*/

        response.put("organizationId", unitId);
        response.put("synInFls", flsSyncStatus);


        return response;
    }

    public Map<String, Object> updateEmployment(long unitEmploymentId, Map<String, Object> wageDetails) throws
            ParseException {

     /*   wageDetails.put("startDate", DateConverter.parseDate(((String) wageDetails.get("startDate"))).getTime());
        wageDetails.put("endDate", DateConverter.parseDate(((String) wageDetails.get("endDate"))).getTime());

        ObjectMapper objectMapper = new ObjectMapper();
        Wage wage = objectMapper.convertValue(wageDetails, Wage.class);
        UnitPermission unitPermission = unitPermissionGraphRepository.findOne(unitEmploymentId);
        unitPermission.getWages().add(wage);
        unitPermissionGraphRepository.save(unitPermission);
        wageDetails.put("id", wage.getId());
     */
        wageDetails.put("message", "Operation currently disabled");
        return wageDetails;

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

    private List<AccessPageQueryResult> getAccessPages(AccessPermission accessPermission) {
        List<Map<String, Object>> accessPages = accessPageRepository.getStaffPermission(accessPermission.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        List<AccessPageQueryResult> queryResults = new ArrayList<>();
        for (Map<String, Object> accessPage : accessPages) {
            AccessPageQueryResult accessPageQueryResult = objectMapper.convertValue((Map<String, Object>) accessPage.get("data"), AccessPageQueryResult.class);
            queryResults.add(accessPageQueryResult);
        }
        List<AccessPageQueryResult> treeData = accessGroupService.getAccessPageHierarchy(queryResults, queryResults);

        List<AccessPageQueryResult> modules = new ArrayList<>();
        for (AccessPageQueryResult accessPageQueryResult : treeData) {
            if (accessPageQueryResult.isModule()) {
                modules.add(accessPageQueryResult);
            }
        }
        return modules;
    }

    /**
     * @param staff
     * @param unitId
     * @return
     * @author prabjot
     * TODO for visitour testing,i am going to keep contact address of staff same as office address,after i will update it
     */

    public boolean syncStaffInVisitour(Staff staff, long unitId, Map<String, String> flsCredentials) {

            /*
                    By Yasir
                Commented below method as we are no longer using FLS Visitour
             */

        /*logger.info("Syncing staff in fls");

        ContactDetail staffContactDetail = staffGraphRepository.getContactDetail(staff.getId());

        OrganizationContactAddress organizationContactData = organizationGraphRepository.getContactAddressOfOrg(unitId);
        ContactAddress officeAddress = organizationContactData.getContactAddress();
        if (officeAddress == null) {
            throw new InternalError("organization address is null");
        }
        ZipCode officeZipCode = organizationContactData.getZipCode();

        if (officeZipCode == null) {
            throw new InternalError("office zip code can not null");
        }

        List<String> skillsToUpdate = staffGraphRepository.getStaffVisitourIdWithLevel(unitId, staff.getId());

        String visitourSkillRequestData = "";
        for (String skill : skillsToUpdate) {
            visitourSkillRequestData = skill + "," + visitourSkillRequestData;
        }

        Map<String, Object> engineerMetaData = new HashMap<>();

        //personal details
        if (staff.getEngineerType() != null) {
            String type = staff.getEngineerType().getVisitourCode();
            engineerMetaData.put("type", type);
        }

        engineerMetaData.put("fmvtid", staff.getId());
        engineerMetaData.put("fmextID", staff.getId());
        engineerMetaData.put("active", true);
        engineerMetaData.put("prename", staff.getLastName());
        engineerMetaData.put("name", staff.getFirstName());

        //staff contact address
        engineerMetaData.put("scountry", "DK");
        engineerMetaData.put("szip", officeZipCode.getZipCode());
        engineerMetaData.put("scity", officeAddress.getCity());
        engineerMetaData.put("sstreet", officeAddress.getStreet1() + " " + officeAddress.getHouseNumber());

        //personal details
        engineerMetaData.put("email", staff.getEmail());
        if (staff.getContactDetail() != null) {
            engineerMetaData.put("phone", staffContactDetail.getPrivatePhone());
            engineerMetaData.put("mobile", staffContactDetail.getMobilePhone());
        }
        //office address
        engineerMetaData.put("ecountry", "DK");
        engineerMetaData.put("ezip", officeZipCode.getZipCode());
        engineerMetaData.put("ecity", officeAddress.getCity());
        engineerMetaData.put("estreet", officeAddress.getStreet1() + " " + officeAddress.getHouseNumber());

        //skills of staff
        engineerMetaData.put("lskills", visitourSkillRequestData);

        logger.info("AddressDTO to verify from fls" + engineerMetaData);
        int code = scheduler.createEngineer(engineerMetaData, flsCredentials);
        logger.info("FLS staff sync status-->" + code);
        if (code == 0) {
            return true;
        }*/
        return false;

    }


    /*
        By Yasir
    Commented below method as we are no longer using FLS Visitour
 */
    private boolean removeStaffFromFls(Staff staff, Map<String, String> flsCredentials) {
        /*Map<String, Object> engineerMetaData = new HashMap<>();
        engineerMetaData.put("fmvtid", staff.getId());
        engineerMetaData.put("fmextID", staff.getId());
        engineerMetaData.put("active", false);
        int code = scheduler.createEngineer(engineerMetaData, flsCredentials);
        logger.info("FLS staff sync status-->" + code);
        if (code == 0) {
            return true;
        }*/
        return false;
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
    public Employment updateEmploymentEndDate(Organization unit, Long staffId) {
        Long employmentEndDate = getMaxEmploymentEndDate(staffId);
        return saveEmploymentEndDate(unit,employmentEndDate, staffId,null,null,null);
    }

    
    public boolean moveToReadOnlyAccessGroup(List<Long> employmentIds) {
        Long curDateMillisStart = DateUtil.getStartOfDay(DateUtil.getCurrentDate()).getTime();
        Long curDateMillisEnd = DateUtil.getEndOfDay(DateUtil.getCurrentDate()).getTime();
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

    public Employment updateEmploymentEndDate(Organization unit, Long staffId, Long endDateMillis, Long reasonCodeId, Long accessGroupId) {
        Long employmentEndDate = null;
        if(Optional.ofNullable(endDateMillis).isPresent()) {
            employmentEndDate = getMaxEmploymentEndDate(staffId);
        }


        return saveEmploymentEndDate(unit,employmentEndDate,staffId,reasonCodeId,endDateMillis,accessGroupId);
    }

    private Long getMaxEmploymentEndDate(Long staffId) {
        Long employmentEndDate = null;
        List<Long> unitPositionsEndDateMillis = unitPositionGraphRepository.getAllUnitPositionsByStaffId(staffId);
            if(!unitPositionsEndDateMillis.isEmpty()) {
                Long maxEndDate = unitPositionsEndDateMillis.get(0);
                boolean isEndDateBlank = false;
                //TODO Get unit positions with date more than the sent unitposition's end date at query level itself
                for (Long unitPositionEndDateMillis : unitPositionsEndDateMillis) {
                    if (!Optional.ofNullable(unitPositionEndDateMillis).isPresent()) {
                        isEndDateBlank = true;
                        break;
                    }
                    if (maxEndDate < unitPositionEndDateMillis) {
                        maxEndDate = unitPositionEndDateMillis;
                    }
                }
                employmentEndDate = isEndDateBlank ? null : maxEndDate;
            }
        return employmentEndDate;

    }

    private Employment saveEmploymentEndDate(Organization unit, Long employmentEndDate, Long staffId,Long reasonCodeId, Long endDateMillis,Long accessGroupId) {

        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        ReasonCode reasonCode = null;
        if (!Optional.ofNullable(parentOrganization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.employment.parentorganization.notfound",unit.getId());

        }

        Employment employment = employmentGraphRepository.findEmployment(parentOrganization.getId(),staffId);
        KairosScheduleJobDTO scheduledJob;
        if(!employmentEndDate.equals(employment.getEndDateMillis())) {
            IntegrationOperation operation = null;
            if(Optional.ofNullable(employment.getEndDateMillis()).isPresent()&&Optional.ofNullable(employmentEndDate).isPresent()) {

                operation = IntegrationOperation.UPDATE;

            }
            else if(Optional.ofNullable(employment.getEndDateMillis()).isPresent()&&!Optional.ofNullable(employmentEndDate).isPresent()) {
                operation = IntegrationOperation.DELETE;
            }
            else if(!Optional.ofNullable(employment.getEndDateMillis()).isPresent()&&Optional.ofNullable(employmentEndDate).isPresent()) {

                operation = IntegrationOperation.CREATE;
            }

            Long oneTimeTriggerDateMillis = null;
            if(Optional.ofNullable(employmentEndDate).isPresent()) {
                oneTimeTriggerDateMillis = DateUtils.getEndOfDayMillisforUnitFromEpoch(parentOrganization.getTimeZone(),employmentEndDate);
            }
            scheduledJob = new KairosScheduleJobDTO(parentOrganization.getId(),JobType.FUNCTIONAL,JobSubType.EMPLOYMENT_END,BigInteger.valueOf(employment.getId()),
                    operation,oneTimeTriggerDateMillis,true);
            kafkaProducer.pushToJobQueue(scheduledJob);
            //scheduledJob.setOneTimeTriggerDateString();
        }

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

        if(Optional.ofNullable(employmentEndDate).isPresent()&&(DateUtil.getDateFromEpoch(employmentEndDate).compareTo(DateUtil.getTimezonedCurrentDate(unit.getTimeZone().toString()))==0)) {
            //employment = employmentGraphRepository.findEmploymentByStaff(staffId);
            List<Long> employmentIds = Stream.of(employment.getId()).collect(Collectors.toList());
            moveToReadOnlyAccessGroup(employmentIds);
        }
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
}
