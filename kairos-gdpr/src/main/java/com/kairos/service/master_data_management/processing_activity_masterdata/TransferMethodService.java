package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.TransferMethodMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class TransferMethodService extends MongoBaseService {


    @Inject
    private TransferMethodMongoRepository transferMethodMongoRepository;


    public Map<String, List<TransferMethod>> createTransferMethod(List<TransferMethod> transferMethods) {
        Map<String, List<TransferMethod>> result = new HashMap<>();
        List<TransferMethod> existing= new ArrayList<>();
        List<TransferMethod> newTransferMethods= new ArrayList<>();
        if (transferMethods.size() != 0) {
            for (TransferMethod transferMethod : transferMethods) {

                TransferMethod exist = transferMethodMongoRepository.findByName(transferMethod.getName());
                if (Optional.ofNullable(exist).isPresent()) {
                    existing.add(exist);

                } else {
                    TransferMethod newTransferMethod = new TransferMethod();
                    newTransferMethod.setName(transferMethod.getName());
                    newTransferMethods.add(save(newTransferMethod));
                }
            }

            result.put("existing", existing);
            result.put("new", newTransferMethods);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<TransferMethod> getAllTransferMethod() {
        List<TransferMethod> result = transferMethodMongoRepository.findAllTransferMethods();
        if (result.size() != 0) {
            return result;

        } else
            throw new DataNotExists("TransferMethod not exist please create purpose ");
    }


    public TransferMethod getTransferMethod(BigInteger id) {

        TransferMethod exist = transferMethodMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteTransferMethod(BigInteger id) {

        TransferMethod exist = transferMethodMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public TransferMethod updateTransferMethod(BigInteger id, TransferMethod transferMethod) {


        TransferMethod exist = transferMethodMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(transferMethod.getName());

            return save(exist);

        }
    }





    public TransferMethod getTransferMethodByName(String name) {


        if (!StringUtils.isBlank(name)) {
            TransferMethod exist = transferMethodMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        }
        else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }




}

    
    
    

