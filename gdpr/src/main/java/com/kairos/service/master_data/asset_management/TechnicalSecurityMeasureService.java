package com.kairos.service.master_data.asset_management;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.TechnicalSecurityMeasure;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TechnicalSecurityMeasureService {

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
    public List<TechnicalSecurityMeasureDTO> createTechnicalSecurityMeasure(Long countryId, List<TechnicalSecurityMeasureDTO> technicalSecurityMeasureDTOS, boolean isSuggestion) {
        Set<String> existingTechnicalSecurityMeasureNames = technicalSecurityMeasureRepository.findNameByCountryIdAndDeleted(countryId);
        Set<String> techSecurityMeasureNames = ComparisonUtils.getNewMetaDataNames(technicalSecurityMeasureDTOS,existingTechnicalSecurityMeasureNames );
        List<TechnicalSecurityMeasure> technicalSecurityMeasures = new ArrayList<>();
        if (!techSecurityMeasureNames.isEmpty()) {
            for (String name : techSecurityMeasureNames) {
                TechnicalSecurityMeasure technicalSecurityMeasure = new TechnicalSecurityMeasure(countryId, name);
                if (isSuggestion) {
                    technicalSecurityMeasure.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    technicalSecurityMeasure.setSuggestedDate(LocalDate.now());
                } else {
                    technicalSecurityMeasure.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                technicalSecurityMeasures.add(technicalSecurityMeasure);

            }
            technicalSecurityMeasureRepository.saveAll(technicalSecurityMeasures);
        }

        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(technicalSecurityMeasures, TechnicalSecurityMeasureDTO.class);
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
    public TechnicalSecurityMeasure getTechnicalSecurityMeasure(Long countryId, Long id) {

        TechnicalSecurityMeasure exist = technicalSecurityMeasureRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(Long countryId, Long id) {

        Integer resultCount = technicalSecurityMeasureRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Technical Security Measure deleted successfully for id :: {}", id);
        } else {
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

        TechnicalSecurityMeasure technicalSecurityMeasure = technicalSecurityMeasureRepository.findByCountryIdAndName(countryId, technicalSecurityMeasureDTO.getName());
        if (Optional.ofNullable(technicalSecurityMeasure).isPresent()) {
            if (id.equals(technicalSecurityMeasure.getId())) {
                return technicalSecurityMeasureDTO;
            }
            throw new DuplicateDataException("data  exist for  " + technicalSecurityMeasureDTO.getName());
        }
        Integer resultCount = technicalSecurityMeasureRepository.updateMasterMetadataName(technicalSecurityMeasureDTO.getName(), id, countryId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.technicalSecurityMeasure", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, technicalSecurityMeasureDTO.getName());
        }
        return technicalSecurityMeasureDTO;


    }

    /**
     * @param countryId
     * @param technicalSecurityMeasureDTOS
     * @return
     * @description method save technical security measure  suggested by unit
     */
    public void saveSuggestedTechnicalSecurityMeasuresFromUnit(Long countryId, List<TechnicalSecurityMeasureDTO> technicalSecurityMeasureDTOS) {
         createTechnicalSecurityMeasure(countryId, technicalSecurityMeasureDTOS, true);

    }


    /**
     * @param countryId
     * @param techSecurityMeasureIds
     * @param suggestedDataStatus
     * @return
     */
    public List<TechnicalSecurityMeasure> updateSuggestedStatusOfTechnicalSecurityMeasures(Long countryId, Set<Long> techSecurityMeasureIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = technicalSecurityMeasureRepository.updateMetadataStatus(countryId, techSecurityMeasureIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Technical Security Measures are updated successfully with ids :: {}", techSecurityMeasureIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.technicalSecurityMeasure", techSecurityMeasureIds);
        }
        return technicalSecurityMeasureRepository.findAllByIds(techSecurityMeasureIds);
    }
}
