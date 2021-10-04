package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.PriorityFor;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.payroll_setting.PayrollPeriod;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityRankingRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.CommonConstants.FULL_WEEK;

@Service
public class ActivityRankingService {
    @Inject
    private ActivityRankingRepository activityRankingRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityService activityService;
    @Inject private TimeTypeMongoRepository timeTypeMongoRepository;

    public ActivityRankingDTO saveActivityRanking(ActivityRankingDTO activityRankingDTO){
        ActivityRanking activityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
        activityRankingRepository.save(activityRanking);
        activityRankingDTO.setId(activityRanking.getId());
        return activityRankingDTO;
    }

    public ActivityRankingDTO updateActivityRankingSettings(ActivityRankingDTO activityRankingDTO){
        ActivityRanking activityRanking = activityRankingRepository.findById(activityRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking", activityRankingDTO.getId())));
        if (isNotNull(activityRanking.getDraftId())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        if (activityRanking.isPublished()) {
            ActivityRanking activityRankingCopy = ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
            activityRankingCopy.setPublished(false);
            activityRankingCopy.setId(null);
            activityRankingRepository.save(activityRankingCopy);
            activityRanking.setDraftId(activityRankingCopy.getId());
        } else {
            activityRanking =ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
        }
        activityRankingRepository.save(activityRanking);
        return activityRankingDTO;
    }

    public List<ActivityRankingDTO> getAbsenceRankingSettings(Long expertiseId, Boolean published){
        List<ActivityRanking> activityRankings;
        if(isNotNull(published)){
            activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, published);
        } else {
            activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
        }
        activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(activityRankings, ActivityRankingDTO.class);
    }

    public List<ActivityRankingDTO> getPresenceRankingSettings(Long unitId){
        List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndDeletedFalse(unitId);
        activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
        return isCollectionEmpty(activityRankings) ? new ArrayList<>() : ObjectMapperUtils.copyCollectionPropertiesByMapper(activityRankings, ActivityRankingDTO.class);
    }

    public boolean deleteActivityRankingSettings(BigInteger id){
        ActivityRanking activityRanking = activityRankingRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking Settings", id)));
        if(activityRanking.isPublished()){
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_ALREADY_PUBLISHED);
        }
        activityRanking.setDeleted(true);
        activityRankingRepository.save(activityRanking);
        ActivityRanking parentAbsenceRanking = activityRankingRepository.findByDraftIdAndDeletedFalse(activityRanking.getId());
        if(isNotNull(parentAbsenceRanking)) {
            parentAbsenceRanking.setDraftId(null);
            activityRankingRepository.save(parentAbsenceRanking);
        }
        return true;
    }


