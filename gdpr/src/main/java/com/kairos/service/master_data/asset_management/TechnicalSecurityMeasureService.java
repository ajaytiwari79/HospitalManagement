package com.kairos.service.master_data.asset_management;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.TechnicalSecurityMeasureMD;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
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
public class TechnicalSecurityMeasureService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalSecurityMeasureService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TechnicalSecurityMeasureRepository technicalSecurityMeasureRepository;

    /**
     * @param countryId
     * @param
     * @param technicalSecurityMeasureDTOS
     * @return return map which contain list of new TechnicalSecurityMeasure and list of existing TechnicalSecurityMeasure if TechnicalSecurityMeasure already exist
     * @description this method create new TechnicalSecurityMeasure if TechnicalSecurityMeasure not exist with same name ,
     * and if exist then simply add  TechnicalSecurityMeasure to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TechnicalSecurityMeasure using collation ,used for case insensitive result
     */
    public Map<String, List<TechnicalSecurityMeasureMD>> createTechnicalSecurityMeasure(Long countryId, List<TechnicalSecurityMeasureDTO> technicalSecurityMeasureDTOS, boolean isSuggestion) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<TechnicalSecurityMeasureMD>> result = new HashMap<>();
        Set<String> techSecurityMeasureNames = new HashSet<>();
        if (!technicalSecurityMeasureDTOS.isEmpty()) {
            for (TechnicalSecurityMeasureDTO technicalSecurityMeasure : technicalSecurityMeasureDTOS) {
                techSecurityMeasureNames.add(technicalSecurityMeasure.getName());
            }
            List<String> nameInLowerCase = techSecurityMeasureNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());

            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<TechnicalSecurityMeasureMD> existing = technicalSecurityMeasureRepository.findByCountryIdAndDeletedAndNameIn(countryId, false, nameInLowerCase);
            techSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(existing, techSecurityMeasureNames);

            List<TechnicalSecurityMeasureMD> newTechnicalMeasures = new ArrayList<>();
            if (!techSecurityMeasureNames.isEmpty()) {
                for (String name : techSecurityMeasureNames) {
                    TechnicalSecurityMeasureMD newTechnicalSecurityMeasure = new TechnicalSecurityMeasureMD(name,countryId);
                    if(isSuggestion){
                        newTechnicalSecurityMeasure.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                        newTechnicalSecurityMeasure.setSuggestedDate(LocalDate.now());
                    }else{
                        newTechnicalSecurityMeasure.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                    }
                    newTechnicalMeasures.add(newTechnicalSecurityMeasure);

                }
                newTechnicalMeasures = technicalSecurityMeasureRepository.saveAll(newTechnicalMeasures);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newTechnicalMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param countryId
     * @param
     * @return list of TechnicalSecurityMeasure
     */
    public List<TechnicalSecurityMeasureResponseDTO> getAllTechnicalSecurityMeasure(Long countryId) {
        return technicalSecurityMeasureRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }


    /**
     * @param countryId
     * @param
     * @param id        id of TechnicalSecurityMeasure
     * @return object of TechnicalSecurityMeasure
     * @throws DataNotFoundByIdException throw exception if TechnicalSecurityMeasure not exist for given id
     */
    public TechnicalSecurityMeasureMD getTechnicalSecurityMeasure(Long countryId, Long id) {

        TechnicalSecurityMeasureMD exist = technicalSecurityMeasureRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(Long countryId,Long id) {

        Integer resultCount = technicalSecurityMeasureRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Technical Security Measure deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;

    }

    /**
     * @param countryId
     * @param
     * @param id                          id of TechnicalSecurityMeasure
     * @param technicalSecurityMeasureDTO
     * @return TechnicalSecurityMeasure updated object
     * @throws DuplicateDataException throw exception if TechnicalSecurityMeasure data not exist for given id
     */
    public TechnicalSecurityMeasureDTO updateTechnicalSecurityMeasure(Long countryId, Long id, TechnicalSecurityMeasureDTO technicalSecurityMeasureDTO) {
        //TODO What actually this code is doing?
        TechnicalSecurityMeasureMD technicalSecurityMeasure = technicalSecurityMeasureRepository.findByCountryIdAndDeletedAndName(countryId, false, technicalSecurityMeasureDTO.getName());
        if (Optional.ofNullable(technicalSecurityMeasure).isPresent()) {
            if (id.equals(technicalSecurityMeasure.getId())) {
                return technicalSecurityMeasureDTO;
            }
            throw new DuplicateDataException("data  exist for  " + technicalSecurityMeasureDTO.getName());
        }
        Integer resultCount =  technicalSecurityMeasureRepository.updateMasterMetadataName(technicalSecurityMeasureDTO.getName(), id, countryId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Technical Security Measure", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, technicalSecurityMeasureDTO.getName());
        }
        return technicalSecurityMeasureDTO;


    }

    /**
     * @description method save technical security measure  suggested by unit
     * @param countryId
     * @param technicalSecurityMeasureDTOS
     * @return
     */
    public List<TechnicalSecurityMeasureMD> saveSuggestedTechnicalSecurityMeasuresFromUnit(Long countryId, List<TechnicalSecurityMeasureDTO> technicalSecurityMeasureDTOS) {
        Map<String, List<TechnicalSecurityMeasureMD>> result = createTechnicalSecurityMeasure(countryId, technicalSecurityMeasureDTOS, true);
        return result.get(NEW_DATA_LIST);

    }


    /**
     *
     * @param countryId
     * @param techSecurityMeasureIds
     * @param suggestedDataStatus
     * @return
     */
    public List<TechnicalSecurityMeasureMD> updateSuggestedStatusOfTechnicalSecurityMeasures(Long countryId, Set<Long> techSecurityMeasureIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = technicalSecurityMeasureRepository.updateMetadataStatus(countryId, techSecurityMeasureIds, suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Technical Security Measures are updated successfully with ids :: {}", techSecurityMeasureIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Technical Security Measure", techSecurityMeasureIds);
        }
        return technicalSecurityMeasureRepository.findAllByIds(techSecurityMeasureIds);
    }
}
