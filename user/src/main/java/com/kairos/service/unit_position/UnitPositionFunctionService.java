package com.kairos.service.unit_position;

import com.kairos.commons.utils.ArrayUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.user.unit_position.UnitPositionFunctionRelationship;
import com.kairos.persistence.model.user.unit_position.UnitPositionFunctionRelationshipQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionLineFunctionQueryResult;
import com.kairos.persistence.repository.user.unit_position.UnitPositionFunctionRelationshipRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
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
import static com.kairos.constants.AppConstants.NON_LEAP_YEAR;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/

@Transactional
@Service
public class UnitPositionFunctionService {

    @Inject private UnitPositionFunctionRelationshipRepository unitPositionFunctionRelationshipRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private StaffRetrievalService staffRetrievalService;
    @Inject private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject private ActivityIntegrationService activityIntegrationService;

    public Boolean applyFunction(Long unitPositionId, Map<String, Object> payload, Long unitId) {

        String dateAsString = new ArrayList<>(payload.keySet()).get(0);

        Map<String, Object> functionMap = (Map<String, Object>) payload.get(dateAsString);
        Long functionId = new Long((Integer) functionMap.get("id"));

        Boolean unitPositionFunctionRelationship = unitPositionFunctionRelationshipRepository.getUnitPositionFunctionRelationshipByUnitPositionAndFunction(unitPositionId,  dateAsString);

        if (unitPositionFunctionRelationship == null) {
            unitPositionFunctionRelationshipRepository.createUnitPositionFunctionRelationship(unitPositionId, functionId, Collections.singletonList(dateAsString));
        } else if (unitPositionFunctionRelationship) {
            exceptionService.actionNotPermittedException("message.unitposition.function.alreadyApplied", dateAsString);
        }

        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByUnitPositionIdAndStaffId(DateUtils.asLocalDate(dateAsString), unitPositionGraphRepository.getStaffIdFromUnitPosition(unitPositionId), unitPositionId, unitId, ORGANIZATION,Collections.emptySet());
        activityIntegrationService.updateTimeBank(unitPositionId, DateUtils.asLocalDate(dateAsString), staffAdditionalInfoDTO);

        return true;
    }

    public Long removeFunction(Long unitId,Long unitPositionId, Date appliedDate) {
        Long functionId = unitPositionFunctionRelationshipRepository.removeDateFromUnitPositionFunctionRelationship(unitPositionId, DateUtils.asLocalDate(appliedDate).toString());
        Long staffId = unitPositionGraphRepository.getStaffIdFromUnitPosition(unitPositionId);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByUnitPositionIdAndStaffId(DateUtils.asLocalDate(appliedDate),staffId , unitPositionId, unitId, ORGANIZATION,Collections.emptySet());
        activityIntegrationService.updateTimeBank(unitPositionId, DateUtils.asLocalDate(appliedDate), staffAdditionalInfoDTO);
        return functionId;
    }

    public Long removeFunctionOnDeleteShift(Long unitPositionId, Date appliedDate) {
        Long functionId = unitPositionFunctionRelationshipRepository.removeDateFromUnitPositionFunctionRelationship(unitPositionId, DateUtils.asLocalDate(appliedDate).toString());
        return functionId;
    }


