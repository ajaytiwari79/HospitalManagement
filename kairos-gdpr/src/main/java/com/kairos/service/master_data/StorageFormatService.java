package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.master_data.StorageFormat;
import com.kairos.persistance.repository.master_data.StorageFormatMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class StorageFormatService extends MongoBaseService {


@Inject
    private StorageFormatMongoRepository  storageFormatMongoRepository ;



    public StorageFormat createStorageFormat(String storageFormat) {
        if (StringUtils.isEmpty(storageFormat))
        {
            throw new RequestDataNull("requested storageFormat name is null");

        }
        StorageFormat exist = storageFormatMongoRepository.findByName(storageFormat);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for  " + storageFormat);
        } else {
            StorageFormat newStorageFormat = new StorageFormat();
            newStorageFormat.setName(storageFormat);
            return save(newStorageFormat);
        }
    }


    public List<StorageFormat> getAllStorageFormat() {
        List<StorageFormat> result = storageFormatMongoRepository.findAll();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("StorageFormat not exist please create purpose ");
    }



    public StorageFormat getStorageFormatById(BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }



    public Boolean deleteStorageFormatById(BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            storageFormatMongoRepository.delete(exist);
            return true;

        }
    }


    public StorageFormat updateStorageFormat(BigInteger id,String storageFormat) {
        if (StringUtils.isEmpty(storageFormat))
        {
            throw new RequestDataNull("requested storageFormat name is null");

        }
        StorageFormat exist = storageFormatMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(storageFormat);
            return save(exist);

        }
    }






}
