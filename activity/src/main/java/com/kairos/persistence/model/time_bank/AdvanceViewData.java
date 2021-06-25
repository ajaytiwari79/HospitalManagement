package com.kairos.persistence.model.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.service.time_bank.TimeBankService;
import lombok.Getter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.AppConstants.CAMEL_CASE_MONTHLY;
import static com.kairos.constants.AppConstants.WEEK;
import static java.util.stream.Collectors.toList;

@Getter
public class AdvanceViewData {

    private Long unitId;
    private Long employmentId;
    private String query;
    private Date startDate;
    private Date endDate;
    private List<EmploymentWithCtaDetailsDTO> employmentDetails;
    private List<Interval> intervals;
    private long totalTimeBankBeforeStartDate;
    private List<ShiftWithActivityDTO> shiftQueryResultWithActivities;
    private List<TimeTypeDTO> timeTypeDTOS;
    private List<PayOutPerShift> payOutPerShifts;
    private List<Long> employmentIds;
    private TimeBankService timeBankService;

    public AdvanceViewData(Long unitId, Long employmentId, String query, Date startDate, Date endDate,TimeBankService timeBankService) {
        this.unitId = unitId;
        this.employmentId = employmentId;
        this.query = query;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeBankService = timeBankService;
    }

    public AdvanceViewData invoke() {
        PlanningPeriod planningPeriod = timeBankService.getPlanningPeriodService().findOneByUnitIdAndDate(unitId, startDate);
        totalTimeBankBeforeStartDate = 0;
        timeTypeDTOS = null;
        employmentIds = newArrayList(employmentId);
        if(isNotNull(endDate)){
            updateDataForTimebankView();
        }else {
            updateDataForTimebalanceViewOfPlanningView(planningPeriod);
        }
        return this;
    }

    private void updateDataForTimebalanceViewOfPlanningView(PlanningPeriod planningPeriod) {
        Interval todayInterval = new Interval(getStartDateByQueryParam(startDate,query), getEndTimeStampByQueryParam(query, startDate));
        Interval planningPeriodInterval = new Interval(asDate(planningPeriod.getStartDate()).getTime(),getEndOfDay(asDate(planningPeriod.getEndDate())).getTime());
        Interval yearTillDate = new Interval(asDate(planningPeriod.getStartDate().with(TemporalAdjusters.firstDayOfYear())).getTime(),getEndOfDay(startDate).getTime());
        intervals = newArrayList(todayInterval,planningPeriodInterval,yearTillDate);
        endDate = todayInterval.getEnd().isAfter(planningPeriodInterval.getEnd()) ? todayInterval.getEnd().toDate() : planningPeriodInterval.getEnd().toDate();
        if(isNotNull(employmentId)){
            EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = timeBankService.updateCostTimeAgreementDetails(employmentId, startDate, endDate);
            employmentDetails = newArrayList(employmentWithCtaDetailsDTO);
        }else {
            employmentDetails = timeBankService.getUserIntegrationService().getAllEmploymentByUnitId(unitId);
            employmentDetails.forEach(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.setCtaRuleTemplates(timeBankService.updateCostTimeAggrement(employmentWithCtaDetailsDTO.getId(),startDate,planningPeriodInterval.getEnd().toDate(),employmentWithCtaDetailsDTO)));
        }
        employmentIds = employmentDetails.stream().map(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getId()).collect(toList());
        payOutPerShifts = timeBankService.getPayOutRepository().findAllByEmploymentsAndDate(employmentIds, asDate(planningPeriod.getStartDate().with(TemporalAdjusters.firstDayOfYear())), endDate);
        shiftQueryResultWithActivities = timeBankService.getShiftMongoRepository().findAllShiftsBetweenDurationByEmploymentIds(null,employmentIds, asDate(planningPeriod.getStartDate().with(TemporalAdjusters.firstDayOfYear())), endDate,null);
    }

    private void updateDataForTimebankView() {
        shiftQueryResultWithActivities = timeBankService.getShiftMongoRepository().findAllShiftsBetweenDurationByEmploymentId(null,employmentId, startDate, endDate,null);
        endDate = getEndOfDay(asDate(DateUtils.asLocalDate(endDate)));
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = timeBankService.updateCostTimeAgreementDetails(employmentId, startDate, endDate);
        employmentDetails = newArrayList(employmentWithCtaDetailsDTO);
        timeTypeDTOS = timeBankService.getTimeTypeService().getAllTimeTypeByCountryId(employmentWithCtaDetailsDTO.getCountryId());
        if(new DateTime(startDate).isAfter(toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate()))) {
            Interval interval = new Interval(toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate()), new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankService.getTimeBankRepository().findAllByEmploymentIdAndStartDate(employmentId, new DateTime(startDate).toDate());
            DateTimeInterval planningPeriodInterval = timeBankService.getPlanningPeriodService().getPlanningPeriodIntervalByUnitId(unitId);
            totalTimeBankBeforeStartDate = (int)timeBankService.getTimeBankCalculationService().calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, new HashSet<>(), dailyTimeBanksBeforeStartDate, false)[0];
        }
        totalTimeBankBeforeStartDate += employmentWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
        intervals = timeBankService.getTimeBankCalculationService().getAllIntervalsBetweenDates(startDate, endDate, query);
        payOutPerShifts = timeBankService.getPayOutRepository().findAllByEmploymentAndDate(employmentWithCtaDetailsDTO.getId(), startDate, endDate);
    }

    private long getStartDateByQueryParam(Date startDate, String query) {
        long timeStamp = startDate.getTime();
        if(query.equals(WEEK)){
            timeStamp = asDate(asZonedDateTime(startDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))).getTime();
        }else if(query.equals(CAMEL_CASE_MONTHLY)){
            timeStamp = asDate(asZonedDateTime(startDate).with(TemporalAdjusters.firstDayOfMonth())).getTime();
        }
        return timeStamp;
    }

    private long getEndTimeStampByQueryParam(String query, Date startDate) {
        long timeStamp = getEndOfDay(startDate).getTime();
        if(query.equals(WEEK)){
            timeStamp = getEndOfDay(asDate(asZonedDateTime(startDate).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)))).getTime();
        }else if(query.equals(CAMEL_CASE_MONTHLY)){
            timeStamp = getEndOfDay(asDate(asZonedDateTime(startDate).with(TemporalAdjusters.lastDayOfMonth()))).getTime();
        }
        return timeStamp;
    }
}
