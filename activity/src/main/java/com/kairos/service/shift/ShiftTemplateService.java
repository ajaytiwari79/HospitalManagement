package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.*;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.AppConstants.FULL_WEEK;

@Service
@Transactional
public class ShiftTemplateService extends MongoBaseService {


     @Inject
     private ShiftTemplateRepository shiftTemplateRepository;
     @Inject
     private IndividualShiftTemplateRepository individualShiftTemplateRepository;
     @Inject
     private ExceptionService exceptionService;
     @Inject
     private ShiftService shiftService;
     @Inject
     private ActivityMongoRepository activityMongoRepository;


    public ShiftTemplateDTO createShiftTemplate(Long unitId, ShiftTemplateDTO shiftTemplateDTO){

        //Check for activity is absence type or not
        Set<BigInteger>  activityIds=shiftTemplateDTO.getShiftList().stream().flatMap(s->s.getActivities().stream().map(a->a.getActivityId())).collect(Collectors.toSet());
        List<Activity> activities=activityMongoRepository.findAllActivitiesByIds(activityIds);
        activities.forEach(activity -> {
            if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
                exceptionService.actionNotPermittedException("message.activity.absenceType", activity.getId());
            }
        });

        //Check for validating duplicate by name
        boolean alreadyExistsByName=shiftTemplateRepository.
                existsByNameIgnoreCaseAndDeletedFalseAndUnitId(shiftTemplateDTO.getName().trim(),unitId);
        if(alreadyExistsByName){
            exceptionService.duplicateDataException("message.shiftTemplate.exists",shiftTemplateDTO.getName());
        }
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOs =shiftTemplateDTO.getShiftList();
        List<IndividualShiftTemplate> individualShiftTemplates=new ArrayList<>();
        individualShiftTemplateDTOs.forEach(individualShiftTemplateDTO -> {
            IndividualShiftTemplate individualShiftTemplate=ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO,IndividualShiftTemplate.class);
            individualShiftTemplates.add(individualShiftTemplate);
        });
        save(individualShiftTemplates);
        Set<BigInteger> individualShiftTemplateIds= individualShiftTemplates.stream().map(i->i.getId()).collect(Collectors.toSet());
        ShiftTemplate shiftTemplate=new ShiftTemplate(shiftTemplateDTO.getName(),individualShiftTemplateIds,unitId);
        save(shiftTemplate);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        shiftTemplateDTO.setUnitId(unitId);
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId){
        List<ShiftTemplate> shiftTemplates= shiftTemplateRepository.findAllByUnitIdAndCreatedByAndDeletedFalse(unitId,UserContext.getUserDetails().getId());
        List<ShiftTemplateDTO> shiftTemplateDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(shiftTemplates,ShiftTemplateDTO.class);
        Set<BigInteger> individualShiftTemplateIds=shiftTemplates.stream().flatMap(e->e.getIndividualShiftTemplateIds().stream()).collect(Collectors.toSet());
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS=  individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        Set<BigInteger>  activityIds=individualShiftTemplateDTOS.stream().flatMap(s->s.getActivities().stream().map(a->a.getActivityId())).collect(Collectors.toSet());
        Map<BigInteger,String> timeTypeMap = activityMongoRepository.findAllTimeTypeByActivityIds(activityIds).stream().collect(Collectors.toMap(k->k.getActivityId(),v->v.getTimeType()));
        Map<BigInteger, IndividualShiftTemplateDTO> individualShiftTemplateDTOMap = individualShiftTemplateDTOS.stream().collect(Collectors.toMap(IndividualShiftTemplateDTO::getId, Function.identity()));
        shiftTemplateDTOS.forEach(shiftTemplateDTO -> {
            shiftTemplateDTO.getIndividualShiftTemplateIds().forEach(individualShiftTemplateId->{
                IndividualShiftTemplateDTO individualShiftTemplateDTO = individualShiftTemplateDTOMap.get(individualShiftTemplateId);
                individualShiftTemplateDTO.getActivities().forEach(shiftActivity->{
                    shiftActivity.setTimeType(timeTypeMap.get(shiftActivity.getActivityId()));
                });
                shiftTemplateDTO.getShiftList().add(individualShiftTemplateDTO);
            });
        });
        return shiftTemplateDTOS;
    }

    public ShiftTemplateDTO updateShiftTemplate(Long unitId,BigInteger shiftTemplateId, ShiftTemplateDTO shiftTemplateDTO){
        ShiftTemplate shiftTemplate= shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent", shiftTemplateId);
        }
        shiftTemplateDTO.setId(shiftTemplateId);
        shiftTemplateDTO.setIndividualShiftTemplateIds(shiftTemplate.getIndividualShiftTemplateIds());
        shiftTemplateDTO.setUnitId(unitId);
        shiftTemplate = ObjectMapperUtils.copyPropertiesByMapper(shiftTemplateDTO,ShiftTemplate.class);
       // ObjectMapperUtils.copyProperties(shiftTemplateDTO,shiftTemplate,"shiftList","createdBy");
        save(shiftTemplate);
        return shiftTemplateDTO;
    }

    public boolean deleteShiftTemplate(BigInteger shiftTemplateId){
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        List<IndividualShiftTemplate> individualShiftTemplates = individualShiftTemplateRepository.getAllByIdInAndDeletedFalse(shiftTemplate.getIndividualShiftTemplateIds());
        individualShiftTemplates.forEach(individualShiftTemplate -> {
            individualShiftTemplate.setDeleted(true);});
        save(individualShiftTemplates);
        shiftTemplate.setDeleted(true);
        save(shiftTemplate);
        return true;

    }

    public IndividualShiftTemplateDTO updateIndividualShiftTemplate(BigInteger individualShiftTemplateId, IndividualShiftTemplateDTO individualShiftTemplateDTO){
        Optional<IndividualShiftTemplate> shiftDayTemplate= individualShiftTemplateRepository.findById(individualShiftTemplateId);
        if(!shiftDayTemplate.isPresent()|| shiftDayTemplate.get().isDeleted()){
            exceptionService.dataNotFoundByIdException("message.individual.shiftTemplate.absent", individualShiftTemplateId);
        }
        individualShiftTemplateDTO.setId(shiftDayTemplate.get().getId());
        IndividualShiftTemplate individualShiftTemplate = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO,IndividualShiftTemplate.class);
        individualShiftTemplate.setId(shiftDayTemplate.get().getId());
        save(individualShiftTemplate);
        return individualShiftTemplateDTO;
    }

    public IndividualShiftTemplateDTO addIndividualShiftTemplate(BigInteger shiftTemplateId, IndividualShiftTemplateDTO individualShiftTemplateDTO){
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.individual.shiftTemplate.absent", shiftTemplateId);
        }
        IndividualShiftTemplate individualShiftTemplate=ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO,IndividualShiftTemplate.class);
        save(individualShiftTemplate);
        shiftTemplate.getIndividualShiftTemplateIds().add(individualShiftTemplate.getId());
        save(shiftTemplate);
        individualShiftTemplateDTO.setId(individualShiftTemplate.getId());
        return individualShiftTemplateDTO;
    }

    public boolean deleteIndividualShiftTemplate(BigInteger shiftTemplateId,BigInteger individualShiftTemplateId){
        IndividualShiftTemplate individualShiftTemplate= individualShiftTemplateRepository.findOneById(individualShiftTemplateId);
        if(!Optional.ofNullable(individualShiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.individual.shiftTemplate.absent", individualShiftTemplateId);
        }
        individualShiftTemplate.setDeleted(true);
        save(individualShiftTemplate);
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        shiftTemplate.getIndividualShiftTemplateIds().remove(individualShiftTemplate.getId());
        save(shiftTemplate);
        return true;
    }

    public ShiftWithViolatedInfoDTO createShiftUsingTemplate(Long unitId, ShiftDTO shiftDTO) {
        List<ShiftDTO> shifts = new ArrayList<>();
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO=new ShiftWithViolatedInfoDTO();
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftDTO.getTemplate().getId());
        Set<BigInteger> individualShiftTemplateIds = shiftTemplate.getIndividualShiftTemplateIds();
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        individualShiftTemplateDTOS.forEach(individualShiftTemplateDTO -> {
            ShiftDTO newShiftDTO = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO,ShiftDTO.class);
            newShiftDTO.setActivities(null);
            newShiftDTO.setId(null);
            newShiftDTO.setStaffId(shiftDTO.getStaffId());
            newShiftDTO.setUnitPositionId(shiftDTO.getUnitPositionId());
            newShiftDTO.setShiftDate(shiftDTO.getShiftDate());
            List<ShiftActivityDTO> shiftActivities = new ArrayList<>(individualShiftTemplateDTO.getActivities().size());
            individualShiftTemplateDTO.getActivities().forEach(shiftTemplateActivity -> {
                Date startDate = DateUtils.asDate(shiftDTO.getTemplate().getStartDate(),shiftTemplateActivity.getStartTime());
                Date endDate = DateUtils.asDate(shiftDTO.getTemplate().getStartDate(), shiftTemplateActivity.getEndTime());
                ShiftActivityDTO shiftActivity = new ShiftActivityDTO(shiftTemplateActivity.getActivityName(),startDate,endDate,shiftTemplateActivity.getActivityId());
                shiftActivities.add(shiftActivity);
            });
            newShiftDTO.setActivities(shiftActivities);
            ShiftWithViolatedInfoDTO result=shiftService.createShift(unitId, newShiftDTO, "Organization",false);
            shiftWithViolatedInfoDTO.setShifts(result.getShifts());
            if(CollectionUtils.isNotEmpty(result.getViolatedRules().getActivities())){
                shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(result.getViolatedRules().getActivities());
            }
            shifts.add(newShiftDTO);
        });
        return shiftWithViolatedInfoDTO;
    }


}
