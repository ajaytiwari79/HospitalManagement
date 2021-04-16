package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.unit_settings.ActivityRankingRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.CommonConstants.FULL_WEEK;
import static com.kairos.enums.TimeTypeEnum.ABSENCE;
import static com.kairos.enums.TimeTypeEnum.PRESENCE;

@Service
public class ActivityRankingService {
    @Inject
    private ActivityRankingRepository activityRankingRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityService activityService;

    public ActivityRankingDTO saveAbsenceRankingSettings(ActivityRankingDTO activityRankingDTO){
        ActivityRanking activityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
        activityRankingRepository.save(activityRanking);
        activityRankingDTO.setId(activityRanking.getId());
        return activityRankingDTO;
    }

    public ActivityRankingDTO updateAbsenceRankingSettings(ActivityRankingDTO activityRankingDTO){
        ActivityRanking activityRanking = activityRankingRepository.findById(activityRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking", activityRankingDTO.getId())));
        if (activityRanking.getDraftId()!=null) {
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

    public boolean deleteAbsenceRankingSettings(BigInteger id){
        ActivityRanking activityRanking = activityRankingRepository.findOne(id);
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


    public ActivityRankingDTO publishAbsenceRanking(BigInteger id, LocalDate publishedDate) {
        ActivityRanking activityRanking = activityRankingRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Absence Ranking Settings", id)));
        if (activityRanking.getFullDayActivities().isEmpty() && activityRanking.getFullWeekActivities().isEmpty() && activityRanking.getPresenceActivities().isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_EMPTY);
        }
        if (activityRanking.isPublished()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_RANKING_ALREADY_PUBLISHED);
        }
        activityRanking.setPublished(true);
        activityRanking.setStartDate(publishedDate); // changing
        ActivityRanking parentAbsenceRanking = activityRankingRepository.findByDraftIdAndDeletedFalse(activityRanking.getId());
        ActivityRanking lastAbsenceRanking = activityRankingRepository.findTopByExpertiseIdAndDeletedFalseOrderByStartDateDesc(activityRanking.getExpertiseId());
        boolean onGoingUpdated = false;
        if (lastAbsenceRanking != null && publishedDate.isAfter(lastAbsenceRanking.getStartDate()) && lastAbsenceRanking.getEndDate() == null) {
            lastAbsenceRanking.setEndDate(publishedDate.minusDays(1));
            activityRankingRepository.save(lastAbsenceRanking);
            activityRanking.setEndDate(null);
            onGoingUpdated = true;
        }
        if (!onGoingUpdated && Optional.ofNullable(parentAbsenceRanking).isPresent()) {
            if (parentAbsenceRanking.getStartDate().isEqual(publishedDate) || parentAbsenceRanking.getStartDate().isAfter(publishedDate)) {
                exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISH_DATE_NOT_LESS_THAN_OR_EQUALS_PARENT_START_DATE);
            }
            parentAbsenceRanking.setEndDate(publishedDate.minusDays(1L));
            if (lastAbsenceRanking == null && activityRanking.getEndDate() != null && activityRanking.getEndDate().isBefore(publishedDate)) {
                activityRanking.setEndDate(null);
            }
        }
        if(isNotNull(parentAbsenceRanking)){
            parentAbsenceRanking.setDraftId(null);
            activityRankingRepository.save(parentAbsenceRanking);
        }
        activityRankingRepository.save(activityRanking);
        return ObjectMapperUtils.copyPropertiesByMapper(parentAbsenceRanking, ActivityRankingDTO.class);
    }

    public Map<String,List<ActivityDTO>> findAllAbsenceActivities(){
        List<ActivityDTO> fullDayActivities = new ArrayList<>();
        List<ActivityDTO> fullWeekActivities = new ArrayList<>();
        activityService.findAllActivitiesByTimeType(UserContext.getUserDetails().getCountryId(), ABSENCE).forEach(activityDTO -> {
            if(CommonConstants.FULL_WEEK.equals(activityDTO.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
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
        return activityService.findAllActivitiesByTimeType(unitId,PRESENCE);
    }

    @Async
    public void createOrUpdateAbsenceActivityRanking(Activity activity, List<Long> expertiseIds){
        for (Long expertiseId : expertiseIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, true);
            if(isCollectionEmpty(activityRankings)){
                ActivityRanking newActivityRanking;
                if(FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                    newActivityRanking = new ActivityRanking(expertiseId, activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), new HashSet<>(), newHashSet(activity.getId()), activity.getCountryId(), true);
                } else {
                    newActivityRanking = new ActivityRanking(expertiseId, activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), newHashSet(activity.getId()), new HashSet<>(), activity.getCountryId(), true);
                }
                activityRankingRepository.save(newActivityRanking);
            } else {
                LocalDate activityEndDate = activity.getActivityGeneralSettings().getEndDate();
                activity.getActivityGeneralSettings().setEndDate(null);
                this.modifyActivityRanking(activityRankings, activity, expertiseId, false);
                if(isNotNull(activityEndDate)){
                    activity.getActivityGeneralSettings().setEndDate(activityEndDate);
                    activityRankings = activityRankingRepository.getAbsenceRankingSettings(expertiseId, activity.getActivityGeneralSettings().getEndDate());
                    updateRankingOnSetActivityEndDate(activityRankings, activity, false);
                }
            }
        }
    }

    @Async
    public void addOrRemovePresenceActivityRanking(Long unitId, Activity activity, boolean addActivityId) {
        List<ActivityRanking> activityRankings = activityRankingRepository.getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(unitId);
        if(addActivityId) {
            if (isCollectionEmpty(activityRankings)) {
                activityRankingRepository.save(new ActivityRanking(activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), newHashSet(activity.getId()), unitId, true));
            } else {
                LocalDate activityEndDate = activity.getActivityGeneralSettings().getEndDate();
                activity.getActivityGeneralSettings().setEndDate(null);
                this.modifyActivityRanking(activityRankings, activity, unitId, true);
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
            this.mergeActivityRanking(activityRankings, true);
        }
    }

    private void modifyActivityRanking(List<ActivityRanking> activityRankings, Activity activity,long unitOrexpertiseId, boolean presenceActivity){
        List<ActivityRanking> newActivityRankings = new ArrayList<>();
        activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
        if(activity.getActivityGeneralSettings().getStartDate().isBefore(activityRankings.get(0).getStartDate())){
            ActivityRanking newActivityRanking;
            if(presenceActivity){
                newActivityRanking = new ActivityRanking(activity.getActivityGeneralSettings().getStartDate(), activityRankings.get(0).getStartDate().minusDays(1), newHashSet(activity.getId()), unitOrexpertiseId, true);
            } else if(FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                newActivityRanking = new ActivityRanking(unitOrexpertiseId, activity.getActivityGeneralSettings().getStartDate(), activityRankings.get(0).getStartDate().minusDays(1), new HashSet<>(), newHashSet(activity.getId()), activity.getCountryId(), true);
            } else {
                newActivityRanking = new ActivityRanking(unitOrexpertiseId, activity.getActivityGeneralSettings().getStartDate(), activityRankings.get(0).getStartDate().minusDays(1), newHashSet(activity.getId()), new HashSet<>(), activity.getCountryId(), true);
            }
            activityRankingRepository.save(newActivityRanking);
            newActivityRankings.add(newActivityRanking);
        }
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
        if(isCollectionNotEmpty(newActivityRankings)){
            activityRankings.addAll(newActivityRankings);
        }
        activityRankingRepository.saveEntities(activityRankings);
        this.mergeActivityRanking(activityRankings, presenceActivity);
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
                this.mergeActivityRanking(activityRankings, false);
            }
        }
    }

