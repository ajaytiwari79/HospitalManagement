package com.kairos.service.shift;

import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.userContext.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FULL_DAY;
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
        Set<BigInteger>  activityIds=new HashSet<>();
        shiftTemplateDTO.getShiftList().forEach(shift->{
            activityIds.add(shift.getActivityId());
            activityIds.addAll(shift.getSubShifts().stream().map(s->s.getActivityId()).collect(Collectors.toSet()));
        });
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
            List<IndividualShiftTemplateDTO> subShifts=individualShiftTemplateDTO.getSubShifts();
            List<IndividualShiftTemplate> individualShiftTemplates1=new ArrayList<>();
            if(Optional.ofNullable(subShifts).isPresent() && subShifts.size()>0){
                individualShiftTemplates1=ObjectMapperUtils.copyProperties(subShifts,IndividualShiftTemplate.class);
                save(individualShiftTemplates1);
                individualShiftTemplateDTO.setSubShifts(ObjectMapperUtils.copyProperties(individualShiftTemplates1,IndividualShiftTemplateDTO.class));
            }
            Set<BigInteger> subShiftIds=individualShiftTemplates1.stream().map(subShifts1-> subShifts1.getId()).collect(Collectors.toSet());
            IndividualShiftTemplate individualShiftTemplate=new IndividualShiftTemplate();
            ObjectMapperUtils.copyPropertiesUsingBeanUtils(individualShiftTemplateDTO,individualShiftTemplate,"shiftList");
            individualShiftTemplate.setSubShiftIds(subShiftIds);
            individualShiftTemplates.add(individualShiftTemplate);
        });
        save(individualShiftTemplates);
        Set<BigInteger> individualShiftTemplateIds=individualShiftTemplates.stream().map(individualShiftTemplate -> individualShiftTemplate.getId()).collect(Collectors.toSet());
        ShiftTemplate shiftTemplate=new ShiftTemplate(shiftTemplateDTO.getName(),individualShiftTemplateIds,unitId,UserContext.getUserDetails().getId());
        save(shiftTemplate);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId){
        List<ShiftTemplate> shiftTemplates= shiftTemplateRepository.findAllByUnitIdAndCreatedByAndDeletedFalse(unitId,UserContext.getUserDetails().getId());
        List<ShiftTemplateDTO> shiftTemplateDTOS=ObjectMapperUtils.copyProperties(shiftTemplates,ShiftTemplateDTO.class);
        Set<BigInteger> individualShiftTemplateIds=shiftTemplates.stream().flatMap(e->e.getIndividualShiftTemplateIds().stream()).collect(Collectors.toSet());
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS=  individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        Map<BigInteger, IndividualShiftTemplateDTO> individualShiftTemplateDTOMap = individualShiftTemplateDTOS.stream().collect(Collectors.toMap(IndividualShiftTemplateDTO::getId, Function.identity()));
        shiftTemplateDTOS.forEach(shiftTemplateDTO -> {
            shiftTemplateDTO.getIndividualShiftTemplateIds().forEach(individualShiftTemplateId->{
                shiftTemplateDTO.getShiftList().add(individualShiftTemplateDTOMap.get(individualShiftTemplateId));
            });
        });
        return shiftTemplateDTOS;
    }

    public ShiftTemplateDTO updateShiftTemplate(BigInteger shiftTemplateId, ShiftTemplateDTO shiftTemplateDTO){
        ShiftTemplate shiftTemplate= shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent", shiftTemplateId);
        }
        BeanUtils.copyProperties(shiftTemplateDTO,shiftTemplate,"shiftList");
        save(shiftTemplate);
        return shiftTemplateDTO;
    }

    public boolean deleteShiftTemplate(BigInteger shiftTemplateId){
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        //Need to verify that individual shifts should also be deleted or not / mapping
//        List<IndividualShiftTemplate> individualShiftTemplates = individualShiftTemplateRepository.getAllByIdInAndDeletedFalse(shiftTemplate.get().getIndividualShiftTemplateIds());
//        individualShiftTemplates.forEach(individualShiftTemplate -> {
//            individualShiftTemplate.setDeleted(true);});
//        save(individualShiftTemplates);
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
        BeanUtils.copyProperties(individualShiftTemplateDTO,shiftDayTemplate.get());
        save(shiftDayTemplate.get());
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


}
