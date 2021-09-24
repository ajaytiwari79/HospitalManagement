package com.kairos.service.staff_settings;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.common.StaffFilterDataDTO;
import com.kairos.dto.activity.shift.StaffActivityResponse;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.dto.user.staff.staff_settings.StaffAndActivitySettingWrapper;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.StaffActivityDetails;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.StaffActivityAssignmentSpecification;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.StaffActivityDetailsService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
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
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private TimeTypeService timeTypeService;
    @Inject private StaffActivityDetailsService staffActivityDetailsService;

    public StaffActivitySettingDTO createStaffActivitySetting(Long unitId,StaffActivitySettingDTO staffActivitySettingDTO){
        activityService.validateActivityTimeRules(staffActivitySettingDTO.getShortestTime(),staffActivitySettingDTO.getLongestTime());
        StaffActivitySetting staffActivitySetting=new StaffActivitySetting();
        ObjectMapperUtils.copyProperties(staffActivitySettingDTO,staffActivitySetting);
        if(isNotNull(staffActivitySettingDTO.getActivityId())) {
            Activity activity = activityMongoRepository.findOne(staffActivitySettingDTO.getActivityId());
            staffActivitySetting.setSecondLevelTimtype(activity.getActivityBalanceSettings().getTimeType());
        }
        staffActivitySetting.setUnitId(unitId);
        staffActivitySettingRepository.save(staffActivitySetting);
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
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(staffActivitySettingDTO.getActivityId());
        staffActivitySetting.setSecondLevelTimtype(activity.getActivityBalanceSettings().getTimeType());
        staffActivitySetting.setUnitId(unitId);
        staffActivitySettingRepository.save(staffActivitySetting);
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
        Map<Long, StaffDTO> staffExpertiseWrapperMap=staffExpertiseWrappers.stream().collect(Collectors.toMap(StaffDTO::getId, v->v));
        Map<String,List<StaffActivityResponse>> responseMap=new HashMap<>();
        for(Long currentStaffId:staffAndActivitySettingWrapper.getStaffIds()){
            responseMap=assignActivitySettingsForCurrentStaff(responseMap,activityMap,staffExpertiseWrapperMap,currentStaffId,staffAndActivitySettingWrapper.getStaffActivitySettings(),unitId,staffActivitySettings);
        }
        return responseMap;
    }

    public List<ActivityWithCompositeDTO> getStaffSpecificActivitySettings(Long unitId,Long staffId,boolean includeTeamActivity,boolean isActivityType){
        List<ActivityWithCompositeDTO> staffPersonalizedActivities = staffActivitySettingRepository.findAllStaffActivitySettingByStaffIdAndUnityIdWithMostUsedActivityCount(unitId,staffId);;
        if(includeTeamActivity) {
            List<StaffActivitySettingDTO> activitySettings = staffActivitySettingRepository.findAllByStaffIdAndDeletedFalse(staffId);
            List<ActivityWithCompositeDTO> activityList = organizationActivityService.getTeamActivitiesOfStaff(unitId, staffId,isActivityType);
            Map<BigInteger, ActivityWithCompositeDTO> activityMap = activityList.stream().collect(Collectors.toMap(ActivityWithCompositeDTO::getId, Function.identity()));
            activitySettings.forEach(activity -> {
                if (activityMap.containsKey(activity.getActivityId())) {
                    ActivityWithCompositeDTO teamActivity = activityMap.get(activity.getActivityId());
                    teamActivity.getActivityRulesSettings().setEarliestStartTime(activity.getEarliestStartTime());
                    teamActivity.getActivityRulesSettings().setLatestStartTime(activity.getLatestStartTime());
                    teamActivity.getActivityRulesSettings().setShortestTime(activity.getShortestTime());
                    teamActivity.getActivityRulesSettings().setLongestTime(activity.getLongestTime());

                }
            });
            staffPersonalizedActivities = activityList;
        }else {
            staffPersonalizedActivities = staffActivitySettingRepository.findAllStaffActivitySettingByStaffIdAndUnityIdWithMostUsedActivityCount(unitId,staffId);
        }
        return staffPersonalizedActivities;
    }


    public StaffActivitySettingDTO getStaffActivitySettingsById(Long unitId,BigInteger staffActivitySettingId){
        return staffActivitySettingRepository.findByIdAndUnitIdAndDeletedFalse(staffActivitySettingId,unitId);
    }

    public StaffActivitySettingDTO getStaffActivitySettingsByActivityId(Long unitId,BigInteger activityId,Long staffId){
        return staffActivitySettingRepository.findByActivityIdAndStaffIdAndUnitIdAndDeletedFalse(activityId,staffId,unitId);
    }


   public List<StaffActivitySettingDTO> updateBulkStaffActivitySettings(Long unitId,Long staffId,List<StaffActivitySettingDTO> staffActivitySettings){
        staffActivitySettings.forEach(staffActivitySetting->{
            staffActivitySetting.setUnitId(unitId);
            staffActivitySetting.setStaffId(staffId);
        });
        List<StaffActivitySetting> staffActivitySettingsList=ObjectMapperUtils.copyCollectionPropertiesByMapper(staffActivitySettings,StaffActivitySetting.class);
        save(staffActivitySettingsList);
        return staffActivitySettings;
   }


    private  List<String> validateActivitySettingsForCurrentStaff(StaffDTO staffDTO, Activity activity){
        Specification<StaffDTO> staffDTOSpecification=new StaffActivityAssignmentSpecification(activity);
        List<String> messages = staffDTOSpecification.isSatisfiedString(staffDTO);
        return !messages.isEmpty() ? messages.stream().map(message-> localeService.getMessage(message)).collect(Collectors.toList()) : null;
    }

   private Map<String,List<StaffActivityResponse>> assignActivitySettingsForCurrentStaff(Map<String,List<StaffActivityResponse>> responseMap, Map<BigInteger,Activity> activityMap, Map<Long, StaffDTO> staffExpertiseWrapperMap, Long staffId,
                                                                                         List<StaffActivitySettingDTO> staffActivitySettingDTOS, Long unitId, Set<StaffActivitySetting> staffActivitySettings){
       List<StaffActivityResponse> success=(responseMap.get(SUCCESS)==null)?new ArrayList<>():responseMap.get(SUCCESS);
       List<StaffActivityResponse> error=(responseMap.get(ERROR)==null)?new ArrayList<>():responseMap.get(ERROR);
       Set<StaffActivitySetting> staffActivitySettingSet=new HashSet<>();
       Map<BigInteger,StaffActivitySettingDTO> activitySettingDTOMap=new HashMap<>();
       Map<Long,Map<BigInteger,StaffActivitySettingDTO>> staffWiseActivityMap=new HashMap<>();
       staffActivitySettingDTOS.forEach(staffActivitySetting->{
          List<String> validationMessage= validateActivitySettingsForCurrentStaff(staffExpertiseWrapperMap.get(staffId),activityMap.get(staffActivitySetting.getActivityId()));
           if(isCollectionNotEmpty(validationMessage)){
               StaffActivityResponse staffActivityResponse = new StaffActivityResponse(staffId,staffActivitySetting.getActivityId(),validationMessage);
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
           Activity activity = activityMap.get(activitySetting.getActivityId());
           StaffActivitySetting staffActivitySetting=new StaffActivitySetting(staffId,activitySetting.getActivityId(),activitySetting.getEmploymentId(),
                   unitId,activitySetting.getShortestTime(),activitySetting.getLongestTime(),activitySetting.getMinLength(),activitySetting.getMaxThisActivityPerShift(),
                   activitySetting.isEligibleForMove(),activitySetting.getEarliestStartTime(),activitySetting.getLatestStartTime(),activitySetting.getMaximumEndTime(),
                   activity.getActivityRulesSettings().getDayTypes(), activitySetting.getDefaultStartTime(),activity.getActivityBalanceSettings().getTimeType());
           staffActivitySetting.setSecondLevelTimtype(activity.getActivityBalanceSettings().getTimeType());
           staffActivitySettingSet.add(staffActivitySetting);
           StaffActivityResponse staffActivityResponse=new StaffActivityResponse(staffId,staffActivitySetting.getActivityId(),newArrayList(localeService.getMessage(DEFAULT_ADDED)));
           success.add(staffActivityResponse);

       });

       if(!staffActivitySettingSet.isEmpty()){
           staffActivitySettingRepository.saveEntities(staffActivitySettingSet);
       }

       responseMap.put(SUCCESS,success);
       responseMap.put(ERROR,error);
       return responseMap;
   }

   public List<StaffActivitySetting> getActivitySettingByUnitIdAndActivityIds(Long unitId, List<BigInteger> activityIds){
        return staffActivitySettingRepository.findByUnitIdAndActivityIdAndDeletedFalse(unitId,activityIds);
   }

   public StaffFilterDataDTO getStaffFilterDataDTO(Long unitId, List<BigInteger> timeTypeIds, List<BigInteger> activityIds){
        StaffFilterDataDTO staffFilterDataDTO = new StaffFilterDataDTO();
        staffFilterDataDTO.setActivityIds(isCollectionNotEmpty(activityIds) ? activityIds : new ArrayList<>());
        if(isCollectionNotEmpty(timeTypeIds)) {
            List<TimeTypeDTO> leafTimeTypes = new ArrayList<>();
            timeTypeService.getAllLeafTimeTypeByParentTimeTypeIds(timeTypeIds, leafTimeTypes);
            List<Activity> activities = activityService.findAllByUnitIdAndTimeTypeIds(unitId, leafTimeTypes.stream().map(TimeTypeDTO::getId).collect(Collectors.toList()));
            staffFilterDataDTO.getActivityIds().addAll(activities.stream().map(Activity::getId).collect(Collectors.toList()));
        }
       if(isCollectionNotEmpty(staffFilterDataDTO.getActivityIds())) {
           List<StaffActivitySetting> staffActivitySettings = getActivitySettingByUnitIdAndActivityIds(unitId,staffFilterDataDTO.getActivityIds());
           staffFilterDataDTO.setStaffIds(staffActivitySettings.stream().map(StaffActivitySetting::getStaffId).collect(Collectors.toList()));
       }
       return staffFilterDataDTO;
   }

}
