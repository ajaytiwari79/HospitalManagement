package com.kairos.service.auto_gap_fill_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.gap_settings.AutoFillGapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.dto.user.access_permission.AccessGroupRole.MANAGEMENT;
import static com.kairos.dto.user.access_permission.AccessGroupRole.STAFF;
import static com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario.*;
import static com.kairos.enums.phase.PhaseDefaultName.REQUEST;

@Service
public class AutoFillGapSettingsService {
    @Inject
    private AutoFillGapSettingsMongoRepository autoFillGapSettingsMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public AutoFillGapSettingsDTO createAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        validateGapSetting(autoFillGapSettingsDTO, forCountry);
        AutoFillGapSettings autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        autoFillGapSettingsDTO.setId(autoFillGapSettings.getId());
        return autoFillGapSettingsDTO;
    }

    public AutoFillGapSettingsDTO updateAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsDTO.getId());
        if(isNull(autoFillGapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        validateGapSetting(autoFillGapSettingsDTO, forCountry);
        autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return autoFillGapSettingsDTO;
    }

    private void validateGapSetting(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings;
        if(forCountry) {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForCountry(autoFillGapSettingsDTO.getCountryId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString(), autoFillGapSettingsDTO.getId(), autoFillGapSettingsDTO.getGapApplicableFor().toString());
        } else {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(autoFillGapSettingsDTO.getUnitId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId(), autoFillGapSettingsDTO.getAutoGapFillingScenario().toString(), autoFillGapSettingsDTO.getId(), autoFillGapSettingsDTO.getGapApplicableFor().toString());
        }
        if(isNotNull(autoFillGapSettings)){
            exceptionService.duplicateDataException("Duplicate configuration for gap setting");
        }
    }

    public List<AutoFillGapSettingsDTO> getAllAutoFillGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<AutoFillGapSettings> autoFillGapSettingsList;
        if(forCountry){
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByCountryId(countryOrUnitId);
        } else {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(autoFillGapSettingsList, AutoFillGapSettingsDTO.class);
    }

    public Boolean deleteAutoFillGapSettings(BigInteger autoFillGapSettingsId) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsId);
        if(isNull(autoFillGapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        autoFillGapSettings.setDeleted(true);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return true;
    }

    public ShiftDTO adjustGapByActivity(ShiftDTO shiftDTO, Shift shift, Phase phase, StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        if(gapCreated(shiftDTO,shift)){
            ShiftActivityDTO[] activities=getActivitiesAroundGap(shiftDTO);
            ShiftActivityDTO shiftActivityBeforeGap = activities[0];
            ShiftActivityDTO shiftActivityAfterGap = activities[1];
            AutoGapFillingScenario gapFillingScenario=getGapFillingScenario(shiftActivityBeforeGap,shiftActivityAfterGap);
            AutoFillGapSettings gapSettings=autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(shiftDTO.getUnitId(),staffAdditionalInfoDTO.getOrganizationType().getId(),staffAdditionalInfoDTO.getOrganizationSubType().getId(),phase.getId(),gapFillingScenario.toString(),null,staffAdditionalInfoDTO.getRoles().contains(MANAGEMENT)?MANAGEMENT.toString():STAFF.toString());
            ShiftActivityDTO shiftActivityDTO;
            switch (gapFillingScenario){
                case PRODUCTIVE_TYPE_ON_BOTH_SIDE:
                    shiftActivityDTO= getApplicableActivityForProductiveTypeOnBothSide(phase,gapSettings,shiftActivityBeforeGap,shiftActivityAfterGap,staffAdditionalInfoDTO);
                    break;
                case ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE:
                    shiftActivityDTO=  getApplicableActivityForProductiveTypeOnOneSide(phase,gapSettings);
                    break;
                default:
                    shiftActivityDTO= getApplicableActivityForNonProductiveTypeOnBothSide(phase,gapSettings);
                    break;
            }

            for(int index=0;index<shiftDTO.getActivities().size()-1;index++){
                if(!shiftDTO.getActivities().get(index).getEndDate().equals(shiftDTO.getActivities().get(index+1).getStartDate())){
                    shiftDTO.getActivities().add(index+1,shiftActivityDTO);
                }
            }



        }
        return shiftDTO;
    }

    private boolean gapCreated(ShiftDTO shiftDTO,Shift shift){
        return shift.getActivities().size() > shiftDTO.getActivities().size() && shift.getStartDate().equals(shiftDTO.getStartDate()) && shift.getEndDate().equals(shiftDTO.getEndDate());
    }

    private ShiftActivityDTO [] getActivitiesAroundGap(ShiftDTO shiftDTO){
        ShiftActivityDTO shiftActivityBeforeGap = null;
        ShiftActivityDTO shiftActivityAfterGap = null;
        for(int i=0;i<shiftDTO.getActivities().size();i++){
            if(!shiftDTO.getActivities().get(i).getEndDate().equals(shiftDTO.getActivities().get(i+1).getStartDate())){
                shiftActivityBeforeGap=shiftDTO.getActivities().get(i);
                shiftActivityAfterGap=shiftDTO.getActivities().get(i+1);
                break;
            }
        }
        return new ShiftActivityDTO[]{shiftActivityBeforeGap, shiftActivityAfterGap};
    }

    private AutoGapFillingScenario getGapFillingScenario(ShiftActivityDTO shiftActivityBeforeGap , ShiftActivityDTO shiftActivityAfterGap){
        if(shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() && shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()){
            return PRODUCTIVE_TYPE_ON_BOTH_SIDE;
        }
        else if(shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() || shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()){
            return ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE;
        }
        return NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE;
    }

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnBothSide(Phase phase, AutoFillGapSettings gapSettings, ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        ShiftActivityDTO shiftActivityDTO = null;
        if(REQUEST.equals(phase.getPhaseEnum())){
            if(UserContext.getUserDetails().isManagement()){
                shiftActivityDTO=beforeGap.getActivity().getActivityPriority().getSequence()<afterGap.getActivity().getActivityPriority().getSequence()?beforeGap:afterGap;
            } else {
                if(staffAdditionalInfoDTO.getMainTeamActivities().contains(beforeGap.getActivityId())){
                    shiftActivityDTO=beforeGap;
                } else if(staffAdditionalInfoDTO.getMainTeamActivities().contains(afterGap.getActivityId())){
                    shiftActivityDTO=afterGap;
                } else {
                    shiftActivityDTO=beforeGap.getActivity().getActivityPriority().getSequence()<afterGap.getActivity().getActivityPriority().getSequence()?beforeGap:afterGap;
                }
            }
        }
        return shiftActivityDTO;

    }

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnOneSide(Phase phase, AutoFillGapSettings gapSettings){
        return null;
    }

    private ShiftActivityDTO getApplicableActivityForNonProductiveTypeOnBothSide(Phase phase, AutoFillGapSettings gapSettings){
        return null;
    }
}
