package com.kairos.service.staffing_level;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DataNotFoundException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityValidationError;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.organization.OrganizationSkillAndOrganizationTypesDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.SkillLevel;
import com.kairos.persistence.model.activity.Activity;
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
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.service_util.StaffingLevelUtil;
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
import static com.kairos.constants.AppConstants.LEVEL;
import static com.kairos.constants.AppConstants.SKILL_ID;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.CommonConstants.FULL_WEEK;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

@Service
@Transactional
public class StaffingLevelService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffingLevelService.class);
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FOR_DAY = "forDay";
    public static final String START_INDEX = "startIndex";
    public static final String SELECTED_DAY = "selectedDay";

    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;

    @Inject
    private PhaseService phaseService;
    @Inject
    private EnvConfig envConfig;
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
        //boolean activitiesRankUpdate = staffingLevelActivityRankService.updateStaffingLevelActivityRank(DateUtils.asLocalDate(staffingLevel.getCurrentDate()), staffingLevel.getId(), staffingLevel.getStaffingLevelSetting().getActivitiesRank());
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

        return staffingLevelMongoRepository.findById(staffingLevelId).orElseThrow(()->new DataNotFoundByIdException(convertMessage("Staffing Level Not Found By Id : {}", staffingLevelId)));
    }

    /**
     * @param presenceStaffingLevelDTO
     * @param unitId
     */
    public PresenceStaffingLevelDto updatePresenceStaffingLevel(BigInteger staffingLevelId, Long unitId
            , PresenceStaffingLevelDto presenceStaffingLevelDTO) {
        StaffingLevelUtil.sortStaffingLevelActivities(presenceStaffingLevelDTO, presenceStaffingLevelDTO.getStaffingLevelSetting().getActivitiesRank());
        LOGGER.info("updating staffing level organizationId and staffingLevelId is {} ,{}", unitId, staffingLevelId);
        StaffingLevel staffingLevel = staffingLevelMongoRepository.findById(staffingLevelId).orElseThrow(()->new DataNotFoundException(convertMessage("Staffing Level Not Found")));
        if (!staffingLevel.getCurrentDate().equals(presenceStaffingLevelDTO.getCurrentDate())) {
            LOGGER.info("current date modified from {}  to this {}", staffingLevel.getCurrentDate(), presenceStaffingLevelDTO.getCurrentDate());
            exceptionService.unsupportedOperationException(MESSAGE_STAFFLEVEL_CURRENTDATE_UPDATE);
        }
        Map<BigInteger, BigInteger> childAndParentActivityIdMap = new HashMap<>();
        staffingLevel = StaffingLevelUtil.updateStaffingLevels(staffingLevelId, presenceStaffingLevelDTO, unitId, staffingLevel, childAndParentActivityIdMap);
        staffingLevelMongoRepository.save(staffingLevel);
        Collections.sort(presenceStaffingLevelDTO.getPresenceStaffingLevelInterval(), Comparator.comparing(StaffingLevelTimeSlotDTO::getSequence));
        StaffingLevelPlanningDTO staffingLevelPlanningDTO = new StaffingLevelPlanningDTO(staffingLevel.getId(), staffingLevel.getPhaseId(), staffingLevel.getCurrentDate(), staffingLevel.getWeekCount(), staffingLevel.getStaffingLevelSetting(), staffingLevel.getPresenceStaffingLevelInterval(), null);
        plannerSyncService.publishStaffingLevel(unitId, staffingLevelPlanningDTO, IntegrationOperation.UPDATE);
        presenceStaffingLevelDTO.setUpdatedAt(staffingLevel.getUpdatedAt());
       return ObjectMapperUtils.copyPropertiesByMapper(updateStaffingLevelAvailableStaffCount(asLocalDate(presenceStaffingLevelDTO.getCurrentDate()),unitId),PresenceStaffingLevelDto.class);
    }

    public StaffingLevel updateStaffingLevelAvailableStaffCount(LocalDate localDate,Long unitId) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        Date startDate = asDate(localDate);
        Date endDate = getEndOfDay(startDate);

        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse( startDate, endDate,unitId);
        List<ShiftDTO>  shiftDTOS = shiftService.updateDraftShiftToShift(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shifts, ShiftDTO.class),userAccessRoleDTO);
        StaffingLevel staffingLevel=staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(UserContext.getUnitId(),startDate);
        if (isNull(staffingLevel)) {
            staffingLevel = createDefaultStaffingLevel(unitId, startDate);
        }
        return updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftDTOS,Shift.class), null);

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
        PhaseDTO phase = phaseService.getUnitPhaseByDate(unitId, proposedDate);
        List<DayType> dayTypes = userIntegrationService.getDayType(proposedDate);
        Map<String, Object> mapOfPhaseAndDayType = new HashMap<>();
        mapOfPhaseAndDayType.put("phase", phase);
        mapOfPhaseAndDayType.put("dayType", dayTypes.isEmpty() ? dayTypes.get(0) : Collections.EMPTY_LIST);
        return mapOfPhaseAndDayType;

    }


    private void createStaffingLevelObject(List<Map<String, String>> processedData, long unitId) {

        List<PresenceStaffingLevelDto> staffingDtoList = new ArrayList<>();
        PresenceStaffingLevelDto staffingDTO;
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlList = new ArrayList<>();
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
        DateFormat sourceFormat = new SimpleDateFormat(COMMON_DATE_FORMAT);
        Map<String, String> firstData = processedData.get(0);
        duration = new Duration(LocalTime.MIN, LocalTime.MAX);
        staffingLevelSetting = new StaffingLevelSetting(15, duration);

        try {
            date = sourceFormat.parse(firstData.get(FOR_DAY));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate dateInLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int currentWeekCount = dateInLocal.get(weekOfYear);

        staffingDTO = new PresenceStaffingLevelDto(null, date, currentWeekCount, staffingLevelSetting);

        for (Map<String, String> singleData : processedData) {

            if (singleData.containsKey(FOR_DAY) && i != 0) {

                staffingDTO.setPresenceStaffingLevelInterval(staffingLevelTimeSlList);
                staffingDtoList.add(staffingDTO);
                activityRankMap = new HashMap<>();

                seq = 0;
                staffingLevelTimeSlList = new ArrayList<>();
                duration = new Duration(LocalTime.MIN, LocalTime.MAX);
                staffingLevelSetting = new StaffingLevelSetting(15, duration);
                try {
                    date = sourceFormat.parse(singleData.get(FOR_DAY));
                } catch (ParseException e) {
                    LOGGER.error("error {}",e.getMessage());
                }
                dateInLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
                currentWeekCount = dateInLocal.get(weekOfYear);
                staffingDTO = new PresenceStaffingLevelDto(null, date, currentWeekCount, staffingLevelSetting);
            } else {
                i++;
            }

            String toTimeS = updateTimeString(singleData,"to");
            String fromTimeS = updateTimeString(singleData,"from");
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
                        && !keyTemp.equals("max") && !keyTemp.equals(FOR_DAY)) {
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

    private String updateTimeString(Map<String, String> singleData,String time) {
        String times = singleData.get(time);
        switch (times.length()) {
            case 1:
                times = "000" + times;
                break;
            case 2:
                times = "00" + times;
                break;
            case 3:
                times = "0" + times;
                break;
            default:
                break;
        }
        return times;
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
                columnEntry.put(START_INDEX, String.valueOf(i));
                columnEntry.put(SELECTED_DAY, headerRecord.get(i));
                columnEntry.put(headerRecord.get(i), String.valueOf(i));
                recordIndexes.add(columnEntry);
            }
        }

        Map<String, String> lastIndexEntry = new HashMap<>();
        lastIndexEntry.put(START_INDEX, String.valueOf(headerRecord.size()));
        lastIndexEntry.put(SELECTED_DAY, recordIndexes.get(recordIndexes.size() - 1).get(SELECTED_DAY));
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
            fromToTimeRecord.put(FOR_DAY, dayRecord.get(SELECTED_DAY));
            List<StaffingLevelInterval> staffingLevelIntervals = new ArrayList<>(timeRecords.size());
            for (CSVRecord csvRecord : timeRecords) {
                if (csvRecord.getRecordNumber() > 3 && csvRecord.getRecordNumber() < 100) {
                    fromToTimeRecord.put("from", csvRecord.get(0));
                    fromToTimeRecord.put("to", csvRecord.get(1));
                    min = Integer.parseInt(csvRecord.get(Integer.parseInt(dayRecord.get(START_INDEX))));
                    max = Integer.parseInt(csvRecord.get(Integer.parseInt(dayRecord.get(START_INDEX)) + 1));
                    fromToTimeRecord.put("min", csvRecord.get(Integer.parseInt(dayRecord.get(START_INDEX))));

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
        StringBuilder result = new StringBuilder();
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
            StaffingLevel staffingLevel=null;
            if(absenceStaffingLevelDto.getId()!=null){
                staffingLevel = staffingLevelMongoRepository.findById(absenceStaffingLevelDto.getId()).orElse(null);
            }

            if (isNotNull(staffingLevel)) {
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
        staffingLevelMongoRepository.saveEntities(staffingLevels);
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
            presenceStaffingLevelMap.put(DateUtils.getDateStringWithFormat(presenceStaffingLevelDto.getCurrentDate(), YYYY_MM_DD), presenceStaffingLevelDto);
        }
        if (!staffingLevel.getAbsenceStaffingLevelInterval().isEmpty()) {
            AbsenceStaffingLevelDto absenceStaffingLevelDto = new AbsenceStaffingLevelDto(staffingLevel.getId(), staffingLevel.getPhaseId(),
                    staffingLevel.getCurrentDate(), staffingLevel.getWeekCount());
            absenceStaffingLevelDto.setMinNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMinNoOfStaff());
            absenceStaffingLevelDto.setMaxNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getMaxNoOfStaff());
            absenceStaffingLevelDto.setAbsentNoOfStaff(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getAvailableNoOfStaff());
            absenceStaffingLevelDto.setStaffingLevelActivities(staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities());
            absenceStaffingLevelDto.setUpdatedAt(staffingLevel.getUpdatedAt());
            absenceStaffingLevelMap.put(DateUtils.getDateStringWithFormat(absenceStaffingLevelDto.getCurrentDate(), YYYY_MM_DD), absenceStaffingLevelDto);
        }
        return startDate;
    }

    private StaffingLevel updateAbsenceStaffingLevelAvailableStaffCount(StaffingLevel staffingLevel) {
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

    public StaffingLevel updatePresenceStaffingLevelAvailableStaffCount(StaffingLevel staffingLevel, List<Shift> shifts, Map<Long, List<Map<String, Object>>> staffSkillsMap) {
        Map[] activityAndParentActivityMap = getActivityAndParentActivityMap(shifts);
        Map<BigInteger,BigInteger> childAndParentActivityIdMap = activityAndParentActivityMap[0];
        Map<BigInteger,Activity> activityMap = activityAndParentActivityMap[1];
        if(isNull(staffSkillsMap)) {
            List<Long> staffIds = shifts.stream().map(shift-> shift.getStaffId()).collect(Collectors.toList());
            List<StaffDTO> staffDTOS = userIntegrationService.getSkillIdAndLevelByStaffIds(UserContext.getUserDetails().getCountryId(), staffIds, asLocalDate(staffingLevel.getCurrentDate()), asLocalDate(staffingLevel.getCurrentDate())).get(asLocalDate(staffingLevel.getCurrentDate()).toString());
            staffSkillsMap = staffDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getSkillInfo()));
        }
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                Activity activity = activityMap.get(shiftActivity.getActivityId());
                if(isNotNull(activity) && (FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()))){
                    updateAbsenceStaffingLevelAvailableStaffCount(staffingLevel);
                }else {
                    int durationMinutes = staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                    updateStaffingLevelInterval(shift.getBreakActivities(),durationMinutes,staffingLevel, shiftActivity, childAndParentActivityIdMap, shift.getStaffId(), staffSkillsMap);
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

    private void updateStaffingLevelInterval(List<ShiftActivity> breakActivities,int durationMinutes,StaffingLevel staffingLevel, ShiftActivity shiftActivity,Map<BigInteger,BigInteger> childAndParentActivityIdMap, Long staffId, Map<Long, List<Map<String,Object>>> staffSkillsMap) {
        for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
            Date startDate = getDateByLocalTime(staffingLevel.getCurrentDate(),staffingLevelInterval.getStaffingLevelDuration().getFrom());
            Date endDate = staffingLevelInterval.getStaffingLevelDuration().getFrom().isAfter(staffingLevelInterval.getStaffingLevelDuration().getTo()) ? asDate(asLocalDate(staffingLevel.getCurrentDate()).plusDays(1)) : getDateByLocalTime(staffingLevel.getCurrentDate(),staffingLevelInterval.getStaffingLevelDuration().getTo());
            DateTimeInterval interval = new DateTimeInterval(startDate,endDate);
            updateShiftActivityStaffingLevel(durationMinutes, shiftActivity, staffingLevelInterval, interval,breakActivities);
            int availableNoOfStaff = staffingLevelInterval.getStaffingLevelActivities().stream().mapToInt(staffingLevelActivity -> staffingLevelActivity.getAvailableNoOfStaff()).sum();
            for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                updateShiftActivityStaffingLevel(durationMinutes, childActivity, staffingLevelInterval, interval,breakActivities);
            }
            staffingLevelInterval.setAvailableNoOfStaff(availableNoOfStaff);
            if(isCollectionNotEmpty(staffingLevelInterval.getStaffingLevelSkills()) && isNotEmpty(staffSkillsMap)){
                updateStaffingLevelSkills(staffingLevelInterval,staffId, staffSkillsMap,interval,shiftActivity);
            }
        }
    }

    private void updateStaffingLevelSkills(StaffingLevelInterval staffingLevelInterval, Long staffId, Map<Long, List<Map<String,Object>>> staffSkillsMap,DateTimeInterval interval,ShiftActivity shiftActivity){
        for (StaffingLevelSkill staffingLevelSkill : staffingLevelInterval.getStaffingLevelSkills()){
            if(staffSkillsMap.containsKey(staffId)) {
                for (Map<String, Object> staffSkill : staffSkillsMap.get(staffId)) {
                    if (staffingLevelSkill.getSkillId().equals(Long.valueOf(staffSkill.get(SKILL_ID).toString())) && interval.overlaps(shiftActivity.getInterval())) {
                        updateAvailableNoOfStaff(staffingLevelSkill, staffSkill);
                    }
                }
            }
        }
    }

    private void updateAvailableNoOfStaff(StaffingLevelSkill staffingLevelSkill, Map<String, Object> staffSkill) {
        SkillLevelSetting basicSkillLevelSetting = staffingLevelSkill.getSkillLevelSettingBySkillLevel(SkillLevel.BASIC);
        SkillLevelSetting advanceSkillLevelSetting = staffingLevelSkill.getSkillLevelSettingBySkillLevel(SkillLevel.ADVANCE);
        SkillLevelSetting expertSkillLevelSetting = staffingLevelSkill.getSkillLevelSettingBySkillLevel(SkillLevel.EXPERT);
        if(SkillLevel.BASIC.toString().equals(staffSkill.get(LEVEL))){
            basicSkillLevelSetting.setAvailableNoOfStaff(basicSkillLevelSetting.getAvailableNoOfStaff()+1);
        }else if(SkillLevel.ADVANCE.toString().equals(staffSkill.get(LEVEL))){
            if(advanceSkillLevelSetting.getNoOfStaff() > advanceSkillLevelSetting.getAvailableNoOfStaff() || basicSkillLevelSetting.getNoOfStaff() <= basicSkillLevelSetting.getAvailableNoOfStaff()){
                advanceSkillLevelSetting.setAvailableNoOfStaff(advanceSkillLevelSetting.getAvailableNoOfStaff()+1);
            }else if(basicSkillLevelSetting.getNoOfStaff() > basicSkillLevelSetting.getAvailableNoOfStaff()){
                basicSkillLevelSetting.setAvailableNoOfStaff(basicSkillLevelSetting.getAvailableNoOfStaff()+1);
            }else{
                advanceSkillLevelSetting.setAvailableNoOfStaff(advanceSkillLevelSetting.getAvailableNoOfStaff()+1);
            }
        }else{
            if(expertSkillLevelSetting.getNoOfStaff() > expertSkillLevelSetting.getAvailableNoOfStaff() || (advanceSkillLevelSetting.getNoOfStaff() <= advanceSkillLevelSetting.getAvailableNoOfStaff() && basicSkillLevelSetting.getNoOfStaff() <= basicSkillLevelSetting.getAvailableNoOfStaff())){
                expertSkillLevelSetting.setAvailableNoOfStaff(expertSkillLevelSetting.getAvailableNoOfStaff()+1);
            }else if(advanceSkillLevelSetting.getNoOfStaff() > advanceSkillLevelSetting.getAvailableNoOfStaff() || basicSkillLevelSetting.getNoOfStaff() <= basicSkillLevelSetting.getAvailableNoOfStaff()){
                advanceSkillLevelSetting.setAvailableNoOfStaff(advanceSkillLevelSetting.getAvailableNoOfStaff()+1);
            }else if(basicSkillLevelSetting.getNoOfStaff() > basicSkillLevelSetting.getAvailableNoOfStaff()){
                basicSkillLevelSetting.setAvailableNoOfStaff(basicSkillLevelSetting.getAvailableNoOfStaff()+1);
            }else{
                expertSkillLevelSetting.setAvailableNoOfStaff(expertSkillLevelSetting.getAvailableNoOfStaff()+1);
            }
        }
    }


    private void updateShiftActivityStaffingLevel(int durationMinutes, ShiftActivity shiftActivity, StaffingLevelInterval staffingLevelInterval, DateTimeInterval interval,List<ShiftActivity> breakActivities) {
        boolean breakValid = breakActivities.stream().anyMatch(shiftActivity1 -> !shiftActivity1.isBreakNotHeld() && interval.overlaps(shiftActivity1.getInterval()) && interval.overlap(shiftActivity1.getInterval()).getMinutes()>=durationMinutes);
        if(!breakValid && interval.overlaps(shiftActivity.getInterval()) && interval.overlap(shiftActivity.getInterval()).getMinutes()>=durationMinutes){
            staffingLevelInterval.getStaffingLevelActivities().stream().filter(staffingLevelActivity -> staffingLevelActivity.getActivityId().equals(shiftActivity.getActivityId())).findFirst().
                    ifPresent(staffingLevelActivity -> staffingLevelActivity.setAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff() + 1));
        }
    }

    public Map<String, Object> createStaffingLevelFromStaffingLevelTemplate(Long unitId, StaffingLevelFromTemplateDTO staffingLevelFromTemplateDTO, BigInteger templateId) {
        Map<String, Object> response = new HashMap<>();

        StaffingLevelTemplate staffingLevelTemplate = staffingLevelTemplateRepository.findByIdAndUnitIdAndDeletedFalse(templateId, unitId);
        if (!Optional.ofNullable(staffingLevelTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(STAFFINGLEVELTEMPLATE_NOT_FOUND, templateId);
        }
        Set<BigInteger> activityIds = staffingLevelFromTemplateDTO.getActivitiesByDate().stream().flatMap(s -> s.getActivityIds().stream()).collect(Collectors.toSet());
        List<BigInteger> parentActivityIds=new ArrayList<>();
        List<ActivityValidationError> activityValidationErrors = staffingLevelTemplateService.validateActivityRules(activityIds, ObjectMapperUtils.copyPropertiesByMapper(staffingLevelTemplate, StaffingLevelTemplateDTO.class),parentActivityIds);
        Map<BigInteger, ActivityValidationError> activityValidationErrorMap = activityValidationErrors.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<DateWiseActivityDTO> dateWiseActivityDTOS = staffingLevelFromTemplateDTO.getActivitiesByDate();
        filterIncorrectDataByDates(dateWiseActivityDTOS, staffingLevelTemplate.getValidity().getStartDate(), staffingLevelTemplate.getValidity().getEndDate(), activityValidationErrors);
        Set<LocalDate> dates = dateWiseActivityDTOS.stream().map(DateWiseActivityDTO::getLocalDate).collect(Collectors.toSet());
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
                        if(parentActivityIds.contains(staffingLevelActivity.getActivityId())){
                            min+=staffingLevelActivity.getMinNoOfStaff();
                            max+=staffingLevelActivity.getMaxNoOfStaff();
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
            List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId,updatedStaffingLevels.stream().map(updatedStaffingLevelDTO -> updatedStaffingLevelDTO.getCurrentDate()).collect(Collectors.toSet()));
            Map<LocalDate,Date> staffingLevelDateMap = staffingLevels.stream().collect(Collectors.toMap(k->asLocalDate(k.getCurrentDate()),StaffingLevel::getUpdatedAt));
            for (UpdatedStaffingLevelDTO updatedStaffingLevel : updatedStaffingLevels) {
                if(isNotNull(updatedStaffingLevel.getUpdatedAt()) && staffingLevelDateMap.containsKey(updatedStaffingLevel.getCurrentDate()) && staffingLevelDateMap.get(updatedStaffingLevel.getCurrentDate()).after(updatedStaffingLevel.getUpdatedAt())) {
                    getStaffingLevelPerDate(unitId, updatedStaffingLevel.getCurrentDate(), presenceStaffingLevelMap, absenceStaffingLevelMap);
                }
            }
            staffingLevelDto = new StaffingLevelDto(presenceStaffingLevelMap, absenceStaffingLevelMap);
        }
        return staffingLevelDto;
    }

    @Async
    public void removedActivityFromStaffingLevel(BigInteger activityId, boolean isPresence){
        List<StaffingLevel> staffingLevels = isPresence ? staffingLevelMongoRepository.findPresenceStaffingLevelsByActivityId(activityId) : staffingLevelMongoRepository.findAbsenceStaffingLevelsByActivityId(activityId);
        for(StaffingLevel staffingLevel : staffingLevels){
            for(StaffingLevelInterval staffingLevelInterval : isPresence ? staffingLevel.getPresenceStaffingLevelInterval() : staffingLevel.getAbsenceStaffingLevelInterval()){
                removedActivityFromStaffingLevelInterval(staffingLevelInterval, activityId);
            }
        }
        staffingLevelMongoRepository.saveAll(staffingLevels);
    }

    private void removedActivityFromStaffingLevelInterval(StaffingLevelInterval staffingLevelInterval, BigInteger activityId) {
        Set<StaffingLevelActivity> staffingLevelActivities = new HashSet<>();
        int minNoOfStaff = 0;
        int maxNoOfStaff = 0;
        for(StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()){
            if(staffingLevelActivity.getActivityId().equals(activityId)){
                minNoOfStaff += staffingLevelActivity.getMinNoOfStaff();
                maxNoOfStaff += staffingLevelActivity.getMaxNoOfStaff();
            }else{
                staffingLevelActivities.add(staffingLevelActivity);
            }
        }
        staffingLevelInterval.setMinNoOfStaff(staffingLevelInterval.getMinNoOfStaff() - minNoOfStaff);
        staffingLevelInterval.setMaxNoOfStaff(staffingLevelInterval.getMaxNoOfStaff() - maxNoOfStaff);
        staffingLevelInterval.setStaffingLevelActivities(staffingLevelActivities);
    }

    public List<StaffingLevel> findByUnitIdAndDates(Long unitId, Date startDate, Date endDate){
        return staffingLevelMongoRepository.findByUnitIdAndDates(unitId, startDate, endDate);
    }
}
