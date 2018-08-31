package com.kairos.service.shift;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;

/**
 * CreatedBy vipulpandey on 31/8/18
 **/
@Service
public class ShiftSickService {
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffRestClient staffRestClient;
    @Inject
    private
    CostTimeAgreementRepository costTimeAgreementRepository;

    public Long createSicknessShiftsOfStaff(Long unitId, BigInteger activityId, Long staffId) {
        ActivityWrapper activityWrapper = activityRepository.findActivityAndTimeTypeByActivityId(activityId);
        Activity activity = activityWrapper.getActivity();
        if (!Optional.ofNullable(activity).isPresent() ) {
            exceptionService.dataNotFoundByIdException("message.activity.id", activity);
        }
        if (!activity.getRulesActivityTab().isAllowedAutoAbsence() ) {
            exceptionService.actionNotPermittedException("activity.notEligible.for.absence", activity.getName());
        }
        StaffUnitPositionDetails staffUnitPositionDetails = staffRestClient.verifyUnitEmploymentOfStaff(staffId, ORGANIZATION);
        if (!Optional.ofNullable(staffUnitPositionDetails).isPresent()){
            exceptionService.dataNotFoundByIdException("message.staffUnitPosition.notFound");
        }
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId(staffUnitPositionDetails.getId(), DateUtils.getDateFromLocalDate(null));
        staffUnitPositionDetails.setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());

        return unitId;
    }
}
