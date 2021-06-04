package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.ShiftDataHelper;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static java.util.stream.Collectors.toMap;

@Service
public class AsyncTimeBankCalculationService {

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private TimeBankService timeBankService;

    @Cacheable(value = "getAccumulatedTimebankAndDelta", key = "{#employmentId, #includeActualTimebank}", cacheManager = "cacheManager")
    public <T> T getAccumulatedTimebankAndDelta(Long employmentId, Long unitId, Boolean includeActualTimebank, StaffEmploymentDetails employment, ShiftDataHelper shiftDataHelper) {
        employment = isNotNull(employment) ? employment : userIntegrationService.getEmploymentDetailsOfStaffByEmploymentId(unitId,  employmentId);
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = timeBankService.getEmploymentDetailDTO(employment, unitId);
        T response;
        DateTimeInterval planningPeriodInterval = isNotNull(shiftDataHelper) ? shiftDataHelper.getPlanningPeriodDateTimeInterval() : planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
        LocalDate periodEndDate = planningPeriodInterval.getEndLocalDate();
        Date endDate = asDate(periodEndDate);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(employmentId, endDate);
        LocalDate employmentStartDate = employmentWithCtaDetailsDTO.getStartDate().isAfter(planningPeriodInterval.getStartLocalDate())?employmentWithCtaDetailsDTO.getStartDate():planningPeriodInterval.getStartLocalDate();
        Date startDate  = asDate(employmentStartDate);
        Map[] mapArray = phaseService.getPhasesByDates(unitId, employmentStartDate,periodEndDate,employmentWithCtaDetailsDTO.getUnitTimeZone(),employmentWithCtaDetailsDTO.getEmploymentTypeId());
        shiftDataHelper = ShiftDataHelper.builder().dateAndPhaseDefaultName(mapArray[0]).dateAndPublishPlanningPeriod(mapArray[1]).build();
        Map<java.time.LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(DailyTimeBankEntry::getDate, v -> v));
        response = (T)timeBankCalculationService.calculateActualTimebank(planningPeriodInterval,dateDailyTimeBankEntryMap,employmentWithCtaDetailsDTO,periodEndDate,employmentStartDate,shiftDataHelper);
        if(includeActualTimebank) {
            List<CTARuleTemplateDTO> ruleTemplates = costTimeAgreementService.getCtaRuleTemplatesByEmploymentId(employmentId, startDate, endDate);
            PlanningPeriod firstRequestPhasePlanningPeriodByUnitId = planningPeriodService.findFirstRequestPhasePlanningPeriodByUnitId(unitId);
            java.time.LocalDate firstRequestPhasePlanningPeriodEndDate = isNull(firstRequestPhasePlanningPeriodByUnitId) ? periodEndDate : firstRequestPhasePlanningPeriodByUnitId.getEndDate();
            response = (T)timeBankCalculationService.getAccumulatedTimebankDTO(firstRequestPhasePlanningPeriodEndDate,planningPeriodInterval, dateDailyTimeBankEntryMap, employmentWithCtaDetailsDTO, employmentStartDate, periodEndDate,(Long)response,ruleTemplates,shiftDataHelper);
        }
        return response;
    }




}
