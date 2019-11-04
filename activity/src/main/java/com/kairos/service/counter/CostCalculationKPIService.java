package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.service.time_bank.TimeBankCalculationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

@Service
public class CostCalculationKPIService {

    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;

  public double calculateTotalCostOfStaff(StaffKpiFilterDTO staffKpiFilterDTO, List<ShiftActivityDTO> shiftActivityDTOS, DateTimeInterval dateTimeInterval){
      Map<Long,EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOMap=staffKpiFilterDTO.getEmployment().stream().collect(Collectors.toMap(k->k.getId(),v->v));
      double totalCost=0l;
      double totalCtaBonus=0l;
      for (ShiftActivityDTO shiftActivityDTO : shiftActivityDTOS) {
          EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO=employmentWithCtaDetailsDTOMap.get(shiftActivityDTO.getEmploymentId());
          CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(shiftActivityDTO.getEmploymentId(), asDate(shiftActivityDTO.getStartLocalDate()));
          for (CTARuleTemplateDTO ruleTemplate : ctaResponseDTO.getRuleTemplates()) {
              totalCtaBonus += (double) timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().getAndUpdateCtaBonusMinutes(dateTimeInterval, ruleTemplate, shiftActivityDTO, new StaffEmploymentDetails(employmentWithCtaDetailsDTO.getStaffId(),employmentWithCtaDetailsDTO.getCtaRuleTemplates(), BigDecimal.valueOf(employmentWithCtaDetailsDTO.getHourlyCost()),employmentWithCtaDetailsDTO.getEmploymentLines()));
          }
          totalCost=(totalCtaBonus/60)*timeBankCalculationService.getHourlyCostByDate(employmentWithCtaDetailsDTO.getEmploymentLines(),shiftActivityDTO.getStartLocalDate()).doubleValue();
      }

   return getValueWithDecimalFormat(totalCost);
  }

}

