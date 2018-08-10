package com.kairos.service.pay_out;

import com.kairos.activity.pay_out.PayOutCTADistributionDTO;
import com.kairos.activity.pay_out.PayOutDTO;
import com.kairos.activity.pay_out.PayOutIntervalDTO;
import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.activity.time_bank.CTARuleTemplateDTO;
import com.kairos.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.constants.AppConstants;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.model.pay_out.PayOutCTADistribution;
import com.kairos.user.country.agreement.cta.CalculationFor;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;


/*
* Created By Pradeep singh
*
* */

@Component
public class PayOutCalculationService {


    /**
     *
     * @param interval
     * @param unitPositionDetails
     * @param shift
     * @param activity
     * @param payOut
     * @return PayOut
     */
    public PayOut calculateAndUpdatePayOut(DateTimeInterval interval, StaffUnitPositionDetails unitPositionDetails, Shift shift, Activity activity, PayOut payOut) {
        int totalPayOut = 0;
        int scheduledMin = 0;
        int contractualMin = interval.getStart().get(ChronoField.DAY_OF_WEEK) <= unitPositionDetails.getWorkingDaysInWeek() ? unitPositionDetails.getTotalWeeklyMinutes() / unitPositionDetails.getWorkingDaysInWeek() : 0;
        Map<Long, Integer> ctaPayoutMinMap = new HashMap<>();
        DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
        if (interval.overlaps(shiftInterval)) {
            shiftInterval = interval.overlap(shiftInterval);
            for (CTARuleTemplateDTO ruleTemplate : unitPositionDetails.getCtaRuleTemplates()) {
                if (ruleTemplate.getAccountType() == null) continue;
                if (ruleTemplate.getAccountType().equals(PAIDOUT_ACCOUNT)) {
                    int ctaPayOutMin = 0;
                    boolean activityValid = ruleTemplate.getActivityIds().contains(activity.getId()) || (ruleTemplate.getTimeTypeIds() != null && ruleTemplate.getTimeTypeIds().contains(activity.getBalanceSettingsActivityTab().getTimeTypeId()));
                    if (activityValid) {
                        boolean ruleTemplateValid = ((ruleTemplate.getDays() != null && ruleTemplate.getDays().contains(shiftInterval.getStart().getDayOfWeek().getValue())) || (ruleTemplate.getPublicHolidays() != null && ruleTemplate.getPublicHolidays().contains(shiftInterval.getStart()))) && (ruleTemplate.getEmploymentTypes() == null || ruleTemplate.getEmploymentTypes().contains(unitPositionDetails.getEmploymentType().getId()));
                        if (ruleTemplateValid) {
                            if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && interval.contains(shift.getStartDate().getTime())) {
                                scheduledMin += shift.getScheduledMinutes();
                                totalPayOut += scheduledMin;
                            }
                            if (ruleTemplate.getCalculationFor().equals(CalculationFor.BONUS_HOURS)) {
                                for (com.kairos.activity.time_bank.CTAIntervalDTO ctaIntervalDTO : ruleTemplate.getCtaIntervalDTOS()) {
                                    DateTimeInterval ctaInterval = getCTAInterval(ctaIntervalDTO, interval.getStart());
                                    if (ctaInterval.overlaps(shiftInterval)) {
                                        int overlapTimeInMin = ctaInterval.overlap(shiftInterval).getMinutes();
                                        if (ctaIntervalDTO.getCompensationType().equals(AppConstants.MINUTES)) {
                                            ctaPayOutMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) * ctaIntervalDTO.getCompensationValue();
                                            totalPayOut += ctaPayOutMin;
                                            break;
                                        } else if (ctaIntervalDTO.getCompensationType().equals(AppConstants.PERCENT)) {
                                            ctaPayOutMin += (int) (((double) Math.round((double) overlapTimeInMin / ruleTemplate.getGranularity()) / 100) * ctaIntervalDTO.getCompensationValue());
                                            totalPayOut += ctaPayOutMin;
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                    }
                    if (!ruleTemplate.isCalculateScheduledHours()) {
                        ctaPayoutMinMap.put(ruleTemplate.getId(), ctaPayoutMinMap.containsKey(ruleTemplate.getId()) ? ctaPayoutMinMap.get(ruleTemplate.getId()) + ctaPayOutMin : ctaPayOutMin);
                    }
                }
            }
        }
        payOut.setPayOutMinWithoutCta(scheduledMin);
        payOut.setPayOutMinWithCta(ctaPayoutMinMap.entrySet().stream().mapToInt(c -> c.getValue()).sum());
        payOut.setContractualMin(contractualMin);
        payOut.setScheduledMin(scheduledMin);
        payOut.setTotalPayOutMin(totalPayOut);
        payOut.setPayOutCTADistributions(getCTADistribution(unitPositionDetails.getCtaRuleTemplates(), ctaPayoutMinMap));
        return payOut;
    }

    /**
     *
     * @param ctaIntervalDTO
     * @param startDate
     * @return DateTimeInterval
     */
    private DateTimeInterval getCTAInterval(com.kairos.activity.time_bank.CTAIntervalDTO ctaIntervalDTO, ZonedDateTime startDate) {
        int ctaStart = ctaIntervalDTO.getStartTime();
        //totalMin in a day
        int totalMinInADay = 1440;
        int ctaEnd = ctaIntervalDTO.getStartTime() >= ctaIntervalDTO.getEndTime() ? totalMinInADay + ctaIntervalDTO.getEndTime() : ctaIntervalDTO.getEndTime();
        return new DateTimeInterval(startDate.plusMinutes(ctaStart), startDate.plusMinutes(ctaEnd));
    }

    /**
     *
     * @param ctaRuleTemplateCalculatedTimeBankDTOS
     * @param ctaTimeBankMinMap
     * @return List<PayOutCTADistribution>s
     */
    private List<PayOutCTADistribution> getCTADistribution(List<CTARuleTemplateDTO> ctaRuleTemplateCalculatedTimeBankDTOS, Map<Long, Integer> ctaTimeBankMinMap) {
        List<PayOutCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalculatedTimeBankDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateCalculatedTimeBankDTOS) {
            if (!ruleTemplate.isCalculateScheduledHours()) {
                timeBankCTADistributions.add(new PayOutCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(),0), ruleTemplate.getId()));
            }
        }
        return timeBankCTADistributions;
    }


