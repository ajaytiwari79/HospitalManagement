package com.kairos.service.staff_settings;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.StaffActivityResponse;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.dto.user.staff.staff_settings.StaffAndActivitySettingWrapper;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.StaffExpertiseSpecification;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class StaffActivitySettingService extends MongoBaseService {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    @Inject private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private LocaleService localeService;
    @Inject private ActivityService activityService;
    @Inject private OrganizationActivityService organizationActivityService;

    public StaffActivitySettingDTO createStaffActivitySetting(Long unitId,StaffActivitySettingDTO staffActivitySettingDTO){
        activityService.validateActivityTimeRules(staffActivitySettingDTO.getShortestTime(),staffActivitySettingDTO.getLongestTime());
        StaffActivitySetting staffActivitySetting=new StaffActivitySetting();
        ObjectMapperUtils.copyProperties(staffActivitySettingDTO,staffActivitySetting);
        staffActivitySetting.setUnitId(unitId);
        save(staffActivitySetting);
        staffActivitySettingDTO.setId(staffActivitySetting.getId());
        return staffActivitySettingDTO;
    }

    public List<StaffActivitySettingDTO> getStaffActivitySettings(Long unitId){
        return staffActivitySettingRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }

    public StaffActivitySettingDTO updateStaffActivitySettings(BigInteger staffActivitySettingId,Long unitId, StaffActivitySettingDTO staffActivitySettingDTO){
        activityService.validateActivityTimeRules(staffActivitySettingDTO.getShortestTime(),staffActivitySettingDTO.getLongestTime());
        StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByIdAndDeletedFalse(staffActivitySettingId);
        if(!Optional.ofNullable(staffActivitySetting).isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_STAFF_ACTIVITY_SETTINGS_ABSENT);
        }
        ObjectMapperUtils.copyProperties(staffActivitySettingDTO,staffActivitySetting);
        staffActivitySetting.setUnitId(unitId);
        save(staffActivitySetting);
        return staffActivitySettingDTO;
    }

    public boolean deleteStaffActivitySettings(BigInteger staffActivitySettingId){
        StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByIdAndDeletedFalse(staffActivitySettingId);
        if(!Optional.ofNullable(staffActivitySetting).isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_STAFF_ACTIVITY_SETTINGS_ABSENT);
        }
        staffActivitySetting.setDeleted(true);
        save(staffActivitySetting);
        return true;
    }

    public StaffActivitySettingDTO getDefaultStaffActivitySettings(Long unitId,BigInteger activityId){
        return activityMongoRepository.findStaffPersonalizedSettings(unitId,activityId);
    }

    public Map<String,List<StaffActivityResponse>> assignActivitySettingToStaffs(Long unitId, StaffAndActivitySettingWrapper staffAndActivitySettingWrapper){
        if(staffAndActivitySettingWrapper.getStaffIds().isEmpty() || staffAndActivitySettingWrapper.getStaffActivitySettings().isEmpty()){
            exceptionService.actionNotPermittedException(ERROR_EMPTY_STAFF_OR_ACTIVITY_SETTING);
        }


        Set<BigInteger> activityIds=staffAndActivitySettingWrapper.getStaffActivitySettings().stream().map(StaffActivitySettingDTO::getActivityId).collect(Collectors.toSet());
        Set<StaffActivitySetting> staffActivitySettings=staffActivitySettingRepository.findByStaffIdInAndActivityIdInAndDeletedFalse(staffAndActivitySettingWrapper.getStaffIds(),activityIds);
        if(staffActivitySettings==null){
            staffActivitySettings=new HashSet<>();
        }
        List<Activity> activities=activityMongoRepository.findAllActivitiesByIds(activityIds);
        Map<BigInteger,Activity> activityMap=activities.stream().collect(Collectors.toMap(Activity::getId,v->v));
        List<StaffDTO> staffExpertiseWrappers= userIntegrationService.getStaffDetailByIds(unitId,staffAndActivitySettingWrapper.getStaffIds());
        Map<Long,StaffDTO> staffExpertiseWrapperMap=staffExpertiseWrappers.stream().collect(Collectors.toMap(StaffDTO::getId,v->v));
        Map<String,List<StaffActivityResponse>> responseMap=new HashMap<>();
        for(Long currentStaffId:staffAndActivitySettingWrapper.getStaffIds()){
            responseMap=assignActivitySettingsForCurrentStaff(responseMap,activityMap,staffExpertiseWrapperMap,currentStaffId,staffAndActivitySettingWrapper.getStaffActivitySettings(),unitId,staffActivitySettings);
        }
        return responseMap;
    }

    public List<ActivityWithCompositeDTO> getStaffSpecificActivitySettings(Long unitId,Long staffId,boolean includeTeamActivity){
        List<ActivityWithCompositeDTO> staffPersonalizedActivities= staffActivitySettingRepository.findAllByUnitIdAndStaffIdAndDeletedFalse(unitId,staffId);
        if(includeTeamActivity) {
            List<ActivityWithCompositeDTO> activityList = organizationActivityService.getTeamActivitiesOfStaff(unitId, staffId, staffPersonalizedActivities);
            Map<BigInteger, ActivityWithCompositeDTO> activityMap = activityList.stream().collect(Collectors.toMap(ActivityWithCompositeDTO::getId, Function.identity()));
            staffPersonalizedActivities.forEach(activity -> {
                if (activityMap.containsKey(activity.getActivityId())) {
                    activityMap.get(activity.getActivityId()).getRulesActivityTab().setEarliestStartTime(activity.getEarliestStartTime());
                    activityMap.get(activity.getActivityId()).getRulesActivityTab().setLatestStartTime(activity.getLatestStartTime());
                    activityMap.get(activity.getActivityId()).getRulesActivityTab().setShortestTime(activity.getShortestTime());
                    activityMap.get(activity.getActivityId()).getRulesActivityTab().setLongestTime(activity.getLongestTime());
                }
            });
            return activityList;
        }
        else {
            return staffPersonalizedActivities;
        }
    }


   public StaffActivitySettingDTO getStaffActivitySettingsById(Long unitId,BigInteger staffActivitySettingId){
        return staffActivitySettingRepository.findByIdAndUnitIdAndDeletedFalse(staffActivitySettingId,unitId);
    }


   public List<StaffActivitySettingDTO> updateBulkStaffActivitySettings(Long unitId,Long staffId,List<StaffActivitySettingDTO> staffActivitySettings){
        staffActivitySettings.forEach(staffActivitySetting->{
            staffActivitySetting.setUnitId(unitId);
            staffActivitySetting.setStaffId(staffId);
        });
        List<StaffActivitySetting> staffActivitySettingsList=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffActivitySettings,StaffActivitySetting.class);
        save(staffActivitySettingsList);
        return staffActivitySettings;
   }


    private  String validateActivitySettingsForCurrentStaff(StaffDTO staffDTO,Activity activity){
        Specification<StaffDTO> staffDTOSpecification=new StaffExpertiseSpecification(activity);
        List<String> messages = staffDTOSpecification.isSatisfiedString(staffDTO);
        return (!messages.isEmpty())?localeService.getMessage(messages.get(0)):null;
    }

   private Map<String,List<StaffActivityResponse>> assignActivitySettingsForCurrentStaff(Map<String,List<StaffActivityResponse>> responseMap,Map<BigInteger,Activity> activityMap,Map<Long,StaffDTO> staffExpertiseWrapperMap,Long staffId,
                                                                                         List<StaffActivitySettingDTO> staffActivitySettingDTOS,Long unitId,Set<StaffActivitySetting> staffActivitySettings){
       List<StaffActivityResponse> success=(responseMap.get(SUCCESS)==null)?new ArrayList<>():responseMap.get(SUCCESS);
       List<StaffActivityResponse> error=(responseMap.get(ERROR)==null)?new ArrayList<>():responseMap.get(ERROR);
       Set<StaffActivitySetting> staffActivitySettingSet=new HashSet<>();
       Map<BigInteger,StaffActivitySettingDTO> activitySettingDTOMap=new HashMap<>();
       Map<Long,Map<BigInteger,StaffActivitySettingDTO>> staffWiseActivityMap=new HashMap<>();
       staffActivitySettingDTOS.forEach(staffActivitySetting->{
          String validationMessage= validateActivitySettingsForCurrentStaff(staffExpertiseWrapperMap.get(staffId),activityMap.get(staffActivitySetting.getActivityId()));
           if(Optional.ofNullable(validationMessage).isPresent()){
               StaffActivityResponse staffActivityResponse=new StaffActivityResponse(staffId,staffActivitySetting.getActivityId(),validationMessage);
               error.add(staffActivityResponse);
               return;
           }
           activitySettingDTOMap.put(staffActivitySetting.getActivityId(),staffActivitySetting);
           staffWiseActivityMap.put(staffId,activitySettingDTOMap);
        });


       staffActivitySettings.forEach(currentStaffActivitySettings->{
           StaffActivitySettingDTO staffActivitySettingDTO=null;
           Map<BigInteger,StaffActivitySettingDTO> staffActivitySettingDTOMap=staffWiseActivityMap.get(currentStaffActivitySettings.getStaffId());
           if(staffActivitySettingDTOMap!=null){
                staffActivitySettingDTO=staffActivitySettingDTOMap.get(currentStaffActivitySettings.getActivityId());
           }

           if(staffActivitySettingDTO!=null){
               staffActivitySettingDTO.setStaffId(currentStaffActivitySettings.getStaffId());
               staffActivitySettingDTO.setId(currentStaffActivitySettings.getId());
               staffActivitySettingDTO.setUnitId(currentStaffActivitySettings.getUnitId());
               currentStaffActivitySettings = ObjectMapperUtils.copyPropertiesByMapper(staffActivitySettingDTO,StaffActivitySetting.class);
               staffActivitySettingSet.add(currentStaffActivitySettings);
               staffWiseActivityMap.remove(currentStaffActivitySettings.getStaffId()).get(currentStaffActivitySettings.getActivityId());
           }
       });

       activitySettingDTOMap.forEach((activityId,activitySetting)->{
           StaffActivitySetting staffActivitySetting=new StaffActivitySetting(staffId,activitySetting.getActivityId(),activitySetting.getEmploymentId(),
                   unitId,activitySetting.getShortestTime(),activitySetting.getLongestTime(),activitySetting.getMinLength(),activitySetting.getMaxThisActivityPerShift(),
                   activitySetting.isEligibleForMove(),activitySetting.getEarliestStartTime(),activitySetting.getLatestStartTime(),activitySetting.getMaximumEndTime(),
                   activityMap.get(activitySetting.getActivityId()).getRulesActivityTab().getDayTypes(), activitySetting.getDefaultStartTime());
           staffActivitySettingSet.add(staffActivitySetting);
           StaffActivityResponse staffActivityResponse=new StaffActivityResponse(staffId,staffActivitySetting.getActivityId(),localeService.getMessage(DEFAULT_ADDED));
           success.add(staffActivityResponse);

       });

       if(!staffActivitySettingSet.isEmpty()){
           save(staffActivitySettingSet);
       }

       responseMap.put(SUCCESS,success);
       responseMap.put(ERROR,error);
       return responseMap;
   }
}
