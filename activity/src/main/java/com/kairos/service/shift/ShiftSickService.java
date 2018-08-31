package com.kairos.service.shift;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;

/**
 * CreatedBy vipulpandey on 31/8/18
 **/
@Service
public class ShiftSickService extends MongoBaseService {
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRestClient staffRestClient;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    PlanningPeriodMongoRepository planningPeriodMongoRepository;


    public Long createSicknessShiftsOfStaff(Long unitId, BigInteger activityId, Long staffId) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(activityId);
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activity);
        }
        if (!activity.getRulesActivityTab().isAllowedAutoAbsence()) {
            exceptionService.actionNotPermittedException("activity.notEligible.for.absence", activity.getName());
        }
        StaffUnitPositionDetails staffUnitPositionDetails = staffRestClient.verifyUnitEmploymentOfStaff(staffId, ORGANIZATION);
        if (!Optional.ofNullable(staffUnitPositionDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staffUnitPosition.notFound");
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffUnitPositionDetails.getId(), DateUtils.getDateFromLocalDate(null));
        if (!Optional.ofNullable(ctaResponseDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.cta.notFound");
        }
        staffUnitPositionDetails.setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findCurrentDatePlanningPeriod(unitId, DateUtils.getCurrentLocalDate(), DateUtils.getCurrentLocalDate());
        if (!Optional.ofNullable(planningPeriod).isPresent()) {
            exceptionService.actionNotPermittedException("message.periodsetting.notFound");
        }
        short shiftNeedsToAddForDays = activity.getRulesActivityTab().getRecurrenceDays();
        List<Shift> shifts = new ArrayList<>();
        while (shiftNeedsToAddForDays != 0 && activity.getRulesActivityTab().getRecurrenceTimes() > 1) {
            shiftNeedsToAddForDays--;
            Date startDate = DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, 9);
            Date endDate = DateUtils.getDateAfterDaysWithTime(shiftNeedsToAddForDays, 18);
            shifts.add(new Shift(startDate, endDate, staffId, activityId, activity.getName(), staffUnitPositionDetails.getId(), unitId));
        }
        if (!shifts.isEmpty())
            save(shifts);
        return unitId;
    }
}
