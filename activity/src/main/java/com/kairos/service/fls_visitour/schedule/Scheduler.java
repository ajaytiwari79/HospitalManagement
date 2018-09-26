package com.kairos.service.fls_visitour.schedule;

import com.kairos.dto.activity.task.TaskAppointmentSuggestionDTO;
import de.tourenserver.CallInfoRec;
import de.tourenserver.FixScheduleResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
public interface Scheduler {
    int createCall(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials);
    int updateCall(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials);
    int deleteCall(Map callMetaData, Map<String, String> flsCredentials);
    int cancelCallSchedule(Map callMetaData, Map<String, String> flsCredentials);
    List<TaskAppointmentSuggestionDTO> getAppointmentSuggestions(Map callMetaData, Map<String, String> flsCredentials);
    int confirmAppointment(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials);
    int confirmAppointmentSuggestion(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials);
    int fixedAppointment(Map callMetaData, Map timeFrameInfo, Map<String, String> flsCredentials);
    Map<String,Object> getGeoCode(Map addressToVerify, Map<String, String> flsCredentials);
    CallInfoRec getCallInfo(Map callMetaData, Map<String, String> flsCredentials);
    int createEngineer(Map engineerMetaData, Map<String, String> flsCredentials);
    FixScheduleResponse getSchedule(Map fixScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials);
    int assignWholeDayAbsence(Map fixScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials);
    int createEngineerWorkSchedule(Map fixScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials);
    int optmizeSchedule(Map optimizeScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials);
    int assignAbsencesToFLS(Map fixScheduleMetaData, Map dateTimeInfo, Map<String, String> flsCredentials);
}
