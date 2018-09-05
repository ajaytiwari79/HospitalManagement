package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureMongoRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class TechnicalSecurityMeasureService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityMeasureService.class);

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param technicalSecurityMeasureDTOS
     * @return return map which contain list of new TechnicalSecurityMeasure and list of existing TechnicalSecurityMeasure if TechnicalSecurityMeasure already exist
     * @description this method create new TechnicalSecurityMeasure if TechnicalSecurityMeasure not exist with same name ,
     * and if exist then simply add  TechnicalSecurityMeasure to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TechnicalSecurityMeasure using collation ,used for case insensitive result
     */
    public Map<String, List<TechnicalSecurityMeasure>> createTechnicalSecurityMeasure(Long countryId, List<TechnicalSecurityMeasureDTO> technicalSecurityMeasureDTOS) {

        Map<String, List<TechnicalSecurityMeasure>> result = new HashMap<>();
        Set<String> techSecurityMeasureNames = new HashSet<>();
        if (!technicalSecurityMeasureDTOS.isEmpty()) {
            for (TechnicalSecurityMeasureDTO technicalSecurityMeasure : technicalSecurityMeasureDTOS) {
                techSecurityMeasureNames.add(technicalSecurityMeasure.getName());
            }
            List<TechnicalSecurityMeasure> existing = findMetaDataByNamesAndCountryId(countryId, techSecurityMeasureNames, TechnicalSecurityMeasure.class);
            techSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(existing, techSecurityMeasureNames);

            List<TechnicalSecurityMeasure> newTechnicalMeasures = new ArrayList<>();
            if (!techSecurityMeasureNames.isEmpty()) {
                for (String name : techSecurityMeasureNames) {
                    TechnicalSecurityMeasure newTechnicalSecurityMeasure = new TechnicalSecurityMeasure(name);
                    newTechnicalSecurityMeasure.setCountryId(countryId);
                    newTechnicalMeasures.add(newTechnicalSecurityMeasure);

                }
                newTechnicalMeasures = technicalSecurityMeasureMongoRepository.saveAll(getNextSequence(newTechnicalMeasures));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newTechnicalMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param countryId
     * @param
     * @return list of TechnicalSecurityMeasure
     */
    public List<TechnicalSecurityMeasureResponseDTO> getAllTechnicalSecurityMeasure(Long countryId) {
        return technicalSecurityMeasureMongoRepository.findAllTechnicalSecurityMeasures(countryId,SuggestedDataStatus.ACCEPTED.value);
    }


    /**
     * @param countryId
     * @param
     * @param id        id of TechnicalSecurityMeasure
     * @return object of TechnicalSecurityMeasure
     * @throws DataNotFoundByIdException throw exception if TechnicalSecurityMeasure not exist for given id
     */
    public TechnicalSecurityMeasure getTechnicalSecurityMeasure(Long countryId, BigInteger id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(Long countryId, BigInteger id) {

        TechnicalSecurityMeasure technicalSecurityMeasure = technicalSecurityMeasureMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(technicalSecurityMeasure).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        }
        delete(technicalSecurityMeasure);
        return true;

    }

    /**
     * @param countryId
     * @param
     * @param id                          id of TechnicalSecurityMeasure
     * @param technicalSecurityMeasureDTO
     * @return TechnicalSecurityMeasure updated object
     * @throws DuplicateDataException throw exception if TechnicalSecurityMeasure data not exist for given id
     */
    public TechnicalSecurityMeasureDTO updateTechnicalSecurityMeasure(Long countryId, BigInteger id, TechnicalSecurityMeasureDTO technicalSecurityMeasureDTO) {

        TechnicalSecurityMeasure technicalSecurityMeasure = technicalSecurityMeasureMongoRepository.findByNameAndCountryId(countryId, technicalSecurityMeasureDTO.getName());
        if (Optional.ofNullable(technicalSecurityMeasure).isPresent()) {
            if (id.equals(technicalSecurityMeasure.getId())) {
                return technicalSecurityMeasureDTO;
            }
            throw new DuplicateDataException("data  exist for  " + technicalSecurityMeasureDTO.getName());
        }
        technicalSecurityMeasure = technicalSecurityMeasureMongoRepository.findByid(id);
        if (!Optional.ofNullable(technicalSecurityMeasure).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Technical Security Measure", id);
        }
        technicalSecurityMeasure.setName(technicalSecurityMeasureDTO.getName());
        technicalSecurityMeasureMongoRepository.save(technicalSecurityMeasure);
        return technicalSecurityMeasureDTO;


    }

    /**
     * @param countryId
     * @param
     * @param name      name of TechnicalSecurityMeasure
     * @return TechnicalSecurityMeasure object fetch on basis of  name
     * @throws DataNotExists throw exception if TechnicalSecurityMeasure exist for given name
     */
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



    /**
     * @description method save technical security measure  suggested by unit
     * @param countryId
     * @param TechnicalSecurityMeasureDTOS
     * @return
     */
    public List<TechnicalSecurityMeasure> saveSuggestedTechnicalSecurityMeasuresFromUnit(Long countryId, List<TechnicalSecurityMeasureDTO> TechnicalSecurityMeasureDTOS) {

        Set<String> technicalSecurityMeasureNameList = new HashSet<>();
        for (TechnicalSecurityMeasureDTO TechnicalSecurityMeasure : TechnicalSecurityMeasureDTOS) {
            technicalSecurityMeasureNameList.add(TechnicalSecurityMeasure.getName());
        }
        List<TechnicalSecurityMeasure> existingTechnicalSecurityMeasures = findMetaDataByNamesAndCountryId(countryId, technicalSecurityMeasureNameList, TechnicalSecurityMeasure.class);
        technicalSecurityMeasureNameList = ComparisonUtils.getNameListForMetadata(existingTechnicalSecurityMeasures, technicalSecurityMeasureNameList);
        List<TechnicalSecurityMeasure> TechnicalSecurityMeasureList = new ArrayList<>();
        if (technicalSecurityMeasureNameList.size() != 0) {
            for (String name : technicalSecurityMeasureNameList) {

                TechnicalSecurityMeasure TechnicalSecurityMeasure = new TechnicalSecurityMeasure(name);
                TechnicalSecurityMeasure.setCountryId(countryId);
                TechnicalSecurityMeasure.setSuggestedDataStatus(SuggestedDataStatus.NEW.value);
                TechnicalSecurityMeasureList.add(TechnicalSecurityMeasure);
            }

            TechnicalSecurityMeasureList = technicalSecurityMeasureMongoRepository.saveAll(getNextSequence(TechnicalSecurityMeasureList));
        }
        return TechnicalSecurityMeasureList;
    }

}
