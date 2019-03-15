package com.kairos.service.data_inventory.asset;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrganizationDataDisposalService {


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
     * @param unitId
     * @param dataDisposalDTOS
     * @return return map which contain list of new data disposal and list of existing data disposal if data disposal already exist
     * @description this method create new data Disposal if data disposal not exist with same name ,
     * and if exist then simply add  data disposal to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing data disposal using collation ,used for case insensitive result
     */
    public List<DataDisposalDTO> createDataDisposal(Long unitId, List<DataDisposalDTO> dataDisposalDTOS) {
        Set<String> existingDataDisposalNames = dataDisposalRepository.findNameByOrganizationIdAndDeleted(unitId);
        Set<String> dataDisposalsNames = ComparisonUtils.getNewMetaDataNames(dataDisposalDTOS,existingDataDisposalNames );
        List<DataDisposal> dataDisposals = new ArrayList<>();
        if (!dataDisposalsNames.isEmpty()) {
            for (String name : dataDisposalsNames) {
                DataDisposal newDataDisposal = new DataDisposal(name);
                newDataDisposal.setOrganizationId(unitId);
                dataDisposals.add(newDataDisposal);
            }

          dataDisposalRepository.saveAll(dataDisposals);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(dataDisposals, DataDisposalDTO.class);

    }

    /**
     * @param unitId
     * @return list of DataDisposal
     */
    public List<DataDisposalResponseDTO> getAllDataDisposal(Long unitId) {
        return dataDisposalRepository.findAllByUnitIdAndSortByCreatedDate(unitId);
    }


    /**
     * @param unitId
     * @param id             id of data disposal
     * @return object of data disposal
     * @throws DataNotFoundByIdException if data disposal not found for id
     */
    public DataDisposal getDataDisposalById(Long unitId, Long id) {

        DataDisposal exist = dataDisposalRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
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
        } else {
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /**
     * @param unitId
     * @param id              id of Data Disposal
     * @param dataDisposalDTO
     * @return updated data disposal object
     * @throws DuplicateDataException if data disposal exist with same name then throw exception
     */
    public DataDisposalDTO updateDataDisposal(Long unitId, Long id, DataDisposalDTO dataDisposalDTO) {


        DataDisposal dataDisposal = dataDisposalRepository.findByOrganizationIdAndDeletedAndName(unitId, dataDisposalDTO.getName());
        if (Optional.ofNullable(dataDisposal).isPresent()) {
            if (id.equals(dataDisposal.getId())) {
                return dataDisposalDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataDisposalDTO.getName());
        }
        Integer resultCount = dataDisposalRepository.updateMetadataName(dataDisposalDTO.getName(), id, unitId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Disposal", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, dataDisposalDTO.getName());
        }
        return dataDisposalDTO;

    }

    public List<DataDisposalDTO> saveAndSuggestDataDisposal(Long countryId, Long unitId, List<DataDisposalDTO> dataDisposalDTOS) {
        dataDisposalService.saveSuggestedDataDisposalFromUnit(countryId, dataDisposalDTOS);
        return createDataDisposal(unitId, dataDisposalDTOS);
    }


}
