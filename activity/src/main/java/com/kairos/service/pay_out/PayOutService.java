package com.kairos.service.pay_out;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.time_bank.TimeBankService;
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

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

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
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject private TimeBankService timeBankService;
    @Inject private TimeTypeService timeTypeService;
    @Inject private TimeBankRepository timeBankRepository;
    @Inject private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject private PayOutTransactionMongoRepository payOutTransactionMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private UserIntegrationService userIntegrationService;



    /**
     *
     * @param employmentDetails
     * @param shifts
     * @param activities
     */
    public void savePayOuts(StaffEmploymentDetails employmentDetails, List<Shift> shifts, List<Activity> activities, Map<BigInteger,ActivityWrapper> activityWrapperMap, List<DayTypeDTO> dayTypeDTOS) {
        List<PayOutPerShift> payOutPerShifts = new ArrayList<>();
        if (activityWrapperMap==null) {
         activityWrapperMap=activities.stream().collect(Collectors.toMap(k -> k.getId(), v -> new ActivityWrapper(v, "")));
        }
        for (Shift shift : shifts) {
            ZonedDateTime startDate = DateUtils.asZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = DateUtils.asZoneDateTime(shift.getEndDate()).truncatedTo(ChronoUnit.DAYS);
            PayOutPerShift payOutPerShift = payOutRepository.findAllByShiftId(shift.getId());
            DateTimeInterval interval = new DateTimeInterval(startDate,endDate);
            payOutPerShift = isNullOrElse(payOutPerShift,new PayOutPerShift(shift.getId(), shift.getEmploymentId(), shift.getStaffId(), interval.getStartLocalDate(),shift.getUnitId()));
            payOutPerShift = payOutCalculationService.calculateAndUpdatePayOut(interval, employmentDetails, shift, activityWrapperMap, payOutPerShift,dayTypeDTOS);
            payOutPerShifts.add(payOutPerShift);
        }
        if (!payOutPerShifts.isEmpty()) {
            payOutRepository.saveEntities(payOutPerShifts);
        }
    }


    /**
     *
     * @param payOutTransactionId
     * @return boolean
     */
    public boolean approvePayOutRequest(BigInteger payOutTransactionId){
        PayOutTransaction payOutTransaction = payOutTransactionMongoRepository.findOne(payOutTransactionId);
        PayOutTransaction approvedPayOutTransaction = new PayOutTransaction(payOutTransaction.getStaffId(),payOutTransaction.getEmploymentId(), PayOutTrasactionStatus.APPROVED,payOutTransaction.getMinutes(), LocalDate.now());
        save(approvedPayOutTransaction);
        PayOutPerShift payOutPerShift = new PayOutPerShift(payOutTransaction.getEmploymentId(),payOutTransaction.getStaffId(),payOutTransaction.getMinutes(),payOutTransaction.getDate());
        PayOutPerShift lastPayOutPerShift = payOutRepository.findLastPayoutByEmploymentId(payOutTransaction.getEmploymentId(),DateUtils.asDate(payOutTransaction.getDate()));
        if(lastPayOutPerShift !=null) {
            payOutPerShift.setPayoutBeforeThisDate(lastPayOutPerShift.getPayoutBeforeThisDate() + lastPayOutPerShift.getTotalPayOutMinutes());
        }
        payOutRepository.updatePayOut(payOutPerShift.getEmploymentId(),(int) payOutPerShift.getTotalPayOutMinutes());
        save(payOutPerShift);
        return true;
    }

    /**
     *
     * @param staffId
     * @param employmentId
     * @param amount
     * @return boolean
     */
    public boolean requestPayOut(Long staffId,Long employmentId,int amount){
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getCTAbyUnitEmployementPosition(employmentId);
        if(employmentWithCtaDetailsDTO ==null){
            exceptionService.invalidRequestException("message.unit.position");
        }
        PayOutTransaction requestPayOutTransaction = new PayOutTransaction(staffId,employmentId, PayOutTrasactionStatus.REQUESTED,amount, LocalDate.now());
        save(requestPayOutTransaction);
        return true;

    }

    /**
     *
     * @param staffAdditionalInfoDTO
     * @param shift
     * @param activityWrapperMap
     */
    public void updatePayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ZonedDateTime startDate = DateUtils.asZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.asZoneDateTime(shift.getEndDate()).truncatedTo(ChronoUnit.DAYS);
        PayOutPerShift payOutPerShift = payOutRepository.findAllByShiftId(shift.getId());
        DateTimeInterval interval = new DateTimeInterval(startDate,endDate);
        payOutPerShift = isNullOrElse(payOutPerShift,new PayOutPerShift(shift.getId(), shift.getEmploymentId(), shift.getStaffId(), interval.getStartLocalDate(),shift.getUnitId()));
        payOutPerShift = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getEmployment(), shift, activityWrapperMap, payOutPerShift,staffAdditionalInfoDTO.getDayTypes());
        payOutRepository.save(payOutPerShift);
    }

    /**
     *
     * @param shiftId
     */
    public void deletePayOut(BigInteger shiftId){
        PayOutPerShift payOutPerShift = payOutRepository.findAllByShiftId(shiftId);
        if(isNotNull(payOutPerShift)) {
            payOutPerShift.setDeleted(true);
            payOutRepository.save(payOutPerShift);
        }
    }


    /**
     *
     * @param employmentIds
     * @param shiftQueryResultWithActivities
     * @return Map<Long, List<ShiftWithActivityDTO>>
     */
    public Map<Long, List<ShiftWithActivityDTO>> getShiftsMapByUEPs(List<Long> employmentIds, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = new HashMap<>(employmentIds.size());
        employmentIds.forEach(uEPId -> {
            shiftsMap.put(uEPId, getShiftsByUEP(uEPId, shiftQueryResultWithActivities));
        });
        return shiftsMap;
    }

    /**
     *
     * @param employmentId
     * @param shiftQueryResultWithActivities
     * @return List<ShiftWithActivityDTO>
     */
    private List<ShiftWithActivityDTO> getShiftsByUEP(Long employmentId, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftQueryResultWithActivities.forEach(s -> {
            if (s.getEmploymentId().equals(employmentId)) {
                shifts.add(s);
            }
        });
        return shifts;
    }


}
