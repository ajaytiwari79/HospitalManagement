package com.kairos.service.data_inventory.asset;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.TechnicalSecurityMeasureMD;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.TechnicalSecurityMeasureService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationTechnicalSecurityMeasureService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationTechnicalSecurityMeasureService.class);

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TechnicalSecurityMeasureService technicalSecurityMeasureService;


    @Inject
    private AssetRepository assetRepository;

    @Inject
    private TechnicalSecurityMeasureRepository technicalSecurityMeasureRepository;


    /**
     * @param
     * @param organizationId
     * @param technicalSecurityMeasureDTOS
     * @return return map which contain list of new TechnicalSecurityMeasure and list of existing TechnicalSecurityMeasure if TechnicalSecurityMeasure already exist
     * @description this method create new TechnicalSecurityMeasure if TechnicalSecurityMeasure not exist with same name ,
     * and if exist then simply add  TechnicalSecurityMeasure to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing TechnicalSecurityMeasure using collation ,used for case insensitive result
     */
    public Map<String, List<TechnicalSecurityMeasureMD>> createTechnicalSecurityMeasure(Long organizationId, List<TechnicalSecurityMeasureDTO> technicalSecurityMeasureDTOS) {

        Map<String, List<TechnicalSecurityMeasureMD>> result = new HashMap<>();
        Set<String> techSecurityMeasureNames = new HashSet<>();
        if (!technicalSecurityMeasureDTOS.isEmpty()) {
            for (TechnicalSecurityMeasureDTO technicalSecurityMeasure : technicalSecurityMeasureDTOS) {
                if (!StringUtils.isBlank(technicalSecurityMeasure.getName())) {
                    techSecurityMeasureNames.add(technicalSecurityMeasure.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<String> nameInLowerCase = techSecurityMeasureNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<TechnicalSecurityMeasureMD> existing = technicalSecurityMeasureRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            techSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(existing, techSecurityMeasureNames);

            List<TechnicalSecurityMeasureMD> newTechnicalMeasures = new ArrayList<>();
            if (!techSecurityMeasureNames.isEmpty()) {
                for (String name : techSecurityMeasureNames) {
                    TechnicalSecurityMeasureMD newTechnicalSecurityMeasure = new TechnicalSecurityMeasureMD(name);
                    newTechnicalSecurityMeasure.setOrganizationId(organizationId);
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
     * @param
     * @param organizationId
     * @return list of TechnicalSecurityMeasure
     */
    public List<TechnicalSecurityMeasureResponseDTO> getAllTechnicalSecurityMeasure(Long organizationId) {
        return technicalSecurityMeasureRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @param id             id of TechnicalSecurityMeasure
     * @return object of TechnicalSecurityMeasure
     * @throws DataNotFoundByIdException throw exception if TechnicalSecurityMeasure not exist for given id
     */
    public TechnicalSecurityMeasureMD getTechnicalSecurityMeasure(Long organizationId, Long id) {

        TechnicalSecurityMeasureMD exist = technicalSecurityMeasureRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteTechnicalSecurityMeasure(Long unitId, Long techSecurityMeasureId) {

        List<String> assetsLinked = assetRepository.findAllAssetLinkedWithTechnicalSecurityMeasure(unitId, techSecurityMeasureId);
        if (CollectionUtils.isNotEmpty(assetsLinked)) {

            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Technical Security Measure", StringUtils.join(assetsLinked, ','));
        }
        technicalSecurityMeasureRepository.deleteByIdAndOrganizationId(techSecurityMeasureId, unitId);
        return true;
    }

    /**
     * @param
     * @param organizationId
     * @param id                          id of TechnicalSecurityMeasure
     * @param technicalSecurityMeasureDTO
     * @return TechnicalSecurityMeasure updated object
     * @throws DuplicateDataException throw exception if TechnicalSecurityMeasure data not exist for given id
     */
    public TechnicalSecurityMeasureDTO updateTechnicalSecurityMeasure(Long organizationId, Long id, TechnicalSecurityMeasureDTO technicalSecurityMeasureDTO) {
        TechnicalSecurityMeasureMD technicalSecurityMeasure = technicalSecurityMeasureRepository.findByOrganizationIdAndDeletedAndName(organizationId, false, technicalSecurityMeasureDTO.getName());
        if (Optional.ofNullable(technicalSecurityMeasure).isPresent()) {
            if (id.equals(technicalSecurityMeasure.getId())) {
                return technicalSecurityMeasureDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Technical Security Measure", technicalSecurityMeasure.getName());
        }
        Integer resultCount =  technicalSecurityMeasureRepository.updateMasterMetadataName(technicalSecurityMeasureDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Technical Security Format", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, technicalSecurityMeasureDTO.getName());
        }
        return technicalSecurityMeasureDTO;


    }


    public Map<String, List<TechnicalSecurityMeasureMD>> saveAndSuggestTechnicalSecurityMeasures(Long countryId, Long organizationId, List<TechnicalSecurityMeasureDTO> techSecurityMeasureDTOS) {

        Map<String, List<TechnicalSecurityMeasureMD>> result = createTechnicalSecurityMeasure(organizationId, techSecurityMeasureDTOS);
        List<TechnicalSecurityMeasureMD> masterTechnicalSecurityMeasureSuggestedByUnit = technicalSecurityMeasureService.saveSuggestedTechnicalSecurityMeasuresFromUnit(countryId, techSecurityMeasureDTOS);
        if (!masterTechnicalSecurityMeasureSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterTechnicalSecurityMeasureSuggestedByUnit);
        }
        return result;
    }

    public List<TechnicalSecurityMeasureMD> getAllTechnicalSecurityMeasureByIds(Set<Long> ids){
        return technicalSecurityMeasureRepository.findAllByIds(ids);
    }


}
