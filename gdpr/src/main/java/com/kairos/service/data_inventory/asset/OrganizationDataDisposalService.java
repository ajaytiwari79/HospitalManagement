package com.kairos.service.data_inventory.asset;


import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.metadata.DataDisposalDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalMongoRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.DataDisposalService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationDataDisposalService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationDataDisposalService.class);

    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private DataDisposalService dataDisposalService;


    /**
     * @param organizationId
     * @param dataDisposalDTOS
     * @return return map which contain list of new data disposal and list of existing data disposal if data disposal already exist
     * @description this method create new data Disposal if data disposal not exist with same name ,
     * and if exist then simply add  data disposal to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing data disposal using collation ,used for case insensitive result
     */
    public Map<String, List<DataDisposal>> createDataDisposal(Long organizationId, List<DataDisposalDTO> dataDisposalDTOS) {

        Map<String, List<DataDisposal>> result = new HashMap<>();
        Set<String> dataDisposalsNames = new HashSet<>();
        for (DataDisposalDTO dataDisposal : dataDisposalDTOS) {
            dataDisposalsNames.add(dataDisposal.getName());
        }
        List<DataDisposal> existing = findMetaDataByNameAndUnitId(organizationId, dataDisposalsNames, DataDisposal.class);
        dataDisposalsNames = ComparisonUtils.getNameListForMetadata(existing, dataDisposalsNames);
        List<DataDisposal> newDataDisposals = new ArrayList<>();
        if (!dataDisposalsNames.isEmpty()) {
            for (String name : dataDisposalsNames) {
                DataDisposal newDataDisposal = new DataDisposal(name);
                newDataDisposal.setOrganizationId(organizationId);
                newDataDisposals.add(newDataDisposal);
            }

            newDataDisposals = dataDisposalMongoRepository.saveAll(getNextSequence(newDataDisposals));
        }
        result.put(EXISTING_DATA_LIST, existing);
        result.put(NEW_DATA_LIST, newDataDisposals);
        return result;

    }

    /**
     * @param organizationId
     * @return list of DataDisposal
     */
    public List<DataDisposalResponseDTO> getAllDataDisposal(Long organizationId) {
        return dataDisposalMongoRepository.findAllByUnitIdAndSortByCreatedDate(organizationId, new Sort(Sort.Direction.DESC, "createdAt"));
    }


    /**
     * @param organizationId
     * @param id             id of data disposal
     * @return object of data disposal
     * @throws DataNotFoundByIdException if data disposal not found for id
     */
    public DataDisposal getDataDisposalById(Long organizationId, BigInteger id) {

        DataDisposal exist = dataDisposalMongoRepository.findByUnitIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataDisposalById(Long unitId, BigInteger dataDisposalId) {

        List<AssetBasicResponseDTO> assetsLinkedWithDataDisposal = assetMongoRepository.findAllAssetLinkedWithDataDisposal(unitId, dataDisposalId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithDataDisposal)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Data Disposal", new StringBuilder(assetsLinkedWithDataDisposal.stream().map(AssetBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        dataDisposalMongoRepository.safeDelete(dataDisposalId);
        return true;

    }


    /**
     * @param organizationId
     * @param id              id of Data Disposal
     * @param dataDisposalDTO
     * @return updated data disposal object
     * @throws DuplicateDataException if data disposal exist with same name then throw exception
     */
    public DataDisposalDTO updateDataDisposal(Long organizationId, BigInteger id, DataDisposalDTO dataDisposalDTO) {


        DataDisposal dataDisposal = dataDisposalMongoRepository.findByUnitIdAndName(organizationId, dataDisposalDTO.getName());
        if (Optional.ofNullable(dataDisposal).isPresent()) {
            if (id.equals(dataDisposal.getId())) {
                return dataDisposalDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Data Disposal", dataDisposal.getName());
        }
        dataDisposal = dataDisposalMongoRepository.findByid(id);
        if (!Optional.ofNullable(dataDisposal).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Disposal", id);
        }
        dataDisposal.setName(dataDisposalDTO.getName());
        dataDisposalMongoRepository.save(dataDisposal);

        return dataDisposalDTO;

    }

    public Map<String, List<DataDisposal>> saveAndSuggestDataDisposal(Long countryId, Long organizationId, List<DataDisposalDTO> dataDisposalDTOS) {

        Map<String, List<DataDisposal>> result;
        result = createDataDisposal(organizationId, dataDisposalDTOS);
        List<DataDisposal> masterDataDisposalSuggestedByUnit = dataDisposalService.saveSuggestedDataDisposalFromUnit(countryId, dataDisposalDTOS);
        if (!masterDataDisposalSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterDataDisposalSuggestedByUnit);
        }
        return result;

    }


}
