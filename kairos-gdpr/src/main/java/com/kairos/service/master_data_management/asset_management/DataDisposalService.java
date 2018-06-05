package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.DataDisposal;
import com.kairos.persistance.repository.master_data_management.asset_management.DataDisposalMongoRepository;
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
public class DataDisposalService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalService.class);

    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;


    public Map<String, List<DataDisposal>> createDataDisposal(Long countryId, List<DataDisposal> dataDisposals) {
        Map<String, List<DataDisposal>> result = new HashMap<>();
        List<DataDisposal> existing = new ArrayList<>();
        List<DataDisposal> newDataDisposals = new ArrayList<>();
        Set<String> names = new HashSet<>();
        if (dataDisposals.size() != 0) {
            for (DataDisposal dataDisposal : dataDisposals) {
                if (!StringUtils.isBlank(dataDisposal.getName())) {
                    names.add(dataDisposal.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            existing = dataDisposalMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));
            if (names.size() != 0) {
                for (String name : names) {

                    DataDisposal newDataDisposal = new DataDisposal();
                    newDataDisposal.setName(name);
                    newDataDisposal.setCountryId(countryId);
                    newDataDisposals.add(newDataDisposal);

                }

                newDataDisposals = save(newDataDisposals);
            }
            result.put("existing", existing);
            result.put("new", newDataDisposals);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<DataDisposal> getAllDataDisposal() {
        return dataDisposalMongoRepository.findAllDataDisposals(UserContext.getCountryId());
    }


    public DataDisposal getDataDisposalById(Long countryId, BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataDisposalById(BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public DataDisposal updateDataDisposal(BigInteger id, DataDisposal dataDisposal) {


        DataDisposal exist = dataDisposalMongoRepository.findByName(UserContext.getCountryId(),dataDisposal.getName());
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist=dataDisposalMongoRepository.findByid(id);
            exist.setName(dataDisposal.getName());
            return save(exist);

        }
    }


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


}





