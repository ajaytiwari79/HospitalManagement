package com.kairos.service.staff_settings;

import com.kairos.dto.activity.shift.StaffActivityResponse;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.StaffEmploymentTypeSpecification;
import com.kairos.rule_validator.activity.StaffExpertiseSpecification;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.locale.LocaleService;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.dto.user.staff.staff_settings.StaffAndActivitySettingWrapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffActivitySettingService extends MongoBaseService {

    @Inject private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private GenericIntegrationService genericIntegrationService;
    @Inject private LocaleService localeService;
    @Inject private ActivityService activityService;

    public StaffActivitySettingDTO createStaffActivitySetting(Long unitId,StaffActivitySettingDTO staffActivitySettingDTO){
        activityService.validateActivityTimeRules(staffActivitySettingDTO.getEarliestStartTime(),staffActivitySettingDTO.getLatestStartTime(),
                staffActivitySettingDTO.getMaximumEndTime(),staffActivitySettingDTO.getShortestTime(),staffActivitySettingDTO.getLongestTime());
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
        activityService.validateActivityTimeRules(staffActivitySettingDTO.getEarliestStartTime(),staffActivitySettingDTO.getLatestStartTime(),
                staffActivitySettingDTO.getMaximumEndTime(),staffActivitySettingDTO.getShortestTime(),staffActivitySettingDTO.getLongestTime());
        StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByIdAndDeletedFalse(staffActivitySettingId);
        if(!Optional.ofNullable(staffActivitySetting).isPresent()){
            exceptionService.dataNotFoundException("message.staff.activity.settings.absent");
        }
        ObjectMapperUtils.copyProperties(staffActivitySettingDTO,staffActivitySetting);
        staffActivitySetting.setUnitId(unitId);
        save(staffActivitySetting);
        return staffActivitySettingDTO;
    }

    public boolean deleteStaffActivitySettings(BigInteger staffActivitySettingId){
        StaffActivitySetting staffActivitySetting=staffActivitySettingRepository.findByIdAndDeletedFalse(staffActivitySettingId);
        if(!Optional.ofNullable(staffActivitySetting).isPresent()){
            exceptionService.dataNotFoundException("message.staff.activity.settings.absent");
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
            exceptionService.actionNotPermittedException("error.empty.staff.or.activity.setting");
        }


        Set<BigInteger> activityIds=staffAndActivitySettingWrapper.getStaffActivitySettings().stream().map(StaffActivitySettingDTO::getActivityId).collect(Collectors.toSet());
        List<StaffActivitySetting> staffActivitySettings=staffActivitySettingRepository.findByStaffIdsInAndActivityIdsInAndDeletedFalse(staffAndActivitySettingWrapper.getStaffIds(),activityIds);
        if(staffActivitySettings==null){
            staffActivitySettings=new ArrayList<>();
        }
        List<Activity> activities=activityMongoRepository.findAllActivitiesByIds(activityIds);
        Map<BigInteger,Activity> activityMap=activities.stream().collect(Collectors.toMap(Activity::getId,v->v));
        List<StaffDTO> staffExpertiseWrappers= genericIntegrationService.getStaffDetailByIds(unitId,staffAndActivitySettingWrapper.getStaffIds());
        Map<Long,StaffDTO> staffExpertiseWrapperMap=staffExpertiseWrappers.stream().collect(Collectors.toMap(StaffDTO::getId,v->v));
        Map<String,List<StaffActivityResponse>> responseMap=new HashMap<>();
        for(Long currentStaffId:staffAndActivitySettingWrapper.getStaffIds()){
            responseMap=assignActivitySettingsForCurrentStaff(responseMap,activityMap,staffExpertiseWrapperMap,currentStaffId,staffAndActivitySettingWrapper.getStaffActivitySettings(),unitId,staffActivitySettings);
        }
        return responseMap;
    }

    public List<StaffActivitySettingDTO> getStaffSpecificActivitySettings(Long unitId,Long staffId){
        return staffActivitySettingRepository.findAllByUnitIdAndStaffIdAndDeletedFalse(unitId,staffId);
    }


   public StaffActivitySettingDTO getStaffActivitySettingsById(Long unitId,BigInteger staffActivitySettingId){
        return staffActivitySettingRepository.findByIdAndUnitIdAndDeletedFalse(staffActivitySettingId,unitId);
    }


   public List<StaffActivitySettingDTO> updateBulkStaffActivitySettings(Long unitId,Long staffId,List<StaffActivitySettingDTO> staffActivitySettings){
        staffActivitySettings.forEach(staffActivitySetting->{
            staffActivitySetting.setUnitId(unitId);
            staffActivitySetting.setStaffId(staffId);
        });
        List<StaffActivitySetting> staffActivitySettingsList=ObjectMapperUtils.copyPropertiesOfListByMapper(staffActivitySettings,StaffActivitySetting.class);
        save(staffActivitySettingsList);
        return staffActivitySettings;
   }


    private  String validateActivitySettingsForCurrentStaff(StaffDTO staffDTO,Activity activity){
        Specification<StaffDTO> staffDTOSpecification=new StaffExpertiseSpecification(activity);
        Specification<StaffDTO> staffEmploymentTypeSpecification=new StaffEmploymentTypeSpecification(activity);
        Specification<StaffDTO> expertiseWrapperSpecification=staffDTOSpecification.and(staffEmploymentTypeSpecification);
        List<String> messages = expertiseWrapperSpecification.isSatisfiedString(staffDTO);
        return (!messages.isEmpty())?localeService.getMessage(messages.get(0)):null;
    }

   private Map<String,List<StaffActivityResponse>> assignActivitySettingsForCurrentStaff(Map<String,List<StaffActivityResponse>> responseMap,Map<BigInteger,Activity> activityMap,Map<Long,StaffDTO> staffExpertiseWrapperMap,Long staffId,
                                                                                         List<StaffActivitySettingDTO> staffActivitySettingDTOS,Long unitId,List<StaffActivitySetting> staffActivitySettings){
       List<StaffActivityResponse> success=(responseMap.get("success")==null)?new ArrayList<>():responseMap.get("success");
       List<StaffActivityResponse> error=(responseMap.get("error")==null)?new ArrayList<>():responseMap.get("error");
       Map<BigInteger,StaffActivitySettingDTO> activitySettingDTOMap=new HashMap<>();
       Map<Long,Map<BigInteger,StaffActivitySettingDTO>> staffWiseActivityMap=new HashMap<>();
       staffActivitySettingDTOS.forEach(staffActivitySetting->{
          String validationMessage= validateActivitySettingsForCurrentStaff(staffExpertiseWrapperMap.get(staffId),activityMap.get(staffActivitySetting.getActivityId()));
           if(Optional.ofNullable(validationMessage).isPresent()){
               StaffActivityResponse staffActivityResponse=new StaffActivityResponse(staffId,staffActivitySetting.getActivityId(),validationMessage);
               error.add(staffActivityResponse);
               return;
           }

           StaffActivitySetting staffActivitySetting1=new StaffActivitySetting(staffId,staffActivitySetting.getActivityId(),staffActivitySetting.getUnitPositionId(),
                   unitId,staffActivitySetting.getShortestTime(),staffActivitySetting.getLongestTime(),staffActivitySetting.getMinLength(),staffActivitySetting.getMaxThisActivityPerShift(),
                   staffActivitySetting.isEligibleForMove(),staffActivitySetting.getEarliestStartTime(),staffActivitySetting.getLatestStartTime(),staffActivitySetting.getMaximumEndTime(),
                   activityMap.get(staffActivitySetting.getActivityId()).getRulesActivityTab().getDayTypes());

           activitySettingDTOMap.put(staffActivitySetting.getActivityId(),staffActivitySetting);
           staffWiseActivityMap.put(staffId,activitySettingDTOMap);
        });










       staffActivitySettings.forEach(currentStaffActivitySettings->{
           StaffActivitySettingDTO staffActivitySettingDTO=staffWiseActivityMap.get(currentStaffActivitySettings.getStaffId()).get(currentStaffActivitySettings.getActivityId());
           if(staffActivitySettingDTO!=null){
               staffActivitySettingDTO.setStaffId(currentStaffActivitySettings.getStaffId());
               staffActivitySettingDTO.setId(currentStaffActivitySettings.getId());
               currentStaffActivitySettings = ObjectMapperUtils.copyPropertiesByMapper(staffActivitySettingDTO,StaffActivitySetting.class);
           }
           else {
               currentStaffActivitySettings =
           }
       });





























       StaffActivitySetting activitySetting = staffActivitySettings.stream().filter(staffActivitySetting1->(
               staffId.equals(staffActivitySetting1.getStaffId()))).filter(staffActivitySetting1->(
               staffActivitySetting.getActivityId().equals(staffActivitySetting1.getActivityId()))).findAny().orElse(null);

       if(activitySetting!=null){
           staffActivitySetting.setId(activitySetting.getId());
           staffActivitySetting.setStaffId(activitySetting.getStaffId());
           activitySetting=ObjectMapperUtils.copyPropertiesByMapper(staffActivitySetting,StaffActivitySetting.class);

       }
       else {
           activitySetting=  new StaffActivitySetting(staffId,staffActivitySetting.getActivityId(),staffActivitySetting.getUnitPositionId(),
                   unitId,staffActivitySetting.getShortestTime(),staffActivitySetting.getLongestTime(),staffActivitySetting.getMinLength(),staffActivitySetting.getMaxThisActivityPerShift(),
                   staffActivitySetting.isEligibleForMove(),staffActivitySetting.getEarliestStartTime(),staffActivitySetting.getLatestStartTime(),staffActivitySetting.getMaximumEndTime(),
                   activityMap.get(staffActivitySetting.getActivityId()).getRulesActivityTab().getDayTypes());
       }

       staffActivitySettings.add(activitySetting);
       StaffActivityResponse staffActivityResponse=new StaffActivityResponse(staffId,staffActivitySetting.getActivityId(),localeService.getMessage("default.added"));
       success.add(staffActivityResponse);












       if(!staffActivitySettings.isEmpty()){
           save(staffActivitySettings);
       }

       responseMap.put("success",success);
       responseMap.put("error",error);
       return responseMap;
   }
}
