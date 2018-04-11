package com.kairos.activity.service.activity;

import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.activity.persistence.repository.activity.ActivityCategoryRepository;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.service.MongoBaseService;
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

    public ActivityCategory updateActivityCategory(Long countryId, BigInteger activityCategoryId, String name){

        if(name.equalsIgnoreCase("NONE")){
            throw new ActionNotPermittedException("Can't rename category as NONE");
        }
        boolean isAlreadyExists=activityCategoryRepository.existsByNameIgnoreCaseAndDeleted(name,false);
        if(isAlreadyExists){
            throw new DuplicateDataException("Category already exists "+name);
        }
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            throw new InvalidOperationException("Can't update NONE category");
        }else {
            activityCategory.setName(name);
            activityCategoryRepository.save(activityCategory);
            return activityCategory;
        }
    }

   public boolean deleteActivityCategory(Long countryId,BigInteger activityCategoryId){
       Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
       ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
       if(activityCategory.getName().equals("NONE")){
           throw new InvalidOperationException("Can't delete NONE category");
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
            throw new ActionNotPermittedException("Can't rename category as NONE");
        }
        boolean isAlreadyExists=activityCategoryRepository.existsByNameIgnoreCaseAndDeleted(name,false);
        if(isAlreadyExists){
            throw new DuplicateDataException("Category already exists "+name);
        }
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            throw new InvalidOperationException("Can't update NONE category");
        }
        if(activityCategory.getCountryId()!=null){
            throw new InvalidOperationException("Can't update Country category");
        }

            activityCategory.setName(name);
            activityCategoryRepository.save(activityCategory);
            return activityCategory;
    }

    public boolean deleteActivityCategoryByUnit(Long unitId,BigInteger activityCategoryId){
        Optional<ActivityCategory> activityCategoryOptional= activityCategoryRepository.findById(activityCategoryId);
        ActivityCategory  activityCategory= activityCategoryOptional.orElseThrow(()->new DataNotFoundByIdException("No ActivityCategory found"));
        if(activityCategory.getName().equals("NONE")){
            throw new InvalidOperationException("Can't delete NONE category");
        }
        if(activityCategory.getCountryId()!=null){
            throw new InvalidOperationException("Can't delete Country category from Unit");
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
