package com.kairos.activity.service.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShiftInterval;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OpenShiftIntervalService extends MongoBaseService {
    @Inject
    private OpenShiftIntervalRepository openShiftIntervalRepository;
    @Inject
    private ExceptionService exceptionService;

    public OpenShiftIntervalDTO createInterval(Long countryId, OpenShiftIntervalDTO openShiftIntervalDTO) {
        List<OpenShiftInterval> openShiftIntervals = openShiftIntervalRepository.findAllByCountryIdAndDeletedFalse(countryId);
        Collections.sort(openShiftIntervals);
        validateRange(openShiftIntervalDTO, openShiftIntervals);
        OpenShiftInterval openShiftInterval = new OpenShiftInterval();
        ObjectMapperUtils.copyProperties(openShiftIntervalDTO, openShiftInterval);
        save(openShiftInterval);
        openShiftIntervalDTO.setId(openShiftInterval.getId());
        return openShiftIntervalDTO;
    }

    public List<OpenShiftIntervalDTO> getAllIntervalsByCountryId(Long countryId) {
        List<OpenShiftInterval> openShiftIntervals = openShiftIntervalRepository.findAllByCountryIdAndDeletedFalse(countryId);
        Collections.sort(openShiftIntervals);
        List<OpenShiftIntervalDTO> openShiftIntervalDTOs = ObjectMapperUtils.copyProperties(openShiftIntervals, OpenShiftIntervalDTO.class);
        return openShiftIntervalDTOs;
    }

    public OpenShiftIntervalDTO updateInterval(Long countryId, BigInteger openShiftIntervalId, OpenShiftIntervalDTO openShiftIntervalDTO) {
        OpenShiftInterval openShiftInterval = openShiftIntervalRepository.findByIdAndCountryIdAndDeletedFalse(openShiftIntervalId,countryId);
        if (!Optional.ofNullable(openShiftInterval).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.noOpenShiftIntervalFound", "OpenShiftInterval", openShiftIntervalId);
        }
        List<OpenShiftInterval> openShiftIntervals = openShiftIntervalRepository.findAllByCountryIdAndDeletedFalse(countryId);
        Collections.sort(openShiftIntervals);
        validateRange(openShiftIntervalDTO, openShiftIntervals);
        ObjectMapperUtils.copyProperties(openShiftIntervalDTO, openShiftInterval);
        save(openShiftInterval);
        return openShiftIntervalDTO;
    }

    public boolean deleteOpenShiftInterval(Long countryId, BigInteger openShiftIntervalId) {
        OpenShiftInterval openShiftInterval = openShiftIntervalRepository.findByIdAndCountryIdAndDeletedFalse(openShiftIntervalId,countryId);
        if (!Optional.ofNullable(openShiftInterval).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.noOpenShiftIntervalFound", "OpenShiftInterval", openShiftIntervalId);
        }
        openShiftInterval.setDeleted(true);
        save(openShiftInterval);
        return true;
    }

    private void validateRange(OpenShiftIntervalDTO openShiftIntervalDTO, List<OpenShiftInterval> openShiftIntervals) {
        for (OpenShiftInterval openShiftInterval : openShiftIntervals) {
            if (openShiftInterval.getId().equals(openShiftIntervalDTO.getId())) {
                continue;
            }
            if (openShiftIntervalDTO.getFrom() < openShiftInterval.getFrom() && !(openShiftIntervalDTO.getTo() <= openShiftInterval.getFrom())) {
                exceptionService.actionNotPermittedException("exception.overlap.interval");
            } else if (openShiftIntervalDTO.getFrom() > openShiftInterval.getFrom() && !(openShiftIntervalDTO.getFrom() >= openShiftInterval.getTo())) {
                exceptionService.actionNotPermittedException("exception.overlap.interval");
            } else if (openShiftIntervalDTO.getFrom() == openShiftInterval.getFrom() || (openShiftIntervalDTO.getTo() == openShiftInterval.getTo())) {
                exceptionService.actionNotPermittedException("exception.overlap.interval");
            }
        }
    }

}
