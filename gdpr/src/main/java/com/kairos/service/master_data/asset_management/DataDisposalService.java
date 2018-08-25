package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.DataDisposalDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.persistance.repository.master_data.asset_management.data_disposal.DataDisposalMongoRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
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
public class DataDisposalService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalService.class);

    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param dataDisposalDTOS
     * @return return map which contain list of new data disposal and list of existing data disposal if data disposal already exist
     * @description this method create new data Disposal if data disposal not exist with same name ,
     * and if exist then simply add  data disposal to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing data disposal using collation ,used for case insensitive result
     */
    public Map<String, List<DataDisposal>> createDataDisposal(Long countryId, List<DataDisposalDTO> dataDisposalDTOS) {

        Map<String, List<DataDisposal>> result = new HashMap<>();
        Set<String> dataDisposalsNames = new HashSet<>();
        for (DataDisposalDTO dataDisposal : dataDisposalDTOS) {
            dataDisposalsNames.add(dataDisposal.getName());
        }
        List<DataDisposal> existing = findMetaDataByNamesAndCountryId(countryId, dataDisposalsNames, DataDisposal.class);
        dataDisposalsNames = ComparisonUtils.getNameListForMetadata(existing, dataDisposalsNames);
        List<DataDisposal> newDataDisposals = new ArrayList<>();
        if (dataDisposalsNames.size() != 0) {
            for (String name : dataDisposalsNames) {

                DataDisposal newDataDisposal = new DataDisposal(name);
                newDataDisposal.setCountryId(countryId);
                newDataDisposals.add(newDataDisposal);

            }

            newDataDisposals = dataDisposalMongoRepository.saveAll(getNextSequence(newDataDisposals));
        }
        result.put(EXISTING_DATA_LIST, existing);
        result.put(NEW_DATA_LIST, newDataDisposals);
        return result;

    }

    /**
     * @param countryId
     * @return list of DataDisposal
     */
    public List<DataDisposalResponseDTO> getAllDataDisposal(Long countryId) {
        return dataDisposalMongoRepository.findAllDataDisposals(countryId,SuggestedDataStatus.ACCEPTED.value);
    }


    /**
     * @param countryId
     * @param
     * @param id        id of data disposal
     * @return object of data disposal
     * @throws DataNotFoundByIdException if data disposal not found for id
     */
    public DataDisposal getDataDisposalById(Long countryId, BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataDisposalById(Long countryId, BigInteger id) {

        DataDisposal dataDisposal = dataDisposalMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(dataDisposal).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        delete(dataDisposal);
        return true;

    }


    /**
     * @param countryId
     * @param
     * @param id              id of Data Disposal
     * @param dataDisposalDTO
     * @return updated data disposal object
     * @throws DuplicateDataException if data disposal exist with same name then throw exception
     */
    public DataDisposalDTO updateDataDisposal(Long countryId, BigInteger id, DataDisposalDTO dataDisposalDTO) {


        DataDisposal dataDisposal = dataDisposalMongoRepository.findByName(countryId, dataDisposalDTO.getName());
        if (Optional.ofNullable(dataDisposal).isPresent()) {
            if (id.equals(dataDisposal.getId())) {
                return dataDisposalDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataDisposalDTO.getName());
        }
        dataDisposal = dataDisposalMongoRepository.findByid(id);
        if (!Optional.ofNullable(dataDisposal).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Disposal", id);
        }
        dataDisposal.setName(dataDisposalDTO.getName());
        dataDisposalMongoRepository.save(dataDisposal);
        return dataDisposalDTO;

    }


    /**
     * @param countryId
     * @param name
     * @return object of data disposal
     * @throws DataNotExists if data  disposal not exist of requested name
     * @description this method is used for get  data disposal by name
     */
    public DataDisposal getDataDisposalByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            DataDisposal exist = dataDisposalMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }

    /**
     * @description method save data disposal suggested by unit
     * @param countryId
     * @param dataDisposalDTOS
     * @return
     */
    public List<DataDisposal> saveSuggestedDataDisposalFromUnit(Long countryId, List<DataDisposalDTO> dataDisposalDTOS) {

        Set<String> dataDisposalsNames = new HashSet<>();
        for (DataDisposalDTO dataDisposal : dataDisposalDTOS) {
            dataDisposalsNames.add(dataDisposal.getName());
        }
        List<DataDisposal> existing = findMetaDataByNamesAndCountryId(countryId, dataDisposalsNames, DataDisposal.class);
        dataDisposalsNames = ComparisonUtils.getNameListForMetadata(existing, dataDisposalsNames);
        List<DataDisposal> newDataDisposals = new ArrayList<>();
        if (dataDisposalsNames.size() != 0) {
            for (String name : dataDisposalsNames) {

                DataDisposal newDataDisposal = new DataDisposal(name);
                newDataDisposal.setCountryId(countryId);
                newDataDisposal.setSuggestedDataStatus(SuggestedDataStatus.NEW.value);
                newDataDisposals.add(newDataDisposal);
            }

            newDataDisposals = dataDisposalMongoRepository.saveAll(getNextSequence(newDataDisposals));
        }
        return newDataDisposals;
    }
}