    /**
     * @param unitPositionId
     * @param appliedDates
     * @return
     * @Desc this method will remove applied functions for multiple dates
     */
    public Map<LocalDate, Long> removeFunctions(Long unitPositionId, Set<LocalDate> appliedDates) {
        Map<LocalDate, Long> localDateAndFunctionIdMap = new HashMap<>();
        List<UnitPositionFunctionRelationship> unitPositionFunctionRelationships = new ArrayList<>();
        Set<String> localDatesAsString = ObjectMapperUtils.copyPropertiesOfSetByMapper(appliedDates, String.class);
        List<UnitPositionFunctionRelationshipQueryResult> unitPositionFunctionRelationshipQueryResults = unitPositionFunctionRelationshipRepository.findAllByAppliedDatesIn(unitPositionId, localDatesAsString);
        for (UnitPositionFunctionRelationshipQueryResult unitPositionFunctionRelationshipQueryResult : unitPositionFunctionRelationshipQueryResults) {
            Set<LocalDate> dateToRemove = ArrayUtil.getIntersectedDates(unitPositionFunctionRelationshipQueryResult.getAppliedDates(), appliedDates);
            unitPositionFunctionRelationshipQueryResult.getAppliedDates().removeAll(dateToRemove);
            unitPositionFunctionRelationships.add(new UnitPositionFunctionRelationship(unitPositionFunctionRelationshipQueryResult.getId(), unitPositionFunctionRelationshipQueryResult.getUnitPosition(), unitPositionFunctionRelationshipQueryResult.getFunction(), unitPositionFunctionRelationshipQueryResult.getAppliedDates()));
            for (LocalDate localDate : dateToRemove) {
                localDateAndFunctionIdMap.put(localDate, unitPositionFunctionRelationshipQueryResult.getFunction().getId());
            }

        }
        unitPositionFunctionRelationshipRepository.saveAll(unitPositionFunctionRelationships);
        return localDateAndFunctionIdMap;
    }
    public Boolean restoreFunctions(Long unitPositionId, Map<Long, Set<LocalDate>> payload) {
        List<UnitPositionFunctionRelationshipQueryResult> unitPositionFunctionRelationshipQueryResults = unitPositionFunctionRelationshipRepository.findAllByFunctionIdAndUnitPositionId(unitPositionId, payload.keySet());
        List<UnitPositionFunctionRelationship> unitPositionFunctionRelationships = new ArrayList<>();

        for (UnitPositionFunctionRelationshipQueryResult current : unitPositionFunctionRelationshipQueryResults) {
            if (payload.get(current.getFunction().getId()) != null) {
                current.getAppliedDates().addAll(payload.get(current.getFunction().getId()));
                unitPositionFunctionRelationships.add(new UnitPositionFunctionRelationship(current.getId(), current.getUnitPosition(), current.getFunction(), current.getAppliedDates()));
            }
        }
        unitPositionFunctionRelationshipRepository.saveAll(unitPositionFunctionRelationships);
        return true;
    }
    /**
     * PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE=7.4
     *
     * @param unitId
     * @param staffId
     * @param unitPositionId
     * @return
     */
    public List<UnitPositionLineFunctionQueryResult> getPositionLinesWithHourlyCost(Long unitId, Long staffId, Long unitPositionId) {
        String inValidField = unitPositionGraphRepository.validateOrganizationStaffUnitPosition(unitId, staffId, unitPositionId);
        if (ORGANIZATION.equals(inValidField)) {
            exceptionService.unitNotFoundException("message.organization.id.notFound", unitId);
        } else if (STAFF.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Staff", staffId);
        } else if (UNIT_POSITION.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "UnitPosition", unitPositionId);
        } else if (UNIT_POSITION_ORGANIZATION_RELATIONSHIP.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "unitPositionOrgRel");
        } else if (UNIT_POSITION_STAFF_RELATIONSHIP.equals(inValidField)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "unitPositionStaffRel");
        }
        List<UnitPositionLineFunctionQueryResult> hourlyCostByUnitPositionLines = unitPositionGraphRepository.getFunctionalHourlyCostByUnitPositionId(unitId, unitPositionId);
        BigDecimal perDayHourOfFullTimeEmployee=new BigDecimal(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE);
        BigDecimal leapYearConst=perDayHourOfFullTimeEmployee.multiply(new BigDecimal(LEAP_YEAR));
        BigDecimal nonLeapYearConst=perDayHourOfFullTimeEmployee.multiply(new BigDecimal(NON_LEAP_YEAR));
        hourlyCostByUnitPositionLines = ObjectMapperUtils.copyPropertiesOfListByMapper(hourlyCostByUnitPositionLines, UnitPositionLineFunctionQueryResult.class);
        for (UnitPositionLineFunctionQueryResult unitPositionLineFunctionQueryResult : hourlyCostByUnitPositionLines) {
            BigDecimal hourlyCostCalculationFactor = unitPositionLineFunctionQueryResult.getStartDate().isLeapYear() ? leapYearConst : nonLeapYearConst;
            unitPositionLineFunctionQueryResult.setBasePayGradeAmount(new BigDecimal(unitPositionLineFunctionQueryResult.getBasePayGradeAmount()).divide(hourlyCostCalculationFactor,3,RoundingMode.CEILING).floatValue());
            unitPositionLineFunctionQueryResult.setHourlyCost(new BigDecimal(unitPositionLineFunctionQueryResult.getHourlyCost()).divide(hourlyCostCalculationFactor,3,RoundingMode.CEILING).floatValue() );
            List<FunctionDTO> functionList = unitPositionLineFunctionQueryResult.getFunctions();
            functionList = functionList.stream().filter(functionDTO -> functionDTO.getAmount()!=null).collect(Collectors.toList());
            for (FunctionDTO functionDTO : functionList) {
                functionDTO.setAmount(functionDTO.getAmount().divide(hourlyCostCalculationFactor,3, RoundingMode.CEILING));
            }
            unitPositionLineFunctionQueryResult.setFunctions(functionList);
        }
        return hourlyCostByUnitPositionLines;
    }
}
