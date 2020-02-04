package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
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
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.utils.worktimeagreement.RuletemplateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

@Service
public class CostCalculationKPIService {

    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private KPIBuilderCalculationService kpiBuilderCalculationService;

  public double calculateTotalCostOfStaff(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
      StaffKpiFilterDTO staffKpiFilterDTO = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
      Map<Long,EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOMap = staffKpiFilterDTO.getEmployment().stream().collect(Collectors.toMap(EmploymentWithCtaDetailsDTO::getId, v->v));
      Map<Long, List<Day>> daytypesMap = staffKpiFilterDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, DayTypeDTO::getValidDays));
      Map<Long, DayTypeDTO> dayTypeDTOMap = staffKpiFilterDTO.getDayTypeDTOS().stream().collect(Collectors.toMap(DayTypeDTO::getId, v->v));
      BigDecimal totalCost=new BigDecimal(0);
      int totalCtaBonus=0;
      List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId,dateTimeInterval,true);
      KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
      KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,true).invoke();
      List<ShiftActivityDTO> shiftActivityDTOS = filterShiftActivity.getShiftActivityDTOS();
      for (ShiftActivityDTO shiftActivityDTO : shiftActivityDTOS) {
          EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO=employmentWithCtaDetailsDTOMap.get(shiftActivityDTO.getEmploymentId());
          CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(shiftActivityDTO.getEmploymentId(), asDate(shiftActivityDTO.getStartLocalDate()));
          for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
              if (CalculationFor.BONUS_HOURS.equals(ruleTemplate.getCalculationFor())) {
                  RuletemplateUtils.updateDayTypeDetailInCTARuletemplate(daytypesMap, ruleTemplate);
                  boolean valid = timeBankCalculationService.validateCTARuleTemplate(dayTypeDTOMap, ruleTemplate, new StaffEmploymentDetails(new EmploymentType(employmentWithCtaDetailsDTO.getEmploymentLines().get(0).getEmploymentTypeId())), shiftActivityDTO.getPhaseId(), shiftActivityDTO.getActivityId(), shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId(), shiftActivityDTO.getStartDate(), shiftActivityDTO.getPlannedTimes());
                  if (valid) {
                      totalCtaBonus += timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().getAndUpdateCtaBonusMinutes(dateTimeInterval, ruleTemplate, shiftActivityDTO, new StaffEmploymentDetails(employmentWithCtaDetailsDTO.getStaffId(), employmentWithCtaDetailsDTO.getCtaRuleTemplates(), BigDecimal.valueOf(employmentWithCtaDetailsDTO.getHourlyCost()), employmentWithCtaDetailsDTO.getEmploymentLines())).intValue();
                  }
              }
          }
          totalCost=timeBankCalculationService.getCostByByMinutes(employmentWithCtaDetailsDTO.getEmploymentLines(),totalCtaBonus,shiftActivityDTO.getStartLocalDate());
      }

   return getValueWithDecimalFormat(totalCost.doubleValue());
  }

}

