package com.kairos.service.gap_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.dto.activity.gap_settings.GapSettingsDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.gap_settings.GapFillingScenario;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.gap_settings.GapSettings;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.gap_settings.GapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.gap_settings.GapFillingScenario.*;
import static com.kairos.enums.phase.PhaseDefaultName.REQUEST;

@Service
public class GapSettingsService {
    @Inject
    private GapSettingsMongoRepository gapSettingsMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public GapSettingsDTO createGapSettings(GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        validateGapSetting(gapSettingsDTO, forCountry);
        GapSettings gapSettings = ObjectMapperUtils.copyPropertiesByMapper(gapSettingsDTO, GapSettings.class);
        gapSettingsMongoRepository.save(gapSettings);
        gapSettingsDTO.setId(gapSettings.getId());
        return gapSettingsDTO;
    }

    public GapSettingsDTO updateGapSettings(GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        GapSettings gapSettings = gapSettingsMongoRepository.findOne(gapSettingsDTO.getId());
        if(isNull(gapSettings)){
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        validateGapSetting(gapSettingsDTO, forCountry);
        gapSettings = ObjectMapperUtils.copyPropertiesByMapper(gapSettingsDTO, GapSettings.class);
        gapSettingsMongoRepository.save(gapSettings);
        return gapSettingsDTO;
    }

    private void validateGapSetting(GapSettingsDTO gapSettingsDTO, boolean forCountry) {
        GapSettings gapSettings;
        if(forCountry) {
            gapSettings = gapSettingsMongoRepository.findByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(gapSettingsDTO.getCountryId(), gapSettingsDTO.getOrganizationTypeId(), gapSettingsDTO.getOrganizationSubTypeId(), gapSettingsDTO.getPhaseId(), gapSettingsDTO.getGapFillingScenario().toString());
        } else {
            gapSettings = gapSettingsMongoRepository.findByUnitIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(gapSettingsDTO.getUnitId(), gapSettingsDTO.getOrganizationTypeId(), gapSettingsDTO.getOrganizationSubTypeId(), gapSettingsDTO.getPhaseId(), gapSettingsDTO.getGapFillingScenario().toString());
        }
        if(isNotNull(gapSettings) && !gapSettingsDTO.getId().equals(gapSettings.getId())){
            exceptionService.duplicateDataException("Duplicate configuration for gap setting");
        }
    }

    public List<GapSettingsDTO> getAllGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<GapSettings> gapSettingsList;
        if(forCountry){
            gapSettingsList = gapSettingsMongoRepository.getAllByCountryId(countryOrUnitId);
        } else {
            gapSettingsList = gapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(gapSettingsList, GapSettingsDTO.class);
    }

    public ShiftDTO adjustGapByActivity(ShiftDTO shiftDTO, Shift shift, Phase phase, StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        if(gapCreated(shiftDTO,shift)){
            ShiftActivityDTO [] activities=getActivitiesAroundGap(shiftDTO);
            ShiftActivityDTO shiftActivityBeforeGap = activities[0];
            ShiftActivityDTO shiftActivityAfterGap = activities[1];
            GapFillingScenario gapFillingScenario=getGapFillingScenario(shiftActivityBeforeGap,shiftActivityAfterGap);
            GapSettings gapSettings=gapSettingsMongoRepository.findByUnitIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndPhaseIdAndGapFillingScenario(shiftDTO.getUnitId(),staffAdditionalInfoDTO.getOrganizationType().getId(),staffAdditionalInfoDTO.getOrganizationSubType().getId(),phase.getId(),gapFillingScenario.toString());
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

    private GapFillingScenario getGapFillingScenario(ShiftActivityDTO shiftActivityBeforeGap , ShiftActivityDTO shiftActivityAfterGap){
        if(shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() && shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()){
            return PRODUCTIVE_TYPE_ON_BOTH_SIDE;
        }
        else if(shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() || shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()){
            return ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE;
        }
        return NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE;
    }

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnBothSide(Phase phase, GapSettings gapSettings, ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, StaffAdditionalInfoDTO staffAdditionalInfoDTO){
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

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnOneSide(Phase phase, GapSettings gapSettings){
        return null;
    }

    private ShiftActivityDTO getApplicableActivityForNonProductiveTypeOnBothSide(Phase phase, GapSettings gapSettings){
        return null;
    }






}
