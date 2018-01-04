package com.kairos.service.pay_level;

import com.kairos.custom_exception.DataNotFoundByIdException;
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
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

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

    private PayLevel findById(Long payLevelId){
        PayLevel payLevel = payLevelGraphRepository.findOne(payLevelId);
        if(payLevel == null){
            throw new DataNotFoundByIdException("Invalid pay level id");
        }
        return payLevel;
    }

    private PayLevelDTO getPayLevelResponse(PayLevel payLevel,PayLevelDTO payLevelDTO){
        payLevelDTO.setId(payLevel.getId());
        return payLevelDTO;
    }

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
        return getPayLevelResponse(payLevel,payLevelDTO);
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

        DateTime startDateAsJodaDate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);

        PayLevel payLevel = new PayLevel(payLevelDTO.getName(),country,expertise,organizationType,payLevelDTO.getPaymentUnit(),
                startDateAsJodaDate.toDate());

        if(Optional.ofNullable(payLevelDTO.getLevelId()).isPresent()){
            Level level = organizationTypeGraphRepository.getLevel(payLevelDTO.getOrganizationTypeId(),payLevelDTO.getLevelId());
            if(level == null){
                throw new InternalError("Invalid level id");
            }
            payLevel.setLevel(level);
        }

        if(payLevelDTO.getEndDate() != null){
            DateTime endDateAsJodaDate = new DateTime(payLevelDTO.getEndDate()).withHourOfDay(0).withMinuteOfHour(0).
                    withSecondOfMinute(0).withMillisOfSecond(0);
            payLevel.setEndDate(endDateAsJodaDate.toDate());
        }
        return payLevel;
    }

    public PayLevelDTO updatePayLevel(Long payLevelId,PayLevelDTO payLevelDTO){

        PayLevel payLevel = findById(payLevelId);
        DateTime startDateToUpdate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime payLevelDate = new DateTime(payLevel.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        DateTime currentDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        if(startDateToUpdate.compareTo(payLevelDate) != 0  &&
                payLevelDate.compareTo(currentDate) <0){
            throw new InternalError("Start date can't be update");
        }
        DateTime startDateAsJodaDate = new DateTime(payLevelDTO.getStartDate()).withHourOfDay(0).withMinuteOfHour(0).
                withSecondOfMinute(0).withMillisOfSecond(0);
        payLevel.setStartDate(startDateAsJodaDate.toDate());
        if(payLevelDTO.getEndDate() != null){
            payLevel.setEndDate(payLevelDTO.getEndDate());
        }
        save(payLevel);
        return getPayLevelResponse(payLevel,payLevelDTO);
    }
}
