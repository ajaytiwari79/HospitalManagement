package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.attendence_setting.TimeAndAttendanceService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/*
 *Created By Pavan on 21/12/18
 *
 */
@Service
public class ShiftStateService {

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeAndAttendanceService timeAndAttendanceService;

    public boolean createShiftState(Long unitId, Date startDate, Date endDate){
        if(startDate.before(DateUtils.getCurrentDayStart()) || endDate.before(DateUtils.getCurrentDayStart())){
            exceptionService.actionNotPermittedException("past.date.allowed");
        }
        List<Shift> shifts=shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(startDate,endDate,unitId);
        timeAndAttendanceService.createShiftState(shifts,false,unitId);
        return true;
    }
}
