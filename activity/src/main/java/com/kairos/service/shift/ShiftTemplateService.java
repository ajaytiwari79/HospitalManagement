package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.shift.*;
import com.kairos.enums.TimeTypeEnum;
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

import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.AppConstants.FULL_WEEK;

@Service
@Transactional
public class ShiftTemplateService{


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


    public ShiftTemplateDTO createShiftTemplate(Long unitId, ShiftTemplateDTO shiftTemplateDTO) {

        //Check for activity is absence type or not
        Set<BigInteger> activityIds = shiftTemplateDTO.getShiftList().stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toSet());
        List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
        activities.forEach(activity -> {
            /*if (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) || activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) {
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ABSENCETYPE, activity.getId());
            }*/

            if (TimeTypeEnum.ABSENCE.equals(activity.getBalanceSettingsActivityTab().getTimeType())){
                exceptionService.actionNotPermittedException(MESSAGE_ACTIVITY_ABSENCETYPE, activity.getId());
            }
        });

        //Check for validating duplicate by name
        boolean alreadyExistsByName = shiftTemplateRepository.
                existsByNameIgnoreCaseAndDeletedFalseAndUnitId(shiftTemplateDTO.getName().trim(), unitId, UserContext.getUserDetails().getId());
        if (alreadyExistsByName) {
            exceptionService.duplicateDataException(MESSAGE_SHIFTTEMPLATE_EXISTS, shiftTemplateDTO.getName());
        }
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOs = shiftTemplateDTO.getShiftList();
        List<IndividualShiftTemplate> individualShiftTemplates = new ArrayList<>();
        individualShiftTemplateDTOs.forEach(individualShiftTemplateDTO -> {
            IndividualShiftTemplate individualShiftTemplate = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO, IndividualShiftTemplate.class);
            individualShiftTemplates.add(individualShiftTemplate);
        });
        individualShiftTemplateRepository.saveEntities(individualShiftTemplates);
        Set<BigInteger> individualShiftTemplateIds = new HashSet<>();
        for (int i = 0; i < individualShiftTemplates.size(); i++) {
            shiftTemplateDTO.getShiftList().get(i).setId(individualShiftTemplates.get(i).getId());
            individualShiftTemplateIds.add(individualShiftTemplates.get(i).getId());
        }
        ShiftTemplate shiftTemplate = new ShiftTemplate(shiftTemplateDTO.getName(), individualShiftTemplateIds, unitId);
        shiftTemplateRepository.save(shiftTemplate);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        shiftTemplateDTO.setUnitId(unitId);
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId) {
        List<ShiftTemplate> shiftTemplates = shiftTemplateRepository.findAllByUnitIdAndCreatedByAndDeletedFalse(unitId, UserContext.getUserDetails().getId());
        List<ShiftTemplateDTO> shiftTemplateDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftTemplates, ShiftTemplateDTO.class);
        Set<BigInteger> individualShiftTemplateIds = shiftTemplates.stream().flatMap(e -> e.getIndividualShiftTemplateIds().stream()).collect(Collectors.toSet());
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        Set<BigInteger> activityIds = individualShiftTemplateDTOS.stream().flatMap(s -> s.getActivities().stream().map(a -> a.getActivityId())).collect(Collectors.toSet());
        Map<BigInteger, String> timeTypeMap = activityMongoRepository.findAllTimeTypeByActivityIds(activityIds).stream().collect(Collectors.toMap(k -> k.getActivityId(), v -> v.getTimeType()));
        Map<BigInteger, IndividualShiftTemplateDTO> individualShiftTemplateDTOMap = individualShiftTemplateDTOS.stream().collect(Collectors.toMap(IndividualShiftTemplateDTO::getId, Function.identity()));
        shiftTemplateDTOS.forEach(shiftTemplateDTO -> {
            shiftTemplateDTO.getIndividualShiftTemplateIds().forEach(individualShiftTemplateId -> {
                IndividualShiftTemplateDTO individualShiftTemplateDTO = individualShiftTemplateDTOMap.get(individualShiftTemplateId);
                individualShiftTemplateDTO.getActivities().forEach(shiftActivity -> {
                    shiftActivity.setTimeType(timeTypeMap.get(shiftActivity.getActivityId()));
                });
                shiftTemplateDTO.getShiftList().add(individualShiftTemplateDTO);
            });
        });
        return shiftTemplateDTOS;
    }

    public ShiftTemplateDTO updateShiftTemplate(Long unitId, BigInteger shiftTemplateId, ShiftTemplateDTO shiftTemplateDTO) {
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftTemplateId);
        if (!Optional.ofNullable(shiftTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFTTEMPLATE_ABSENT, shiftTemplateId);
        }
        UserInfo userInfo = shiftTemplate.getCreatedBy();
        shiftTemplateDTO.setId(shiftTemplateId);
        shiftTemplateDTO.setIndividualShiftTemplateIds(shiftTemplate.getIndividualShiftTemplateIds());
        shiftTemplateDTO.setUnitId(unitId);
        shiftTemplate = ObjectMapperUtils.copyPropertiesByMapper(shiftTemplateDTO, ShiftTemplate.class);
        shiftTemplate.setCreatedBy(userInfo);
        shiftTemplateRepository.save(shiftTemplate);
        return shiftTemplateDTO;
    }

    public boolean deleteShiftTemplate(BigInteger shiftTemplateId) {
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftTemplateId);
        if (!Optional.ofNullable(shiftTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFTTEMPLATE_ABSENT, shiftTemplateId);
        }
        List<IndividualShiftTemplate> individualShiftTemplates = individualShiftTemplateRepository.getAllByIdInAndDeletedFalse(shiftTemplate.getIndividualShiftTemplateIds());
        individualShiftTemplates.forEach(individualShiftTemplate -> {
            individualShiftTemplate.setDeleted(true);
        });
        individualShiftTemplateRepository.saveEntities(individualShiftTemplates);
        shiftTemplate.setDeleted(true);
        shiftTemplateRepository.save(shiftTemplate);
        return true;

    }

    public IndividualShiftTemplateDTO updateIndividualShiftTemplate(BigInteger individualShiftTemplateId, IndividualShiftTemplateDTO individualShiftTemplateDTO) {
        Optional<IndividualShiftTemplate> shiftDayTemplate = individualShiftTemplateRepository.findById(individualShiftTemplateId);
        if (!shiftDayTemplate.isPresent() || shiftDayTemplate.get().isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_INDIVIDUAL_SHIFTTEMPLATE_ABSENT, individualShiftTemplateId);
        }
        individualShiftTemplateDTO.setId(shiftDayTemplate.get().getId());
        IndividualShiftTemplate individualShiftTemplate = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO, IndividualShiftTemplate.class);
        individualShiftTemplate.setId(shiftDayTemplate.get().getId());
        individualShiftTemplateRepository.save(individualShiftTemplate);
        return individualShiftTemplateDTO;
    }

    public IndividualShiftTemplateDTO addIndividualShiftTemplate(BigInteger shiftTemplateId, IndividualShiftTemplateDTO individualShiftTemplateDTO) {
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftTemplateId);
        if (!Optional.ofNullable(shiftTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_INDIVIDUAL_SHIFTTEMPLATE_ABSENT, shiftTemplateId);
        }
        IndividualShiftTemplate individualShiftTemplate = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO, IndividualShiftTemplate.class);
        individualShiftTemplateRepository.save(individualShiftTemplate);
        shiftTemplate.getIndividualShiftTemplateIds().add(individualShiftTemplate.getId());
        shiftTemplateRepository.save(shiftTemplate);
        individualShiftTemplateDTO.setId(individualShiftTemplate.getId());
        return individualShiftTemplateDTO;
    }

    public boolean deleteIndividualShiftTemplate(BigInteger shiftTemplateId, BigInteger individualShiftTemplateId) {
        IndividualShiftTemplate individualShiftTemplate = individualShiftTemplateRepository.findOneById(individualShiftTemplateId);
        if (!Optional.ofNullable(individualShiftTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_INDIVIDUAL_SHIFTTEMPLATE_ABSENT, individualShiftTemplateId);
        }
        individualShiftTemplate.setDeleted(true);
        individualShiftTemplateRepository.save(individualShiftTemplate);
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftTemplateId);
        shiftTemplate.getIndividualShiftTemplateIds().remove(individualShiftTemplate.getId());
        shiftTemplateRepository.save(shiftTemplate);
        return true;
    }

    public ShiftWithViolatedInfoDTO createShiftUsingTemplate(Long unitId, ShiftDTO shiftDTO) {
        List<ShiftDTO> shifts = new ArrayList<>();
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO();
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findOneById(shiftDTO.getTemplate().getId());
        Set<BigInteger> individualShiftTemplateIds = shiftTemplate.getIndividualShiftTemplateIds();
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateRepository.getAllIndividualShiftTemplateByIdsIn(individualShiftTemplateIds);
        individualShiftTemplateDTOS.forEach(individualShiftTemplateDTO -> {
            ShiftDTO newShiftDTO = ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO, ShiftDTO.class);
            newShiftDTO.setActivities(null);
            newShiftDTO.setId(null);
            newShiftDTO.setStaffId(shiftDTO.getStaffId());
            newShiftDTO.setEmploymentId(shiftDTO.getEmploymentId());
            newShiftDTO.setShiftDate(shiftDTO.getShiftDate());
            List<ShiftActivityDTO> shiftActivities = new ArrayList<>(individualShiftTemplateDTO.getActivities().size());
            individualShiftTemplateDTO.getActivities().forEach(shiftTemplateActivity -> {
                Date startDate = DateUtils.asDate(shiftDTO.getTemplate().getStartDate(), shiftTemplateActivity.getStartTime());
                Date endDate = DateUtils.asDate(shiftDTO.getTemplate().getStartDate(), shiftTemplateActivity.getEndTime());
                ShiftActivityDTO shiftActivity = new ShiftActivityDTO(shiftTemplateActivity.getActivityName(), startDate, endDate, shiftTemplateActivity.getActivityId(), shiftTemplateActivity.getAbsenceReasonCodeId());
                shiftActivities.add(shiftActivity);
            });
            newShiftDTO.setActivities(shiftActivities);
            ShiftWithViolatedInfoDTO result = shiftService.createShift(unitId, newShiftDTO, "Organization",null);
            shiftWithViolatedInfoDTO.setShifts(result.getShifts());

            if (CollectionUtils.isNotEmpty(result.getViolatedRules().getActivities())) {
                shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(result.getViolatedRules().getActivities());
            }
            if (CollectionUtils.isNotEmpty(result.getViolatedRules().getWorkTimeAgreements())) {
                shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(result.getViolatedRules().getWorkTimeAgreements());
            }
            shifts.addAll(result.getShifts());
        });
        shiftWithViolatedInfoDTO.setShifts(shifts);
        return shiftWithViolatedInfoDTO;
    }


}
