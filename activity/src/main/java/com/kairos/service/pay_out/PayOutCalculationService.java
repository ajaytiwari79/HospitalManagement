package com.kairos.service.pay_out;

import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.activity.time_bank.CTARuleTemplateDTO;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.model.pay_out.PayOutCTADistribution;
import com.kairos.user.country.agreement.cta.CalculationFor;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.kairos.constants.AppConstants.*;


/*
* Created By Mohit Shakya
*
* */

@Service
public class PayOutCalculationService {




    public PayOut calculateAndUpdatePayOut(DateTimeInterval interval, StaffUnitPositionDetails unitPositionDetails, Shift shift, Activity activity, PayOut payOut) {
        int totalDailyTimebank = 0;
        int dailyScheduledMin = 0;
        int contractualMin = interval.getStart().get(ChronoField.DAY_OF_WEEK) <= unitPositionDetails.getWorkingDaysInWeek() ? unitPositionDetails.getTotalWeeklyMinutes() / unitPositionDetails.getWorkingDaysInWeek() : 0;
        Map<Long, Integer> ctaTimeBankMinMap = new HashMap<>();
        DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        if (interval.overlaps(shiftInterval)) {
            shiftInterval = interval.overlap(shiftInterval);
            for (CTARuleTemplateDTO ruleTemplate : unitPositionDetails.getCtaRuleTemplates()) {
                if (ruleTemplate.getAccountType() == null) continue;
                if (ruleTemplate.getAccountType().equals(PAIDOUT_ACCOUNT)) {
                    int ctaTimeBankMin = 0;
                    boolean activityValid = ruleTemplate.getActivityIds().contains(activity.getId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(activity.getBalanceSettingsActivityTab().getTimeTypeId()));
                    if (activityValid) {
                        boolean ruleTemplateValid = (ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(shiftInterval.getStart()) && (ruleTemplate.getEmploymentTypes() == null || ruleTemplate.getEmploymentTypes().contains(unitPositionDetails.getEmploymentType().getId())));
                        if (ruleTemplateValid) {
                            if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && interval.contains(shift.getStartDate().getTime())) {
                                dailyScheduledMin += shift.getScheduledMinutes();
                                totalDailyTimebank += dailyScheduledMin;
                            }
                            if (ruleTemplate.getCalculationFor().equals(CalculationFor.BONUS_HOURS)) {
                                for (com.kairos.activity.time_bank.CTAIntervalDTO ctaIntervalDTO : ruleTemplate.getCtaIntervalDTOS()) {
                                    DateTimeInterval ctaInterval = getCTAInterval(ctaIntervalDTO, interval.getStart());
                                    if (ctaInterval.overlaps(shiftInterval)) {
                                        int overlapTimeInMin = ctaInterval.overlap(shiftInterval).getMinutes();
                                        if (ctaIntervalDTO.getCompensationType().equals(AppConstants.MINUTES)) {
                                            ctaTimeBankMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * ctaIntervalDTO.getCompensationValue();
                                            totalDailyTimebank += ctaTimeBankMin;
                                            break;
                                        } else if (ctaIntervalDTO.getCompensationType().equals(AppConstants.PERCENT)) {
                                            ctaTimeBankMin += (int) (((double) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) / 100) * ctaIntervalDTO.getCompensationValue());
                                            totalDailyTimebank += ctaTimeBankMin;
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                    }
                    if (!ruleTemplate.isCalculateScheduledHours()) {
                        ctaTimeBankMinMap.put(ruleTemplate.getId(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId()) ? ctaTimeBankMinMap.get(ruleTemplate.getId()) + ctaTimeBankMin : ctaTimeBankMin);
                    }
                }
            }
        }
        totalDailyTimebank = interval.getStart().get(ChronoField.DAY_OF_WEEK) <= unitPositionDetails.getWorkingDaysInWeek() ? totalDailyTimebank - contractualMin : totalDailyTimebank;
        int timeBankMinWithoutCta = dailyScheduledMin - contractualMin;
        payOut.setPayOutMinWithoutCta(timeBankMinWithoutCta);
        payOut.setPayOutMinWithCta(ctaTimeBankMinMap.entrySet().stream().mapToInt(c -> c.getValue()).sum());
        payOut.setContractualMin(contractualMin);
        payOut.setScheduledMin(dailyScheduledMin);
        payOut.setTotalPayOutMin(totalDailyTimebank);
        payOut.setPayOutCTADistributionList(getCTADistribution(unitPositionDetails.getCtaRuleTemplates(), ctaTimeBankMinMap));
        return payOut;
    }

    public DateTimeInterval getCTAInterval(com.kairos.activity.time_bank.CTAIntervalDTO ctaIntervalDTO, ZonedDateTime startDate) {
        int ctaStart = ctaIntervalDTO.getStartTime();
        int ctaEnd = ctaIntervalDTO.getStartTime() >= ctaIntervalDTO.getEndTime() ? 1440 + ctaIntervalDTO.getEndTime() : ctaIntervalDTO.getEndTime();
        return new DateTimeInterval(startDate.plusMinutes(ctaStart), startDate.plusMinutes(ctaEnd));
    }

    private List<PayOutCTADistribution> getCTADistribution(List<CTARuleTemplateDTO> ctaRuleTemplateCalulatedTimeBankDTOS, Map<Long, Integer> ctaTimeBankMinMap) {
        List<PayOutCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalulatedTimeBankDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateCalulatedTimeBankDTOS) {
            if (!ruleTemplate.isCalculateScheduledHours()) {
                timeBankCTADistributions.add(new PayOutCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.containsKey(ruleTemplate.getId()) ? ctaTimeBankMinMap.get(ruleTemplate.getId()) : 0, ruleTemplate.getId()));
            }
        }
        return timeBankCTADistributions;
    }


}
