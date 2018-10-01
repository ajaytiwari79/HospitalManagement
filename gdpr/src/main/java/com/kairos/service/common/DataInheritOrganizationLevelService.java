package com.kairos.service.common;


import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.response.dto.common.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataInheritOrganizationLevelService extends MongoBaseService {


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;
    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;
    @Inject
    private AssetMongoRepository assetMongoRepository;
    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;
    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;
    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;
    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;
    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureMongoRepository;
    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;
    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;


    /**
     * @param countryId
     * @param unitId               - id of the organization which inherit data from from
     * @param organizationMetaData - contain meta data about child organization, on the basis of meta data (org type ,sub type ,service category and sub service) unit
     *                             inherit data from parent
     * @return
     */
    public Boolean inheritDataFromParentOrganization(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaData) {

        return true;

    }


    private void inheritAssetAndAssetMetaDataFromCountry(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaDataDTO) {

        List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasures = organizationalSecurityMeasureMongoRepository.findAllByCountryId(countryId);
        List<StorageFormatResponseDTO> storageFormats = storageFormatMongoRepository.findAllByCountryId(countryId);
        List<TechnicalSecurityMeasureResponseDTO> technicalSecurityMeasures = technicalSecurityMeasureMongoRepository.findAllByCountryId(countryId);
        saveDataDisposalByUnitId(countryId, unitId);
        saveHostingProviderByUnitId(countryId, unitId);
        saveHostingTypeByUnitId(countryId,unitId);
        saveOrgSecurtyMeasureByUnitId(unitId, orgSecurityMeasures);
        saveStorageFormatByUnitId(unitId, storageFormats);
        saveTechSecurityMessureByUnitId(unitId, technicalSecurityMeasures);


    }


    private void saveDataDisposalByUnitId(Long countryId, Long unitId) {
        List<DataDisposalResponseDTO> dataDisposalDTOS = dataDisposalMongoRepository.findAllByCountryId(countryId);
        if (CollectionUtils.isNotEmpty(dataDisposalDTOS)) {
            List<DataDisposal> dataDisposalsList = new ArrayList<>();
            for (DataDisposalResponseDTO dataDisposalDTO : dataDisposalDTOS) {
                DataDisposal dataDisposal = new DataDisposal(dataDisposalDTO.getName());
                dataDisposal.setOrganizationId(unitId);
                dataDisposalsList.add(dataDisposal);
            }
            dataDisposalMongoRepository.saveAll(getNextSequence(dataDisposalsList));
        }
    }

    private void saveHostingProviderByUnitId(Long countryId, Long unitId) {
        List<HostingProviderResponseDTO> hostingProviderDTOS = hostingProviderMongoRepository.findAllByCountryId(countryId);
        if (CollectionUtils.isNotEmpty(hostingProviderDTOS)) {
            List<HostingProvider> hostingProviderList = new ArrayList<>();
            for (HostingProviderResponseDTO hostingProviderDTO : hostingProviderDTOS) {
                HostingProvider hostingProvider = new HostingProvider(hostingProviderDTO.getName());
                hostingProvider.setOrganizationId(unitId);
                hostingProviderList.add(hostingProvider);
            }
            hostingProviderMongoRepository.saveAll(getNextSequence(hostingProviderList));
        }
    }

    private void saveHostingTypeByUnitId(Long countryId, Long unitId) {
        List<HostingTypeResponseDTO> hostingTypeDTOS = hostingTypeMongoRepository.findAllByCountryId(countryId);
        if (CollectionUtils.isNotEmpty(hostingTypeDTOS)) {
            List<HostingType> hostingTypeList = new ArrayList<>();
            hostingTypeDTOS.forEach(hostingTypeDTO -> hostingTypeList.add(new HostingType(hostingTypeDTO.getName())));
            hostingTypeMongoRepository.saveAll(getNextSequence(hostingTypeList));
        }
    }

    private void saveOrgSecurtyMeasureByUnitId(Long unitId, List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasureDTOS) {
        if (CollectionUtils.isNotEmpty(orgSecurityMeasureDTOS)) {
            List<OrganizationalSecurityMeasure> organizationalSecurityMeasureList = new ArrayList<>();
            orgSecurityMeasureDTOS.forEach(orgSecurityMeasureDTO -> organizationalSecurityMeasureList.add(new OrganizationalSecurityMeasure(orgSecurityMeasureDTO.getName())));
            organizationalSecurityMeasureMongoRepository.saveAll(getNextSequence(organizationalSecurityMeasureList));
        }
    }

    private void saveStorageFormatByUnitId(Long unitId, List<StorageFormatResponseDTO> storageFormatDTOS) {
        if (CollectionUtils.isNotEmpty(storageFormatDTOS)) {
            List<StorageFormat> storageFormatList = new ArrayList<>();
            storageFormatDTOS.forEach(storageFormatDTO -> storageFormatList.add(new StorageFormat(storageFormatDTO.getName())));
            storageFormatMongoRepository.saveAll(getNextSequence(storageFormatList));
        }
    }


    private void saveTechSecurityMessureByUnitId(Long unitId, List<TechnicalSecurityMeasureResponseDTO> technicalSecurityMeasureDTOS) {
        if (CollectionUtils.isNotEmpty(technicalSecurityMeasureDTOS)) {
            List<TechnicalSecurityMeasure> technicalSecurityMeasures = new ArrayList<>();
            technicalSecurityMeasureDTOS.forEach(measureResponseDTO -> technicalSecurityMeasures.add(new TechnicalSecurityMeasure(measureResponseDTO.getName())));
            technicalSecurityMeasureMongoRepository.saveAll(getNextSequence(technicalSecurityMeasures));
        }
    }


}


