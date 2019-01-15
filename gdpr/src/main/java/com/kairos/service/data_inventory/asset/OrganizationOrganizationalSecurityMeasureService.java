package com.kairos.service.data_inventory.asset;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.OrganizationalSecurityMeasureDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasureMD;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureMDRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureRepository;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.OrganizationalSecurityMeasureService;
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
public class OrganizationOrganizationalSecurityMeasureService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureService.class);

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private OrganizationalSecurityMeasureService organizationalSecurityMeasureService;

    @Inject
    private AssetMongoRepository assetMongoRepository;

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
    public Map<String, List<OrganizationalSecurityMeasureMD>> createOrganizationalSecurityMeasure(Long organizationId, List<OrganizationalSecurityMeasureDTO> orgSecurityMeasureDTOs) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<OrganizationalSecurityMeasureMD>> result = new HashMap<>();
        Set<String> orgSecurityMeasureNames = new HashSet<>();
        if (!orgSecurityMeasureDTOs.isEmpty()) {
            for (OrganizationalSecurityMeasureDTO securityMeasure : orgSecurityMeasureDTOs) {
                orgSecurityMeasureNames.add(securityMeasure.getName());
            }

            List<String> nameInLowerCase = orgSecurityMeasureNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<OrganizationalSecurityMeasureMD> existing = organizationalSecurityMeasureRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            orgSecurityMeasureNames = ComparisonUtils.getNameListForMetadata(existing, orgSecurityMeasureNames);
            List<OrganizationalSecurityMeasureMD> newOrgSecurityMeasures = new ArrayList<>();
            if (!orgSecurityMeasureNames.isEmpty()) {
                for (String name : orgSecurityMeasureNames) {

                    OrganizationalSecurityMeasureMD newOrganizationalSecurityMeasure = new OrganizationalSecurityMeasureMD(name);
                    newOrganizationalSecurityMeasure.setOrganizationId(organizationId);
                    newOrgSecurityMeasures.add(newOrganizationalSecurityMeasure);

                }
                newOrgSecurityMeasures = organizationalSecurityMeasureRepository.saveAll(newOrgSecurityMeasures);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newOrgSecurityMeasures);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


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
    public OrganizationalSecurityMeasureMD getOrganizationalSecurityMeasure(Long organizationId, Long id) {

        OrganizationalSecurityMeasureMD exist = organizationalSecurityMeasureRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        }
        return exist;

    }


    public Boolean deleteOrganizationalSecurityMeasure(Long unitId, BigInteger orgSecurityMeasureId) {

        List<AssetBasicResponseDTO> assetsLinkedWithOrganizationalSecurityMeasure = assetMongoRepository.findAllAssetLinkedWithOrganizationalSecurityMeasure(unitId, orgSecurityMeasureId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithOrganizationalSecurityMeasure)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Organization Security Measure", new StringBuilder(assetsLinkedWithOrganizationalSecurityMeasure.stream().map(AssetBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        organizationalSecurityMeasureMongoRepository.safeDeleteById(orgSecurityMeasureId);
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

        OrganizationalSecurityMeasureMD organizationalSecurityMeasure = organizationalSecurityMeasureRepository.findByOrganizationIdAndDeletedAndName(organizationId, false, orgSecurityMeasureDTO.getName());
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


    public Map<String, List<OrganizationalSecurityMeasureMD>> saveAndSuggestOrganizationalSecurityMeasures(Long countryId, Long organizationId, List<OrganizationalSecurityMeasureDTO> orgSecurityMeasureDTOS) {

        Map<String, List<OrganizationalSecurityMeasureMD>> result = createOrganizationalSecurityMeasure(organizationId, orgSecurityMeasureDTOS);
        List<OrganizationalSecurityMeasureMD> masterOrganizationalSecurityMeasureSuggestedByUnit = organizationalSecurityMeasureService.saveSuggestedOrganizationalSecurityMeasuresFromUnit(countryId, orgSecurityMeasureDTOS);
        if (!masterOrganizationalSecurityMeasureSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterOrganizationalSecurityMeasureSuggestedByUnit);
        }
        return result;
    }

}
