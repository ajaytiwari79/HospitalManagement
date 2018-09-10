package com.kairos.service.open_shift;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateAndPriorityGroupWrapper;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.persistence.model.priority_group.PriorityGroup;
import com.kairos.persistence.repository.open_shift.OpenShiftRuleTemplateRepository;
import com.kairos.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenShiftRuleTemplateService extends MongoBaseService {
    @Inject
    private OpenShiftRuleTemplateRepository openShiftRuleTemplateRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private PriorityGroupRepository priorityGroupRepository;

    public OpenShiftRuleTemplateDTO createRuleTemplateForOpenShift(long countryId, OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        boolean isExistWithSameName=openShiftRuleTemplateRepository.existsByNameIgnoreCaseAndDeletedFalseAndCountryId(openShiftRuleTemplateDTO.getName().trim(),countryId);
        if(isExistWithSameName){
            exceptionService.duplicateDataException("exception.duplicate.openShiftRuleTemplate",openShiftRuleTemplateDTO.getName());
        }
        OpenShiftRuleTemplate openShiftRuleTemplate = new OpenShiftRuleTemplate();
        ObjectMapperUtils.copyProperties(openShiftRuleTemplateDTO, openShiftRuleTemplate);
        save(openShiftRuleTemplate);
        List<PriorityGroup> priorityGroups=priorityGroupRepository.findAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        priorityGroups.forEach(priorityGroup -> {
            priorityGroup.setParentId(priorityGroup.getId());
            priorityGroup.setId(null);
            priorityGroup.setRuleTemplateId(openShiftRuleTemplate.getId());
        });
        save(priorityGroups);
        ObjectMapperUtils.copyProperties(openShiftRuleTemplate, openShiftRuleTemplateDTO);
        return openShiftRuleTemplateDTO;
    }

    public List<OpenShiftRuleTemplateDTO> findAllRuleTemplateForOpenShift(long countryId) {
        return openShiftRuleTemplateRepository.findAllRuleTemplateByCountryIdAndDeletedFalse(countryId);
    }

    public OpenShiftRuleTemplateDTO updateRuleTemplateForOpenShift(long countryId, BigInteger ruleTemplateId, OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        OpenShiftRuleTemplate openShiftRuleTemplate = openShiftRuleTemplateRepository.findByIdAndCountryIdAndDeletedFalse(ruleTemplateId, countryId);
        if (!Optional.ofNullable(openShiftRuleTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShiftRuleTemplate", ruleTemplateId);
        }
        ObjectMapperUtils.copyProperties(openShiftRuleTemplateDTO, openShiftRuleTemplate);
        save(openShiftRuleTemplate);
        ObjectMapperUtils.copyProperties(openShiftRuleTemplate, openShiftRuleTemplateDTO);
        return openShiftRuleTemplateDTO;
    }

    public boolean deleteOpenShiftRuleTemplate(long countryId, BigInteger ruleTemplateId) {
        OpenShiftRuleTemplate openShiftRuleTemplate = openShiftRuleTemplateRepository.findByIdAndCountryIdAndDeletedFalse(ruleTemplateId, countryId);
        if (!Optional.ofNullable(openShiftRuleTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShiftRuleTemplate", ruleTemplateId);
        }
        openShiftRuleTemplate.setDeleted(true);
        save(openShiftRuleTemplate);
        return true;
    }

    public boolean copyOpenShiftRuleTemplateInUnit(long unitId,OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO){
        List<OpenShiftRuleTemplate> openShiftRuleTemplates = openShiftRuleTemplateRepository.findAllByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndDeletedFalse(orgTypeAndSubTypeDTO.getCountryId(),orgTypeAndSubTypeDTO.getOrganizationTypeId(),orgTypeAndSubTypeDTO.getOrganizationSubTypeId());

        if (!openShiftRuleTemplates.isEmpty()){
            Set<BigInteger> ruleTemplateIds=openShiftRuleTemplates.stream().map(OpenShiftRuleTemplate::getId).collect(Collectors.toSet());
            List<PriorityGroup> priorityGroups=priorityGroupRepository.findAllByRuleTemplateIdInAndCountryIdAndDeletedFalse(ruleTemplateIds,orgTypeAndSubTypeDTO.getCountryId());
            openShiftRuleTemplates.forEach(openShiftRuleTemplate -> {
                openShiftRuleTemplate.setParentId(openShiftRuleTemplate.getId());
                openShiftRuleTemplate.setUnitId(unitId);
                openShiftRuleTemplate.setId(null);
                openShiftRuleTemplate.setCountryId(null);
            });
            save(openShiftRuleTemplates);
            Map<BigInteger,BigInteger> openShiftRuleTemplateParentIdAndIdMap=openShiftRuleTemplates.stream().collect(Collectors.toMap(OpenShiftRuleTemplate::getParentId,OpenShiftRuleTemplate::getId));
            priorityGroups.forEach(priorityGroup -> {
                priorityGroup.setParentId(priorityGroup.getId());
                priorityGroup.setUnitId(unitId);
                priorityGroup.setId(null);
                priorityGroup.setCountryId(null);
                priorityGroup.setRuleTemplateId(openShiftRuleTemplateParentIdAndIdMap.get(priorityGroup.getRuleTemplateId()));
            });
            if(!priorityGroups.isEmpty()){
                save(priorityGroups);
            }

            }

        return true;
    }

    public List<OpenShiftRuleTemplateDTO> getRuleTemplatesOfUnit(long unitId){
        return openShiftRuleTemplateRepository.findByUnitIdAndDeletedFalse(unitId);
    }

    public OpenShiftRuleTemplateDTO updateRuleTemplateOfUnit(long unitId,BigInteger ruleTemplateId,OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO){
        OpenShiftRuleTemplate openShiftRuleTemplate=openShiftRuleTemplateRepository.findByIdAndUnitIdAndDeletedFalse(ruleTemplateId,unitId);
        if (!Optional.ofNullable(openShiftRuleTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShiftRuleTemplate", ruleTemplateId);
        }
        ObjectMapperUtils.copyProperties(openShiftRuleTemplateDTO,openShiftRuleTemplate);
        save(openShiftRuleTemplate);
        ObjectMapperUtils.copyProperties(openShiftRuleTemplate,openShiftRuleTemplateDTO);
        return openShiftRuleTemplateDTO;
    }

    public boolean deleteRuleTemplateOfUnit(BigInteger ruleTemplateId,long unitId){
        OpenShiftRuleTemplate openShiftRuleTemplate = openShiftRuleTemplateRepository.findByIdAndUnitIdAndDeletedFalse(ruleTemplateId, unitId);
        if (!Optional.ofNullable(openShiftRuleTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShiftRuleTemplate", ruleTemplateId);
        }
        openShiftRuleTemplate.setDeleted(true);
        save(openShiftRuleTemplate);
        return true;
    }



    public OpenShiftRuleTemplateAndPriorityGroupWrapper getRuleTemplateAndPriorityGroupByIdAtUnit(BigInteger ruleTemplateId, long countryId) {
        OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO=  openShiftRuleTemplateRepository.getByIdAndUnitIdAndDeletedFalse(ruleTemplateId,countryId);
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndRuleTemplateIdAndOrderIdIsNullAndDeletedFalse(countryId,ruleTemplateId);
        return new OpenShiftRuleTemplateAndPriorityGroupWrapper(openShiftRuleTemplateDTO,priorityGroupDTOS);
    }



    public OpenShiftRuleTemplateAndPriorityGroupWrapper getRuleTemplateAndPriorityGroupByIdAtCountry(BigInteger ruleTemplateId, long countryId) {
        OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO=  openShiftRuleTemplateRepository.getByIdAndCountryIdAndDeletedFalse(ruleTemplateId,countryId);
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByCountryIdAndRuleTemplateIdAndDeletedFalse(countryId,ruleTemplateId);
        return new OpenShiftRuleTemplateAndPriorityGroupWrapper(openShiftRuleTemplateDTO,priorityGroupDTOS);
    }


    public List<OpenShiftRuleTemplateDTO> findByUnitIdAndActivityId(BigInteger activityId,Long unitId){
       return openShiftRuleTemplateRepository.findByUnitIdAndActivityId(activityId,unitId);
    }

    public OpenShiftRuleTemplateDTO createRuleTemplateForUnit(Long unitId,OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO){
        boolean isExistWithSameName=openShiftRuleTemplateRepository.existsByNameIgnoreCaseAndDeletedFalseAndUnitId(openShiftRuleTemplateDTO.getName().trim(),unitId);
        if(isExistWithSameName){
            exceptionService.duplicateDataException("exception.duplicate.openShiftRuleTemplate",openShiftRuleTemplateDTO.getName());
        }
        OpenShiftRuleTemplate openShiftRuleTemplate=new OpenShiftRuleTemplate();
        openShiftRuleTemplateDTO.setUnitId(unitId);
        ObjectMapperUtils.copyProperties(openShiftRuleTemplateDTO,openShiftRuleTemplate);
        save(openShiftRuleTemplate);
        List<PriorityGroup> priorityGroups=priorityGroupRepository.findAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(unitId);
        if(!priorityGroups.isEmpty()){
            priorityGroups.forEach(priorityGroup -> {
                priorityGroup.setParentId(priorityGroup.getId());
                priorityGroup.setId(null);
                priorityGroup.setRuleTemplateId(openShiftRuleTemplate.getId());
            });
            save(priorityGroups);
        }
        openShiftRuleTemplateDTO.setId(openShiftRuleTemplate.getId());
        return openShiftRuleTemplateDTO;
    }
}