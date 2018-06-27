package com.kairos.service.country;
import com.kairos.user.country.CountryTimeType;
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
    CountryTimeTypeGraphRepository Neo4jBaseRepository;


    public CountryTimeType addTimeType(CountryTimeType countryTimeType){
        return Neo4jBaseRepository.save(countryTimeType);
    }

    public CountryTimeType getTimeType(Long countryTimeTypeId){
        return Neo4jBaseRepository.findOne(countryTimeTypeId);
    }

    public List<CountryTimeType> getAllTimeType(){
        return Neo4jBaseRepository.findAll();
    }

    public CountryTimeType updateTimeType(CountryTimeType countryTimeType){
        CountryTimeType timeType = Neo4jBaseRepository.findOne(countryTimeType.getId());
        timeType.setName(countryTimeType.getName());
        timeType.setDescription(countryTimeType.getDescription());

        return Neo4jBaseRepository.save(timeType);
    }

    public boolean deleteTimeType(Long countryTimeTypeId){
         Neo4jBaseRepository.deleteById(countryTimeTypeId);

        return !Neo4jBaseRepository.existsById(countryTimeTypeId);
    }
}
