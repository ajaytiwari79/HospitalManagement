package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.filter_utils.FilterUtils;
import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.staff.EmploymentDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftFilterDurationType;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftPlanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningService.class);
    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    public <T> List<StaffShiftDetails> getShiftPlanningDetailsForUnit(Long unitId, ShiftSearchDTO shiftSearchDTO) {
        Map<FilterType, Set<T>> validMatches = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails);

        final Set<Long> employmentIds = new HashSet<>();
        staffListWithPersonalDetails.forEach(staffShiftDetails ->
                employmentIds.addAll(staffShiftDetails.getEmployments().stream().map(EmploymentDTO::getId).collect(Collectors.toList()))
        );
        List<Date> startAndEndDates = getStartAndEndDates(shiftSearchDTO.getShiftFilterDurationType());
        List<StaffShiftDetails> shiftWithActivityDTOS = shiftMongoRepository.getFilteredShiftsGroupedByStaff(employmentIds, validMatches, unitId, startAndEndDates.get(0), startAndEndDates.get(1));
        return assignShiftsToStaff(staffListWithPersonalDetails, shiftWithActivityDTOS);
    }

    public StaffShiftDetails getShiftPlanningDetailsForOneStaff(Long unitId, ShiftSearchDTO shiftSearchDTO){
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId,shiftSearchDTO);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails );
        int i=0;
        StaffShiftDetails matchedStaff = null;
        for (StaffShiftDetails staffShiftDetails : staffListWithPersonalDetails) {
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetails.getUserId())) {
                matchedStaff = staffShiftDetails;
                break;
            }
            i++;
        }
        LOGGER.debug(" staff found at index {}", i);

        staffListWithPersonalDetails.remove(i);
        staffListWithPersonalDetails.add(0, matchedStaff);
        final Set<Long> employmentIds = Objects.requireNonNull(matchedStaff).getEmployments().stream().map(EmploymentDTO::getId).collect(Collectors.toSet());
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds, shiftSearchDTO.getShiftFilterDurationType());
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

        if (i < staffListWithPersonalDetails.size()) {
            staffListWithPersonalDetails.remove(i);
            staffListWithPersonalDetails.add(0, matchedStaff);
        }

        final Set<Long> employmentIds = Objects.requireNonNull(matchedStaff).getEmployments().stream().map(EmploymentDTO::getId).collect(Collectors.toSet());
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds, shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return staffListWithPersonalDetails;
    }

    private StaffShiftDetails findShiftsForSelectedEmploymentsAndDuration(Set<Long> employmentIds, ShiftFilterDurationType shiftFilterDurationType){

        LOGGER.debug("employment ids are {}",employmentIds);
        List<Date> startAndEndDates  = getStartAndEndDates(shiftFilterDurationType);
        Date fromDate = startAndEndDates.get(0);
        Date toDate = startAndEndDates.get(1);
        LOGGER.debug("fetching shifts between start {} and end {} date",fromDate,toDate);
        return shiftMongoRepository.getAllShiftsForOneStaffWithEmploymentsAndBetweenDuration(employmentIds, fromDate, toDate);
    }

   private List<Date> getStartAndEndDates(ShiftFilterDurationType shiftFilterDurationType){

        List<Date> startAndEndDates = new ArrayList<>(2);

        LocalDate startDate;
        LocalDate endDate;

        switch (shiftFilterDurationType){

            case DAILY:
                startDate = LocalDate.now().minusDays(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusDays(shiftFilterDurationType.getDuration());
                break;
            case MONTHLY:
                startDate = LocalDate.now().minusMonths(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusMonths(shiftFilterDurationType.getDuration());
                break;
            default:
                startDate = LocalDate.now().minusWeeks(shiftFilterDurationType.getDuration());
                endDate = LocalDate.now().plusWeeks(shiftFilterDurationType.getDuration());
        }

        Date fromDate = DateUtils.asDate(startDate);
        Date toDate = DateUtils.asDate(endDate);
        LOGGER.debug(" searching between dates start {} and end {} for filter type {}",startDate,endDate,shiftFilterDurationType.getValue());
        startAndEndDates.add(fromDate);
        startAndEndDates.add(toDate);
        return startAndEndDates;
    }

    public List<StaffShiftDetails> getAllStaffEligibleForPlanning(Long unitId, ShiftSearchDTO shiftSearchDTO){
      return  userIntegrationService.getAllPlanningStaffForUnit(unitId,shiftSearchDTO);
    }

   private  List<StaffShiftDetails> assignShiftsToStaff(List<StaffShiftDetails> staffShiftPersonalDetailsList,List<StaffShiftDetails> shiftData){
       Map<Long, List<ShiftWithActivityDTO>> shiftsMap = shiftData.stream().collect(Collectors.toMap(StaffShiftDetails::getId, StaffShiftDetails::getShifts));
        for(StaffShiftDetails staffShiftDetails:staffShiftPersonalDetailsList){
            staffShiftDetails.setShifts(shiftsMap.getOrDefault(staffShiftDetails.getId(), new ArrayList<>()));
        }
        return staffShiftPersonalDetailsList;
    }


}
