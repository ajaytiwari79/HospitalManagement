package com.kairos.service.expertise;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.CareDaysDetails;
import com.kairos.persistence.model.user.expertise.CareDays;
import com.kairos.persistence.model.user.expertise.ChildCareDays;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.repository.user.expertise.ChildCareDaysGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public CareDaysDetails updateMatrixInChildCareDays(CareDaysDetails careDaysDetails) {
        ChildCareDays childCareDays = childCareDaysGraphRepository.findById(careDaysDetails.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, FUNCTIONALPAYMENT, careDaysDetails.getId())));

        if (childCareDays.isOneTimeUpdatedAfterPublish()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }

        if (childCareDays.isPublished()) {
            // functional payment is published so we need to create a  new copy and update in same
            ChildCareDays childCareDayCopy = ObjectMapperUtils.copyPropertiesByMapper(careDaysDetails,ChildCareDays.class);
            childCareDayCopy.setPublished(false);
            childCareDayCopy.setOneTimeUpdatedAfterPublish(false);
            childCareDays.setOneTimeUpdatedAfterPublish(true);
            childCareDayCopy.setParentChildCareDays(childCareDays);
            childCareDaysGraphRepository.save(childCareDayCopy);
            careDaysDetails.setId(childCareDayCopy.getId());

        } else {
            // update in current copy

            validateAgeRange(careDaysDetails.getCareDays());
            List<CareDays> careDays = ObjectMapperUtils.copyCollectionPropertiesByMapper(careDaysDetails.getCareDays(), CareDays.class);
            childCareDays.setCareDays(careDays);
            childCareDaysGraphRepository.save(childCareDays);
        }
        return careDaysDetails;
    }

    public CareDaysDetails publishChildCareDays(Long childCareDaysId, CareDaysDetails careDaysDetails) {
        ChildCareDays childCareDays = childCareDaysGraphRepository.findById(childCareDaysId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND, FUNCTIONALPAYMENT, careDaysDetails.getId())));
        if (childCareDays.getCareDays().isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_FUNCTIONAL_PAYMENT_EMPTY_MATRIX);
        }
        if (childCareDays.isPublished()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_FUNCTIONALPAYMENT_ALREADYPUBLISHED);
        }
        if (childCareDays.getStartDate().isAfter(careDaysDetails.getStartDate()) ||
                (childCareDays.getEndDate()!=null && childCareDays.getEndDate().isBefore(careDaysDetails.getStartDate()))) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISHDATE_NOTLESSTHAN_STARTDATE);
        }
        childCareDays.setPublished(true);
        childCareDays.setStartDate(careDaysDetails.getStartDate()); // changing
        ChildCareDays parentChildCareDays = childCareDays.getParentChildCareDays();
        ChildCareDays lastChildCareDays = childCareDaysGraphRepository.findLastByExpertiseId(childCareDays.getExpertise().getId());
        boolean onGoingUpdated = false;
        if (lastChildCareDays != null && careDaysDetails.getStartDate().isAfter(lastChildCareDays.getStartDate()) && lastChildCareDays.getEndDate() == null) {
            lastChildCareDays.setEndDate(careDaysDetails.getStartDate().minusDays(1));
            childCareDaysGraphRepository.save(lastChildCareDays);
            childCareDaysGraphRepository.detachChildCareDays(childCareDaysId, parentChildCareDays.getId());
            childCareDays.setEndDate(null);
            onGoingUpdated = true;
        }
        if (!onGoingUpdated && Optional.ofNullable(parentChildCareDays).isPresent()) {
            if (parentChildCareDays.getStartDate().isEqual(careDaysDetails.getStartDate()) || parentChildCareDays.getStartDate().isAfter(careDaysDetails.getStartDate())) {
                exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISHDATE_NOTLESSTHAN_OR_EQUALS_PARENT_STARTDATE);
            }
            childCareDaysGraphRepository.setEndDateToChildCareDays(childCareDaysId, parentChildCareDays.getId(), careDaysDetails.getStartDate().minusDays(1L).toString());
            parentChildCareDays.setEndDate(careDaysDetails.getStartDate().minusDays(1L));
            if (lastChildCareDays == null && childCareDays.getEndDate() != null && childCareDays.getEndDate().isBefore(careDaysDetails.getStartDate())) {
                childCareDays.setEndDate(null);
            }
        }
        childCareDaysGraphRepository.save(childCareDays);
        return ObjectMapperUtils.copyPropertiesByMapper(parentChildCareDays,CareDaysDetails.class);

    }


}
