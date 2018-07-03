package com.kairos.service.master_data_management.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.TransferMethodMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class TransferMethodService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

    @Inject
    private TransferMethodMongoRepository transferMethodRepository;

    @Inject
    private ComparisonUtils comparisonUtils;

    public Map<String, List<TransferMethod>> createTransferMethod(Long countryId, Long organizationId, List<TransferMethod> transferMethods) {

        Map<String, List<TransferMethod>> result = new HashMap<>();
        Set<String> transferMethodNames = new HashSet<>();
        if (transferMethods.size() != 0) {
            for (TransferMethod transferMethod : transferMethods) {
                if (!StringUtils.isBlank(transferMethod.getName())) {
                    transferMethodNames.add(transferMethod.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");

            }
            List<TransferMethod> existing = findByNamesList(countryId, organizationId, transferMethodNames, TransferMethod.class);
            transferMethodNames = comparisonUtils.getNameListForMetadata(existing, transferMethodNames);

            List<TransferMethod> newTransferMethods = new ArrayList<>();
            if (transferMethodNames.size() != 0) {
                for (String name : transferMethodNames) {
                    TransferMethod newTransferMethod = new TransferMethod();
                    newTransferMethod.setName(name);
                    newTransferMethod.setCountryId(countryId);
                    newTransferMethod.setOrganizationId(organizationId);
                    newTransferMethods.add(newTransferMethod);
                }

                newTransferMethods = transferMethodRepository.saveAll(save(newTransferMethods));
            }
            result.put("existing", existing);
            result.put("new", newTransferMethods);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<TransferMethod> getAllTransferMethod(Long countryId, Long organizationId) {
        return transferMethodRepository.findAllTransferMethods(countryId, organizationId);
    }

    public TransferMethod getTransferMethod(Long countryId, Long organizationId, BigInteger id) {

        TransferMethod exist = transferMethodRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;
        }
    }


    public Boolean deleteTransferMethod(Long countryId, Long organizationId, BigInteger id) {

        TransferMethod exist = transferMethodRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            delete(exist);
            return true;
        }
    }


    public TransferMethod updateTransferMethod(Long countryIdId, Long organizationId, BigInteger id, TransferMethod transferMethod) {

        TransferMethod exist = transferMethodRepository.findByName(countryIdId, organizationId, transferMethod.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + transferMethod.getName());
        } else {
            exist = transferMethodRepository.findByid(id);
            exist.setName(transferMethod.getName());
            return transferMethodRepository.save(save(exist));

        }
    }


    public TransferMethod getTransferMethodByName(Long countryId, Long organizationId, String name) {
        if (!StringUtils.isBlank(name)) {
            TransferMethod exist = transferMethodRepository.findByName(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}

    
    
    

