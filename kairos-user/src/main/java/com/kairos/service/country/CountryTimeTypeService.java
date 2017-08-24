package com.kairos.service.country;
import com.kairos.persistence.model.user.country.CountryTimeType;
import com.kairos.persistence.repository.user.country.CountryTimeTypeGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by oodles on 23/11/16.
 */
@Service
@Transactional
public class CountryTimeTypeService extends UserBaseService {


    @Inject
    CountryTimeTypeGraphRepository graphRepository;


    public CountryTimeType addTimeType(CountryTimeType countryTimeType){
        return graphRepository.save(countryTimeType);
    }

    public CountryTimeType getTimeType(Long countryTimeTypeId){
        return graphRepository.findOne(countryTimeTypeId);
    }

    public List<CountryTimeType> getAllTimeType(){
        return graphRepository.findAll();
    }

    public CountryTimeType updateTimeType(CountryTimeType countryTimeType){
        CountryTimeType timeType = graphRepository.findOne(countryTimeType.getId());
        timeType.setName(countryTimeType.getName());
        timeType.setDescription(countryTimeType.getDescription());

        return graphRepository.save(timeType);
    }

    public boolean deleteTimeType(Long countryTimeTypeId){
         graphRepository.delete(countryTimeTypeId);

        return !graphRepository.exists(countryTimeTypeId);
    }
}
