package com.kairos.service.auto_gap_fill_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.auto_gap_fill_settings.AutoFillGapSettingsDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.dto.user.staff.staff.TeamRankingInfoDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.auto_gap_fill_settings.AutoFillGapSettingsRule;
import com.kairos.enums.auto_gap_fill_settings.AutoGapFillingScenario;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.auto_gap_fill_settings.AutoFillGapSettings;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.gap_settings.AutoFillGapSettingsMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.redis.RedisService;
import com.kairos.service.staffing_level.StaffingLevelValidatorService;
import com.kairos.service.unit_settings.ActivityRankingService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
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
    private StaffingLevelValidatorService staffingLevelValidatorService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private RedisService redisService;
    @Inject
    private ActivityRankingService activityRankingService;

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
        AutoFillGapSettings parentSetting;
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
                exceptionService.actionNotPermittedException(ERROR_PUBLISH_DATE_INVALID);
            }
        } else {
            parentSetting = autoFillGapSettingsMongoRepository.findOne(autoFillGapSettings.getParentId());
            if ((!parentSetting.getStartDate().isBefore(autoFillGapSettings.getStartDate())) || isNotNull(parentSetting.getEndDate()) && parentSetting.getEndDate().isBefore(autoFillGapSettings.getStartDate())) {
                exceptionService.actionNotPermittedException(ERROR_PUBLISH_DATE_INVALID);
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
        Boolean skipRules=null;
        if (gapCreated(shiftDTO, shift)) {
            adjustTiming(shiftDTO, shift);
            ShiftActivityDTO[] activities = getActivitiesAroundGap(shiftDTO, shift);
            ShiftActivityDTO shiftActivityBeforeGap = activities[0];
            ShiftActivityDTO shiftActivityAfterGap = activities[1];
            ShiftActivityDTO removedActivity = activities[2];
            Set<BigInteger> allProductiveActivityIds = activityMongoRepository.findAllProductiveTypeActivityIdsByUnitId(staffAdditionalInfoDTO.getUnitId());
            allProductiveActivityIds.remove(removedActivity.getActivityId());
            staffAdditionalInfoDTO.setStaffTeamRankingInfoData(staffAdditionalInfoDTO.getStaffTeamRankingInfoData().stream().filter(node->!node.getActivityId().equals(removedActivity.getActivityId())).collect(Collectors.toList()));
            List<ActivityWrapper> activityList = activityMongoRepository.findParentActivitiesAndTimeTypeByActivityId(allProductiveActivityIds);
            activityList = filterParentActivities(activityList);
            updateActivityRank(staffAdditionalInfoDTO.getUnitId(), shiftDTO.getShiftDate(), activityList);
            Map<BigInteger, ActivityWrapper> activityWrapperMap = activityList.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
            Map<BigInteger, Integer> staffActivityRankMap = getStaffRankMap(staffAdditionalInfoDTO);
            filterActivities(staffAdditionalInfoDTO.getStaffTeamRankingInfoData(), shiftDTO.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet()), shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toSet()));
            setBasicDetails(shiftActivityBeforeGap, shiftActivityAfterGap, activityWrapperMap, staffActivityRankMap, UserContext.getUserDetails().isStaff() && staffAdditionalInfoDTO.getCanRankTeam());
            Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap = updateStaffingLevelDetails(shiftActivityBeforeGap,shiftActivityAfterGap, phase, activityWrapperMap);
            AutoGapFillingScenario gapFillingScenario = getGapFillingScenario(shiftActivityBeforeGap, shiftActivityAfterGap);
            AutoFillGapSettings gapSettings = autoFillGapSettingsMongoRepository.getCurrentlyApplicableGapSettingsForUnit(shiftDTO.getUnitId(), phase.getId(), gapFillingScenario.toString(), null, UserContext.getUserDetails().isManagement() ? MANAGEMENT.toString() : STAFF.toString(), shiftDTO.getShiftDate());
            if (isNull(gapSettings)) {
                exceptionService.dataNotFoundException(GAP_FILLING_SETTING_NOT_CONFIGURED);
            }
            ShiftActivityDTO shiftActivityDTO = getActivityToFillTheGap(staffAdditionalInfoDTO, shiftActivityBeforeGap, shiftActivityAfterGap, gapSettings, staffingLevelActivityWithDurationMap, activityList);
            for (int index = 0; index < shiftDTO.getActivities().size() - 1; index++) {
                if (!shiftDTO.getActivities().get(index).getEndDate().equals(shiftDTO.getActivities().get(index + 1).getStartDate())) {
                    shiftDTO.getActivities().add(index + 1, shiftActivityDTO);
                    break;
                }
            }
            shiftDTO.setActivities(shiftDTO.getActivities());
            skipRules = shiftActivityDTO.isSkipRules();
        }
        return skipRules;
    }

    private Map<BigInteger, Integer> getStaffRankMap(StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<BigInteger, Integer> rankMap = new HashMap<>();
        for (TeamRankingInfoDTO staffTeamRankingInfoDatum : staffAdditionalInfoDTO.getStaffTeamRankingInfoData()) {
            rankMap.put(staffTeamRankingInfoDatum.getActivityId(), staffTeamRankingInfoDatum.getRank());
        }
        return rankMap;
    }

    private void updateActivityRank(Long unitId, LocalDate shiftDate, List<ActivityWrapper> activityList) {
        ActivityRanking activityRanking = activityRankingService.getCurrentlyActiveActivityRankingSettings(unitId, shiftDate);
        List<BigInteger> activities = isNotNull(activityRanking) && isCollectionNotEmpty(activityRanking.getPresenceActivities()) ? activityRanking.getPresenceActivities().stream().collect(Collectors.toList()) : new ArrayList<>();
        activityList.forEach(activityWrapper->{
            if(activities.contains(activityWrapper.getActivity().getId())){
                activityWrapper.setRanking(activities.indexOf(activityWrapper.getActivity().getId()) + 1);
            } else {
                activityWrapper.setRanking(Integer.MAX_VALUE);
            }
        });
    }

    public void setBasicDetails(ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, Map<BigInteger, ActivityWrapper> activityWrapperMap, Map<BigInteger, Integer> staffActivityRankMap, boolean isStaff) {
        List<ActivityWrapper> activityList = activityMongoRepository.findParentActivitiesAndTimeTypeByActivityId(newArrayList(shiftActivityBeforeGap.getActivityId(),shiftActivityAfterGap.getActivityId()));
        Map<BigInteger, ActivityWrapper> adjacentActivityWrapperMap = activityList.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftActivityBeforeGap.setActivity(ObjectMapperUtils.copyPropertiesByMapper(adjacentActivityWrapperMap.get(shiftActivityBeforeGap.getActivityId()).getActivity(), ActivityDTO.class));
        shiftActivityBeforeGap.getActivity().setTimeType(ObjectMapperUtils.copyPropertiesByMapper(adjacentActivityWrapperMap.get(shiftActivityBeforeGap.getActivityId()).getTimeTypeInfo(), TimeTypeDTO.class));
        shiftActivityAfterGap.setActivity(ObjectMapperUtils.copyPropertiesByMapper(adjacentActivityWrapperMap.get(shiftActivityAfterGap.getActivityId()).getActivity(), ActivityDTO.class));
        shiftActivityAfterGap.getActivity().setTimeType(ObjectMapperUtils.copyPropertiesByMapper(adjacentActivityWrapperMap.get(shiftActivityAfterGap.getActivityId()).getTimeTypeInfo(), TimeTypeDTO.class));
        if(isStaff) {
            shiftActivityBeforeGap.getActivity().setRanking(staffActivityRankMap.getOrDefault(shiftActivityBeforeGap.getActivityId(), Integer.MAX_VALUE));
            shiftActivityAfterGap.getActivity().setRanking(staffActivityRankMap.getOrDefault(shiftActivityAfterGap.getActivityId(), Integer.MAX_VALUE));
        } else {
            shiftActivityBeforeGap.getActivity().setRanking(activityWrapperMap.containsKey(shiftActivityBeforeGap.getActivityId()) ? activityWrapperMap.get(shiftActivityBeforeGap.getActivityId()).getRanking() : Integer.MAX_VALUE);
            shiftActivityAfterGap.getActivity().setRanking(activityWrapperMap.containsKey(shiftActivityAfterGap.getActivityId()) ? activityWrapperMap.get(shiftActivityAfterGap.getActivityId()).getRanking() : Integer.MAX_VALUE);
        }
    }


    private void adjustTiming(ShiftDTO shiftDTO, Shift shift) {
        boolean sameActivity = shift.getActivities().size() - shiftDTO.getActivities().size() > 1;
        if (sameActivity) {
            Set<Date> activityStartDate = shiftDTO.getActivities().stream().map(ShiftActivityDTO::getStartDate).collect(Collectors.toSet());
            Set<Date> activityEndDate = shiftDTO.getActivities().stream().map(ShiftActivityDTO::getEndDate).collect(Collectors.toSet());
            List<ShiftActivity> shiftActivities = ObjectMapperUtils.copyCollectionPropertiesByMapper(shift.getActivities(), ShiftActivity.class);
            shiftActivities.removeIf(current -> !activityStartDate.contains(current.getStartDate()) && !activityEndDate.contains(current.getEndDate()));
            shiftDTO.setActivities(ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftActivities, ShiftActivityDTO.class));
        } else {
            for (int i = 1; i < shiftDTO.getActivities().size(); i++) {
                if (!shiftDTO.getActivities().get(i).getActivityId().equals(shift.getActivities().get(i).getActivityId())) {
                    shiftDTO.getActivities().get(i - 1).setEndDate(shift.getActivities().get(i).getStartDate());
                    shiftDTO.getActivities().get(i).setStartDate(shift.getActivities().get(i).getEndDate());
                    break;
                }
            }
        }
    }

    public ShiftActivityDTO getActivityToFillTheGap(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, AutoFillGapSettings autoFillGapSetting, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, List<ActivityWrapper> activityList) {
        ShiftActivityDTO shiftActivityDTO = null;
        boolean showError = false;
        for (AutoFillGapSettingsRule autoFillGapSettingsRule : autoFillGapSetting.getSelectedAutoFillGapSettingsRules()) {
            Object[] activityIdAndShowError = getActivityIdAndShowError(autoFillGapSettingsRule, staffAdditionalInfoDTO, autoFillGapSetting, shiftActivityBeforeGap, shiftActivityAfterGap, staffingLevelActivityWithDurationMap, activityList);
            BigInteger activityId = (BigInteger) activityIdAndShowError[0];
            showError = (boolean) activityIdAndShowError[1];
            if(isNotNull(activityId)) {
                Activity activity=activityMongoRepository.findOne(activityId);
                shiftActivityDTO = new ShiftActivityDTO(activity.getName(), shiftActivityBeforeGap.getEndDate(), shiftActivityAfterGap.getStartDate(), activityId, null);
                shiftActivityDTO.setBackgroundColor(activity.getActivityGeneralSettings().getBackgroundColor());
                break;
            }
        }
        if(showError) {
            exceptionService.actionNotPermittedException(GAP_FILLING_CONFIGURATION_INCORRECTLY);
        }
        if (isNull(shiftActivityDTO)) {
            exceptionService.actionNotPermittedException(SYSTEM_NOT_FOUND_ACTIVITY_TO_GAP_FILLING_CONFIGURATION);
        }
        return shiftActivityDTO;
    }

    private Object[] getActivityIdAndShowError(AutoFillGapSettingsRule autoFillGapSettingsRule, StaffAdditionalInfoDTO staffAdditionalInfoDTO, AutoFillGapSettings autoFillGapSetting, ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap, List<ActivityWrapper> activityList){
        BigInteger activityId = null;
        boolean showError = false;
        switch (autoFillGapSettingsRule) {
            case HIGHEST_RANKED_ACTIVITY_PLANNED_ADJACENT_TO_THE_GAP :
                if(!NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE.equals(autoFillGapSetting.getAutoGapFillingScenario())) {
                    activityId = shiftActivityAfterGap.getActivity().getRanking() < shiftActivityBeforeGap.getActivity().getRanking() ? shiftActivityAfterGap.getActivityId() : shiftActivityBeforeGap.getActivityId();
                } else {
                    showError = true;
                }
                break;
            case HIGHEST_RANKED_ACTIVITY_IF_HIGHEST_IS_CAUSING_GAP_THEN_USE_SECOND_HIGHEST :
                activityId = getHighestRankActivity(staffAdditionalInfoDTO, activityList, autoFillGapSetting);
                break;
            case HIGHEST_RANKED_ACTIVITY_PLANNED_ADJACENT_TO_THE_GAP_SOLVING_MORE_PROBLEMS_THAN_CAUSING :
                if(!NON_PRODUCTIVE_TYPE_ON_BOTH_SIDE.equals(autoFillGapSetting.getAutoGapFillingScenario())) {
                    activityId = getHighestRankActivityOfAdjacentToGapSolvingProblems(shiftActivityBeforeGap, shiftActivityAfterGap, staffingLevelActivityWithDurationMap);
                } else {
                    showError = true;
                }
                break;
            case HIGHEST_RANKED_ACTIVITY_IF_IT_IS_SOLVING_MORE_PROBLEMS_THAN_CAUSING :
                activityId = getHighestRankActivity(staffAdditionalInfoDTO, activityList, autoFillGapSetting);
                if(!(isNotNull(activityId) && staffingLevelActivityWithDurationMap.containsKey(activityId) && staffingLevelActivityWithDurationMap.get(activityId).getOverStaffingDurationInMinutes() < staffingLevelActivityWithDurationMap.get(activityId).getResolvingUnderOrOverStaffingDurationInMinutes())) {
                    activityId = null;
                }
                break;
            case HIGHEST_RANKED_ACTIVITY_AMONGST_THAT_ARE_SOLVING_MORE_PROBLEMS_THAN_CAUSING :
                activityId = getHighestRankActivityOfSolvingProblems(staffAdditionalInfoDTO, activityList, autoFillGapSetting, staffingLevelActivityWithDurationMap);
                break;
            case DO_NOT_ALLOW_TO_CAUSE_GAP :
                exceptionService.actionNotPermittedException(DO_NOT_ALLOW_TO_CAUSE_GAP);
                break;
            default:
        }
        return new Object[]{activityId,showError};
    }

    private BigInteger getHighestRankActivityOfAdjacentToGapSolvingProblems(ShiftActivityDTO shiftActivityBeforeGap, ShiftActivityDTO shiftActivityAfterGap, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        BigInteger beforeActivityId = staffingLevelActivityWithDurationMap.containsKey(shiftActivityBeforeGap.getActivityId()) && staffingLevelActivityWithDurationMap.get(shiftActivityBeforeGap.getActivityId()).getResolvingUnderOrOverStaffingDurationInMinutes() > staffingLevelActivityWithDurationMap.get(shiftActivityBeforeGap.getActivityId()).getOverStaffingDurationInMinutes() ? shiftActivityBeforeGap.getActivityId() : null;
        BigInteger afterActivityId = staffingLevelActivityWithDurationMap.containsKey(shiftActivityAfterGap.getActivityId()) && staffingLevelActivityWithDurationMap.get(shiftActivityAfterGap.getActivityId()).getResolvingUnderOrOverStaffingDurationInMinutes() > staffingLevelActivityWithDurationMap.get(shiftActivityAfterGap.getActivityId()).getOverStaffingDurationInMinutes() ? shiftActivityAfterGap.getActivityId() : null;
        BigInteger activityId = null;
        if(isNotNull(beforeActivityId) && isNotNull(afterActivityId)){
            if (shiftActivityBeforeGap.getActivity().getRanking() < shiftActivityAfterGap.getActivity().getRanking()) {
                activityId = beforeActivityId;
            } else {
                activityId = afterActivityId;
            }
        } else if(isNotNull(beforeActivityId)){
            activityId = beforeActivityId;
        } else if(isNotNull(afterActivityId)){
            activityId = afterActivityId;
        }
        return activityId;
    }

    private BigInteger getHighestRankActivity(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ActivityWrapper> activityList, AutoFillGapSettings autoFillGapSetting) {
        BigInteger activityId;
        boolean canRankTeam = staffAdditionalInfoDTO.getCanRankTeam();
        if(canRankTeam && AccessGroupRole.STAFF.equals(autoFillGapSetting.getGapApplicableFor())){
            List<TeamRankingInfoDTO> sortedStaffTeamRanking = staffAdditionalInfoDTO.getStaffTeamRankingInfoData().stream().sorted(Comparator.comparing(TeamRankingInfoDTO::getRank)).collect(Collectors.toList());
            activityId = isCollectionNotEmpty(sortedStaffTeamRanking) ? sortedStaffTeamRanking.get(0).getActivityId() : null;
        } else {
            List<ActivityWrapper> sortedActivityWrapper = activityList.stream().sorted(Comparator.comparing(ActivityWrapper::getRanking)).collect(Collectors.toList());
            activityId = isCollectionNotEmpty(sortedActivityWrapper) ? sortedActivityWrapper.get(0).getActivity().getId() : null;
        }
        return activityId;
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

    private BigInteger getHighestRankActivityOfSolvingProblems(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<ActivityWrapper> activityList, AutoFillGapSettings autoFillGapSetting, Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMap) {
        List<BigInteger> allActivitySolvingMaxDuration = staffingLevelActivityWithDurationMap.values().stream().filter(k->k.getResolvingUnderOrOverStaffingDurationInMinutes() > k.getOverStaffingDurationInMinutes()).collect(Collectors.toList()).stream().map(StaffingLevelActivityWithDuration::getActivityId).collect(Collectors.toList());
        if (allActivitySolvingMaxDuration.isEmpty()) {
            return null;
        }
        BigInteger activityId = null;
        boolean canRankTeam = staffAdditionalInfoDTO.getCanRankTeam();
        if(canRankTeam && AccessGroupRole.STAFF.equals(autoFillGapSetting.getGapApplicableFor())){
            List<TeamRankingInfoDTO> sortedStaffTeamRanking = staffAdditionalInfoDTO.getStaffTeamRankingInfoData().stream().sorted(Comparator.comparing(TeamRankingInfoDTO::getRank)).collect(Collectors.toList());
            for (TeamRankingInfoDTO teamRankingInfoDTO : sortedStaffTeamRanking) {
                if(allActivitySolvingMaxDuration.contains(teamRankingInfoDTO.getActivityId())){
                    activityId = teamRankingInfoDTO.getActivityId();
                    break;
                }
            }
        } else {
            List<ActivityWrapper> sortedActivityWrapper = activityList.stream().sorted(Comparator.comparing(ActivityWrapper::getRanking)).collect(Collectors.toList());
            for (ActivityWrapper activityWrapper : sortedActivityWrapper) {
                if(allActivitySolvingMaxDuration.contains(activityWrapper.getActivity().getId())){
                    activityId = activityWrapper.getActivity().getId();
                    break;
                }
            }
        }
        return activityId;
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
            staffingLevelValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, true, shiftActivity, staffingLevelActivityWithDurationMap, true);
        }
        return staffingLevelActivityWithDurationMap;
    }

    private void filterActivities(List<TeamRankingInfoDTO> teamDTOS, Set<BigInteger> activityIds, Set<BigInteger> activityIdsDB) {
        activityIdsDB.removeAll(activityIds);
        teamDTOS.removeIf(k -> activityIdsDB.contains(k.getActivityId()));
    }

    private List<ActivityWrapper> filterParentActivities(List<ActivityWrapper> activityWrappers) {
        List<ActivityWrapper> temp = ObjectMapperUtils.copyCollectionPropertiesByMapper(activityWrappers, ActivityWrapper.class);
        temp.removeIf(current -> activityWrappers.stream().anyMatch(k -> k.getActivity().getChildActivityIds().contains(current.getActivity().getId())));
        return temp;
    }

    private void resetCacheData(AutoFillGapSettings autoFillGapSettings) {
        if (isNotNull(autoFillGapSettings.getCountryId())) {
            redisService.removeKeyFromCacheAsyscronously(newHashSet("getAllAutoFillGapSettingsByCountryId::" + autoFillGapSettings.getCountryId()));
        } else if (isNotNull(autoFillGapSettings.getUnitId())) {
            redisService.removeKeyFromCacheAsyscronously(newHashSet("getAllAutoFillGapSettingsByUnitId::" + autoFillGapSettings.getUnitId()));
        }
    }

}