    private void mergeActivityRanking(List<ActivityRanking> activityRankings, boolean presenceActivity) {
        Map<BigInteger,ActivityRanking> mergeActivityRankings = new HashMap<>();
        if(isCollectionNotEmpty(activityRankings) && activityRankings.size() > 1) {
            activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
            for(int index=0; index < activityRankings.size()-1; index++){
                ActivityRanking activityRanking = activityRankings.get(index);
                ActivityRanking nextActivityRanking = activityRankings.get(index+1);
                checkAndMerge(mergeActivityRankings, activityRanking, nextActivityRanking, presenceActivity);
            }
        } else if(isCollectionNotEmpty(activityRankings) && ((presenceActivity && isCollectionEmpty(activityRankings.get(0).getPresenceActivities())) ||(!presenceActivity && isCollectionEmpty(activityRankings.get(0).getFullDayActivities()) && isCollectionEmpty(activityRankings.get(0).getFullWeekActivities())))){
            activityRankings.get(0).setDeleted(true);
            mergeActivityRankings.put(activityRankings.get(0).getId(), activityRankings.get(0));
        }
        if(isMapNotEmpty(mergeActivityRankings)){
            activityRankingRepository.saveEntities(mergeActivityRankings.values());
        }
    }

    private void checkAndMerge(Map<BigInteger, ActivityRanking> mergeActivityRankings, ActivityRanking activityRanking, ActivityRanking nextActivityRanking, boolean presenceActivity) {
        if(presenceActivity){
            activityRanking.setDeleted(isCollectionEmpty(activityRanking.getPresenceActivities()));
            nextActivityRanking.setDeleted(isCollectionEmpty(nextActivityRanking.getPresenceActivities()));
        } else {
            activityRanking.setDeleted(isCollectionEmpty(activityRanking.getFullWeekActivities()) && isCollectionEmpty(activityRanking.getFullDayActivities()));
            nextActivityRanking.setDeleted(isCollectionEmpty(nextActivityRanking.getFullWeekActivities()) && isCollectionEmpty(nextActivityRanking.getFullDayActivities()));
        }
        if((presenceActivity && activityRanking.getPresenceActivities().equals(nextActivityRanking.getPresenceActivities())) ||
                (!presenceActivity && activityRanking.getFullWeekActivities().equals(nextActivityRanking.getFullWeekActivities()) && activityRanking.getFullDayActivities().equals(nextActivityRanking.getFullDayActivities()))){
            activityRanking.setDeleted(true);
            nextActivityRanking.setStartDate(activityRanking.getStartDate());
        }
        mergeActivityRankings.put(activityRanking.getId(), activityRanking);
        mergeActivityRankings.put(nextActivityRanking.getId(), nextActivityRanking);
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
                newActivityRanking = new ActivityRanking(activityRankings.get(activityRankings.size()-1).getEndDate().plusDays(1), null, newHashSet(activity.getId()), unitOrexpertiseId, true);
            } else if(FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime())){
                newActivityRanking = new ActivityRanking(unitOrexpertiseId, activityRankings.get(activityRankings.size()-1).getEndDate().plusDays(1), null, new HashSet<>(), newHashSet(activity.getId()), activity.getCountryId(), true);
            } else {
                newActivityRanking = new ActivityRanking(unitOrexpertiseId,activityRankings.get(activityRankings.size()-1).getEndDate().plusDays(1), null, newHashSet(activity.getId()), new HashSet<>(), activity.getCountryId(), true);
            }
            activityRankingRepository.save(newActivityRanking);
            activityRankings.add(newActivityRanking);
        }
        if(isCollectionNotEmpty(updateActivityRankings)) {
            activityRankingRepository.saveEntities(updateActivityRankings);
        }
        this.mergeActivityRanking(activityRankings, presenceActivity);
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
            this.mergeActivityRanking(activityRankings, presenceActivity);
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

}
