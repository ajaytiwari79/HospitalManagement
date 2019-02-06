package com.kairos.service.data_inventory.asset;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.metadata.DataDisposalDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.DataDisposalService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationDataDisposalService{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationDataDisposalService.class);

    @Inject
    private DataDisposalRepository dataDisposalRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetRepository assetRepository;

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
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<DataDisposal>> result = new HashMap<>();
        Set<String> dataDisposalsNames = new HashSet<>();
        for (DataDisposalDTO dataDisposal : dataDisposalDTOS) {
            dataDisposalsNames.add(dataDisposal.getName());
        }

        List<String> nameInLowerCase = dataDisposalsNames.stream().map(String::toLowerCase)
                .collect(Collectors.toList());
        //TODO still need to update we can return name of list from here and can apply removeAll on list
        List<DataDisposal> existing = dataDisposalRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
        dataDisposalsNames = ComparisonUtils.getNameListForMetadata(existing, dataDisposalsNames);
        List<DataDisposal> newDataDisposals = new ArrayList<>();
        if (!dataDisposalsNames.isEmpty()) {
            for (String name : dataDisposalsNames) {
                DataDisposal newDataDisposal = new DataDisposal(name);
                newDataDisposal.setOrganizationId(organizationId);
                newDataDisposals.add(newDataDisposal);
            }

            newDataDisposals = dataDisposalRepository.saveAll(newDataDisposals);
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
        return dataDisposalRepository.findAllByUnitIdAndSortByCreatedDate(organizationId);
    }


    /**
     * @param organizationId
     * @param id             id of data disposal
     * @return object of data disposal
     * @throws DataNotFoundByIdException if data disposal not found for id
     */
    public DataDisposal getDataDisposalById(Long organizationId, Long id) {

        DataDisposal exist = dataDisposalRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteDataDisposalById(Long unitId, Long dataDisposalId) {
        List<String> assetNames = assetRepository.findAllAssetLinkedWithDataDisposal(unitId, dataDisposalId);
        if (CollectionUtils.isNotEmpty(assetNames)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Data Disposal", StringUtils.join(assetNames, ','));
        }
        Integer resultCount = dataDisposalRepository.deleteByIdAndOrganizationId(dataDisposalId, unitId);
        if (resultCount > 0) {
            LOGGER.info("Data Disposal deleted successfully for id :: {}", dataDisposalId);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /**
     * @param organizationId
     * @param id              id of Data Disposal
     * @param dataDisposalDTO
     * @return updated data disposal object
     * @throws DuplicateDataException if data disposal exist with same name then throw exception
     */
    public DataDisposalDTO updateDataDisposal(Long organizationId, Long id, DataDisposalDTO dataDisposalDTO) {

        //TODO What actually this code is doing?
        DataDisposal dataDisposal = dataDisposalRepository.findByOrganizationIdAndDeletedAndName(organizationId,  dataDisposalDTO.getName());
        if (Optional.ofNullable(dataDisposal).isPresent()) {
            if (id.equals(dataDisposal.getId())) {
                return dataDisposalDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataDisposalDTO.getName());
        }
        Integer resultCount =  dataDisposalRepository.updateMetadataName(dataDisposalDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Disposal", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, dataDisposalDTO.getName());
        }
        return dataDisposalDTO;

    }

    public Map<String, List<DataDisposal>> saveAndSuggestDataDisposal(Long countryId, Long organizationId, List<DataDisposalDTO> dataDisposalDTOS) {

        Map<String, List<DataDisposal>> result = createDataDisposal(organizationId, dataDisposalDTOS);
        List<DataDisposal> masterDataDisposalSuggestedByUnit = dataDisposalService.saveSuggestedDataDisposalFromUnit(countryId, dataDisposalDTOS);
        if (!masterDataDisposalSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterDataDisposalSuggestedByUnit);
        }
        return result;

    }


}
