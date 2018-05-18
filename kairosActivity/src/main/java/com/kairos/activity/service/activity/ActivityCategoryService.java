package com.kairos.activity.service.activity;

import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.activity.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
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

    public ActivityCategory updateActivityCategoryByUnit(Long unitId, BigInteger activityCategoryId, String name){
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
        }
        if(activityCategory.getCountryId()!=null){
            exceptionService.actionNotPermittedException("message.category.country.update");
        }

            activityCategory.setName(name);
            activityCategoryRepository.save(activityCategory);
            return activityCategory;
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
}
