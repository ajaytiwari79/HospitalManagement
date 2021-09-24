package com.kairos.service.priority_group;

import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.activity.open_shift.PriorityGroupWrapper;
import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.persistence.model.priority_group.PriorityGroup;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.priority_group.PriorityGroupRuleDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.ActivityMessagesConstants.*;

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
    private SendGridMailService sendGridMailService;
    @Inject
    private ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(PriorityGroupService.class);

    @Inject private UserIntegrationService userIntegrationService;
    @Inject private CounterRepository counterRepository;
    @Inject
    private OpenShiftNotificationMongoRepository openShiftNotificationRepository;


    public boolean createPriorityGroupForCountry(long countryId,List<PriorityGroupDTO> priorityGroupDTO) {
        boolean isPriorityGroupsAlreadyExists=priorityGroupRepository.existsByCountryId(countryId);
        if(isPriorityGroupsAlreadyExists){
            exceptionService.actionNotPermittedException(PRIORITYGROUP_ALREADY_EXISTS,countryId);
        }
        List<PriorityGroup> priorityGroups=ObjectMapperUtils.copyCollectionPropertiesByMapper(priorityGroupDTO, PriorityGroup.class);
        save(priorityGroups);
        return true;
    }

    public PriorityGroupWrapper findAllPriorityGroups(long countryId) {
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.getAllByCountryIdAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        priorityGroupDTOS.forEach(priorityGroupDTO -> {
            if(priorityGroupDTO.getTranslations()==null){
                priorityGroupDTO.setTranslations(new HashMap<>());
            }
        });
        PriorityGroupDefaultData priorityGroupDefaultData= userIntegrationService.getExpertiseAndEmployment(countryId);
        List<CounterDTO> counters=counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        return new PriorityGroupWrapper(new PriorityGroupDefaultData(priorityGroupDefaultData.getEmploymentTypes(),priorityGroupDefaultData.getExpertises(),counters)
                                        ,priorityGroupDTOS);
    }

    public PriorityGroupDTO updatePriorityGroup(long countryId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,PRIORITY_GROUP,priorityGroupId);
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
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,PRIORITY_GROUP,priorityGroupId);
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
        priorityGroupDTOS.forEach(priorityGroupDTO -> {
            if(priorityGroupDTO.getTranslations()==null){
                priorityGroupDTO.setTranslations(new HashMap<>());
            }
        });
        PriorityGroupDefaultData priorityGroupDefaultData= userIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        List<CounterDTO> counters=counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        return new PriorityGroupWrapper(new PriorityGroupDefaultData(priorityGroupDefaultData.getEmploymentTypes(),priorityGroupDefaultData.getExpertises(),counters),priorityGroupDTOS);

    }


    public PriorityGroupDTO updatePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,PRIORITY_GROUP,priorityGroupId);
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
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,PRIORITY_GROUP,priorityGroupId);
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
        List<PriorityGroup> priorityGroups=ObjectMapperUtils.copyCollectionPropertiesByMapper(priorityGroupDTOs, PriorityGroup.class);
        save(priorityGroups);

        return ObjectMapperUtils.copyCollectionPropertiesByMapper(priorityGroups,PriorityGroupDTO.class);
        //return  priorityGroupDTOs;
    }
    public List<PriorityGroupDTO> updatePriorityGroupsForOrder(List<PriorityGroupDTO> priorityGroupDTOs) {
        List<PriorityGroup> priorityGroups= ObjectMapperUtils.copyCollectionPropertiesByMapper(priorityGroupDTOs,PriorityGroup.class);
        save(priorityGroups);
        return priorityGroupDTOs;
    }

    public PriorityGroupWrapper getPriorityGroupsByRuleTemplateForUnit(Long unitId,BigInteger ruleTemplateId){
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndRuleTemplateIdAndOrderIdIsNullAndDeletedFalse(unitId,ruleTemplateId);
        PriorityGroupDefaultData priorityGroupDefaultData1= userIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        List<CounterDTO> counters=counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertises(),counters);
        return new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);
    }
    public List<PriorityGroupDTO> getPriorityGroupsByOrderId(long unitId,BigInteger orderId) {
        return priorityGroupRepository.findByUnitIdAndOrderIdAndDeletedFalse(unitId,orderId);
    }

    public void notifyStaffByPriorityGroup(BigInteger priorityGroupId){
        if(Optional.ofNullable(priorityGroupId).isPresent()) {
            logger.info("Executing priority group----------->"+priorityGroupId);
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

    public void sendNotificationsToStaff(Map<BigInteger,List<StaffEmploymentQueryResult>> openShiftStaffMap) {
        OpenShiftNotification openShiftNotification;
        List<OpenShiftNotification> openShiftNotifications = new ArrayList<>();

        for(Map.Entry<BigInteger,List<StaffEmploymentQueryResult>> entry:openShiftStaffMap.entrySet()) {

            int fibonacciCounter = 0;//Using it to put fibonacci order in email for testing.
            for(StaffEmploymentQueryResult staffEmploymentQueryResult :entry.getValue()) {

                sendGridMailService.sendMailWithSendGrid(null,null, String.format(AppConstants.OPENSHIFT_EMAIL_BODY,fibonacciCounter++,
                        staffEmploymentQueryResult.getAccumulatedTimeBank(), staffEmploymentQueryResult.getDeltaWeeklytimeBank(),
                        staffEmploymentQueryResult.getPlannedHoursWeek()),AppConstants.OPENSHIFT_SUBJECT, staffEmploymentQueryResult.getStaffEmail());
                openShiftNotification = new OpenShiftNotification(entry.getKey(), staffEmploymentQueryResult.getStaffId());
                openShiftNotifications.add(openShiftNotification);
            }
        }
        save(openShiftNotifications);
    }

    public PriorityGroupWrapper getPriorityGroupsByOrderIdForUnit(Long unitId,BigInteger orderId){
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndOrderIdAndDeletedFalse(unitId,orderId);
        PriorityGroupDefaultData priorityGroupDefaultData1= userIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        List<CounterDTO> counters=counterRepository.getAllCounterBySupportedModule(ModuleType.OPEN_SHIFT);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertises(),counters);
        return new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);

    }

    public Map<String, TranslationInfo> updateTranslation(BigInteger priorityGroupId, Map<String,TranslationInfo> translations) {
        PriorityGroup priorityGroup= priorityGroupRepository.findOne(priorityGroupId);
        priorityGroup.setTranslations(translations);
        priorityGroupRepository.save(priorityGroup);
        return priorityGroup.getTranslations();
    }
}

