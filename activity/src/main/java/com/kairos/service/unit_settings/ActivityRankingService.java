package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.unit_settings.ActivityRankingRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class ActivityRankingService {
    @Inject
    private ActivityRankingRepository activityRankingRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityService activityService;

    public ActivityRankingDTO saveActivityRanking(ActivityRankingDTO activityRankingDTO){
        ActivityRanking activityRanking = ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
        activityRankingRepository.save(activityRanking);
        activityRankingDTO.setId(activityRanking.getId());
        return activityRankingDTO;
    }

    public ActivityRankingDTO updateActivityRanking(ActivityRankingDTO activityRankingDTO){
        ActivityRanking activityRanking = activityRankingRepository.findById(activityRankingDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Actvity Ranking", activityRankingDTO.getId())));
        if (activityRanking.getDraftId()!=null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        if (activityRanking.isPublished()) {
            // current is published so we need to create a  new copy and update in same
            ActivityRanking activityRankingCopy = ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
            activityRankingCopy.setPublished(false);
            activityRankingCopy.setId(null);
            activityRankingRepository.save(activityRankingCopy);

        } else {
            // update in current copy
            activityRanking =ObjectMapperUtils.copyPropertiesByMapper(activityRankingDTO, ActivityRanking.class);
            activityRankingRepository.save(activityRanking);
        }
        return activityRankingDTO;
    }

    public List<ActivityRankingDTO> getActivityRanking(){
        return activityRankingRepository.getActivityRankingByDeletedFalse();
    }

    public List<ActivityRankingDTO> getActivityRanking(Long expertiseId, Boolean published){
        if(isNotNull(published)){
            return activityRankingRepository.getActivityRankingByExpertiseIdAndPublishedAndDeletedFalse(expertiseId, published);
        } else {
            return activityRankingRepository.getActivityRankingByExpertiseIdAndDeletedFalse(expertiseId);
        }
    }

    public List<ActivityRankingDTO> getActivityRanking(Boolean published, Long unitId){
        if(isNotNull(published)){
            return activityRankingRepository.getActivityRankingByPublishedAndUnitIdAndDeletedFalse(published, unitId);
        } else {
            return activityRankingRepository.getActivityRankingByUnitIdAndDeletedFalse(unitId);
        }
    }


    public boolean deleteActivityRanking(BigInteger id){
        ActivityRanking activityRanking = activityRankingRepository.findOne(id);
        activityRanking.setDeleted(true);
        activityRankingRepository.save(activityRanking);
        return true;
    }


    public ActivityRankingDTO publishActivityRanking(BigInteger id, LocalDate publishedDate) {
        ActivityRanking activityRanking = activityRankingRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "Activity Ranking", id)));
        if (activityRanking.getActivityRankings().isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_RANKING_EMPTY);
        }
        if (activityRanking.isPublished()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_RANKING_ALREADY_PUBLISHED);
        }
//        if (activityRanking.getStartDate().isAfter(publishedDate) ||
//                (activityRanking.getEndDate()!=null && activityRanking.getEndDate().isBefore(publishedDate))) {
//            exceptionService.dataNotFoundByIdException("MESSAGE_PUBLISHDATE_NOTLESSTHAN_STARTDATE");
//        }
        activityRanking.setPublished(true);
        activityRanking.setStartDate(publishedDate); // changing
        ActivityRanking parentActivityRanking = activityRankingRepository.findByDraftIdAndPriorityForAndDeletedFalse(activityRanking.getId(), activityRanking.getPriorityFor());
        ActivityRanking lastActivityRanking = activityRankingRepository.findTopByExpertiseIdAndPriorityForAndDeletedFalseOrderByStartDateDesc(activityRanking.getExpertiseId(), activityRanking.getPriorityFor());
        boolean onGoingUpdated = false;

        if (lastActivityRanking != null && publishedDate.isAfter(lastActivityRanking.getStartDate()) && lastActivityRanking.getEndDate() == null) {
            lastActivityRanking.setEndDate(publishedDate.minusDays(1));
            activityRankingRepository.save(lastActivityRanking);
            activityRanking.setEndDate(null);
            onGoingUpdated = true;
        }
        if (!onGoingUpdated && Optional.ofNullable(parentActivityRanking).isPresent()) {
            if (parentActivityRanking.getStartDate().isEqual(publishedDate) || parentActivityRanking.getStartDate().isAfter(publishedDate)) {
                exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISH_DATE_NOT_LESS_THAN_OR_EQUALS_PARENT_START_DATE);
            }
            parentActivityRanking.setEndDate(publishedDate.minusDays(1L));
            if (lastActivityRanking == null && activityRanking.getEndDate() != null && activityRanking.getEndDate().isBefore(publishedDate)) {
                activityRanking.setEndDate(null);
            }
        }
        if(isNotNull(parentActivityRanking)){
            parentActivityRanking.setDraftId(null);
            activityRankingRepository.save(parentActivityRanking);
        }
        activityRankingRepository.save(activityRanking);
        return ObjectMapperUtils.copyPropertiesByMapper(parentActivityRanking, ActivityRankingDTO.class);

    }

    public List<ActivityDTO> findAllAbsenceActivities(){
        return activityService.findAllAbsenceActivities();
    }
}
