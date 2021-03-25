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
    public void removeActivityId(Activity activity, List<Long> expertiseIds){
        for (Long expertiseId : expertiseIds) {
            List<ActivityRanking> activityRankings = activityRankingRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
            for (ActivityRanking activityRanking : activityRankings) {
                activityRanking.getFullDayActivities().remove(activity.getId());
                activityRanking.getFullWeekActivities().remove(activity.getId());
            }
            if(isCollectionNotEmpty(activityRankings)) {
                this.mergeAbsenceActivityRanking(activityRankings);
                activityRankingRepository.saveEntities(activityRankings);
            }
        }
    }

    private void mergeAbsenceActivityRanking(List<ActivityRanking> activityRankings) {
        activityRankings.sort(Comparator.comparing(ActivityRanking::getStartDate));

    }
}
