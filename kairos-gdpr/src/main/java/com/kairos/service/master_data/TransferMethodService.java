package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.TransferMethod;
import com.kairos.persistance.repository.master_data.TransferMethodMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class TransferMethodService extends MongoBaseService {


    @Inject
    private TransferMethodMongoRepository transferMethodMongoRepository;


    public TransferMethod createTransferMethod(String transferMethod) {

        if (StringUtils.isEmpty(transferMethod))
        {
            throw new InvalidRequestException("requested dataSource  is null or empty");
        }
        TransferMethod exist = transferMethodMongoRepository.findByName(transferMethod);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for name " + transferMethod);
        } else {
            TransferMethod newTransferMethod = new TransferMethod();
            newTransferMethod.setName(transferMethod);
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
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            transferMethodMongoRepository.delete(exist);
            return true;

        }
    }


    public TransferMethod updateTransferMethod(BigInteger id,String transferMethod) {

        if (StringUtils.isEmpty(transferMethod))
        {
            throw new InvalidRequestException("requested dataSource  is null or empty");
        }
        TransferMethod exist = transferMethodMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id "+id);
        } else {
            exist.setName(transferMethod);
            return save(exist);

        }
    }



}
