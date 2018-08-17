package com.kairos.service.staffing_level;

import com.kairos.activity.activity.ActivityResponse;
import com.kairos.activity.staffing_level.StaffingLevelInterval;
import com.kairos.activity.staffing_level.StaffingLevelTemplateDTO;
import com.kairos.enums.Day;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelTemplateRepository;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.locale.LocaleService;
import com.kairos.user.country.day_type.DayType;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class StaffingLevelTemplateService extends MongoBaseService {
    private Logger logger = LoggerFactory.getLogger(StaffingLevelService.class);
    @Autowired
    private StaffingLevelTemplateRepository staffingLevelTemplateRepository;
    @Autowired
    private OrganizationRestClient organizationRestClient;
    @Autowired
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private LocaleService localeService;

    /**
     * @param staffingLevelTemplateDTO
     * @return
     */
    public StaffingLevelTemplateDTO createStaffingLevelTemplate(StaffingLevelTemplateDTO staffingLevelTemplateDTO) {
        logger.info("saving staffing level Template  {}", staffingLevelTemplateDTO);
        Set<BigInteger> activityIds=new HashSet<>();
        staffingLevelTemplateDTO.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
            staffingLevelInterval.getStaffingLevelActivities().forEach(staffingLevelActivity -> {
                     activityIds.add(staffingLevelActivity.getActivityId());
                });
        });


        List<Activity> activities=activityMongoRepository.findAllActivitiesByIds(activityIds);
        //validating Activities
        List<ActivityResponse> errors= validateActivityRules(activities,staffingLevelTemplateDTO.getValidity().getStartDate(),staffingLevelTemplateDTO.getValidity().getEndDate(),staffingLevelTemplateDTO.getDayType());
        if(!errors.isEmpty()){
            staffingLevelTemplateDTO.setErrors(errors);
            return staffingLevelTemplateDTO;
        }

        StaffingLevelTemplate staffingLevelTemplate = new StaffingLevelTemplate();
        ObjectMapperUtils.copyProperties(staffingLevelTemplateDTO, staffingLevelTemplate);
        this.save(staffingLevelTemplate);
        BeanUtils.copyProperties(staffingLevelTemplate, staffingLevelTemplateDTO);
        staffingLevelTemplateDTO.setPresenceStaffingLevelInterval(staffingLevelTemplateDTO.getPresenceStaffingLevelInterval().stream()
                .sorted(Comparator.comparing(StaffingLevelInterval::getSequence)).collect(Collectors.toList()));

        return staffingLevelTemplateDTO;

    }

    /**
     * @param staffingLevelTemplateDTO
     * @param staffingTemplateId
     * @return
     */
    public StaffingLevelTemplateDTO updateStaffingTemplate(StaffingLevelTemplateDTO staffingLevelTemplateDTO,
                                                           BigInteger staffingTemplateId) {
        logger.info("updating staffing level Template ID={}", staffingTemplateId);
        Set<BigInteger> activityIds=new HashSet<>();
        staffingLevelTemplateDTO.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
            staffingLevelInterval.getStaffingLevelActivities().forEach(staffingLevelActivity -> {
                activityIds.add(staffingLevelActivity.getActivityId());
            });
        });


        List<Activity> activities=activityMongoRepository.findAllActivitiesByIds(activityIds);
        //validating Activities
        List<ActivityResponse> errors= validateActivityRules(activities,staffingLevelTemplateDTO.getValidity().getStartDate(),staffingLevelTemplateDTO.getValidity().getEndDate(),staffingLevelTemplateDTO.getDayType());
        if(!errors.isEmpty()){
            staffingLevelTemplateDTO.setErrors(errors);
            return staffingLevelTemplateDTO;
        }

        StaffingLevelTemplate staffingLevelTemplate = staffingLevelTemplateRepository.findOne(staffingTemplateId);
        if (Optional.ofNullable(staffingLevelTemplate).isPresent()) {
            staffingLevelTemplate = updateStaffingTemplate(staffingTemplateId, staffingLevelTemplateDTO, staffingLevelTemplate);
            this.save(staffingLevelTemplate);
            BeanUtils.copyProperties(staffingLevelTemplate, staffingLevelTemplateDTO);
            staffingLevelTemplateDTO.setPresenceStaffingLevelInterval(staffingLevelTemplate.getPresenceStaffingLevelInterval().stream()
                    .sorted(Comparator.comparing(StaffingLevelInterval::getSequence)).collect(Collectors.toList()));


        } else {
            exceptionService.dataNotFoundByIdException("message.staffleveltemplate", staffingTemplateId);
        }
        return staffingLevelTemplateDTO;

    }

    private StaffingLevelTemplate updateStaffingTemplate(BigInteger staffingTemplateId, StaffingLevelTemplateDTO staffingLevelTemplateDTO,
                                                               StaffingLevelTemplate staffingLevelTemplate) {

        BeanUtils.copyProperties(staffingLevelTemplateDTO, staffingLevelTemplate);
        staffingLevelTemplate.setId(staffingTemplateId);
        return staffingLevelTemplate;

    }

    /**
     * @param unitId
     * @param proposedDate
     * @return
     * @auther anil maurya
     * <pre>
     *  1.get day type for selected date
     *  2.check validity for staffing level template
     *  3.check valid day type
     *
     * </pre>
     */
    public List<StaffingLevelTemplateDTO> getStaffingLevelTemplates(Long unitId, Date proposedDate) {
        if(!Optional.ofNullable(proposedDate).isPresent()){
            return staffingLevelTemplateRepository.findAllByUnitIdAndDeletedFalse(unitId);
        }
        List<DayType> dayTypes = organizationRestClient.getDayType(proposedDate);
        List<Long> dayTypeIds = dayTypes.stream().map(DayType::getId).collect(Collectors.toList());

        Optional<DayType> holidayDayType = dayTypes.stream().filter(dayType -> dayType.isHolidayType()).findFirst();
        LocalDate localDate = DateUtils.asLocalDate(proposedDate);

        String day = localDate.getDayOfWeek().name();
        Day dayEnum = holidayDayType.isPresent() ? Day.EVERYDAY : Day.valueOf(day);
        return staffingLevelTemplateRepository.findByUnitIdAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqualAndDayTypeInAndValidDaysIn(unitId, proposedDate, proposedDate, dayTypeIds, Stream.of(dayEnum.toString()).collect(Collectors.toList()));
        }

    /**
     * @Auther Pavan Kumar
     * @param activities
     * @param startDate
     * @param endDate
     */
    public List<ActivityResponse> validateActivityRules(List<Activity> activities,LocalDate startDate,LocalDate endDate,List<Long> dayTypes){

        List<ActivityResponse> activityResponse=new ArrayList<>();
        if(!Optional.ofNullable(endDate).isPresent()){
            activities.forEach(activity -> {
                List<String> errors=new ArrayList<>();
                if(!Optional.ofNullable(activity.getGeneralActivityTab().getEndDate()).isPresent() &&
                        activity.getGeneralActivityTab().getStartDate().isBefore(startDate)){
                    errors.add(exceptionService.getLanguageSpecificText("activity.out.of.range",activity.getName()));
                }
                if(!activity.getRulesActivityTab().isEligibleForStaffingLevel())  {
                    errors.add(exceptionService.getLanguageSpecificText("activity.not.eligible.for.staffing.level",activity.getName()));
                }
                if(activity.getRulesActivityTab().isEligibleForPresence()){
                    errors.add(exceptionService.getLanguageSpecificText("activity.not.presenceType",activity.getName()));
                }
                if(!CollectionUtils.containsAny(dayTypes,activity.getRulesActivityTab().getDayTypes())){
                    errors.add(exceptionService.getLanguageSpecificText("activity.not.eligible.dayType",activity.getName()));
                }
                if(!errors.isEmpty()){
                    activityResponse.add(new ActivityResponse(activity.getId(),activity.getName(),activity.getGeneralActivityTab().getStartDate(),
                            activity.getGeneralActivityTab().getEndDate(),errors));
                }

            });
        } else {
            activities.forEach(activity -> {
                List<String> errors=new ArrayList<>();
                if(Optional.ofNullable(activity.getGeneralActivityTab().getEndDate()).isPresent() &&
                        (activity.getGeneralActivityTab().getEndDate().isBefore(startDate) ||
                                activity.getGeneralActivityTab().getStartDate().isAfter(endDate))){
                    errors.add(exceptionService.getLanguageSpecificText("activity.out.of.range",activity.getName()));
                } else if(!Optional.ofNullable(activity.getGeneralActivityTab().getEndDate()).isPresent() &&
                        activity.getGeneralActivityTab().getStartDate().isAfter(endDate)){
                    errors.add(exceptionService.getLanguageSpecificText("activity.out.of.range",activity.getName()));
                }

                if(!activity.getRulesActivityTab().isEligibleForStaffingLevel())  {
                    errors.add(exceptionService.getLanguageSpecificText("activity.not.eligible.for.staffing.level",activity.getName()));
                }
                if(activity.getRulesActivityTab().isEligibleForPresence()){
                    errors.add(exceptionService.getLanguageSpecificText("activity.not.presenceType",activity.getName()));
                }
                if(!CollectionUtils.containsAny(dayTypes,activity.getRulesActivityTab().getDayTypes())){
                    errors.add(exceptionService.getLanguageSpecificText("activity.not.eligible.dayType",activity.getName()));
                }

                if(!errors.isEmpty()){
                    activityResponse.add(new ActivityResponse(activity.getId(),activity.getName(),activity.getGeneralActivityTab().getStartDate(),
                            activity.getGeneralActivityTab().getEndDate(),errors));
                }
            });
        }
        return activityResponse;

    }

    public boolean deleteStaffingLevelTemplate(BigInteger staffingLevelTemplateId){
         staffingLevelTemplateRepository.deleteStaffingLevelTemplate(staffingLevelTemplateId);
         return true;
    }

}
