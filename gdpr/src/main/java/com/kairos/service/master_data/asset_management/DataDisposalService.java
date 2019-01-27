package com.kairos.service.master_data.asset_management;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.DataDisposalDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class DataDisposalService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private DataDisposalRepository dataDisposalRepository;


    /**
     * @param countryId
     * @param dataDisposalDTOS
     * @return return map which contain list of new data disposal and list of existing data disposal if data disposal already exist
     * @description this method create new data Disposal if data disposal not exist with same name ,
     * and if exist then simply add  data disposal to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing data disposal using collation ,used for case insensitive result
     */
    public Map<String, List<DataDisposalMD>> createDataDisposal(Long countryId, List<DataDisposalDTO> dataDisposalDTOS, boolean isSuggestion) {

        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<DataDisposalMD>> result = new HashMap<>();
        Set<String> dataDisposalsNames = new HashSet<>();
        for (DataDisposalDTO dataDisposal : dataDisposalDTOS) {
            dataDisposalsNames.add(dataDisposal.getName());
        }

        List<String> nameInLowerCase = dataDisposalsNames.stream().map(String::toLowerCase)
                .collect(Collectors.toList());

        //TODO still need to update we can return name of list from here and can apply removeAll on list
        List<DataDisposalMD> existing = dataDisposalRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
        dataDisposalsNames = ComparisonUtils.getNameListForMetadata(existing, dataDisposalsNames);
        List<DataDisposalMD> newDataDisposals = new ArrayList<>();
        if (!dataDisposalsNames.isEmpty()) {
            for (String name : dataDisposalsNames) {
                DataDisposalMD newDataDisposal = new DataDisposalMD(name, countryId);
                if(isSuggestion){
                    newDataDisposal.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    newDataDisposal.setSuggestedDate(LocalDate.now());
                }else {
                    newDataDisposal.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                newDataDisposals.add(newDataDisposal);
            }
            newDataDisposals = dataDisposalRepository.saveAll(newDataDisposals);
        }
        result.put(EXISTING_DATA_LIST, existing);
        result.put(NEW_DATA_LIST, newDataDisposals);
        return result;

    }

    /**
     * @param countryId
     * @return list of DataDisposal
     */
    public List<DataDisposalResponseDTO> getAllDataDisposal(Long countryId) {
        return dataDisposalRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id        id of data disposal
     * @return object of data disposal
     * @throws DataNotFoundByIdException if data disposal not found for id
     */
    public DataDisposalMD getDataDisposalById(Long countryId, Long id) {
        DataDisposalMD exist = dataDisposalRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }

    public Boolean deleteDataDisposalById(Long countryId, Long id) {
        Integer resultCount = dataDisposalRepository.deleteByIdAndCountryId(id, countryId);
       if (resultCount > 0) {
           LOGGER.info("Data Disposal deleted successfully for id :: {}", id);
       }else{
           throw new DataNotFoundByIdException("No data found");
       }
        return true;
    }


    /**
     * @param countryId
     * @param
     * @param id              id of Data Disposal
     * @param dataDisposalDTO
     * @return updated data disposal object
     * @throws DuplicateDataException if data disposal exist with same name then throw exception
     */
    public DataDisposalDTO updateDataDisposal(Long countryId, Long id, DataDisposalDTO dataDisposalDTO) {

        //TODO What actually this code is doing?
        DataDisposalMD dataDisposal = dataDisposalRepository.findByCountryIdAndDeletedAndName(countryId, false, dataDisposalDTO.getName());
        if (Optional.ofNullable(dataDisposal).isPresent()) {
            if (id.equals(dataDisposal.getId())) {
                return dataDisposalDTO;
            }
            throw new DuplicateDataException("data  exist for  " + dataDisposalDTO.getName());
        }
        Integer resultCount =  dataDisposalRepository.updateMasterMetadataName(dataDisposalDTO.getName(), id, countryId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Disposal", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, dataDisposalDTO.getName());
        }
        return dataDisposalDTO;
    }

    /**
     * @param countryId
     * @param dataDisposalDTOS
     * @return
     * @description method save data disposal suggested by unit
     */
    public List<DataDisposalMD> saveSuggestedDataDisposalFromUnit(Long countryId, List<DataDisposalDTO> dataDisposalDTOS) {
        Map<String, List<DataDisposalMD>> result = createDataDisposal(countryId, dataDisposalDTOS, true);
        return result.get(NEW_DATA_LIST);
    }


    /**
     * @param countryId
     * @param dataDisposalIds     - id of data disposal
     * @param suggestedDataStatus -status to update
     */
    public List<DataDisposalMD> updateSuggestedStatusOfDataDisposals(Long countryId, Set<Long> dataDisposalIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = dataDisposalRepository.updateMetadataStatus(countryId, dataDisposalIds, suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Data Disposals are updated successfully with ids :: {}", dataDisposalIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Data Disposal", dataDisposalIds);
        }
        return dataDisposalRepository.findAllByIds(dataDisposalIds);
    }


}





