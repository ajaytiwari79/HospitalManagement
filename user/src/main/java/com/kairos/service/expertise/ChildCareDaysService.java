package com.kairos.service.expertise;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CareDaysDetails;
import com.kairos.persistence.model.user.expertise.CareDays;
import com.kairos.persistence.model.user.expertise.ChildCareDays;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorDays;
import com.kairos.persistence.repository.user.expertise.ChildCareDaysGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.UserMessagesConstants.*;

@Service
public class ChildCareDaysService {

    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ChildCareDaysGraphRepository childCareDaysGraphRepository;

    public CareDaysDetails saveChildCareDays(Long expertiseId, CareDaysDetails careDaysDetails) {
        Expertise expertise = expertiseService.findById(expertiseId, 1);
        ChildCareDays childCareDays = ObjectMapperUtils.copyPropertiesByMapper(careDaysDetails, ChildCareDays.class);
        childCareDays.setExpertise(expertise);
        childCareDaysGraphRepository.save(childCareDays);
        careDaysDetails.setId(childCareDays.getId());
        return careDaysDetails;
    }

    public List<CareDaysDetails> getChildCareDays(Long expertiseId) {
        List<ChildCareDays> childCareDays = childCareDaysGraphRepository.getChildCareDaysOfExpertise(expertiseId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(childCareDays, CareDaysDetails.class);
    }

    public CareDaysDetails updateChildCareDays(CareDaysDetails careDaysDetails) {
        ChildCareDays childCareDays = childCareDaysGraphRepository.findById(careDaysDetails.getId()).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, "childCareDays", careDaysDetails.getId())));
        if (!childCareDays.getStartDate().equals(careDaysDetails.getStartDate()) || childCareDays.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_FUNCTIONALPAYMENT_UNEDITABLE, "startdate");
        }
        childCareDays.setStartDate(careDaysDetails.getStartDate());
        childCareDays.setEndDate(careDaysDetails.getEndDate());
        childCareDaysGraphRepository.save(childCareDays);
        return careDaysDetails;
    }

    public List<AgeRangeDTO> addMatrixInChildCareDays(Long childCareDaysId, List<AgeRangeDTO> ageRangeDTO) {
        ChildCareDays childCareDays = childCareDaysGraphRepository.findOne(childCareDaysId);
        if (isNull(childCareDays) || childCareDays.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EXPERTISE_ID_NOTFOUND, childCareDaysId);
        }
        validateAgeRange(ageRangeDTO);
        List<CareDays> careDays = ObjectMapperUtils.copyCollectionPropertiesByMapper(ageRangeDTO, CareDays.class);
        childCareDays.setCareDays(careDays);
        childCareDaysGraphRepository.save(childCareDays);
        return ageRangeDTO;
    }

    //Validating age range
    private void validateAgeRange(List<AgeRangeDTO> ageRangeDTO) {
        Collections.sort(ageRangeDTO);
        for (int i = 0; i < ageRangeDTO.size(); i++) {
            if (ageRangeDTO.get(i).getTo() != null && (ageRangeDTO.get(i).getFrom() > ageRangeDTO.get(i).getTo()))
                exceptionService.actionNotPermittedException(MESSAGE_EXPERTISE_AGE_RANGEINVALID, ageRangeDTO.get(i).getFrom(), ageRangeDTO.get(i).getTo());
            if (ageRangeDTO.size() > 1 && i < ageRangeDTO.size() - 1 && ageRangeDTO.get(i).getTo() > ageRangeDTO.get(i + 1).getFrom())
                exceptionService.actionNotPermittedException(MESSAGE_EXPERTISE_AGE_OVERLAP);

        }

    }

    public ChildCareDays getMatrixOfChildCareDays(Long childCareDaysId) {
        return childCareDaysGraphRepository.findOne(childCareDaysId);
    }

    public CareDaysDetails updateMatrixInChildCareDays(Long childCareDayId,List<AgeRangeDTO> ageRangeDTOS) {
        ChildCareDays childCareDays = childCareDaysGraphRepository.findById(childCareDayId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, FUNCTIONALPAYMENT, childCareDayId)));

        if (childCareDays.isOneTimeUpdatedAfterPublish()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        validateAgeRange(ageRangeDTOS);
        if (childCareDays.isPublished()) {
            // functional payment is published so we need to create a  new copy and update in same
            ChildCareDays childCareDayCopy = ObjectMapperUtils.copyPropertiesByMapper(childCareDays,ChildCareDays.class);
            childCareDayCopy.setPublished(false);
            childCareDayCopy.setOneTimeUpdatedAfterPublish(false);
            childCareDays.setOneTimeUpdatedAfterPublish(true);
            childCareDayCopy.setParentChildCareDays(childCareDays);
            childCareDayCopy.setId(null);
            List<CareDays> careDays=ObjectMapperUtils.copyCollectionPropertiesByMapper(ageRangeDTOS,CareDays.class);
            careDays.forEach(careDays1 -> careDays1.setId(null));
            childCareDayCopy.setCareDays(careDays);
            childCareDaysGraphRepository.save(childCareDayCopy);

        } else {
            // update in current copy

            List<CareDays> careDays = ObjectMapperUtils.copyCollectionPropertiesByMapper(ageRangeDTOS, CareDays.class);
            childCareDays.setCareDays(careDays);
            childCareDaysGraphRepository.save(childCareDays);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(childCareDays,CareDaysDetails.class);

    }

    public CareDaysDetails publishChildCareDays(Long childCareDaysId, LocalDate publishedDate) {
        ChildCareDays childCareDays = childCareDaysGraphRepository.findById(childCareDaysId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, FUNCTIONALPAYMENT, childCareDaysId)));
        if (isCollectionEmpty(childCareDays.getCareDays())) {
            exceptionService.actionNotPermittedException(MESSAGE_FUNCTIONAL_PAYMENT_EMPTY_MATRIX);
        }
        if (childCareDays.isPublished()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_FUNCTIONALPAYMENT_ALREADYPUBLISHED);
        }
        if (childCareDays.getStartDate().isAfter(publishedDate) ||
                (childCareDays.getEndDate()!=null && childCareDays.getEndDate().isBefore(publishedDate))) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISHDATE_NOTLESSTHAN_STARTDATE);
        }
        childCareDays.setPublished(true);
        childCareDays.setStartDate(publishedDate); // changing
        ChildCareDays parentChildCareDays = childCareDays.getParentChildCareDays();
        ChildCareDays lastChildCareDays = childCareDaysGraphRepository.findLastByExpertiseId(childCareDays.getExpertise().getId());
        boolean onGoingUpdated = false;
        if (lastChildCareDays != null && publishedDate.isAfter(lastChildCareDays.getStartDate()) && lastChildCareDays.getEndDate() == null) {
            lastChildCareDays.setEndDate(publishedDate.minusDays(1));
            childCareDaysGraphRepository.save(lastChildCareDays);
            childCareDaysGraphRepository.detachChildCareDays(childCareDaysId, parentChildCareDays.getId());
            childCareDays.setEndDate(null);
            onGoingUpdated = true;
        }
        if (!onGoingUpdated && Optional.ofNullable(parentChildCareDays).isPresent()) {
            if (parentChildCareDays.getStartDate().isEqual(publishedDate) || parentChildCareDays.getStartDate().isAfter(publishedDate)) {
                exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISHDATE_NOTLESSTHAN_OR_EQUALS_PARENT_STARTDATE);
            }
            childCareDaysGraphRepository.setEndDateToChildCareDays(childCareDaysId, parentChildCareDays.getId(), publishedDate.minusDays(1L).toString());
            parentChildCareDays.setEndDate(publishedDate.minusDays(1L));
            if (lastChildCareDays == null && childCareDays.getEndDate() != null && childCareDays.getEndDate().isBefore(publishedDate)) {
                childCareDays.setEndDate(null);
            }
        }
        childCareDays.setParentChildCareDays(null);
        childCareDaysGraphRepository.save(childCareDays);
        return ObjectMapperUtils.copyPropertiesByMapper(parentChildCareDays,CareDaysDetails.class);

    }

    public boolean deleteChildCareDays(Long childCareId) {
        return childCareDaysGraphRepository.deleteChildCareDays(childCareId);
    }


}
