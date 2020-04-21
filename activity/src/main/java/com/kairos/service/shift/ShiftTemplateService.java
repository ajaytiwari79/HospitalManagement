package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.ActivityMessagesConstants.*;

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
        List<ShiftTemplateDTO> shiftTemplateDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftTemplates, ShiftTemplateDTO.class);
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

    public List<ShiftWithViolatedInfoDTO> createShiftUsingTemplate(Long unitId, ShiftDTO shiftDTO) {
        List<ShiftDTO> shifts = new ArrayList<>();
        List<ShiftWithViolatedInfoDTO>  shiftWithViolatedInfoDTOS = new ArrayList<>();
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
            List<ShiftActivityDTO> childActivities = new ArrayList<>(individualShiftTemplateDTO.getActivities().get(0).getChildActivities().size());
            individualShiftTemplateDTO.getActivities().forEach(shiftTemplateActivity -> {
                shiftTemplateActivity.getChildActivities().forEach(shiftTemplateActivity1 -> {
                    shiftTemplateActivity1.setStartTime(DateUtils.asLocalTime(shiftTemplateActivity1.getStartDate()));
                    shiftTemplateActivity1.setEndTime(DateUtils.asLocalTime(shiftTemplateActivity1.getEndDate()));
                    ShiftActivityDTO  shiftChildActivity =getShiftActivityDTO(shiftDTO,shiftTemplateActivity1);
                    childActivities.add(shiftChildActivity);
                });
                ShiftActivityDTO shiftActivity = getShiftActivityDTO(shiftDTO, shiftTemplateActivity);
                shiftActivity.setChildActivities(childActivities);
                shiftActivities.add(shiftActivity);
            });
            newShiftDTO.setActivities(shiftActivities);
            List<ShiftWithViolatedInfoDTO> result = shiftService.createShift(unitId, newShiftDTO,null);
            for (ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO : result) {
                shiftWithViolatedInfoDTO.setShifts(shiftWithViolatedInfoDTO.getShifts());

                if (CollectionUtils.isNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getActivities())) {
                    shiftWithViolatedInfoDTO.getViolatedRules().getActivities().addAll(shiftWithViolatedInfoDTO.getViolatedRules().getActivities());
                }
                if (CollectionUtils.isNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements())) {
                    shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().addAll(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
                }
                //shifts.addAll(shiftWithViolatedInfoDTO.getShifts());
            }
            shiftWithViolatedInfoDTOS.addAll(result);
        });
        return shiftWithViolatedInfoDTOS;
    }

    private ShiftActivityDTO getShiftActivityDTO(ShiftDTO shiftDTO, ShiftTemplateActivity shiftTemplateActivity) {
        Date startDate = DateUtils.asDate(shiftDTO.getTemplate().getStartDate(), shiftTemplateActivity.getStartTime());
        LocalDate localEndDate =shiftTemplateActivity.getStartTime().isAfter(shiftTemplateActivity.getEndTime())?shiftDTO.getTemplate().getStartDate().plusDays(1):shiftDTO.getTemplate().getStartDate();
        Date endDate = DateUtils.asDate(localEndDate, shiftTemplateActivity.getEndTime());
        return new ShiftActivityDTO(shiftTemplateActivity.getActivityName(), startDate, endDate, shiftTemplateActivity.getActivityId(), shiftTemplateActivity.getAbsenceReasonCodeId());
    }


}
