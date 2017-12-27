package com.kairos.service.pay_level;

import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.pay_level.*;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_level.PayLevelGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by prabjot on 26/12/17.
 */
@Service
public class PayLevelService extends UserBaseService {

    @Inject
    private PayLevelGraphRepository payLevelGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;

    public Map<String, Object> getPayLevels(Long countryId){
        List<PayLevelGlobalData> payLevelGlobalData = payLevelGraphRepository.getPayLevelGlobalData(countryId);
        List<PayLevelDTO> payLevelDTOS = payLevelGraphRepository.getPayLevels(countryId);
        Map<String,Object> payLevelResponse = new HashMap<>();
        PayLevelGlobalDataWrapper payLevelGlobalDataWrapper = new PayLevelGlobalDataWrapper(payLevelGlobalData, PaymentUnit.getValues());
        payLevelResponse.put("payLevelGlobalData",payLevelGlobalDataWrapper);
        payLevelResponse.put("payLevels",payLevelDTOS);
        return payLevelResponse;

    }

    public PayLevelDTO createPayLevel(Long countryId, PayLevelDTO payLevelDTO){
        validatePayLevel(countryId,payLevelDTO);
        PayLevel payLevel = validatePayLevelMetaData(countryId,payLevelDTO);
        save(payLevel);
        payLevelDTO.setId(payLevel.getId());
        return payLevelDTO;
    }

    private void validatePayLevel(Long countryId,PayLevelDTO payLevelDTO){

        List<PayLevelDTO> payLevels = payLevelGraphRepository.findByOrganizationTypeAndExpertiseId(countryId,
                payLevelDTO.getOrganizationTypeId(),payLevelDTO.getExpertiseId(),payLevelDTO.getLevelId());
        payLevels.forEach(payLevelToValidate -> {

            if(payLevelToValidate.getEndDate() == null || payLevelDTO.getStartDate().compareTo(payLevelToValidate.getEndDate())<=0){
                throw new DuplicateDataException("Pay level already exist");
            }
        });

    }

    private PayLevel validatePayLevelMetaData(Long countryId,PayLevelDTO payLevelDTO){
        Country country = countryGraphRepository.findOne(countryId);
        if(country == null){
            throw new InternalError("Invalid countryId");
        }
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(payLevelDTO.getOrganizationTypeId());
        if(organizationType == null){
            throw new InternalError("Invalid Organization type id ");
        }
        Expertise expertise = expertiseGraphRepository.findOne(payLevelDTO.getExpertiseId());
        if(expertise == null){
            throw new InternalError("Invalid expertise id ");
        }

        PayLevel payLevel = new PayLevel(payLevelDTO.getName(),country,expertise,organizationType,payLevelDTO.getPaymentUnit(),
                payLevelDTO.getStartDate());

        if(Optional.ofNullable(payLevelDTO.getLevelId()).isPresent()){
            Level level = organizationTypeGraphRepository.getLevel(payLevelDTO.getOrganizationTypeId(),payLevelDTO.getLevelId());
            if(level == null){
                throw new InternalError("Invalid level id");
            }
            payLevel.setLevel(level);
        }
        payLevel.setEndDate(payLevelDTO.getEndDate());
        return payLevel;
    }

}
