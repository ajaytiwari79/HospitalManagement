package com.kairos.service.pay_out;

import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta.CompensationTableInterval;
import com.kairos.dto.activity.pay_out.PayOutCTADistributionDTO;
import com.kairos.dto.activity.pay_out.PayOutDTO;
import com.kairos.dto.activity.pay_out.PayOutIntervalDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.constants.AppConstants;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.model.pay_out.PayOutCTADistribution;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.enums.cta.AccountType.PAID_OUT;
import static com.kairos.constants.AppConstants.*;


/*
* Created By Pradeep singh
*
* */

@Component
public class PayOutCalculationService {

    @Inject
    private TimeBankCalculationService timeBankCalculationService;
//~ ======================================================================================================================

    /**
     * @param interval
     * @param staffAdditionalInfoDTO
     * @param shift
     * @param activityWrapperMap
     * @param payOut
     * @return PayOut
     */
    public PayOut calculateAndUpdatePayOut(DateTimeInterval interval, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, PayOut payOut) {
        StaffUnitPositionDetails unitPositionDetails = staffAdditionalInfoDTO.getUnitPosition();
        int totalPayOut = 0;
        int scheduledMin = 0;
        int contractualMin = interval.getStart().get(ChronoField.DAY_OF_WEEK) <= unitPositionDetails.getWorkingDaysInWeek() ? unitPositionDetails.getTotalWeeklyMinutes() / unitPositionDetails.getWorkingDaysInWeek() : 0;
        Map<BigInteger, Integer> ctaPayoutMinMap = new HashMap<>();
        Map<Long,DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k->k.getId(), v->v));

        for (ShiftActivity shiftActivity : shift.getActivities()) {
            Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
            DateTimeInterval shiftInterval = new DateTimeInterval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime());
            if (interval.overlaps(shiftInterval)) {
                shiftInterval = interval.overlap(shiftInterval);
                for (CTARuleTemplateDTO ruleTemplate : unitPositionDetails.getCtaRuleTemplates()) {
                    boolean ruleTemplateValid = timeBankCalculationService.validateCTARuleTemplate(dayTypeDTOMap,ruleTemplate, unitPositionDetails, shift.getPhaseId(), activity.getId(),activity.getBalanceSettingsActivityTab().getTimeTypeId(), shiftInterval,shiftActivity.getPlannedTimeId()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT);
                    if (ruleTemplateValid) {
                        int ctaPayOutMin = 0;
                        if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && interval.contains(shift.getStartDate().getTime())) {
                            scheduledMin += shiftActivity.getScheduledMinutes();
                            totalPayOut += scheduledMin;
                        }
                        if (ruleTemplate.getCalculationFor().equals(CalculationFor.BONUS_HOURS)) {
                            for (CompensationTableInterval ctaIntervalDTO : ruleTemplate.getCompensationTable().getCompensationTableInterval()) {
                                List<DateTimeInterval> ctaIntervals = getCTAInterval(ctaIntervalDTO, interval.getStart());
                                for (DateTimeInterval ctaInterval : ctaIntervals) {
                                    if (ctaInterval.overlaps(shiftInterval)) {
                                        int overlapTimeInMin = (int) ctaInterval.overlap(shiftInterval).getMinutes();
                                        if (ctaIntervalDTO.getCompensationMeasurementType().equals(CompensationMeasurementType.MINUTES)) {
                                            ctaPayOutMin += (int) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) * ctaIntervalDTO.getValue();
                                            totalPayOut += ctaPayOutMin;
                                            break;
                                        } else if (ctaIntervalDTO.getCompensationMeasurementType().equals(CompensationMeasurementType.PERCENT)) {
                                            ctaPayOutMin += (int) (((double) Math.round((double) overlapTimeInMin / ruleTemplate.getCompensationTable().getGranularityLevel()) / 100) * ctaIntervalDTO.getValue());
                                            totalPayOut += ctaPayOutMin;
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                        if (!ruleTemplate.isCalculateScheduledHours()) {
                            ctaPayoutMinMap.put(ruleTemplate.getId(), ctaPayoutMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaPayOutMin);
                        }
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
     * @param interval
     * @param startDate
     * @return DateTimeInterval
     */
    private List<DateTimeInterval> getCTAInterval(CompensationTableInterval interval, ZonedDateTime startDate) {
        List<DateTimeInterval> ctaIntervals = new ArrayList<>(2);
        if(interval.getFrom().isAfter(interval.getTo())){
            ctaIntervals.add(new DateTimeInterval(startDate.truncatedTo(ChronoUnit.DAYS),startDate.truncatedTo(ChronoUnit.DAYS).plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
            ctaIntervals.add(new DateTimeInterval(startDate.truncatedTo(ChronoUnit.DAYS).plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.truncatedTo(ChronoUnit.DAYS).plusDays(1)));
        }
        else if(interval.getFrom().equals(interval.getTo())){
            ctaIntervals.add(new DateTimeInterval(startDate.truncatedTo(ChronoUnit.DAYS), startDate.truncatedTo(ChronoUnit.DAYS).plusDays(1)));
        }
        else{
            ctaIntervals.add(new DateTimeInterval(startDate.truncatedTo(ChronoUnit.DAYS).plusMinutes(interval.getFrom().get(ChronoField.MINUTE_OF_DAY)), startDate.truncatedTo(ChronoUnit.DAYS).plusMinutes(interval.getTo().get(ChronoField.MINUTE_OF_DAY))));
        }
        return ctaIntervals;
    }

    /**
     * @param ctaRuleTemplateCalculatedTimeBankDTOS
     * @param ctaTimeBankMinMap
     * @return List<PayOutCTADistribution>s
     */
    private List<PayOutCTADistribution> getCTADistribution(List<CTARuleTemplateDTO> ctaRuleTemplateCalculatedTimeBankDTOS, Map<BigInteger, Integer> ctaTimeBankMinMap) {
        List<PayOutCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalculatedTimeBankDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateCalculatedTimeBankDTOS) {
            if (!CalculationFor.SCHEDULED_HOURS.equals(ruleTemplate.getCalculationFor()) && PAID_OUT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                timeBankCTADistributions.add(new PayOutCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0), ruleTemplate.getId()));
            }
        }
        return timeBankCTADistributions;
    }


    /**
     * @param intervals
     * @param payOuts
     * @param payoutTransactionAndIntervalMap
     * @param unitPositionWithCtaDetailsDTO
     * @return PayOutDTO
     */
    public PayOutDTO getAdvanceViewPayout(List<Interval> intervals, List<PayOut> payOuts, Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, String query) {
        Map<Interval, List<PayOut>> payoutsIntervalMap = getPayoutIntervalsMap(intervals, payOuts);
        List<PayOutIntervalDTO> payoutIntervalDTOS = getPayoutIntervals(intervals, payoutsIntervalMap, payoutTransactionAndIntervalMap, unitPositionWithCtaDetailsDTO, query);
        long[] payoutCalculatedValue = calculatePayoutForInterval(payoutIntervalDTOS);
        Map<BigInteger, Long> ctaDistributionMap = payoutIntervalDTOS.stream().flatMap(p -> p.getPayOutDistribution().getChildren().stream()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(ptb -> ptb.getId(), Collectors.summingLong(p -> p.getMinutes())));
        List<CTADistributionDTO> ctaDistributionDTOS = getDistributionOfPayout(ctaDistributionMap, unitPositionWithCtaDetailsDTO);
        long payoutChange = payoutCalculatedValue[0];
        long payoutBefore = payoutCalculatedValue[1];
        long payoutAfter = payoutCalculatedValue[2];
        long payoutFromCTA = payoutCalculatedValue[3];
        PayOutCTADistributionDTO payOutCTADistributionDTO = new PayOutCTADistributionDTO(payoutFromCTA, ctaDistributionDTOS);
        return new PayOutDTO(intervals.get(0).getStart().toDate(), intervals.get(intervals.size() - 1).getEnd().toDate(), payoutAfter, payoutBefore, payoutChange, payoutIntervalDTOS, payOutCTADistributionDTO);
    }

    /**
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
     * @param interval
     * @param payOuts
     * @return List<PayOut>
     */
    private List<PayOut> getPayoutsByInterval(Interval interval, List<PayOut> payOuts) {
        List<PayOut> payOutList = new ArrayList<>();
        payOuts.forEach(payOut -> {
            if (interval.contains(DateUtils.asDate(payOut.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(payOut.getDate()))) {
                payOutList.add(payOut);
            }
        });
        return payOutList;
    }


    /**
     * @param intervals
     * @param payoutsIntervalMap
     * @param payoutTransactionAndIntervalMap
     * @param unitPositionWithCtaDetailsDTO
     * @return List<PayOutIntervalDTO>
     */
    private List<PayOutIntervalDTO> getPayoutIntervals(List<Interval> intervals, Map<Interval, List<PayOut>> payoutsIntervalMap, Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, String query) {
        List<PayOutIntervalDTO> payOutIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<PayOut> payOuts = payoutsIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionAndIntervalMap.get(interval);
            Long payoutChange = payOuts.stream().mapToLong(p -> p.getTotalPayOutMin()).sum();
            Long approvePayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p -> (long) p.getMinutes()).sum();
            payoutChange += approvePayOut;
            Long payoutBefore = payOuts.isEmpty() ? 0 : payOuts.get(0).getPayoutBeforeThisDate();
            Long payoutAfter = payoutBefore + payoutChange;
            Map<BigInteger, Long> ctaDistributionMap = payOuts.stream().flatMap(p -> p.getPayOutCTADistributions().stream()).collect(Collectors.toList()).stream().collect(Collectors.groupingBy(ptb -> ptb.getCtaRuleTemplateId(), Collectors.summingLong(p -> p.getMinutes())));
            List<CTADistributionDTO> payOutCTADistributionDTOS = getDistributionOfPayout(ctaDistributionMap, unitPositionWithCtaDetailsDTO);
            Long payoutFromCTA = payOutCTADistributionDTOS.stream().mapToLong(pd -> pd.getMinutes()).sum();
            PayOutCTADistributionDTO payOutCTADistributionDTO = new PayOutCTADistributionDTO(payoutFromCTA, payOutCTADistributionDTOS);
            String title = getTitle(query, interval);
            PayOutIntervalDTO payOutIntervalDTO = new PayOutIntervalDTO(interval.getStart().toDate(), interval.getEnd().toDate(), payoutAfter, payoutBefore, payoutChange, payOutCTADistributionDTO, DayOfWeek.of(interval.getStart().getDayOfWeek()), title);
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
     * @param ctaDistributionMap
     * @param unitPositionWithCtaDetailsDTO
     * @return List<CTADistributionDTO>
     */
    private List<CTADistributionDTO> getDistributionOfPayout(Map<BigInteger, Long> ctaDistributionMap, UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        List<CTADistributionDTO> distributionDTOS = new ArrayList<>();
        unitPositionWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            if (!cta.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS) && cta.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT)) {
                distributionDTOS.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getId(), 0l).intValue()));
            }
        });
        return distributionDTOS;
    }

    /**
     * @param timeBankIntervalDTOS
     * @return long[]
     */
    private long[] calculatePayoutForInterval(List<PayOutIntervalDTO> timeBankIntervalDTOS) {
        long payoutChange = 0l;
        long payoutBefore = timeBankIntervalDTOS.get(timeBankIntervalDTOS.size() - 1).getTotalPayOutBeforeCtaMin();
        long payoutFromCTA = 0l;
        for (PayOutIntervalDTO payOutIntervalDTO : timeBankIntervalDTOS) {
            payoutChange += payOutIntervalDTO.getPayoutChange();
            payoutFromCTA += payOutIntervalDTO.getPayOutDistribution().getMinutes();
        }
        long payoutAfter = payoutBefore + payoutChange;
        return new long[]{payoutChange, payoutBefore, payoutAfter, payoutFromCTA};

    }

}
