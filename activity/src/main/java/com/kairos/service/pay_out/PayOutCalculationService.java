package com.kairos.service.pay_out;

import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.cta.CompensationTableInterval;
import com.kairos.dto.activity.pay_out.PayOutCTADistributionDTO;
import com.kairos.dto.activity.pay_out.PayOutDTO;
import com.kairos.dto.activity.pay_out.PayOutIntervalDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.time_bank.CTARuletemplateBonus;
import com.kairos.dto.activity.time_bank.TimeBankCTADistributionDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.constants.AppConstants;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.service.time_bank.TimeBankCalculationService;
import org.apache.commons.lang3.StringUtils;
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

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.PAID_OUT;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.cta.AccountType.TIMEBANK_ACCOUNT;


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
     * @param staffEmploymentDetails
     * @param shift
     * @param activityWrapperMap
     * @param payOutPerShift
     * @return PayOutPerShift
     */

    public PayOutPerShift calculateAndUpdatePayOut(DateTimeInterval interval, StaffEmploymentDetails staffEmploymentDetails, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, PayOutPerShift payOutPerShift, List<DayTypeDTO> dayTypeDTOS) {
        int scheduledMinutesOfPayout = 0;
        Map<BigInteger, Integer> ctaPayoutMinMap = new HashMap<>();
        Map<Long,DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        boolean ruleTemplateValid = false;
        int ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ruleTemplate : staffEmploymentDetails.getCtaRuleTemplates()) {
            int ctaScheduledOrCompensationMinutes = 0;
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                shiftActivity.setPayoutPerShiftCTADistributions(new ArrayList<>());
                DateTimeInterval shiftInterval = new DateTimeInterval(shiftActivity.getStartDate().getTime(), shiftActivity.getEndDate().getTime());
                    Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
                    ruleTemplateValid = timeBankCalculationService.validateCTARuleTemplate(dayTypeDTOMap, ruleTemplate, staffEmploymentDetails, shift.getPhaseId(), activity.getId(), activity.getBalanceSettingsActivityTab().getTimeTypeId(), new DateTimeInterval(shiftInterval.getStart(), shiftInterval.getEnd()), shiftActivity.getPlannedTimeId()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT);
                    if (ruleTemplateValid) {
                        if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS)) {
                            scheduledMinutesOfPayout += shiftActivity.getScheduledMinutes();
                            ctaScheduledOrCompensationMinutes = shiftActivity.getScheduledMinutes();
                            shiftActivity.setScheduledMinutesOfTimebank(shiftActivity.getScheduledMinutes()+shiftActivity.getScheduledMinutesOfTimebank());
                        } else if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                            ctaScheduledOrCompensationMinutes = timeBankCalculationService.calculateCTARuleTemplateBonus(ruleTemplate, interval, shiftInterval);
                            ctaBonusMinutes += ctaScheduledOrCompensationMinutes;
                            shiftActivity.getPayoutPerShiftCTADistributions().add(new PayOutPerShiftCTADistribution(ruleTemplate.getName(), ctaPayoutMinMap.getOrDefault(ruleTemplate.getId(), 0)+ctaScheduledOrCompensationMinutes, ruleTemplate.getId()));
                            shiftActivity.setPayoutCtaBonusMinutes(shiftActivity.getTimeBankCtaBonusMinutes() + ctaScheduledOrCompensationMinutes);

                        }
                        ctaPayoutMinMap.put(ruleTemplate.getId(), ctaScheduledOrCompensationMinutes);
                    }
            }
            if (ruleTemplate.getCalculationFor().equals(FUNCTIONS) && ruleTemplateValid) {
                int value = timeBankCalculationService.getFunctionalBonusCompensation(staffEmploymentDetails,ruleTemplate,interval);
                ctaScheduledOrCompensationMinutes = value;
                ctaBonusMinutes += value;
                ctaPayoutMinMap.put(ruleTemplate.getId(), ctaScheduledOrCompensationMinutes);
            }
        }
        payOutPerShift.setCtaBonusMinutesOfPayOut(ctaBonusMinutes);
        payOutPerShift.setScheduledMinutes(scheduledMinutesOfPayout);
        payOutPerShift.setTotalPayOutMinutes(ctaBonusMinutes+scheduledMinutesOfPayout);
        payOutPerShift.setPayOutPerShiftCTADistributions(getCTADistribution(staffEmploymentDetails.getCtaRuleTemplates(), ctaPayoutMinMap));
        return payOutPerShift;
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
     * @return List<PayOutPerShiftCTADistribution>s
     */
    private List<PayOutPerShiftCTADistribution> getCTADistribution(List<CTARuleTemplateDTO> ctaRuleTemplateCalculatedTimeBankDTOS, Map<BigInteger, Integer> ctaTimeBankMinMap) {
        List<PayOutPerShiftCTADistribution> timeBankCTADistributions = new ArrayList<>(ctaRuleTemplateCalculatedTimeBankDTOS.size());
        for (CTARuleTemplateDTO ruleTemplate : ctaRuleTemplateCalculatedTimeBankDTOS) {
            if (PAID_OUT.equals(ruleTemplate.getPlannedTimeWithFactor().getAccountType())) {
                timeBankCTADistributions.add(new PayOutPerShiftCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0), ruleTemplate.getId()));
            }
        }
        return timeBankCTADistributions;
    }


    /**
     * @param intervals
     * @param payOutPerShifts
     * @param payoutTransactionAndIntervalMap
     * @param employmentWithCtaDetailsDTO
     * @return PayOutDTO
     */
    public PayOutDTO getAdvanceViewPayout(List<Interval> intervals, List<PayOutPerShift> payOutPerShifts, long payoutMinutesBefore, Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, String query) {
        Map<Interval, List<PayOutPerShift>> payoutsIntervalMap = getPayoutIntervalsMap(intervals, payOutPerShifts);
        List<PayOutIntervalDTO> payoutIntervalDTOS = getPayoutIntervals(intervals, payoutsIntervalMap,payoutMinutesBefore, payoutTransactionAndIntervalMap, employmentWithCtaDetailsDTO, query);
        List<CTADistributionDTO> scheduledCTADistributions = payoutIntervalDTOS.stream().flatMap(ti -> ti.getPayOutDistribution().getScheduledCTADistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = scheduledCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getName(), Collectors.summingInt(tb -> tb.getMinutes())));
        scheduledCTADistributions = getScheduledCTADistributions(ctaDistributionMap, employmentWithCtaDetailsDTO);
        List<CTADistributionDTO> ctaBonusDistributions = payoutIntervalDTOS.stream().flatMap(ti -> ti.getPayOutDistribution().getCtaRuletemplateBonus().getCtaDistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaBonusDistributionMap = ctaBonusDistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getName(), Collectors.summingInt(tb -> tb.getMinutes())));
        long[] payoutCalculatedValue = calculatePayoutForInterval(payoutIntervalDTOS);
        long payoutChange = payoutCalculatedValue[0];
        long payoutBefore = payoutCalculatedValue[1];
        long payoutAfter = payoutCalculatedValue[2];
        long payoutFromCTA = payoutCalculatedValue[3];
        PayOutCTADistributionDTO payOutCTADistributionDTO = new PayOutCTADistributionDTO(scheduledCTADistributions, getCTABonusDistributions(ctaBonusDistributionMap, employmentWithCtaDetailsDTO),payoutFromCTA);
        return new PayOutDTO(intervals.get(0).getStart().toDate(), intervals.get(intervals.size() - 1).getEnd().toDate(), payoutAfter, payoutBefore, payoutChange, payoutIntervalDTOS, payOutCTADistributionDTO);
    }

    private CTARuletemplateBonus getCTABonusDistributions(Map<String, Integer> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<CTADistributionDTO> ctaBonusDistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : employmentWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if(ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT) && (ctaRuleTemplate.getCalculationFor().equals(BONUS_HOURS) || ctaRuleTemplate.getCalculationFor().equals(FUNCTIONS))) {
                CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0));
                ctaBonusDistributions.add(ctaDistributionDTO);
                ctaBonusMinutes += ctaDistributionDTO.getMinutes();
            }
        }
        return new CTARuletemplateBonus(ctaBonusDistributions, ctaBonusMinutes);
    }

    private List<CTADistributionDTO> getScheduledCTADistributions(Map<String, Integer> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        employmentWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
            if(cta.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT) && cta.getCalculationFor().equals(SCHEDULED_HOURS)) {
                scheduledCTADistributions.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getName(), 0)));

            }
        });
        return scheduledCTADistributions;
    }

    /**
     * @param intervals
     * @param payOutPerShifts
     * @return Map<Interval, List<PayOutPerShift>>
     */
    private Map<Interval, List<PayOutPerShift>> getPayoutIntervalsMap(List<Interval> intervals, List<PayOutPerShift> payOutPerShifts) {
        Map<Interval, List<PayOutPerShift>> timeBanksIntervalMap = new HashMap<>(intervals.size());
        intervals.forEach(i -> timeBanksIntervalMap.put(i, getPayoutsByInterval(i, payOutPerShifts)));
        return timeBanksIntervalMap;
    }


    /**
     * @param interval
     * @param payOutPerShifts
     * @return List<PayOutPerShift>
     */
    private List<PayOutPerShift> getPayoutsByInterval(Interval interval, List<PayOutPerShift> payOutPerShifts) {
        List<PayOutPerShift> payOutPerShiftList = new ArrayList<>();
        payOutPerShifts.forEach(payOut -> {
            if (interval.contains(DateUtils.asDate(payOut.getDate()).getTime()) || interval.getStart().equals(DateUtils.toJodaDateTime(payOut.getDate()))) {
                payOutPerShiftList.add(payOut);
            }
        });
        return payOutPerShiftList;
    }


    /**
     * @param intervals
     * @param payoutsIntervalMap
     * @param payoutTransactionAndIntervalMap
     * @param employmentWithCtaDetailsDTO
     * @return List<PayOutIntervalDTO>
     */
    private List<PayOutIntervalDTO> getPayoutIntervals(List<Interval> intervals, Map<Interval, List<PayOutPerShift>> payoutsIntervalMap, long payoutMinutesBefore, Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, String query) {
        List<PayOutIntervalDTO> payOutIntervalDTOS = new ArrayList<>(intervals.size());
        for (Interval interval : intervals) {
            List<PayOutPerShift> payOutPerShifts = payoutsIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionAndIntervalMap.get(interval);
            Long payoutChange = payOutPerShifts.stream().mapToLong(p -> p.getTotalPayOutMinutes()).sum();
            Long approvePayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p -> (long) p.getMinutes()).sum();
            payoutChange += approvePayOut;
            Long payoutAfter = payoutMinutesBefore + payoutChange;
            List<PayOutPerShiftCTADistribution> payOutPerShiftCTADistributions = payOutPerShifts.stream().flatMap(payOutPerShift -> payOutPerShift.getPayOutPerShiftCTADistributions().stream()).collect(Collectors.toList());
            Map<String, Integer> ctaDistributionMap = payOutPerShiftCTADistributions.stream().collect(Collectors.groupingBy(tbdistribution -> tbdistribution.getCtaName(), Collectors.summingInt(tb -> tb.getMinutes())));
            Long payoutFromCTA = payOutPerShifts.stream().mapToLong(payOutPerShift->payOutPerShift.getScheduledMinutes()+payOutPerShift.getCtaBonusMinutesOfPayOut()).sum();
            PayOutCTADistributionDTO payOutCTADistributionDTO = getDistributionOfPayout(ctaDistributionMap, employmentWithCtaDetailsDTO,payoutFromCTA);
            String title = getTitle(query, interval);
            PayOutIntervalDTO payOutIntervalDTO = new PayOutIntervalDTO(interval.getStart().toDate(), interval.getEnd().toDate(), payoutAfter, payoutMinutesBefore, payoutChange, payOutCTADistributionDTO, DayOfWeek.of(interval.getStart().getDayOfWeek()), title);
            payoutMinutesBefore+=payoutChange;
            payOutIntervalDTOS.add(payOutIntervalDTO);
        }
        Collections.reverse(payOutIntervalDTOS);
        return payOutIntervalDTOS;
    }

    public PayOutCTADistributionDTO getDistributionOfPayout(Map<String, Integer> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, long plannedMinutesOfPayOut) {
        List<CTADistributionDTO> timeBankCTADistributionDTOS = new ArrayList<>();
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : employmentWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if(ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT)) {
                if(ctaRuleTemplate.getCalculationFor().equals(BONUS_HOURS) || ctaRuleTemplate.getCalculationFor().equals(FUNCTIONS)) {
                    CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0));
                    timeBankCTADistributionDTOS.add(ctaDistributionDTO);
                    ctaBonusMinutes += ctaDistributionDTO.getMinutes();
                } else if(ctaRuleTemplate.getCalculationFor().equals(SCHEDULED_HOURS)) {
                    scheduledCTADistributions.add(new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0)));
                }
            }
        }
        return new PayOutCTADistributionDTO(scheduledCTADistributions, new CTARuletemplateBonus(timeBankCTADistributionDTOS, ctaBonusMinutes),plannedMinutesOfPayOut);
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
     * @param employmentWithCtaDetailsDTO
     * @return List<CTADistributionDTO>
     */
    private List<CTADistributionDTO> getDistributionOfPayout(Map<BigInteger, Long> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<CTADistributionDTO> distributionDTOS = new ArrayList<>();
        employmentWithCtaDetailsDTO.getCtaRuleTemplates().forEach(cta -> {
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
            payoutFromCTA += payOutIntervalDTO.getPayOutDistribution().getPlannedMinutesOfPayout();
        }
        long payoutAfter = payoutBefore + payoutChange;
        return new long[]{payoutChange, payoutBefore, payoutAfter, payoutFromCTA};

    }

}
