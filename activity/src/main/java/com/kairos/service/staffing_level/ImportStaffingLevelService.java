package com.kairos.service.staffing_level;

import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.COMMON_DATE_FORMAT;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

@Service
public class ImportStaffingLevelService {

    public static final String FOR_DAY = "forDay";
    public static final String START_INDEX = "startIndex";
    public static final String SELECTED_DAY = "selectedDay";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportStaffingLevelService.class);


    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject private StaffingLevelService staffingLevelService;


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

    private void createStaffingLevelObject(List<Map<String, String>> processedData, long unitId) {

        List<PresenceStaffingLevelDto> staffingDtoList = new ArrayList<>();
        PresenceStaffingLevelDto staffingDTO;
        List<StaffingLevelInterval> staffingLevelTimeSlList = new ArrayList<>();
        StaffingLevelInterval staffingLevelTimeSlot;
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
                    LOGGER.error("error {}", e.getMessage());
                }
                dateInLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
                currentWeekCount = dateInLocal.get(weekOfYear);
                staffingDTO = new PresenceStaffingLevelDto(null, date, currentWeekCount, staffingLevelSetting);
            } else {
                i++;
            }

            String toTimeS = updateTimeString(singleData, "to");
            String fromTimeS = updateTimeString(singleData, "from");
            fromTime = LocalTime.parse(fromTimeS.substring(0, 2) + ":" + fromTimeS.substring(2, 4));
            toTime = LocalTime.parse(toTimeS.substring(0, 2) + ":" + toTimeS.substring(2, 4));
            duration = new Duration(fromTime, toTime);
            staffingLevelTimeSlot = new StaffingLevelInterval(seq++, Integer.parseInt(singleData.get("min")), Integer.parseInt(singleData.get("max")), duration);

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
        staffingDtoList.add(staffingDTO);

        staffingDtoList.forEach(staffingLevelDto -> {
            staffingLevelService.createStaffingLevel(staffingLevelDto, unitId);
        });
    }

    private String updateTimeString(Map<String, String> singleData, String time) {
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
}
