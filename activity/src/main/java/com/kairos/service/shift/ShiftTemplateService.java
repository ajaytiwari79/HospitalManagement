package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.dto.activity.shift.IndividualShiftTemplateDTO;
import com.kairos.dto.activity.shift.ShiftTemplateDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
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
        boolean alreadyExistsByName=shiftTemplateRepository.existsByNameIgnoreCaseAndDeletedFalseAndUnitId(shiftTemplateDTO.getName().trim(),unitId);
        if(alreadyExistsByName){
            exceptionService.duplicateDataException("message.shiftTemplate.exists",shiftTemplateDTO.getName());
        }
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOs =shiftTemplateDTO.getShiftList();
        List<IndividualShiftTemplate> individualShiftTemplates=new ArrayList<>();
        individualShiftTemplateDTOs.forEach(individualShiftTemplateDTO -> {
            IndividualShiftTemplate individualShiftTemplate=ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO,IndividualShiftTemplate.class);
            individualShiftTemplate.setMainShift(true);
            individualShiftTemplates.add(individualShiftTemplate);
        });
        save(individualShiftTemplates);
        Set<BigInteger> individualShiftTemplateIds= individualShiftTemplates.stream().map(i->i.getId()).collect(Collectors.toSet());
        ShiftTemplate shiftTemplate=new ShiftTemplate(shiftTemplateDTO.getName(),individualShiftTemplateIds,unitId,UserContext.getUserDetails().getId());
        save(shiftTemplate);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        shiftTemplateDTO.setCreatedBy(shiftTemplate.getCreatedBy());
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
                individualShiftTemplateDTO.getActivities().forEach(subShift->{
                    subShift.setTimeType(timeTypeMap.get(subShift.getActivityId()));
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
        ObjectMapperUtils.copyPropertiesExceptSpecific(shiftTemplateDTO,shiftTemplate,"shiftList","createdBy");
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
        if(!individualShiftTemplate.isMainShift()){
            exceptionService.actionNotPermittedException("message.individualShift.not.mainShift");
        }
        individualShiftTemplate.setDeleted(true);
        save(individualShiftTemplate);
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        shiftTemplate.getIndividualShiftTemplateIds().remove(individualShiftTemplate.getId());
        save(shiftTemplate);
        return true;
    }

    public List<ShiftDTO> createShiftUsingTemplate(Long unitId, ShiftDTO shiftDTO) {
        List<ShiftDTO> shifts = new ArrayList<>();
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftDTO.getTemplateId());
        Set<BigInteger> individualShiftTemplateIds = shiftTemplate.getIndividualShiftTemplateIds();
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        individualShiftTemplateDTOS.forEach(individualShiftTemplateDTO -> {
            ShiftDTO shiftDTO1 = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO, ShiftDTO.class);
            shiftDTO1.setId(null);
            shiftDTO1.setStaffId(shiftDTO.getStaffId());
            shiftDTO1.setUnitPositionId(shiftDTO.getUnitPositionId());
            LocalDate shiftStartDate = DateUtils.asLocalDate(shiftDTO.getStartDate());
            shiftDTO1.setStartDate(DateUtils.asDate(shiftStartDate, individualShiftTemplateDTO.getStartTime()));
            LocalDate shiftEndDate = DateUtils.asLocalDate(shiftDTO.getEndDate());
            shiftDTO1.setEndDate(DateUtils.asDate(shiftEndDate, individualShiftTemplateDTO.getEndTime()));
            ShiftDTO shiftQueryResult = shiftService.addSubShift(unitId, shiftDTO1, "Organization").getShifts().get(0);
            shifts.add(shiftQueryResult);

        });
        return shifts;
    }


}
