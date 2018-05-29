package com.kairos.activity.service.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.activity.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.activity.persistence.model.priority_group.PriorityGroup;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftRuleTemplateRepository;
import com.kairos.activity.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.persistence.model.organization.OrgTypeAndSubTypeDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class OpenShiftRuleTemplateService extends MongoBaseService {
    @Inject
    private OpenShiftRuleTemplateRepository openShiftRuleTemplateRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private PriorityGroupRepository priorityGroupRepository;

    public OpenShiftRuleTemplateDTO createRuleTemplateForOpenShift(long countryId, OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        boolean isExistWithSameName=openShiftRuleTemplateRepository.existsByNameIgnoreCaseAndDeletedFalseAndCountryId(openShiftRuleTemplateDTO.getName(),countryId);
        if(isExistWithSameName){
            exceptionService.duplicateDataException("exception.duplicate.openShiftRuleTemplate",openShiftRuleTemplateDTO.getName());
        }
        OpenShiftRuleTemplate openShiftRuleTemplate = new OpenShiftRuleTemplate();
        ObjectMapperUtils.copyProperties(openShiftRuleTemplateDTO, openShiftRuleTemplate);
        save(openShiftRuleTemplate);
        List<PriorityGroup> priorityGroups=priorityGroupRepository.findAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        priorityGroups.forEach(priorityGroup -> {
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

    public boolean deleteRuleTemplateForOpenShift(long countryId, BigInteger ruleTemplateId) {
        OpenShiftRuleTemplate openShiftRuleTemplate = openShiftRuleTemplateRepository.findByIdAndCountryIdAndDeletedFalse(ruleTemplateId, countryId);
        if (!Optional.ofNullable(openShiftRuleTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShiftRuleTemplate", ruleTemplateId);
        }
        openShiftRuleTemplate.setDeleted(true);
        save(openShiftRuleTemplate);
        return true;
    }

    public boolean copyRuleTemplateForUnit(long unitId,OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO){
        List<OpenShiftRuleTemplate> openShiftRuleTemplates = openShiftRuleTemplateRepository.findAllByCountryIdAndOrganizationTypeIdAndOrganizationSubTypeIdAndDeletedFalse(orgTypeAndSubTypeDTO.getCountryId(),orgTypeAndSubTypeDTO.getOrganizationTypeId(),orgTypeAndSubTypeDTO.getOrganizationSubTypeId());
        openShiftRuleTemplates.forEach(openShiftRuleTemplate -> {
            openShiftRuleTemplate.setParentId(openShiftRuleTemplate.getId());
            openShiftRuleTemplate.setUnitId(unitId);
            openShiftRuleTemplate.setId(null);
            openShiftRuleTemplate.setCountryId(null);
        });
        save(openShiftRuleTemplates);
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

    public OpenShiftRuleTemplate getRuleTemplateByIdAndUnitId(BigInteger ruleTemplateId,long unitId) {
       return  openShiftRuleTemplateRepository.findByIdAndUnitIdAndDeletedFalse(ruleTemplateId,unitId);
    }

    public OpenShiftRuleTemplate getRuleTemplateByIdAtCountry(BigInteger ruleTemplateId,long countryId) {
        return  openShiftRuleTemplateRepository.findByIdAndCountryIdAndDeletedFalse(ruleTemplateId,countryId);
    }

    public List<OpenShiftRuleTemplateDTO> findByUnitIdAndActivityId(BigInteger activityId,Long unitId){
       return openShiftRuleTemplateRepository.findByUnitIdAndActivityId(activityId,unitId);
    }

    public OpenShiftRuleTemplateDTO createRuleTemplateForUnit(Long unitId,OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO){
        OpenShiftRuleTemplate openShiftRuleTemplate=new OpenShiftRuleTemplate();
        openShiftRuleTemplateDTO.setUnitId(unitId);
        ObjectMapperUtils.copyProperties(openShiftRuleTemplateDTO,openShiftRuleTemplate);
        save(openShiftRuleTemplate);
        List<PriorityGroup> priorityGroups=priorityGroupRepository.findAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(unitId);
        priorityGroups.forEach(priorityGroup -> {
            priorityGroup.setId(null);
            priorityGroup.setRuleTemplateId(openShiftRuleTemplate.getId());
        });
        save(priorityGroups);
        openShiftRuleTemplateDTO.setId(openShiftRuleTemplate.getId());
        return openShiftRuleTemplateDTO;
    }

}
