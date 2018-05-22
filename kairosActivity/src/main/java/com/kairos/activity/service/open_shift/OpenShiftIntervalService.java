package com.kairos.activity.service.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShiftInterval;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftIntervalRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.OpenShiftIntervalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OpenShiftIntervalService extends MongoBaseService {
    @Inject
    private OpenShiftIntervalRepository openShiftIntervalRepository;
    @Inject private ExceptionService exceptionService;

    public OpenShiftIntervalDTO createInterval(Long countryId,OpenShiftIntervalDTO openShiftIntervalDTO){
        OpenShiftInterval openShiftInterval=new OpenShiftInterval();
        ObjectMapperUtils.copyProperties(openShiftIntervalDTO,openShiftInterval);
        openShiftIntervalRepository.save(openShiftInterval);
        openShiftIntervalDTO.setId(openShiftInterval.getId());
        return openShiftIntervalDTO;
        }

        public List<OpenShiftIntervalDTO> getAllIntervalsByCountryId(Long countryId){
         return openShiftIntervalRepository.findByCountryIdAndDeletedFalse(countryId);
        }

        public OpenShiftIntervalDTO updateInterval(Long countryId,BigInteger openShiftIntervalId,OpenShiftIntervalDTO openShiftIntervalDTO){
            Optional<OpenShiftInterval> openShiftInterval=openShiftIntervalRepository.findById(openShiftIntervalId);
            if(!openShiftInterval.isPresent()){
                exceptionService.dataNotFoundByIdException("exception.noOpenShiftIntervalFound","OpenShiftInterval",openShiftIntervalId);
            }
            ObjectMapperUtils.copyProperties(openShiftIntervalDTO,openShiftInterval);
            openShiftIntervalRepository.save(openShiftInterval.get());
            return openShiftIntervalDTO;
        }

        public boolean deleteOpenShiftInterval(Long countryId,BigInteger openShiftIntervalId){
            Optional<OpenShiftInterval> openShiftInterval=openShiftIntervalRepository.findById(openShiftIntervalId);
            if(!openShiftInterval.isPresent()){
                exceptionService.dataNotFoundByIdException("exception.noOpenShiftIntervalFound","OpenShiftInterval",openShiftIntervalId);
            }
            openShiftInterval.get().setDeleted(true);
            openShiftIntervalRepository.save(openShiftInterval.get());
            return true;
        }

        private void validateRange(OpenShiftInterval openShiftInterval,OpenShiftIntervalDTO openShiftIntervalDTO){
            for
        }

}