    public ActivityRankingDTO publishActivityRanking(BigInteger id, LocalDate publishedDate) {
        ActivityRanking activityRanking = activityRankingRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking Settings", id)));
        if (activityRanking.getFullDayActivities().isEmpty() && activityRanking.getFullWeekActivities().isEmpty() && activityRanking.getPresenceActivities().isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_EMPTY);
        }
        if (activityRanking.isPublished()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_RANKING_ALREADY_PUBLISHED);
        }
        ActivityRanking parentAbsenceRanking = activityRankingRepository.findByDraftIdAndDeletedFalse(activityRanking.getId());
        if(publishedDate.isBefore(parentAbsenceRanking.getStartDate()) && isNotNull(parentAbsenceRanking.getEndDate()) && publishedDate.isAfter(parentAbsenceRanking.getEndDate())){
            exceptionService.actionNotPermittedException(ERROR_PUBLISH_DATE_INVALID);
        }
        if(publishedDate.isEqual(parentAbsenceRanking.getStartDate())){
            activityRanking.setDeleted(true);
            parentAbsenceRanking.setFullDayActivities(activityRanking.getFullDayActivities());
            parentAbsenceRanking.setFullWeekActivities(activityRanking.getFullWeekActivities());
            parentAbsenceRanking.setPresenceActivities(activityRanking.getPresenceActivities());
        } else {
            activityRanking.setPublished(true);
            activityRanking.setStartDate(publishedDate);
            activityRanking.setEndDate(parentAbsenceRanking.getEndDate());
            parentAbsenceRanking.setEndDate(publishedDate.minusDays(1));
        }
        parentAbsenceRanking.setDraftId(null);
        activityRankingRepository.saveEntities(newArrayList(activityRanking,parentAbsenceRanking));
        List<ActivityRanking> activityRankings = isNotNull(parentAbsenceRanking.getExpertiseId()) ? activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(parentAbsenceRanking.getExpertiseId(), true) : activityRankingRepository.getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(parentAbsenceRanking.getUnitId());
        mergeActivityRanking(activityRankings, isNull(parentAbsenceRanking.getExpertiseId()));
        return ObjectMapperUtils.copyPropertiesByMapper(parentAbsenceRanking, ActivityRankingDTO.class);
    }

    public Map<String,List<ActivityDTO>> findAllAbsenceActivities(){
        List<ActivityDTO> fullDayActivities = new ArrayList<>();
        List<ActivityDTO> fullWeekActivities = new ArrayList<>();
        List<Activity> activities = activityService.findAllActivitiesByCountryAndTimeTypePriority(UserContext.getUserDetails().getCountryId(), true, PriorityFor.ABSENCE);
        activities.forEach(activity -> {
            ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activity, ActivityDTO.class);
            if(CommonConstants.FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                fullWeekActivities.add(activityDTO);
            } else {
                fullDayActivities.add(activityDTO);
            }
        });
        Map<String,List<ActivityDTO>> resultMap = new HashMap<>();
        resultMap.put("fullDayActivities", fullDayActivities);
        resultMap.put("fullWeekActivities", fullWeekActivities);
        return resultMap;
    }

    public List<ActivityDTO> findAllPresenceActivities(Long unitId){
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(activityService.findAllActivitiesByCountryAndTimeTypePriority(unitId, false, PriorityFor.PRESENCE), ActivityDTO.class);
    }

    @Async
    public void updateTimeCalculationInActivity(Activity activity, Activity oldActivity){
        if (FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime()) || FULL_WEEK.equals(oldActivity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
            this.removeAbsenceActivityId(oldActivity, oldActivity.getExpertises());
            this.createOrUpdateAbsenceActivityRanking(activity, activity.getExpertises());
        }
    }

    @Async
    public void createOrUpdateAbsenceActivityRanking(Activity activity, List<Long> expertiseIds){
        for (Long expertiseId : expertiseIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, true);
            if(isCollectionEmpty(activityRankings)){
                ActivityRanking newActivityRanking;
                if(FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                    newActivityRanking = new ActivityRanking(expertiseId, activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), new LinkedHashSet<>(), newLinkedHashSet(activity.getId()), activity.getCountryId(), true);
                } else {
                    newActivityRanking = new ActivityRanking(expertiseId, activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), newLinkedHashSet(activity.getId()), new LinkedHashSet<>(), activity.getCountryId(), true);
                }
                activityRankingRepository.save(newActivityRanking);
            } else {
                LocalDate activityEndDate = activity.getActivityGeneralSettings().getEndDate();
                activity.getActivityGeneralSettings().setEndDate(null);
                modifyActivityRanking(activityRankings, activity, expertiseId, false);
                if(isNotNull(activityEndDate)){
                    activity.getActivityGeneralSettings().setEndDate(activityEndDate);
                    activityRankings = activityRankingRepository.getAbsenceRankingSettings(expertiseId, activity.getActivityGeneralSettings().getEndDate());
                    updateRankingOnSetActivityEndDate(activityRankings, activity, false);
                }
            }
        }
        this.removeActivityFromDraftRanking();
    }

    @Async
    public void addOrRemovePresenceActivityRanking(Long unitId, Activity activity, boolean addActivityId) {
        List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(unitId);
        if(addActivityId) {
            if (isCollectionEmpty(activityRankings)) {
                activityRankingRepository.save(new ActivityRanking(activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), newLinkedHashSet(activity.getId()), unitId, true));
            } else {
                LocalDate activityEndDate = activity.getActivityGeneralSettings().getEndDate();
                activity.getActivityGeneralSettings().setEndDate(null);
                modifyActivityRanking(activityRankings, activity, unitId, true);
                if(isNotNull(activityEndDate)){
                    activity.getActivityGeneralSettings().setEndDate(activityEndDate);
                    activityRankings = activityRankingRepository.getPresenceRankingSettings(unitId, activity.getActivityGeneralSettings().getEndDate());
                    updateRankingOnSetActivityEndDate(activityRankings, activity, true);
                }
            }
        } else {
            for (ActivityRanking activityRanking : activityRankings) {
                activityRanking.getPresenceActivities().remove(activity.getId());
                if(isCollectionEmpty(activityRanking.getPresenceActivities())){
                    activityRanking.setDeleted(true);
                }
            }
            activityRankingRepository.saveEntities(activityRankings);
            mergeActivityRanking(activityRankings, true);
        }
        this.removeActivityFromDraftRanking();
    }

    private void modifyActivityRanking(List<ActivityRanking> activityRankings, Activity activity,long unitOrExpertiseId, boolean presenceActivity){
        List<ActivityRanking> newActivityRankings = new ArrayList<>();
        activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
        if(activity.getActivityGeneralSettings().getStartDate().isBefore(activityRankings.get(0).getStartDate())){
            ActivityRanking newActivityRanking;
            if(presenceActivity){
                newActivityRanking = new ActivityRanking(activity.getActivityGeneralSettings().getStartDate(), activityRankings.get(0).getStartDate().minusDays(1), newLinkedHashSet(activity.getId()), unitOrExpertiseId, true);
            } else if(FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                newActivityRanking = new ActivityRanking(unitOrExpertiseId, activity.getActivityGeneralSettings().getStartDate(), activityRankings.get(0).getStartDate().minusDays(1), new LinkedHashSet<>(), newLinkedHashSet(activity.getId()), activity.getCountryId(), true);
            } else {
                newActivityRanking = new ActivityRanking(unitOrExpertiseId, activity.getActivityGeneralSettings().getStartDate(), activityRankings.get(0).getStartDate().minusDays(1), newLinkedHashSet(activity.getId()), new LinkedHashSet<>(), activity.getCountryId(), true);
            }
            activityRankingRepository.save(newActivityRanking);
            newActivityRankings.add(newActivityRanking);
        }
        addActivityInRanking(activityRankings, activity, presenceActivity, newActivityRankings);
        if(isCollectionNotEmpty(newActivityRankings)){
            activityRankings.addAll(newActivityRankings);
        }
        activityRankingRepository.saveEntities(activityRankings);
        mergeActivityRanking(activityRankings, presenceActivity);
    }

    private void addActivityInRanking(List<ActivityRanking> activityRankings, Activity activity, boolean presenceActivity, List<ActivityRanking> newActivityRankings) {
        for (ActivityRanking activityRanking : activityRankings) {
            if(activityRanking.getStartDate().isBefore(activity.getActivityGeneralSettings().getStartDate()) && (isNull(activityRanking.getEndDate()) || activityRanking.getEndDate().isAfter(activity.getActivityGeneralSettings().getStartDate()))) {
                newActivityRankings.add(createNewAbsenceRanking(activity, presenceActivity, activityRanking));
            } else if(!activity.getActivityGeneralSettings().getStartDate().isAfter(activityRanking.getStartDate())) {
                if (presenceActivity) {
                    activityRanking.getPresenceActivities().add(activity.getId());
                } else if (FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
                    activityRanking.getFullWeekActivities().add(activity.getId());
                } else {
                    activityRanking.getFullDayActivities().add(activity.getId());
                }
            }
        }
    }

    private ActivityRanking createNewAbsenceRanking(Activity activity, boolean presenceActivity, ActivityRanking activityRanking) {
        ActivityRanking newActivityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRanking, ActivityRanking.class);
        newActivityRanking.setStartDate(activity.getActivityGeneralSettings().getStartDate());
        newActivityRanking.setId(null);
        activityRanking.setEndDate(activity.getActivityGeneralSettings().getStartDate().minusDays(1));
        if (presenceActivity) {
            newActivityRanking.getPresenceActivities().add(activity.getId());
        } else if (FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
            newActivityRanking.getFullWeekActivities().add(activity.getId());
        } else {
            newActivityRanking.getFullDayActivities().add(activity.getId());
        }
        return  newActivityRanking;
    }

    @Async
    public void removeAbsenceActivityId(Activity activity, List<Long> expertiseIds){
        for (Long expertiseId : expertiseIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, true);
            for (ActivityRanking activityRanking : activityRankings) {
                if(activityRanking.getFullDayActivities().remove(activity.getId()) || activityRanking.getFullWeekActivities().remove(activity.getId())){
                    if(isCollectionEmpty(activityRanking.getFullDayActivities()) && isCollectionEmpty(activityRanking.getFullWeekActivities())){
                        activityRanking.setDeleted(true);
                    }
                }
            }
            if(isCollectionNotEmpty(activityRankings)) {
                activityRankingRepository.saveEntities(activityRankings);
                mergeActivityRanking(activityRankings, false);
            }
        }
        this.removeActivityFromDraftRanking();
    }

    private void removeActivityFromDraftRanking(){
        List<ActivityRanking> activityRankings = activityRankingRepository.getAllDraftRankings();
        List<ActivityRanking> parentActivityRankings = activityRankingRepository.getAllRankingByDraftId(activityRankings.stream().map(ActivityRanking::getId).collect(Collectors.toList()));
        Map<BigInteger, ActivityRanking> parentActivityRankMap = parentActivityRankings.stream().collect(Collectors.toMap(ActivityRanking::getDraftId, v->v));
        for (ActivityRanking activityRanking : activityRankings) {
            if(parentActivityRankMap.containsKey(activityRanking.getId())){
                ActivityRanking parentActivityRanking = parentActivityRankMap.get(activityRanking.getId());
                activityRanking.setFullDayActivities(parentActivityRanking.getFullDayActivities());
                activityRanking.setFullWeekActivities(parentActivityRanking.getFullWeekActivities());
                activityRanking.setPresenceActivities(parentActivityRanking.getPresenceActivities());
                activityRanking.setStartDate(parentActivityRanking.getStartDate());
                activityRanking.setEndDate(parentActivityRanking.getEndDate());
            } else {
                activityRanking.setDeleted(true);
            }
        }
        if(isCollectionNotEmpty(activityRankings)) {
            activityRankingRepository.saveEntities(activityRankings);
        }
    }

    private void mergeActivityRanking(List<ActivityRanking> activityRankings, boolean presenceActivity) {
        Map<BigInteger,ActivityRanking> mergeActivityRankings = new HashMap<>();
        List<BigInteger> deleteDraftCopy = new ArrayList<>();
        if(isCollectionNotEmpty(activityRankings) && activityRankings.size() > 1) {
            activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
            for(int index=0; index < activityRankings.size()-1; index++){
                ActivityRanking activityRanking = activityRankings.get(index);
                ActivityRanking nextActivityRanking = activityRankings.get(index+1);
                checkAndMerge(mergeActivityRankings, activityRanking, nextActivityRanking, presenceActivity, deleteDraftCopy);
            }
        } else if(isCollectionNotEmpty(activityRankings) && ((presenceActivity && isCollectionEmpty(activityRankings.get(0).getPresenceActivities())) ||(!presenceActivity && isCollectionEmpty(activityRankings.get(0).getFullDayActivities()) && isCollectionEmpty(activityRankings.get(0).getFullWeekActivities())))){
            activityRankings.get(0).setDeleted(true);
            mergeActivityRankings.put(activityRankings.get(0).getId(), activityRankings.get(0));
            if(isNotNull(activityRankings.get(0).getDraftId())){
                deleteDraftCopy.add(activityRankings.get(0).getDraftId());
            }
        }
        if(isMapNotEmpty(mergeActivityRankings)){
            activityRankingRepository.saveEntities(mergeActivityRankings.values());
        }
        if(isCollectionNotEmpty(deleteDraftCopy)){
            List<ActivityRanking> draftRankings = activityRankingRepository.getAllDraftByIds(deleteDraftCopy);
            draftRankings.forEach(ranking-> ranking.setDeleted(true));
            activityRankingRepository.saveEntities(draftRankings);
        }
    }

    private void checkAndMerge(Map<BigInteger, ActivityRanking> mergeActivityRankings, ActivityRanking activityRanking, ActivityRanking nextActivityRanking, boolean presenceActivity, List<BigInteger> deleteDraftCopy) {
        if(presenceActivity){
            activityRanking.setDeleted(isCollectionEmpty(activityRanking.getPresenceActivities()));
            nextActivityRanking.setDeleted(isCollectionEmpty(nextActivityRanking.getPresenceActivities()));
        } else {
            activityRanking.setDeleted(isCollectionEmpty(activityRanking.getFullWeekActivities()) && isCollectionEmpty(activityRanking.getFullDayActivities()));
            nextActivityRanking.setDeleted(isCollectionEmpty(nextActivityRanking.getFullWeekActivities()) && isCollectionEmpty(nextActivityRanking.getFullDayActivities()));
        }
        if((presenceActivity && isSetSame(activityRanking.getPresenceActivities(), nextActivityRanking.getPresenceActivities())) ||
                (!presenceActivity && isSetSame(activityRanking.getFullDayActivities(), nextActivityRanking.getFullDayActivities()) && isSetSame(activityRanking.getFullDayActivities(), nextActivityRanking.getFullDayActivities()))){
            activityRanking.setDeleted(true);
            nextActivityRanking.setStartDate(activityRanking.getStartDate());
        }
        if(activityRanking.isDeleted() && isNotNull(activityRanking.getDraftId())){
            deleteDraftCopy.add(activityRanking.getDraftId());
        }
        if(nextActivityRanking.isDeleted() && isNotNull(nextActivityRanking.getDraftId())){
            deleteDraftCopy.add(nextActivityRanking.getDraftId());
        }
        mergeActivityRankings.put(activityRanking.getId(), activityRanking);
        mergeActivityRankings.put(nextActivityRanking.getId(), nextActivityRanking);
    }

    private boolean isSetSame(LinkedHashSet<BigInteger> firstSet, LinkedHashSet<BigInteger> secondSet) {
        boolean isSame = firstSet.size() == secondSet.size();
        if(isSame){
            Object[] activities = secondSet.toArray();
            int index = 0;
            for (BigInteger activityId : firstSet) {
                if(!activityId.equals(activities[index++])){
                    isSame = false;
                    break;
                }
            }
        }
        return isSame;
    }

    @Async
    public void updateEndDateOfAbsenceActivity(Activity activity, LocalDate oldEndDate) {
        LocalDate newEndDate = activity.getActivityGeneralSettings().getEndDate();
        if(isCollectionNotEmpty(activity.getExpertises())){
            for (Long expertiseId : activity.getExpertises()) {
                if (isNull(oldEndDate)) {
                    List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettings(expertiseId, activity.getActivityGeneralSettings().getEndDate());
                    updateRankingOnSetActivityEndDate(activityRankings, activity, false);
                } else if(isNull(newEndDate)) {
                    List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, true);
                    updateRankingOnResetActivityEndDate(activityRankings, activity, oldEndDate, expertiseId, false);
                } else {
                    activity.getActivityGeneralSettings().setEndDate(null);
                    List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, true);
                    updateRankingOnResetActivityEndDate(activityRankings, activity, oldEndDate, expertiseId, false);
                    activity.getActivityGeneralSettings().setEndDate(newEndDate);
                    activityRankings = activityRankingRepository.getAbsenceRankingSettings(expertiseId, activity.getActivityGeneralSettings().getEndDate());
                    updateRankingOnSetActivityEndDate(activityRankings, activity, false);
                }
            }
        }
    }

    @Async
    public void updateEndDateOfPresenceActivity(Long unitId, Activity activity, LocalDate oldEndDate) {
        LocalDate newEndDate = activity.getActivityGeneralSettings().getEndDate();
        if (isNull(oldEndDate)) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getPresenceRankingSettings(unitId, activity.getActivityGeneralSettings().getEndDate());
            updateRankingOnSetActivityEndDate(activityRankings, activity, true);
        } else if(isNull(newEndDate)) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(unitId);
            updateRankingOnResetActivityEndDate(activityRankings, activity, oldEndDate, unitId, true);
        } else {
            activity.getActivityGeneralSettings().setEndDate(null);

            List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(unitId);
            updateRankingOnResetActivityEndDate(activityRankings, activity, oldEndDate, unitId, true);
            activity.getActivityGeneralSettings().setEndDate(newEndDate);
            activityRankings = activityRankingRepository.getPresenceRankingSettings(unitId, activity.getActivityGeneralSettings().getEndDate());
            updateRankingOnSetActivityEndDate(activityRankings, activity, true);
        }
    }

    private void updateRankingOnResetActivityEndDate(List<ActivityRanking> activityRankings, Activity activity, LocalDate oldEndDate,long unitOrexpertiseId, boolean presenceActivity) {
        List<ActivityRanking> updateActivityRankings = new ArrayList<>();
        activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
        for (ActivityRanking activityRanking : activityRankings) {
            if (isNull(activity.getActivityGeneralSettings().getEndDate())) {
                if(!activityRanking.getStartDate().isBefore(oldEndDate)) {
                    if(presenceActivity) {
                        activityRanking.getPresenceActivities().add(activity.getId());
                    } else if (FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
                        activityRanking.getFullWeekActivities().add(activity.getId());
                    } else {
                        activityRanking.getFullDayActivities().add(activity.getId());
                    }
                    updateActivityRankings.add(activityRanking);
                }
            }
        }
        if(isCollectionNotEmpty(activityRankings) && isNotNull(activityRankings.get(activityRankings.size()-1).getEndDate())){
            ActivityRanking newActivityRanking;
            if(presenceActivity){
                newActivityRanking = new ActivityRanking(activityRankings.get(activityRankings.size()-1).getEndDate().plusDays(1), null, newLinkedHashSet(activity.getId()), unitOrexpertiseId, true);
            } else if(FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                newActivityRanking = new ActivityRanking(unitOrexpertiseId, activityRankings.get(activityRankings.size()-1).getEndDate().plusDays(1), null, new LinkedHashSet<>(), newLinkedHashSet(activity.getId()), activity.getCountryId(), true);
            } else {
                newActivityRanking = new ActivityRanking(unitOrexpertiseId,activityRankings.get(activityRankings.size()-1).getEndDate().plusDays(1), null, newLinkedHashSet(activity.getId()), new LinkedHashSet<>(), activity.getCountryId(), true);
            }
            activityRankingRepository.save(newActivityRanking);
            activityRankings.add(newActivityRanking);
        }
        if(isCollectionNotEmpty(updateActivityRankings)) {
            activityRankingRepository.saveEntities(updateActivityRankings);
        }
        mergeActivityRanking(activityRankings, presenceActivity);
    }

    private void updateRankingOnSetActivityEndDate(List<ActivityRanking> activityRankings, Activity activity, boolean presenceActivity) {
        if(isCollectionNotEmpty(activityRankings)) {
            activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
            ActivityRanking newActivityRanking = null;
            if (isNull(activityRankings.get(0).getEndDate()) || activityRankings.get(0).getEndDate().isAfter(activity.getActivityGeneralSettings().getEndDate())) {
                newActivityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRankings.get(0), ActivityRanking.class);
                newActivityRanking.setId(null);
                newActivityRanking.setStartDate(activity.getActivityGeneralSettings().getEndDate().plusDays(1));
                activityRankings.get(0).setEndDate(activity.getActivityGeneralSettings().getEndDate());
                if(presenceActivity) {
                    newActivityRanking.getPresenceActivities().remove(activity.getId());
                } else if (FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
                    newActivityRanking.getFullWeekActivities().remove(activity.getId());
                } else {
                    newActivityRanking.getFullDayActivities().remove(activity.getId());
                }
                activityRankingRepository.saveEntities(newArrayList(newActivityRanking,activityRankings.get(0)));
            }
            removeActivityFromRanking(activity, activityRankings, presenceActivity);
            if (isNotNull(newActivityRanking)) {
                activityRankings.add(newActivityRanking);
            }
            mergeActivityRanking(activityRankings, presenceActivity);
        }
    }

    private void removeActivityFromRanking(Activity activity, List<ActivityRanking> activityRankings, boolean presenceActivity) {
        if (activityRankings.size() > 1) {
            for (int index = 1; index < activityRankings.size(); index++) {
                if(presenceActivity) {
                    activityRankings.get(index).getPresenceActivities().remove(activity.getId());
                } else if (FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())) {
                    activityRankings.get(index).getFullWeekActivities().remove(activity.getId());
                } else {
                    activityRankings.get(index).getFullDayActivities().remove(activity.getId());
                }
            }
        }
    }

    public ActivityRanking getCurrentlyActiveActivityRankingSettings(Long unitId,LocalDate shiftDate){
        return activityRankingRepository.getCurrentlyActiveActivityRankingSettings(unitId,shiftDate);
    }

    @Async
    public void updateRankListOnChangeActivityChildList(Long unitId, Set<BigInteger> removeActivityIds, Set<BigInteger> addActivityIds, List<Activity> activities) {
        Map<BigInteger, Activity> activityMap = activities.stream().collect(Collectors.toMap(Activity::getId, v->v));
        if(isCollectionNotEmpty(removeActivityIds)) {
            removeAllActivityFromPresenceRanking(newHashSet(unitId), removeActivityIds);
        }
        if(isCollectionNotEmpty(addActivityIds)) {
            for (BigInteger addActivityId : addActivityIds) {
                Activity activity = activityMap.get(addActivityId);
                if(PriorityFor.PRESENCE.equals(activity.getActivityBalanceSettings().getPriorityFor())) {
                    addOrRemovePresenceActivityRanking(unitId, activity, true);
                }
            }
        }
    }

    private void removeAllActivityFromPresenceRanking(Set<Long> unitIds, Set<BigInteger> removeActivityIds) {
        for (Long unitId : unitIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(unitId);
            for (ActivityRanking activityRanking : activityRankings) {
                activityRanking.getPresenceActivities().removeAll(removeActivityIds);
                if (isCollectionEmpty(activityRanking.getPresenceActivities())) {
                    activityRanking.setDeleted(true);
                }
            }
            activityRankingRepository.saveEntities(activityRankings);
            mergeActivityRanking(activityRankings, true);
        }
    }

    @Async
    public void updateRankingListOnUpdatePriorityFor(BigInteger timeTypeId, PriorityFor priorityFor, PriorityFor oldPriorityFor) {
        List<Activity> activities = activityService.findAllByTimeTypeId(timeTypeId);
        List<Activity> unitActivities = activities.stream().filter(activity -> isNotNull(activity.getUnitId())).collect(Collectors.toList());
        List<Activity> countryActivities = activities.stream().filter(activity -> isNotNull(activity.getCountryId())).collect(Collectors.toList());
        Set<BigInteger> unitActivityIds = unitActivities.stream().map(Activity::getId).collect(Collectors.toSet());
        Set<Long> unitIds = unitActivities.stream().map(Activity::getUnitId).collect(Collectors.toSet());
        Set<BigInteger> countryActivityIds = countryActivities.stream().map(Activity::getId).collect(Collectors.toSet());
        Set<Long> expertiseIds = countryActivities.stream().flatMap(activity -> activity.getExpertises().stream()).collect(Collectors.toSet());
        if(PriorityFor.PRESENCE.equals(oldPriorityFor) && isCollectionNotEmpty(unitActivityIds)){
            removeAllActivityFromPresenceRanking(unitIds, unitActivityIds);
        } else if(PriorityFor.ABSENCE.equals(oldPriorityFor) && isCollectionNotEmpty(countryActivityIds)){
            removeAllActivityFromAbsenceRanking(expertiseIds, countryActivityIds);
        }
        if(PriorityFor.PRESENCE.equals(priorityFor)){
            for (Activity unitActivity : unitActivities) {
                addOrRemovePresenceActivityRanking(unitActivity.getUnitId(), unitActivity, true);
            }
        } else if(PriorityFor.ABSENCE.equals(priorityFor)){
            for (Activity countryActivity : countryActivities) {
                createOrUpdateAbsenceActivityRanking(countryActivity, countryActivity.getExpertises());
            }
        }
    }

    private void removeAllActivityFromAbsenceRanking(Set<Long> expertiseIds, Set<BigInteger> activityIds) {
        for (Long expertise : expertiseIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertise, true);
            for (ActivityRanking activityRanking : activityRankings) {
                activityRanking.getFullDayActivities().removeAll(activityIds);
                activityRanking.getFullWeekActivities().removeAll(activityIds);
                if (isCollectionEmpty(activityRanking.getFullDayActivities()) && isCollectionEmpty(activityRanking.getFullWeekActivities())) {
                    activityRanking.setDeleted(true);
                }
            }
            activityRankingRepository.saveEntities(activityRankings);
            mergeActivityRanking(activityRankings, false);
        }
    }

    public boolean createPresenceRanking(Long unitId){
        List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndDeletedFalse(unitId);
        activityRankings.forEach(activityRanking -> activityRanking.setDeleted(true));
        activityRankingRepository.saveEntities(activityRankings);
        List<Activity> activities = activityService.findAllActivitiesByCountryAndTimeTypePriority(unitId, false, PriorityFor.PRESENCE);
        for (Activity activity : activities) {
            if(ActivityStateEnum.PUBLISHED.equals(activity.getState()) && !activity.isChildActivity()) {
                addOrRemovePresenceActivityRanking(unitId, activity, true);
            }
        }
        return true;
    }

    public boolean createAbsenceRanking(Long countryId){
        List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByCountryIdAndDeletedFalse(countryId);
        activityRankings.forEach(activityRanking -> activityRanking.setDeleted(true));
        activityRankingRepository.saveEntities(activityRankings);
        List<Activity> activities = activityService.findAllActivitiesByCountryAndTimeTypePriority(countryId, true, PriorityFor.ABSENCE);
        for (Activity activity : activities) {
            if(ActivityStateEnum.PUBLISHED.equals(activity.getState()) && isCollectionNotEmpty(activity.getExpertises()) && !activity.isChildActivity()) {
                createOrUpdateAbsenceActivityRanking(activity, activity.getExpertises());
            }
        }
        return true;
    }

    public Map<Long, List<ActivityRanking>> getAllAbsenceActivitiesRanking(Long countryId) {
        List<ActivityRanking> activityRankingList = activityRankingRepository.getCurrentlyActiveActivityRankingSettings(countryId);
        return activityRankingList.stream().collect(Collectors.groupingBy(ActivityRanking::getExpertiseId));
    }
}