package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.DataDisposal;
import com.kairos.persistance.repository.master_data_management.asset_management.DataDisposalMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class DataDisposalService extends MongoBaseService {


    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;


    public Map<String, List<DataDisposal>> createDataDisposal(List<DataDisposal> dataDisposals) {
        Map<String, List<DataDisposal>> result = new HashMap<>();
        List<DataDisposal> existTechnicalMeasure = new ArrayList<>();
        List<DataDisposal> newDataDisposals = new ArrayList<>();
        if (dataDisposals.size() != 0) {
            for (DataDisposal dataDisposal : dataDisposals) {

                if (!StringUtils.isBlank(dataDisposal.getName())) {
                    DataDisposal exist = dataDisposalMongoRepository.findByName(dataDisposal.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existTechnicalMeasure.add(exist);

                    } else {
                        DataDisposal newDataDisposal = new DataDisposal();
                        newDataDisposal.setName(dataDisposal.getName());
                        newDataDisposals.add(save(newDataDisposal));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existTechnicalMeasure);
            result.put("new", newDataDisposals);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<DataDisposal> getAllDataDisposal() {
        List<DataDisposal> result = dataDisposalMongoRepository.findAllDataDisposals();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("DataDisposal not exist please create purpose ");
    }


    public DataDisposal getDataDisposalById(BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataDisposalById(BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public DataDisposal updateDataDisposal(BigInteger id, DataDisposal dataDisposal) {


        DataDisposal exist = dataDisposalMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(dataDisposal.getName());
            return save(exist);

        }
    }


    public DataDisposal getDataDisposalByName(String name) {


        if (!StringUtils.isBlank(name)) {
            DataDisposal exist = dataDisposalMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}





