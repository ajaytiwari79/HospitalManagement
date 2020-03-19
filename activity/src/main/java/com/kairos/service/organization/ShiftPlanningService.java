package com.kairos.service.organization;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.enums.data_filters.StaffFilterSelectionDTO;
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

    public List<StaffShiftDetails> getShiftPlanningDetailsForUnit(Long unitId, ShiftSearchDTO shiftSearchDTO){

        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO.getStaffFilters());

        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails );

       final Set<Long> employmentIds = new HashSet<>();
                staffListWithPersonalDetails.forEach(staffShiftDetails ->
             employmentIds.addAll(staffShiftDetails.getEmployments().stream().map(employment -> employment.getId()).collect(Collectors.toList()))
        );
        LOGGER.debug("employment ids are {}",employmentIds);
        Date dateToday = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate sixWeeksBefore = LocalDate.now().minusWeeks(6);
        LocalDate sixWeeksAfter = LocalDate.now().plusWeeks(6);
        Date fromDate = Date.from(sixWeeksBefore.atStartOfDay(ZoneId.systemDefault()).toInstant())  ;
        Date toDate = Date.from(sixWeeksAfter.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LOGGER.debug("fetching shifts between start {} and end {} date",fromDate,toDate);
        List<StaffShiftDetails> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsByEmploymentsAndBetweenDuration(employmentIds,fromDate,toDate);

        return assignShiftsToStaff(staffListWithPersonalDetails,shiftWithActivityDTOS);
//        return staffListWithPersonalDetails;
    }

    public StaffShiftDetails getShiftPlanningDetailsForOneStaff(Long unitId, ShiftSearchDTO shiftSearchDTO){
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO.getStaffFilters());
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails );
//        Optional<StaffShiftDetails> loggedInStaffDetails = staffListWithPersonalDetails.stream().filter(staff-> staff.getUserId() == shiftSearchDTO.getLoggedInUserId()).findFirst();
        int i=0;
        StaffShiftDetails matchedStaff = null;
        for(StaffShiftDetails staffShiftDetails:staffListWithPersonalDetails){
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetails.getUserId())) {
                matchedStaff = staffShiftDetails;
                break;
            }
            i++;
        }
       LOGGER.debug(" staff found at index {}",i);

        staffListWithPersonalDetails.remove(i);
        staffListWithPersonalDetails.add(0,matchedStaff);
        final Set<Long> employmentIds = new HashSet<>();
        employmentIds.addAll(matchedStaff.getEmployments().stream().map(employment -> employment.getId()).collect(Collectors.toSet()));
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds);
        matchedStaff.setShifts(shiftDetails.getShifts());
        return matchedStaff;
    }


    public List<StaffShiftDetails> getUnitPlanningAndShiftForSelectedStaff(Long unitId, ShiftSearchDTO shiftSearchDTO){
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO.getStaffFilters());

        int i=-1;
        StaffShiftDetails matchedStaff = null;
        for(StaffShiftDetails staffShiftDetails:staffListWithPersonalDetails){
            i++;
            LOGGER.debug(" staff's user id {}",staffShiftDetails.getUserId());
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetails.getUserId())) {
                matchedStaff = staffShiftDetails;
                break;
            }

        }
        LOGGER.debug(" staff found at index {}",i);
        if(i<staffListWithPersonalDetails.size()) {
            staffListWithPersonalDetails.remove(i);
            staffListWithPersonalDetails.add(0,matchedStaff);
        }

        final Set<Long> employmentIds = new HashSet<>();
        employmentIds.addAll(matchedStaff.getEmployments().stream().map(employment -> employment.getId()).collect(Collectors.toSet()));
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds);
        matchedStaff.setShifts(shiftDetails.getShifts());
        return staffListWithPersonalDetails;
    }

    private StaffShiftDetails findShiftsForSelectedEmploymentsAndDuration(Set<Long> employmentIds){
        LOGGER.debug("employment ids are {}",employmentIds);
        Date dateToday = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate sixWeeksBefore = LocalDate.now().minusWeeks(6);
        LocalDate sixWeeksAfter = LocalDate.now().plusWeeks(6);
        Date fromDate = Date.from(sixWeeksBefore.atStartOfDay(ZoneId.systemDefault()).toInstant())  ;
        Date toDate = Date.from(sixWeeksAfter.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LOGGER.debug("fetching shifts between start {} and end {} date",fromDate,toDate);
        StaffShiftDetails staffShiftDetails = shiftMongoRepository.getAllShiftsForOneStaffWithEmploymentsAndBetweenDuration(employmentIds,fromDate,toDate);
        return staffShiftDetails;
    }


    public List<StaffShiftDetails> getAllStaffEligibleForPlanning(Long unitId, List<StaffFilterSelectionDTO> staffFilters){
      return  userIntegrationService.getAllPlanningStaffForUnit(unitId,staffFilters);
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

    private  StaffShiftDetails assignShiftsToStaff(StaffShiftDetails staffShiftPersonalDetails,StaffShiftDetails shiftData){
        staffShiftPersonalDetails.setShifts(shiftData.getShifts());
        return staffShiftPersonalDetails;
    }



}
