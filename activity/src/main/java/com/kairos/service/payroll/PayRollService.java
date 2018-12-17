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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstants.LINK;
import static com.kairos.constants.AppConstants.UNLINK;

@Service
public class PayRollService extends MongoBaseService {

    @Inject
    private PayRollRepository payRollRepository;
    @Inject
    private ExceptionService exceptionService;

    public PayRollDTO createPayRoll(PayRollDTO payRollDTO){
        if(payRollRepository.existsByNameIgnoreCaseAndDeletedFalse(payRollDTO.getName().trim())){
            exceptionService.duplicateDataException("data.already.exists",payRollDTO.getName());
        }
        PayRoll payRoll=new PayRoll(null,payRollDTO.getName().trim(),payRollDTO.getCode(),payRollDTO.isActive());
        save(payRoll);
        payRollDTO.setId(payRoll.getId());
        return payRollDTO;
    }

    public PayRollDTO updatePayRoll(BigInteger payRollId,PayRollDTO payRollDTO){
        if(payRollRepository.existsByNameIgnoreCaseAndDeletedFalseAndIdNot(payRollDTO.getName(),payRollId)){
            exceptionService.duplicateDataException("data.already.exists",payRollDTO.getName());
        }
        PayRoll payRoll=payRollRepository.findById(payRollId).orElse(null);
        if(payRoll==null){
            exceptionService.dataNotFoundByIdException("data.not.found");
        }
        payRoll=new PayRoll(payRoll.getId(),payRollDTO.getName().trim(),payRollDTO.getCode(),payRollDTO.isActive());
        save(payRoll);
        return payRollDTO;
    }

    public boolean deletePayRoll(BigInteger payRollId){
        payRollRepository.safeDeleteById(payRollId);
        return true;
    }

    public PayRollDTO getPayRollById(BigInteger payRollId){
        return payRollRepository.findByIdAndDeletedFalse(payRollId);
    }

    public List<PayRollDTO> getAllPayRoll(){
        return payRollRepository.findAllByDeletedFalse();
    }

    public List<PayRollDTO> linkPayRollWithCountry(Long countryId, Set<BigInteger> payRollIds,String action){
        List<PayRoll> payRolls=payRollRepository.findAllByIdsInAndDeletedFalse(payRollIds);
        if(CollectionUtils.isNotEmpty(payRolls)){
            payRolls.forEach(payRoll -> {
                if(LINK.equals(action))payRoll.getCountryIds().add(countryId);
                else if(UNLINK.equals(action))payRoll.getCountryIds().remove(countryId);});
            save(payRolls);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(payRolls,PayRollDTO.class);
    }

    public List<PayRollDTO> getAllPayRollOfCountry(Long countryId){
        List<PayRollDTO> payRollDTOS=payRollRepository.findAllByDeletedFalse();
        payRollDTOS.forEach(payRollDTO -> {if(payRollDTO.getCountryIds().contains(countryId))payRollDTO.setApplicableForCountry(true);});
        return payRollDTOS;
    }


}
