package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.asset_management.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.master_data.asset_management.TechnicalSecurityMeasureMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
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


    @Inject
    private ComparisonUtils comparisonUtils;


    public Map<String, List<TechnicalSecurityMeasure>> createTechnicalSecurityMeasure(Long countryId, Long organizationId, List<TechnicalSecurityMeasure> techSecurityMeasures) {

        Map<String, List<TechnicalSecurityMeasure>> result = new HashMap<>();
        Set<String> techSecurityMeasureNames = new HashSet<>();
        if (techSecurityMeasures.size() != 0) {
            for (TechnicalSecurityMeasure technicalSecurityMeasure : techSecurityMeasures) {
                if (!StringUtils.isBlank(technicalSecurityMeasure.getName())) {
                    techSecurityMeasureNames.add(technicalSecurityMeasure.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<TechnicalSecurityMeasure> existing =  findByNamesList(countryId,organizationId,techSecurityMeasureNames,TechnicalSecurityMeasure.class);
            techSecurityMeasureNames = comparisonUtils.getNameListForMetadata(existing, techSecurityMeasureNames);

            List<TechnicalSecurityMeasure> newTechnicalMeasures = new ArrayList<>();
            if (techSecurityMeasureNames.size() != 0) {
                for (String name : techSecurityMeasureNames) {
                    TechnicalSecurityMeasure newTechnicalSecurityMeasure = new TechnicalSecurityMeasure();
                    newTechnicalSecurityMeasure.setName(name);
                    newTechnicalSecurityMeasure.setCountryId(countryId);
                    newTechnicalSecurityMeasure.setOrganizationId(organizationId);
                    newTechnicalMeasures.add(newTechnicalSecurityMeasure);

                }
                newTechnicalMeasures = technicalSecurityMeasureMongoRepository.saveAll(sequenceGenerator(newTechnicalMeasures));
            }
            result.put("existing", existing);
            result.put("new", newTechnicalMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    public List<TechnicalSecurityMeasure> getAllTechnicalSecurityMeasure(Long countryId, Long organizationId) {
        return technicalSecurityMeasureMongoRepository.findAllTechnicalSecurityMeasures(countryId, organizationId);
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasure(Long countryId, Long organizationId, BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(Long countryId, Long organizationId, BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            delete(exist);
            return true;

        }
    }


    public TechnicalSecurityMeasure updateTechnicalSecurityMeasure(Long countryId, Long organizationId, BigInteger id, TechnicalSecurityMeasure techSecurityMeasure) {
        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByNameAndCountryId(countryId, organizationId, techSecurityMeasure.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + techSecurityMeasure.getName());
        } else {
            exist = technicalSecurityMeasureMongoRepository.findByid(id);
            exist.setName(techSecurityMeasure.getName());
            return technicalSecurityMeasureMongoRepository.save(sequenceGenerator(exist));

        }
    }


    public TechnicalSecurityMeasure getTechnicalSecurityMeasureByName(Long countryId, Long organizationId, String name) {

        if (!StringUtils.isBlank(name)) {
            TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByNameAndCountryId(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
