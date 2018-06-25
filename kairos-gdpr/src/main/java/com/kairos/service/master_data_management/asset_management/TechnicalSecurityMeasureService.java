package com.kairos.service.master_data_management.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.master_data_management.asset_management.TechnicalSecurityMeasureMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class TechnicalSecurityMeasureService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityMeasureService.class);

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;


    public Map<String, List<TechnicalSecurityMeasure>> createTechnicalSecurityMeasure(Long countryId, List<TechnicalSecurityMeasure> techSecurityMeasures) {
        Map<String, List<TechnicalSecurityMeasure>> result = new HashMap<>();
        List<TechnicalSecurityMeasure> existing = new ArrayList<>();
        List<TechnicalSecurityMeasure> newTechnicalMeasures = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (techSecurityMeasures.size() != 0) {
            for (TechnicalSecurityMeasure technicalSecurityMeasure : techSecurityMeasures) {
                if (!StringUtils.isBlank(technicalSecurityMeasure.getName())) {
                    names.add(technicalSecurityMeasure.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            existing = technicalSecurityMeasureMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));

            if (names.size() != 0) {
                for (String name : names) {

                    TechnicalSecurityMeasure newTechnicalSecurityMeasure = new TechnicalSecurityMeasure();
                    newTechnicalSecurityMeasure.setName(name);
                    newTechnicalSecurityMeasure.setCountryId(countryId);
                    newTechnicalMeasures.add(newTechnicalSecurityMeasure);

                }


                newTechnicalMeasures = save(newTechnicalMeasures);
            }
            result.put("existing", existing);
            result.put("new", newTechnicalMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    public List<TechnicalSecurityMeasure> getAllTechnicalSecurityMeasure() {
        return technicalSecurityMeasureMongoRepository.findAllTechnicalSecurityMeasures(UserContext.getCountryId());
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasure(Long countryId, BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public TechnicalSecurityMeasure updateTechnicalSecurityMeasure(BigInteger id, TechnicalSecurityMeasure techSecurityMeasure) {
        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByNameAndCountryId(UserContext.getCountryId(),techSecurityMeasure.getName());
        if (Optional.ofNullable(exist).isPresent() ) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  "+techSecurityMeasure.getName());
        } else {
            exist=technicalSecurityMeasureMongoRepository.findByid(id);
            exist.setName(techSecurityMeasure.getName());
            return save(exist);

        }
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasureByName(Long countryId, String name) {

        if (!StringUtils.isBlank(name)) {
            TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByNameAndCountryId(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
