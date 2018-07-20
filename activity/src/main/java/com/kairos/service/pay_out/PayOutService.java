package com.kairos.service.pay_out;


import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.rest_client.pay_out.PayOutRestClient;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.persistence.repository.pay_out.PayOutMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    @Inject private PayOutTransactionMongoRepository payOutTransactionMongoRepository;

    public void savePayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Activity activity) {
        ZonedDateTime startDate = DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.getZoneDateTime(shift.getEndDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        List<PayOut> payOuts = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
            PayOut payOut = new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), interval.getStartLocalDate());
            payOut = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getUnitPosition(), shift, activity, payOut);
            payOuts.add(payOut);
            startDate = startDate.plusDays(1);
        }
        if (!payOuts.isEmpty()) {
            save(payOuts);
        }
    }

    public void savePayOuts(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts, Activity activity) {
        List<PayOut> payOuts = new ArrayList<>();
        for (Shift shift : shifts) {
            ZonedDateTime startDate = DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = DateUtils.getZoneDateTime(shift.getEndDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS);
            while (startDate.isBefore(endDate)) {
                DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
                PayOut payOut = new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), interval.getStartLocalDate());
                payOut = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getUnitPosition(), shift, activity, payOut);
                payOuts.add(payOut);
                startDate = startDate.plusDays(1);
            }
        }
        if (!payOuts.isEmpty()) {
            save(payOuts);
        }
    }

    private boolean approvePayOutRequest(BigInteger payOutTransactionId){
        PayOutTransaction payOutTransaction = payOutTransactionMongoRepository.findOne(payOutTransactionId);
        PayOutTransaction approvedPayOutTransaction = new PayOutTransaction(payOutTransaction.getStaffId(),payOutTransaction.getUnitPositionId(), PayOutTrasactionStatus.APPROVED,payOutTransaction.getMinutes(), LocalDate.now());
        save(approvedPayOutTransaction);
        return true;
    }

    private void updatePayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Activity activity) {
        ZonedDateTime startDate = DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.getZoneDateTime(shift.getEndDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        List<PayOut> payOuts = payOutMongoRepository.findAllByUnitPositionAndDate(staffAdditionalInfoDTO.getId(), DateUtils.getDateByZoneDateTime(startDate), DateUtils.getDateByZoneDateTime(endDate));
        while (startDate.isBefore(endDate)) {
            DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
            Optional<PayOut> payOut = payOuts.stream().filter(p -> p.getDate().equals(interval.getStartLocalDate())).findFirst();
            if(payOut.isPresent()){
                PayOut updatedPayOut = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getUnitPosition(), shift, activity, payOut.get());
                payOuts.add(updatedPayOut);
            }
            startDate = startDate.plusDays(1);
        }
        if(!payOuts.isEmpty()){
            save(payOuts);
        }

    }

    public void deletePayOut(BigInteger shiftId){
        List<PayOut> payOuts = payOutMongoRepository.findAllByShiftId(shiftId);
        if(!payOuts.isEmpty()) {
            payOuts.forEach(p -> {
                p.setDeleted(true);
            });
            save(payOuts);
        }
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


}
