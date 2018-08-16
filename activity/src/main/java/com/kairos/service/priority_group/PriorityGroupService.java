package com.kairos.service.priority_group;

import com.kairos.activity.enums.counter.Module;
import com.kairos.activity.counter.CounterDTO;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.persistence.model.priority_group.*;
import com.kairos.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.wrapper.priority_group.PriorityGroupRuleDataDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.mail.MailService;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.activity.open_shift.PriorityGroupWrapper;
import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PriorityGroupService extends MongoBaseService {
    @Inject
    private PriorityGroupRepository priorityGroupRepository;
    @Inject
    private  ExceptionService exceptionService;
    @Inject
    private PriorityGroupRulesDataGetterService priorityGroupRulesDataGetterService;
    @Inject
    private MailService mailService;
    @Inject
    private ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(PriorityGroupService.class);

    @Inject private GenericIntegrationService genericIntegrationService;
    @Inject private CounterRepository counterRepository;


    public boolean createPriorityGroupForCountry(long countryId,List<PriorityGroupDTO> priorityGroupDTO) {
        boolean isPriorityGroupsAlreadyExists=priorityGroupRepository.existsByCountryId(countryId);
        if(isPriorityGroupsAlreadyExists){
            exceptionService.actionNotPermittedException("priorityGroup.already.exists",countryId);
        }
        List<PriorityGroup> priorityGroups=ObjectMapperUtils.copyProperties(priorityGroupDTO, PriorityGroup.class);
        save(priorityGroups);
        return true;
    }

    public PriorityGroupWrapper findAllPriorityGroups(long countryId) {
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.getAllByCountryIdAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        PriorityGroupDefaultData priorityGroupDefaultData=genericIntegrationService.getExpertiseAndEmployment(countryId);
        List<CounterDTO> counters=counterRepository.getAllCounterBySupportedModule(Module.OPEN_SHIFT);
        return new PriorityGroupWrapper(new PriorityGroupDefaultData(priorityGroupDefaultData.getEmploymentTypes(),priorityGroupDefaultData.getExpertises(),counters)
                                        ,priorityGroupDTOS);
    }

    public PriorityGroupDTO updatePriorityGroup(long countryId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        if (priorityGroup.isDeActivated()!=priorityGroupDTO.isDeActivated()){
            priorityGroup.setDeActivated(priorityGroupDTO.isDeActivated());
        }else {
            priorityGroupDTO.setName(priorityGroup.getName());
            ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
            }
        priorityGroup.setId(priorityGroupId);
        priorityGroup.setCountryId(countryId);
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
//        return new PriorityGroupDTO(priorityGroup.getPriority(), priorityGroup.getId(), priorityGroup.isDeActivated(),
//                priorityGroup.getOpenShiftCancelProcess(), priorityGroup.getRoundRules(), priorityGroup.getStaffExcludeFilter(), priorityGroup.getStaffIncludeFilter(), priorityGroup.getCountryId(), priorityGroup.getUnitId());
    }

    public boolean deletePriorityGroup(long countryId, BigInteger priorityGroupId) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        priorityGroup.setDeleted(true);
        save(priorityGroup);
        return true;
    }

    public boolean copyPriorityGroupsForUnit(long unitId,long countryId){
        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        if(!priorityGroups.isEmpty()) {
            priorityGroups.forEach(priorityGroup -> {
                priorityGroup.setParentId(priorityGroup.getId());
                priorityGroup.setUnitId(unitId);
                priorityGroup.setId(null);
                priorityGroup.setCountryId(null);
            });
            save(priorityGroups);
            return true;
        } else  {
            return false;
        }
    }

    public PriorityGroupWrapper getPriorityGroupsOfUnit(long unitId) {
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.getAllByUnitIdAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(unitId);
        PriorityGroupDefaultData priorityGroupDefaultData=genericIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        List<CounterDTO> counters=counterRepository.getAllCounterBySupportedModule(Module.OPEN_SHIFT);
        return new PriorityGroupWrapper(new PriorityGroupDefaultData(priorityGroupDefaultData.getEmploymentTypes(),priorityGroupDefaultData.getExpertises(),counters),priorityGroupDTOS);

    }


    public PriorityGroupDTO updatePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        if (priorityGroup.isDeActivated()!=priorityGroupDTO.isDeActivated()){
            priorityGroup.setDeActivated(priorityGroupDTO.isDeActivated());
        }else {
            ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
            priorityGroup.setId(priorityGroupId);
            priorityGroup.setUnitId(unitId);
            priorityGroup.setName(priorityGroupDTO.getName());
        }
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
//        return new PriorityGroupDTO(priorityGroup.getPriority(), priorityGroup.getId(), priorityGroup.isDeActivated(),
//                priorityGroup.getOpenShiftCancelProcess(), priorityGroup.getRoundRules(), priorityGroup.getStaffExcludeFilter(), priorityGroup.getStaffIncludeFilter(), priorityGroup.getCountryId(), priorityGroup.getUnitId());
    }

    public boolean deletePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        priorityGroup.setDeleted(true);
        save(priorityGroup);
        return true;
    }


    public boolean copyPriorityGroupsForOrder(long unitId, BigInteger orderId){

        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(unitId);
        priorityGroups.forEach(priorityGroup -> {
            priorityGroup.setOrderId(orderId);
            priorityGroup.setId(null);
            });
        save(priorityGroups);
        return true;
    }
    public PriorityGroup getPriorityGroupOfCountryById(long countryId,BigInteger priorityGroupId){
        return priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId,countryId);
    }
    public PriorityGroup getPriorityGroupOfUnitById(Long unitId, BigInteger priorityGroupId){
        return priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId,unitId);
    }
    public List<PriorityGroupDTO> createPriorityGroups(BigInteger orderId,List<PriorityGroupDTO> priorityGroupDTOs) {
        priorityGroupDTOs.forEach(priorityGroupDTO -> {
            priorityGroupDTO.setParentId(priorityGroupDTO.getId());
            priorityGroupDTO.setOrderId(orderId);
            priorityGroupDTO.setId(null);

        });
        List<PriorityGroup> priorityGroups=ObjectMapperUtils.copyProperties(priorityGroupDTOs, PriorityGroup.class);
        save(priorityGroups);

        return ObjectMapperUtils.copyPropertiesOfListByMapper(priorityGroups,PriorityGroupDTO.class);
        //return  priorityGroupDTOs;
    }
    public List<PriorityGroupDTO> updatePriorityGroupsForOrder(List<PriorityGroupDTO> priorityGroupDTOs) {
        List<PriorityGroup> priorityGroups= ObjectMapperUtils.copyProperties(priorityGroupDTOs,PriorityGroup.class);
        save(priorityGroups);
        return priorityGroupDTOs;
    }

    public PriorityGroupWrapper getPriorityGroupsByRuleTemplateForUnit(Long unitId,BigInteger ruleTemplateId){
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndRuleTemplateIdAndOrderIdIsNullAndDeletedFalse(unitId,ruleTemplateId);
        PriorityGroupDefaultData priorityGroupDefaultData1=genericIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertises());
        return new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);
    }
    public List<PriorityGroupDTO> getPriorityGroupsByOrderId(long unitId,BigInteger orderId) {
        return priorityGroupRepository.findByUnitIdAndOrderIdAndDeletedFalse(unitId,orderId);
    }

    public void notifyStaffByPriorityGroup(BigInteger priorityGroupId){
        if(Optional.ofNullable(priorityGroupId).isPresent()) {
            logger.info("Excuting priority group----------->"+priorityGroupId);
            PriorityGroupDTO priorityGroup = priorityGroupRepository.findByIdAndDeletedFalse(priorityGroupId);
            PriorityGroupRuleDataDTO priorityGroupRuleDataDTO = priorityGroupRulesDataGetterService.getData(priorityGroup);
            logger.info("Priority group data---------->filtering staffs from---------->"+priorityGroupRuleDataDTO.getOpenShiftStaffMap().toString());
            PriorityGroupRulesExecutorService priorityGroupRulesExecutorService = new PriorityGroupRulesExecutorService();
            ImpactWeight impactWeight = new ImpactWeight(7,4);
            priorityGroupRulesExecutorService.executeRules(priorityGroup,priorityGroupRuleDataDTO,impactWeight);
            logger.info("Priority group data---------->filtering staffs from---------->"+priorityGroupRuleDataDTO.getOpenShiftStaffMap().toString());

            applicationContext.publishEvent(priorityGroupRuleDataDTO);
        }

    }

    public PriorityGroupWrapper getPriorityGroupsByOrderIdForUnit(Long unitId,BigInteger orderId){
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndOrderIdAndDeletedFalse(unitId,orderId);
        PriorityGroupDefaultData priorityGroupDefaultData1=genericIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertises());
        return new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);

    }

}

