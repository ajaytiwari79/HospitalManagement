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
import java.util.stream.Collectors;

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

        } else {
            activityRanking =ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
            activityRankingRepository.save(activityRanking);
        }
        return activityRankingDTO;
    }

    public List<ActivityRankingDTO> getAbsenceRankingSettings(){
        return activityRankingRepository.getAbsenceRankingSettingsByDeletedFalse();
    }

    public List<ActivityRankingDTO> getAbsenceRankingSettings(Long expertiseId, Boolean published){
        List<ActivityRanking> activityRankings;
        if(isNotNull(published)){
            activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, published);
        } else {
            activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(activityRankings, ActivityRankingDTO.class);
    }

    public List<ActivityRankingDTO> getPresenceRankingSettings(Long unitId){
        return activityRankingRepository.getAbsenceRankingSettingsByUnitIdAndDeletedFalse(unitId);
    }

    public boolean deleteAbsenceRankingSettings(BigInteger id){
        ActivityRanking activityRanking = activityRankingRepository.findOne(id);
        if(activityRanking.isPublished()){
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_ALREADY_PUBLISHED);
        }
        activityRanking.setDeleted(true);
        activityRankingRepository.save(activityRanking);
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
        List<ActivityRanking> modifyActivityRankings = new ArrayList<>();
        for (Long expertiseId : expertiseIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
            boolean fullWeekActivity = FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime());
            if(isCollectionEmpty(activityRankings)){
                Set<BigInteger> fullDayActivities = new LinkedHashSet<>();
                Set<BigInteger> fullWeekActivities = new LinkedHashSet<>();
                if(fullWeekActivity){
                    fullWeekActivities.add(activity.getId());
                } else {
                    fullDayActivities.add(activity.getId());
                }
                modifyActivityRankings.add(new ActivityRanking(expertiseId, activity.getActivityGeneralSettings().getStartDate(), activity.getActivityGeneralSettings().getEndDate(), fullDayActivities, fullWeekActivities, activity.getCountryId(), true));
            } else {
                modifyActivityRankings.addAll(this.getModifyAbsenceActivityRanking(activityRankings, activity, fullWeekActivity));
            }
        }
        if(isCollectionNotEmpty(modifyActivityRankings)) {
            activityRankingRepository.saveEntities(modifyActivityRankings);
        }
    }

    private List<ActivityRanking> getModifyAbsenceActivityRanking(List<ActivityRanking> activityRankings, Activity activity, boolean fullWeekActivity){
        List<ActivityRanking> modifyActivityRankings = new ArrayList<>();
        for (ActivityRanking activityRanking : activityRankings) {
            if(isNull(activityRanking.getEndDate()) || activity.getActivityGeneralSettings().getStartDate().isBefore(activityRanking.getEndDate())){
                if(activityRanking.isPublished() && activityRanking.getStartDate().isBefore(activity.getActivityGeneralSettings().getStartDate())){
                    ActivityRanking newActivityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRanking, ActivityRanking.class);
                    newActivityRanking.setId(null);
                    newActivityRanking.setEndDate(activity.getActivityGeneralSettings().getStartDate().minusDays(1));
                    modifyActivityRankings.add(newActivityRanking);
                    activityRanking.setStartDate(activity.getActivityGeneralSettings().getStartDate());
                }
                if(fullWeekActivity){
                    activityRanking.getFullWeekActivities().add(activity.getId());
                } else {
                    activityRanking.getFullDayActivities().add(activity.getId());
                }
                modifyActivityRankings.add(activityRanking);
            }
        }
        return modifyActivityRankings;
    }

    @Async
    public void removeAbsenceActivityId(Activity activity, List<Long> expertiseIds){
        for (Long expertiseId : expertiseIds) {
            List<ActivityRanking> removeActivityRanking = new ArrayList<>();
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
            for (ActivityRanking activityRanking : activityRankings) {
                if(activityRanking.getFullDayActivities().remove(activity.getId()) || activityRanking.getFullWeekActivities().remove(activity.getId())){
                    removeActivityRanking.add(activityRanking);
                }
            }
            if(isCollectionNotEmpty(removeActivityRanking)) {
                activityRankingRepository.saveEntities(removeActivityRanking);
                List<ActivityRanking> draftActivityRankings = activityRankings.stream().filter(activityRanking -> !activityRanking.isPublished()).collect(Collectors.toList());
                this.mergeAbsenceActivityRanking(draftActivityRankings);
                List<ActivityRanking> publishActivityRankings = activityRankings.stream().filter(ActivityRanking::isPublished).collect(Collectors.toList());
                this.mergeAbsenceActivityRanking(publishActivityRankings);
            }
        }
    }

    private void mergeAbsenceActivityRanking(List<ActivityRanking> activityRankings) {
        Map<BigInteger,ActivityRanking> mergeActivityRankings = new HashMap<>();
        if(isCollectionNotEmpty(activityRankings) && activityRankings.size() > 1) {
            activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
            for(int index=0; index < activityRankings.size()-1; index++){
                ActivityRanking temp1 = activityRankings.get(index);
                ActivityRanking temp2 = activityRankings.get(index+1);
                checkAndMerge(mergeActivityRankings, temp1, temp2);
            }
        } else if(isCollectionNotEmpty(activityRankings) && isCollectionEmpty(activityRankings.get(0).getFullDayActivities()) && isCollectionEmpty(activityRankings.get(0).getFullWeekActivities())){
            activityRankings.get(0).setDeleted(true);
            mergeActivityRankings.put(activityRankings.get(0).getId(), activityRankings.get(0));
        }
        if(isMapNotEmpty(mergeActivityRankings)){
            activityRankingRepository.saveEntities(mergeActivityRankings.values());
        }
    }

    private void checkAndMerge(Map<BigInteger, ActivityRanking> mergeActivityRankings, ActivityRanking temp1, ActivityRanking temp2) {
        boolean checkRankingSame = true;
        if(isCollectionEmpty(temp1.getFullDayActivities()) && isCollectionEmpty(temp1.getFullWeekActivities())){
            temp1.setDeleted(true);
            mergeActivityRankings.put(temp1.getId(), temp1);
            checkRankingSame = false;
        }
        if(isCollectionEmpty(temp2.getFullDayActivities()) && isCollectionEmpty(temp2.getFullWeekActivities())){
            temp2.setDeleted(true);
            mergeActivityRankings.put(temp2.getId(), temp2);
            checkRankingSame = false;
        }
        if(checkRankingSame && temp1.getFullWeekActivities().equals(temp2.getFullWeekActivities()) && temp1.getFullDayActivities().equals(temp2.getFullDayActivities())){
            temp1.setDeleted(true);
            temp2.setStartDate(temp1.getStartDate());
            mergeActivityRankings.put(temp1.getId(), temp1);
            mergeActivityRankings.put(temp2.getId(), temp2);
        }
    }

    @Async
    public void updateEndDateOfAbsenceActivity(Activity activity, LocalDate oldEndDate) {
        if(isCollectionNotEmpty(activity.getExpertises())){
            for (Long expertiseId : activity.getExpertises()) {
                boolean fullWeekActivity = FULL_WEEK.equals(activity.getActivityTimeCalculationSettings().getMethodForCalculatingTime());
                if (isNull(oldEndDate)) {
                    updateRankingOnSetActivityEndDate(activity, fullWeekActivity, expertiseId);
                } else {
                    updateRankingOnResetActivityEndDate(activity, oldEndDate, fullWeekActivity, expertiseId);
                }
            }
        }
    }

    private void updateRankingOnResetActivityEndDate(Activity activity, LocalDate oldEndDate, boolean fullWeekActivity, Long expertiseId) {
        List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
        List<ActivityRanking> updateActivityRankings = new ArrayList<>();
        for (ActivityRanking activityRanking : activityRankings) {
            if (isNull(activity.getActivityGeneralSettings().getEndDate())) {
                if(!activityRanking.getStartDate().isBefore(oldEndDate)) {
                    if (fullWeekActivity) {
                        activityRanking.getFullWeekActivities().add(activity.getId());
                    } else {
                        activityRanking.getFullDayActivities().add(activity.getId());
                    }
                    updateActivityRankings.add(activityRanking);
                }
            } else if (activity.getActivityGeneralSettings().getEndDate().isAfter(oldEndDate)) {
                updateActivityEndDateAfter(activity, oldEndDate, fullWeekActivity, updateActivityRankings, activityRanking);
            } else {
                updateActivityEndDateBefor(activity, oldEndDate, fullWeekActivity, updateActivityRankings, activityRanking);
            }
        }
        activityRankingRepository.saveEntities(updateActivityRankings);
        List<ActivityRanking> draftActivityRankings = activityRankings.stream().filter(activityRanking -> !activityRanking.isPublished()).collect(Collectors.toList());
        this.mergeAbsenceActivityRanking(draftActivityRankings);
        List<ActivityRanking> publishActivityRankings = activityRankings.stream().filter(ActivityRanking::isPublished).collect(Collectors.toList());
        this.mergeAbsenceActivityRanking(publishActivityRankings);
    }

    private void updateActivityEndDateBefor(Activity activity, LocalDate oldEndDate, boolean fullWeekActivity, List<ActivityRanking> updateActivityRankings, ActivityRanking activityRanking) {
        if(!activityRanking.getStartDate().isBefore(activity.getActivityGeneralSettings().getEndDate()) && activityRanking.getStartDate().isBefore(oldEndDate)) {
            if(activityRanking.getStartDate().isBefore(activity.getActivityGeneralSettings().getEndDate()) && (isNull(activityRanking.getEndDate()) || activityRanking.getEndDate().isBefore(activity.getActivityGeneralSettings().getEndDate()))){
                updateActivityRankings.add(getNewActivityRanking(activityRanking, activity.getActivityGeneralSettings().getEndDate()));
            }
            if (fullWeekActivity) {
                activityRanking.getFullWeekActivities().remove(activity.getId());
            } else {
                activityRanking.getFullDayActivities().remove(activity.getId());
            }
            updateActivityRankings.add(activityRanking);
        }
    }

    private ActivityRanking getNewActivityRanking(ActivityRanking activityRanking, LocalDate activityEndDate) {
        ActivityRanking newActivityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRanking, ActivityRanking.class);
        newActivityRanking.setId(null);
        activityRanking.setEndDate(activityEndDate);
        newActivityRanking.setStartDate(activityEndDate.plusDays(1));
        return newActivityRanking;
    }

    private void updateActivityEndDateAfter(Activity activity, LocalDate oldEndDate, boolean fullWeekActivity, List<ActivityRanking> updateActivityRankings, ActivityRanking activityRanking) {
        if(activityRanking.getStartDate().isAfter(oldEndDate) && activityRanking.getStartDate().isBefore(activity.getActivityGeneralSettings().getEndDate())) {
            if(activityRanking.getStartDate().isBefore(activity.getActivityGeneralSettings().getEndDate()) && (isNull(activityRanking.getEndDate()) || activityRanking.getEndDate().isBefore(activity.getActivityGeneralSettings().getEndDate()))){
                updateActivityRankings.add(getNewActivityRanking(activityRanking, activity.getActivityGeneralSettings().getEndDate()));
            }
            if (fullWeekActivity) {
                activityRanking.getFullWeekActivities().add(activity.getId());
            } else {
                activityRanking.getFullDayActivities().add(activity.getId());
            }
            updateActivityRankings.add(activityRanking);
        }
    }

    private void updateRankingOnSetActivityEndDate(Activity activity, boolean fullWeekActivity, long expertiseId) {
        List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettings(expertiseId, activity.getActivityGeneralSettings().getEndDate());
        if(isCollectionNotEmpty(activityRankings)) {
            activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));
            ActivityRanking newActivityRanking = createActivityRanking(activity, fullWeekActivity, activityRankings.get(0));
            removeActivityFromRanking(activity, fullWeekActivity, activityRankings);
            if (isNotNull(newActivityRanking)) {
                activityRankings.add(newActivityRanking);
            }
        }
    }

    private void removeActivityFromRanking(Activity activity, boolean fullWeekActivity, List<ActivityRanking> activityRankings) {
        if (activityRankings.size() > 1) {
            for (int index = 1; index < activityRankings.size(); index++) {
                if (fullWeekActivity) {
                    activityRankings.get(index).getFullWeekActivities().remove(activity.getId());
                } else {
                    activityRankings.get(index).getFullDayActivities().remove(activity.getId());
                }
            }
        }
    }

    private ActivityRanking createActivityRanking(Activity activity, boolean fullWeekActivity, ActivityRanking activityRanking) {
        ActivityRanking newActivityRanking = null;
        if (isNull(activityRanking.getEndDate()) || activityRanking.getEndDate().isAfter(activity.getActivityGeneralSettings().getEndDate())) {
            newActivityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRanking, ActivityRanking.class);
            newActivityRanking.setId(null);
            newActivityRanking.setStartDate(activity.getActivityGeneralSettings().getEndDate().plusDays(1));
            activityRanking.setEndDate(activity.getActivityGeneralSettings().getEndDate());
            if (fullWeekActivity) {
                newActivityRanking.getFullWeekActivities().remove(activity.getId());
            } else {
                newActivityRanking.getFullDayActivities().remove(activity.getId());
            }
            activityRankingRepository.saveEntities(newArrayList(newActivityRanking,activityRanking));
        }
        return newActivityRanking;
    }
}
