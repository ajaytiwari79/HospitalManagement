package com.kairos.service.activity;

import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;

/**
 * Created by pavan on 16/2/18.
 */

@Service
public class ActivityCategoryService{

    public static final String NO_ACTIVITY_CATEGORY_FOUND = "No ActivityCategory found";
    @Inject
    ActivityCategoryRepository activityCategoryRepository;
    @Inject
    ActivityMongoRepository activityMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public void updateActivityCategoryForTimeType(Long countryId, TimeType timeType){
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, timeType.getId());
        if(category != null){
            category.setName(timeType.getLabel());
            activityCategoryRepository.save(category);
        }
    }

    public void removeTimeTypeRelatedCategory(Long countryId, BigInteger timeTypeId){
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, timeTypeId);
        if(category !=null){
            category.setDeleted(true);
            activityCategoryRepository.save(category);
        }
    }

}
