package com.kairos.service.pay_level;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.pay_level.PayLevel;
import com.kairos.persistence.model.user.pay_level.PayLevelDTO;
import com.kairos.persistence.model.user.pay_level.PayLevelGlobalData;
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
        payLevelResponse.put("payLevelGlobalData",payLevelGlobalData);
        payLevelResponse.put("payLevels",payLevelDTOS);
        return payLevelResponse;

    }

    public PayLevel createPayLevel(Long countryId, PayLevelDTO payLevelDTO){

        PayLevel payLevel = validatePayLevelMetaData(countryId,payLevelDTO);
        save(payLevel);
        return payLevel;
    }

    private void validatePayLevel(Long countryId,PayLevelDTO payLevelDTO){


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
            throw new InternalError("Invalida expertise id ");
        }

        PayLevel payLevel = new PayLevel(payLevelDTO.getName(),country,expertise,organizationType,payLevelDTO.getPaymentUnit(),
                payLevelDTO.getStartDate());

        if(Optional.ofNullable(payLevelDTO.getLevelId()).isPresent()){
            Level level = countryGraphRepository.getLevel(countryId,payLevelDTO.getLevelId());
            if(level == null){
                throw new InternalError("Invalid level id");
            }
            payLevel.setLevel(level);
        }
        payLevel.setEndDate(payLevelDTO.getEndDate());
        return payLevel;
    }

}
