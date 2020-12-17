package com.kairos.service.auto_gap_fill_settings;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.auto_gap_fill_settings.AutoFillGapSettingsRule;
import com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario;
import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.gap_settings.AutoFillGapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftValidatorService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ShiftValidatorService staffingLevelService;
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
        if (isNull(autoFillGapSettings)) {
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        validateGapSetting(autoFillGapSettingsDTO, forCountry);
        autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return autoFillGapSettingsDTO;
    }

    private void validateGapSetting(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings;
        if (forCountry) {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForCountry(autoFillGapSettingsDTO.getCountryId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId());
        } else {
            autoFillGapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(autoFillGapSettingsDTO.getUnitId(), autoFillGapSettingsDTO.getOrganizationTypeId(), autoFillGapSettingsDTO.getOrganizationSubTypeId(), autoFillGapSettingsDTO.getPhaseId());
        }
        if (isNotNull(autoFillGapSettings)) {
            exceptionService.duplicateDataException("Duplicate configuration for gap setting");
        }
    }

    public List<AutoFillGapSettingsDTO> getAllAutoFillGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<AutoFillGapSettings> autoFillGapSettingsList;
        if (forCountry) {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByCountryId(countryOrUnitId);
        } else {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllByUnitId(countryOrUnitId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(autoFillGapSettingsList, AutoFillGapSettingsDTO.class);
    }

    public Boolean deleteAutoFillGapSettings(BigInteger autoFillGapSettingsId) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsId);
        if (isNull(autoFillGapSettings)) {
            exceptionService.dataNotFoundByIdException("gap filling setting not found");
        }
        autoFillGapSettings.setDeleted(true);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        return true;
    }

    public void adjustGapByActivity(ShiftDTO shiftDTO, Shift shift, Phase phase, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        if (gapCreated(shiftDTO, shift)) {
            ShiftActivityDTO[] activities = getActivitiesAroundGap(shiftDTO);
            ShiftActivityDTO shiftActivityBeforeGap = activities[0];
            ShiftActivityDTO shiftActivityAfterGap = activities[1];
            Set<BigInteger> allProductiveActivityIds = staffAdditionalInfoDTO.getTeamsData().stream().flatMap(k -> k.getActivityIds().stream()).collect(Collectors.toSet());
            List<ActivityWrapper> activityList = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(allProductiveActivityIds);
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activityList.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = updateStaffingLevelDetails(activityList, activities, phase, activityWrapperMap);
            AutoGapFillingScenario gapFillingScenario = getGapFillingScenario(shiftActivityBeforeGap, shiftActivityAfterGap);
            AutoFillGapSettings gapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(shiftDTO.getUnitId(), staffAdditionalInfoDTO.getOrganizationType().getId(), staffAdditionalInfoDTO.getOrganizationSubType().getId(), phase.getId(), gapFillingScenario.toString(), null, staffAdditionalInfoDTO.getRoles().contains(MANAGEMENT) ? MANAGEMENT.toString() : STAFF.toString());
            ShiftActivityDTO shiftActivityDTO = getActivityToFillTheGap(phase, staffAdditionalInfoDTO, shiftActivityBeforeGap, shiftActivityAfterGap, gapFillingScenario, gapSettings,staffingLevelActivityWithDurationMap);
            for (int index = 0; index < shiftDTO.getActivities().size() - 1; index++) {
                if (!shiftDTO.getActivities().get(index).getEndDate().equals(shiftDTO.getActivities().get(index + 1).getStartDate())) {
                    shiftDTO.getActivities().add(index + 1, shiftActivityDTO);
                }
            }
        }
    }

    public ShiftActivityDTO getActivityToFillTheGap(Phase phase, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, AutoGapFillingScenario gapFillingScenario, AutoFillGapSettings gapSettings, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        ShiftActivityDTO shiftActivityDTO;
        switch (gapFillingScenario) {
            case PRODUCTIVE_TYPE_ON_BOTH_SIDE:
                shiftActivityDTO = getApplicableActivityForProductiveTypeOnBothSide(phase, gapSettings, shiftActivityBeforeGap, shiftActivityAfterGap, staffAdditionalInfoDTO,staffingLevelActivityWithDurationMap);
                break;
            case ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE:
                shiftActivityDTO = getApplicableActivityForProductiveTypeOnOneSide(phase, gapSettings);
                break;
            default:
                shiftActivityDTO = getApplicableActivityForNonProductiveTypeOnBothSide(phase, gapSettings);
                break;
        }
        return shiftActivityDTO;
    }

    private boolean gapCreated(ShiftDTO shiftDTO, Shift shift) {
        return shift.getActivities().size() > shiftDTO.getActivities().size() && shift.getStartDate().equals(shiftDTO.getStartDate()) && shift.getEndDate().equals(shiftDTO.getEndDate());
    }

    private ShiftActivityDTO[] getActivitiesAroundGap(ShiftDTO shiftDTO) {
        ShiftActivityDTO shiftActivityBeforeGap = null;
        ShiftActivityDTO shiftActivityAfterGap = null;
        for (int i = 0; i < shiftDTO.getActivities().size(); i++) {
            if (!shiftDTO.getActivities().get(i).getEndDate().equals(shiftDTO.getActivities().get(i + 1).getStartDate())) {
                shiftActivityBeforeGap = shiftDTO.getActivities().get(i);
                shiftActivityAfterGap = shiftDTO.getActivities().get(i + 1);
                break;
            }
        }
        return new ShiftActivityDTO[]{shiftActivityBeforeGap, shiftActivityAfterGap};
    }

    private AutoGapFillingScenario getGapFillingScenario(ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap) {
        if (shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() && shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()) {
            return PRODUCTIVE_TYPE_ON_BOTH_SIDE;
        } else if (shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() || shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()) {
            return ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE;
        }
        return NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE;
    }

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnBothSide(Phase phase, AutoFillGapSettings gapSettings, ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        ShiftActivityDTO shiftActivityDTO=null;
        TeamDTO mainTeam= staffAdditionalInfoDTO.getTeamsData().stream().filter(k-> TeamType.MAIN.equals(k.getTeamType())).findAny().orElse(null);
        long gapDuration= new DateTimeInterval(beforeGap.getEndDate(),afterGap.getStartDate()).getMinutes();
        for (AutoFillGapSettingsRule autoFillGapSettingsRule : gapSettings.getSelectedAutoFillGapSettingsRules()) {
            switch (autoFillGapSettingsRule) {
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE1:
                    shiftActivityDTO = getShiftActivityDTO(beforeGap, afterGap, staffAdditionalInfoDTO, shiftActivityDTO);
                    if(shiftActivityDTO!=null) break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2:
                    if(mainTeam!=null){
                        shiftActivityDTO=new ShiftActivityDTO("",beforeGap.getEndDate(),afterGap.getStartDate(),mainTeam.getActivityIds().iterator().next(),null);
                        break;
                    }
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE3:
                    shiftActivityDTO = beforeGap.getActivity().getActivityPriority().getSequence() < afterGap.getActivity().getActivityPriority().getSequence() ? beforeGap : afterGap;
                    break;
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE1:
                    shiftActivityDTO = beforeGap.getActivity().getActivityPriority().getSequence() < afterGap.getActivity().getActivityPriority().getSequence() ? beforeGap : afterGap;
                    break;
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2:
                    shiftActivityDTO = beforeGap.getActivity().getActivityPriority().getSequence() < afterGap.getActivity().getActivityPriority().getSequence() ? beforeGap : afterGap;
                    break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE1:
                     if(mainTeam!=null && staffingLevelActivityWithDurationMap.get(mainTeam.getActivityIds().iterator().next()).getResolvingUnderOrOverStaffingDurationInMinutes()>0){
                         shiftActivityDTO=new ShiftActivityDTO("",beforeGap.getEndDate(),afterGap.getStartDate(),mainTeam.getActivityIds().iterator().next(),null);
                         break;
                     }
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE2:
                    TeamDTO highestRankTeam=staffAdditionalInfoDTO.getTeamsData().stream().sorted(Comparator.comparing(TeamDTO::getSequence)).collect(Collectors.toList()).get(0);
                    if(staffingLevelActivityWithDurationMap.get(highestRankTeam.getActivityIds().iterator().next()).getResolvingUnderOrOverStaffingDurationInMinutes()>0){
                        shiftActivityDTO=new ShiftActivityDTO("",beforeGap.getEndDate(),afterGap.getStartDate(),mainTeam.getActivityIds().iterator().next(),null);
                        break;
                    }
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE3:
                     List<BigInteger> allActivitySolvingMaxDuration=
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE4:
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE1:
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE2:
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE3:
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE4:
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE5:
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REAL_TIME_PHASE1:
            }

        }
        if (REQUEST.equals(phase.getPhaseEnum())) {
            if (UserContext.getUserDetails().isManagement()) {
                shiftActivityDTO = beforeGap.getActivity().getActivityPriority().getSequence() < afterGap.getActivity().getActivityPriority().getSequence() ? beforeGap : afterGap;
            } else {
                if (staffAdditionalInfoDTO.getMainTeamActivities().contains(beforeGap.getActivityId())) {
                    shiftActivityDTO = beforeGap;
                } else if (staffAdditionalInfoDTO.getMainTeamActivities().contains(afterGap.getActivityId())) {
                    shiftActivityDTO = afterGap;
                } else {
                    shiftActivityDTO = beforeGap.getActivity().getActivityPriority().getSequence() < afterGap.getActivity().getActivityPriority().getSequence() ? beforeGap : afterGap;
                }
            }
        }
        return shiftActivityDTO;

    }

    private ShiftActivityDTO getShiftActivityDTO(ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftActivityDTO shiftActivityDTO) {
        TeamDTO mainTeam= staffAdditionalInfoDTO.getTeamsData().stream().filter(k-> TeamType.MAIN.equals(k.getTeamType())).findAny().orElse(null);
        if(mainTeam!=null){
            if (staffAdditionalInfoDTO.getMainTeamActivities().contains(beforeGap.getActivityId())) {
                shiftActivityDTO = beforeGap;
            } else if (staffAdditionalInfoDTO.getMainTeamActivities().contains(afterGap.getActivityId())) {
                shiftActivityDTO = afterGap;
            }
        }
        return shiftActivityDTO;
    }

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnOneSide(Phase phase, AutoFillGapSettings gapSettings) {
        for (AutoFillGapSettingsRule autoFillGapSettingsRule:gapSettings.getSelectedAutoFillGapSettingsRules()) {
            switch (autoFillGapSettingsRule){
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REQUEST_PHASE1:
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE1:
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE2:
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE3:
                case RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REQUEST_PHASE1:
                case RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE1:
                case RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE2:
                case RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE3:
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REAL_TIME_PHASE1:
                case RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REAL_TIME_PHASE1:
            }
        }
        return null;
    }

    private ShiftActivityDTO getApplicableActivityForNonProductiveTypeOnBothSide(Phase phase, AutoFillGapSettings gapSettings) {
        for (AutoFillGapSettingsRule autoFillGapSettingsRule:gapSettings.getSelectedAutoFillGapSettingsRules()) {
            switch (autoFillGapSettingsRule){
                case RULES_AS_PER_STAFF_NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE_ALL_PHASE1:
                case RULES_AS_PER_MANAGEMENT_NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE_ALL_PHASE1:
            }
        }
        return null;
    }

    private Map<BigInteger, StaffingLevelActivityWithDuration> updateStaffingLevelDetails(List<ActivityWrapper> activityDTOList, ShiftActivityDTO[] activities, Phase phase, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        activityDTOList.forEach(k -> shiftActivities.add(new ShiftActivity(k.getActivity().getName(), activities[0].getEndDate(), activities[1].getEndDate(), k.getActivity().getId(), null)));
        Shift shift = new Shift();
        shift.setActivities(shiftActivities);
        shift.setStartDate(activities[0].getEndDate());
        shift.setEndDate(activities[1].getStartDate());
        Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = new HashMap<>();
        for (ShiftActivity shiftActivity : shiftActivities) {
            staffingLevelService.validateStaffingLevel(phase, shift, activityWrapperMap, true, shiftActivity, null, staffingLevelActivityWithDurationMap);
        }
        return staffingLevelActivityWithDurationMap;
    }

    private Set<BigInteger> getActivitiesResolvingMostProblem(Map<BigInteger,StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap){
        Set<BigInteger> activityIds=new HashSet<>();
        Map<Short,BigInteger> solvingMostProblem=new TreeMap<>(Collections.reverseOrder());
        staffingLevelActivityWithDurationMap.forEach((k,v)-> solvingMostProblem.put(v.getResolvingUnderOrOverStaffingDurationInMinutes(),v.getActivityId()));
        solvingMostProblem.forEach((k,v)->{

        });
        for(int i=0;i<solvingMostProblem.size()-1;i++){
            if(i==2) break;
            activityIds.add(solvingMostProblem.)
        }

    }




}