    /**
     *
     * @param intervals
     * @param payOuts
     * @param payoutTransactionAndIntervalMap
     * @param unitPositionWithCtaDetailsDTO
     * @return PayOutDTO
     */
    public PayOutDTO getAdvanceViewPayout(List<Interval> intervals, List<PayOut> payOuts, Map<Interval,List<PayOutTransaction>> payoutTransactionAndIntervalMap,UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO,String query) {
        Map<Interval, List<PayOut>> payoutsIntervalMap = getPayoutIntervalsMap(intervals, payOuts);
       List<PayOutIntervalDTO> payoutIntervalDTOS = getPayoutIntervals(intervals, payoutsIntervalMap,payoutTransactionAndIntervalMap,unitPositionWithCtaDetailsDTO,query);
        long[] payoutCalculatedValue = calculatePayoutForInterval(payoutIntervalDTOS);
        Map<Long,Long> ctaDistributionMap = payoutIntervalDTOS.stream().flatMap(p->p.getPayOutDistribution().getChildren().stream()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(ptb->ptb.getId(),Collectors.summingLong(p->p.getMinutes())));
        List<CTADistributionDTO> ctaDistributionDTOS = getDistributionOfPayout(ctaDistributionMap,unitPositionWithCtaDetailsDTO);
        long payoutChange = payoutCalculatedValue[0];
        long payoutBefore = payoutCalculatedValue[1];
        long payoutAfter = payoutCalculatedValue[2];
        long payoutFromCTA = payoutCalculatedValue[3];
        PayOutCTADistributionDTO payOutCTADistributionDTO = new PayOutCTADistributionDTO(payoutFromCTA,ctaDistributionDTOS);
        return new PayOutDTO(intervals.get(0).getStart().toDate(),intervals.get(intervals.size()-1).getEnd().toDate(),payoutAfter,payoutBefore,payoutChange,payoutIntervalDTOS,payOutCTADistributionDTO);
    }

