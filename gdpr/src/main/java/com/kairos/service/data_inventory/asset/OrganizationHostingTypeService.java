package com.kairos.service.data_inventory.asset;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.HostingTypeDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.HostingTypeService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationHostingTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeService.class);

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

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
    public Map<String, List<HostingType>> createHostingType(Long organizationId, List<HostingTypeDTO> hostingTypeDTOS) {

        Map<String, List<HostingType>> result = new HashMap<>();
        Set<String> hostingTypeNames = new HashSet<>();
        if (!hostingTypeDTOS.isEmpty()) {
            for (HostingTypeDTO hostingType : hostingTypeDTOS) {
                if (!StringUtils.isBlank(hostingType.getName())) {
                    hostingTypeNames.add(hostingType.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<HostingType> existing = findMetaDataByNameAndUnitId(organizationId, hostingTypeNames, HostingType.class);
            hostingTypeNames = ComparisonUtils.getNameListForMetadata(existing, hostingTypeNames);
            List<HostingType> newHostingTypes = new ArrayList<>();
            if (!hostingTypeNames.isEmpty()) {
                for (String name : hostingTypeNames) {
                    HostingType newHostingType = new HostingType(name);
                    newHostingType.setOrganizationId(organizationId);
                    newHostingTypes.add(newHostingType);
                }
                newHostingTypes = hostingTypeMongoRepository.saveAll(getNextSequence(newHostingTypes));
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
        return hostingTypeMongoRepository.findAllOrganizationHostingTypes(organizationId,new Sort(Sort.Direction.DESC, "_id"));
    }


    /**
     * @param
     * @param organizationId
     * @param id             of HostingType
     * @return HostingType object fetch by given id
     * @throws DataNotFoundByIdException throw exception if HostingType not found for given id
     */
    public HostingType getHostingType(Long organizationId, BigInteger id) {

        HostingType exist = hostingTypeMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteHostingType(Long unitId, BigInteger hostingTypeId) {

        List<AssetBasicResponseDTO> assetsLinkedWithHostingType = assetMongoRepository.findAllAssetLinkedWithHostingType(unitId, hostingTypeId);
        if (!assetsLinkedWithHostingType.isEmpty()) {
            StringBuilder assetNames=new StringBuilder();
            assetsLinkedWithHostingType.forEach(asset->assetNames.append(asset.getName()+","));
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Hosting Type", assetNames);
        }
        HostingType hostingType = hostingTypeMongoRepository.findByOrganizationIdAndId(unitId, hostingTypeId);
        if (!Optional.ofNullable(hostingType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Type", hostingTypeId);
        }
        delete(hostingType);
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
    public HostingTypeDTO updateHostingType(Long organizationId, BigInteger id, HostingTypeDTO hostingTypeDTO) {


        HostingType hostingType = hostingTypeMongoRepository.findByOrganizationIdAndName(organizationId, hostingTypeDTO.getName());
        if (Optional.ofNullable(hostingType).isPresent()) {
            if (id.equals(hostingType.getId())) {
                return hostingTypeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Hosting Type", hostingType.getName());
        }
        hostingType = hostingTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(hostingType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Hosting Type", id);
        }
        hostingType.setName(hostingTypeDTO.getName());
        hostingTypeMongoRepository.save(hostingType);
        return hostingTypeDTO;


    }


    public Map<String, List<HostingType>> saveAndSuggestHostingTypes(Long countryId, Long organizationId, List<HostingTypeDTO> hostingTypeDTOS) {

        Map<String, List<HostingType>> result;
        result = createHostingType(organizationId, hostingTypeDTOS);
        List<HostingType> masterHostingTypeSuggestedByUnit = hostingTypeService.saveSuggestedHostingTypesFromUnit(countryId, hostingTypeDTOS);
        if (!masterHostingTypeSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterHostingTypeSuggestedByUnit);
        }
        return result;
    }

}
