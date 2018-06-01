package com.kairos.activity.service.pay_out;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.pay_out.PayOutRestClient;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.pay_out.PayOutMongoRepository;
import com.kairos.activity.persistence.model.pay_out.DailyPayOutEntry;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.response.dto.pay_out.CalculatedPayOutByDateDTO;
import com.kairos.response.dto.pay_out.PayOutDTO;
import com.kairos.response.dto.pay_out.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.activity.TimeTypeService;
import com.kairos.activity.util.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/*
* Created By Mohit Shakya
*
* */
@Transactional
@Service
public class PayOutService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(PayOutService.class);

    @Inject
    private PayOutMongoRepository payOutMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PayOutRestClient payOutRestClient;
    @Inject
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private TimeTypeService timeTypeService;

    public void savePayOut(Long unitPositionId, Shift shift) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        List<DailyPayOutEntry> dailyPayOuts = calculateDailyPayOut(unitPositionWithCtaDetailsDTO,shift,unitPositionId);
        if (!dailyPayOuts.isEmpty()) {
            save(dailyPayOuts);
        }
    }

    public void savePayOuts(Long unitPositionId, List<Shift> shifts) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        List<DailyPayOutEntry> updatedDailyPayOutEntries = new ArrayList<>();
        for (Shift shift : shifts) {
            List<DailyPayOutEntry> dailyPayOutEntries = calculateDailyPayOut(unitPositionWithCtaDetailsDTO,shift,unitPositionId);
            updatedDailyPayOutEntries.addAll(dailyPayOutEntries);
        }
        if(!updatedDailyPayOutEntries.isEmpty()) {
            save(updatedDailyPayOutEntries);
        }
    }

    private List<DailyPayOutEntry> calculateDailyPayOut(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Shift shift, Long unitPositionId){
        unitPositionWithCtaDetailsDTO.setStaffId(shift.getStaffId());
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).withTimeAtStartOfDay();
        List<DailyPayOutEntry> dailyPayOuts = payOutMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate.toDate(), endDate.toDate());
        payOutMongoRepository.deleteAll(dailyPayOuts);
        dailyPayOuts = new ArrayList<>();
        Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        DailyPayOutEntry dailyPayOut = new DailyPayOutEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        shifts = filterSubshifts(shifts);
        if (shifts != null && !shifts.isEmpty()) {
            payOutCalculationService.getPayOutByInterval(unitPositionWithCtaDetailsDTO, interval, shifts, dailyPayOut);
            dailyPayOuts.add(dailyPayOut);

        }
        if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
            interval = new Interval(endDate, endDate.plusDays(1).withTimeAtStartOfDay());
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
            shifts = filterSubshifts(shifts);
            if (shifts != null && !shifts.isEmpty()) {
                payOutCalculationService.getPayOutByInterval(unitPositionWithCtaDetailsDTO, interval, shifts, dailyPayOut);
                dailyPayOuts.add(dailyPayOut);
            }
        }
        return dailyPayOuts;
    }


    public List<ShiftWithActivityDTO> filterSubshifts(List<ShiftWithActivityDTO> shifts){
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>(shifts.size());
        for (ShiftWithActivityDTO shift : shifts) {
            if (shift.getSubShift() != null && shift.getSubShift().getStartDate() != null) {
                ShiftWithActivityDTO shiftWithActivityDTO = new ShiftWithActivityDTO(shift.getStartDate(), shift.getEndDate(), shift.getActivity());
                shiftQueryResultWithActivities.add(shiftWithActivityDTO);
            } else {
                shiftQueryResultWithActivities.add(shift);
            }
        }
        return shiftQueryResultWithActivities;
    }


    @Deprecated
    public Boolean createBlankPayOut(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        DateTime startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        startDate = startDate.plusDays(1);//Todo this should be removed
        DateTime endDate = startDate.plusYears(3);
        List<DailyPayOutEntry> newDailyPayOutEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyPayOutEntry dailyPayOutEntry = new DailyPayOutEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() ? unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() : 0;
            dailyPayOutEntry.setTotalPayOutMin(-contractualMin);
            dailyPayOutEntry.setContractualMin(contractualMin);
            dailyPayOutEntry.setPayOutCTADistributionList(payOutCalculationService.getDistribution(unitPositionWithCtaDetailsDTO));
            newDailyPayOutEntries.add(dailyPayOutEntry);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyPayOutEntries.isEmpty()) {
            save(newDailyPayOutEntries);
        }
        return null;
    }

    public Boolean updateBlankPayOut(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        DateTime startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusYears(3);
        List<DailyPayOutEntry> newDailyPayOutEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyPayOutEntry dailyPayOutEntry = new DailyPayOutEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() ? unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() : 0;
            dailyPayOutEntry.setTotalPayOutMin(-contractualMin);
            dailyPayOutEntry.setContractualMin(contractualMin);
            dailyPayOutEntry.setPayOutCTADistributionList(payOutCalculationService.getDistribution(unitPositionWithCtaDetailsDTO));
            newDailyPayOutEntries.add(dailyPayOutEntry);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyPayOutEntries.isEmpty()) {
            save(newDailyPayOutEntries);
        }
        return null;
    }


    public Map<Long, List<ShiftWithActivityDTO>> getShiftsMapByUEPs(List<Long> unitPositionIds, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = new HashMap<>(unitPositionIds.size());
        unitPositionIds.forEach(uEPId -> {
            shiftsMap.put(uEPId, getShiftsByUEP(uEPId, shiftQueryResultWithActivities));
        });
        return shiftsMap;
    }


    public List<ShiftWithActivityDTO> getShiftsByUEP(Long unitPositionId, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftQueryResultWithActivities.forEach(s -> {
            if (s.getUnitPositionId().equals(unitPositionId)) {
                shifts.add(s);
            }
        });
        return shifts;
    }


    public List<CalculatedPayOutByDateDTO> getPayOutFromCurrentDateByUEP(Long unitPositonId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(145l);//payOutRestClient.getCTAbyUnitEmployementPosition(unitEmploymentPositonId);
        List<ShiftWithActivityDTO> shifts = null;//shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositonId,new Date(),new Date());
        return payOutCalculationService.getPayOutByDates(unitPositionWithCtaDetailsDTO, shifts, 365);
    }

    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = payOutRestClient.getCTAbyUnitEmployementPosition(unitPositionId);
        return unitPositionWithCtaDetailsDTO;
    }


    public PayOutDTO getAdvanceViewPayOut(Long unitId, Long unitPositionId, String query, Date startDate, Date endDate) {
        PayOutDTO payOutDTO = null;
        List<DailyPayOutEntry> dailyPayOuts = payOutMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        shiftQueryResultWithActivities = filterSubshifts(shiftQueryResultWithActivities);
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        int totalPayOutBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(unitPositionWithCtaDetailsDTO.getCountryId());
        if(new DateTime(startDate).isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()))){
            Interval interval = new Interval(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()),new DateTime(startDate));
            List<DailyPayOutEntry> dailyPayOutsBeforeStartDate = payOutMongoRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());

            int totalPayOut = payOutCalculationService.calculatePayOutForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyPayOutsBeforeStartDate,false);

            totalPayOutBeforeStartDate = dailyPayOutsBeforeStartDate != null && !dailyPayOutsBeforeStartDate.isEmpty()
                    ? dailyPayOutsBeforeStartDate.stream().mapToInt(dailyPayOut -> dailyPayOut.getTotalPayOutMin()).sum() : 0;
            totalPayOutBeforeStartDate = totalPayOutBeforeStartDate - totalPayOut;
        }
        payOutDTO = payOutCalculationService.getAdvanceViewPayOut(totalPayOutBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyPayOuts, unitPositionWithCtaDetailsDTO, timeTypeDTOS);
        //payOutDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return payOutDTO;
    }


    public PayOutDTO getOverviewPayOut(Long unitPositionId, Integer year) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        Interval interval = getIntervalByDateForOverviewPayOut(unitPositionWithCtaDetailsDTO, year);
        List<DailyPayOutEntry> dailyPayOutEntries = new ArrayList<>();
        if (interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyPayOutEntries = payOutMongoRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return payOutCalculationService.getOverviewPayOut(unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyPayOutEntries, unitPositionWithCtaDetailsDTO);
    }

    private Interval getIntervalByDateForOverviewPayOut(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Integer year) {

        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if (startDate.getYear() == DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay())) {
            startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        }
        if (startDate.getYear() != DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()))) {
            startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        }
        if (endDate.isAfter(new DateTime().plusDays(1).withTimeAtStartOfDay()) && endDate.getYear() == new DateTime().getYear()) {
            endDate = new DateTime().withTimeAtStartOfDay();
        }
        //endDate = endDate.plusMonths(5);//todo this should be removed
        return new Interval(startDate, endDate);
    }


    public boolean savePayOut() {
        Shift shift = new Shift();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime dt = formatter.parseDateTime("20/01/2018 03:41");
        shift.setStartDate(dt.toDate());
        shift.setEndDate(dt.plusHours(10).toDate());
        savePayOut(145l, shift);
        return true;

    }

}
