package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.attendance.AttendanceDTO;
import com.kairos.response.dto.web.attendance.UnitIdAndNameDTO;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AttendanceSettingService extends MongoBaseService {

    @Inject
    private AttendanceSettingRepository attendanceSettingRepository;

    @Inject
    private GenericIntegrationService restClient;

    @Inject
    private ShiftService shiftService;

    @Inject
    private ExceptionService exceptionService;

    public AttendanceDTO getAttendanceSetting() {
    AttendanceSetting attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(),DateUtils.asDate(LocalDate.now().minusDays(1)));
    return new AttendanceDTO(attendanceSetting.getAttendanceDuration());
    }

    public AttendanceDTO updateAttendanceSetting(Long unitId,boolean checkIn) {
        AttendanceDTO attendanceDTO = null;
        AttendanceSetting attendanceSetting = null;
        Long userId = Long.valueOf(UserContext.getUserDetails().getId());
        List<StaffResultDTO> staffAndOrganizationIds = restClient.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("message.user.staff.notfound");
        }

        StaffResultDTO staffId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
        if (staffId == null) {
            exceptionService.actionNotPermittedException("message.staff.notfound");
        }
//        LocalDateTime chechInDateAndTime= DateUtils.getTimezonedCurrentDateTime(staffId.getTimeZone());
//        LocalDateTime currentDate=DateUtils.getTimezonedCurrentDate(chechInDateAndTime);
       // ShiftQueryResult shiftQueryResults = shiftService.getShiftByStaffIdAndDate(staffIds, DateUtils.getCurrentDate());
//        if (!Optional.ofNullable(shiftQueryResults).isPresent() && unitId == null) {
//            List<UnitIdAndNameDTO> unitIdAndName = staffAndOrganizationIds.stream().map(s -> new UnitIdAndNameDTO(s.getUnitId(), s.getUnitName())).collect(Collectors.toList());
//            attendanceDTO = new AttendanceDTO(unitIdAndName);
//        } else {
//
            attendanceSetting = (checkIn == true) ? checkInSetting(unitId, staffId) : checkOutSetting(staffId);
            attendanceDTO = new AttendanceDTO(attendanceSetting.getAttendanceDuration());
            save(attendanceSetting);
        return attendanceDTO;
    }

    private AttendanceSetting checkInSetting(Long unitId,StaffResultDTO staffId ) {
        AttendanceSetting attendanceSetting;
                  LocalDateTime chechInDate= DateUtils.getTimezonedCurrentDateTime(staffId.getTimeZone());
                AttendanceDuration attendanceDuration = new AttendanceDuration(chechInDate);
                attendanceSetting = new AttendanceSetting(unitId, staffId.getStaffId(), UserContext.getUserDetails().getId(), attendanceDuration);
                return attendanceSetting;
        }

    private AttendanceSetting checkOutSetting(StaffResultDTO staffId){
        AttendanceDuration duration = null;
        AttendanceSetting attendanceSetting;
        attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.asDate(LocalDate.now().minusDays(1)));
        if (Optional.ofNullable(attendanceSetting).isPresent()) {
            duration = attendanceSetting.getAttendanceDuration();
            if (duration.getTo() == null) {
                LocalDateTime chechOutDate= DateUtils.getTimezonedCurrentDateTime(staffId.getTimeZone());
                duration.setTo(chechOutDate);
            }
        }
        return attendanceSetting;
    }
//TODO
//    private AttendanceSetting checkInSetting(Long unitId,List<StaffResultDTO> staffAndOrganizationIds ,ShiftQueryResult shiftQueryResults) {
//        AttendanceSetting attendanceSetting;
//        if (unitId != null) {
//            StaffResultDTO staffId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
//            if (staffId == null) {
//                exceptionService.actionNotPermittedException("message.staff.notfound");
//            }
//            LocalDateTime chechInDateAndTime= DateUtils.getTimezonedCurrentDateTime(staffId.getTimeZone());
//            AttendanceDuration attendanceDuration = new AttendanceDuration(LocalDateTime.now());
//            attendanceSetting = new AttendanceSetting(unitId, staffId.getStaffId(), UserContext.getUserDetails().getId(), attendanceDuration);
//        } else {
//            AttendanceDuration attendanceDuration = new AttendanceDuration(LocalDateTime.now());
//            attendanceSetting = new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(), UserContext.getUserDetails().getId(), attendanceDuration);
//        }
//        return attendanceSetting;
//    }

}



