package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.attendance.AttendanceDTO;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class AttendanceSettingService extends MongoBaseService{

@Inject
private AttendanceSettingRepository attendanceSettingRepository;

@Inject
private GenericIntegrationService restClient;

@Inject
private ShiftService shiftService;

@Inject
private  ExceptionService exceptionService;

public Duration getAttendanceSetting(Long unitId, Long staffId) {
//    LocalDate currentDate= DateUtils.getCurrentLocalDate();
//    Duration attendanceDuration=new Duration();
//    AttendanceSetting AttendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
//    if(Optional.ofNullable(AttendanceSetting).isPresent()) {
//     attendanceDuration=AttendanceSetting.getAttendanceDuration().get(AttendanceSetting.getAttendanceDuration().size()-1);
//    }
    return null;
}

public AttendanceDTO updateAttendanceSetting(Long userId, AttendanceDuration attendanceDuration) {
    AttendanceDTO attendanceDTO=null;
    List<StaffResultDTO> staffAndOrganizationIds=restClient.getStaffIdsByUserId(userId);
    if(staffAndOrganizationIds==null)
    {
        exceptionService.actionNotPermittedException("error");
    }
//    Date checkIn=DateUtils.getDateByLocalDateAndLocalTime( DateUtils.asLocalDate(attendanceDuration.getCheckIn()),attendanceDuration.getFrom());
    if(attendanceDuration!=null){

    }
//
//    ShiftQueryResult shiftQueryResults=shiftService.getShiftByStaffIdAndDate(staffAndOrganizationIds.getStaffIds(),checkIn);
//    if(!Optional.ofNullable(shiftQueryResults).isPresent()){
//        return attendanceDTO=new AttendanceDTO(staffAndOrganizationIds.getOrganizationIdAndNameResults());
//    }
//    else {
//        AttendanceSetting getattendanceSetting = attendanceSettingRepository.findByUnitIdAndStaffIdAndDate(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(),checkIn);
//        if (Optional.ofNullable(getattendanceSetting).isPresent()) {
//            if (attendanceDuration.getTo() != null) {
//                AttendanceDuration duration = getattendanceSetting.getAttendanceDuration();
//                if (duration.getCheckOut() == null) {
//                    Date checkOut=DateUtils.getDateByLocalDateAndLocalTime( DateUtils.getCurrentLocalDate(),attendanceDuration.getTo());
//                    duration.setCheckOut(checkOut);
//                }
//            } else {
//                getattendanceSetting.setAttendanceDuration(attendanceDuration);
//            }
//        } else {
//            getattendanceSetting = new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId());
//            AttendanceDuration  attendanceDuration1=new AttendanceDuration();
//            attendanceDuration1.setCheckIn(checkIn);
//            getattendanceSetting.setAttendanceDuration(attendanceDuration1);
//        }
////    AttendanceSetting attendanceSetting=new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(),date);
////    attendanceSetting.getAttendanceDuration().add(attendanceDuration);
//    save(getattendanceSetting);}
//    attendanceDTO=new AttendanceDTO();
//    attendanceDTO.setDuration(attendanceDuration);
    return attendanceDTO;
    }
}
