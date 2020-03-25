package com.kairos.service.organization;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.enums.shift.ShiftFilterDurationType;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftPlanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningService.class);
    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    public List<StaffShiftDetails> getShiftPlanningDetailsForUnit(Long unitId, ShiftSearchDTO shiftSearchDTO){

        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails );

       final Set<Long> employmentIds = new HashSet<>();
                staffListWithPersonalDetails.forEach(staffShiftDetails ->
             employmentIds.addAll(staffShiftDetails.getEmployments().stream().map(employment -> employment.getId()).collect(Collectors.toList()))
        );
        List<Date> startAndEndDates  = getStartAndEndDates(shiftSearchDTO.getShiftFilterDurationType());
        List<StaffShiftDetails> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsByEmploymentsAndBetweenDuration(employmentIds,startAndEndDates.get(0),startAndEndDates.get(1));
        return assignShiftsToStaff(staffListWithPersonalDetails,shiftWithActivityDTOS);
    }

    public StaffShiftDetails getShiftPlanningDetailsForOneStaff(Long unitId, ShiftSearchDTO shiftSearchDTO){
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails );
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
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds,shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return matchedStaff;
    }


    public List<StaffShiftDetails> getUnitPlanningAndShiftForSelectedStaff(Long unitId, ShiftSearchDTO shiftSearchDTO){
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO);

        int i=-1;
        StaffShiftDetails matchedStaff = null;
        for(StaffShiftDetails staffShiftDetails:staffListWithPersonalDetails){
            i++;
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetails.getUserId())) {
                matchedStaff = staffShiftDetails;
                break;
            }
        }

        if(i<staffListWithPersonalDetails.size()) {
            staffListWithPersonalDetails.remove(i);
            staffListWithPersonalDetails.add(0,matchedStaff);
        }

        final Set<Long> employmentIds = new HashSet<>();
        employmentIds.addAll(matchedStaff.getEmployments().stream().map(employment -> employment.getId()).collect(Collectors.toSet()));
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds,shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return staffListWithPersonalDetails;
    }

    private StaffShiftDetails findShiftsForSelectedEmploymentsAndDuration(Set<Long> employmentIds, ShiftFilterDurationType shiftFilterDurationType){

        LOGGER.debug("employment ids are {}",employmentIds);
        Date dateToday = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Date> startAndEndDates  = getStartAndEndDates(shiftFilterDurationType);
        Date fromDate = startAndEndDates.get(0);
        Date toDate = startAndEndDates.get(1);
        LOGGER.debug("fetching shifts between start {} and end {} date",fromDate,toDate);
        StaffShiftDetails staffShiftDetails = shiftMongoRepository.getAllShiftsForOneStaffWithEmploymentsAndBetweenDuration(employmentIds,fromDate,toDate);
        return staffShiftDetails;
    }

   private List<Date> getStartAndEndDates(ShiftFilterDurationType shiftFilterDurationType){

        List<Date> startAndEndDates = new ArrayList<>(2);

        LocalDate startDate;
        LocalDate endDate;

        switch (shiftFilterDurationType){

            case INDIVIDUAL:
                startDate = LocalDate.now().minusWeeks(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusWeeks(shiftFilterDurationType.getDuration());
                break;
            case DAILY:
                startDate = LocalDate.now().minusDays(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusDays(shiftFilterDurationType.getDuration());
                break;
            case WEEKLY:
                startDate = LocalDate.now().minusWeeks(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusWeeks(shiftFilterDurationType.getDuration());
                break;
            case MONTHLY:
                startDate = LocalDate.now().minusMonths(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusMonths(shiftFilterDurationType.getDuration());
                break;
            default:
                startDate = LocalDate.now().minusWeeks(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusWeeks(shiftFilterDurationType.getDuration());
        }

        Date fromDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())  ;
        Date toDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LOGGER.debug(" searching between dates start {} and end {} for filter type {}",startDate,endDate,shiftFilterDurationType.getValue());
        startAndEndDates.add(fromDate);
        startAndEndDates.add(toDate);
        return startAndEndDates;
    }



    public List<StaffShiftDetails> getAllStaffEligibleForPlanning(Long unitId, ShiftSearchDTO shiftSearchDTO){
      return  userIntegrationService.getAllPlanningStaffForUnit(unitId,shiftSearchDTO);
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
