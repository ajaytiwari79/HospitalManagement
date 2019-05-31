package com.kairos.service.staffing_level;


import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityValidationError;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.organization.OrganizationSkillAndOrganizationTypesDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepositoryImpl;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelTemplateRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.event.ShiftNotificationEvent;
import com.kairos.utils.service_util.StaffingLevelUtil;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.FULL_DAY;
import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.AppConstants.FULL_WEEK;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

@Service
@Transactional
public class StaffingLevelService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffingLevelService.class);

    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;

    @Inject
    private PhaseService phaseService;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    com.fasterxml.jackson.databind.ObjectMapper objectMapper;
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
    private StaffingLevelActivityRankService staffingLevelActivityRankService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ShiftService shiftService;


    /**
     * @param presenceStaffingLevelDTO
     * @param unitId
     */
    public PresenceStaffingLevelDto createStaffingLevel(PresenceStaffingLevelDto presenceStaffingLevelDTO, Long unitId) {
        LOGGER.debug("saving staffing level organizationId {}", unitId);
        StaffingLevel staffingLevel = null;
        staffingLevel = staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalseCustom(unitId, DateUtils.onlyDate(presenceStaffingLevelDTO.getCurrentDate()));
        StaffingLevelUtil.sortStaffingLevelActivities(presenceStaffingLevelDTO, presenceStaffingLevelDTO.getStaffingLevelSetting().getActivitiesRank());
        if (Optional.ofNullable(staffingLevel).isPresent()) {
            if (staffingLevel.getPresenceStaffingLevelInterval().isEmpty()) {
                List<StaffingLevelInterval> presenceStaffingLevelIntervals = new ArrayList<StaffingLevelInterval>();
                for (StaffingLevelTimeSlotDTO staffingLevelTimeSlotDTO : presenceStaffingLevelDTO.getPresenceStaffingLevelInterval()) {
                    StaffingLevelInterval presenceStaffingLevelInterval = new StaffingLevelInterval(staffingLevelTimeSlotDTO.getSequence(), staffingLevelTimeSlotDTO.getMinNoOfStaff(),
                            staffingLevelTimeSlotDTO.getMaxNoOfStaff(), staffingLevelTimeSlotDTO.getStaffingLevelDuration()
                    );
                    presenceStaffingLevelInterval.addStaffLevelActivity(staffingLevelTimeSlotDTO.getStaffingLevelActivities());
                    presenceStaffingLevelInterval.addStaffLevelSkill(staffingLevelTimeSlotDTO.getStaffingLevelSkills());
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
        boolean activitiesRankUpdate = staffingLevelActivityRankService.updateStaffingLevelActivityRank(DateUtils.asLocalDate(staffingLevel.getCurrentDate()), staffingLevel.getId(), staffingLevel.getStaffingLevelSetting().getActivitiesRank());
        BeanUtils.copyProperties(staffingLevel, presenceStaffingLevelDTO, new String[]{"presenceStaffingLevelInterval", "absenceStaffingLevelInterval"});
        presenceStaffingLevelDTO.setPresenceStaffingLevelInterval(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval().stream()
                .sorted(Comparator.comparing(StaffingLevelTimeSlotDTO::getSequence)).collect(Collectors.toList()));
        StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
        plannerSyncService.publishStaffingLevel(unitId, staffingLevelPlanningDTO, IntegrationOperation.CREATE);
        presenceStaffingLevelDTO.setUpdatedAt(staffingLevel.getUpdatedAt());
        return presenceStaffingLevelDTO;
    }

    /**
     * @param unitId
     * @return
     * @auther Anil Maurya
     */

    public Map<String, StaffingLevel> getPresenceStaffingLevel(Long unitId, Date startDate, Date endDate) {
        LOGGER.debug("getting staffing level organizationId ,startDate ,endDate {},{},{}", unitId, startDate, endDate);
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(unitId, startDate, endDate);
        Map<String, StaffingLevel> staffingLevelsMap = staffingLevels.parallelStream().collect(Collectors.toMap(staffingLevel -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

        return staffingLevelMongoRepository.findById(staffingLevelId).get();
    }

    /**
     * @param presenceStaffingLevelDTO
     * @param unitId
     */
    public PresenceStaffingLevelDto updatePresenceStaffingLevel(BigInteger staffingLevelId, Long unitId
            , PresenceStaffingLevelDto presenceStaffingLevelDTO) {
        StaffingLevelUtil.sortStaffingLevelActivities(presenceStaffingLevelDTO, presenceStaffingLevelDTO.getStaffingLevelSetting().getActivitiesRank());
        LOGGER.info("updating staffing level organizationId and staffingLevelId is {} ,{}", unitId, staffingLevelId);
        StaffingLevel staffingLevel = staffingLevelMongoRepository.findById(staffingLevelId).get();
        if (!staffingLevel.getCurrentDate().equals(presenceStaffingLevelDTO.getCurrentDate())) {
            LOGGER.info("current date modified from {}  to this {}", staffingLevel.getCurrentDate(), presenceStaffingLevelDTO.getCurrentDate());
            exceptionService.unsupportedOperationException(MESSAGE_STAFFLEVEL_CURRENTDATE_UPDATE);
        }
        List<ActivityDTO> activityDTOS = activityMongoRepository.findChildActivityActivityIds(presenceStaffingLevelDTO.getStaffingLevelSetting().getActivitiesRank().keySet());
        Map<BigInteger, BigInteger> childAndParentActivityIdMap = new HashMap<>();
        activityDTOS.forEach(activityDTO -> {
            if (isCollectionNotEmpty(activityDTO.getChildActivityIds())) {
                activityDTO.getChildActivityIds().forEach(childActivityId -> childAndParentActivityIdMap.put(childActivityId, activityDTO.getId()));
            }
        });
        staffingLevel = StaffingLevelUtil.updateStaffingLevels(staffingLevelId, presenceStaffingLevelDTO, unitId, staffingLevel, childAndParentActivityIdMap);
        this.save(staffingLevel);
        staffingLevelActivityRankService.updateStaffingLevelActivityRank(DateUtils.asLocalDate(staffingLevel.getCurrentDate()), staffingLevel.getId(), staffingLevel.getStaffingLevelSetting().getActivitiesRank());
        Collections.sort(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval(), Comparator.comparing(StaffingLevelTimeSlotDTO::getSequence));
        StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
        plannerSyncService.publishStaffingLevel(unitId, staffingLevelPlanningDTO, IntegrationOperation.UPDATE);
        presenceStaffingLevelDTO.setUpdatedAt(staffingLevel.getUpdatedAt());
        return presenceStaffingLevelDTO;
    }

    public StaffingLevel updateStaffingLevelAvailableStaffCount(LocalDate localDate,Long unitId) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        Date startDate = asDate(localDate);
        Date endDate = getEndOfDay(startDate);
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse( startDate, endDate,unitId);
        List<ShiftDTO>  shiftDTOS = shiftService.updateDraftShiftToShift(ObjectMapperUtils.copyPropertiesOfListByMapper(shifts, ShiftDTO.class),userAccessRoleDTO);
        StaffingLevel staffingLevel=staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(UserContext.getUnitId(),startDate);
        if (isNull(staffingLevel)) {
            staffingLevel = createDefaultStaffingLevel(unitId, startDate);
        }
        return updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfListByMapper(shiftDTOS,Shift.class));

    }


    private boolean isShiftPeriodModified(ShiftNotificationEvent shiftNotificationEvent) {

        return !(shiftNotificationEvent.getPreviousStateShift().getStartDate().equals(shiftNotificationEvent.getShift().getStartDate())
                && shiftNotificationEvent.getPreviousStateShift().getEndDate().equals(shiftNotificationEvent.getShift().getEndDate()));

    }

    /**
     * @param staffingLevel
     * @param shiftNotificationEvent
     */
    public StaffingLevel updateStaffingLevelAvailableStaffCountForNewlyCreatedShift(StaffingLevel staffingLevel, ShiftNotificationEvent shiftNotificationEvent) {

        for (ShiftActivity activity : shiftNotificationEvent.getShift().getActivities()) {

            int lowerLimit = getLowerIndex(activity.getStartDate());
            int upperLimit = getUpperIndex(activity.getEndDate());
            int currentAvailableStaffCount = 0;
            for (int currentIndex = lowerLimit; currentIndex <= upperLimit; currentIndex++) {
                currentAvailableStaffCount = staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getAvailableNoOfStaff();
                staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).setAvailableNoOfStaff(++currentAvailableStaffCount);
                staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelActivities().stream().
                        filter(staffingLevelActivity -> staffingLevelActivity.getActivityId().equals(activity.getActivityId())).findFirst().
                        ifPresent(staffingLevelActivity -> staffingLevelActivity.setAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff() + 1));
            }

        }
        return staffingLevel;

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

        PhaseDTO phase = phaseService.getUnitPhaseByDate(unitId, currentDate);
        LocalDate date = LocalDate.now();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeekCount = date.get(woy);
        StaffingLevel staffingLevel = new StaffingLevel(currentDate, currentWeekCount, unitId, phase.getId(), staffingLevelSetting);
        List<StaffingLevelInterval> StaffingLevelIntervals = new ArrayList<>();
        int startTimeCounter = 0;
        LocalTime startTime = LocalTime.MIN;
        for (int i = 0; i <= 95; i++) {
            StaffingLevelInterval staffingLevelInterval = new StaffingLevelInterval(i, 0, 0, new Duration(startTime.plusMinutes(startTimeCounter),
                    startTime.plusMinutes(startTimeCounter += 15)));
            staffingLevelInterval.setAvailableNoOfStaff(0);
            StaffingLevelIntervals.add(staffingLevelInterval);
        }
        List<StaffingLevelInterval> absenceStaffingLevels = new ArrayList<>();
        absenceStaffingLevels.add(new StaffingLevelInterval(0, 0, duration));
        staffingLevel.setPresenceStaffingLevelInterval(StaffingLevelIntervals);
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
        PhaseDTO phase = phaseService.getUnitPhaseByDate(unitId, proposedDate);
        List<DayType> dayTypes = userIntegrationService.getDayType(proposedDate);
        Map<String, Object> mapOfPhaseAndDayType = new HashMap<>();
        mapOfPhaseAndDayType.put("phase", phase);
        mapOfPhaseAndDayType.put("dayType", dayTypes.isEmpty() ? dayTypes.get(0) : Collections.EMPTY_LIST);
        return mapOfPhaseAndDayType;

    }


    private void createStaffingLevelObject(List<Map<String, String>> processedData, long unitId) {

        List<PresenceStaffingLevelDto> staffingDtoList = new ArrayList<PresenceStaffingLevelDto>();
        PresenceStaffingLevelDto staffingDTO;
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlList = new ArrayList<StaffingLevelTimeSlotDTO>();
        StaffingLevelTimeSlotDTO staffingLevelTimeSlot;
        Duration duration;
        StaffingLevelSetting staffingLevelSetting;
        LocalTime fromTime;
        LocalTime toTime;
        Set<StaffingLevelActivity> activitySet;
        Map<BigInteger, Integer> activityRankMap = new HashMap<>();


        Date date = null;

        int i = 0;
        int seq = 0;
        DateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
        Map<String, String> firstData = processedData.get(0);
        duration = new Duration(LocalTime.MIN, LocalTime.MAX);
        staffingLevelSetting = new StaffingLevelSetting(15, duration);

        try {
            date = sourceFormat.parse(firstData.get("forDay"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate dateInLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeekCount = dateInLocal.get(weekOfYear);

        staffingDTO = new PresenceStaffingLevelDto(null, date, currentWeekCount, staffingLevelSetting);

        for (Map<String, String> singleData : processedData) {

            if (singleData.containsKey("forDay") && i != 0) {

                staffingDTO.setPresenceStaffingLevelInterval(staffingLevelTimeSlList);
                staffingDtoList.add(staffingDTO);
                activityRankMap = new HashMap<>();

                seq = 0;
                staffingDTO = new PresenceStaffingLevelDto();
                staffingLevelTimeSlList = new ArrayList<StaffingLevelTimeSlotDTO>();
                duration = new Duration(LocalTime.MIN, LocalTime.MAX);
                staffingLevelSetting = new StaffingLevelSetting(15, duration);
                try {
                    date = sourceFormat.parse(singleData.get("forDay"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateInLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
                currentWeekCount = dateInLocal.get(weekOfYear);
                staffingDTO = new PresenceStaffingLevelDto(null, date, currentWeekCount, staffingLevelSetting);
            } else {
                i++;
            }
            String fromTimeS = singleData.get("from");
            String toTimeS = singleData.get("to");
            switch (fromTimeS.length()) {
                case 1:
                    fromTimeS = "000" + fromTimeS;
                    break;
                case 2:
                    fromTimeS = "00" + fromTimeS;
                    break;
                case 3:
                    fromTimeS = "0" + fromTimeS;
                    break;
                default:
                    break;
            }
            switch (toTimeS.length()) {
                case 1:
                    toTimeS = "000" + toTimeS;
                    break;
                case 2:
                    toTimeS = "00" + toTimeS;
                    break;
                case 3:
                    toTimeS = "0" + toTimeS;
                    break;
                default:
                    break;
            }

            fromTime = LocalTime.parse(fromTimeS.substring(0, 2) + ":" + fromTimeS.substring(2, 4));
            toTime = LocalTime.parse(toTimeS.substring(0, 2) + ":" + toTimeS.substring(2, 4));
            duration = new Duration(fromTime, toTime);
            staffingLevelTimeSlot = new StaffingLevelTimeSlotDTO(seq++, Integer.parseInt(singleData.get("min")), Integer.parseInt(singleData.get("max")), duration);

            activitySet = new HashSet<>();
            Iterator<String> keyFirstItr = singleData.keySet().iterator();

            int rank = 0;

            while (keyFirstItr.hasNext()) {
                String keyTemp = keyFirstItr.next();
                if (!keyTemp.equals("to") && !keyTemp.equals("from") && !keyTemp.equals("min")
                        && !keyTemp.equals("max") && !keyTemp.equals("forDay")) {
                    Activity activityDB = activityMongoRepository.getActivityByNameAndUnitId(unitId, keyTemp.trim());
                    if (activityDB != null) {
                        StaffingLevelActivity staffingLevelActivity = new StaffingLevelActivity(activityDB.getId(), keyTemp, Integer.parseInt(singleData.get(keyTemp)), Integer.parseInt(singleData.get(keyTemp)));
                        activitySet.add(staffingLevelActivity);
                        activityRankMap.put(activityDB.getId(), ++rank);
                    }
                }
            }
            staffingLevelTimeSlot.setStaffingLevelActivities(activitySet);
            staffingLevelTimeSlList.add(staffingLevelTimeSlot);
        }
        staffingDTO.setPresenceStaffingLevelInterval(staffingLevelTimeSlList);
        staffingDTO.getStaffingLevelSetting().setActivitiesRank(activityRankMap);
        staffingDtoList.add(staffingDTO);

        staffingDtoList.forEach(staffingLevelDto -> {
            createStaffingLevel(staffingLevelDto, unitId);
        });
    }

    public void processStaffingLevel(MultipartFile file, long unitId) throws IOException {

        CSVParser csvRecords = CSVParser.parse(file.getInputStream(), StandardCharsets.UTF_8, CSVFormat.DEFAULT);
        List<CSVRecord> timeRecords = csvRecords.getRecords();
        CSVRecord headerRecord = timeRecords.get(1);
        CSVRecord columnActivityNameRecord = timeRecords.get(2);
        List<Map<String, String>> recordIndexes = new ArrayList<>();

        Map<String, String> columnEntry = null;
        for (int i = 2; i < headerRecord.size(); i++) {
            if (!headerRecord.get(i).isEmpty()) {
                columnEntry = new HashMap<>();
                columnEntry.put("startIndex", String.valueOf(i));
                columnEntry.put("selectedDay", headerRecord.get(i));
                columnEntry.put(headerRecord.get(i), String.valueOf(i));
                recordIndexes.add(columnEntry);
            }
        }

        Map<String, String> lastIndexEntry = new HashMap<>();
        lastIndexEntry.put("startIndex", String.valueOf(headerRecord.size()));
        lastIndexEntry.put("selectedDay", recordIndexes.get(recordIndexes.size() - 1).get("selectedDay"));
        recordIndexes.add(lastIndexEntry);

        int allRecordsFor = recordIndexes.size();
        List<Map<String, String>> staffingLevelRecordByFromToTimeAndActivity = new ArrayList<>();
        Map<String, String> fromToTimeRecord;
        Set<String> activitiesNameList = new HashSet<>();
        int n = 0;
        int min = 0, max = 0;
        // LOGGER.debug("runningFor Day index "+recordIndexes.toString());
        processRecords:
        for (Map<String, String> dayRecord : recordIndexes) {

            fromToTimeRecord = new LinkedHashMap<>();
            fromToTimeRecord.put("forDay", dayRecord.get("selectedDay"));
            List<StaffingLevelInterval> staffingLevelIntervals = new ArrayList<>(timeRecords.size());
            for (CSVRecord csvRecord : timeRecords) {
                if (csvRecord.getRecordNumber() > 3 && csvRecord.getRecordNumber() < 100) {
                    fromToTimeRecord.put("from", csvRecord.get(0));
                    fromToTimeRecord.put("to", csvRecord.get(1));
                    min = Integer.parseInt(csvRecord.get(Integer.parseInt(dayRecord.get("startIndex"))));
                    max = Integer.parseInt(csvRecord.get(Integer.parseInt(dayRecord.get("startIndex")) + 1));
                    fromToTimeRecord.put("min", csvRecord.get(Integer.parseInt(dayRecord.get("startIndex"))));

                    //setting max value as min if min > max
                    fromToTimeRecord.put("max", String.valueOf(min > max ? min : max));

                    boolean initialCountAdded = false;
                    int startPos = 0;
                    DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                            .appendValue(HOUR_OF_DAY)
                            .appendValue(MINUTE_OF_HOUR, 2)
                            .toFormatter();
                    LocalTime fromTime = LocalTime.parse("0000" + (String) csvRecord.get(0), dateTimeFormatter);
                    LocalTime toTime = LocalTime.parse("0000" + (String) csvRecord.get(1), dateTimeFormatter);

                    Duration staffingLevelIntervalDuration = new Duration(fromTime, toTime);
                    StaffingLevelInterval staffingLevelInterval = new StaffingLevelInterval(new Integer(csvRecord.get(2)), new Integer(csvRecord.get(3)), staffingLevelIntervalDuration);

                    if (initialCountAdded == false) {
                        startPos = 2 + Integer.parseInt(recordIndexes.get(n).get(dayRecord.keySet().toArray()[0]));
                    } else {
                        startPos = Integer.parseInt(recordIndexes.get(n).get(dayRecord.keySet().toArray()[0]));
                    }
                    Set<StaffingLevelActivity> staffingLevelActivities = new HashSet<>();
                    int runFor = Integer.parseInt(recordIndexes.get(n + 1).get(recordIndexes.get(n + 1).keySet().toArray()[0]));
                    for (int j = startPos; j < runFor; j++) {
                        staffingLevelActivities.add(new StaffingLevelActivity(columnActivityNameRecord.get(j), new Integer(csvRecord.get(j)), new Integer(csvRecord.get(j))));
                        fromToTimeRecord.put(columnActivityNameRecord.get(j), csvRecord.get(j));
                        activitiesNameList.add(columnActivityNameRecord.get(j));
                    }
                    staffingLevelInterval.setStaffingLevelActivities(staffingLevelActivities);
                    staffingLevelIntervals.add(staffingLevelInterval);
                    staffingLevelRecordByFromToTimeAndActivity.add(fromToTimeRecord);
                    if (csvRecord.getRecordNumber() < 99)
                        fromToTimeRecord = new HashMap<>();
                }
            }
            n++;
            if (n == allRecordsFor - 1) {
                break processRecords;
            }

        }
        createStaffingLevelObject(staffingLevelRecordByFromToTimeAndActivity, unitId);
    }

    public void submitShiftPlanningInfoToPlanner(Long unitId, Date startDate, Date endDate) {
        List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(unitId, startDate, endDate);

        Map<String, Object> shiftPlanningInfo = new HashMap<>();
        Object[] objects = getStaffingLevelDto(staffingLevels);
        shiftPlanningInfo.put("staffingLevel", (List<ShiftPlanningStaffingLevelDTO>) objects[0]);
        List<BigInteger> activityIds = new ArrayList<BigInteger>((Set<BigInteger>) objects[1]);
        List<ActivityDTO> activityDTOS = activityMongoRepositoryImpl.getAllActivityWithTimeType(unitId, activityIds);
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
        StringBuffer result = new StringBuffer();
        try {
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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
            System.out.println("Exception in json; " + result.toString());
            return null;
        }
        return postBody.toMap();
    }

    private HttpUriRequest createPostRequest(JSONObject body, Map<String, Object> urlParameters, Map<String, String> headers, String url) {
        HttpPost postRequest = new HttpPost(url);
        if (headers == null) headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        postRequest = (HttpPost) setHeaders(headers, postRequest);
        if (urlParameters != null) {
            List<BasicNameValuePair> parametersList = new ArrayList<BasicNameValuePair>();
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
            if (Optional.ofNullable(absenceStaffingLevelDto.getId()).isPresent()) {
                staffingLevel = staffingLevelMongoRepository.findById(absenceStaffingLevelDto.getId()).get();
                if (!staffingLevel.getCurrentDate().equals(absenceStaffingLevelDto.getCurrentDate())) {
                    LOGGER.info("current date modified from {}  to this {}", staffingLevel.getCurrentDate(), absenceStaffingLevelDto.getCurrentDate());
                    exceptionService.unsupportedOperationException(MESSAGE_STAFFLEVEL_CURRENTDATE_UPDATE);
                }
                staffingLevel = StaffingLevelUtil.updateAbsenceStaffingLevels(absenceStaffingLevelDto, unitId, staffingLevel);
            } else {
                staffingLevel = staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(unitId, absenceStaffingLevelDto.getCurrentDate());
                if (Optional.ofNullable(staffingLevel).isPresent()) {
                    staffingLevel = StaffingLevelUtil.updateAbsenceStaffingLevels(absenceStaffingLevelDto, unitId, staffingLevel);
                } else {
                    staffingLevel = StaffingLevelUtil.buildAbsenceStaffingLevels(absenceStaffingLevelDto, unitId);
                }
            }
            staffingLevels.add(staffingLevel);
            StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
            staffingLevelPlanningDTOS.add(staffingLevelPlanningDTO);
        }
        this.save(staffingLevels);
        absenceStaffingLevelDtos = StaffingLevelUtil.buildAbsenceStaffingLevelDto(staffingLevels);
        plannerSyncService.publishStaffingLevels(unitId, staffingLevelPlanningDTOS, IntegrationOperation.UPDATE);
        return absenceStaffingLevelDtos;
    }


    public StaffingLevelDto getStaffingLevel(Long unitId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("getting staffing level organizationId ,startDate ,endDate {},{},{}", unitId, startDate, endDate);

        Map<String, PresenceStaffingLevelDto> presenceStaffingLevelMap = new HashMap<String, PresenceStaffingLevelDto>();
        Map<String, AbsenceStaffingLevelDto> absenceStaffingLevelMap = new HashMap<String, AbsenceStaffingLevelDto>();
        while (!startDate.isAfter(endDate)){
            getStaffingLevelPerDate(unitId, startDate, presenceStaffingLevelMap, absenceStaffingLevelMap);
            startDate = startDate.plusDays(1);
        }
        return new StaffingLevelDto(presenceStaffingLevelMap, absenceStaffingLevelMap);
    }

    private LocalDate getStaffingLevelPerDate(Long unitId, LocalDate startDate, Map<String, PresenceStaffingLevelDto> presenceStaffingLevelMap, Map<String, AbsenceStaffingLevelDto> absenceStaffingLevelMap) {
        StaffingLevel staffingLevel = updateStaffingLevelAvailableStaffCount(startDate,unitId);
        if (!staffingLevel.getPresenceStaffingLevelInterval().isEmpty()) {
            PresenceStaffingLevelDto presenceStaffingLevelDto = new PresenceStaffingLevelDto();
            BeanUtils.copyProperties(staffingLevel, presenceStaffingLevelDto);
            presenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
            presenceStaffingLevelMap.put(DateUtils.getDateStringWithFormat(presenceStaffingLevelDto.getCurrentDate(), "yyyy-MM-dd"), presenceStaffingLevelDto);
        }
        if (!staffingLevel.getAbsenceStaffingLevelInterval().isEmpty()) {
            AbsenceStaffingLevelDto absenceStaffingLevelDto = new AbsenceStaffingLevelDto(staffingLevel.getId(), staffingLevel.getPhaseId(),
                    staffingLevel.getCurrentDate(), staffingLevel.getWeekCount());
            absenceStaffingLevelDto.setMinNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMinNoOfStaff());
            absenceStaffingLevelDto.setMaxNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMaxNoOfStaff());
            absenceStaffingLevelDto.setAbsentNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getAvailableNoOfStaff());
            absenceStaffingLevelDto.setStaffingLevelActivities(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
            absenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
            absenceStaffingLevelMap.put(DateUtils.getDateStringWithFormat(absenceStaffingLevelDto.getCurrentDate(), "yyyy-MM-dd"), absenceStaffingLevelDto);
        }
        return startDate;
    }

    private StaffingLevel updateAbsenceStaffingLevelAvailableStaffCountForNewlyCreatedShift(StaffingLevel staffingLevel) {
        if (!staffingLevel.getAbsenceStaffingLevelInterval().isEmpty()) {
            StaffingLevelInterval absenceStaffingLevelInterval = staffingLevel.getAbsenceStaffingLevelInterval().get(0);
            absenceStaffingLevelInterval.setAvailableNoOfStaff(absenceStaffingLevelInterval.getAvailableNoOfStaff() + 1);
        } else {
            Duration duration = new Duration(LocalTime.MIN, LocalTime.MAX);
            StaffingLevelInterval absenceStaffingLevelInterval = new StaffingLevelInterval(0, 0, duration, 1);
            staffingLevel.getAbsenceStaffingLevelInterval().add(absenceStaffingLevelInterval);
        }
        return staffingLevel;
    }

    private StaffingLevel updatePresenceStaffingLevelAvailableStaffCount(StaffingLevel staffingLevel, List<Shift> shifts) {
        Map[] activityAndParentActivityMap = getActivityAndParentActivityMap(shifts);
        Map<BigInteger,BigInteger> childAndParentActivityIdMap = activityAndParentActivityMap[0];
        Map<BigInteger,Activity> activityMap = activityAndParentActivityMap[1];
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                Activity activity = activityMap.get(shiftActivity.getActivityId());
                if(isNotNull(activity) && (FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))){
                    updateAbsenceStaffingLevelAvailableStaffCountForNewlyCreatedShift(staffingLevel);
                }else {
                    updateStaffingLevelInterval(staffingLevel, shiftActivity, childAndParentActivityIdMap);
                }
            }
        }
        return staffingLevel;
    }

    private Map[] getActivityAndParentActivityMap(List<Shift> shifts){
        List<BigInteger> activityIds = shifts.stream().flatMap(shift -> shift.getActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList());
        List<Activity> parentActivities = activityMongoRepository.findByChildActivityIds(activityIds);
        List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
        Map<BigInteger,Activity> activityMap = activities.stream().collect(Collectors.toMap(Activity::getId,v->v));
        Map<BigInteger,BigInteger> childAndParentActivityIdMap = new HashMap<>();
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                for (Activity parentActivity : parentActivities) {
                    if(parentActivity.getChildActivityIds().contains(shiftActivity.getActivityId())){
                        childAndParentActivityIdMap.put(shiftActivity.getActivityId(),parentActivity.getId());
                    }
                }
            }
        }
        return new Map[]{childAndParentActivityIdMap,activityMap};
    }

    private void updateStaffingLevelInterval(StaffingLevel staffingLevel, ShiftActivity shiftActivity,Map<BigInteger,BigInteger> childAndParentActivityIdMap) {
        int lowerLimit = getLowerIndex(shiftActivity.getStartDate());
        int upperLimit = getUpperIndex(shiftActivity.getEndDate());
        for (int currentIndex = lowerLimit; currentIndex <= upperLimit; currentIndex++) {
            if (currentIndex >= staffingLevel.getPresenceStaffingLevelInterval().size()) {
                LOGGER.info("index value is " + currentIndex + " and size is " + staffingLevel.getPresenceStaffingLevelInterval().size());
                continue;
            }
            //TODO yatharth please verify properly current index sometime greater or equal to size of staffingLevel.getPresenceStaffingLevelInterval()
            int currentAvailableStaffCount = staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getAvailableNoOfStaff();
            staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).setAvailableNoOfStaff(++currentAvailableStaffCount);
            updateStaffingLevelOfChildAndParent(staffingLevel,  currentIndex, shiftActivity.getActivityId());
            if (childAndParentActivityIdMap.containsKey(shiftActivity.getActivityId()))
                updateStaffingLevelOfChildAndParent(staffingLevel, currentIndex, childAndParentActivityIdMap.get(shiftActivity.getActivityId()));

        }

    }

    private void updateStaffingLevelOfChildAndParent(StaffingLevel staffingLevel,int currentIndex, BigInteger activityId) {
            staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelActivities().stream().
                    filter(staffingLevelActivity -> staffingLevelActivity.getActivityId().equals(activityId)).findFirst().
                    ifPresent(staffingLevelActivity -> staffingLevelActivity.setAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff() + 1));
    }


    public Map<String, Object> createStaffingLevelFromStaffingLevelTemplate(Long unitId, StaffingLevelFromTemplateDTO staffingLevelFromTemplateDTO, BigInteger templateId) {
        Map<String, Object> response = new HashMap<>();

        StaffingLevelTemplate staffingLevelTemplate = staffingLevelTemplateRepository.findByIdAndUnitIdAndDeletedFalse(templateId, unitId);
        if (!Optional.ofNullable(staffingLevelTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(STAFFINGLEVELTEMPLATE_NOT_FOUND, templateId);
        }
        Set<BigInteger> activityIds = staffingLevelFromTemplateDTO.getActivitiesByDate().stream().flatMap(s -> s.getActivityIds().stream()).collect(Collectors.toSet());
        List<ActivityValidationError> activityValidationErrors = staffingLevelTemplateService.validateActivityRules(activityIds, ObjectMapperUtils.copyPropertiesByMapper(staffingLevelTemplate, StaffingLevelTemplateDTO.class));
        Map<BigInteger, ActivityValidationError> activityValidationErrorMap = activityValidationErrors.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<StaffingLevel> staffingLevels = new ArrayList<>();
        List<DateWiseActivityDTO> dateWiseActivityDTOS = staffingLevelFromTemplateDTO.getActivitiesByDate();
        filterIncorrectDataByDates(dateWiseActivityDTOS, staffingLevelTemplate.getValidity().getStartDate(), staffingLevelTemplate.getValidity().getEndDate(), activityValidationErrors);
        Set<LocalDate> dates = dateWiseActivityDTOS.stream().map(DateWiseActivityDTO::getLocalDate).collect(Collectors.toSet());
        List<StaffingLevel> allStaffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, dates);
        Map<LocalDate, StaffingLevel> dateStaffingLevelMap = allStaffingLevels.stream().collect(Collectors.toMap(k -> asLocalDate(k.getCurrentDate()), v -> v));
        dateWiseActivityDTOS.forEach(currentDateWiseActivities -> {
            Map<BigInteger, BigInteger> activityMap = currentDateWiseActivities.getActivityIds().stream().collect(Collectors.toMap(k -> k, v -> v));
            Set<StaffingLevelActivity> selectedActivitiesForCurrentDate = new HashSet<>();
            List<StaffingLevelInterval> staffingLevelIntervals = new ArrayList<>();
            staffingLevelTemplate.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
                StaffingLevelInterval currentInterval = ObjectMapperUtils.copyPropertiesByMapper(staffingLevelInterval, StaffingLevelInterval.class);
                AtomicInteger min = new AtomicInteger(0);
                AtomicInteger max = new AtomicInteger(0);
                staffingLevelInterval.getStaffingLevelActivities().forEach(activity -> {

                    if (activityMap.get(activity.getActivityId()) != null && activityValidationErrorMap.get(activity.getActivityId()) == null) {
                        selectedActivitiesForCurrentDate.add(activity);
                        min.addAndGet(activity.getMinNoOfStaff());
                        max.addAndGet(activity.getMaxNoOfStaff());
                    }
                });
                currentInterval.setStaffingLevelActivities(selectedActivitiesForCurrentDate);
                currentInterval.setMinNoOfStaff(min.get());
                currentInterval.setMaxNoOfStaff(max.get());
                staffingLevelIntervals.add(currentInterval);
            });
            StaffingLevel staffingLevel = getStaffingLevelIfExist(dateStaffingLevelMap, currentDateWiseActivities, staffingLevelIntervals, staffingLevelTemplate, unitId);
            staffingLevels.add(staffingLevel);
        });

        if (!staffingLevels.isEmpty()) {
            save(staffingLevels);
        }
        response.put("success", staffingLevels);
        response.put("errors", activityValidationErrors);
        return response;
    }


    private void filterIncorrectDataByDates(List<DateWiseActivityDTO> dateWiseActivityDTOS, LocalDate startDate, LocalDate endDate, List<ActivityValidationError> activityValidationErrors) {
        Iterator<DateWiseActivityDTO> iterator = dateWiseActivityDTOS.iterator();
        while (iterator.hasNext()) {
            DateWiseActivityDTO dateWiseActivityDTO = iterator.next();
            if (dateWiseActivityDTO.getLocalDate().isBefore(startDate) || dateWiseActivityDTO.getLocalDate().isAfter(endDate) || dateWiseActivityDTO.getLocalDate().isBefore(DateUtils.getCurrentLocalDate())) {
                iterator.remove();
                activityValidationErrors.add(new ActivityValidationError(Arrays.asList(exceptionService.getLanguageSpecificText(DATE_OUT_OF_RANGE, dateWiseActivityDTO.getLocalDate()))));
            }
        }
    }


    private StaffingLevel getStaffingLevelIfExist(Map<LocalDate, StaffingLevel> localDateStaffingLevelMap, DateWiseActivityDTO currentDate, List<StaffingLevelInterval> staffingLevelIntervals, StaffingLevelTemplate staffingLevelTemplate, Long unitId) {
        StaffingLevel staffingLevel = localDateStaffingLevelMap.get(currentDate.getLocalDate());
        if (staffingLevel != null) {
            staffingLevel.setPresenceStaffingLevelInterval(staffingLevelIntervals);
        } else {
            staffingLevel = ObjectMapperUtils.copyPropertiesByMapper(staffingLevelTemplate, StaffingLevel.class);
            staffingLevel.setId(null);
            staffingLevel.setPresenceStaffingLevelInterval(staffingLevelIntervals);
            staffingLevel.setWeekCount(getWeekNumberByLocalDate(currentDate.getLocalDate()));
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(unitId, DateUtils.asDate(currentDate.getLocalDate()), null);
            staffingLevel.setPhaseId(phase.getId());
            staffingLevel.setUnitId(unitId);
            staffingLevel.setCurrentDate(DateUtils.asDate(currentDate.getLocalDate()));
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
            for (UpdatedStaffingLevelDTO updatedStaffingLevel : updatedStaffingLevels) {
                getStaffingLevelPerDate(unitId,updatedStaffingLevel.getCurrentDate(),presenceStaffingLevelMap,absenceStaffingLevelMap);
            }
            staffingLevelDto = new StaffingLevelDto(presenceStaffingLevelMap, absenceStaffingLevelMap);
        }
        return staffingLevelDto;
    }
}