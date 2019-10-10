package com.kairos.service.pay_out;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.payout.PayOutTrasactionStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.pay_out.PayOutPerShiftCTADistribution;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
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

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_EMPLOYMENT_ABSENT;

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
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private PayOutTransactionMongoRepository payOutTransactionMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ShiftService shiftService;


    /**
     * @param employmentDetails
     * @param shifts
     * @param activities
     */
    public void savePayOuts(StaffEmploymentDetails employmentDetails, List<Shift> shifts, List<Activity> activities, Map<BigInteger, ActivityWrapper> activityWrapperMap, List<DayTypeDTO> dayTypeDTOS) {
        if (isNull(activityWrapperMap)) {
            activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getId(), v -> new ActivityWrapper(v, "")));
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(employmentDetails,dayTypeDTOS);
        for (Shift shift : shifts) {
            updatePayOut(staffAdditionalInfoDTO,shift,activityWrapperMap);
        }
    }


    /**
     * @param payOutTransactionId
     * @return boolean
     */
    public boolean approvePayOutRequest(BigInteger payOutTransactionId) {
        PayOutTransaction payOutTransaction = payOutTransactionMongoRepository.findOne(payOutTransactionId);
        PayOutTransaction approvedPayOutTransaction = new PayOutTransaction(payOutTransaction.getStaffId(), payOutTransaction.getEmploymentId(), PayOutTrasactionStatus.APPROVED, payOutTransaction.getMinutes(), LocalDate.now());
        save(approvedPayOutTransaction);
        PayOutPerShift payOutPerShift = new PayOutPerShift(payOutTransaction.getEmploymentId(), payOutTransaction.getStaffId(), payOutTransaction.getMinutes(), payOutTransaction.getDate());
        PayOutPerShift lastPayOutPerShift = payOutRepository.findLastPayoutByEmploymentId(payOutTransaction.getEmploymentId(), DateUtils.asDate(payOutTransaction.getDate()));
        if (lastPayOutPerShift != null) {
            payOutPerShift.setPayoutBeforeThisDate(lastPayOutPerShift.getPayoutBeforeThisDate() + lastPayOutPerShift.getTotalPayOutMinutes());
        }
        payOutRepository.updatePayOut(payOutPerShift.getEmploymentId(), (int) payOutPerShift.getTotalPayOutMinutes());
        payOutRepository.save(payOutPerShift);
        return true;
    }

    /**
     * @param staffId
     * @param employmentId
     * @param amount
     * @return boolean
     */
    public boolean requestPayOut(Long staffId, Long employmentId, int amount) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        if (employmentWithCtaDetailsDTO == null) {
            exceptionService.invalidRequestException(MESSAGE_EMPLOYMENT_ABSENT);
        }
        PayOutTransaction requestPayOutTransaction = new PayOutTransaction(staffId, employmentId, PayOutTrasactionStatus.REQUESTED, amount, LocalDate.now());
        save(requestPayOutTransaction);
        return true;

    }

    /**
     * @param staffAdditionalInfoDTO
     * @param shift
     * @param activityWrapperMap
     */
    public void updatePayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        updateActivityWrapper(shift,activityWrapperMap);
        ZonedDateTime startDate = DateUtils.asZoneDateTime(shift.getStartDate()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.asZoneDateTime(shift.getEndDate()).truncatedTo(ChronoUnit.DAYS);
        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
        ShiftWithActivityDTO shiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shift,activityWrapperMap);
        updatePayoutByShift(staffAdditionalInfoDTO, shiftWithActivityDTO, activityWrapperMap, interval);
        updatePayoutDetailInShift(shiftWithActivityDTO,shift);
        if(isNotNull(shift.getDraftShift())){
            ShiftWithActivityDTO draftShiftWithActivityDTO = buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shift.getDraftShift(),activityWrapperMap);
            updatePayoutByShift(staffAdditionalInfoDTO, draftShiftWithActivityDTO, activityWrapperMap, interval);
            updatePayoutDetailInShift(draftShiftWithActivityDTO,shift.getDraftShift());
        }
        shiftMongoRepository.save(shift);
    }

    private void updatePayoutDetailInShift(ShiftWithActivityDTO shiftWithActivityDTO,Shift shift){
        for (int index = 0; index < shift.getActivities().size(); index++) {
            ShiftActivity shiftActivity = shift.getActivities().get(index);
            ShiftActivityDTO shiftActivityDTO = shiftWithActivityDTO.getActivities().get(index);
            shiftActivity.setPayoutCtaBonusMinutes(shiftActivityDTO.getPayoutCtaBonusMinutes());
            shiftActivity.setPlannedMinutesOfPayout(shiftActivityDTO.getPlannedMinutesOfPayout());
            shiftActivity.setScheduledMinutesOfPayout(shiftActivityDTO.getScheduledMinutesOfPayout());
            shiftActivity.setPayoutPerShiftCTADistributions(ObjectMapperUtils.copyPropertiesOfListByMapper(shiftActivityDTO.getPayoutPerShiftCTADistributions(), PayOutPerShiftCTADistribution.class));
        }
        shift.setPlannedMinutesOfPayout(shiftWithActivityDTO.getPlannedMinutesOfPayout());
    }

    public ShiftWithActivityDTO buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftWithActivityDTO.class);
        shift.getActivities().forEach(shiftActivityDTO ->
                shiftActivityDTO.setActivityName(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity().getName())
        );
        shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
            shiftActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class));
            shiftActivityDTO.setTimeType(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getTimeType());
            shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> {
                childActivityDTO.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityDTO.getActivityId()).getActivity(), ActivityDTO.class));
                childActivityDTO.setTimeType(activityWrapperMap.get(childActivityDTO.getActivityId()).getTimeType());
            });
        });
        shiftWithActivityDTO.setStartDate(shift.getActivities().get(0).getStartDate());
        shiftWithActivityDTO.setEndDate(shift.getActivities().get(shift.getActivities().size() - 1).getEndDate());
        return shiftWithActivityDTO;
    }

    private void updatePayoutByShift(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftWithActivityDTO shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, DateTimeInterval interval) {
        PayOutPerShift payOutPerShift = payOutRepository.findAllByShiftId(shift.getId());
        payOutPerShift = isNullOrElse(payOutPerShift, new PayOutPerShift(shift.getId(), shift.getEmploymentId(), shift.getStaffId(), interval.getStartLocalDate(), shift.getUnitId()));
        payOutPerShift = payOutCalculationService.calculateAndUpdatePayOut(interval, staffAdditionalInfoDTO.getEmployment(), shift, activityWrapperMap, payOutPerShift, staffAdditionalInfoDTO.getDayTypes());
        payOutRepository.save(payOutPerShift);
    }

    public Map<BigInteger, ActivityWrapper> updateActivityWrapper(Shift shift,Map<BigInteger, ActivityWrapper> activityWrapperMap){
        Set<BigInteger> activityIds = new HashSet<>();
        Map<BigInteger, ActivityWrapper> updatedActivityWrapperMap = new HashMap<>();
        activityIds.addAll(getActivityIdsByShift(shift, activityWrapperMap));
        if(isNotNull(shift.getDraftShift())) {
            activityIds.addAll(getActivityIdsByShift(shift.getDraftShift(), activityWrapperMap));
        }
        if(isCollectionNotEmpty(activityIds)){
            updatedActivityWrapperMap = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds).stream().collect(Collectors.toMap(k->k.getActivity().getId(),v->v));
            activityWrapperMap.putAll(updatedActivityWrapperMap);
        }
        return updatedActivityWrapperMap;
    }

    private Set<BigInteger> getActivityIdsByShift(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                if(!activityWrapperMap.containsKey(childActivity.getActivityId())){
                    activityIds.add(childActivity.getActivityId());
                }
            }
        }
        return activityIds;
    }

    /**
     * @param shiftId
     */
    public void deletePayOut(BigInteger shiftId) {
        PayOutPerShift payOutPerShift = payOutRepository.findAllByShiftId(shiftId);
        if (isNotNull(payOutPerShift)) {
            payOutPerShift.setDeleted(true);
            payOutRepository.save(payOutPerShift);
        }
    }

    public void savePayout(List<PayOutPerShift> payOutPerShifts){
        payOutRepository.saveEntities(payOutPerShifts);
    }

    public List<PayOutPerShift> findAllByEmploymentsAndDate(Set<Long> employmentIds, Date startDate, Date endDate){
        return payOutRepository.findAllByEmploymentsAndDate(employmentIds,startDate,endDate);
    }
}
