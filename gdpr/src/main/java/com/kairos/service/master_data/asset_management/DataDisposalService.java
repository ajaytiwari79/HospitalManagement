package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.persistance.repository.master_data.asset_management.data_disposal.DataDisposalMongoRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
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
public class DataDisposalService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalService.class);

    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;



    /**
     * @description this method create new data Disposal if data disposal not exist with same name ,
     * and if exist then simply add  data disposal to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing data disposal using collation ,used for case insensitive result
     * @param countryId
     * @param dataDisposals
     * @return return map which contain list of new data disposal and list of existing data disposal if data disposal already exist
     *
     */
    public Map<String, List<DataDisposal>> createDataDisposal(Long countryId, List<DataDisposal> dataDisposals) {

        Map<String, List<DataDisposal>> result = new HashMap<>();
        Set<String> dataDisposalsNames = new HashSet<>();
        if (dataDisposals.size() != 0) {
            for (DataDisposal dataDisposal : dataDisposals) {
                if (!StringUtils.isBlank(dataDisposal.getName())) {
                    dataDisposalsNames.add(dataDisposal.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            List<DataDisposal> existing =  findByNamesAndCountryId(countryId,dataDisposalsNames,DataDisposal.class);
            dataDisposalsNames = ComparisonUtils.getNameListForMetadata(existing, dataDisposalsNames);
            List<DataDisposal> newDataDisposals = new ArrayList<>();
            if (dataDisposalsNames.size() != 0) {
                for (String name : dataDisposalsNames) {

                    DataDisposal newDataDisposal = new DataDisposal(name);
                    newDataDisposal.setCountryId(countryId);
                    newDataDisposals.add(newDataDisposal);

                }

                newDataDisposals =dataDisposalMongoRepository.saveAll(getNextSequence(newDataDisposals));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newDataDisposals);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     *
     * @param countryId
     * @return list of DataDisposal
     */
    public List<DataDisposal> getAllDataDisposal(Long countryId) {
        return dataDisposalMongoRepository.findAllDataDisposals(countryId);
    }


    /**
     * @throws DataNotFoundByIdException if data disposal not found for id
     * @param countryId
     * @param 
     * @param id id of data disposal
     * @return object of data disposal
     */
    public DataDisposal getDataDisposalById(Long countryId,  BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataDisposalById(Long countryId,  BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;

        }
    }


    /**
     * @throws DuplicateDataException if data disposal exist with same name then throw exception
     * @param countryId
     * @param 
     * @param id id of Data Disposal
     * @param dataDisposal
     * @return updated data disposal object
     */
    public DataDisposal updateDataDisposal(Long countryId,  BigInteger id, DataDisposal dataDisposal) {


        DataDisposal exist = dataDisposalMongoRepository.findByName(countryId, dataDisposal.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + dataDisposal.getName());
        } else {
            exist = dataDisposalMongoRepository.findByid(id);
            exist.setName(dataDisposal.getName());
            return dataDisposalMongoRepository.save(getNextSequence(exist));

        }
    }


    /**
     * @param countryId
     * @param name
     * @throws DataNotExists if data  disposal not exist of requested name
     * @description this method is used for get  data disposal by name
     * @return object of data disposal
     */
    public DataDisposal getDataDisposalByName(Long countryId,  String name) {


        if (!StringUtils.isBlank(name)) {
            DataDisposal exist = dataDisposalMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }




    public List<DataDisposalResponseDTO> getAllNotInheritedDataDisposalFromParentOrgAndUnitDataDisposal(Long countryId,Long parentOrganizationId,Long unitId){

        return dataDisposalMongoRepository.getAllNotInheritedDataDisposalFromParentOrgAndUnitDataDisposal(countryId,parentOrganizationId,unitId);
    }





}





