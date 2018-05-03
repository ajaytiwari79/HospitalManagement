package com.kairos.activity.service.pay_out;

import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.persistence.model.pay_out.DailyPOEntry;
import com.kairos.activity.persistence.model.pay_out.PayOutCTADistribution;
import com.kairos.activity.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.pay_out.CTARuleTemplateCalulatedPayOutDTO;
import com.kairos.activity.response.dto.pay_out.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.response.dto.time_bank.CTAIntervalDTO;
import com.kairos.activity.response.dto.time_bank.CTARuleTemplateCalulatedTimeBankDTO;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.activity.constants.AppConstants.PAIDOUT_ACCOUNT;

@Transactional
@Service
public class PayOutCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(PayOutCalculationService.class);

    @Inject
    private TimeBankCalculationService timeBankCalculationService;


    public DailyPOEntry getTimeBankByInterval(UnitPositionWithCtaDetailsDTO ctaDto, Interval interval, List<ShiftQueryResultWithActivity> shifts, DailyPOEntry dailyPOEntry) {
        if (shifts != null && !shifts.isEmpty()) {
            calculateDailyTimebank(interval, ctaDto, shifts, dailyPOEntry);
        } else {
            int dailyContractualMinutes = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? -ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek() : 0;
            dailyPOEntry.setTotalPOMinutes(dailyContractualMinutes != 0 ? -dailyContractualMinutes : 0);
            dailyPOEntry.setContractualMinutes(dailyContractualMinutes);
            dailyPOEntry.setScheduledMinutes(0);
            dailyPOEntry.setPoMinutesWithoutCta(0);
            dailyPOEntry.setPoMinutesWithCta(0);
            dailyPOEntry.setPoCtaDistibList(getDistribution(ctaDto));
        }
        return dailyPOEntry;
    }

    public List<PayOutCTADistribution> getDistribution(UnitPositionWithCtaDetailsDTO ctaDto) {
        List<PayOutCTADistribution> poCTADistributions = new ArrayList<>(ctaDto.getCtaRuleTemplates().size());
        ctaDto.getCtaRuleTemplates().forEach(rt -> {
            poCTADistributions.add(new PayOutCTADistribution(rt.getName(), 0, rt.getId()));
        });
        return poCTADistributions;
    }

    public DailyPOEntry calculateDailyTimebank(Interval interval, UnitPositionWithCtaDetailsDTO ctaDto, List<ShiftQueryResultWithActivity> shifts, DailyPOEntry dailyPOEntry) {
        int totalPOMinutes = 0;
        int dailyScheduledMinuntes = 0;
        int poMinWithoutCta = 0;
        int contractualMinutes = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? ctaDto.getContractedMinByWeek() / ctaDto.getWorkingDaysPerWeek() : 0;
        Map<Long, Integer> ctaTimeBankMinMap = new HashMap<>();
        for (ShiftQueryResultWithActivity shift : shifts) {
            Interval shiftInterval = new Interval(new DateTime(shift.getStartDate().getTime()).withZone(ctaDto.getUnitDateTimeZone()), new DateTime(shift.getEndDate().getTime()).withZone(ctaDto.getUnitDateTimeZone()));
            shiftInterval = interval.overlap(shiftInterval);
            totalPOMinutes += dailyScheduledMinuntes;
            for (CTARuleTemplateCalulatedPayOutDTO ruleTemplate : ctaDto.getCtaRuleTemplates()) {
                if(ruleTemplate.getAccountType()==null) continue;
                if(ruleTemplate.getAccountType().equals(PAIDOUT_ACCOUNT)){
                    int ctaTimeBankMin = 0;
                    if ((ruleTemplate.getActivityIds().contains(shift.getActivity().getId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()))) && ((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(DateUtils.toLocalDate(shiftInterval.getStart()))))) {
                        if(ruleTemplate.isCalculateScheduledHours()) {
                            dailyScheduledMinuntes += shift.getScheduledMinutes();
                        }else {
                            for (CTAIntervalDTO ctaIntervalDTO : ruleTemplate.getCtaIntervalDTOS()) {
                                Interval ctaInterval = timeBankCalculationService.getCTAInterval(ctaIntervalDTO,interval);
                                if (ctaInterval.overlaps(shiftInterval)) {
                                    int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).toDuration().getStandardMinutes();
                                    if (ctaIntervalDTO.getCompensationType().equals(AppConstants.MINUTES)) {
                                        ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * ctaIntervalDTO.getCompensationValue();
                                        totalPOMinutes += ctaTimeBankMin;
                                        break;
                                    } else if (ctaIntervalDTO.getCompensationType().equals(AppConstants.PERCENT)) {
                                        ctaTimeBankMin += (int)(((double)Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity())/100) * ctaIntervalDTO.getCompensationValue());
                                        totalPOMinutes += ctaTimeBankMin;
                                        break;
                                    }

                                }
                            }
                        }
                    }
                    ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId()) ? ctaTimeBankMinMap.get(ruleTemplate.getId()) + ctaTimeBankMin : ctaTimeBankMin);
                }
            }
        }
        totalPOMinutes = interval.getStart().getDayOfWeek() <= ctaDto.getWorkingDaysPerWeek() ? totalPOMinutes - contractualMinutes : totalPOMinutes;
        poMinWithoutCta = dailyScheduledMinuntes-contractualMinutes;
        dailyPOEntry.setPoMinutesWithoutCta(poMinWithoutCta);
        dailyPOEntry.setPoMinutesWithCta(totalPOMinutes - poMinWithoutCta);
        dailyPOEntry.setContractualMinutes(contractualMinutes);
        dailyPOEntry.setScheduledMinutes(dailyScheduledMinuntes);
        dailyPOEntry.setTotalPOMinutes(totalPOMinutes);
        dailyPOEntry.setPoCtaDistibList(getBlankPayOutDistribution(ctaDto.getCtaRuleTemplates(), ctaTimeBankMinMap));
        return dailyPOEntry;
    }

    private List<PayOutCTADistribution> getBlankPayOutDistribution(List<CTARuleTemplateCalulatedPayOutDTO> ctaRuleTemplateCalulatedTimeBankDTOS, Map<Long, Integer> ctaTimeBankMinMap) {
        List<PayOutCTADistribution> PayOutCTADistributions = new ArrayList<>(ctaRuleTemplateCalulatedTimeBankDTOS.size());
        for (CTARuleTemplateCalulatedPayOutDTO ruleTemplate : ctaRuleTemplateCalulatedTimeBankDTOS) {
            PayOutCTADistributions.add(new PayOutCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId())?ctaTimeBankMinMap.get(ruleTemplate.getId()):0, ruleTemplate.getId()));
        }
        return PayOutCTADistributions;
    }



}
