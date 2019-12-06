package com.kairos.service.employment;

import com.kairos.commons.utils.ArrayUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.user.employment.EmploymentFunctionRelationship;
import com.kairos.persistence.model.user.employment.EmploymentFunctionRelationshipQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLineFunctionQueryResult;
import com.kairos.persistence.repository.user.employment.EmploymentFunctionRelationshipRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.staff.StaffRetrievalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_ORGANIZATION_ID_NOTFOUND;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/

@Transactional
@Service
public class EmploymentFunctionService {

    @Inject
    private EmploymentFunctionRelationshipRepository employmentFunctionRelationshipRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;

    public Boolean applyFunction(Long employmentId, Map<String, Long> payload, Long unitId) {

        String dateAsString = new ArrayList<>(payload.keySet()).get(0);

        Long functionId = payload.get(dateAsString);
        employmentFunctionRelationshipRepository.removeDateFromEmploymentFunctionRelationship(employmentId, DateUtils.asLocalDate(dateAsString).toString());
        employmentFunctionRelationshipRepository.createEmploymentFunctionRelationship(employmentId, functionId, Collections.singletonList(dateAsString));
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentIdAndStaffId(DateUtils.asLocalDate(dateAsString), employmentGraphRepository.getStaffIdFromEmployment(employmentId), employmentId, unitId, Collections.emptySet());
        activityIntegrationService.updateTimeBank(employmentId, DateUtils.asLocalDate(dateAsString), staffAdditionalInfoDTO);

        return true;
    }

    public Long removeFunction(Long unitId, Long employmentId, Date appliedDate) {
        Long functionId = employmentFunctionRelationshipRepository.removeDateFromEmploymentFunctionRelationship(employmentId, DateUtils.asLocalDate(appliedDate).toString());
        Long staffId = employmentGraphRepository.getStaffIdFromEmployment(employmentId);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentIdAndStaffId(DateUtils.asLocalDate(appliedDate), staffId, employmentId, unitId, Collections.emptySet());
        activityIntegrationService.updateTimeBank(employmentId, DateUtils.asLocalDate(appliedDate), staffAdditionalInfoDTO);
        return functionId;
    }

    public Long removeFunctionOnDeleteShift(Long employmentId, Date appliedDate) {
        return employmentFunctionRelationshipRepository.removeDateFromEmploymentFunctionRelationship(employmentId, DateUtils.asLocalDate(appliedDate).toString());
    }


    /**
     * @param employmentId
     * @param appliedDates
     * @return
     * @Desc this method will remove applied functions for multiple dates
     */
    public Map<LocalDate, Long> removeFunctions(Long employmentId, Set<LocalDate> appliedDates) {
        Map<LocalDate, Long> localDateAndFunctionIdMap = new HashMap<>();
        List<EmploymentFunctionRelationship> employmentFunctionRelationships = new ArrayList<>();
        Set<String> localDatesAsString = (Set<String>) ObjectMapperUtils.copyPropertiesOfCollectionByMapper(appliedDates, String.class);
        List<EmploymentFunctionRelationshipQueryResult> employmentFunctionRelationshipQueryResults = employmentFunctionRelationshipRepository.findAllByAppliedDatesIn(employmentId, localDatesAsString);
        for (EmploymentFunctionRelationshipQueryResult employmentFunctionRelationshipQueryResult : employmentFunctionRelationshipQueryResults) {
            Set<LocalDate> dateToRemove = ArrayUtil.getIntersectedDates(employmentFunctionRelationshipQueryResult.getAppliedDates(), appliedDates);
            employmentFunctionRelationshipQueryResult.getAppliedDates().removeAll(dateToRemove);
            employmentFunctionRelationships.add(new EmploymentFunctionRelationship(employmentFunctionRelationshipQueryResult.getId(), employmentFunctionRelationshipQueryResult.getEmployment(), employmentFunctionRelationshipQueryResult.getFunction(), employmentFunctionRelationshipQueryResult.getAppliedDates()));
            for (LocalDate localDate : dateToRemove) {
                localDateAndFunctionIdMap.put(localDate, employmentFunctionRelationshipQueryResult.getFunction().getId());
            }

        }
        employmentFunctionRelationshipRepository.saveAll(employmentFunctionRelationships);
        return localDateAndFunctionIdMap;
    }

