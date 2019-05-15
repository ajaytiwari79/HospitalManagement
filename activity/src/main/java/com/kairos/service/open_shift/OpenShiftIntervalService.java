package com.kairos.service.open_shift;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.persistence.model.open_shift.OpenShiftInterval;
import com.kairos.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.EXCEPTION_NOOPENSHIFTINTERVALFOUND;
import static com.kairos.constants.ActivityMessagesConstants.EXCEPTION_OVERLAP_INTERVAL;

@Service
@Transactional
public class OpenShiftIntervalService extends MongoBaseService {
    @Inject
    private OpenShiftIntervalRepository openShiftIntervalRepository;
    @Inject
    private ExceptionService exceptionService;

    public OpenShiftIntervalDTO createInterval(Long countryId, OpenShiftIntervalDTO openShiftIntervalDTO) {
        boolean isIntervalInValid= openShiftIntervalRepository.isIntervalInValid(openShiftIntervalDTO.getFrom(),openShiftIntervalDTO.getTo(),null);
        if(isIntervalInValid){
            exceptionService.actionNotPermittedException(EXCEPTION_OVERLAP_INTERVAL);
        }
        OpenShiftInterval openShiftInterval = new OpenShiftInterval();
        ObjectMapperUtils.copyProperties(openShiftIntervalDTO, openShiftInterval);
        save(openShiftInterval);
        openShiftIntervalDTO.setId(openShiftInterval.getId());
        return openShiftIntervalDTO;
    }

    public List<OpenShiftIntervalDTO> getAllIntervalsByCountryId(Long countryId) {
        List<OpenShiftInterval> openShiftIntervals = openShiftIntervalRepository.findAllByCountryIdAndDeletedFalse(countryId);
        Collections.sort(openShiftIntervals);
        List<OpenShiftIntervalDTO> openShiftIntervalDTOs = ObjectMapperUtils.copyPropertiesOfListByMapper(openShiftIntervals, OpenShiftIntervalDTO.class);
        return openShiftIntervalDTOs;
    }

    public OpenShiftIntervalDTO updateInterval(Long countryId, BigInteger openShiftIntervalId, OpenShiftIntervalDTO openShiftIntervalDTO) {
        OpenShiftInterval openShiftInterval = openShiftIntervalRepository.findByIdAndCountryIdAndDeletedFalse(openShiftIntervalId,countryId);
        if (!Optional.ofNullable(openShiftInterval).isPresent()) {
            exceptionService.dataNotFoundByIdException(EXCEPTION_NOOPENSHIFTINTERVALFOUND, "OpenShiftInterval", openShiftIntervalId);
        }
        boolean isIntervalInValid= openShiftIntervalRepository.isIntervalInValid(openShiftIntervalDTO.getFrom(),openShiftIntervalDTO.getTo(),openShiftIntervalId);
        if(isIntervalInValid){
            exceptionService.actionNotPermittedException(EXCEPTION_OVERLAP_INTERVAL);
        }
        ObjectMapperUtils.copyProperties(openShiftIntervalDTO, openShiftInterval);
        save(openShiftInterval);
        return openShiftIntervalDTO;
    }

    public boolean deleteOpenShiftInterval(Long countryId, BigInteger openShiftIntervalId) {
        OpenShiftInterval openShiftInterval = openShiftIntervalRepository.findByIdAndCountryIdAndDeletedFalse(openShiftIntervalId,countryId);
        if (!Optional.ofNullable(openShiftInterval).isPresent()) {
            exceptionService.dataNotFoundByIdException(EXCEPTION_NOOPENSHIFTINTERVALFOUND, "OpenShiftInterval", openShiftIntervalId);
        }
        openShiftInterval.setDeleted(true);
        save(openShiftInterval);
        return true;
    }


}
