package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.persistance.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureMongoRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureReponseDTO;
import com.kairos.service.common.MongoBaseService;
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
    private ComparisonUtils comparisonUtils;

    /**
     * @description this method create new TechnicalSecurityMeasure if TechnicalSecurityMeasure not exist with same name ,
     * and if exist then simply add  TechnicalSecurityMeasure to existing list and return list ;
     * findByNamesList()  return list of existing TechnicalSecurityMeasure using collation ,used for case insensitive result
     * @param countryId
     * @param organizationId
     * @param techSecurityMeasures
     * @return return map which contain list of new TechnicalSecurityMeasure and list of existing TechnicalSecurityMeasure if TechnicalSecurityMeasure already exist
     *
     */
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
                newTechnicalMeasures = technicalSecurityMeasureMongoRepository.saveAll(getNextSequence(newTechnicalMeasures));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newTechnicalMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     *
     * @param countryId
     * @param organizationId
     * @return list of TechnicalSecurityMeasure
     */
    public List<TechnicalSecurityMeasure> getAllTechnicalSecurityMeasure(Long countryId, Long organizationId) {
        return technicalSecurityMeasureMongoRepository.findAllTechnicalSecurityMeasures(countryId, organizationId);
    }




    /**
     * @throws DataNotFoundByIdException throw exception if TechnicalSecurityMeasure not exist for given id
     * @param countryId
     * @param organizationId
     * @param id id of TechnicalSecurityMeasure
     * @return object of TechnicalSecurityMeasure
     */
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

    /**
     * @throws  DuplicateDataException throw exception if TechnicalSecurityMeasure data not exist for given id
     * @param countryId
     * @param organizationId
     * @param id id of TechnicalSecurityMeasure
     * @param techSecurityMeasure
     * @return TechnicalSecurityMeasure updated object
     */
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
            return technicalSecurityMeasureMongoRepository.save(getNextSequence(exist));

        }
    }

    /**
     * @throws DataNotExists throw exception if TechnicalSecurityMeasure exist for given name
     * @param countryId
     * @param organizationId
     * @param name name of TechnicalSecurityMeasure
     * @return TechnicalSecurityMeasure object fetch on basis of  name
     */
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


    public List<TechnicalSecurityMeasureReponseDTO> getAllNotInheritedTechnicalSecurityMeasureFromParentOrgAndUnitSecurityMeasure(Long countryId, Long parentOrganizationId, Long unitId){

        return technicalSecurityMeasureMongoRepository.getAllNotInheritedTechnicalSecurityMeasureFromParentOrgAndUnitSecurityMeasure(countryId,parentOrganizationId,unitId);
    }


}
