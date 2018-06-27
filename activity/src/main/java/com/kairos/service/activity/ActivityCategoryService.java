package com.kairos.service.activity;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

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
            exceptionService.actionNotPermittedException("message.category.rename");
        }
        boolean isAlreadyExists=activityCategoryRepository.existsByNameIgnoreCaseAndDeleted(name,false);
        if(isAlreadyExists){
            exceptionService.duplicateDataException("message.category.alreadyexists");
        }
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            exceptionService.invalidOperationException("message.category.update");
        }
        if(activityCategory.getCountryId()!=null){
            exceptionService.invalidOperationException("message.category.country.update");
        }

        activityCategory.setName(name);
        activityCategoryRepository.save(activityCategory);
        return activityCategory;
    }

    //TODO: need to be removed, not in use as category will be updated accroding to linked time type.
    public ActivityCategory updateActivityCategory(Long countryId, BigInteger activityCategoryId, String name){

        if(name.equalsIgnoreCase("NONE")){
            exceptionService.actionNotPermittedException("message.category.rename");
        }
        boolean isAlreadyExists=activityCategoryRepository.existsByNameIgnoreCaseAndDeleted(name,false);
        if(isAlreadyExists){
            exceptionService.duplicateDataException("message.category.alreadyexists",name);
        }
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            exceptionService.actionNotPermittedException("message.category.update");
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
           exceptionService.actionNotPermittedException("message.category.delete");
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
            exceptionService.actionNotPermittedException("message.category.delete");
        }
        if(activityCategory.getCountryId()!=null){
            exceptionService.actionNotPermittedException("message.category.country.unit.delete");

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
