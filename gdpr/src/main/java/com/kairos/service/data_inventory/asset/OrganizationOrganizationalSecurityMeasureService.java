package com.kairos.service.data_inventory.asset;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.OrganizationalSecurityMeasureDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureRepository;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.OrganizationalSecurityMeasureService;
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
public class OrganizationOrganizationalSecurityMeasureService{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private OrganizationalSecurityMeasureService organizationalSecurityMeasureService;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private OrganizationalSecurityMeasureRepository organizationalSecurityMeasureRepository;


    /**
     * @param
     * @param organizationId
     * @param orgSecurityMeasureDTOs
     * @return return map which contain list of new OrganizationalSecurityMeasure and list of existing OrganizationalSecurityMeasure if OrganizationalSecurityMeasure already exist
     * @description this method create new OrganizationalSecurityMeasure if OrganizationalSecurityMeasure not exist with same name ,
     * and if exist then simply add  OrganizationalSecurityMeasure to existing list and return list ;
     * findByOrganizationIdAndNamesList()  return list of existing OrganizationalSecurityMeasure using collation ,used for case insensitive result
     */
    public List<OrganizationalSecurityMeasureDTO> createOrganizationalSecurityMeasure(Long organizationId, List<OrganizationalSecurityMeasureDTO> orgSecurityMeasureDTOs) {
        //TODO still need to optimize we can get name of list in string from here
        Set<String> orgSecurityMeasureNames = new HashSet<>();
            for (OrganizationalSecurityMeasureDTO securityMeasure : orgSecurityMeasureDTOs) {
                orgSecurityMeasureNames.add(securityMeasure.getName());
            }

            List<String> nameInLowerCase = orgSecurityMeasureNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<OrganizationalSecurityMeasure> previousOrganizationalSecurityMeasures = organizationalSecurityMeasureRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            orgSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(previousOrganizationalSecurityMeasures, orgSecurityMeasureNames);
            List<OrganizationalSecurityMeasure> organizationalSecurityMeasures = new ArrayList<>();
            if (!orgSecurityMeasureNames.isEmpty()) {
                for (String name : orgSecurityMeasureNames) {

                    OrganizationalSecurityMeasure newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasure(name);
                    newOrganizationalSecurityMeasure.setOrganizationId(organizationId);
                    organizationalSecurityMeasures.add(newOrganizationalSecurityMeasure);

                }
              organizationalSecurityMeasureRepository.saveAll(organizationalSecurityMeasures);
            }
           return ObjectMapperUtils.copyPropertiesOfListByMapper(organizationalSecurityMeasures,OrganizationalSecurityMeasureDTO.class);

    }

    /**
     * @param
     * @param organizationId
     * @return list of OrganizationalSecurityMeasure
     */
    public List<OrganizationalSecurityMeasureResponseDTO> getAllOrganizationalSecurityMeasure(Long organizationId) {
        return organizationalSecurityMeasureRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @param id             id of OrganizationalSecurityMeasure
     * @return OrganizationalSecurityMeasure object fetch via id
     * @throws DataNotFoundByIdException throw exception if OrganizationalSecurityMeasure not exist for given id
     */
    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasure(Long organizationId, Long id) {

        OrganizationalSecurityMeasure exist = organizationalSecurityMeasureRepository.findByIdAndOrganizationIdAndDeletedFalse(id, organizationId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        return exist;

    }


    public Boolean deleteOrganizationalSecurityMeasure(Long unitId, Long orgSecurityMeasureId) {

        List<String> assetsLinked = assetRepository.findAllAssetLinkedWithOrganizationalSecurityMeasure(unitId, orgSecurityMeasureId);
        if (CollectionUtils.isNotEmpty(assetsLinked)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Organization Security Measure", StringUtils.join(assetsLinked, ','));
        }
        organizationalSecurityMeasureRepository.deleteByIdAndOrganizationId(orgSecurityMeasureId, unitId);
        return true;

    }

    /**
     * @param
     * @param organizationId
     * @param id                    id of OrganizationalSecurityMeasure
     * @param orgSecurityMeasureDTO
     * @return return updated OrganizationalSecurityMeasure object
     * @throws DuplicateDataException if OrganizationalSecurityMeasure not exist for given id
     */
    public OrganizationalSecurityMeasureDTO updateOrganizationalSecurityMeasure(Long organizationId, Long id, OrganizationalSecurityMeasureDTO orgSecurityMeasureDTO) {

        OrganizationalSecurityMeasure organizationalSecurityMeasure = organizationalSecurityMeasureRepository.findByOrganizationIdAndDeletedAndName(organizationId,  orgSecurityMeasureDTO.getName());
        if (Optional.ofNullable(organizationalSecurityMeasure).isPresent()) {
            if (id.equals(organizationalSecurityMeasure.getId())) {
                return orgSecurityMeasureDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Organizational Security Measure", organizationalSecurityMeasure.getName());
        }
        Integer resultCount =  organizationalSecurityMeasureRepository.updateMetadataName(orgSecurityMeasureDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Organizational Security Measure", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, orgSecurityMeasureDTO.getName());
        }
        return orgSecurityMeasureDTO;


    }


    public List<OrganizationalSecurityMeasureDTO> saveAndSuggestOrganizationalSecurityMeasures(Long countryId, Long organizationId, List<OrganizationalSecurityMeasureDTO> orgSecurityMeasureDTOS) {

        List<OrganizationalSecurityMeasureDTO> result = createOrganizationalSecurityMeasure(organizationId, orgSecurityMeasureDTOS);
        organizationalSecurityMeasureService.saveSuggestedOrganizationalSecurityMeasuresFromUnit(countryId, orgSecurityMeasureDTOS);
        return result;
    }

    public List<OrganizationalSecurityMeasure> getAllOrganizationalSecurityMeasureByIds(Set<Long> ids){
        return organizationalSecurityMeasureRepository.findAllByIds(ids);
    }

}
