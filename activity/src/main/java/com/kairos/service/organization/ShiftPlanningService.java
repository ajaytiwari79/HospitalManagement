package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.filter_utils.FilterUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.employment.PlanningEmploymentDTO;
import com.kairos.dto.user.filter.FilteredStaffsAndRequiredDataFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftFilterDurationType;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.shift.StaffShiftDetailsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;

import static com.kairos.commons.utils.ObjectUtils.*;
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
    @Inject
    private NightWorkerMongoRepository nightWorkerMongoRepository;


    public <T> List<StaffShiftDetailsDTO> getShiftPlanningDetailsForUnit(final Long unitId, final ShiftSearchDTO shiftSearchDTO, boolean showAllStaffs) {
        Object[] validFilterObjectsAndExistShiftFilter = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        Map<FilterType, Set<T>> validMatches = (Map<FilterType, Set<T>>)validFilterObjectsAndExistShiftFilter[0];
        List<StaffShiftDetailsDTO> staffListWithPersonalDetails = getFilteredListOfStaffPersonalDetails(unitId, shiftSearchDTO, validMatches,showAllStaffs);
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
        List<StaffShiftDetailsDTO> shiftWithActivityDTOS = shiftMongoRepository.getFilteredShiftsGroupedByStaff(employmentIds, validMatches, unitId, shiftSearchDTO.getStartDate(), shiftSearchDTO.getEndDate(),includeDateComparison);
        return assignShiftsToStaff(staffListWithPersonalDetails, shiftWithActivityDTOS);
    }

    private <T> List<StaffShiftDetailsDTO> getFilteredListOfStaffPersonalDetails(final Long unitId, final ShiftSearchDTO shiftSearchDTO, Map<FilterType, Set<T>> validMatches, boolean showAllStaffs) {
        FilteredStaffsAndRequiredDataFilterDTO filteredStaffsAndRequiredDataFilterDTO = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO,showAllStaffs);
        List<StaffShiftDetailsDTO> staffListWithPersonalDetails = filteredStaffsAndRequiredDataFilterDTO.getStaffShiftDetailsDTOS();
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails);
        if (CollectionUtils.isEmpty(staffListWithPersonalDetails)) {
            return Collections.emptyList();
        }

        StaffShiftDetailsDTO loggedInStaff = null;
        if (staffListWithPersonalDetails.get(0).getUserId().equals(shiftSearchDTO.getLoggedInUserId())) {
            loggedInStaff = staffListWithPersonalDetails.get(0);
        }
        if(validMatches.containsKey(CTA_ACCOUNT_TYPE)){
            Map<Long,Set<Long>> staffEmploymentMap = staffListWithPersonalDetails.stream().collect(Collectors.toMap(staffShiftDetails1 -> staffShiftDetails1.getId(), staffShiftDetails1 -> staffShiftDetails1.getEmployments().stream().map(planningEmploymentDTO -> planningEmploymentDTO.getId()).collect(Collectors.toSet())));
            staffListWithPersonalDetails = filterStaffByCTATemplateAccountType(staffListWithPersonalDetails,validMatches,staffEmploymentMap);

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

    public <T> List<StaffShiftDetailsDTO> getFilteredStaffForMatchingFilter(final Long unitId, final ShiftSearchDTO shiftSearchDTO, boolean showAllStaffs) {
         Object[] validFilterObjectsAndExistShiftFilter = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        Map<FilterType, Set<T>> validFilterMap = (Map<FilterType, Set<T>>)validFilterObjectsAndExistShiftFilter[0];
         boolean anyShiftFilterExists = (boolean)validFilterObjectsAndExistShiftFilter[1];
        LOGGER.debug(" shift filters present are {}", anyShiftFilterExists);
        FilteredStaffsAndRequiredDataFilterDTO filteredStaffsAndRequiredDataFilterDTO = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO,showAllStaffs);
        List<StaffShiftDetailsDTO> staffListWithPersonalDetails = filteredStaffsAndRequiredDataFilterDTO.getStaffShiftDetailsDTOS();
        if (CollectionUtils.isEmpty(staffListWithPersonalDetails)) {
            return Collections.emptyList();
        }
        Optional<StaffShiftDetailsDTO> loggedInStaff = staffListWithPersonalDetails.stream().filter(staffShiftDetailsDTO -> staffShiftDetailsDTO.getUserId().equals(shiftSearchDTO.getLoggedInUserId())).findFirst();
        Map<Long,Set<Long>> staffEmploymentMap = staffListWithPersonalDetails.stream().collect(Collectors.toMap(staffShiftDetails1 -> staffShiftDetails1.getId(), staffShiftDetails1 -> staffShiftDetails1.getEmployments().stream().map(planningEmploymentDTO -> planningEmploymentDTO.getId()).collect(Collectors.toSet())));
        staffListWithPersonalDetails = updateStaffListBasedOnStaffFilter(staffListWithPersonalDetails,unitId,validFilterMap,staffEmploymentMap);
        if (shiftSearchDTO.getStartDate().equals(shiftSearchDTO.getEndDate())) {
            shiftSearchDTO.setEndDate(asDate(DateUtils.asZonedDateTime(shiftSearchDTO.getEndDate()).plusDays(1)));
        }
        boolean includeDateComparison = !shiftSearchDTO.getShiftFilterDurationType().equals(ShiftFilterDurationType.INDIVIDUAL);
        List<StaffShiftDetailsDTO> shiftWithActivityDTOS = Collections.EMPTY_LIST;
        if (anyShiftFilterExists) {
            shiftWithActivityDTOS = shiftMongoRepository.getStaffListFilteredByShiftCriteria(staffEmploymentMap.keySet(), validFilterMap, unitId, shiftSearchDTO.getStartDate(), shiftSearchDTO.getEndDate(), includeDateComparison,filteredStaffsAndRequiredDataFilterDTO.getRequiredDataForFilterDTO());
            Set<Long> filteredShiftStaff = shiftWithActivityDTOS.stream().map(StaffShiftDetailsDTO::getId).collect(Collectors.toSet());
            staffListWithPersonalDetails = staffListWithPersonalDetails.stream().filter(spl -> filteredShiftStaff.contains(spl.getId()) && !spl.getUserId().equals(shiftSearchDTO.getLoggedInUserId())).collect(Collectors.toList());
        }
        if(loggedInStaff.isPresent() && staffListWithPersonalDetails.stream().noneMatch(k->k.getUserId().equals(shiftSearchDTO.getLoggedInUserId()))){
            staffListWithPersonalDetails.add(0,loggedInStaff.get());
        }
        setNightWorkerDetails(staffListWithPersonalDetails);
        return staffListWithPersonalDetails;//getStaffListAfterShiftFilterMatches(staffListWithPersonalDetails, shiftWithActivityDTOS, shiftSearchDTO.getLoggedInUserId(),shiftSearchDTO,anyShiftFilterExists);
    }

    private <T> List<StaffShiftDetailsDTO> updateStaffListBasedOnStaffFilter(List<StaffShiftDetailsDTO> staffListWithPersonalDetails,final Long unitId, Map<FilterType, Set<T>> filterTypeSetMap,Map<Long,Set<Long>> staffEmploymentMap) {
        if(filterTypeSetMap.containsKey(CTA_ACCOUNT_TYPE)){
            staffListWithPersonalDetails = filterStaffByCTATemplateAccountType(staffListWithPersonalDetails,filterTypeSetMap,staffEmploymentMap);
        }
        if (filterTypeSetMap.containsKey(FilterType.REAL_TIME_STATUS)) {
            Set<Long> staffIds = shiftMongoRepository.getStaffListAsIdForRealtimeCriteria(unitId, (Set<String>) filterTypeSetMap.get(FilterType.REAL_TIME_STATUS));
            staffListWithPersonalDetails = staffListWithPersonalDetails.stream().filter(spd -> staffIds.contains(spd.getId())).collect(Collectors.toList());
        }
        return staffListWithPersonalDetails;
    }

    public StaffShiftDetailsDTO getShiftPlanningDetailsForOneStaff(Long unitId, ShiftSearchDTO shiftSearchDTO, boolean showAllStaffs) {
        FilteredStaffsAndRequiredDataFilterDTO filteredStaffsAndRequiredDataFilterDTO = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO,showAllStaffs);
        List<StaffShiftDetailsDTO> staffListWithPersonalDetails = filteredStaffsAndRequiredDataFilterDTO.getStaffShiftDetailsDTOS();
                LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails);
        int i = 0;
        StaffShiftDetailsDTO matchedStaff = null;
        for (StaffShiftDetailsDTO staffShiftDetailsDTO : staffListWithPersonalDetails) {
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetailsDTO.getUserId())) {
                matchedStaff = staffShiftDetailsDTO;
                break;
            }
            i++;
        }
        LOGGER.debug(" staff found at index {}", i);

        staffListWithPersonalDetails.remove(i);
        staffListWithPersonalDetails.add(0, matchedStaff);
        final Set<Long> employmentIds = Objects.requireNonNull(matchedStaff).getEmployments().stream().map(PlanningEmploymentDTO::getId).collect(Collectors.toSet());
        StaffShiftDetailsDTO shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds, shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return matchedStaff;
    }


    public <T> List<StaffShiftDetailsDTO> getUnitPlanningAndShiftForSelectedStaff(Long unitId, ShiftSearchDTO shiftSearchDTO, boolean showAllStaffs) {
        Object[] validFilterObjectsAndExistShiftFilter = FilterUtils.filterOutEmptyQueriesAndPrepareMap(shiftSearchDTO);
        Map<FilterType, Set<T>> validFilterMap = (Map<FilterType, Set<T>>)validFilterObjectsAndExistShiftFilter[0];
        FilteredStaffsAndRequiredDataFilterDTO filteredStaffsAndRequiredDataFilterDTO = getAllStaffEligibleForPlanning(unitId, shiftSearchDTO,showAllStaffs);
        List<StaffShiftDetailsDTO> staffListWithPersonalDetails = filteredStaffsAndRequiredDataFilterDTO.getStaffShiftDetailsDTOS();
        if (CollectionUtils.isEmpty(staffListWithPersonalDetails)) {
            return Collections.emptyList();
        }

        if (validFilterMap.containsKey(FilterType.REAL_TIME_STATUS)) {
            Set<Long> staffIds = shiftMongoRepository.getStaffListAsIdForRealtimeCriteria(unitId, (Set<String>) validFilterMap.get(FilterType.REAL_TIME_STATUS));
            staffListWithPersonalDetails = staffListWithPersonalDetails.stream().filter(spd -> staffIds.contains(spd.getId())).collect(Collectors.toList());
        }

        int i = -1;
        StaffShiftDetailsDTO matchedStaff = null;
        for (StaffShiftDetailsDTO staffShiftDetailsDTO : staffListWithPersonalDetails) {
            i++;
            if (shiftSearchDTO.getLoggedInUserId().equals(staffShiftDetailsDTO.getUserId())) {
                matchedStaff = staffShiftDetailsDTO;
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
        StaffShiftDetailsDTO shiftDetails = findShiftsForSelectedEmploymentsAndDuration(employmentIds, shiftSearchDTO.getShiftFilterDurationType());
        matchedStaff.setShifts(shiftDetails.getShifts());
        return staffListWithPersonalDetails;
    }



    private StaffShiftDetailsDTO findShiftsForSelectedEmploymentsAndDuration(Set<Long> employmentIds, ShiftFilterDurationType shiftFilterDurationType) {

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

        Date fromDate = asDate(startDate);
        Date toDate = asDate(endDate);
        LOGGER.debug(" searching between dates start {} and end {} for filter type {}", startDate, endDate, shiftFilterDurationType.getValue());
        startAndEndDates.add(fromDate);
        startAndEndDates.add(toDate);
        return startAndEndDates;
    }

    public FilteredStaffsAndRequiredDataFilterDTO getAllStaffEligibleForPlanning(Long unitId, ShiftSearchDTO shiftSearchDTO, boolean showAllStaffs) {
        return userIntegrationService.getAllPlanningStaffForUnit(unitId, shiftSearchDTO,showAllStaffs);
    }

    private List<StaffShiftDetailsDTO> assignShiftsToStaff(List<StaffShiftDetailsDTO> staffShiftPersonalDetailsList, List<StaffShiftDetailsDTO> shiftData) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = shiftData.stream().collect(Collectors.toMap(StaffShiftDetailsDTO::getId, StaffShiftDetailsDTO::getShifts));
        for (StaffShiftDetailsDTO staffShiftDetailsDTO : staffShiftPersonalDetailsList) {
            staffShiftDetailsDTO.setShifts(shiftsMap.getOrDefault(staffShiftDetailsDTO.getId(), new ArrayList<>()));
        }
        return staffShiftPersonalDetailsList;
    }


    private List<StaffShiftDetailsDTO> getStaffListAfterShiftFilterMatches(List<StaffShiftDetailsDTO> staffShiftPersonalDetailsList, List<StaffShiftDetailsDTO> shiftData, final Long loggedInUserId, ShiftSearchDTO shiftSearchDTO, boolean anyShiftFilterExists) {
        boolean staffToAdd = false;
        StaffShiftDetailsDTO loggedInStaff = null;
        if (staffShiftPersonalDetailsList.get(0).getUserId().equals(loggedInUserId)) {
            staffToAdd = true;
            loggedInStaff = staffShiftPersonalDetailsList.get(0);
        }
        if (CollectionUtils.isNotEmpty(shiftData)|| (isCollectionNotEmpty(shiftSearchDTO.getFiltersData()) && anyShiftFilterExists)) {
            Set<Long> filteredShiftStaff = shiftData.stream().map(StaffShiftDetailsDTO::getId).collect(Collectors.toSet());
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

    private <T> List<StaffShiftDetailsDTO> filterStaffByCTATemplateAccountType(List<StaffShiftDetailsDTO> staffShiftDetailDTOS, Map<FilterType, Set<T>> filterTypeMap,Map<Long,Set<Long>> staffEmploymentMap) {
        List<StaffShiftDetailsDTO> staffShiftDetailsDTOS = new ArrayList<>();
        if(filterTypeMap.containsKey(CTA_ACCOUNT_TYPE) && isCollectionNotEmpty(filterTypeMap.get(CTA_ACCOUNT_TYPE))){
            Set<Long> employmentIds = staffEmploymentMap.values().stream().flatMap(longs -> longs.stream()).filter(longs -> isNotNull(longs)).collect(Collectors.toSet());
            List<CTAResponseDTO> allCTAs = costTimeAgreementRepository.getEmploymentIdsByAccountTypes(employmentIds,(Set<String>)filterTypeMap.get(CTA_ACCOUNT_TYPE));
            employmentIds = allCTAs.stream().map(ctaResponseDTO -> ctaResponseDTO.getEmploymentId()).collect(Collectors.toSet());
            for (StaffShiftDetailsDTO staffShiftDetailDTO : staffShiftDetailDTOS) {
                if(CollectionUtils.containsAny(employmentIds,staffShiftDetailDTO.getEmploymentIds())){
                    staffShiftDetailsDTOS.add(staffShiftDetailDTO);
                }
            }
        }
        return staffShiftDetailsDTOS;
    }

    private void setNightWorkerDetails(List<StaffShiftDetailsDTO> staffListWithPersonalDetails){
        List<NightWorker> nightWorker = nightWorkerMongoRepository.findByStaffIds(staffListWithPersonalDetails.stream().map(StaffShiftDetailsDTO::getId).collect(Collectors.toSet()));
        Map<Long, Boolean> nightWorkerMap = nightWorker.stream().filter(distinctByKey(NightWorker::getStaffId)).collect(Collectors.toMap(NightWorker::getStaffId, NightWorker::isNightWorker));
        staffListWithPersonalDetails.forEach(k-> k.getEmployments().get(0).setNightWorker(nightWorkerMap.getOrDefault(k.getId(),false)));
    }


}
