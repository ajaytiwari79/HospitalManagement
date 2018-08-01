package com.kairos.service.pay_out;


import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.rest_client.pay_out.PayOutRestClient;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.DateTimeInterval;
import com.kairos.util.DateUtils;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
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
import java.util.stream.Collectors;

/*
* Created By Mohit Shakya
*
* */
@Transactional
@Service
public class PayOutService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(PayOutService.class);

    @Inject
    private PayOutRepository payOutRepository;
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


    /**
     *
     * @param staffAdditionalInfoDTO
     * @param shift
     * @param activity
     */
    public void savePayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Activity activity) {
        ZonedDateTime startDate = DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.getZoneDateTime(shift.getEndDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        List<PayOut> payOuts = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
            PayOut payOut = new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), interval.getStartLocalDate(),shift.getUnitId());
            PayOut lastPayOut = payOutRepository.findLastPayoutByUnitPositionId(shift.getUnitPositionId(),DateUtils.getDateByZoneDateTime(startDate));
            if(lastPayOut!=null) {
                payOut.setPayoutBeforeThisDate(lastPayOut.getPayoutBeforeThisDate() + lastPayOut.getTotalPayOutMin());
            }
            payOut = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getUnitPosition(), shift, activity, payOut);
            if(payOut.getTotalPayOutMin()>0) {
                payOutRepository.updatePayOut(payOut.getUnitPositionId(),(int) payOut.getTotalPayOutMin());
                payOuts.add(payOut);
            }
            startDate = startDate.plusDays(1);
        }
        if (!payOuts.isEmpty()) {
            save(payOuts);
        }
    }


    /**
     *
     * @param staffAdditionalInfoDTO
     * @param shifts
     * @param activities
     */
    public void savePayOuts(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts, List<Activity> activities) {
        List<PayOut> payOuts = new ArrayList<>();
        Map<BigInteger,Activity> activityMap = activities.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        for (Shift shift : shifts) {
            ZonedDateTime startDate = DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = DateUtils.getZoneDateTime(shift.getEndDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS);
            while (startDate.isBefore(endDate)) {
                DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
                PayOut payOut = new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), interval.getStartLocalDate(),shift.getUnitId());
                PayOut lastPayOut = payOutRepository.findLastPayoutByUnitPositionId(shift.getUnitPositionId(),DateUtils.getDateByZoneDateTime(startDate));
                if(lastPayOut!=null) {
                    payOut.setPayoutBeforeThisDate(lastPayOut.getPayoutBeforeThisDate() + lastPayOut.getTotalPayOutMin());
                }
                Activity activity = activityMap.get(shift.getActivityId());
                payOut = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getUnitPosition(), shift, activity, payOut);
                if(payOut.getTotalPayOutMin()>0) {
                    payOutRepository.updatePayOut(payOut.getUnitPositionId(),(int) payOut.getTotalPayOutMin());
                    payOuts.add(payOut);
                }
                startDate = startDate.plusDays(1);
            }
        }
        if (!payOuts.isEmpty()) {
            save(payOuts);
        }
    }

    /**
     *
     * @param payOutTransactionId
     * @return boolean
     */
    public boolean approvePayOutRequest(BigInteger payOutTransactionId){
        PayOutTransaction payOutTransaction = payOutTransactionMongoRepository.findOne(payOutTransactionId);
        PayOutTransaction approvedPayOutTransaction = new PayOutTransaction(payOutTransaction.getStaffId(),payOutTransaction.getUnitPositionId(), PayOutTrasactionStatus.APPROVED,payOutTransaction.getMinutes(), LocalDate.now());
        save(approvedPayOutTransaction);
        PayOut payOut = new PayOut(payOutTransaction.getUnitPositionId(),payOutTransaction.getStaffId(),payOutTransaction.getMinutes(),payOutTransaction.getDate());
        PayOut lastPayOut = payOutRepository.findLastPayoutByUnitPositionId(payOutTransaction.getUnitPositionId(),DateUtils.getDateFromLocalDate(payOutTransaction.getDate()));
        if(lastPayOut!=null) {
            payOut.setPayoutBeforeThisDate(lastPayOut.getPayoutBeforeThisDate() + lastPayOut.getTotalPayOutMin());
        }
        payOutRepository.updatePayOut(payOut.getUnitPositionId(),(int) payOut.getTotalPayOutMin());
        save(payOut);
        return true;
    }

    /**
     *
     * @param staffAdditionalInfoDTO
     * @param shift
     * @param activity
     */
    public void updatePayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Activity activity) {
        ZonedDateTime startDate = DateUtils.getZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.getZoneDateTime(shift.getEndDate()).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        List<PayOut> payOuts = payOutRepository.findAllByShiftId(shift.getId());
        while (startDate.isBefore(endDate)) {
            DateTimeInterval interval = new DateTimeInterval(startDate, startDate.plusDays(1));
            PayOut payOut = payOuts.stream().filter(p -> p.getDate().equals(interval.getStartLocalDate())).findFirst().get();
            PayOut lastPayOut = payOutRepository.findLastPayoutByUnitPositionId(shift.getUnitPositionId(),DateUtils.getDateByZoneDateTime(startDate));
            if(payOut==null){
                payOut = new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), interval.getStartLocalDate(),shift.getUnitId());
            }
            if(lastPayOut!=null) {
                payOut.setPayoutBeforeThisDate(lastPayOut.getPayoutBeforeThisDate() + lastPayOut.getTotalPayOutMin());
            }
            payOut = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getUnitPosition(), shift, activity, payOut);
            if(payOut.getTotalPayOutMin()>0) {
                payOutRepository.updatePayOut(payOut.getUnitPositionId(),(int) payOut.getTotalPayOutMin());
                payOuts.add(payOut);
            }
            startDate = startDate.plusDays(1);
        }
        if(!payOuts.isEmpty()){
            save(payOuts);
        }

    }

    /**
     *
     * @param shiftId
     */
    public void deletePayOut(BigInteger shiftId){
        List<PayOut> payOuts = payOutRepository.findAllByShiftId(shiftId);
        if(!payOuts.isEmpty()) {
            payOuts.forEach(p -> {
                p.setDeleted(true);
            });
            save(payOuts);
        }
    }


    /**
     *
     * @param unitPositionIds
     * @param shiftQueryResultWithActivities
     * @return Map<Long, List<ShiftWithActivityDTO>>
     */
    public Map<Long, List<ShiftWithActivityDTO>> getShiftsMapByUEPs(List<Long> unitPositionIds, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = new HashMap<>(unitPositionIds.size());
        unitPositionIds.forEach(uEPId -> {
            shiftsMap.put(uEPId, getShiftsByUEP(uEPId, shiftQueryResultWithActivities));
        });
        return shiftsMap;
    }

    /**
     *
     * @param unitPositionId
     * @param shiftQueryResultWithActivities
     * @return List<ShiftWithActivityDTO>
     */
    private List<ShiftWithActivityDTO> getShiftsByUEP(Long unitPositionId, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftQueryResultWithActivities.forEach(s -> {
            if (s.getUnitPositionId().equals(unitPositionId)) {
                shifts.add(s);
            }
        });
        return shifts;
    }


}
