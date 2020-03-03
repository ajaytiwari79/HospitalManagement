package com.kairos.service.organization;

import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShiftPlanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningService.class);
    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    public List<StaffShiftDetails> getShiftPlanningDetailsForUnit(Long unitId){
        List<StaffShiftDetails> staffListWithPersonalDetails = userIntegrationService.getAllPlanningStaffForUnit(unitId);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails  );
       final Set<Long> employmentIds = new HashSet<>();
                staffListWithPersonalDetails.forEach(staffShiftDetails ->
             employmentIds.addAll(staffShiftDetails.getEmployments().stream().map(employment -> employment.getId()).collect(Collectors.toList()))
        );
        LOGGER.debug("employment ids are {}",employmentIds);
        Date fromDate = Date.from(LocalDate.of(2019,01,01).atStartOfDay(ZoneId.systemDefault()).toInstant())  ;
        Date toDate = Date.from(LocalDate.of(2020,12,01).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<StaffShiftDetails> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsByEmploymentsAndBetweenDuration(employmentIds,fromDate,toDate);
        return assignShiftsToStaff(staffListWithPersonalDetails,shiftWithActivityDTOS);
//        return staffListWithPersonalDetails;
    }

    private  List<StaffShiftDetails> assignShiftsToStaff(List<StaffShiftDetails> staffShiftPersonalDetailsList,List<StaffShiftDetails> shiftData){

        for(StaffShiftDetails staffShiftDetails:staffShiftPersonalDetailsList){
            for(StaffShiftDetails shiftDetails:shiftData){
                if(shiftDetails.getId().equals(staffShiftDetails.getId())){
                    staffShiftDetails.setShifts(shiftDetails.getShifts());
                }
            }
        }
        return staffShiftPersonalDetailsList;

    }



}
