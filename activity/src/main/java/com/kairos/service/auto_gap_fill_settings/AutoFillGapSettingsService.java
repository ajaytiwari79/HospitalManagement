package com.kairos.service.auto_gap_fill_settings;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityPriorityDTO;
import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
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
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.gap_settings.AutoFillGapSettingsMongoRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.redis.RedisService;
import com.kairos.service.shift.ShiftValidatorService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.dto.user.access_permission.AccessGroupRole.MANAGEMENT;
import static com.kairos.dto.user.access_permission.AccessGroupRole.STAFF;
import static com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario.*;

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
    @Inject
    private RedisService redisService;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;

    public AutoFillGapSettingsDTO createAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        if (autoFillGapSettings.isPublished()) {
            validateGapSettingAndUpdateParentEndDate(autoFillGapSettings, forCountry);
        }
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        autoFillGapSettingsDTO.setId(autoFillGapSettings.getId());
        resetCacheData(autoFillGapSettings);
        return autoFillGapSettingsDTO;
    }

    public AutoFillGapSettingsDTO updateAutoFillGapSettings(AutoFillGapSettingsDTO autoFillGapSettingsDTO, boolean forCountry) {
        AutoFillGapSettings autoFillGapSettingsFromDB = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsDTO.getId());
        if (isNull(autoFillGapSettingsFromDB)) {
            exceptionService.dataNotFoundByIdException(ERROR_AUTO_FILL_GAP_SETTING_NOT_FOUND);
        }
        AutoFillGapSettings autoFillGapSettings = ObjectMapperUtils.copyPropertiesByMapper(autoFillGapSettingsDTO, AutoFillGapSettings.class);
        if (autoFillGapSettings.isPublished()) {
            if (autoFillGapSettingsFromDB.isPublished()) {
                exceptionService.actionNotPermittedException(ERROR_ALREADY_AUTO_FILL_GAP_SETTING_PUBLISH);
            }
            validateGapSettingAndUpdateParentEndDate(autoFillGapSettings, forCountry);
        } else {
            if (autoFillGapSettingsFromDB.isPublished()) {
                if (isNotNull(autoFillGapSettingsMongoRepository.getGapSettingsByParentId(autoFillGapSettingsDTO.getId()))) {
                    exceptionService.actionNotPermittedException(ERROR_DRAFT_COPY_ALREADY_CREATED);
                }
                autoFillGapSettings.setParentId(autoFillGapSettings.getId());
                autoFillGapSettings.setId(null);
            }
        }
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        resetCacheData(autoFillGapSettings);
        return autoFillGapSettingsDTO;
    }

    private void validateGapSettingAndUpdateParentEndDate(AutoFillGapSettings autoFillGapSettings, boolean forCountry) {
        AutoFillGapSettings parentSetting = null;
        if (isNull(autoFillGapSettings.getParentId())) {
            List<AutoFillGapSettings> autoFillGapSettingsList;
            if (forCountry) {
                autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getGapSettingsForCountry(autoFillGapSettings.getCountryId(), autoFillGapSettings.getOrganizationTypeId(), autoFillGapSettings.getOrganizationSubTypeId(), autoFillGapSettings.getPhaseId(), autoFillGapSettings.getAutoGapFillingScenario().toString(), autoFillGapSettings.getId(), autoFillGapSettings.getGapApplicableFor().toString(), autoFillGapSettings.getStartDate());
                parentSetting = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForCountry(autoFillGapSettings.getCountryId(), autoFillGapSettings.getOrganizationTypeId(), autoFillGapSettings.getOrganizationSubTypeId(), autoFillGapSettings.getPhaseId(), autoFillGapSettings.getAutoGapFillingScenario().toString(), autoFillGapSettings.getId(), autoFillGapSettings.getGapApplicableFor().toString(), autoFillGapSettings.getStartDate());
            } else {
                autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getGapSettingsForUnit(autoFillGapSettings.getUnitId(), autoFillGapSettings.getOrganizationTypeId(), autoFillGapSettings.getOrganizationSubTypeId(), autoFillGapSettings.getPhaseId(), autoFillGapSettings.getAutoGapFillingScenario().toString(), autoFillGapSettings.getId(), autoFillGapSettings.getGapApplicableFor().toString(), autoFillGapSettings.getStartDate());
                parentSetting = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(autoFillGapSettings.getUnitId(), autoFillGapSettings.getPhaseId(), autoFillGapSettings.getAutoGapFillingScenario().toString(), autoFillGapSettings.getId(), autoFillGapSettings.getGapApplicableFor().toString(), autoFillGapSettings.getStartDate());
            }
            if (isCollectionNotEmpty(autoFillGapSettingsList)) {
                exceptionService.actionNotPermittedException(ERROR_AUTO_FILL_GAP_SETTING_PUBLISH_DATE_INVALID);
            }
        } else {
            parentSetting = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettings.getParentId());
            if ((!parentSetting.getStartDate().isBefore(autoFillGapSettings.getStartDate())) || isNotNull(parentSetting.getEndDate()) && parentSetting.getEndDate().isBefore(autoFillGapSettings.getStartDate())) {
                exceptionService.actionNotPermittedException(ERROR_AUTO_FILL_GAP_SETTING_PUBLISH_DATE_INVALID);
            }
        }
        if (isNotNull(parentSetting)) {
            autoFillGapSettings.setEndDate(parentSetting.getEndDate());
            parentSetting.setEndDate(autoFillGapSettings.getStartDate().minusDays(1));
            autoFillGapSettingsMongoRepository.save(parentSetting);
        }
        autoFillGapSettings.setParentId(null);
    }

    public List<AutoFillGapSettingsDTO> getAllAutoFillGapSettings(Long countryOrUnitId, boolean forCountry) {
        List<AutoFillGapSettingsDTO> autoFillGapSettingsList;
        if (forCountry) {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllAutoFillGapSettingsByCountryId(countryOrUnitId);
        } else {
            autoFillGapSettingsList = autoFillGapSettingsMongoRepository.getAllAutoFillGapSettingsByUnitId(countryOrUnitId);
        }
        return autoFillGapSettingsList;
    }

    public Boolean deleteAutoFillGapSettings(BigInteger autoFillGapSettingsId) {
        AutoFillGapSettings autoFillGapSettings = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettingsId);
        if (isNull(autoFillGapSettings)) {
            exceptionService.dataNotFoundByIdException(ERROR_AUTO_FILL_GAP_SETTING_NOT_FOUND);
        }
        autoFillGapSettings.setDeleted(true);
        autoFillGapSettingsMongoRepository.save(autoFillGapSettings);
        resetCacheData(autoFillGapSettings);
        return true;
    }


    public void createDefaultAutoFillGapSettings(Long unitId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO, List<Phase> phases) {
        List<AutoFillGapSettings> autoFillGapSettings = autoFillGapSettingsMongoRepository.getAllDefautAutoFillSettings(orgTypeAndSubTypeDTO.getCountryId(), orgTypeAndSubTypeDTO.getOrganizationTypeId(), orgTypeAndSubTypeDTO.getSubTypeId());
        Map<BigInteger, BigInteger> countryPhaseIdAndUnitPhaseIdMap = phases.stream().collect(Collectors.toMap(Phase::getParentCountryPhaseId, Phase::getId));
        if (isCollectionNotEmpty(autoFillGapSettings)) {
            autoFillGapSettings.forEach(autoFillGapSetting -> {
                autoFillGapSetting.setId(null);
                autoFillGapSetting.setCountryId(null);
                autoFillGapSetting.setUnitId(unitId);
                autoFillGapSetting.setPhaseId(countryPhaseIdAndUnitPhaseIdMap.get(autoFillGapSetting.getPhaseId()));
            });
            autoFillGapSettingsMongoRepository.saveEntities(autoFillGapSettings);
        }
    }

    public Boolean adjustGapByActivity(ShiftDTO shiftDTO, Shift shift, Phase phase, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        if (gapCreated(shiftDTO, shift)) {
            adjustTiming(shiftDTO, shift);
            ShiftActivityDTO[] activities = getActivitiesAroundGap(shiftDTO, shift);
            ShiftActivityDTO shiftActivityBeforeGap = activities[0];
            ShiftActivityDTO shiftActivityAfterGap = activities[1];
            ShiftActivityDTO removedActivity = activities[2];
            Set<BigInteger> allProductiveActivityIds = staffAdditionalInfoDTO.getTeamsData().stream().flatMap(k -> k.getActivityIds().stream()).collect(Collectors.toSet());
            allProductiveActivityIds.addAll(newHashSet(shiftActivityBeforeGap.getActivityId(), shiftActivityAfterGap.getActivityId()));
            allProductiveActivityIds.remove(removedActivity.getActivityId());
            List<ActivityWrapper> activityList = activityMongoRepository.findParentActivitiesAndTimeTypeByActivityId(allProductiveActivityIds);
            activityList = filterParentActivities(activityList);
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activityList.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            boolean mainTeamRemoved = staffAdditionalInfoDTO.getTeamsData().stream().anyMatch(k -> TeamType.MAIN.equals(k.getTeamType()) && k.getActivityIds().contains(removedActivity.getActivityId()));
            filterActivities(staffAdditionalInfoDTO.getTeamsData(), activityWrapperMap, shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet()), shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet()), mainTeamRemoved);
            setBasicDetails(shiftActivityBeforeGap, shiftActivityAfterGap, activityWrapperMap);
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = updateStaffingLevelDetails(shiftActivityBeforeGap,shiftActivityAfterGap, phase, activityWrapperMap);
            AutoGapFillingScenario gapFillingScenario = getGapFillingScenario(shiftActivityBeforeGap, shiftActivityAfterGap);
            AutoFillGapSettings gapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(shiftDTO.getUnitId(), phase.getId(), gapFillingScenario.toString(), null, UserContext.getUserDetails().isManagement() ? MANAGEMENT.toString() : STAFF.toString(), shiftDTO.getShiftDate());
            if (isNull(gapSettings)) {
                exceptionService.dataNotFoundException(GAP_FILLING_SETTING_NOT_CONFIGURED);
            }
            ShiftActivityDTO shiftActivityDTO = getActivityToFillTheGap(staffAdditionalInfoDTO, shiftActivityBeforeGap, shiftActivityAfterGap, gapFillingScenario, gapSettings, staffingLevelActivityWithDurationMap, activityList, mainTeamRemoved, phase,shiftDTO);
            for (int index = 0; index < shiftDTO.getActivities().size() - 1; index++) {
                if (!shiftDTO.getActivities().get(index).getEndDate().equals(shiftDTO.getActivities().get(index + 1).getStartDate())) {
                    shiftDTO.getActivities().add(index + 1, shiftActivityDTO);
                    break;
                }
            }
            shiftDTO.setActivities(shiftDTO.getActivities());
            return shiftActivityDTO.isSkipRules();
        }
        return null;
    }

    public void setBasicDetails(ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        shiftActivityBeforeGap.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityBeforeGap.getActivityId()).getActivity(), ActivityDTO.class));
        shiftActivityBeforeGap.getActivity().setTimeType(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityBeforeGap.getActivityId()).getTimeTypeInfo(), TimeTypeDTO.class));
        shiftActivityAfterGap.setActivity(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityAfterGap.getActivityId()).getActivity(), ActivityDTO.class));
        shiftActivityAfterGap.getActivity().setTimeType(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityAfterGap.getActivityId()).getTimeTypeInfo(), TimeTypeDTO.class));
        shiftActivityBeforeGap.getActivity().setActivityPriority(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityBeforeGap.getActivityId()), ActivityPriorityDTO.class));
        shiftActivityAfterGap.getActivity().setActivityPriority(ObjectMapperUtils.copyPropertiesByMapper(activityWrapperMap.get(shiftActivityAfterGap.getActivityId()), ActivityPriorityDTO.class));
    }


    private void adjustTiming(ShiftDTO shiftDTO, Shift shift) {
        boolean sameActivity = shift.getActivities().size() - shiftDTO.getActivities().size() > 1;
        if (sameActivity) {
            Set<BigInteger> activityIds = shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
            List<ShiftActivity> shiftActivities = ObjectMapperUtils.copyCollectionPropertiesByMapper(shift.getActivities(), ShiftActivity.class);
            shiftActivities.removeIf(current -> !activityIds.contains(current.getActivityId()));
            shiftDTO.setActivities(ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftActivities, ShiftActivityDTO.class));
        } else {
            for (int i = 1; i < shiftDTO.getActivities().size(); i++) {
                if (!shiftDTO.getActivities().get(i).getActivityId().equals(shift.getActivities().get(i).getActivityId())) {
                    shiftDTO.getActivities().get(i - 1).setEndDate(shift.getActivities().get(i).getStartDate());
                    break;
                }
            }
        }
    }

    public ShiftActivityDTO getActivityToFillTheGap(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, AutoGapFillingScenario gapFillingScenario, AutoFillGapSettings gapSettings, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, List<ActivityWrapper> activityList, boolean mainTeamRemoved, Phase phase, ShiftDTO shiftDTO) {
        ShiftActivityDTO shiftActivityDTO;
        switch (gapFillingScenario) {
            case PRODUCTIVE_TYPE_ON_BOTH_SIDE:
                shiftActivityDTO = getApplicableActivityForProductiveTypeOnBothSide(gapSettings, shiftActivityBeforeGap, shiftActivityAfterGap, staffAdditionalInfoDTO, staffingLevelActivityWithDurationMap,shiftDTO,mainTeamRemoved,activityList);
                break;
            case ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE:
                shiftActivityDTO = getApplicableActivityForProductiveTypeOnOneSide(gapSettings, shiftActivityBeforeGap, shiftActivityAfterGap, staffAdditionalInfoDTO, staffingLevelActivityWithDurationMap, activityList, mainTeamRemoved,shiftDTO);
                break;
            default:
                shiftActivityDTO = new ShiftActivityDTO("", shiftActivityBeforeGap.getEndDate(), shiftActivityAfterGap.getStartDate(), shiftActivityBeforeGap.getActivityId(), null);
                break;
        }
        if (isNull(shiftActivityDTO)) {
            exceptionService.actionNotPermittedException(GAP_FILLING_SETTING_NOT_CONFIGURED);
        }
        return shiftActivityDTO;
    }


    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnOneSide(AutoFillGapSettings gapSettings, ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, List<ActivityWrapper> activityList, boolean mainTeamRemoved, ShiftDTO shiftDTO) {
        ShiftActivityDTO shiftActivityDTO = null;
        ShiftActivityDTO productiveActivity = afterGap.getActivity().getTimeType().isPartOfTeam() ? afterGap : beforeGap;
        BigInteger mainTeamActivityId = null;
        TeamDTO mainTeam = staffAdditionalInfoDTO.getTeamsData().stream().filter(k -> TeamType.MAIN.equals(k.getTeamType())).findAny().orElse(null);
        if (mainTeam != null) {
            mainTeamActivityId = mainTeam.getActivityIds().iterator().next();
        }
        staffAdditionalInfoDTO.getTeamsData().remove(mainTeam);
        staffAdditionalInfoDTO.setTeamsData(staffAdditionalInfoDTO.getTeamsData().stream().sorted(Comparator.comparing(TeamDTO::getSequence)).collect(Collectors.toList()));
        TeamDTO highestRankTeam = staffAdditionalInfoDTO.getTeamsData().isEmpty() ? null : staffAdditionalInfoDTO.getTeamsData().get(0);
        short gapDuration = (short) new DateTimeInterval(beforeGap.getEndDate(),afterGap.getStartDate()).getMinutes();
        for (AutoFillGapSettingsRule autoFillGapSettingsRule : gapSettings.getSelectedAutoFillGapSettingsRules()) {
            switch (autoFillGapSettingsRule) {
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REQUEST_PHASE1:
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), productiveActivity.getActivityId(), null);
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE1:
                    if (staffingLevelActivityWithDurationMap.containsKey(productiveActivity.getActivityId()) && staffingLevelActivityWithDurationMap.get(productiveActivity.getActivityId()).getResolvingUnderOrOverStaffingDurationInMinutes() > 0) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), productiveActivity.getActivityId(), null);
                    }
                    break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2:
                    if(!mainTeamRemoved && mainTeamActivityId==null){
                        exceptionService.actionNotPermittedException(MAIN_TEAM_ABSENT);
                    }
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), mainTeamActivityId==null ? highestRankTeam.getActivityIds().iterator().next():mainTeamActivityId , true);
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE2:
                    List<ActivityWrapper> activityWrappers = activityList.stream().filter(k -> !k.getActivity().getId().equals(productiveActivity.getActivityId())).collect(Collectors.toList());
                    BigInteger activityId = staffingLevelActivityWithDurationMap.isEmpty() ? null : getHighestRankActivity(staffAdditionalInfoDTO, staffingLevelActivityWithDurationMap, activityWrappers, shiftDTO,gapDuration);
                    if (activityId != null) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), activityId, null);
                    }
                    break;
                case RULES_AS_PER_STAFF_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_PUZZLE_TO_TENTATIVE_PHASE3:
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), beforeGap.getActivity().getTimeType().isPartOfTeam() ? afterGap.getActivityId() : beforeGap.getActivityId(), null);
                case RULES_AS_PER_MANAGEMENT_ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE_REAL_TIME_PHASE1:
                    activityId = afterGap.getActivity().getActivityPriority().getSequence() < beforeGap.getActivity().getActivityPriority().getSequence() ? afterGap.getActivityId() : beforeGap.getActivityId();
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), activityId, null);
                default:
                    exceptionService.actionNotPermittedException(GAP_FILLING_SETTING_NOT_CONFIGURED);
            }
        }
        return shiftActivityDTO;
    }

    private boolean gapCreated(ShiftDTO shiftDTO, Shift shift) {
        return shift.getActivities().size() > shiftDTO.getActivities().size() && shift.getStartDate().equals(shiftDTO.getStartDate()) && shift.getEndDate().equals(shiftDTO.getEndDate()) && shift.getActivities().get(0).getActivityId().equals(shiftDTO.getActivities().get(0).getActivityId()) && shift.getActivities().get(shift.getActivities().size() - 1).getActivityId().equals(shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getActivityId());
    }

    private ShiftActivityDTO[] getActivitiesAroundGap(ShiftDTO shiftDTO, Shift shift) {
        ShiftActivityDTO shiftActivityBeforeGap = null;
        ShiftActivityDTO shiftActivityAfterGap = null;
        ShiftActivityDTO removedActivity = null;
        for (int i = 0; i < shiftDTO.getActivities().size() - 1; i++) {
            if (!shiftDTO.getActivities().get(i).getEndDate().equals(shiftDTO.getActivities().get(i + 1).getStartDate())) {
                shiftActivityBeforeGap = shiftDTO.getActivities().get(i);
                shiftActivityAfterGap = shiftDTO.getActivities().get(i + 1);
                removedActivity = ObjectMapperUtils.copyPropertiesByMapper(shift.getActivities().get(i + 1), ShiftActivityDTO.class);
                break;
            }
        }
        return new ShiftActivityDTO[]{shiftActivityBeforeGap, shiftActivityAfterGap, removedActivity};
    }

    private AutoGapFillingScenario getGapFillingScenario(ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap) {
        if (shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() && shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()) {
            return PRODUCTIVE_TYPE_ON_BOTH_SIDE;
        } else if (shiftActivityBeforeGap.getActivity().getTimeType().isPartOfTeam() || shiftActivityAfterGap.getActivity().getTimeType().isPartOfTeam()) {
            return ONE_SIDE_PRODUCTIVE_OTHER_SIDE_NON_PRODUCTIVE;
        }
        return NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE;
    }

    private ShiftActivityDTO getApplicableActivityForProductiveTypeOnBothSide(AutoFillGapSettings gapSettings, ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, ShiftDTO shiftDTO, boolean mainTeamRemoved, List<ActivityWrapper> activityList) {
        ShiftActivityDTO shiftActivityDTO = null;
        BigInteger mainTeamActivityId = null;
        TeamDTO mainTeam = staffAdditionalInfoDTO.getTeamsData().stream().filter(k -> TeamType.MAIN.equals(k.getTeamType())).findAny().orElse(null);
        if (mainTeam != null) {
            mainTeamActivityId = mainTeam.getActivityIds().iterator().next();
        }
        Set<BigInteger> activityIds=shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
        staffAdditionalInfoDTO.getTeamsData().remove(mainTeam);
        staffAdditionalInfoDTO.setTeamsData(staffAdditionalInfoDTO.getTeamsData().stream().sorted(Comparator.comparing(TeamDTO::getSequence)).collect(Collectors.toList()));
        TeamDTO highestRankTeam = staffAdditionalInfoDTO.getTeamsData().isEmpty() ? null : staffAdditionalInfoDTO.getTeamsData().get(0);
        TeamDTO highestRankTeamApartFromShift = staffAdditionalInfoDTO.getTeamsData().stream().filter(k -> !k.getActivityIds().contains(beforeGap.getActivityId()) && !k.getActivityIds().contains(afterGap.getActivityId())).findFirst().orElse(null);
        Map<BigInteger,Integer> teamRankingMap=staffAdditionalInfoDTO.getTeamsData().stream().collect(Collectors.toMap(k->k.getActivityIds().iterator().next(), TeamDTO::getSequence));
        short gapDuration = (short) new DateTimeInterval(beforeGap.getEndDate(),afterGap.getStartDate()).getMinutes();
        for (AutoFillGapSettingsRule autoFillGapSettingsRule : gapSettings.getSelectedAutoFillGapSettingsRules()) {
            switch (autoFillGapSettingsRule) {
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE1:
                    return getShiftActivityDTO(beforeGap, afterGap, shiftActivityDTO,mainTeamActivityId);
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2:
                    if(!mainTeamRemoved && mainTeamActivityId==null){
                        exceptionService.actionNotPermittedException(MAIN_TEAM_ABSENT);
                    }
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), mainTeamActivityId==null ? highestRankTeam.getActivityIds().iterator().next():mainTeamActivityId , true);
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE3:
                    if(!teamRankingMap.containsKey(afterGap.getActivityId()) && !teamRankingMap.containsKey(beforeGap.getActivityId())){
                        exceptionService.actionNotPermittedException(GAP_FILLING_CONFIGURATION_ABSENT);
                    }
                    BigInteger actId = teamRankingMap.getOrDefault(afterGap.getActivity().getId(),Integer.MAX_VALUE) < teamRankingMap.getOrDefault(beforeGap.getActivity().getId(),Integer.MAX_VALUE) ? afterGap.getActivityId() : beforeGap.getActivityId();
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), actId, null);
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE1:
                    BigInteger activityId = afterGap.getActivity().getActivityPriority().getSequence() < beforeGap.getActivity().getActivityPriority().getSequence() ? afterGap.getActivityId() : beforeGap.getActivityId();
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), activityId, null);
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REQUEST_PHASE2:
                    if (highestRankTeam != null) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), highestRankTeam.getActivityIds().iterator().next(), true);
                    } else {
                        exceptionService.actionNotPermittedException(HIGHEST_RANK_ACTIVITY_ABSENT);
                    }
                    break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE1:
                    if (mainTeamActivityId != null && !activityIds.contains(mainTeamActivityId) && staffingLevelActivityWithDurationMap.getOrDefault(mainTeamActivityId, new StaffingLevelActivityWithDuration()).getResolvingUnderOrOverStaffingDurationInMinutes() > 0) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), mainTeamActivityId, null);
                    } else {
                        exceptionService.actionNotPermittedException(MAIN_TEAM_ABSENT);
                    }
                    break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE2:
                    boolean mainTeamResolvingProblem=staffingLevelActivityWithDurationMap.getOrDefault(mainTeamActivityId, new StaffingLevelActivityWithDuration()).getResolvingUnderOrOverStaffingDurationInMinutes() > 0;
                    TeamDTO highestRank=null;
                    if(!mainTeamResolvingProblem){
                         highestRank=staffAdditionalInfoDTO.getTeamsData().stream().filter(k->staffingLevelActivityWithDurationMap.getOrDefault(k.getActivityIds().iterator().next(),new StaffingLevelActivityWithDuration()).getResolvingUnderOrOverStaffingDurationInMinutes()>0).findFirst().orElse(null);
                    }
                    if (mainTeamResolvingProblem || (highestRank != null)) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(),mainTeamResolvingProblem ? mainTeamActivityId: highestRankTeam.getActivityIds().iterator().next(), null);
                    } else {
                        exceptionService.actionNotPermittedException(HIGHEST_RANK_ACTIVITY_ABSENT);
                    }
                    break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE3:
                    activityId = getHighestRankActivity(staffAdditionalInfoDTO, staffingLevelActivityWithDurationMap, new ArrayList<>(),shiftDTO,gapDuration);
                    if (activityId != null) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), activityId, null);
                    } else {
                        exceptionService.actionNotPermittedException(HIGHEST_RANK_ACTIVITY_ABSENT);
                    }
                    break;
                case RULES_AS_PER_STAFF_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE4:
                    if (allActivitiesAreCreatingProblems(staffingLevelActivityWithDurationMap)) {
                        exceptionService.actionNotPermittedException("all.activities.create.problem");
                    }
                    break;
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE1:
                    highestRank=staffAdditionalInfoDTO.getTeamsData().stream().filter(k->!activityIds.contains(k.getActivityIds().iterator().next()) && staffingLevelActivityWithDurationMap.getOrDefault(k.getActivityIds().iterator().next(),new StaffingLevelActivityWithDuration()).getResolvingUnderOrOverStaffingDurationInMinutes()>0).findFirst().orElse(null);
                    if (highestRank != null) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), highestRankTeamApartFromShift.getActivityIds().iterator().next(), null);
                    } else {
                        exceptionService.actionNotPermittedException(HIGHEST_RANK_ACTIVITY_ABSENT);
                    }
                    break;
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE2:
                    short durationOfBefore = staffingLevelActivityWithDurationMap.containsKey(beforeGap.getActivityId()) ? staffingLevelActivityWithDurationMap.get(beforeGap.getActivityId()).getResolvingUnderOrOverStaffingDurationInMinutes() : 0;
                    short durationOfAfter = staffingLevelActivityWithDurationMap.containsKey(afterGap.getActivityId()) ? staffingLevelActivityWithDurationMap.get(afterGap.getActivityId()).getResolvingUnderOrOverStaffingDurationInMinutes() : 0;
                    if (durationOfBefore != 0 || durationOfAfter != 0) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), durationOfBefore > durationOfAfter ? beforeGap.getActivityId() : afterGap.getActivityId(), null);
                    }
                    break;
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_PUZZLE_TO_TENTATIVE_PHASE3:
                    activityId = getHighestRankActivity(staffAdditionalInfoDTO, staffingLevelActivityWithDurationMap, activityList, shiftDTO,gapDuration);
                    if (activityId != null) {
                        return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), activityId, null);
                    } else {
                        exceptionService.actionNotPermittedException(HIGHEST_RANK_ACTIVITY_ABSENT);
                    }
                    break;
                case RULES_AS_PER_MANAGEMENT_PRODUCTIVE_TYPE_ON_BOTH_SIDE_REAL_TIME_PHASE1:
                    activityId = activityList.stream().sorted(Comparator.comparing(k -> k.getActivityPriority().getSequence())).collect(Collectors.toList()).get(0).getActivity().getId();
                    return new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), activityId, null);
                default:
                    exceptionService.actionNotPermittedException(GAP_FILLING_CONFIGURATION_ABSENT);
            }

        }
        return shiftActivityDTO;

    }

    private BigInteger getHighestRankActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, List<ActivityWrapper> activityWrappers, ShiftDTO shiftDTO,short gapDuration) {
        List<BigInteger> allActivitySolvingMaxDuration = getActivitiesResolvingMostProblem(staffingLevelActivityWithDurationMap,shiftDTO);
        if (allActivitySolvingMaxDuration.isEmpty()) {
            return null;
        }
        short solvedDuration = staffingLevelActivityWithDurationMap.getOrDefault(allActivitySolvingMaxDuration.get(0), new StaffingLevelActivityWithDuration()).getResolvingUnderOrOverStaffingDurationInMinutes();
        Set<BigInteger> solvingEqualProblems = new HashSet<>();
        solvingEqualProblems.add(allActivitySolvingMaxDuration.get(0));
        for (int i = 1; i < allActivitySolvingMaxDuration.size() - 1; i++) {
            if (solvedDuration == staffingLevelActivityWithDurationMap.getOrDefault(allActivitySolvingMaxDuration.get(i), new StaffingLevelActivityWithDuration()).getResolvingUnderOrOverStaffingDurationInMinutes() || solvedDuration > gapDuration) {
                solvingEqualProblems.add(allActivitySolvingMaxDuration.get(0));
                continue;
            }
            break;
        }
        return getHighestRankedActivity(solvingEqualProblems, activityWrappers.isEmpty() ? new ArrayList<>() : staffAdditionalInfoDTO.getTeamsData(), activityWrappers.isEmpty() ? new ArrayList<>() : activityWrappers);
    }

    private boolean allActivitiesAreCreatingProblems(Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        return staffingLevelActivityWithDurationMap.values().stream().noneMatch(k -> k.getResolvingUnderOrOverStaffingDurationInMinutes() > 0);
    }

    private ShiftActivityDTO getShiftActivityDTO(ShiftActivityDTO beforeGap, ShiftActivityDTO afterGap, ShiftActivityDTO shiftActivityDTO, BigInteger mainTeamActivityId) {
        if (mainTeamActivityId != null) {
            if (mainTeamActivityId.equals(beforeGap.getActivityId())) {
                shiftActivityDTO = new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), beforeGap.getActivityId(), null);
            } else if (mainTeamActivityId.equals(afterGap.getActivityId())) {
                shiftActivityDTO = new ShiftActivityDTO("", beforeGap.getEndDate(), afterGap.getStartDate(), afterGap.getActivityId(), null);
            }
        }
        return shiftActivityDTO;
    }


    private Map<BigInteger, StaffingLevelActivityWithDuration> updateStaffingLevelDetails(ShiftActivityDTO beforeGap,ShiftActivityDTO afterGap, Phase phase, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        activityWrapperMap.forEach((k, v) -> shiftActivities.add(new ShiftActivity(v.getActivity().getName(), beforeGap.getEndDate(), afterGap.getStartDate(), k, null, v.getActivity().getActivityGeneralSettings().getUltraShortName(), v.getActivity().getActivityGeneralSettings().getShortName())));
        Shift shift = new Shift();
        shift.setActivities(shiftActivities);
        shift.setStartDate(beforeGap.getEndDate());
        shift.setEndDate(afterGap.getStartDate());
        shift.setUnitId(phase.getOrganizationId());
        Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = new HashMap<>();
        for (ShiftActivity shiftActivity : shiftActivities) {
            staffingLevelService.validateStaffingLevel(phase, shift, activityWrapperMap, true, shiftActivity, new RuleTemplateSpecificInfo(), staffingLevelActivityWithDurationMap, true);
        }
        return staffingLevelActivityWithDurationMap;
    }

    private List<BigInteger> getActivitiesResolvingMostProblem(Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, ShiftDTO shiftDTO) {
        Set<BigInteger> activityIds=shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
        List<BigInteger> mostProblemResolvingActivities=staffingLevelActivityWithDurationMap.values().stream().filter(k->k.getResolvingUnderOrOverStaffingDurationInMinutes()!=0).sorted(Comparator.comparing(StaffingLevelActivityWithDuration::getResolvingUnderOrOverStaffingDurationInMinutes).reversed()).collect(Collectors.toList()).stream().map(StaffingLevelActivityWithDuration::getActivityId).collect(Collectors.toList());
        mostProblemResolvingActivities.removeAll(activityIds);
        return mostProblemResolvingActivities;
    }

    private BigInteger getHighestRankedActivity(Set<BigInteger> activityIds, List<TeamDTO> teamsData, List<ActivityWrapper> activityList) {
        if (activityIds.size() == 1) {
            return activityIds.iterator().next();
        }
        List<TeamDTO> sortedData = teamsData.stream().sorted(Comparator.comparing(TeamDTO::getSequence)).collect(Collectors.toList());
        for (TeamDTO teamDTO : sortedData) {
            if (activityIds.contains(teamDTO.getActivityIds().iterator().next())) {
                return teamDTO.getActivityIds().iterator().next();
            }
        }
        List<ActivityWrapper> sortedActivityWrapper = activityList.stream().sorted(Comparator.comparing(k -> k.getActivityPriority().getSequence())).collect(Collectors.toList());
        for (ActivityWrapper activityWrapper : sortedActivityWrapper) {
            if (activityIds.contains(activityWrapper.getActivity().getId())) {
                return activityWrapper.getActivity().getId();
            }
        }
        return null;
    }

    private void filterActivities(List<TeamDTO> teamDTOS, Map<BigInteger, ActivityWrapper> activityWrapperMap, Set<BigInteger> activityIds, Set<BigInteger> activityIdsDB, boolean mainTeamRemoved) {
        teamDTOS.forEach(team -> {
            team.getActivityIds().removeIf(k -> !activityWrapperMap.containsKey(k));
            if (mainTeamRemoved) {
                activityIdsDB.removeAll(activityIds);
                team.getActivityIds().removeAll(activityIdsDB);
            }

        });
        teamDTOS.removeIf(k -> k.getActivityIds().isEmpty());

    }

    private List<ActivityWrapper> filterParentActivities(List<ActivityWrapper> activityWrappers) {
        List<ActivityWrapper> temp = ObjectMapperUtils.copyCollectionPropertiesByMapper(activityWrappers, ActivityWrapper.class);
        temp.removeIf(current -> activityWrappers.stream().anyMatch(k -> k.getActivity().getChildActivityIds().contains(current.getActivity().getId())));
        return temp;
    }

    private void resetCacheData(AutoFillGapSettings autoFillGapSettings) {
        if (isNotNull(autoFillGapSettings.getCountryId())) {
            redisService.removeKeyFromCache(newHashSet("getAllAutoFillGapSettingsByCountryId::" + autoFillGapSettings.getCountryId()));
        } else if (isNotNull(autoFillGapSettings.getUnitId())) {
            redisService.removeKeyFromCache(newHashSet("getAllAutoFillGapSettingsByUnitId::" + autoFillGapSettings.getUnitId()));
        }
    }

}