    public Boolean restoreFunctions(Long employmentId, Map<Long, Set<LocalDate>> payload) {
        List<EmploymentFunctionRelationshipQueryResult> employmentFunctionRelationshipQueryResults = employmentFunctionRelationshipRepository.findAllByFunctionIdAndEmploymentId(employmentId, payload.keySet());
        List<EmploymentFunctionRelationship> employmentFunctionRelationships = new ArrayList<>();

        for (EmploymentFunctionRelationshipQueryResult current : employmentFunctionRelationshipQueryResults) {
            if (payload.get(current.getFunction().getId()) != null) {
                current.getAppliedDates().addAll(payload.get(current.getFunction().getId()));
                employmentFunctionRelationships.add(new EmploymentFunctionRelationship(current.getId(), current.getEmployment(), current.getFunction(), current.getAppliedDates()));
            }
        }
        employmentFunctionRelationshipRepository.saveAll(employmentFunctionRelationships);
        return true;
    }

    /**
     * PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE=7.4
     *
     * @param unitId
     * @param staffId
     * @param employmentId
     * @return
     */
    public List<EmploymentLineFunctionQueryResult> getEmploymentLinesWithHourlyCost(Long unitId, Long staffId, Long employmentId) {
        String inValidField = employmentGraphRepository.validateOrganizationStaffEmployment(unitId, staffId, employmentId);
        if (ORGANIZATION.equals(inValidField)) {
            exceptionService.unitNotFoundException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        } else if (STAFF.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "Staff", staffId);
        } else if (EMPLOYMENT.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "Employment", employmentId);
        } else if (EMPLOYMENT_ORGANIZATION_RELATIONSHIP.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "employmentOrgRel");
        } else if (EMPLOYMENT_STAFF_RELATIONSHIP.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "employmentStaffRel");
        }
        List<EmploymentLineFunctionQueryResult> hourlyCostByEmploymentLines = employmentGraphRepository.getFunctionalHourlyCostByEmploymentId(unitId, employmentId);
        BigDecimal leapYearConst = PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE.multiply(new BigDecimal(LEAP_YEAR));
        BigDecimal nonLeapYearConst = PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE.multiply(new BigDecimal(NON_LEAP_YEAR));
        hourlyCostByEmploymentLines = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(hourlyCostByEmploymentLines, EmploymentLineFunctionQueryResult.class);
        for (EmploymentLineFunctionQueryResult employmentLineFunctionQueryResult : hourlyCostByEmploymentLines) {
            BigDecimal hourlyCostCalculationFactor = employmentLineFunctionQueryResult.getStartDate().isLeapYear() ? leapYearConst : nonLeapYearConst;
            employmentLineFunctionQueryResult.setBasePayGradeAmount(employmentLineFunctionQueryResult.getBasePayGradeAmount().divide(hourlyCostCalculationFactor, 2, RoundingMode.CEILING));
            employmentLineFunctionQueryResult.setHourlyCost(employmentLineFunctionQueryResult.getHourlyCost().divide(hourlyCostCalculationFactor, 2, RoundingMode.CEILING));
            List<FunctionDTO> functionList = employmentLineFunctionQueryResult.getFunctions();
            functionList = functionList.stream().filter(functionDTO -> functionDTO.getAmount() != null).collect(Collectors.toList());
            for (FunctionDTO functionDTO : functionList) {
                functionDTO.setAmount(functionDTO.getAmount().divide(hourlyCostCalculationFactor, 2, RoundingMode.CEILING));
            }
            employmentLineFunctionQueryResult.setFunctions(functionList);
        }
        return hourlyCostByEmploymentLines;
    }
}
