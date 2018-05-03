package com.kairos.activity.service.pay_out;

import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.pay_out.DailyPOEntry;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.pay_out.PayOutRepository;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.util.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class PayOutService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PayOutService.class);

    @Inject
    private TimeBankService timeBankService;
    @Inject
    private PayOutRepository payOutRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private PayOutCalculationService payOutCalculationService;

    public void savePayOut(Long unitPositionId, Shift shift){
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = timeBankService.getCostTimeAgreement(unitPositionId);
        List<DailyPOEntry> dailyPOEntries = new ArrayList<>();
        if(!dailyPOEntries.isEmpty())
            save(dailyPOEntries);
    }

    private List<DailyPOEntry> calculateDailyPayOut(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Shift shift, Long unitPositionId){
        unitPositionWithCtaDetailsDTO.setStaffId(shift.getStaffId());
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).withTimeAtStartOfDay();
        List<DailyPOEntry> dailyPayOuts = payOutRepository.findAllByUnitPositionAndDate(unitPositionId, startDate.toDate(), endDate.toDate());
        payOutRepository.deleteAll(dailyPayOuts);
        dailyPayOuts = new ArrayList<>();
        Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
        List<ShiftQueryResultWithActivity> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        DailyPOEntry dailyPOEntry = new DailyPOEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        shifts = timeBankService.filterSubshifts(shifts);
        if (shifts != null && !shifts.isEmpty()) {
            payOutCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts, dailyPOEntry);
            dailyPayOuts.add(dailyPOEntry);
        }
        if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
            interval = new Interval(endDate, endDate.plusDays(1).withTimeAtStartOfDay());
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
            shifts = timeBankService.filterSubshifts(shifts);
            if (shifts != null && !shifts.isEmpty()) {
                payOutCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts, dailyPOEntry);
                dailyPayOuts.add(dailyPOEntry);
            }
        }
        return dailyPayOuts;
    }







}
