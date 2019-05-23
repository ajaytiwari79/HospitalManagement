package com.kairos.service.activity;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.*;

/**
 * Created by pavan on 16/2/18.
 */

@Service
public class ActivityCategoryService extends MongoBaseService{

    @Inject
    ActivityCategoryRepository activityCategoryRepository;
    @Inject
    ActivityMongoRepository activityMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    //TODO: need to be removed, not in use as category will be updated accroding to linked time type.
    public ActivityCategory updateActivityCategoryByUnit(Long unitId, BigInteger activityCategoryId, String name){
        if(name.equalsIgnoreCase("NONE")){
            exceptionService.actionNotPermittedException(MESSAGE_CATEGORY_RENAME);
        }
        boolean isAlreadyExists=activityCategoryRepository.existsByNameIgnoreCaseAndDeleted(name,false);
        if(isAlreadyExists){
            exceptionService.duplicateDataException(MESSAGE_CATEGORY_ALREADYEXISTS);
        }
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            exceptionService.invalidOperationException(MESSAGE_CATEGORY_UPDATE);
        }
        if(activityCategory.getCountryId()!=null){
            exceptionService.invalidOperationException(MESSAGE_CATEGORY_COUNTRY_UPDATE);
        }

        activityCategory.setName(name);
        activityCategoryRepository.save(activityCategory);
        return activityCategory;
    }

    //TODO: need to be removed, not in use as category will be updated accroding to linked time type.
    public ActivityCategory updateActivityCategory(Long countryId, BigInteger activityCategoryId, String name){

        if(name.equalsIgnoreCase("NONE")){
            exceptionService.actionNotPermittedException(MESSAGE_CATEGORY_RENAME);
        }
        boolean isAlreadyExists=activityCategoryRepository.existsByNameIgnoreCaseAndDeleted(name,false);
        if(isAlreadyExists){
            exceptionService.duplicateDataException(MESSAGE_CATEGORY_ALREADYEXISTS,name);
        }
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            exceptionService.actionNotPermittedException(MESSAGE_CATEGORY_UPDATE);
        }else {
            activityCategory.setName(name);
            activityCategoryRepository.save(activityCategory);
        }
            return activityCategory;

    }

    public void updateActivityCategoryForTimeType(Long countryId, TimeType timeType){
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, timeType.getId());
        if(category != null){
            category.setName(timeType.getLabel());
            save(category);
        }
    }

   public boolean deleteActivityCategory(Long countryId,BigInteger activityCategoryId){
       Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
       ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
       if(activityCategory.getName().equals("NONE")){
           exceptionService.actionNotPermittedException(MESSAGE_CATEGORY_DELETE);
       }
       ActivityCategory category=activityCategoryRepository.getCategoryByNameAndCountryAndDeleted("NONE",countryId,false);

       List<Activity> activities=activityMongoRepository.findActivitiesByCategoryId(activityCategoryId);
       for(Activity activity:activities){
           activity.getGeneralActivityTab().setCategoryId(category.getId());
           activityMongoRepository.save(activity);
       }
       activityCategory.setDeleted(true);
       activityCategoryRepository.save(activityCategory);
       return  true;

   }

    public boolean deleteActivityCategoryByUnit(Long unitId,BigInteger activityCategoryId){
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            exceptionService.actionNotPermittedException(MESSAGE_CATEGORY_DELETE);
        }
        if(activityCategory.getCountryId()!=null){
            exceptionService.actionNotPermittedException(MESSAGE_CATEGORY_COUNTRY_UNIT_DELETE);

        }
        ActivityCategory category=activityCategoryRepository.getCategoryByName("NONE");

        List<Activity> activities=activityMongoRepository.findActivitiesByCategoryId(activityCategoryId);
        for(Activity activity:activities){
            activity.getGeneralActivityTab().setCategoryId(category.getId());
            activityMongoRepository.save(activity);
        }
        activityCategory.setDeleted(true);
        activityCategoryRepository.save(activityCategory);
        return  true;
    }

    public void removeTimeTypeRelatedCategory(Long countryId, BigInteger timeTypeId){
        ActivityCategory category = activityCategoryRepository.getCategoryByTimeType(countryId, timeTypeId);
        if(category !=null){
            category.setDeleted(true);
            save(category);
        }
    }

}
