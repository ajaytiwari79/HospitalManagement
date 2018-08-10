package com.kairos.service.staffing_level;

import com.kairos.activity.activity.ActivityDTO;
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
import com.kairos.user.country.day_type.DayType;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * @param staffingLevelTemplateDTO
     * @return
     */
    public StaffingLevelTemplateDTO createStaffingTemplate(StaffingLevelTemplateDTO staffingLevelTemplateDTO) {
        logger.info("saving staffing level Template  {}", staffingLevelTemplateDTO);
        Set<BigInteger> activityIds=new HashSet<>();
        staffingLevelTemplateDTO.getPresenceStaffingLevelInterval().forEach(staffingLevelInterval -> {
            staffingLevelInterval.getStaffingLevelActivities().forEach(staffingLevelActivity -> {
                     activityIds.add(staffingLevelActivity.getActivityId());
                });
        });
        List<ActivityDTO> activities=activityMongoRepository.getAllInvalidActivitys(activityIds,staffingLevelTemplateDTO.getValidity().getStartDate(),staffingLevelTemplateDTO.getValidity().getEndDate());

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

    public static StaffingLevelTemplate updateStaffingTemplate(BigInteger staffingTemplateId, StaffingLevelTemplateDTO staffingLevelTemplateDTO,
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
    public List<StaffingLevelTemplate> getStaffingLevelTemplates(Long unitId, Date proposedDate) {

        List<DayType> dayTypes = organizationRestClient.getDayType(proposedDate);
        List<Long> dayTypeIds = dayTypes.stream().map(DayType::getId).collect(Collectors.toList());

        Optional<DayType> holidayDayType = dayTypes.stream().filter(dayType -> dayType.isHolidayType()).findFirst();
        LocalDate localDate = DateUtils.asLocalDate(proposedDate);

        String day = localDate.getDayOfWeek().name();
        Day dayEnum = holidayDayType.isPresent() ? Day.EVERYDAY : Day.valueOf(day);
        List<StaffingLevelTemplate> validStaffingLevelTemplates = staffingLevelTemplateRepository.findByUnitIdAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqualAndDayTypeInAndValidDaysIn(unitId, proposedDate, proposedDate, dayTypeIds, Stream.of(dayEnum.toString()).collect(Collectors.toList()));
        return validStaffingLevelTemplates;
    }

}
