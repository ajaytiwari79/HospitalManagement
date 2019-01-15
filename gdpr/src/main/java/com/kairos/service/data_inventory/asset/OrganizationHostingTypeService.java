package com.kairos.service.data_inventory.asset;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingTypeMD;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.HostingTypeService;
import com.kairos.utils.ComparisonUtils;
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
public class OrganizationHostingTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeRepository hostingTypeRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private HostingTypeService hostingTypeService;

    @Inject
    private AssetMongoRepository assetMongoRepository;


    /**
     * @param organizationId
     * @param hostingTypeDTOS
     * @return return map which contain list of new HostingType and list of existing HostingType if HostingType already exist
     * @description this method create new HostingType if HostingType not exist with same name ,
     * and if exist then simply add  HostingType to existing list and return list ;
     * findByOrganizationIdAndNamesList()  return list of existing HostingType using collation ,used for case insensitive result
     */
    public Map<String, List<HostingTypeMD>> createHostingType(Long organizationId, List<HostingTypeDTO> hostingTypeDTOS) {

        Map<String, List<HostingTypeMD>> result = new HashMap<>();
        Set<String> hostingTypeNames = new HashSet<>();
        if (!hostingTypeDTOS.isEmpty()) {
            for (HostingTypeDTO hostingType : hostingTypeDTOS) {
                if (!StringUtils.isBlank(hostingType.getName())) {
                    hostingTypeNames.add(hostingType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<String> nameInLowerCase = hostingTypeNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<HostingTypeMD> existing = hostingTypeRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            hostingTypeNames = ComparisonUtils.getNameListForMetadata(existing, hostingTypeNames);
            List<HostingTypeMD> newHostingTypes = new ArrayList<>();
            if (!hostingTypeNames.isEmpty()) {
                for (String name : hostingTypeNames) {
                    HostingTypeMD newHostingType = new HostingTypeMD(name);
                    newHostingType.setOrganizationId(organizationId);
                    newHostingTypes.add(newHostingType);
                }
                newHostingTypes = hostingTypeRepository.saveAll(newHostingTypes);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newHostingTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param
     * @param organizationId
     * @return list of HostingType
     */
    public List<HostingTypeResponseDTO> getAllHostingType(Long organizationId) {
        return hostingTypeRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @param id             of HostingType
     * @return HostingType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if HostingType not found for given id
     */
    public HostingTypeMD getHostingType(Long organizationId, Long id) {

        HostingTypeMD exist = hostingTypeRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long unitId, BigInteger hostingTypeId) {

      /*  List<AssetBasicResponseDTO> assetsLinkedWithHostingType = assetMongoRepository.findAllAssetLinkedWithHostingType(unitId, hostingTypeId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithHostingType)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Hosting Type", new StringBuilder(assetsLinkedWithHostingType.stream().map(AssetBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        hostingTypeMongoRepository.safeDeleteById(hostingTypeId);*/
        return true;
    }


    /**
     * @param
     * @param organizationId
     * @param id             id of HostingType
     * @param hostingTypeDTO
     * @return HostingType updated object
     * @throws DuplicateDataException if HostingType already exist with same name
     */
    public HostingTypeDTO updateHostingType(Long organizationId, Long id, HostingTypeDTO hostingTypeDTO) {


        HostingTypeMD hostingType = hostingTypeRepository.findByOrganizationIdAndDeletedAndName(organizationId, false, hostingTypeDTO.getName());
        if (Optional.ofNullable(hostingType).isPresent()) {
            if (id.equals(hostingType.getId())) {
                return hostingTypeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Hosting Type", hostingType.getName());
        }
        Integer resultCount =  hostingTypeRepository.updateMetadataName(hostingTypeDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Type", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, hostingTypeDTO.getName());
        }
        return hostingTypeDTO;


    }


    public Map<String, List<HostingTypeMD>> saveAndSuggestHostingTypes(Long countryId, Long organizationId, List<HostingTypeDTO> hostingTypeDTOS) {

        Map<String, List<HostingTypeMD>> result = createHostingType(organizationId, hostingTypeDTOS);
        List<HostingTypeMD> masterHostingTypeSuggestedByUnit = hostingTypeService.saveSuggestedHostingTypesFromUnit(countryId, hostingTypeDTOS);
        if (!masterHostingTypeSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterHostingTypeSuggestedByUnit);
        }
        return result;
    }

}
