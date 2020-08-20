package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.filter_utils.FilterUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.EmploymentType;
import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.employment.PlanningEmploymentDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftFilterDurationType;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.FilterType.CTA_ACCOUNT_TYPE;

@Service
public class ShiftPlanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningService.class);
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private WorkTimeAgreementService workTimeAgreementService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;

    @Inject
    private ShiftMongoRepository shiftMongoRepository;


    public <T> List<StaffShiftDetails> getShiftPlanningDetailsForUnit(final Long unitId, final ShiftSearchDTO shiftSearchDTO) {

        Map<FilterType, Set<T>> validMatches = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        List<StaffShiftDetails> staffListWithPersonalDetails = getFilteredListOfStaffPersonalDetails(unitId, shiftSearchDTO, validMatches);
        final Set<Long> employmentIds = new HashSet<>();
        staffListWithPersonalDetails.forEach(staffShiftDetails ->
                employmentIds.addAll(staffShiftDetails.getEmployments().stream().map(PlanningEmploymentDTO::getId).collect(Collectors.toList()))
        );
        boolean includeDateComparison = true;
        if (shiftSearchDTO.getShiftFilterDurationType().equals(ShiftFilterDurationType.INDIVIDUAL)) {
            includeDateComparison = false;
        } else if (shiftSearchDTO.getStartDate().equals(shiftSearchDTO.getEndDate())) {
            shiftSearchDTO.setEndDate(new Date(shiftSearchDTO.getEndDate().getTime() + 86400000));
        }

        List<StaffShiftDetails> shiftWithActivityDTOS = shiftMongoRepository.getFilteredShiftsGroupedByStaff(employmentIds, validMatches, unitId, shiftSearchDTO.getStartDate(), shiftSearchDTO.getEndDate(),includeDateComparison);
        return assignShiftsToStaff(staffListWithPersonalDetails, shiftWithActivityDTOS);
    }

    private <T> List<StaffShiftDetails> getFilteredListOfStaffPersonalDetails(final Long unitId, final ShiftSearchDTO shiftSearchDTO, Map<FilterType, Set<T>> validMatches) {
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails);

        if (CollectionUtils.isEmpty(staffListWithPersonalDetails)) {
            return Collections.emptyList();
        }

        StaffShiftDetails loggedInStaff = null;
        if (staffListWithPersonalDetails.get(0).getUserId().equals(shiftSearchDTO.getLoggedInUserId())) {
            loggedInStaff = staffListWithPersonalDetails.get(0);
        }

        if(validMatches.containsKey(CTA_ACCOUNT_TYPE)){
            Set<Long> staffIds =staffListWithPersonalDetails.stream().map(s -> s.getId()).collect(Collectors.toSet());
            Set<Long> filterStaffIds = filterStaffByCTATemplateAccountType(staffListWithPersonalDetails,staffIds,validMatches);
            staffListWithPersonalDetails = staffListWithPersonalDetails.stream().filter(spd -> filterStaffIds.contains(spd.getId())).collect(Collectors.toList());
        }

        if (validMatches.containsKey(FilterType.REAL_TIME_STATUS)) {
            Set<Long> staffIds = shiftMongoRepository.getStaffListAsIdForRealtimeCriteria(unitId, (Set<String>) validMatches.get(FilterType.REAL_TIME_STATUS));
            staffListWithPersonalDetails = staffListWithPersonalDetails.stream().filter(spd -> staffIds.contains(spd.getId())).collect(Collectors.toList());
        }

        if (!staffListWithPersonalDetails.contains(loggedInStaff) && loggedInStaff != null) {
            staffListWithPersonalDetails.add(0, loggedInStaff);
        }

        return staffListWithPersonalDetails;
    }

    public <T> List<StaffShiftDetails> getFilteredStaffForMatchingFilter(final Long unitId, final ShiftSearchDTO shiftSearchDTO) {
        Map<FilterType, Set<T>> validMatches = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        long shiftFilterCount = validMatches.keySet().stream().filter(filterType -> filterType.getMatchType().equals(FilterType.MatchType.SHIFT)).count();
        LOGGER.debug(" shift filters present are {}", shiftFilterCount);
        List<StaffShiftDetails> staffListWithPersonalDetails = getFilteredListOfStaffPersonalDetails(unitId, shiftSearchDTO, validMatches);
        if (CollectionUtils.isEmpty(staffListWithPersonalDetails)) {
            return Collections.emptyList();
        }
        final Set<Long> employmentIds = new HashSet<>();
        staffListWithPersonalDetails.forEach(staffShiftDetails ->
                employmentIds.addAll(staffShiftDetails.getEmployments().stream().map(PlanningEmploymentDTO::getId).collect(Collectors.toList()))
        );

        if (shiftSearchDTO.getStartDate().equals(shiftSearchDTO.getEndDate())) {
            shiftSearchDTO.setEndDate(new Date(shiftSearchDTO.getEndDate().getTime() + 86400000));
        }
        boolean includeDateComparison = true;
        if (shiftSearchDTO.getShiftFilterDurationType().equals(ShiftFilterDurationType.INDIVIDUAL)) {
            includeDateComparison = false;
        }
        List<StaffShiftDetails> shiftWithActivityDTOS = Collections.EMPTY_LIST;
        if (shiftFilterCount > 0) {
            shiftWithActivityDTOS = shiftMongoRepository.getStaffListFilteredByShiftCriteria(employmentIds, validMatches, unitId, shiftSearchDTO.getStartDate(), shiftSearchDTO.getEndDate(), includeDateComparison);
        }
        return getStaffListAfterShiftFilterMatches(staffListWithPersonalDetails, shiftWithActivityDTOS, shiftSearchDTO.getLoggedInUserId(),shiftSearchDTO,shiftFilterCount);
    }

    public StaffShiftDetails getShiftPlanningDetailsForOneStaff(Long unitId, ShiftSearchDTO shiftSearchDTO) {
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails);
        int i = 0;
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
        final Set<Long> employmentIds = Objects.requireNonNull(matchedStaff).getEmployments().stream().map(PlanningEmploymentDTO::getId).collect(Collectors.toSet());
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds, shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return matchedStaff;
    }


    public <T> List<StaffShiftDetails> getUnitPlanningAndShiftForSelectedStaff(Long unitId, ShiftSearchDTO shiftSearchDTO) {
        Map<FilterType, Set<T>> validMatches = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        List<StaffShiftDetails> staffListWithPersonalDetails = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO);
        if (CollectionUtils.isEmpty(staffListWithPersonalDetails)) {
            return Collections.emptyList();
        }

        if (validMatches.containsKey(FilterType.REAL_TIME_STATUS)) {
            Set<Long> staffIds = shiftMongoRepository.getStaffListAsIdForRealtimeCriteria(unitId, (Set<String>) validMatches.get(FilterType.REAL_TIME_STATUS));
            staffListWithPersonalDetails = staffListWithPersonalDetails.stream().filter(spd -> staffIds.contains(spd.getId())).collect(Collectors.toList());
        }

        int i = -1;
        StaffShiftDetails matchedStaff = null;
        for (StaffShiftDetails staffShiftDetails : staffListWithPersonalDetails) {
            i++;
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetails.getUserId())) {
                matchedStaff = staffShiftDetails;
                break;
            }
        }

        if (matchedStaff == null) {
            matchedStaff = staffListWithPersonalDetails.get(0);
        } else {
            staffListWithPersonalDetails.remove(i);
            staffListWithPersonalDetails.add(0, matchedStaff);
        }
        final Set<Long> employmentIds = matchedStaff.getEmployments().stream().map(PlanningEmploymentDTO::getId).collect(Collectors.toSet());
        StaffShiftDetails shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds, shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return staffListWithPersonalDetails;
    }



    private StaffShiftDetails findShiftsForSelectedEmploymentsAndDuration(Set<Long> employmentIds, ShiftFilterDurationType shiftFilterDurationType) {

        LOGGER.debug("employment ids are {}", employmentIds);
        List<Date> startAndEndDates = getStartAndEndDates(shiftFilterDurationType);
        Date fromDate = startAndEndDates.get(0);
        Date toDate = startAndEndDates.get(1);
        LOGGER.debug("fetching shifts between start {} and end {} date", fromDate, toDate);
        return shiftMongoRepository.getAllShiftsForOneStaffWithEmploymentsAndBetweenDuration(employmentIds, fromDate, toDate);
    }

    private List<Date> getStartAndEndDates(ShiftFilterDurationType shiftFilterDurationType) {

        List<Date> startAndEndDates = new ArrayList<>(2);

        LocalDate startDate;
        LocalDate endDate;

        switch (shiftFilterDurationType) {

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
        LOGGER.debug(" searching between dates start {} and end {} for filter type {}", startDate, endDate, shiftFilterDurationType.getValue());
        startAndEndDates.add(fromDate);
        startAndEndDates.add(toDate);
        return startAndEndDates;
    }

    public List<StaffShiftDetails> getAllStaffEligibleForPlanning(Long unitId, ShiftSearchDTO shiftSearchDTO) {
        return userIntegrationService.getAllPlanningStaffForUnit(unitId, shiftSearchDTO);
    }

    private List<StaffShiftDetails> assignShiftsToStaff(List<StaffShiftDetails> staffShiftPersonalDetailsList, List<StaffShiftDetails> shiftData) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = shiftData.stream().collect(Collectors.toMap(StaffShiftDetails::getId, StaffShiftDetails::getShifts));
        for (StaffShiftDetails staffShiftDetails : staffShiftPersonalDetailsList) {
            staffShiftDetails.setShifts(shiftsMap.getOrDefault(staffShiftDetails.getId(), new ArrayList<>()));
        }
        return staffShiftPersonalDetailsList;
    }


    private List<StaffShiftDetails> getStaffListAfterShiftFilterMatches(List<StaffShiftDetails> staffShiftPersonalDetailsList, List<StaffShiftDetails> shiftData,final Long loggedInUserId,ShiftSearchDTO shiftSearchDTO,long shiftFilterCount) {
        boolean staffToAdd = false;
        StaffShiftDetails loggedInStaff = null;
        if (staffShiftPersonalDetailsList.get(0).getUserId().equals(loggedInUserId)) {
            staffToAdd = true;
            loggedInStaff = staffShiftPersonalDetailsList.get(0);
        }

        if (CollectionUtils.isNotEmpty(shiftData)||(isCollectionNotEmpty(shiftSearchDTO.getFiltersData())&&shiftFilterCount>0)) {
            Set<Long> filteredShiftStaff = shiftData.stream().map(StaffShiftDetails::getId).collect(Collectors.toSet());
            staffShiftPersonalDetailsList = staffShiftPersonalDetailsList.stream().filter(spl -> filteredShiftStaff.contains(spl.getId())).collect(Collectors.toList());
        }


        if (CollectionUtils.isEmpty(staffShiftPersonalDetailsList)) {
            staffShiftPersonalDetailsList.add(loggedInStaff);
        } else if (CollectionUtils.isNotEmpty(staffShiftPersonalDetailsList) && staffToAdd && !staffShiftPersonalDetailsList.get(0).getUserId().equals(loggedInUserId)) {
            staffShiftPersonalDetailsList.add(0, loggedInStaff);
        }
        return staffShiftPersonalDetailsList;
    }
    public List<EmploymentTypeDTO> getEmploymentTypes(Long unitId){
       List<EmploymentTypeDTO> employmentTypeDTOList = userIntegrationService.getEmploymentTypeList(unitId);
       return employmentTypeDTOList;
    }

    public Set<Long> getStaffListAsId(final Long unitId, final Set<String> statuses) {
        return shiftMongoRepository.getStaffListAsIdForRealtimeCriteria(unitId, statuses);
    }

    private <T> Set<Long>  filterStaffByCTATemplateAccountType(List<StaffShiftDetails> staffShiftDetails, Set<Long> staffIds, Map<FilterType, Set<T>> filterTypeMap) {
        Set<Long> filteredStaffIds = staffIds;
        Map<Long,List<PlanningEmploymentDTO>> staffEmploymentMap =staffShiftDetails.stream().collect(Collectors.toMap(staffShiftDetails1 -> staffShiftDetails1.getId(),staffShiftDetails1 -> staffShiftDetails1.getEmployments()));
        Map<Long,List<Long>> staffEmplymentIdMap =getStaffEmploymentMap(staffEmploymentMap);
        if(filterTypeMap.containsKey(CTA_ACCOUNT_TYPE)){
            List<CTAResponseDTO> allCTAs = costTimeAgreementRepository.getParentCTAByUpIds(staffEmplymentIdMap.values().stream().flatMap(longs -> longs.stream()).filter(longs -> isNotNull(longs)).collect(Collectors.toList()));
            Map<Long,List<CTAResponseDTO>>  ctagroup = allCTAs.stream().collect(Collectors.groupingBy(ctaResponseDTO -> ctaResponseDTO.getEmploymentId(),Collectors.toList()));
            Set<Long> staffFilterDTOList = new HashSet<>();
            for(Long staffId:staffIds) {
                List<Long> employmentIDs=staffEmplymentIdMap.get(staffId);
                for(Long employmentID:employmentIDs) {
                    List<CTAResponseDTO> CTAs=ctagroup.getOrDefault(employmentID,new ArrayList<>());
                    for(CTAResponseDTO ctaResponseDTO:CTAs) {
                        for(CTARuleTemplateDTO CTARule:ctaResponseDTO.getRuleTemplates()) {
                            if(filterTypeMap.get(CTA_ACCOUNT_TYPE).contains(CTARule.getPlannedTimeWithFactor().getAccountType().toString())){
                                staffFilterDTOList.add(staffId);
                            }

                        }
                    }
                }
            }
            filteredStaffIds = staffFilterDTOList;

        }
        return filteredStaffIds;
    }

    private Map<Long,List<Long>> getStaffEmploymentMap(Map<Long,List<PlanningEmploymentDTO>> staffEmploymentMap){
        Map<Long,List<Long>> staffIdAndEmploymentIdsMap = new HashMap<>();
        List<Long> employmentIds ;
        for(Map.Entry<Long,List<PlanningEmploymentDTO>> entry :staffEmploymentMap.entrySet()){
            employmentIds =entry.getValue().stream().map(v-> v.getId()).collect(Collectors.toList());
            staffIdAndEmploymentIdsMap.put(entry.getKey(),employmentIds);
         }
       return staffIdAndEmploymentIdsMap;
    }




    }
