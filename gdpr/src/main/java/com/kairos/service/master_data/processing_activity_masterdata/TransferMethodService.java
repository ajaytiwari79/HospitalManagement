package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.metadata.TransferMethodDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.TransferMethod;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class TransferMethodService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMethodService.class);

    @Inject
    private TransferMethodMongoRepository transferMethodRepository;

    @Inject
    private ComparisonUtils comparisonUtils;


    /**
     * @description this method create new TransferMethod if TransferMethod not exist with same name ,
     * and if exist then simply add  TransferMethod to existing list and return list ;
     * findByNamesList()  return list of existing TransferMethod using collation ,used for case insensitive result
     * @param countryId
     * @param organizationId
     * @param transferMethods
     * @return return map which contain list of new TransferMethod and list of existing TransferMethod if TransferMethod already exist
     *
     */
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
                    TransferMethod newTransferMethod = new TransferMethod(name,countryId);
                    newTransferMethod.setOrganizationId(organizationId);
                    newTransferMethods.add(newTransferMethod);
                }

                newTransferMethods = transferMethodRepository.saveAll(getNextSequence(newTransferMethods));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newTransferMethods);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    /**
     *
     * @param countryId
     * @param organizationId
     * @return list of TransferMethod
     */
    public List<TransferMethod> getAllTransferMethod(Long countryId, Long organizationId) {
        return transferMethodRepository.findAllTransferMethods(countryId, organizationId);
    }

    /**
     * @throws DataNotFoundByIdException throw exception if TransferMethod not found for given id
     * @param countryId
     * @param organizationId
     * @param id id of TransferMethod
     * @return TransferMethod object fetch by given id
     */
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

    /***
     * @throws  DuplicateDataException throw exception if TransferMethod data not exist for given id
     * @param countryId
     * @param organizationId
     * @param id id of TransferMethod
     * @param transferMethod
     * @return TransferMethod updated object
     */
    public TransferMethod updateTransferMethod(Long countryId, Long organizationId, BigInteger id, TransferMethod transferMethod) {

        TransferMethod exist = transferMethodRepository.findByName(countryId, organizationId, transferMethod.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + transferMethod.getName());
        } else {
            exist = transferMethodRepository.findByid(id);
            exist.setName(transferMethod.getName());
            return transferMethodRepository.save(getNextSequence(exist));

        }
    }

    /**
     * @throws DataNotExists throw exception if TransferMethod not exist for given name
     * @param countryId
     * @param organizationId
     * @param name name of TransferMethod
     * @return TransferMethod object fetch on basis of  name
     */
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


    public List<BigInteger> createTransferMethodForOrganizationOnInheritingFromParentOrganization(Long countryId, Long organizationId, List<TransferMethodDTO> transferMethodDTOS) {

        List<TransferMethod> newInheritTransferMethodFromParentOrg = new ArrayList<>();
        List<BigInteger> transferMethodIds = new ArrayList<>();
        for (TransferMethodDTO   transferMethodDTO: transferMethodDTOS) {
            if (!transferMethodDTO.getOrganizationId().equals(organizationId)) {
                TransferMethod transferMethod = new TransferMethod(transferMethodDTO.getName(), countryId);
                transferMethod.setOrganizationId(organizationId);
                newInheritTransferMethodFromParentOrg.add(transferMethod);
            } else {
                transferMethodIds.add(transferMethodDTO.getId());
            }
        }
        newInheritTransferMethodFromParentOrg = transferMethodRepository.saveAll(getNextSequence(newInheritTransferMethodFromParentOrg));
        newInheritTransferMethodFromParentOrg.forEach(dataSource -> {
            transferMethodIds.add(dataSource.getId());
        });
        return transferMethodIds;
    }


    public List<TransferMethodResponseDTO> getAllNotInheritedTransferMethodFromParentOrgAndUnitTransferMethod(Long countryId,Long parentOrganizationId,Long unitId)
    {
        return transferMethodRepository.getAllNotInheritedTransferMethodFromParentOrgAndUnitTransferMethod(countryId,parentOrganizationId,unitId);

    }



}

    
    
    

