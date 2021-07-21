package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.EmploymentType;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.Day;
import com.kairos.persistence.model.CalculatePlannedHoursAndScheduledHours;
import com.kairos.persistence.repository.counter.CounterHelperRepository;
import com.kairos.utils.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;

@Service
public class CostCalculationKPIService {

    @Inject
    private TimeBankService timeBankService;
    @Inject private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject private CounterHelperRepository counterHelperRepository;

  public double calculateTotalCostOfStaff(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo){
      StaffKpiFilterDTO staffKpiFilterDTO = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
      Map<Long,EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOMap = staffKpiFilterDTO.getEmployment().stream().collect(Collectors.toMap(EmploymentWithCtaDetailsDTO::getId, v->v));
      Map<BigInteger, List<Day>> daytypesMap = staffKpiFilterDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, DayTypeDTO::getValidDays));
      Map<BigInteger, DayTypeDTO> dayTypeDTOMap = staffKpiFilterDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, v->v));
      BigDecimal totalCost=new BigDecimal(0);
      int totalCtaBonus=0;
      List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId,dateTimeInterval,true);
      KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
      KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,true).invoke();
      List<ShiftActivityDTO> shiftActivityDTOS = filterShiftActivity.getShiftActivityDTOS();
      for (ShiftActivityDTO shiftActivityDTO : shiftActivityDTOS) {
          EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO=employmentWithCtaDetailsDTOMap.get(shiftActivityDTO.getEmploymentId());
          CTAResponseDTO ctaResponseDTO = counterHelperRepository.getCTAByEmploymentIdAndDate(shiftActivityDTO.getEmploymentId(), DateUtils.asDate(shiftActivityDTO.getStartLocalDate()));
          for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
              if (CalculationFor.BONUS_HOURS.equals(ruleTemplate.getCalculationFor())) {
                  TimeBankService.updateDayTypeDetailInCTARuletemplate(daytypesMap, ruleTemplate);
                  boolean valid = timeBankService.validateCTARuleTemplate(ruleTemplate, new StaffEmploymentDetails(new EmploymentType(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentTypeId())), shiftActivityDTO.getPhaseId(), ObjectUtils.newHashSet(shiftActivityDTO.getActivityId()), ObjectUtils.newHashSet(shiftActivityDTO.getActivity().getActivityBalanceSettings().getTimeTypeId()), shiftActivityDTO.getPlannedTimes());
                  boolean dayTypeValid = timeBankService.isDayTypeValid(shiftActivityDTO.getStartDate(), ruleTemplate, dayTypeDTOMap);
                  if (valid && dayTypeValid) {
                      totalCtaBonus += new CalculatePlannedHoursAndScheduledHours(timeBankService,new HashMap<>(),null).getAndUpdateCtaBonusMinutes(dateTimeInterval, ruleTemplate, shiftActivityDTO, new StaffEmploymentDetails(employmentWithCtaDetailsDTO.getStaffId(), employmentWithCtaDetailsDTO.getCtaRuleTemplates(), BigDecimal.valueOf(employmentWithCtaDetailsDTO.getHourlyCost()), employmentWithCtaDetailsDTO.getEmploymentLines()),dayTypeDTOMap).intValue();
                  }
              }
          }
          totalCost= timeBankService.getCostByByMinutes(employmentWithCtaDetailsDTO.getEmploymentLines(),totalCtaBonus,shiftActivityDTO.getStartLocalDate());
      }

   return KPIUtils.getValueWithDecimalFormat(totalCost.doubleValue());
  }

}

