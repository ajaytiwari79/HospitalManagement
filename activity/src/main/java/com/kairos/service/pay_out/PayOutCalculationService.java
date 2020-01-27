package com.kairos.service.pay_out;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.pay_out.PayOutCTADistributionDTO;
import com.kairos.dto.activity.pay_out.PayOutDTO;
import com.kairos.dto.activity.pay_out.PayOutIntervalDTO;
import com.kairos.dto.activity.pay_out.PayOutPerShiftCTADistributionDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.CTARuletemplateBonus;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.time_bank.time_bank_basic.time_bank.CTADistributionDTO;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.service.time_bank.TimeBankCalculationService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Interval;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.distinctByKey;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.*;
import static com.kairos.enums.cta.AccountType.PAID_OUT;


/*
* Created By Pradeep singh
*
* */

@Service
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

    public PayOutPerShift calculateAndUpdatePayOut(DateTimeInterval interval, StaffEmploymentDetails staffEmploymentDetails, ShiftWithActivityDTO shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, PayOutPerShift payOutPerShift, List<DayTypeDTO> dayTypeDTOS) {
        int scheduledMinutesOfPayout = 0;
        Map<BigInteger, Integer> ctaPayoutMinMap = new HashMap<>();
        Map<Long,DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(DayTypeDTO::getId, v->v));
        boolean ruleTemplateValid = false;
        int ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ruleTemplate : staffEmploymentDetails.getCtaRuleTemplates()) {
            int ctaScheduledOrCompensationMinutes = 0;
            List<ShiftActivityDTO> shiftActivities = timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().getShiftActivityByBreak(shift.getActivities(),shift.getBreakActivities());
            for (ShiftActivityDTO shiftActivity : shiftActivities) {
                ShiftActivityDTO shiftActivityDTO = timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().getShiftActivityDTO(shift,shiftActivity);
                Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
                ruleTemplateValid = timeBankCalculationService.validateCTARuleTemplate(dayTypeDTOMap, ruleTemplate, staffEmploymentDetails, shift.getPhaseId(), activity.getId(), activity.getBalanceSettingsActivityTab().getTimeTypeId(), shiftActivity.getStartDate(), shiftActivity.getPlannedTimes()) && ruleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT);
                if (ruleTemplateValid) {
                    if (ruleTemplate.getCalculationFor().equals(CalculationFor.SCHEDULED_HOURS)) {
                        scheduledMinutesOfPayout += shiftActivity.getScheduledMinutes();
                        ctaScheduledOrCompensationMinutes = shiftActivity.getScheduledMinutes();
                        shiftActivityDTO.setScheduledMinutesOfPayout(shiftActivity.getScheduledMinutes() + shiftActivityDTO.getScheduledMinutesOfPayout());
                    } else if (ruleTemplate.getCalculationFor().equals(BONUS_HOURS)) {
                        ctaScheduledOrCompensationMinutes = (int)Math.round(timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().getAndUpdateCtaBonusMinutes(interval, ruleTemplate, shiftActivity,staffEmploymentDetails));
                        ctaBonusMinutes += ctaScheduledOrCompensationMinutes;
                        Optional<PayOutPerShiftCTADistributionDTO> payOutPerShiftCTADistributionDTOOptional = shiftActivity.getPayoutPerShiftCTADistributions().stream().filter(distributionDTO -> distributionDTO.getCtaRuleTemplateId().equals(ruleTemplate.getId())).findAny();
                        if (payOutPerShiftCTADistributionDTOOptional.isPresent()) {
                            payOutPerShiftCTADistributionDTOOptional.get().setMinutes(payOutPerShiftCTADistributionDTOOptional.get().getMinutes() + ctaPayoutMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaScheduledOrCompensationMinutes);
                        } else {
                            PayOutPerShiftCTADistributionDTO payOutPerShiftCTADistributionDTO = new PayOutPerShiftCTADistributionDTO(ruleTemplate.getName(), ctaPayoutMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaScheduledOrCompensationMinutes, ruleTemplate.getId());
                            shiftActivityDTO.getPayoutPerShiftCTADistributions().add(payOutPerShiftCTADistributionDTO);
                        }
                        shiftActivityDTO.setPayoutCtaBonusMinutes(shiftActivity.getPayoutCtaBonusMinutes() + ctaScheduledOrCompensationMinutes);

                    }
                    ctaPayoutMinMap.put(ruleTemplate.getId(), ctaPayoutMinMap.getOrDefault(ruleTemplate.getId(), 0) + ctaScheduledOrCompensationMinutes);
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
        shift.setPlannedMinutesOfPayout(ctaBonusMinutes+scheduledMinutesOfPayout);
        payOutPerShift.setPayOutPerShiftCTADistributions(getCTADistribution(staffEmploymentDetails.getCtaRuleTemplates(), ctaPayoutMinMap));
        return payOutPerShift;
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
                timeBankCTADistributions.add(new PayOutPerShiftCTADistribution(ruleTemplate.getName(), ctaTimeBankMinMap.getOrDefault(ruleTemplate.getId(), 0), ruleTemplate.getId(),0f));
            }
        }
        return timeBankCTADistributions;
    }


    /**
     * @param intervals
     * @param payOutPerShifts
     * @param payoutTransactionAndIntervalMap
     * @param employmentWithCtaDetailsDTOS
     * @return PayOutDTO
     */
    public PayOutDTO getAdvanceViewPayout(List<Interval> intervals, List<PayOutPerShift> payOutPerShifts, long payoutMinutesBefore, Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap, List<EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOS, String query,Map<Interval,Integer> sequenceIntervalMap) {
        Map<Interval, List<PayOutPerShift>> payoutsIntervalMap = getPayoutIntervalsMap(intervals, payOutPerShifts);
        List<PayOutIntervalDTO> payoutIntervalDTOS = getPayoutIntervals(intervals, payoutsIntervalMap,payoutMinutesBefore, payoutTransactionAndIntervalMap, employmentWithCtaDetailsDTOS, query,sequenceIntervalMap);
        List<CTADistributionDTO> scheduledCTADistributions = payoutIntervalDTOS.stream().flatMap(ti -> ti.getPayOutDistribution().getScheduledCTADistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaDistributionMap = scheduledCTADistributions.stream().collect(Collectors.groupingBy(CTADistributionDTO::getName, Collectors.summingInt(CTADistributionDTO::getMinutes)));
        scheduledCTADistributions = getScheduledCTADistributions(ctaDistributionMap, employmentWithCtaDetailsDTOS.get(0));
        List<CTADistributionDTO> ctaBonusDistributions = payoutIntervalDTOS.stream().flatMap(ti -> ti.getPayOutDistribution().getCtaRuletemplateBonus().getCtaDistributions().stream()).collect(Collectors.toList());
        Map<String, Integer> ctaBonusDistributionMap = ctaBonusDistributions.stream().collect(Collectors.groupingBy(CTADistributionDTO::getName, Collectors.summingInt(CTADistributionDTO::getMinutes)));
        long[] payoutCalculatedValue = calculatePayoutForInterval(payoutIntervalDTOS);
        long payoutChange = payoutCalculatedValue[0];
        long payoutBefore = payoutCalculatedValue[1];
        long payoutAfter = payoutCalculatedValue[2];
        long payoutFromCTA = payoutCalculatedValue[3];
        long protectedDaysOffMinutes = payoutCalculatedValue[4];
        PayOutCTADistributionDTO payOutCTADistributionDTO = new PayOutCTADistributionDTO(scheduledCTADistributions, getCTABonusDistributions(ctaBonusDistributionMap, employmentWithCtaDetailsDTOS.get(0)),payoutFromCTA);
        return new PayOutDTO(intervals.get(0).getStart().toDate(), intervals.get(intervals.size() - 1).getEnd().toDate(), payoutAfter, payoutBefore, payoutChange, payoutIntervalDTOS, payOutCTADistributionDTO,protectedDaysOffMinutes);
    }

    private CTARuletemplateBonus getCTABonusDistributions(Map<String, Integer> ctaDistributionMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        List<CTADistributionDTO> ctaBonusDistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : employmentWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if(ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT) && ObjectUtils.newHashSet(BONUS_HOURS,FUNCTIONS,UNUSED_DAYOFF_LEAVES).contains(ctaRuleTemplate.getCalculationFor())) {
                CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0),0);
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
                scheduledCTADistributions.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getName(), 0),0f));

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
     * @param employmentWithCtaDetailsDTOS
     * @return List<PayOutIntervalDTO>
     */
    private List<PayOutIntervalDTO> getPayoutIntervals(List<Interval> intervals, Map<Interval, List<PayOutPerShift>> payoutsIntervalMap, long payoutMinutesBefore, Map<Interval, List<PayOutTransaction>> payoutTransactionAndIntervalMap, List<EmploymentWithCtaDetailsDTO> employmentWithCtaDetailsDTOS, String query,Map<Interval,Integer> sequenceIntervalMap) {
        List<PayOutIntervalDTO> payOutIntervalDTOS = new ArrayList<>(intervals.size());
        Map<Long,List<EmploymentLinesDTO>> employmentWithCtaDetailsHourlyCostMap = employmentWithCtaDetailsDTOS.stream().filter(distinctByKey(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getId())).collect(Collectors.toMap(k->k.getId(), v->v.getEmploymentLines()));
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = employmentWithCtaDetailsDTOS.stream().flatMap(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getCtaRuleTemplates().stream()).filter(distinctByKey(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getName())).collect(Collectors.toList());
        for (Interval interval : intervals) {
            List<PayOutPerShift> payOutPerShifts = payoutsIntervalMap.get(interval);
            List<PayOutTransaction> payOutTransactionList = payoutTransactionAndIntervalMap.get(interval);
            Long payoutChange = payOutPerShifts.stream().mapToLong(PayOutPerShift::getTotalPayOutMinutes).sum();
            Long protectedDaysOffMinutes = payOutPerShifts.stream().mapToLong(PayOutPerShift::getProtectedDaysOffMinutes).sum();
            Double payoutCost = payOutPerShifts.stream().mapToDouble(payOutPerShift -> timeBankCalculationService.getCostByByMinutes(employmentWithCtaDetailsHourlyCostMap.get(payOutPerShift.getEmploymentId()),(int)payOutPerShift.getTotalPayOutMinutes(),payOutPerShift.getDate()).doubleValue()).sum();
            Long approvePayOut = payOutTransactionList.stream().filter(p -> p.getPayOutTrasactionStatus().equals(PayOutTrasactionStatus.APPROVED)).mapToLong(p -> (long) p.getMinutes()).sum();
            payoutChange += approvePayOut;
            Long payoutAfter = payoutMinutesBefore + payoutChange;
            updateCostInDistribution(employmentWithCtaDetailsHourlyCostMap, payOutPerShifts);
            List<PayOutPerShiftCTADistribution> payOutPerShiftCTADistributions = payOutPerShifts.stream().flatMap(payOutPerShift -> payOutPerShift.getPayOutPerShiftCTADistributions().stream()).collect(Collectors.toList());
            Map<String, Integer> ctaDistributionMap = payOutPerShiftCTADistributions.stream().collect(Collectors.groupingBy(PayOutPerShiftCTADistribution::getCtaName, Collectors.summingInt(PayOutPerShiftCTADistribution::getMinutes)));
            Long payoutFromCTA = payOutPerShifts.stream().mapToLong(payOutPerShift->payOutPerShift.getScheduledMinutes()+payOutPerShift.getCtaBonusMinutesOfPayOut()).sum();
            Map<String, Double> ctaCostDistributionMap = payOutPerShiftCTADistributions.stream().collect(Collectors.groupingBy(PayOutPerShiftCTADistribution::getCtaName, Collectors.summingDouble(PayOutPerShiftCTADistribution::getCost)));
            PayOutCTADistributionDTO payOutCTADistributionDTO = getDistributionOfPayout(ctaDistributionMap, ctaRuleTemplateDTOS,payoutFromCTA,ctaCostDistributionMap);
            String title = getTitle(query, interval);
            PayOutIntervalDTO payOutIntervalDTO = new PayOutIntervalDTO(interval.getStart().toDate(), interval.getEnd().toDate(), payoutAfter, payoutMinutesBefore, payoutChange, payOutCTADistributionDTO, DayOfWeek.of(interval.getStart().getDayOfWeek()), title,payoutCost.floatValue());
            payoutMinutesBefore+=payoutChange;
            payOutIntervalDTO.setProtectedDaysOffMinutes(protectedDaysOffMinutes);
            payOutIntervalDTO.setSequence(sequenceIntervalMap.getOrDefault(interval,0));
            payOutIntervalDTOS.add(payOutIntervalDTO);
        }
        Collections.reverse(payOutIntervalDTOS);
        return payOutIntervalDTOS;
    }

    private void updateCostInDistribution(Map<Long, List<EmploymentLinesDTO>> employmentWithCtaDetailsHourlyCostMap, List<PayOutPerShift> payOutPerShifts) {
        payOutPerShifts.forEach(payOutPerShift -> {
    payOutPerShift.getPayOutPerShiftCTADistributions().forEach(payOutPerShiftCTADistribution -> {
        float cost = timeBankCalculationService.getCostByByMinutes(employmentWithCtaDetailsHourlyCostMap.get(payOutPerShift.getEmploymentId()),payOutPerShiftCTADistribution.getMinutes(),payOutPerShift.getDate()).floatValue();
        payOutPerShiftCTADistribution.setCost(cost);
    });
});
    }

    public PayOutCTADistributionDTO getDistributionOfPayout(Map<String, Integer> ctaDistributionMap, List<CTARuleTemplateDTO> ctaRuleTemplateDTOS, long plannedMinutesOfPayOut,Map<String, Double> ctaCostDistributionMap) {
        List<CTADistributionDTO> timeBankCTADistributionDTOS = new ArrayList<>();
        List<CTADistributionDTO> scheduledCTADistributions = new ArrayList<>();
        long ctaBonusMinutes = 0;
        for (CTARuleTemplateDTO ctaRuleTemplate : ctaRuleTemplateDTOS) {
            if(ctaRuleTemplate.getPlannedTimeWithFactor().getAccountType().equals(PAID_OUT)) {
                if(ObjectUtils.newHashSet(BONUS_HOURS,FUNCTIONS,UNUSED_DAYOFF_LEAVES).contains(ctaRuleTemplate.getCalculationFor())) {
                    CTADistributionDTO ctaDistributionDTO = new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0),ctaCostDistributionMap.getOrDefault(ctaRuleTemplate.getName(), new Double(0.0)).intValue());
                    timeBankCTADistributionDTOS.add(ctaDistributionDTO);
                    ctaBonusMinutes += ctaDistributionDTO.getMinutes();
                } else if(ctaRuleTemplate.getCalculationFor().equals(SCHEDULED_HOURS)) {
                    scheduledCTADistributions.add(new CTADistributionDTO(ctaRuleTemplate.getId(), ctaRuleTemplate.getName(), ctaDistributionMap.getOrDefault(ctaRuleTemplate.getName(), 0),ctaCostDistributionMap.getOrDefault(ctaRuleTemplate.getName(), new Double(0.0)).intValue()));
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
            default:
                break;
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
                distributionDTOS.add(new CTADistributionDTO(cta.getId(), cta.getName(), ctaDistributionMap.getOrDefault(cta.getId(), 0l).intValue(),0f));
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
        long protectedDaysOffMinutes = 0l;
        for (PayOutIntervalDTO payOutIntervalDTO : timeBankIntervalDTOS) {
            payoutChange += payOutIntervalDTO.getPayoutChange();
            payoutFromCTA += payOutIntervalDTO.getPayOutDistribution().getPlannedMinutesOfPayout();
            protectedDaysOffMinutes += payOutIntervalDTO.getProtectedDaysOffMinutes();
        }
        long payoutAfter = payoutBefore + payoutChange;
        return new long[]{payoutChange, payoutBefore, payoutAfter, payoutFromCTA,protectedDaysOffMinutes};

    }

}
