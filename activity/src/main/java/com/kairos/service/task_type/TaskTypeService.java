package com.kairos.service.task_type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;

import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.task_type.*;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.model.task_type.*;
import com.kairos.persistence.model.task_type.TaskTypeSkill;
import com.kairos.persistence.repository.repository_impl.CustomTaskTypeRepositoryImpl;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.persistence.repository.task_type.TaskMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeSettingMongoRepository;
import com.kairos.persistence.repository.task_type.TaskTypeSlaConfigMongoRepository;
import com.kairos.rest_client.*;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationLevel;
import com.kairos.dto.user.organization.OrganizationTypeHierarchyQueryResult;
import com.kairos.dto.user.organization.TimeSlot;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.FileUtil;
import com.kairos.utils.external_plateform_shift.GetAllActivitiesResponse;
import com.kairos.utils.external_plateform_shift.TimeCareActivity;
import com.kairos.wrapper.task_type.TaskTypeResourceDTO;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.FileUtil.createDirectory;

/**
 * Created by prabjot on 4/10/16.
 */
@Service
public class TaskTypeService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TaskTypeService.class);

    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject
    private TaskTypeSettingMongoRepository taskTypeSettingMongoRepository;
    @Inject
    private TaskTypeSlaConfigMongoRepository taskTypeSlaConfigMongoRepository;
    @Inject
    private EnvConfig envConfig;

    @Autowired private OrganizationRestClient organizationRestClient;

    @Autowired private GenericIntegrationService genericIntegrationService;

    @Autowired private TimeSlotRestClient timeSlotRestClient;

    @Autowired
    private CustomTaskTypeRepositoryImpl customTaskTypeRepository;



    @Inject
    private TagMongoRepository tagMongoRepository;
    @Autowired
    private ExceptionService exceptionService;
    @Inject private TaskMongoRepository taskMongoRepository;



    public TaskTypeDTO createTaskType(TaskTypeDTO taskTypeDTO, long subServiceId) throws ParseException {

        TaskType taskType = new TaskType(taskTypeDTO.getTitle(), taskTypeDTO.getDescription(),subServiceId, taskTypeDTO.getExpiresOn(), taskTypeDTO.getDuration());
        taskType.setTags(taskTypeDTO.getTags());
        save(taskType);
        return taskType.getBasicTaskTypeInfo();
    }

    /*public List<Map<String, Object>> getTaskTypes(Long subServiceId) {
        List<TaskType> taskTypes = (subServiceId == null) ? taskTypeMongoRepository.findAll() : taskTypeMongoRepository.findBySubServiceIdAndOrganizationId(subServiceId,0L);
        List<Map<String, Object>> response = new ArrayList<>(taskTypes.size());
        for (TaskType taskType : taskTypes) {
            response.add(taskType.getBasicTaskTypeInfo());
        }
        return response;
    }*/

    public List<TaskTypeDTO> getTaskTypes(Long subServiceId) {
        List<TaskType> taskTypes = (subServiceId == null) ? taskTypeMongoRepository.findAll() : taskTypeMongoRepository.findBySubServiceIdAndOrganizationId(subServiceId,0L);
        List<TaskTypeDTO> response = new ArrayList<>(taskTypes.size());
        for (TaskType taskType : taskTypes) {
            response.add(taskType.getBasicTaskTypeInfo());
        }
        return response;
    }

    public List<TaskTypeResponseDTO> getAllTaskTypes(Long subServiceId) {
        return (subServiceId == null) ? customTaskTypeRepository.getAllTaskType() : customTaskTypeRepository.findAllBySubServiceIdAndOrganizationId(subServiceId,0L);
    }

    public void updateStatus(String taskTypeId, boolean status) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (taskType == null) {
            return;
        }
        taskType.setEnabled(status);
        save(taskType);
    }

    public Map<String, Object> saveAgreementSetting(String taskId, String union, String agreement, Date startPeriod, Date endPeriod) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveAgreementSettings(union, agreement, startPeriod, endPeriod);
        save(taskType);

        Map<String,Object> agreementSettings = taskType.getAgreementSettings();
        CountryDTO countryDTO = genericIntegrationService.getCountryByOrganizationService(taskType.getSubServiceId());
        agreementSettings.put("contractTypes",genericIntegrationService.getAllContractType(countryDTO.getId()));
        return agreementSettings;
    }

    public Map<String, Object> getAgreementSettings(String id) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(id));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getAgreementSettings();
    }

    public Map<String, Object> saveBalanceSettings(String taskTypeId, Map<String, Object> balanceSettings) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveBalanceSettings(balanceSettings);
        List<TaskTypeEnum.TaskTypeCount> taskTypeCounts = new ArrayList<>();
        for (String taskTypeCount : (List<String>) balanceSettings.get("taskTypeCount")) {
            taskTypeCounts.add(TaskTypeEnum.TaskTypeCount.getByValue(taskTypeCount));
        }

        List<TaskTypeEnum.TaskTypeInclude> taskTypeIncludes = new ArrayList<>();
        for (String taskTypeInculde : (List<String>) balanceSettings.get("taskTypeIncluded")) {
            taskTypeIncludes.add(TaskTypeEnum.TaskTypeInclude.getByValue(taskTypeInculde));
        }

        List<TaskTypeEnum.TaskTypeDate> taskTypeDates = new ArrayList<>();
        for (String taskTypeDate : (List<String>) balanceSettings.get("taskTypeDate")) {
            taskTypeDates.add(TaskTypeEnum.TaskTypeDate.getByValue(taskTypeDate));
        }

        taskType.setTaskTypeIncluded(taskTypeIncludes);
        taskType.setTaskTypeCount(taskTypeCounts);
        taskType.setTaskTypeDate(taskTypeDates);
        save(taskType);
        return taskType.getBalanceSettings();
    }

    public Map<String, Object> getBalanceSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getBalanceSettings();
    }

    public Map<String, Object> saveCommunicationSettings(String taskTypeId, boolean reminderBySms, boolean notificationBySms, String reminderByText, String smsBeforeArrival, boolean isArrival, String time) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveCommunicationSettings(reminderBySms, notificationBySms, reminderByText, smsBeforeArrival, isArrival, time);

        save(taskType);
        return taskType.getCommunicationSettings();
    }

    public Map<String, Object> getCommunicationSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getCommunicationSettings();
    }

    public Map<String, Object> saveCostIncomeSettings(String taskTypeId, Map<String, Object> incomeSettings) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveIncomeCostSettings(incomeSettings);
        save(taskType);
        return taskType.getIncomeSettings();
    }

    public Map<String, Object> getCostIncomeSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getIncomeSettings();
    }

    public List<String> saveTaskTypeRules(String taskTypeId, List<String> rules) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<TaskTypeEnum.TaskTypeCreation> creationRules = new ArrayList<>(rules.size());
        for (String rule : rules) {
            creationRules.add(TaskTypeEnum.TaskTypeCreation.getByValue(rule));
        }
        taskType.setCreators(creationRules);
        save(taskType);
        return rules;
    }

    public List<String> getTaskTypeRules(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<TaskTypeEnum.TaskTypeCreation> rules = taskType.getCreators();
        List<String> response = new ArrayList<>();
        for (TaskTypeEnum.TaskTypeCreation type : rules) {
            response.add(type.value);
        }
        return response;
    }

    public TaskTypeDefination saveTaskTypeDefinations(String taskTypeId, TaskTypeDefination taskTypeDefination) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.setDefinations(taskTypeDefination);
        save(taskType);
        return taskType.getDefinations();
    }

    public TaskTypeDefination getTaskTypeDefinitions(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getDefinations();
    }

    public void saveTaskTypeDependencies(String taskTypeId, boolean finishToStart) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return;
        }
        taskType.setHasFinishToStart(finishToStart);
        save(taskType);
    }

    public Map<String, Boolean> getTaskTypeDependencies(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put("finishToStart", taskType.isHasFinishToStart());
        return map;
    }

    public Map<String, Object> saveGeneralSettings(String taskTypeId, Map<String, Object> settings) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<Long> organizationTypes = new ArrayList<>();
        for (Integer organizationType : (List<Integer>) settings.get("organizationTypes")) {
            organizationTypes.add(organizationType.longValue());
        }
        settings.put("organizationTypes", organizationTypes);
        List<BigInteger> tagsList = new ArrayList<BigInteger>();
        List<Object> tagList = (List<Object>) settings.get("tags");
        for (Object tag : tagList) {
            tagsList.add( new BigInteger(tag.toString()));
        }
        taskType.setTags(tagsList);
        taskType.saveGeneralSettings(settings);
        save(taskType);
        return taskType.getCommunicationSettings();
    }

    public Map<String, Object> getGeneralSettings(String taskTypeId, String type) {
        String filePath = envConfig.getServerHost() + FORWARD_SLASH ;
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Map<String, Object> response = new HashMap<>();
        // anil maurya used rest template here
        /*for (Map<String, Object> map : countryGraphRepository.getCountryAndOrganizationTypes()) {
            response.put("countries", map.get("countries"));
            response.put("organizationTypes", map.get("types"));
        }

        List<Map<String,Object>> organizationTypes = Collections.emptyList();
        Country country = countryGraphRepository.getCountryByOrganizationService(taskType.getSubServiceId());
        if(country != null){
            OrganizationTypeHierarchyQueryResult organizationTypeHierarchyQueryResult = organizationTypeGraphRepository.getOrganizationTypeHierarchy(country.getId(),taskType.getOrganizationSubTypes());
            organizationTypes = organizationTypeHierarchyQueryResult.getOrganizationTypes();
        }*/
        CountryDTO countryDTO = genericIntegrationService.getCountryByOrganizationService(taskType.getSubServiceId());
        OrganizationTypeHierarchyQueryResult organizationTypeHierarchyQueryResult =
                genericIntegrationService.getOrgTypesHierarchy(countryDTO.getId(),taskType.getOrganizationSubTypes());
        Map<String ,Object> generalSettings = taskType.getGeneralSettings(filePath);
        List<TagDTO> tags = new ArrayList<>();
        if(type != null  && type.equals("Organization")){
            tags = tagMongoRepository.getTagsById(taskType.getTags());
        } else {
            tags = tagMongoRepository.getTagsById(taskType.getTags());
        }
        generalSettings.put("tags",tags);
        response.put("generalSettings", generalSettings);
        response.put("taskTypes", taskTypeMongoRepository.getTaskTypesForCopySettings(taskTypeId));
        response.put("organizationTypes",organizationTypeHierarchyQueryResult.getOrganizationTypes());
        return response;

    }

    public Map<String, Object> saveLoggingSettings(String taskTypeId, boolean hasDateOfChange, boolean hasDateOfCreation) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveLoggingSettings(hasDateOfChange, hasDateOfCreation);
        save(taskType);
        return taskType.getLoggingSettings();
    }

    public Map<String, Object> getLoggingSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getLoggingSettings();
    }


    public Map<String, Object> saveMainTaskSettings(String taskTypeId, boolean isMainTask, boolean hasCompositeShift, List<String> subTask, boolean finishToStart) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveMainTaskSettings(isMainTask, hasCompositeShift, subTask);
        taskType.setHasFinishToStart(finishToStart);
        save(taskType);
        return taskType.getMainTaskSettings();
    }

    public Map<String, Object> getMainTaskSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("taskTypes", taskTypeMongoRepository.getTaskTypesForCopySettings(taskTypeId));
        response.put("main_task", taskType.getMainTaskSettings());
        return response;
    }

    public Map<String, Object> saveNotificationSettings(String taskTypeId, Map<String, Object> settings) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }

        if ((boolean) settings.get("isArrival")) {
            String time = (String) settings.get("time");
            String[] args = time.split(":");
            taskType.setHours(Integer.parseInt(args[0]));
            taskType.setMinutes(Integer.parseInt(args[1]));
        }
        taskType.setArrival((boolean) settings.get("isArrival"));
        save(taskType);
        return settings;
    }

    public Map<String, Object> getNotificationSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Map<String, Object> response = new HashMap<>(4);
        response.put("isArrival", taskType.isArrival());
        response.put("time", taskType.getHours() + ":" + taskType.getMinutes());
        return response;
    }

    public void savePlanningRules(String taskTypeId, boolean isAssignedToClipBoard, boolean useInShiftPlanning, List<String> shiftPlanningPhases) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return;
        }
        List<TaskTypeEnum.ShiftPlanningPhase> planningPhases = null;
        if (useInShiftPlanning) {
            planningPhases = new ArrayList<>();
            for (String phase : shiftPlanningPhases) {
                planningPhases.add(TaskTypeEnum.ShiftPlanningPhase.getByValue(phase));
            }
        }
        taskType.savePlanningRules(isAssignedToClipBoard, useInShiftPlanning, planningPhases);
        save(taskType);
    }

    public Map<String, Object> getPlanningRules(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("isAssignedToClipBoard", taskType.isAssignedToClipBoard());
        map.put("useInShiftPlanning", taskType.isUseInShiftPlanning());
        List<String> planningPhases = new ArrayList<>();
        for (TaskTypeEnum.ShiftPlanningPhase phase : taskType.getShiftPlanningPhases()) {
            planningPhases.add(phase.value);
        }
        map.put("shiftPlanningPhases", planningPhases);
        return map;
    }

    public List<String> saveMethodsForPoints(String taskTypeId, List<String> methods) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<TaskTypeEnum.Points> pointMethods = new ArrayList<>();
        for (String method : methods) {
            pointMethods.add(TaskTypeEnum.Points.getByValue(method));
        }
        taskType.setPointMethods(pointMethods);
        save(taskType);
        return methods;
    }

    public List<String> getMethodsForPoints(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<String> pointMethods = new ArrayList<>();
        for (TaskTypeEnum.Points point : taskType.getPointMethods()) {
            pointMethods.add(point.value);
        }
        return pointMethods;
    }

    public Boolean saveResources(String taskTypeId, TaskTypeResourceDTO taskTypeResourceDTO) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return false;
        }
        taskType.setVehicleRequired(taskTypeResourceDTO.isVehicleRequired());
        taskType.addResources(taskTypeResourceDTO.getResources());
        save(taskType);
        return true;
    }

    public TaskTypeResourceDTO getResources(String taskTypeId) {
        // anil maurya
        // OfficeResourceTypeMetadata are in beacon module currently not used
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            exceptionService.internalError("error.task.type");
        }
        TaskTypeResourceDTO taskTypeResourceDTO = new TaskTypeResourceDTO();
        taskTypeResourceDTO.setVehicleRequired(taskType.isVehicleRequired());
        taskTypeResourceDTO.setResources(taskType.getResources());

        return taskTypeResourceDTO;

    }

    public void saveRestingTime(String taskTypeId, String time) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return;
        }
        String[] args = time.split(":");
        int hours = (args.length == 0) ? 0 : Integer.parseInt(args[0]);
        int minutes = (args.length == 0) ? 0 : Integer.parseInt(args[1]);
        taskType.setRestingHours(hours);
        taskType.setRestingMinutes(minutes);
        save(taskType);
    }

    public Map<String, String> getRestingTime(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("restTime", taskType.getRestingHours() + ":" + taskType.getRestingMinutes());
        return map;

    }

    public void saveSkillsForTaskType(String taskTypeId, List<Map<String, Object>> data) {

        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return;
        }

        List<TaskTypeSkill> taskTypeSkills = new ArrayList<>();
        TaskTypeSkill taskTypeSkill = null;

        ObjectMapper objectMapper = new ObjectMapper();

        for (Map<String, Object> skill : data) {
            taskTypeSkill = objectMapper.convertValue(skill, TaskTypeSkill.class);
            if( skill.get("id") != null  && GenericValidator.isLong(skill.get("id").toString())){
                taskTypeSkill.setSkillId(Long.valueOf( skill.get("id").toString()));
            }
            taskTypeSkills.add(taskTypeSkill);
        }
        taskType.setTaskTypeSkills(taskTypeSkills);
        save(taskType);
    }

    public Map<String, Object> getSkills(String taskTypeId) {


        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        //anil maurya move this code in user micro service and call via rest template
        List<Map<String,Object>> skills;
        if(taskType.getOrganizationId() == 0){
            CountryDTO countryDTO = genericIntegrationService.getCountryByOrganizationService(taskType.getSubServiceId());
            skills = genericIntegrationService.getSkillsByCountryForTaskType(countryDTO.getId());
        } else {
            skills = genericIntegrationService.getSkillsOfOrganization(taskType.getOrganizationId());
        }

        List<Map<String, Object>> filterSkillData = new ArrayList<>();
        for (Map<String, Object> map : skills) {
            filterSkillData.add((Map<String, Object>) map.get("data"));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("showData", taskType.getTaskTypeSkills());
        response.put("treeData",filterSkillData);
        response.put("skillLevel", Skill.SkillLevel.values());
        return response;
    }

    public void saveTaskTypeStaff(String taskTypeId, Map<String, Object> data) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return;
        }
        List<String> assigneeList = (List<String>) data.get("assignee");
        List<String> employees = (List<String>) data.get("employees");
        boolean hasSubcontractors = (boolean) data.get("hasSubcontractors");

        List<TaskTypeEnum.TaskTypeStaff> assigneeEnums = new ArrayList<>();
        List<TaskTypeEnum.TaskTypeStaff> employeeEnums = new ArrayList<>();
        for (String assignee : assigneeList) {
            assigneeEnums.add(TaskTypeEnum.TaskTypeStaff.getByValue(assignee));
        }

        for (String employee : employees) {
            employeeEnums.add(TaskTypeEnum.TaskTypeStaff.getByValue(employee));
        }
        taskType.saveTaskTypeStaff(assigneeEnums, hasSubcontractors, employeeEnums);
        save(taskType);
    }

    public Map<String, Object> getTaskTypeStaff(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<String> assigneeList = new ArrayList<>();
        List<String> employeeList = new ArrayList<>();

        for (TaskTypeEnum.TaskTypeStaff taskTypeStaff : taskType.getAssignee()) {
            if(taskTypeStaff.value != null){
                assigneeList.add(taskTypeStaff.value);
            }
        }

        for (TaskTypeEnum.TaskTypeStaff taskTypeStaff : taskType.getEmployees()) {
            if(taskTypeStaff.value != null){
                employeeList.add(taskTypeStaff.value);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("assignee", assigneeList);
        response.put("employees", employeeList);
        response.put("hasSubcontractors", taskType.isHasSubcontractors());
        return response;
    }

    public void saveTimeFrames(String taskTypeId, Map<String, Object> data) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return;
        }
        /*List<TaskTypeEnum.TaskTypeDays> taskTypeDaysList = new ArrayList<>();
        for (String forbiddenDay : (List<String>) data.get("forbiddenDays")) {
            taskTypeDaysList.add(TaskTypeEnum.TaskTypeDays.getByValue(forbiddenDay));
        }*/

        List<Long> forbiddenDayTypeIds = (List<Long>) data.get("forbiddenDayTypeIds");

        //boolean firstVisit = (boolean) data.get("firstVisit");
        //boolean lastVisit = (boolean) data.get("lastVisit");
        boolean clientPresenceRequired = (boolean) data.get("clientPresenceRequired");
        boolean deliverOutsideUnitHours = (boolean) data.get("deliverOutsideUnitHours");
        //TaskTypeEnum.CauseGroup causeGroup = TaskTypeEnum.CauseGroup.getByValue((String) data.get("causeGroup"));
        //TaskTypeEnum.SequenceGroup sequenceGroup = TaskTypeEnum.SequenceGroup.getByValue((String) data.get("sequenceGroup"));
        taskType.saveTimeFrames(forbiddenDayTypeIds, clientPresenceRequired, deliverOutsideUnitHours);
        save(taskType);
    }

    public Map<String, Object> getDayTypeMapList(List<DayType> dayTypes, TaskType taskType){
        Map<String, Object> map = new HashMap<>();
        map.put("dayTypeList", dayTypes);
        map.put("forbiddenDayTypeIds", taskType.getForbiddenDayTypeIds());
        /*map.put("causeGroup", (taskType.getCauseGroup() == null) ? null : taskType.getCauseGroup().value);
        map.put("sequenceGroup", (taskType.getSequenceGroup() == null) ? null : taskType.getSequenceGroup().value);
        map.put("firstVisit", taskType.isFirstVisit());
        map.put("lastVisit", taskType.isLastVisit());*/
        map.put("clientPresenceRequired", taskType.isClientPresenceRequired());
        map.put("deliverOutsideUnitHours", taskType.isDeliverOutsideUnitHours());
        return map;
    }

    public Map<String, Object> getTimeFrames(Long unitId, String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        /*List<String> taskTypeDays = new ArrayList<>();
        for (TaskTypeEnum.TaskTypeDays taskTypeDay : taskType.getForbiddenDays()) {
            taskTypeDays.add(taskTypeDay.value);
        }*/
        List<DayType> dayTypes=organizationRestClient.getDayTypes(unitId);
        return getDayTypeMapList(dayTypes,taskType);
    }

    public Map<String, Object> getTimeFramesByCountryIdAndTaskTypeId(Long countryId, String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        /*List<String> taskTypeDays = new ArrayList<>();
        for (TaskTypeEnum.TaskTypeDays taskTypeDay : taskType.getForbiddenDays()) {
            taskTypeDays.add(taskTypeDay.value);
        }*/
        List<DayType> dayTypes=organizationRestClient.getDayTypesByCountryId(countryId);
        return getDayTypeMapList(dayTypes,taskType);
    }


    public Map<String, Object> saveTimeRules(String taskTypeId, Map<String, Object> data) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        List<TaskTypeEnum.TimeTypes> timeTypes = new ArrayList<>();
        for (String value : (List<String>) data.get("timeTypes")) {
            timeTypes.add(TaskTypeEnum.TimeTypes.getByValue(value));
        }
        taskType.saveTimeRules(TaskTypeEnum.DurationType.getByValue((String) data.get("durationType")), timeTypes, data);
        save(taskType);
        return taskType.retrieveTimeRules();

    }

    public Map<String, Object> getTimeRules(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.retrieveTimeRules();
    }

    public Map<String, Boolean> saveVisitationSettings(String taskTypeId, boolean onlyVisitatorCanAssignDuration, boolean onlyVisitatorCanTaskFrequency, boolean onlyVisitatorCanAssignToClients) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        taskType.saveVisitationSettings(onlyVisitatorCanAssignDuration, onlyVisitatorCanTaskFrequency, onlyVisitatorCanAssignToClients);
        save(taskType);
        return taskType.getVisitationSettings();
    }

    public Map<String, Boolean> getVisitationSettings(String taskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        return taskType.getVisitationSettings();
    }

    public Map<String, Object> copySettings(String taskTypeId, String sourceTaskTypeId) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        String filePath = envConfig.getServerHost() + FORWARD_SLASH ;
        TaskType sourceTaskType = taskTypeMongoRepository.findOne(new BigInteger(sourceTaskTypeId));
        if (isNullOrDeleted(taskType) || sourceTaskType == null) {
            return null;
        }
        taskType = taskType.copyAllSettings(sourceTaskType);
        taskType.setSourceTaskId(sourceTaskTypeId);
        taskType.setSourceTaskTypeTitle(sourceTaskType.getTitle());
        save(taskType);
        return taskType.getGeneralSettings(filePath);
    }

    /**
     * @auther anil maurya
     * used rest template to check organization
     * @param taskTypeId
     * @param organizationId
     * @param subServiceId
     * @return
     */
    public TaskTypeDTO linkTaskTypesWithOrg(String taskTypeId, long organizationId,
                                            long subServiceId) throws CloneNotSupportedException {

        boolean exist=organizationRestClient.isExistOrganization(organizationId);
        if (!exist) {
            return null;
        }

        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));

        TaskType copyObj = taskTypeMongoRepository.findByOrganizationIdAndRootIdAndSubServiceId(organizationId,new BigInteger(taskTypeId),subServiceId);
        if(copyObj == null){
            copyObj = taskType.clone();
        }
        copyProperties(taskType,copyObj,organizationId,subServiceId);
        save(copyObj);
        copySlaValues(taskType,Arrays.asList(copyObj),organizationId);
        return copyObj.getBasicTaskTypeInfo();

    }


    public TaskType copyProperties(TaskType source, TaskType target,long organizationId,long subServiceId){
        target.setOrganizationId(organizationId);
        target.setSubServiceId(subServiceId);
        target.setRootId(source.getId().toString());
        target.setEnabled(true);
        return target;
    }

    public List<TaskTypeDTO> getTaskTypesOfOrganizations(long organizationId, long subService) {
        List<TaskTypeDTO> data = new ArrayList<>();
        for (TaskType taskType : taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(organizationId,true)) {
            data.add(taskType.getBasicTaskTypeInfo());
        }
        return data;
    }

    public List<TaskType> getAvailableTaskToSubServiceOfOrganization(Long subServiceId) {
        return taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,0L,true);
    }

    public TaskType getTaskTypeById(String taskTypeId) {
        return taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));

    }


    public TaskType createTimeCareTaskType(Map<String, Object> data) {
        if (data != null) {
            String title = String.valueOf(data.get("title"));
            String shortName = String.valueOf(data.get("shortName"));
            String externalId = String.valueOf(data.get("externalId"));
            Long orgnaizationId = Long.valueOf(String.valueOf(data.get("organizationId")));
            Long subServiceId = Long.valueOf(String.valueOf(data.get("subServiceId")));
            String taskTypeSchedule = String.valueOf(data.get("taskTypeSchedule"));
            String taskTypeVisibility = String.valueOf(data.get("taskTypeVisibility"));
            Boolean isBreak = Boolean.valueOf(String.valueOf(data.get("isBreak")));
            Boolean isExportable = Boolean.valueOf(String.valueOf(data.get("isExportable")));
            TaskType taskType;
            taskType = taskTypeMongoRepository.findByExternalId(externalId);
            if (taskType == null) taskType = new TaskType();
            taskType.setTitle(title);
            taskType.setShortName(shortName);
            taskType.setOfTypeBreak(isBreak);
            taskType.setExternalId(externalId);
            taskType.setOrganizationId(orgnaizationId);
            taskType.setExportedToVisitour(isExportable);
            taskType.setTaskTypeSchedule(taskTypeSchedule);
            taskType.setTaskTypeVisibility(taskTypeVisibility);
            taskType.setSubServiceId(subServiceId);
            return save(taskType);
        }
        return null;
    }

    public List<TaskType> getAllTaskTypes() {
        return taskTypeMongoRepository.findAll();
    }

    public TaskType findByExternalId(String externalId) {
        return taskTypeMongoRepository.findByExternalId(externalId);

    }

    public boolean deleteTaskType(String taskTypeId,long organizationId,long subServiceId){
        TaskType taskType = taskTypeMongoRepository.findByRootIdAndOrganizationIdAndSubServiceIdAndIsEnabled(taskTypeId,organizationId,subServiceId,true);
        if(isNullOrDeleted(taskType)){
            return false;
        }
        taskType.setEnabled(false);
        save(taskType);
        return true;
    }

    public List<TaskType> getByOrganization(Long org){

        return taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(org,true);
    }


    public Object linkWithOrganizationTypes(String taskTypeId,Set<Long> organizationSubTypeId, boolean isSelected){

        //anil maurya code commented due to wrong implementation
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        if (isNullOrDeleted(taskType)) {
            return null;
        }
        Long organizationTypeId = null;
        for(long organizationType : organizationSubTypeId){
            organizationTypeId = organizationType;
        }

        if(organizationTypeId == null){
            exceptionService.internalError("error.organization.type");
        }


        List<OrganizationDTO> organizations = organizationRestClient.getOrganizationsByOrganizationType(organizationTypeId);
        if(isSelected){
            Set<Long> subTypes = taskType.getOrganizationSubTypes();
            subTypes.addAll(organizationSubTypeId);
            taskType.setOrganizationSubTypes(subTypes);
            organizationRestClient.linkOrganizationTypeWithService(organizationSubTypeId,taskType.getSubServiceId());
            List<TaskType> taskTypes = new ArrayList<>();
            organizations.forEach(organization -> {
                TaskType copyObj = taskTypeMongoRepository.findByOrganizationIdAndRootIdAndSubServiceId(organization.getId(),new BigInteger(taskTypeId),taskType.getSubServiceId());
                if(copyObj == null){
                    try{
                        copyObj = taskType.clone();
                    } catch (CloneNotSupportedException e){
                        throw new RuntimeException(e);
                    }
                }
                copyProperties(taskType,copyObj,organization.getId(),taskType.getSubServiceId());
                taskTypes.add(copyObj);
            });
            if(!taskTypes.isEmpty()){
                save(taskTypes);
            }
        } else {
            organizationRestClient.deleteLinkingOfOrganizationTypeAndService(organizationSubTypeId,taskType.getSubServiceId());
            taskType.getOrganizationSubTypes().removeAll(organizationSubTypeId);
            organizations.forEach(organization -> {
                deleteTaskType(taskTypeId,organization.getId(),taskType.getSubServiceId());
            });
        }
        save(taskType);
        return true;
    }

    public String getWorkPlacesFromTimeCare(GetAllActivitiesResponse getAllActivitiesResponse, Long unitId){
        try{
            logger.info(" Activities---> " + getAllActivitiesResponse.getGetAllActivitiesResult().size());
            for(TimeCareActivity activity : getAllActivitiesResponse.getGetAllActivitiesResult() ){

                Map<String, Object> activityMetaData = new HashMap<>();
                activityMetaData.put("title",activity.getName());
                activityMetaData.put("shortName",activity.getShortName());
                activityMetaData.put("externalId",activity.getId());
                activityMetaData.put("organizationId",unitId);
                activityMetaData.put("subServiceId", 1196);
                if(activity.getIsStaffing()) activityMetaData.put("taskTypeVisibility","Present");
                else activityMetaData.put("taskTypeVisibility", "Absent");
                if(activity.getWholeDay()) activityMetaData.put("taskTypeSchedule","Full day");
                else activityMetaData.put("taskTypeSchedule","Partially");

                activityMetaData.put("isBreak",activity.getIsBreak());
                activityMetaData.put("isExportable",activity.getIsExportable());

                TaskType taskType = createTimeCareTaskType(activityMetaData);
                logger.info("task type----> "+taskType.getId());

            }
            return "Received";
        }catch (Exception exception) {
            logger.warn("Exception while hitting rest for saving workplaces", exception);
        }
        return null;
    }

    public boolean saveTaskTypeSlaConfig(Long unitId, String taskTypeId, TaskTypeSlaConfigDTO taskTypeSlaConfigDTO){

        TaskTypeSlaConfig taskTypeSlaConfig = taskTypeSlaConfigMongoRepository.findByUnitIdAndTaskTypeIdAndTimeSlotId(unitId,new BigInteger(taskTypeId),
                taskTypeSlaConfigDTO.getTimeSlotId());
        if(taskTypeSlaConfig == null){
            Map<String, Object> timeSlotMap = timeSlotRestClient.getTimeSlotByUnitIdAndTimeSlotId(unitId,taskTypeSlaConfigDTO.getTimeSlotId());
            taskTypeSlaConfig = new TaskTypeSlaConfig(new BigInteger(taskTypeId),unitId, taskTypeSlaConfigDTO.getTimeSlotId(), timeSlotMap.get("name").toString());
        }

        List<SlaPerDayInfo> slaPerDayInfoList = taskTypeSlaConfig.getSlaPerDayInfo() != null ? taskTypeSlaConfig.getSlaPerDayInfo() :  new ArrayList<>();
        return updateSlaValues(slaPerDayInfoList,taskTypeSlaConfig,taskTypeSlaConfigDTO);
    }

    public boolean saveTaskTypeSlaConfigForCountry(Long countryId, BigInteger taskTypeId, TaskTypeSlaConfigDTO taskTypeSlaConfigDTO){

        TaskType taskType = taskTypeMongoRepository.findOne(taskTypeId);
        if(!Optional.ofNullable(taskType).isPresent()){
            exceptionService.internalError("error.task.type");
        }
        TaskTypeSlaConfig taskTypeSlaConfig = taskTypeSlaConfigMongoRepository.findByUnitIdAndTaskTypeIdAndTimeSlotId(taskType.getOrganizationId(),taskTypeId,taskTypeSlaConfigDTO.getTimeSlotId());
        if(taskTypeSlaConfig == null){
            List<TimeSlot> timeSlots = genericIntegrationService.getTimeSlotSetsOfCountry(countryId);
            Optional<TimeSlot> result = timeSlots.stream().filter(timeSlot -> timeSlot.getId().equals(taskTypeSlaConfigDTO.getTimeSlotId())).findFirst();
            if(result.isPresent()){
                taskTypeSlaConfig = new TaskTypeSlaConfig(taskTypeId,taskType.getOrganizationId(),
                        taskTypeSlaConfigDTO.getTimeSlotId(), result.get().getName());
            } else {
                exceptionService.dataNotFoundByIdException("message.timeslot.id");
            }
        }
        List<SlaPerDayInfo> slaPerDayInfoList = taskTypeSlaConfig.getSlaPerDayInfo() != null ? taskTypeSlaConfig.getSlaPerDayInfo() :  new ArrayList<>();
        return updateSlaValues(slaPerDayInfoList,taskTypeSlaConfig,taskTypeSlaConfigDTO);
    }

    private boolean updateSlaValues(List<SlaPerDayInfo> slaPerDayInfoList,TaskTypeSlaConfig taskTypeSlaConfig,TaskTypeSlaConfigDTO taskTypeSlaConfigDTO){

        boolean createNewEntry = true;

        for(SlaPerDayInfo slaPerDayInfo : slaPerDayInfoList){
            if(slaPerDayInfo.getTaskTypeSlaDay().equals(taskTypeSlaConfigDTO.getTaskTypeSlaDay())){
                createNewEntry = false;
                slaPerDayInfo.setSlaStartDuration(taskTypeSlaConfigDTO.getSlaStartDuration());
                slaPerDayInfo.setSlaEndDuration(taskTypeSlaConfigDTO.getSlaEndDuration());
            }
        }

        if(createNewEntry){
            SlaPerDayInfo  addSlaPerDayInfo = new SlaPerDayInfo();
            addSlaPerDayInfo.setTaskTypeSlaDay(taskTypeSlaConfigDTO.getTaskTypeSlaDay());
            addSlaPerDayInfo.setSlaStartDuration(taskTypeSlaConfigDTO.getSlaStartDuration());
            addSlaPerDayInfo.setSlaEndDuration(taskTypeSlaConfigDTO.getSlaEndDuration());
            slaPerDayInfoList.add(addSlaPerDayInfo);
        }
        taskTypeSlaConfig.setSlaPerDayInfo(slaPerDayInfoList);
        save(taskTypeSlaConfig);
        return true;
    }



    public Map<String, Object> getTaskTypeSlaConfig(Long unitId, String taskTypeId){

        Map<String, Object> responseMap = new HashMap();
        //anil maurya call this code via rest template
        //List<Map<String,Object>> currentTimeSlots= timeSlotGraphRepository.getUnitCurrentTimeSlots(unitId);

        List<TimeSlotWrapper> currentTimeSlots=timeSlotRestClient.getCurrentTimeSlot(unitId);
        List<TimeSlotWrapper> timeSlots = new ArrayList<>(currentTimeSlots.size());
        List<Long> timeSlotIds = new ArrayList<>(currentTimeSlots.size());
        for(TimeSlotWrapper standredTimeSlot : currentTimeSlots){
            timeSlots.add(standredTimeSlot);
            timeSlotIds.add(standredTimeSlot.getId());
        }

        List<Map<String,Object>> response = new ArrayList<>();
        List<TaskTypeSlaConfig> taskTypeSlaConfigList = taskTypeSlaConfigMongoRepository.findAllByUnitIdAndTaskTypeIdAndTimeSlotIdIn(unitId,new BigInteger(taskTypeId), timeSlotIds);

        for(TaskTypeSlaConfig taskTypeSlaConfig : taskTypeSlaConfigList){
            Map<String,Object> customResult = new HashMap<>();
            customResult.put("id", taskTypeSlaConfig.getTimeSlotId());
            customResult.put("name", taskTypeSlaConfig.getTimeSlotName());
            for(SlaPerDayInfo slaPerDayInfo : taskTypeSlaConfig.getSlaPerDayInfo()){
                customResult.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, slaPerDayInfo.getTaskTypeSlaDay().name()) ,
                        slaPerDayInfo);
            }
            response.add(customResult);
        }

        responseMap.put("taskTypeSlaConfig",response);
        responseMap.put("currentTimeSlots",timeSlots);
        responseMap.put("taskTypeSlaDays",TaskTypeEnum.TaskTypeSlaDay.values());

        return responseMap;

    }

    public Map<String, Object> getTaskTypeSlaConfigForCountry(BigInteger taskTypeId,Long countryId){

        TaskType taskType = taskTypeMongoRepository.findOne(taskTypeId);
        if(!Optional.ofNullable(taskType).isPresent()){
            exceptionService.internalError("task type not found");
        }

        List<TimeSlot> currentTimeSlots = genericIntegrationService.getTimeSlotSetsOfCountry(countryId);
        List<Long> timeSlotIds = currentTimeSlots.stream().map(currentTimeSlot -> currentTimeSlot.getId()).collect(Collectors.toList());

        List<TaskTypeSlaConfig> taskTypeSlaConfigList = taskTypeSlaConfigMongoRepository.findAllByUnitIdAndTaskTypeIdAndTimeSlotIdIn(taskType.getOrganizationId(),
                taskTypeId, timeSlotIds);

        List<Map<String,Object>> response = new ArrayList<>();
        for(TaskTypeSlaConfig taskTypeSlaConfig : taskTypeSlaConfigList){
            Map<String,Object> customResult = new HashMap<>();
            customResult.put("id", taskTypeSlaConfig.getTimeSlotId());
            customResult.put("name", taskTypeSlaConfig.getTimeSlotName());
            for(SlaPerDayInfo slaPerDayInfo : taskTypeSlaConfig.getSlaPerDayInfo()){
                customResult.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, slaPerDayInfo.getTaskTypeSlaDay().name()) , slaPerDayInfo);
            }
            response.add(customResult);
        }

        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("taskTypeSlaConfig",response);
        responseMap.put("currentTimeSlots",currentTimeSlots);
        responseMap.put("taskTypeSlaDays",TaskTypeEnum.TaskTypeSlaDay.values());

        return responseMap;

    }

    private boolean isNullOrDeleted(TaskType taskType){
        if(taskType == null || !taskType.isEnabled()){
            return true;
        }
        return false;
    }


    public Map<String, Object> uploadPhoto(String taskTypeId, MultipartFile multipartFile) {
        TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
        String filePath = envConfig.getServerHost() + FORWARD_SLASH ;
        if (taskType == null) {
            return null;
        }
        createDirectory(IMAGES_PATH);
        String fileName = DateUtils.getDate().getTime() + multipartFile.getOriginalFilename();
        final String path = IMAGES_PATH + File.separator + fileName;
        try {
            FileUtil.writeFile(path, multipartFile);
        } catch (IOException e) {
            fileName = null;
        } catch (Exception e) {
            fileName = null;
        }
        taskType.setIcon(fileName);
        save(taskType);
        Map<String, Object> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("icon", fileName);
        return map;

    }



    /**
     *  @auther anil m2
     *
     * @param organizationId
     * @return
     */
    public List<TaskType> getTaskTypesByOrganization(Long organizationId){
        return taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(organizationId,true);
    }


    /**
     *
     * @param id
     * @param subServiceId
     * @param type
     * @return
     */
    public HashMap<String, Object> getTaskTypes(long id, long subServiceId,String type) {

        List<TaskTypeResponseDTO> visibleTaskTypes = new ArrayList<>();;
        List<TaskTypeResponseDTO> selectedTaskTypes = new ArrayList<>();
        if(ORGANIZATION.equalsIgnoreCase(type)){
            OrganizationDTO organization=organizationRestClient.getOrganization(id);
            // OrganizationDTO organization = organizationGraphRepository.findOne(id);
            if (organization == null) {
                return null;
            }
            OrganizationDTO parent;
            if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
                // parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());
                parent = organizationRestClient.getParentOrganizationOfCityLevel(organization.getId());

            } else {
                // parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
                parent = organizationRestClient.getParentOfOrganization(organization.getId());
            }
            /*if(parent == null){
                for(TaskType taskType : taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,0,true)){
                    visibleTaskTypes.add(taskType.getBasicTaskTypeInfo());
                }
            } else {
                visibleTaskTypes = new ArrayList<>();
                for(TaskType taskType :  taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,parent.getId(),true)){
                    visibleTaskTypes.add(taskType.getBasicTaskTypeInfo());
                }
            }
            for(TaskType taskType : taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,id,true)){
                selectedTaskTypes.add(taskType.getBasicTaskTypeInfo());
            }*/
           /* if(parent == null){
                visibleTaskTypes.addAll(customTaskTypeRepository.getAllTaskTypeBySubServiceAndOrganizationAndIsEnabled(subServiceId,0,true));
            } else {*/
            //Todo why we use parent id when we don't set organisatio id on creating taskType @yasir
            visibleTaskTypes.addAll(customTaskTypeRepository.getAllTaskTypeBySubServiceAndOrganizationAndIsEnabled(subServiceId,0,true));
            //}
            selectedTaskTypes.addAll(customTaskTypeRepository.getAllTaskTypeBySubServiceAndOrganizationAndIsEnabled(subServiceId,id,true));
        } else if(TEAM.equalsIgnoreCase(type)){
            //OrganizationDTO unit = organizationGraphRepository.getOrganizationByTeamId(id);
            OrganizationDTO unit = organizationRestClient.getOrganizationByTeamId(id);
            if(unit == null){
                exceptionService.internalError("error.organization.team.notfound");
            }
            visibleTaskTypes.addAll(customTaskTypeRepository.getAllTaskTypeBySubServiceAndOrganizationAndIsEnabled(subServiceId,unit.getId(),true));
            selectedTaskTypes.addAll(customTaskTypeRepository.getAllTaskTypeByTeamIdAndSubServiceAndIsEnabled(id,subServiceId,true));

        }

        HashMap<String,Object> response = new HashMap<>();
        response.put("parentOrganizationTypes",visibleTaskTypes);
        response.put("unitTaskTypes",selectedTaskTypes);
        return response;
    }

    /**
     * anil maurya
     * @author prabjot
     * to update task type in organization/team based on type of node
     * @param id id of organization or team will be decided by type parameter
     * @param subServiceId
     * @param taskTypeId
     * @param isSelected if true task type will be added otherwise
     * @param type type can be {organization},{team}
     * @return
     */
    public Map<String,Object> updateTaskType(long id, long subServiceId, String taskTypeId, boolean isSelected, String type) throws CloneNotSupportedException {

        if(ORGANIZATION.equalsIgnoreCase(type)){
            if(isSelected){
                linkTaskTypesWithOrg(taskTypeId,id,subServiceId);
            } else {
                deleteTaskType(taskTypeId,id,subServiceId);
            }
        } else if(TEAM.equalsIgnoreCase(type)){
            TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
            if(taskType == null){
                exceptionService.internalError("error.task.type.notnull");
            }
            if(isSelected){
                taskType.setTeamId(id);
                save(taskType);
            } else {
                taskType.setEnabled(false);
                save(taskType);
            }
        }
        return getTaskTypes(id,subServiceId,type);
    }


    /**
     * @auther anil maurya
     * @param serviceIds
     * @param orgId
     * @return
     */
    public Map<String, Object> getTaskTypeList(List<Long> serviceIds,long orgId){

        Map<String, Object> response = new HashMap<>();
        //  List<TaskType> taskTypes = taskTypeMongoRepository.findBySubServiceIdInAndOrganizationIdAndIsEnabled(serviceIds, orgId, true);
        List<TaskType> taskTypes = taskTypeMongoRepository.findByOrganizationIdAndIsEnabled(orgId,true);
        List<Map<String, Object>> taskTypeList = new ArrayList<>(taskTypes.size());
        for (TaskType taskType : taskTypes) {
            Map<String, Object> taskTypesMap = new HashMap<>();
            taskTypesMap.put("id", taskType.getId());
            taskTypesMap.put("name", taskType.getTitle());
            taskTypesMap.put("colorForGantt", taskType.getColorForGantt());
            taskTypeList.add(taskTypesMap);
        }
        response.put("taskTypeList", taskTypeList);
        return response;
    }

    public List<String> getTaskTypeIdsByServiceIds(List<Long> serviceIds, Long unitId){
        List<String> taskTypeIds = new ArrayList<>();
        List<TaskType> taskTypes = taskTypeMongoRepository.findBySubServiceIdInAndOrganizationIdAndIsEnabled(serviceIds, unitId, true);
        if(!taskTypes.isEmpty()){
            taskTypeIds.addAll(taskTypes.stream().map(taskType -> taskType.getId().toString()).collect(Collectors.toList()));
        }
        return taskTypeIds;
    }

    public List<TaskTypeDTO> createCopiesForTaskType(BigInteger taskTypeId, List<String> taskTypeNames) throws CloneNotSupportedException {
        TaskType taskType = taskTypeMongoRepository.findOne(taskTypeId);
        if(!Optional.ofNullable(taskType).isPresent()){
            logger.error("Incorrect task type id " + taskType);
            exceptionService.dataNotFoundByIdException("meassage.task.type.id");
        }
        List<TaskType> newTaskTypes = new ArrayList<>(taskTypeNames.size());
        taskTypeNames.forEach(taskTypeName->{
            TaskType clonedObject;
            try {
                clonedObject = taskType.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Clone not supported on task type");
            }
            clonedObject.setTitle(taskTypeName);
            newTaskTypes.add(clonedObject);
        });
        save(newTaskTypes);
        copySlaValues(taskType,newTaskTypes,taskType.getOrganizationId());

        return newTaskTypes.stream().map(newTaskType->new TaskTypeDTO(newTaskType.getTitle(),
                newTaskType.getExpiresOn(),newTaskType.getDescription(),newTaskType.getId(),newTaskType.isEnabled())).collect(Collectors.toList());
    }

    private void copySlaValues(TaskType taskType,List<TaskType> clonedTaskTypes,Long unitId) throws CloneNotSupportedException {

        System.out.println("Copying SLA values " + unitId + " task type id " + taskType.getId());

        List<TaskTypeSlaConfig> slaPerDayInfos = taskTypeSlaConfigMongoRepository.findByUnitIdAndTaskTypeId(taskType.getOrganizationId(),taskType.getId());
        List<TaskTypeSlaConfig> clonedTaskTypeSlaDayConfigs = new ArrayList<>(slaPerDayInfos.size());
        for(TaskType clonedTaskType : clonedTaskTypes){
            for(TaskTypeSlaConfig taskTypeSlaConfig : slaPerDayInfos){
                TaskTypeSlaConfig clonedTaskTypeSlaConfig = taskTypeSlaConfig.clone();
                clonedTaskTypeSlaConfig.setTaskTypeId(clonedTaskType.getId());
                clonedTaskTypeSlaConfig.setUnitId(unitId);
                clonedTaskTypeSlaDayConfigs.add(clonedTaskTypeSlaConfig);
            }
        }
        if(!clonedTaskTypeSlaDayConfigs.isEmpty()){
            save(clonedTaskTypeSlaDayConfigs);
        }

    }

    /*public TaskTypeSettingDTO createTaskTypeSetting(TaskTypeSettingDTO taskTypeSettingDTO){
        TaskTypeSetting taskTypeSetting = new TaskTypeSetting(taskTypeSettingDTO.getStaffId(),taskTypeSettingDTO.getTaskTypeId(),taskTypeSettingDTO.getEfficiency());
        save(taskTypeSetting);
        taskTypeSettingDTO.setId(taskTypeSetting.getId());
        return taskTypeSettingDTO;
    }*/

    public TaskTypeSettingDTO updateOrCreateTaskTypeSettingForStaff(Long staffId, TaskTypeSettingDTO taskTypeSettingDTO){
        TaskTypeSetting taskTypeSetting = taskTypeSettingMongoRepository.findByStaffIdAndTaskType(staffId,taskTypeSettingDTO.getTaskTypeId());
        if(taskTypeSetting ==null){
            taskTypeSetting = new TaskTypeSetting(staffId,taskTypeSettingDTO.getTaskTypeId(),taskTypeSettingDTO.getEfficiency());
        }
        //TaskType taskType = taskTypeMongoRepository.findOne(taskTypeSettingDTO.getTaskTypeId());
        taskTypeSetting.setEfficiency(taskTypeSettingDTO.getEfficiency());
        save(taskTypeSetting);
        //taskTypeSettingDTO.setTaskTypeName(taskType.getTitle());
        taskTypeSettingDTO.setId(taskTypeSetting.getId());
        return taskTypeSettingDTO;
    }


    public TaskTypeSettingDTO updateOrCreateTaskTypeSettingForClient(Long clientId,TaskTypeSettingDTO taskTypeSettingDTO){
        TaskTypeSetting taskTypeSetting = taskTypeSettingMongoRepository.findByClientIdAndTaskType(clientId,taskTypeSettingDTO.getTaskTypeId());
        if(taskTypeSetting ==null){
            taskTypeSetting = new TaskTypeSetting(taskTypeSettingDTO.getTaskTypeId(),clientId);
        }
        taskTypeSetting.setDuration(taskTypeSettingDTO.getDuration());
        save(taskTypeSetting);
        List<Task> tasks = taskMongoRepository.findAllBycitizenIdAndTaskTypeId(clientId,taskTypeSetting.getTaskTypeId());
        if(!tasks.isEmpty()){
            tasks.forEach(t->{
                t.setDuration(taskTypeSettingDTO.getDuration());
            });
            save(tasks);
        }
        taskTypeSettingDTO.setId(taskTypeSetting.getId());
        return taskTypeSettingDTO;
    }

    /*public List<TaskTypeSettingDTO> getTaskTypeSettingByStaff(Long staffId){
        return taskTypeSettingMongoRepository.findByStaffId(staffId);
    }*/


    public TaskTypeSettingWrapper getTaskTypeByOrganisationAndStaffSetting(Long organisationId, Long staffId){
        List<TaskTypeSettingDTO> taskTypeSettings = taskTypeSettingMongoRepository.findByStaffId(staffId);
        List<Long> serviceIds = getServiceIds(organisationId);
        List<TaskTypeDTO> taskTypes = taskTypeMongoRepository.getTaskTypesOfOrganisation(organisationId,serviceIds);
        return new TaskTypeSettingWrapper(taskTypes,taskTypeSettings);
    }

    public TaskTypeSettingWrapper getTaskTypeByOrganisationAndClientSetting(Long organisationId, Long clientId){
        List<TaskTypeSettingDTO> taskTypeSettings = taskTypeSettingMongoRepository.findByClientId(clientId);
        Map<BigInteger,TaskTypeSettingDTO> taskTypeSettingDTOMap = taskTypeSettings.stream().collect(Collectors.toMap(k->k.getTaskTypeId(),v->v));
        List<Long> serviceIds = getServiceIds(organisationId);
        List<TaskTypeDTO> taskTypes = taskTypeMongoRepository.getTaskTypesOfOrganisation(organisationId,serviceIds);
        taskTypes.forEach(t->{
            if(!taskTypeSettingDTOMap.containsKey(t.getId())){
                taskTypeSettings.add(new TaskTypeSettingDTO(t.getId(),t.getTitle(),clientId,t.getDuration()));
            }
        });
        return new TaskTypeSettingWrapper(taskTypes,taskTypeSettings);
    }

    public List<Long> getServiceIds(Long organisationId){
        List<Long> serviceIds = new ArrayList<>();
        Map<String, Object> services = genericIntegrationService.getOrganizationServices(organisationId, AppConstants.ORGANIZATION);
        List<Map> service = (List<Map>)services.get("selectedServices");
        service.get(0).get("children");
        service.forEach(t->{
            List<Map> children = (List<Map>)t.get("children");
            children.forEach(c->{
                serviceIds.add(((Integer)c.get("id")).longValue());
            });
        });
        return serviceIds;
    }



}

