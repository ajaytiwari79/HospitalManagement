package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.persistance.model.master_data.TransferMethod;
import com.kairos.persistance.repository.master_data.TransferMethodMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class TransferMethodService extends MongoBaseService {


    @Inject
    private TransferMethodMongoRepository transferMethodMongoRepository;


    public TransferMethod createTransferMethod(TransferMethod transferMethod) {
        String name = transferMethod.getName();
        TransferMethod exist = transferMethodMongoRepository.findByName(name);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for name " + name);
        } else {
            TransferMethod newTransferMethod = new TransferMethod();
            newTransferMethod.setName(name);
            return save(newTransferMethod);

        }
    }


    public List<TransferMethod> getAllTransferMethod() {
        List<TransferMethod> result = transferMethodMongoRepository.findAll();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("processing purpose not exist please create purpose ");
    }



    public TransferMethod getTransferMethodById(BigInteger id) {

        TransferMethod exist = transferMethodMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }



    public Boolean deleteTransferMethodById(BigInteger id) {

        TransferMethod exist = transferMethodMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            transferMethodMongoRepository.delete(exist);
            return true;

        }
    }


    public TransferMethod updateTransferMethod(BigInteger id,TransferMethod transferMethod) {
        TransferMethod exist = transferMethodMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(transferMethod.getName());
            return save(exist);

        }
    }



}
