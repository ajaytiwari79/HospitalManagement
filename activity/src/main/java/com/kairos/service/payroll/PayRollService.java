package com.kairos.service.payroll;
/*
 *Created By Pavan on 14/12/18
 *
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.payroll.PayRollDTO;
import com.kairos.persistence.model.payroll.PayRoll;
import com.kairos.persistence.repository.activity.PayRollRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Service
public class PayRollService extends MongoBaseService {

    @Inject
    private PayRollRepository payRollRepository;
    @Inject
    private ExceptionService exceptionService;

    public PayRollDTO createPayRoll(PayRollDTO payRollDTO) {
        PayRoll payRoll = payRollRepository.getByDeletedFalseAndNameIgnoreCaseOrCode(payRollDTO.getName(),payRollDTO.getCode());
        validatePayRoll(payRoll,payRollDTO);
        payRoll = new PayRoll(null, payRollDTO.getName(), payRollDTO.getCode(), payRollDTO.isActive());
        save(payRoll);
        payRollDTO.setId(payRoll.getId());
        return payRollDTO;
    }

    public PayRollDTO updatePayRoll(BigInteger payRollId, PayRollDTO payRollDTO) {
        PayRoll alreadyExist = payRollRepository.getByDeletedFalseAndIdNotOrNameIgnoreCaseAndCode(payRollId,payRollDTO.getName(),payRollDTO.getCode());
        validatePayRoll(alreadyExist,payRollDTO);
        PayRoll payRoll = payRollRepository.getByIdAndDeletedFalse(payRollId);
        if (payRoll == null) {
            exceptionService.dataNotFoundByIdException("payroll.not.found",payRollId);
        }
        payRoll = new PayRoll(payRoll.getId(), payRollDTO.getName(), payRollDTO.getCode(), payRollDTO.isActive());
        save(payRoll);
        return payRollDTO;
    }

    public boolean deletePayRoll(BigInteger payRollId) {
        payRollRepository.safeDeleteById(payRollId);
        return true;
    }

    public PayRollDTO getPayRollById(BigInteger payRollId) {
        return payRollRepository.findByIdAndDeletedFalse(payRollId);
    }

    public List<PayRollDTO> getAllPayRoll() {
        return payRollRepository.findAllByDeletedFalse();
    }

    public PayRollDTO linkPayRollWithCountry(Long countryId, BigInteger payRollId, boolean checked) {
        PayRoll payRoll = payRollRepository.getByIdAndDeletedFalse(payRollId);
        if (payRoll == null) {
            exceptionService.dataNotFoundByIdException("payroll.not.found",payRollId);
        }

        if (checked){
            payRoll.getCountryIds().add(countryId);
        } else{
            payRoll.getCountryIds().remove(countryId);
        }
        save(payRoll);
        return ObjectMapperUtils.copyPropertiesByMapper(payRoll,PayRollDTO.class);

    }

    public List<PayRollDTO> getAllPayRollOfCountry(Long countryId) {
        List<PayRollDTO> payRollDTOS = payRollRepository.findAllByDeletedFalse();
        payRollDTOS.forEach(payRollDTO -> {
            if (payRollDTO.getCountryIds().contains(countryId)) {
                payRollDTO.setApplicableForCountry(true);
            };
        });
        return payRollDTOS;
    }

    private void validatePayRoll(PayRoll payRoll,PayRollDTO payRollDTO){
        if (payRoll!=null && payRollDTO.getName().equalsIgnoreCase(payRoll.getName())) {
            exceptionService.duplicateDataException("payroll.already.exists.name", payRollDTO.getName());
        }
        if (payRoll!=null && payRollDTO.getCode()==payRoll.getCode()) {
            exceptionService.duplicateDataException("payroll.already.exists.code", payRollDTO.getCode());
        }
    }


}
