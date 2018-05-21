package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.master_data_management.asset_management.TechnicalSecurityMeasureMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class TechnicalSecurityMeasureService extends MongoBaseService {

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;


    public Map<String, List<TechnicalSecurityMeasure>> createTechnicalSecurityMeasure(List<TechnicalSecurityMeasure> techSecurityMeasures) {
        Map<String, List<TechnicalSecurityMeasure>> result = new HashMap<>();
        List<TechnicalSecurityMeasure> existing = new ArrayList<>();
        List<TechnicalSecurityMeasure> newTechnicalMeasures = new ArrayList<>();
        if (techSecurityMeasures.size() != 0) {
            for (TechnicalSecurityMeasure technicalSecurityMeasure : techSecurityMeasures) {

                TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByName(technicalSecurityMeasure.getName());
                if (Optional.ofNullable(exist).isPresent()) {
                    existing.add(exist);

                } else {
                    TechnicalSecurityMeasure newTechnicalMeasure = new TechnicalSecurityMeasure();
                    newTechnicalMeasure.setName(technicalSecurityMeasure.getName());
                    newTechnicalMeasures.add(save(newTechnicalMeasure));
                }
            }

            result.put("existing", existing);
            result.put("new", newTechnicalMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    public List<TechnicalSecurityMeasure> getAllTechnicalSecurityMeasure() {
        List<TechnicalSecurityMeasure> result = technicalSecurityMeasureMongoRepository.findAllTechnicalSecurityMeasures();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("TechnicalSecurityMeasure not exist please create purpose ");
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasure(BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public TechnicalSecurityMeasure updateTechnicalSecurityMeasure(BigInteger id, TechnicalSecurityMeasure techSecurityMeasure) {
        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setName(techSecurityMeasure.getName());
            return save(exist);

        }
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasureByName(String name) {


        if (!StringUtils.isBlank(name)) {
            TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        }
        else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }











}
