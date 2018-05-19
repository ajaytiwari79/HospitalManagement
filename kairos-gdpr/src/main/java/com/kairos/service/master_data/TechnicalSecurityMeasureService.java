package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.master_data.TechnicalSecurityMeasureMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class TechnicalSecurityMeasureService extends MongoBaseService {

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;


    public TechnicalSecurityMeasure createTechnicalSecurityMeasure(String techSecurityMeasure) {
        if (StringUtils.isEmpty(techSecurityMeasure)) {
            throw new InvalidRequestException("requested techSecurityMeasure name is null");

        }
        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByName(techSecurityMeasure);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for " + techSecurityMeasure);
        } else {
            TechnicalSecurityMeasure newTechnicalSecurityMeasure = new TechnicalSecurityMeasure();
            newTechnicalSecurityMeasure.setName(techSecurityMeasure);
            return save(newTechnicalSecurityMeasure);
        }
    }


    public List<TechnicalSecurityMeasure> getAllTechnicalSecurityMeasure() {
        List<TechnicalSecurityMeasure> result = technicalSecurityMeasureMongoRepository.findAll();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("TechnicalSecurityMeasure not exist please create purpose ");
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasureById(BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasureById(BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            technicalSecurityMeasureMongoRepository.delete(exist);
            return true;

        }
    }


    public TechnicalSecurityMeasure updateTechnicalSecurityMeasure(BigInteger id, String techSecurityMeasure) {
        if (StringUtils.isEmpty(techSecurityMeasure)) {
            throw new InvalidRequestException("requested techSecurityMeasure name is null");

        }

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setName(techSecurityMeasure);
            return save(exist);

        }
    }


}
