package com.kairos.activity.service.staffing_level;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.DayType;
import com.kairos.activity.persistence.model.staffing_level.Day;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelInterval;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelTemplate;
import com.kairos.activity.persistence.repository.staffing_level.StaffingLevelTemplateRepository;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTemplateDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class StaffingLevelTemplateService extends MongoBaseService {
    private Logger logger= LoggerFactory.getLogger(StaffingLevelService.class);
    @Autowired
    private StaffingLevelTemplateRepository staffingLevelTemplateRepository;
    @Autowired
    private OrganizationRestClient organizationRestClient;
    @Autowired
    private ExceptionService exceptionService;

    /**
     *
     * @param staffingLevelTemplateDTO
     * @return
     */
    public StaffingLevelTemplateDTO createStaffingTemplate(StaffingLevelTemplateDTO staffingLevelTemplateDTO){
        logger.info("saving staffing level Template  {}",staffingLevelTemplateDTO);

        StaffingLevelTemplate staffingLevelTemplate=StaffingLevelTemplateDTO.buildStaffingLevelTemplate(staffingLevelTemplateDTO);
        this.save(staffingLevelTemplate);
        BeanUtils.copyProperties(staffingLevelTemplate,staffingLevelTemplateDTO);
        staffingLevelTemplateDTO.setPresenceStaffingLevelInterval( staffingLevelTemplateDTO.getPresenceStaffingLevelInterval().stream()
                .sorted(Comparator.comparing(StaffingLevelInterval::getSequence)).collect(Collectors.toList()));
        return staffingLevelTemplateDTO;

    }

    /**
     *
     * @param staffingLevelTemplateDTO
     * @param staffingTemplateId
     * @return
     */
    public StaffingLevelTemplateDTO updateStaffingTemplate(StaffingLevelTemplateDTO staffingLevelTemplateDTO,
        BigInteger staffingTemplateId){
        logger.info("updating staffing level Template ID={}",staffingTemplateId);
        StaffingLevelTemplate staffingLevelTemplate=staffingLevelTemplateRepository.findOne(staffingTemplateId);
        if(Optional.ofNullable(staffingLevelTemplate).isPresent()){
            staffingLevelTemplate=StaffingLevelTemplateDTO.updateStaffingTemplate(staffingTemplateId,staffingLevelTemplateDTO,staffingLevelTemplate);
            this.save(staffingLevelTemplate);
            BeanUtils.copyProperties(staffingLevelTemplate,staffingLevelTemplateDTO);
            staffingLevelTemplateDTO.setPresenceStaffingLevelInterval( staffingLevelTemplate.getPresenceStaffingLevelInterval().stream()
                    .sorted(Comparator.comparing(StaffingLevelInterval::getSequence)).collect(Collectors.toList()));


        }else{
            exceptionService.dataNotFoundByIdException("message.staffleveltemplate",staffingTemplateId);
        }
        return staffingLevelTemplateDTO;

    }

    /**
     * @auther anil maurya
     * <pre>
     *  1.get day type for selected date
     *  2.check validity for staffing level template
     *  3.check valid day type
     *
     * </pre>
     * @param unitId
     * @param proposedDate
     * @return
     */
    public List<StaffingLevelTemplate> getStaffingLevelTemplates(Long unitId,Date proposedDate){

        List<DayType> dayTypes=organizationRestClient.getDayType(proposedDate);
        List<Long>dayTypeIds=dayTypes.stream().map(DayType::getId).collect(Collectors.toList());

        Optional<DayType>holidayDayType=dayTypes.stream().filter(dayType -> dayType.isHolidayType()).findFirst();
        LocalDate localDate= DateUtils.asLocalDate(proposedDate);

        String day=localDate.getDayOfWeek().name();
        Day dayEnum=holidayDayType.isPresent()?Day.EVERYDAY:Day.valueOf(day);
        List<StaffingLevelTemplate> validStaffingLevelTemplates=staffingLevelTemplateRepository.findByUnitIdAndValidityStartDateGreaterThanEqualAndValidityEndDateLessThanEqualAndDayTypeInAndValidDaysIn(unitId,proposedDate,proposedDate,dayTypeIds, Stream.of(dayEnum.toString()).collect(Collectors.toList()));
        return validStaffingLevelTemplates;
    }

}
