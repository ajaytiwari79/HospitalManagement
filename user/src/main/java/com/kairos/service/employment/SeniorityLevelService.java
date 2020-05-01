package com.kairos.service.employment;

import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_SENIORITYLEVEL_ID_NOTFOUND;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_STAFF_EXPERTISE_NOTASSIGNED;

@Service
public class SeniorityLevelService {
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    private static final Logger LOGGER= LoggerFactory.getLogger(SeniorityLevelService.class);

    public SeniorityLevel getSeniorityLevelByStaffAndExpertise(Long staffId, ExpertiseLine expertiseLine, Long expertiseId) {
        StaffExperienceInExpertiseDTO staffSelectedExpertise = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseId(staffId, expertiseId);
        if (!Optional.ofNullable(staffSelectedExpertise).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_EXPERTISE_NOTASSIGNED);
        }
        Integer experienceInMonth = (int) ChronoUnit.MONTHS.between(DateUtils.asLocalDate(staffSelectedExpertise.getExpertiseStartDate()), LocalDate.now());
        LOGGER.info("user has current experience in months :{}", experienceInMonth);
        SeniorityLevel appliedSeniorityLevel = null;
        for (SeniorityLevel seniorityLevel : expertiseLine.getSeniorityLevel()) {
            if (seniorityLevel.getTo() == null) {
                // more than  is set if
                if (experienceInMonth >= seniorityLevel.getFrom() * 12) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            } else {
                // to and from is present
                LOGGER.info("user has current experience in months :{} ,{},{},{}", seniorityLevel.getFrom(), experienceInMonth, seniorityLevel.getTo(), experienceInMonth);

                if (seniorityLevel.getFrom() * 12 <= experienceInMonth && seniorityLevel.getTo() * 12 >= experienceInMonth) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            }
        }
        if (appliedSeniorityLevel == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SENIORITYLEVEL_ID_NOTFOUND);
        }

        return appliedSeniorityLevel;
    }
}