    /**
     *
     * @param intervals
     * @param payOuts
     * @return Map<Interval, List<PayOut>>
     */
    private Map<Interval, List<PayOut>> getPayoutIntervalsMap(List<Interval> intervals, List<PayOut> payOuts) {
        Map<Interval, List<PayOut>> timeBanksIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> timeBanksIntervalMap.put(i, getPayoutsByInterval(i, payOuts)));
        return timeBanksIntervalMap;
    }


    /**
     *
     * @param interval
     * @param payOuts
     * @return List<PayOut>
     */
    private List<PayOut> getPayoutsByInterval(Interval interval, List<PayOut> payOuts) {
        List<PayOut> payOutList = new ArrayList<>();
        payOuts.forEach(payOut -> {
            if (interval.contains(DateUtils.getDateFromLocalDate(payOut.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(payOut.getDate()))) {
                payOutList.add(payOut);
            }
        });
        return payOutList;
    }


    /**
     *
     * @param intervals
     * @param payoutsIntervalMap
     * @param payoutTransactionAndIntervalMap
     * @param unitPositionWithCtaDetailsDTO
     * @return List<PayOutIntervalDTO>
     */
    private List<PayOutIntervalDTO> getPayoutIntervals(List<Interval> intervals,Map<Interval, List<PayOut>> payoutsIntervalMap,Map<Interval,List<PayOutTransaction>> payoutTransactionAndIntervalMap,UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO,String query) {
        List<PayOutIntervalDTO> payOutIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval:intervals){
            List<PayOut> payOuts = payoutsIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionAndIntervalMap.get(interval);
            Long payoutChange = payOuts.stream().mapToLong(p->p.getTotalPayOutMin()).sum();
            Long approvePayOut = payOutTransactionList.stream().filter(p->p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p->(long)p.getMinutes()).sum();
            payoutChange += approvePayOut;
            Long payoutBefore = payOuts.isEmpty()?0:payOuts.get(0).getPayoutBeforeThisDate();
            Long payoutAfter = payoutBefore+payoutChange;
            Map<Long,Long> ctaDistributionMap = payOuts.stream().flatMap(p->p.getPayOutCTADistributions().stream()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(ptb->ptb.getCtaRuleTemplateId(),Collectors.summingLong(p->p.getMinutes())));
            List<CTADistributionDTO> payOutCTADistributionDTOS = getDistributionOfPayout(ctaDistributionMap,unitPositionWithCtaDetailsDTO);
            Long payoutFromCTA = payOutCTADistributionDTOS.stream().mapToLong(pd->pd.getMinutes()).sum();
            PayOutCTADistributionDTO payOutCTADistributionDTO = new PayOutCTADistributionDTO(payoutFromCTA,payOutCTADistributionDTOS);
            String title = getTitle(query,interval);
            PayOutIntervalDTO payOutIntervalDTO = new PayOutIntervalDTO(interval.getStart().toDate(),interval.getEnd().toDate(),payoutAfter,payoutBefore,payoutChange,payOutCTADistributionDTO, DayOfWeek.of(interval.getStart().getDayOfWeek()),title);
            payOutIntervalDTOS.add(payOutIntervalDTO);
        }
        Collections.reverse(payOutIntervalDTOS);
        return payOutIntervalDTOS;
    }

    private String getTitle(String query, Interval interval) {
        switch (query) {
            case DAILY:
                return interval.getStart().toLocalDate().toString();
            case WEEKLY:
                return StringUtils.capitalize(AppConstants.WEEKLY) + " " + interval.getStart().getWeekOfWeekyear();
            case MONTHLY:
                return interval.getStart().monthOfYear().getAsText();
            case ANNUALLY:
                return StringUtils.capitalize(AppConstants.YEAR) + " " + interval.getStart().getYear();
            case QUATERLY:
                return StringUtils.capitalize(AppConstants.QUARTER) + " " + (interval.getStart().dayOfMonth().withMinimumValue().equals(interval.getStart()) ? interval.getStart().getMonthOfYear() / 3 : (interval.getStart().getMonthOfYear() / 3) + 1);
            //case "ByPeriod": return getActualTimeBankByPeriod(startDate,endDate,shifts);
        }
        return "";
    }

    /**
     *
     * @param ctaDistributionMap
     * @param unitPositionWithCtaDetailsDTO
     * @return List<CTADistributionDTO>
     */
    private List<CTADistributionDTO> getDistributionOfPayout(Map<Long, Long> ctaDistributionMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<CTADistributionDTO> distributionDTOS = new ArrayList<>();
        unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            if (!cta.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && cta.getAccountType().equals(PAIDOUT_ACCOUNT)) {
                distributionDTOS.add(new CTADistributionDTO(cta.getId(), cta.getName(),ctaDistributionMap.getOrDefault(cta.getId(), 0l).intValue()));
            }
        });
        return distributionDTOS;
    }

    /**
     *
     * @param timeBankIntervalDTOS
     * @return long[]
     */
    private long[] calculatePayoutForInterval(List<PayOutIntervalDTO> timeBankIntervalDTOS){
        long payoutChange = 0l;
        long payoutBefore = 0l;
        long payoutAfter = 0l;
        long payoutFromCTA = 0l;
        for (PayOutIntervalDTO payOutIntervalDTO : timeBankIntervalDTOS) {
            payoutChange += payOutIntervalDTO.getPayoutChange();
            payoutFromCTA += payOutIntervalDTO.getPayOutDistribution().getMinutes();
        }
        return new long[]{payoutChange,payoutBefore,payoutAfter,payoutFromCTA};

    }

}
