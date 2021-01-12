package com.kairos.service.staffing_level;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityValidationError;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDetailsByTimeSlotDTO;
import com.kairos.dto.kpermissions.FieldPermissionUserData;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.OrganizationSkillAndOrganizationTypesDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepositoryImpl;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelTemplateRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftValidatorService;
import com.kairos.service.time_slot.TimeSlotSetService;
import com.kairos.utils.service_util.StaffingLevelUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static com.kairos.utils.service_util.StaffingLevelUtil.initializeUserWiseLogs;

@Service
@Transactional
public class StaffingLevelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffingLevelService.class);
    public static final String YYYY_MM_DD = "yyyy-MM-dd";


    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;

    @Inject
    private PhaseService phaseService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ActivityMongoRepositoryImpl activityMongoRepositoryImpl;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffingLevelTemplateRepository staffingLevelTemplateRepository;
    @Inject
    private StaffingLevelTemplateService staffingLevelTemplateService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private TimeSlotSetService timeSlotSetService;
    @Inject
    private ActivityService activityService;
    @Inject StaffingLevelAvailableCountService staffingLevelAvailableCountService;


    /**
     * @param presenceStaffingLevelDTO
     * @param unitId
     */
    public PresenceStaffingLevelDto createStaffingLevel(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long unitId) {
        LOGGER.debug("saving staffing level organizationId {}", unitId);
        StaffingLevel staffingLevel = null;
        staffingLevel = staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(unitId, DateUtils.onlyDate(presenceStaffingLevelDTO.getCurrentDate()));
        if (Optional.ofNullable(staffingLevel).isPresent()) {
            if (staffingLevel.getPresenceStaffingLevelInterval().isEmpty()) {
                List<StaffingLevelInterval> presenceStaffingLevelIntervals = new ArrayList<>();
                for (StaffingLevelInterval staffingLevelInterval : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval()) {
                    StaffingLevelInterval presenceStaffingLevelInterval = new StaffingLevelInterval(staffingLevelInterval.getSequence(), staffingLevelInterval.getStaffingLevelDuration());
                    if (presenceStaffingLevelDTO.isDraft()) {
                        initializeUserWiseLogs(presenceStaffingLevelInterval);
                    } else {
                        presenceStaffingLevelInterval.addStaffLevelActivity(staffingLevelInterval.getStaffingLevelActivities());
                        presenceStaffingLevelInterval.addStaffLevelSkill(staffingLevelInterval.getStaffingLevelSkills());
                        presenceStaffingLevelInterval.setMinNoOfStaff(presenceStaffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMinNoOfStaff())));
                        presenceStaffingLevelInterval.setMaxNoOfStaff(presenceStaffingLevelInterval.getStaffingLevelActivities().stream().collect(Collectors.summingInt(k -> k.getMaxNoOfStaff())));

                    }
                    presenceStaffingLevelIntervals.add(presenceStaffingLevelInterval);

                }
                staffingLevel.setPresenceStaffingLevelInterval(presenceStaffingLevelIntervals);
            } else {
                exceptionService.duplicateDataException(MESSAGE_STAFFLEVEL_CURRENTDATE, presenceStaffingLevelDTO.getCurrentDate());
            }
        } else {
            staffingLevel = StaffingLevelUtil.buildPresenceStaffingLevels(presenceStaffingLevelDTO, unitId);

        }
        staffingLevelMongoRepository.save(staffingLevel);
        publishStaffingLevel(presenceStaffingLevelDTO, unitId, staffingLevel);
        return presenceStaffingLevelDTO;
    }


    /**
     * @param unitId
     * @return
     * @auther Anil Maurya
     */

    public Map<String, StaffingLevel> getPresenceStaffingLevel(Long unitId, Date startDate, Date endDate) {
        LOGGER.debug("getting staffing level organizationId ,startDate ,endDate {},{},{}", unitId, startDate, endDate);
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndCurrentDateGreaterThanEqualAndCurrentDateLessThanEqualAndDeletedFalseOrderByCurrentDate(unitId, startDate, endDate);
        Map<String, StaffingLevel> staffingLevelsMap = staffingLevels.parallelStream().collect(Collectors.toMap(staffingLevel -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
            LocalDateTime dateTime = DateUtils.asLocalDateTime(staffingLevel.getCurrentDate());
            return dateTime.format(formatter);
        }, staffingLevel -> staffingLevel));
        return staffingLevelsMap;
    }

    public StaffingLevel getPresenceStaffingLevel(Long unitId, Date currentDate) {
        LOGGER.debug("getting staffing level organizationId ,currentDate {},{}", unitId, currentDate);

        return staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(unitId, currentDate);

    }

    public StaffingLevel getPresenceStaffingLevel(BigInteger staffingLevelId) {
        LOGGER.debug("getting staffing level staffingLevelId {}", staffingLevelId);

        return staffingLevelMongoRepository.findById(staffingLevelId).orElseThrow(() -> new DataNotFoundByIdException(convertMessage("Staffing Level Not Found By Id : {}", staffingLevelId)));
    }

    /**
     * @param presenceStaffingLevelDTO
     * @param unitId
     */
    public List<PresenceStaffingLevelDto> updatePresenceStaffingLevel(BigInteger staffingLevelId, Long unitId, PresenceStaffingLevelDto presenceStaffingLevelDTO) {
        LOGGER.info("updating staffing level organizationId and staffingLevelId is {} ,{}", unitId, staffingLevelId);
        List<PresenceStaffingLevelDto> presenceStaffingLevelDtos = new ArrayList<>();
        PresenceStaffingLevelDto clonedObjectToReUse = ObjectMapperUtils.copyPropertiesByMapper(presenceStaffingLevelDTO, PresenceStaffingLevelDto.class);
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdBetweenDates(unitId, presenceStaffingLevelDTO.getStartDate(), presenceStaffingLevelDTO.getEndDate());
        for (StaffingLevel staffingLevel : staffingLevels) {
            StaffingLevelUtil.setUserWiseLogs(staffingLevel, presenceStaffingLevelDTO);
            publishStaffingLevel(presenceStaffingLevelDTO, unitId, staffingLevel);
            presenceStaffingLevelDTO = clonedObjectToReUse;
        }
        if (isCollectionNotEmpty(staffingLevels)) {
            staffingLevelMongoRepository.saveEntities(staffingLevels);
        }
        for (StaffingLevel staffingLevel : staffingLevels) {
            presenceStaffingLevelDtos.add(ObjectMapperUtils.copyPropertiesByMapper(staffingLevel, PresenceStaffingLevelDto.class));
        }
        return presenceStaffingLevelDtos;
    }


    private void publishStaffingLevel(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long unitId, StaffingLevel staffingLevel) {
        BeanUtils.copyProperties(staffingLevel, presenceStaffingLevelDTO, new String[]{"presenceStaffingLevelInterval", "absenceStaffingLevelInterval"});
        presenceStaffingLevelDTO.setPresenceStaffingLevelInterval(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().stream()
                .sorted(Comparator.comparing(StaffingLevelInterval::getSequence)).collect(Collectors.toList()));
        StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
        plannerSyncService.publishStaffingLevel(unitId, staffingLevelPlanningDTO, IntegrationOperation.CREATE);
        presenceStaffingLevelDTO.setUpdatedAt(staffingLevel.getUpdatedAt());
    }

    /**
     * create default staffing level when not present for selected date
     *
     * @param unitId
     * @return
     */
    private StaffingLevel createDefaultStaffingLevel(Long unitId, Date currentDate) {

        Duration duration = new Duration(LocalTime.MIN, LocalTime.MAX);
        StaffingLevelSetting staffingLevelSetting = new StaffingLevelSetting(15, duration);

        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, currentDate, null);
        LocalDate date = LocalDate.now();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeekCount = date.get(woy);
        StaffingLevel staffingLevel = new StaffingLevel(currentDate, currentWeekCount, unitId, phase.getId(), staffingLevelSetting);
        List<StaffingLevelInterval> staffingLevelIntervals = new ArrayList<>();
        int startTimeCounter = 0;
        LocalTime startTime = LocalTime.MIN;
        for (int i = 0; i <= 95; i++) {
            StaffingLevelInterval staffingLevelInterval = new StaffingLevelInterval(i, 0, 0, new Duration(startTime.plusMinutes(startTimeCounter),
                    startTime.plusMinutes(startTimeCounter += 15)));
            staffingLevelInterval.setAvailableNoOfStaff(0);
            staffingLevelIntervals.add(staffingLevelInterval);
        }
        List<StaffingLevelInterval> absenceStaffingLevels = new ArrayList<>();
        absenceStaffingLevels.add(new StaffingLevelInterval(0, 0, duration));
        staffingLevel.setPresenceStaffingLevelInterval(staffingLevelIntervals);
        staffingLevel.setAbsenceStaffingLevelInterval(absenceStaffingLevels);
        return staffingLevel;
    }

    /**
     * @param unitId
     * @return
     * @auther anil maurya
     */

    public Map<String, Object> getActivityTypesAndSkillsByUnitId(Long unitId) {
        OrganizationSkillAndOrganizationTypesDTO organizationSkillAndOrganizationTypesDTO =
                userIntegrationService.getOrganizationSkillOrganizationSubTypeByUnitId(unitId);
        List<ActivityDTO> activityTypeList = activityMongoRepository.findAllActivityByOrganizationGroupWithCategoryName(unitId, false);

        Map<ActivityCategoryDTO, List<ActivityDTO>> activityTypeCategoryListMap = activityTypeList.stream().collect(
                Collectors.groupingBy(activityType -> new ActivityCategoryDTO(activityType.getCategoryId(), activityType.getCategoryName()))
        );
        List<ActivityCategoryListDTO> activityCategoryListDTOS = activityTypeCategoryListMap.entrySet().stream().map(activity -> new ActivityCategoryListDTO(activity.getKey(),
                activity.getValue())).collect(Collectors.toList());
        Map<String, Object> activityTypesAndSkills = new HashMap<>();
        activityTypesAndSkills.put("activities", activityCategoryListDTOS);
        activityTypesAndSkills.put("orgazationSkill", organizationSkillAndOrganizationTypesDTO.getAvailableSkills());
        return activityTypesAndSkills;
    }

    public Map<String, Object> getPhaseAndDayTypesForStaffingLevel(Long unitId, Date proposedDate) {
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, proposedDate,null);
        List<DayTypeDTO> dayTypes = dayTypeService.getDayTypeByDate(UserContext.getUserDetails().getCountryId(),proposedDate);
        Map<String, Object> mapOfPhaseAndDayType = new HashMap<>();
        mapOfPhaseAndDayType.put("phase", phase);
        mapOfPhaseAndDayType.put("dayType", dayTypes.isEmpty() ? dayTypes.get(0) : Collections.EMPTY_LIST);
        return mapOfPhaseAndDayType;

    }

    public void submitShiftPlanningInfoToPlanner(Long unitId, Date startDate, Date endDate) {
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndCurrentDateGreaterThanEqualAndCurrentDateLessThanEqualAndDeletedFalseOrderByCurrentDate(unitId, startDate, endDate);

        Map<String, Object> shiftPlanningInfo = new HashMap<>();
        Object[] objects = getStaffingLevelDto(staffingLevels);
        shiftPlanningInfo.put("staffingLevel", (List<ShiftPlanningStaffingLevelDTO>) objects[0]);
        List<BigInteger> activityIds = new ArrayList<BigInteger>((Set<BigInteger>) objects[1]);
        List<ActivityDTO> activityDTOS = activityMongoRepositoryImpl.getAllActivityWithTimeType(activityIds);
        shiftPlanningInfo.put("unitId", unitId);
        shiftPlanningInfo.put("activities", activityDTOS);
        Set<Long> expertiseIds = activityDTOS.stream().flatMap(a -> a.getExpertises().stream()).collect(Collectors.toSet());
        shiftPlanningInfo.put("staffs", userIntegrationService.getStaffInfo(unitId, expertiseIds));
        submitShiftPlanningProblemToPlanner(shiftPlanningInfo);
    }

    @Async
    public Map<String, Object> submitShiftPlanningProblemToPlanner(Map<String, Object> shiftPlanningInfo) {
        final String baseUrl = "http://192.168.6.211:8081/api/taskPlanning/planner/submitRecomendationProblem";

        JSONObject postBody = new JSONObject(shiftPlanningInfo);
        HttpClient client = HttpClientBuilder.create().build();

        HttpUriRequest request = createPostRequest(postBody, null, null, baseUrl);
        StringBuilder result = new StringBuilder();
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            postBody = new JSONObject(result.toString());
        } catch (JSONException ex) {
            return null;
        }
        return postBody.toMap();
    }

    private HttpUriRequest createPostRequest(JSONObject body, Map<String, Object> urlParameters, Map<String, String> headers, String url) {
        HttpPost postRequest = new HttpPost(url);
        if (headers == null) headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        postRequest = (HttpPost) setHeaders(headers, postRequest);
        if (urlParameters != null) {
            List<BasicNameValuePair> parametersList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : urlParameters.entrySet()) {
                parametersList.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parametersList);
                postRequest.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (body != null) {
            ByteArrayEntity entity = new ByteArrayEntity(body.toString().getBytes());
            postRequest.setEntity(entity);
        }
        return postRequest;
    }

    private HttpUriRequest setHeaders(Map<String, String> headers, HttpUriRequest request) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }


    private Object[] getStaffingLevelDto(List<StaffingLevel> staffingLevels) {
        List<ShiftPlanningStaffingLevelDTO> staffingLevelDtos = new ArrayList<>(staffingLevels.size());
        Set<BigInteger> activityIds = new HashSet<>();
        Object[] objects = null;
        for (StaffingLevel sl : staffingLevels) {
            ShiftPlanningStaffingLevelDTO staffingLevel = new ShiftPlanningStaffingLevelDTO(sl.getPhaseId(), asLocalDate(sl.getCurrentDate()), sl.getWeekCount(), sl.getStaffingLevelSetting());
            objects = getStaffingLevelInterval(sl.getPresenceStaffingLevelInterval());
            activityIds.addAll((Set<BigInteger>) objects[1]);
            staffingLevel.setStaffingLevelInterval((List<StaffingLevelTimeSlotDTO>) objects[0]);
            staffingLevelDtos.add(staffingLevel);
        }
        return new Object[]{staffingLevelDtos, activityIds};
    }

    private Object[] getStaffingLevelInterval(List<StaffingLevelInterval> staffingLevelIntervals) {
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlotDTOS = new ArrayList<>(staffingLevelIntervals.size());
        Set<BigInteger> activityIds = new HashSet<>();
        staffingLevelIntervals.forEach(sli -> {
            StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO = new StaffingLevelTimeSlotDTO(sli.getSequence(), sli.getMinNoOfStaff(), sli.getMaxNoOfStaff(), sli.getStaffingLevelDuration());
            staffingLevelTimeSlotDTO.setStaffingLevelActivities(sli.getStaffingLevelActivities());
            activityIds.addAll(sli.getStaffingLevelActivities().stream().filter(a -> a.getMinNoOfStaff() != 0).map(a -> new BigInteger(a.getActivityId().toString())).collect(Collectors.toSet()));
            staffingLevelTimeSlotDTO.setStaffingLevelSkills(sli.getStaffingLevelSkills());
            staffingLevelTimeSlotDTOS.add(staffingLevelTimeSlotDTO);
        });
        return new Object[]{staffingLevelTimeSlotDTOS, activityIds};
    }


    /**
     * @param unitId
     * @param absenceStaffingLevelDtos
     */
    public List<AbsenceStaffingLevelDto> updateAbsenceStaffingLevel(Long unitId
            , List<AbsenceStaffingLevelDto> absenceStaffingLevelDtos) {
        LOGGER.info("updating staffing level organizationId  {}", unitId);
        List<StaffingLevel> staffingLevels = new ArrayList<StaffingLevel>();
        List<StaffingLevelPlanningDTO> staffingLevelPlanningDTOS = new ArrayList<>();
        for (AbsenceStaffingLevelDto absenceStaffingLevelDto : absenceStaffingLevelDtos) {
            StaffingLevel staffingLevel = null;
            if (absenceStaffingLevelDto.getId() != null) {
                staffingLevel = staffingLevelMongoRepository.findById(absenceStaffingLevelDto.getId()).orElse(null);
            }

            if (isNotNull(staffingLevel)) {
                if (!staffingLevel.getCurrentDate().equals(absenceStaffingLevelDto.getCurrentDate())) {
                    LOGGER.info("current date modified from {}  to this {}", staffingLevel.getCurrentDate(), absenceStaffingLevelDto.getCurrentDate());
                    exceptionService.unsupportedOperationException(MESSAGE_STAFFLEVEL_CURRENTDATE_UPDATE);
                }
                StaffingLevelUtil.setUserWiseLogsInAbsence(staffingLevel, absenceStaffingLevelDto);
            } else {
                staffingLevel = staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(unitId, absenceStaffingLevelDto.getCurrentDate());
                if (Optional.ofNullable(staffingLevel).isPresent()) {
                    StaffingLevelUtil.setUserWiseLogsInAbsence(staffingLevel, absenceStaffingLevelDto);
                } else {
                    staffingLevel = StaffingLevelUtil.buildAbsenceStaffingLevels(absenceStaffingLevelDto, unitId);
                }
            }
            staffingLevels.add(staffingLevel);
            StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
            staffingLevelPlanningDTOS.add(staffingLevelPlanningDTO);
            absenceStaffingLevelDto.setStaffingLevelIntervalLogs(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelIntervalLogs());
        }
        staffingLevelMongoRepository.saveEntities(staffingLevels);
        absenceStaffingLevelDtos = StaffingLevelUtil.buildAbsenceStaffingLevelDto(staffingLevels);
        plannerSyncService.publishStaffingLevels(unitId, staffingLevelPlanningDTOS, IntegrationOperation.UPDATE);
        return absenceStaffingLevelDtos;
    }


    public StaffingLevelDto getStaffingLevel(Long unitId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("getting staffing level organizationId ,startDate ,endDate {},{},{}", unitId, startDate, endDate);
        Map<String, PresenceStaffingLevelDto> presenceStaffingLevelMap = new HashMap<String, PresenceStaffingLevelDto>();
        Map<String, AbsenceStaffingLevelDto> absenceStaffingLevelMap = new HashMap<String, AbsenceStaffingLevelDto>();
        FieldPermissionUserData fieldPermissionUserData=userIntegrationService.getPermissionData(newHashSet("Activity"));
        Map<LocalDate,StaffingLevel> staffingLevelMap = staffingLevelMongoRepository.findByUnitIdAndDates(unitId,asDate(startDate),asDate(endDate)).stream().collect(Collectors.toMap(k->asLocalDate(k.getCurrentDate()),v->v));
        Map<String,Set<FieldLevelPermission>> fieldPermissionMap=new HashMap<>();
        activityService.prepareFLPMap(fieldPermissionUserData.getModelDTOS(),fieldPermissionMap);
        while (!startDate.isAfter(endDate)) {
            getStaffingLevelPerDate(unitId, startDate, presenceStaffingLevelMap, absenceStaffingLevelMap,staffingLevelMap.get(startDate),fieldPermissionMap);
            startDate = startDate.plusDays(1);
        }
        return new StaffingLevelDto(presenceStaffingLevelMap, absenceStaffingLevelMap);
    }

    private LocalDate getStaffingLevelPerDate(Long unitId, LocalDate startDate, Map<String, PresenceStaffingLevelDto> presenceStaffingLevelMap, Map<String, AbsenceStaffingLevelDto> absenceStaffingLevelMap,StaffingLevel staffingLevel,Map<String,Set<FieldLevelPermission>> fieldPermissionMap) {
        staffingLevel = isNotNull(staffingLevel) ? staffingLevel : staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(unitId, asDate(startDate));
        if (!staffingLevel.getPresenceStaffingLevelInterval().isEmpty()) {
            PresenceStaffingLevelDto presenceStaffingLevelDto = ObjectMapperUtils.copyPropertiesByMapper(staffingLevel,PresenceStaffingLevelDto.class);
            presenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
            if (fieldPermissionMap.get("name").contains(FieldLevelPermission.HIDE) || fieldPermissionMap.get("name").isEmpty()) {
                staffingLevel.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities().forEach(k -> k.setName("XXXXX"));
            }
            presenceStaffingLevelDto.setStaffingLevelActivities(staffingLevel.getPresenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
            presenceStaffingLevelMap.put(DateUtils.getDateStringWithFormat(presenceStaffingLevelDto.getCurrentDate(), YYYY_MM_DD), presenceStaffingLevelDto);
        }
        AbsenceStaffingLevelDto absenceStaffingLevelDto = getAbsenceStaffingLevelDto(staffingLevel);
        absenceStaffingLevelMap.put(DateUtils.getDateStringWithFormat(absenceStaffingLevelDto.getCurrentDate(), YYYY_MM_DD), absenceStaffingLevelDto);
        return startDate;
    }

    private AbsenceStaffingLevelDto getAbsenceStaffingLevelDto(StaffingLevel staffingLevel) {
        AbsenceStaffingLevelDto absenceStaffingLevelDto = new AbsenceStaffingLevelDto(staffingLevel.getId(), staffingLevel.getPhaseId(),
                staffingLevel.getCurrentDate(), staffingLevel.getWeekCount());
        absenceStaffingLevelDto.setStaffingLevelSetting(new StaffingLevelSetting());
        absenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
        if (!staffingLevel.getAbsenceStaffingLevelInterval().isEmpty()) {
            updateIntervalData(staffingLevel, absenceStaffingLevelDto);
        }
        return absenceStaffingLevelDto;
    }

    private Map<BigInteger, Integer> getActivityIdRankingMap(Long unitId, List<BigInteger> activityIds) {
        List<ActivityDTO> activities = activityMongoRepository.findActivitiesByUnitId(unitId, activityIds);
        return activities.stream().collect(Collectors.toMap(ActivityDTO::getId, ActivityDTO::getActivitySequence));
    }

    private void updateInnerAbsenceStaffingAvailableNoOfStaff(StaffingLevelInterval absenceStaffingLevelInterval, BigInteger activityId) {
        for (StaffingLevelActivity staffingLevelActivity : absenceStaffingLevelInterval.getStaffingLevelActivities()) {
            if (activityId.equals(staffingLevelActivity.getActivityId())) {
                staffingLevelActivity.setAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff() + 1);
            }
        }
    }

    public Map<String, Object> createStaffingLevelFromStaffingLevelTemplate(Long unitId, StaffingLevelFromTemplateDTO staffingLevelFromTemplateDTO, BigInteger templateId) {
        Map<String, Object> response = new HashMap<>();

        StaffingLevelTemplate staffingLevelTemplate = staffingLevelTemplateRepository.findByIdAndUnitIdAndDeletedFalse(templateId, unitId);
        if (!Optional.ofNullable(staffingLevelTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(STAFFINGLEVELTEMPLATE_NOT_FOUND, templateId);
        }
        Set<BigInteger> activityIds = staffingLevelFromTemplateDTO.getActivitiesByDate().stream().flatMap(s -> s.getActivityIds().stream()).collect(Collectors.toSet());
        List<BigInteger> parentActivityIds = new ArrayList<>();
        List<ActivityValidationError> activityValidationErrors = staffingLevelTemplateService.validateActivityRules(activityIds, ObjectMapperUtils.copyPropertiesByMapper(staffingLevelTemplate, StaffingLevelTemplateDTO.class), parentActivityIds);
        Map<BigInteger, ActivityValidationError> activityValidationErrorMap = activityValidationErrors.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<DateWiseActivityDTO> dateWiseActivityDTOS = staffingLevelFromTemplateDTO.getActivitiesByDate();
        filterIncorrectDataByDates(dateWiseActivityDTOS, staffingLevelTemplate.getValidity().getStartDate(), staffingLevelTemplate.getValidity().getEndDate(), activityValidationErrors);
        Set<LocalDate> dates = dateWiseActivityDTOS.stream().map(DateWiseActivityDTO::getDate).collect(Collectors.toSet());
        List<StaffingLevel> allStaffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, dates);
        Map<LocalDate, StaffingLevel> dateStaffingLevelMap = allStaffingLevels.stream().collect(Collectors.toMap(k -> asLocalDate(k.getCurrentDate()), v -> v));
        List<StaffingLevel> staffingLevels = getStaffingLevelsByTemplate(unitId, staffingLevelTemplate, parentActivityIds, activityValidationErrorMap, dateWiseActivityDTOS, dateStaffingLevelMap);
        response.put("success", staffingLevels);
        response.put("errors", activityValidationErrors);
        return response;
    }

    private List<StaffingLevel> getStaffingLevelsByTemplate(Long unitId, StaffingLevelTemplate staffingLevelTemplate, List<BigInteger> parentActivityIds, Map<BigInteger, ActivityValidationError> activityValidationErrorMap, List<DateWiseActivityDTO> dateWiseActivityDTOS, Map<LocalDate, StaffingLevel> dateStaffingLevelMap) {
        List<StaffingLevel> staffingLevels = new ArrayList<>();
        dateWiseActivityDTOS.forEach(currentDateWiseActivities -> {
            Map<BigInteger, BigInteger> activityMap = currentDateWiseActivities.getActivityIds().stream().collect(Collectors.toMap(k -> k, v -> v));
            List<StaffingLevelInterval> staffingLevelIntervals = new ArrayList<>();
            for (StaffingLevelInterval staffingLevelInterval : staffingLevelTemplate.getPresenceStaffingLevelInterval()) {
                Set<StaffingLevelActivity> selectedActivitiesForCurrentDate = new HashSet<>();
                StaffingLevelInterval currentInterval = ObjectMapperUtils.copyPropertiesByMapper(staffingLevelInterval, StaffingLevelInterval.class);
                int min = 0;
                int max = 0;
                for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                    if (activityMap.containsKey(staffingLevelActivity.getActivityId()) && !activityValidationErrorMap.containsKey(staffingLevelActivity.getActivityId())) {
                        selectedActivitiesForCurrentDate.add(staffingLevelActivity);
                        if (parentActivityIds.contains(staffingLevelActivity.getActivityId())) {
                            min += staffingLevelActivity.getMinNoOfStaff();
                            max += staffingLevelActivity.getMaxNoOfStaff();
                        }

                    }
                }
                currentInterval.setStaffingLevelActivities(selectedActivitiesForCurrentDate);
                currentInterval.setMinNoOfStaff(min);
                currentInterval.setMaxNoOfStaff(max);
                staffingLevelIntervals.add(currentInterval);
            }
            StaffingLevel staffingLevel = getStaffingLevelIfExist(dateStaffingLevelMap, currentDateWiseActivities, staffingLevelIntervals, staffingLevelTemplate, unitId);
            staffingLevel.setStaffingLevelSetting(staffingLevelTemplate.getStaffingLevelSetting());
            staffingLevels.add(staffingLevel);
        });
        if (!staffingLevels.isEmpty()) {
            staffingLevelMongoRepository.saveEntities(staffingLevels);
        }
        return staffingLevels;
    }


    private void filterIncorrectDataByDates(List<DateWiseActivityDTO> dateWiseActivityDTOS, LocalDate startDate, LocalDate endDate, List<ActivityValidationError> activityValidationErrors) {
        Iterator<DateWiseActivityDTO> iterator = dateWiseActivityDTOS.iterator();
        DateTimeInterval timeInterval=new DateTimeInterval(startDate,endDate);
        while (iterator.hasNext()) {
            DateWiseActivityDTO dateWiseActivityDTO = iterator.next();
            if (!timeInterval.contains(dateWiseActivityDTO.getDate()) || dateWiseActivityDTO.getDate().isBefore(DateUtils.getCurrentLocalDate())) {
                iterator.remove();
                activityValidationErrors.add(new ActivityValidationError(Arrays.asList(exceptionService.getLanguageSpecificText(DATE_OUT_OF_RANGE, dateWiseActivityDTO.getDate()))));
            }
        }
    }


    private StaffingLevel getStaffingLevelIfExist(Map<LocalDate, StaffingLevel> localDateStaffingLevelMap, DateWiseActivityDTO currentDate, List<StaffingLevelInterval> staffingLevelIntervals, StaffingLevelTemplate staffingLevelTemplate, Long unitId) {
        StaffingLevel staffingLevel = localDateStaffingLevelMap.get(currentDate.getDate());
        if (staffingLevel != null) {
            staffingLevel.setPresenceStaffingLevelInterval(staffingLevelIntervals);
        } else {
            staffingLevel = ObjectMapperUtils.copyPropertiesByMapper(staffingLevelTemplate, StaffingLevel.class);
            staffingLevel.setId(null);
            staffingLevel.setPresenceStaffingLevelInterval(staffingLevelIntervals);
            staffingLevel.setWeekCount(getWeekNumberByLocalDate(currentDate.getDate()));
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, DateUtils.asDate(currentDate.getDate()), null);
            staffingLevel.setPhaseId(phase.getId());
            staffingLevel.setUnitId(unitId);
            staffingLevel.setCurrentDate(DateUtils.asDate(currentDate.getDate()));
        }
        return staffingLevel;
    }

    public int getLowerIndex(Date startDate) {

        int lowerLimit = DateUtils.getHourFromDate(startDate) * 4;
        int minutes = DateUtils.getMinutesFromDate(startDate);
        int minuteOffset = 0;
        if (minutes >= 45) {
            minuteOffset = 3;
        } else if (minutes >= 30) {
            minuteOffset = 2;
        } else if (minutes >= 15) {
            minuteOffset = 1;
        }
        return lowerLimit + minuteOffset;
    }

    public int getUpperIndex(Date endDate) {
        int upperLimit = DateUtils.getHourFromDate(endDate) * 4;
        int minutes = DateUtils.getMinutesFromDate(endDate);
        int minuteOffset = 0;
        if (minutes > 45) {
            minuteOffset = 4;
        } else if (minutes > 30) {
            minuteOffset = 3;
        } else if (minutes > 15) {
            minuteOffset = 2;
        } else if (minutes > 0) {
            minuteOffset = 1;
        }
        return upperLimit + minuteOffset - 1;
    }

    public StaffingLevelDto getStaffingLevelIfUpdated(Long unitId, List<UpdatedStaffingLevelDTO> updatedStaffingLevels) {
        StaffingLevelDto staffingLevelDto = null;
        if (isCollectionNotEmpty(updatedStaffingLevels)) {
            Map<String, PresenceStaffingLevelDto> presenceStaffingLevelMap = new HashMap<>();
            Map<String, AbsenceStaffingLevelDto> absenceStaffingLevelMap = new HashMap<>();
            List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, updatedStaffingLevels.stream().map(updatedStaffingLevelDTO -> updatedStaffingLevelDTO.getCurrentDate()).collect(Collectors.toSet()));
            Map<LocalDate, Date> staffingLevelDateMap = staffingLevels.stream().collect(Collectors.toMap(k -> asLocalDate(k.getCurrentDate()), StaffingLevel::getUpdatedAt));
            for (UpdatedStaffingLevelDTO updatedStaffingLevel : updatedStaffingLevels) {
                if (isNotNull(updatedStaffingLevel.getUpdatedAt()) && staffingLevelDateMap.containsKey(updatedStaffingLevel.getCurrentDate()) && staffingLevelDateMap.get(updatedStaffingLevel.getCurrentDate()).after(updatedStaffingLevel.getUpdatedAt())) {
                    getStaffingLevelPerDate(unitId, updatedStaffingLevel.getCurrentDate(), presenceStaffingLevelMap, absenceStaffingLevelMap, null,new HashMap<>());
                }
            }
            staffingLevelDto = new StaffingLevelDto(presenceStaffingLevelMap, absenceStaffingLevelMap);
        }
        return staffingLevelDto;
    }

    @Async
    public void removedActivityFromStaffingLevel(BigInteger activityId, boolean isPresence) {
        List<StaffingLevel> staffingLevels = isPresence ? staffingLevelMongoRepository.findPresenceStaffingLevelsByActivityId(activityId, DateUtils.getDate()) : staffingLevelMongoRepository.findAbsenceStaffingLevelsByActivityId(activityId, DateUtils.getDate());
        for (StaffingLevel staffingLevel : staffingLevels) {
            for (StaffingLevelInterval staffingLevelInterval : isPresence ? staffingLevel.getPresenceStaffingLevelInterval() : staffingLevel.getAbsenceStaffingLevelInterval()) {
                removedActivityFromStaffingLevelInterval(staffingLevelInterval, activityId);
            }
        }
        staffingLevelMongoRepository.saveAll(staffingLevels);
    }

    private void removedActivityFromStaffingLevelInterval(StaffingLevelInterval staffingLevelInterval, BigInteger activityId) {
        TreeSet<StaffingLevelIntervalLog> staffingLevelIntervalLogs = staffingLevelInterval.getStaffingLevelIntervalLogs();
        staffingLevelIntervalLogs.forEach(staffingLevelIntervalLog -> {
            staffingLevelIntervalLog.getStaffingLevelActivities().removeIf(k -> k.getActivityId().equals(activityId));
            staffingLevelIntervalLog.getActivityRemoveLogs().removeIf(k -> k.getActivityId().equals(activityId));
            staffingLevelIntervalLog.getNewlyAddedActivityIds().remove(activityId);
            staffingLevelIntervalLog.setMinNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().mapToInt(StaffingLevelActivity::getMinNoOfStaff).sum());
            staffingLevelIntervalLog.setMaxNoOfStaff(staffingLevelIntervalLog.getStaffingLevelActivities().stream().mapToInt(k -> k.getMaxNoOfStaff()).sum());
        });
        Set<StaffingLevelActivity> staffingLevelActivities = new HashSet<>();
        int minNoOfStaff = 0;
        int maxNoOfStaff = 0;
        for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
            if (staffingLevelActivity.getActivityId().equals(activityId)) {
                minNoOfStaff += staffingLevelActivity.getMinNoOfStaff();
                maxNoOfStaff += staffingLevelActivity.getMaxNoOfStaff();
            } else {
                staffingLevelActivities.add(staffingLevelActivity);
            }
        }
        staffingLevelInterval.setMinNoOfStaff(staffingLevelInterval.getMinNoOfStaff() - minNoOfStaff);
        staffingLevelInterval.setMaxNoOfStaff(staffingLevelInterval.getMaxNoOfStaff() - maxNoOfStaff);
        staffingLevelInterval.setStaffingLevelActivities(staffingLevelActivities);
    }

    public List<StaffingLevel> findByUnitIdAndDates(Long unitId, Date startDate, Date endDate) {
        return staffingLevelMongoRepository.findByUnitIdAndDates(unitId, startDate, endDate);
    }

    public StaffingLevelDto publishStaffingLevel(Long unitId, StaffingLevelPublishDTO staffingLevelPublishDTO) {
        Map<String, PresenceStaffingLevelDto> presenceStaffingLevelMap = new HashMap<String, PresenceStaffingLevelDto>();
        Map<String, AbsenceStaffingLevelDto> absenceStaffingLevelMap = new HashMap<String, AbsenceStaffingLevelDto>();
        List<StaffingLevel> staffingLevels = isCollectionNotEmpty(staffingLevelPublishDTO.getWeekDates()) ? staffingLevelMongoRepository.findByUnitIdAndDates(unitId, staffingLevelPublishDTO.getWeekDates()) : staffingLevelMongoRepository.findByUnitIdAndDates(unitId, staffingLevelPublishDTO.getStartDate(), staffingLevelPublishDTO.getEndDate());
        for (StaffingLevel staffingLevel : staffingLevels) {
            StaffingLevelUtil.updateStaffingLevelToPublish(staffingLevelPublishDTO, staffingLevel);
            staffingLevelAvailableCountService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel);
        }
        staffingLevelMongoRepository.saveEntities(staffingLevels);
        DateTimeInterval presenceInterval = new DateTimeInterval(staffingLevelPublishDTO.getSelectedDateForPresence(), staffingLevelPublishDTO.getSelectedEndDateForPresence());
        DateTimeInterval absenceInterval = new DateTimeInterval(staffingLevelPublishDTO.getSelectedDateForAbsence(), staffingLevelPublishDTO.getSelectedEndDateForAbsence());
        for (StaffingLevel staffingLevel : staffingLevels) {
            LocalDate currentDate = asLocalDate(staffingLevel.getCurrentDate());
            if (presenceInterval.containsOrEqualsEnd(currentDate)) {
                presenceStaffingLevelMap.put(currentDate.toString(), ObjectMapperUtils.copyPropertiesByMapper(staffingLevel, PresenceStaffingLevelDto.class));
            }
            if (absenceInterval.containsOrEqualsEnd(currentDate)) {
                AbsenceStaffingLevelDto absenceStaffingLevelDto = new AbsenceStaffingLevelDto(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount());
                if (!staffingLevel.getAbsenceStaffingLevelInterval().isEmpty()) {
                    updateIntervalData(staffingLevel, absenceStaffingLevelDto);
                }
                absenceStaffingLevelDto.setStaffingLevelSetting(new StaffingLevelSetting());
                absenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
                absenceStaffingLevelMap.put(currentDate.toString(), absenceStaffingLevelDto);
            }
        }
        return new StaffingLevelDto(presenceStaffingLevelMap, absenceStaffingLevelMap);
    }

    public void updateIntervalData(StaffingLevel staffingLevel, AbsenceStaffingLevelDto absenceStaffingLevelDto) {
        absenceStaffingLevelDto.setMinNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMinNoOfStaff());
        absenceStaffingLevelDto.setMaxNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMaxNoOfStaff());
        absenceStaffingLevelDto.setAbsentNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getAvailableNoOfStaff());
        absenceStaffingLevelDto.setStaffingLevelActivities(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
        absenceStaffingLevelDto.setStaffingLevelIntervalLogs(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelIntervalLogs());
    }

    public boolean validateStaffingLevel(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, Phase phase, Shift oldStateShift) {
        ShiftType oldStateShiftType = oldStateShift.getShiftType();
        ShiftType shiftType = shift.getShiftType();
        boolean activityReplaced = activityReplaced(oldStateShift, shift);
        PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        if (!Optional.ofNullable(phaseSettings).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_PHASESETTINGS_ABSENT);
        }
        if (activityReplaced) {
            for (int i = 0; i < oldStateShift.getActivities().size(); i++) {
                try {
                    if (activityWrapperMap.get(oldStateShift.getActivities().get(i).getActivityId()).getTimeTypeInfo().getPriorityFor().equals(activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getTimeTypeInfo().getPriorityFor())) {
                        shift.setShiftType(oldStateShiftType);
                        boolean isOldShiftVerifyStaffingLevel = shiftValidatorService.isVerificationRequired(false, phaseSettings);
                        shift.setShiftType(shiftType);
                        boolean isNewShiftVerifyStaffingLevel = shiftValidatorService.isVerificationRequired(true, phaseSettings);
                        if (isNull(activityWrapperMap.get(oldStateShift.getActivities().get(i).getActivityId()).getActivityPriority()) || isNull(activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getActivityPriority())) {
                            exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_PRIORITY_SEQUENCE);
                        }
                        int rankOfOld = activityWrapperMap.get(oldStateShift.getActivities().get(i).getActivityId()).getActivityPriority().getSequence();
                        int rankOfNew = activityWrapperMap.get(shift.getActivities().get(i).getActivityId()).getActivityPriority().getSequence();
                        if (isNewShiftVerifyStaffingLevel || isOldShiftVerifyStaffingLevel) {
                            validateRankOfActivity(rankOfOld, rankOfNew);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    //Intentionally left blank
                }
            }
        }
        return activityReplaced;
    }

    private void validateRankOfActivity(final int rankOfOld, final int rankOfNew) {
        if (rankOfNew > rankOfOld) {
            exceptionService.actionNotPermittedException(SHIFT_CAN_NOT_MOVE, LOW_ACTIVITY_RANK);
        }

    }

    private boolean activityReplaced(Shift dbShift, Shift shift) {
        boolean activityReplaced = false;
        if (shift.getActivities().size() == dbShift.getActivities().size()) {
            for (int i = 0; i < shift.getActivities().size(); i++) {
                if (!shift.getActivities().get(i).getActivityId().equals(dbShift.getActivities().get(i).getActivityId())) {
                    activityReplaced = true;
                    break;
                }
            }
        }
        return activityReplaced;
    }

    public Map<LocalDate, DailyStaffingLevelDetailsDTO> getWeeklyStaffingLevel(Long unitId, LocalDate date, BigInteger activityId, boolean unpublishedChanges) {
        LocalDate startLocalDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate endLocalDate = startLocalDate.plusWeeks(3).plusDays(1);
        List<TimeSlotDTO> timeSlots = timeSlotSetService.getShiftPlanningTimeSlotByUnit(unitId);
        List<PresenceStaffingLevelDto> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDatesAndActivityId(unitId, asDate(startLocalDate), asDate(endLocalDate), activityId);
        Object[] staffingLevelMapAndActivityIds = getStaffingLevelMapAndActivityIds(staffingLevels);
        Set<BigInteger> activityIds = (Set<BigInteger>) staffingLevelMapAndActivityIds[0];
        List<Activity> activities = activityMongoRepository.findAllBreakActivitiesByOrganizationId(unitId);
        Set<BigInteger> breakActivityIds = activities.stream().map(activity -> activity.getId()).collect(Collectors.toSet());
        activityIds.addAll(breakActivityIds);
        Map<LocalDate, PresenceStaffingLevelDto> staffingLevelMap = (Map<LocalDate, PresenceStaffingLevelDto>) staffingLevelMapAndActivityIds[1];
        Map<LocalDate, DailyStaffingLevelDetailsDTO> localDateDailyStaffingLevelDetailsDTOMap = new HashMap<>();
        while (startLocalDate.isBefore(endLocalDate)) {
            PresenceStaffingLevelDto staffingLevel = staffingLevelMap.get(startLocalDate);
            List<StaffingLevelDetailsByTimeSlotDTO> staffingLevelDetailsByTimeSlotDTOS = new ArrayList<>();
            Integer detailLevelMinutes = staffingLevel.getStaffingLevelSetting().getDetailLevelMinutes();
            for (TimeSlotDTO timeSlot : timeSlots) {
                List<DateTimeInterval> timeSlotIntervals = getTimeSlotInterval(startLocalDate, timeSlot);
                AtomicReference<LocalDate> localDateAtomicReference = new AtomicReference<>(startLocalDate);
                List<StaffingLevelInterval> staffingLevelIntervals = getStaffingLevelInterval(activityId, unpublishedChanges, staffingLevel, timeSlotIntervals, localDateAtomicReference);
                if (timeSlot.getStartHour() > timeSlot.getEndHour()) {
                    LocalDate nextDay = startLocalDate.plusDays(1);
                    PresenceStaffingLevelDto nextDayStaffingLevel = staffingLevelMap.get(nextDay);
                    localDateAtomicReference = new AtomicReference<>(nextDay);
                    staffingLevelIntervals.addAll(getStaffingLevelInterval(activityId, unpublishedChanges, nextDayStaffingLevel, timeSlotIntervals, localDateAtomicReference));
                }
                int[] maxAndMinNoOfStaff = getMinAndMaxCount(staffingLevelIntervals, true, unpublishedChanges, activityId);
                int overStaffing = maxAndMinNoOfStaff[0];
                int underStaffing = maxAndMinNoOfStaff[1];
                int totalMinNoOfStaff = maxAndMinNoOfStaff[2];
                int totalMaxNoOfStaff = maxAndMinNoOfStaff[3];
                int totalMinimumMinutes = totalMinNoOfStaff * detailLevelMinutes;
                int totalMaximumMinutes = totalMaxNoOfStaff * detailLevelMinutes;
                staffingLevelDetailsByTimeSlotDTOS.add(new StaffingLevelDetailsByTimeSlotDTO(underStaffing, overStaffing, underStaffing * detailLevelMinutes, overStaffing * detailLevelMinutes, timeSlot.getName(), totalMinNoOfStaff, totalMaxNoOfStaff, totalMinimumMinutes, totalMaximumMinutes));
            }
            int[] maxAndMinNoOfStaff = getMinAndMaxCount(staffingLevel.getPresenceStaffingLevelInterval(), false, unpublishedChanges, null);
            int overStaffing = maxAndMinNoOfStaff[0];
            int underStaffing = maxAndMinNoOfStaff[1];
            int totalMinNoOfStaff = maxAndMinNoOfStaff[2];
            int totalMaxNoOfStaff = maxAndMinNoOfStaff[3];
            int totalMinimumMinutes = totalMinNoOfStaff * detailLevelMinutes;
            int totalMaximumMinutes = totalMaxNoOfStaff * detailLevelMinutes;
            DailyStaffingLevelDetailsDTO dailyStaffingLevelDetailsDTO = new DailyStaffingLevelDetailsDTO(underStaffing, overStaffing, underStaffing * detailLevelMinutes, overStaffing * detailLevelMinutes, staffingLevelDetailsByTimeSlotDTOS, totalMinNoOfStaff, totalMaxNoOfStaff, totalMinimumMinutes, totalMaximumMinutes);
            localDateDailyStaffingLevelDetailsDTOMap.put(startLocalDate, dailyStaffingLevelDetailsDTO);
            startLocalDate = startLocalDate.plusDays(1);
        }
        return localDateDailyStaffingLevelDetailsDTOMap;
    }

    private List<DateTimeInterval> getTimeSlotInterval(LocalDate startLocalDate, TimeSlotDTO timeSlot) {
        ZonedDateTime startZonedDateTime = timeSlot.getStartZoneDateTime(startLocalDate);
        ZonedDateTime endZonedDateTime = timeSlot.getEndZoneDateTime(startLocalDate);
        List<DateTimeInterval> timeIntervals = new ArrayList<>();
        timeIntervals.add(new DateTimeInterval(startZonedDateTime, endZonedDateTime));
        return timeIntervals;
    }

    private List<StaffingLevelInterval> getStaffingLevelInterval(BigInteger activityId, boolean unpublishedChanges, PresenceStaffingLevelDto staffingLevel, List<DateTimeInterval> timeSlotIntervals, AtomicReference<LocalDate> localDateAtomicReference) {
        List<StaffingLevelInterval> updatedIntervals = new ArrayList<>(96);
        for (DateTimeInterval timeSlotInterval : timeSlotIntervals) {
            for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
                if ((!unpublishedChanges || isCollectionEmpty(staffingLevelInterval.getStaffingLevelIntervalLogs())) && staffingLevelInterval.getStaffingLevelDuration().getInterval(localDateAtomicReference.get()).overlaps(timeSlotInterval) && staffingLevelInterval.getActivityIds().contains(activityId)) {
                    updatedIntervals.add(staffingLevelInterval);
                } else {
                    if (unpublishedChanges && isCollectionNotEmpty(staffingLevelInterval.getStaffingLevelIntervalLogs()) && staffingLevelInterval.getStaffingLevelDuration().getInterval(localDateAtomicReference.get()).overlaps(timeSlotInterval) && staffingLevelInterval.getStaffingLevelIntervalLogs().last().getActivityIds().contains(activityId)) {
                        updatedIntervals.add(staffingLevelInterval);
                    }
                }
            }
        }
        return updatedIntervals;
    }

    private int[] getMinAndMaxCount(List<StaffingLevelInterval> staffingLevelIntervals, boolean calculateActivityWise, boolean unpublishedChanges, BigInteger activityId) {
        int overStaffing = 0;
        int underStaffing = 0;
        int totalMinNoOfStaff = 0;
        int totalMaxNoOfStaff = 0;
        for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
            if (calculateActivityWise) {
                StaffingLevelActivity staffingLevelActivity = !unpublishedChanges || isCollectionEmpty(staffingLevelInterval.getStaffingLevelIntervalLogs()) ? staffingLevelInterval.getStaffingLevelActivity(activityId) : staffingLevelInterval.getStaffingLevelIntervalLogs().last().getStaffingLevelActivities().stream().filter(staffingLevelActivity1 -> staffingLevelActivity1.getActivityId().equals(activityId)).findFirst().orElse(null);
                if (isNotNull(staffingLevelActivity)) {
                    overStaffing += staffingLevelActivity.getMaxNoOfStaff() < staffingLevelActivity.getAvailableNoOfStaff() ? staffingLevelActivity.getAvailableNoOfStaff() - staffingLevelActivity.getMaxNoOfStaff() : 0;
                    underStaffing += staffingLevelActivity.getMinNoOfStaff() > staffingLevelActivity.getAvailableNoOfStaff() ? staffingLevelActivity.getMinNoOfStaff() - staffingLevelActivity.getAvailableNoOfStaff() : 0;
                    totalMinNoOfStaff += staffingLevelInterval.getMinNoOfStaff();
                    totalMaxNoOfStaff += staffingLevelInterval.getMaxNoOfStaff();
                }
            } else {
                overStaffing += staffingLevelInterval.getMaxNoOfStaff() < staffingLevelInterval.getAvailableNoOfStaff() ? staffingLevelInterval.getAvailableNoOfStaff() - staffingLevelInterval.getMaxNoOfStaff() : 0;
                underStaffing += staffingLevelInterval.getMinNoOfStaff() > staffingLevelInterval.getAvailableNoOfStaff() ? staffingLevelInterval.getMinNoOfStaff() - staffingLevelInterval.getAvailableNoOfStaff() : 0;
                totalMinNoOfStaff += staffingLevelInterval.getMinNoOfStaff();
                totalMaxNoOfStaff += staffingLevelInterval.getMaxNoOfStaff();
            }
        }
        return new int[]{overStaffing, underStaffing, totalMinNoOfStaff, totalMaxNoOfStaff};
    }

    private Object[] getStaffingLevelMapAndActivityIds(List<PresenceStaffingLevelDto> staffingLevels) {
        Map<LocalDate, PresenceStaffingLevelDto> staffingLevelDtoMap = new HashMap<>();
        Set<BigInteger> activityIds = new HashSet<>();
        for (PresenceStaffingLevelDto staffingLevel : staffingLevels) {
            staffingLevelDtoMap.put(asLocalDate(staffingLevel.getCurrentDate()), staffingLevel);
            activityIds.addAll(staffingLevel.getPresenceStaffingLevelInterval().get(0).getActivityIds());
        }
        return new Object[]{activityIds, staffingLevelDtoMap};
    }

    public UnityStaffingLevelRelatedDetails getStaffingLevelActivities(Long unitId, LocalDate startDate, String query) {
        UnityStaffingLevelRelatedDetails unityStaffingLevelRelatedDetails = new UnityStaffingLevelRelatedDetails();
        PlanningPeriod planningPeriod = planningPeriodService.getPlanningPeriodContainsDate(unitId, startDate);
        unityStaffingLevelRelatedDetails.setPlanningPeriodStartDate(planningPeriod.getStartDate());
        unityStaffingLevelRelatedDetails.setPlanningPeriodEndDate(planningPeriod.getEndDate());
        unityStaffingLevelRelatedDetails.setWeekStartDate(startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        unityStaffingLevelRelatedDetails.setWeekEndDate(startDate.plusWeeks(1).minusDays(1));
        unityStaffingLevelRelatedDetails.setStartDate(startDate);
        unityStaffingLevelRelatedDetails.setEndDate(startDate);
        LocalDate endDate;
        switch (query) {
            case PLANNING_PERIOD:
                startDate = planningPeriod.getStartDate();
                endDate = planningPeriod.getEndDate();
                break;
            case WEEK:
                startDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = startDate.plusWeeks(1);
                break;
            default:
                endDate = startDate;
                break;
        }
        unityStaffingLevelRelatedDetails.setActivities(staffingLevelMongoRepository.getStaffingLevelActivities(unitId, startDate, endDate));
        return unityStaffingLevelRelatedDetails;
    }

    //TODO just for setting the data in exisiting
    public boolean updateInitialInStaffingLevel() {
        List<StaffingLevel> staffingLevels=staffingLevelMongoRepository.findAllByDeletedFalse();
        for (StaffingLevel staffingLevel : staffingLevels) {
            staffingLevelAvailableCountService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel);
            StaffingLevelUtil.setStaffingLevelDetails(staffingLevel);
        }
        //staffingLevelMongoRepository.saveEntities(staffingLevels);
        return true;
    }

    @Async
    public void setIntialValueOfStaffingLevel(PlanningPeriod planningPeriod){
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(planningPeriod.getUnitId(),asDate(planningPeriod.getStartDate()),asDate(planningPeriod.getEndDate()));
        staffingLevels.parallelStream().forEach(staffingLevel -> {
            for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
                for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                    staffingLevelActivity.setInitialOverStaffing(Math.max(staffingLevelActivity.getAvailableNoOfStaff()-staffingLevelActivity.getMaxNoOfStaff(),0));
                    staffingLevelActivity.setInitialUnderStaffing(Math.max(staffingLevelActivity.getMinNoOfStaff() - staffingLevelActivity.getAvailableNoOfStaff(),0));
                }
            }
        });
        staffingLevelMongoRepository.saveEntities(staffingLevels);
    }
}
