package com.kairos.service.common;


import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.*;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataCategoryMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
import com.kairos.response.dto.common.*;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.AsynchronousService;
import com.kairos.service.master_data.data_category_element.DataSubjectMappingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.Callable;

@Service
public class DataInheritOrganizationLevelService extends MongoBaseService {


    @Inject
    private AsynchronousService asynchronousService;
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
    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;
    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;
    @Inject
    private ProcessingLegalBasisMongoRepository processingLegalBasisMongoRepository;
    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;
    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;
    @Inject
    private TransferMethodMongoRepository transferMethodMongoRepository;
    @Inject
    private DataSubjectMappingService dataSubjectMappingService;
    @Inject
    private DataCategoryMongoRepository dataCategoryMongoRepository;


    public Boolean inheritMasterAssetAndProcessingActivityMetaData(Long countryId, Long unitId) throws Exception {

        List<Callable<Boolean>> callables = new ArrayList<>();
        Callable<Boolean> dataDispoaslTask = () -> {
            List<DataDisposalResponseDTO> dataDisposalResponseDTOS = dataDisposalMongoRepository.findAllByCountryId(countryId);
            saveDataDisposal(unitId, dataDisposalResponseDTOS);
            return true;
        };
        Callable<Boolean> hostingProviderTask = () -> {
            List<HostingProviderResponseDTO> hostingProviderDTOS = hostingProviderMongoRepository.findAllByCountryId(countryId);
            saveHostingProvider(unitId, hostingProviderDTOS);
            return true;
        };
        Callable<Boolean> hostingTypeTask = () -> {
            List<HostingTypeResponseDTO> hostingTypeDTOS = hostingTypeMongoRepository.findAllByCountryId(countryId);
            saveHostingType(unitId, hostingTypeDTOS);
            return true;

        };
        Callable<Boolean> storageFormatTask = () -> {
            List<StorageFormatResponseDTO> storageFormatDTOS = storageFormatMongoRepository.findAllByCountryId(countryId);
            saveStorageFormat(unitId, storageFormatDTOS);
            return true;

        };
        Callable<Boolean> technicalSecurityMeasureTask = () -> {

            List<TechnicalSecurityMeasureResponseDTO> techSecurityMeasureDTOS = technicalSecurityMeasureMongoRepository.findAllByCountryId(countryId);
            saveTechnicalSecurityMeasure(unitId, techSecurityMeasureDTOS);
            return true;

        };
        Callable<Boolean> orgSecurityMeasureTask = () -> {
            List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasureDTOS = organizationalSecurityMeasureMongoRepository.findAllByCountryId(countryId);
            saveOrgSecurityMeasure(unitId, orgSecurityMeasureDTOS);
            return true;
        };

        Callable<Boolean> accessorPartyTask = () -> {
            List<AccessorPartyResponseDTO> accessorPartyDTOS = accessorPartyMongoRepository.findAllByCountryId(countryId);
            saveAccessorParties(unitId, accessorPartyDTOS);
            return true;
        };

        Callable<Boolean> dataSourceTask = () -> {
            List<DataSourceResponseDTO> dataSourceDTOS = dataSourceMongoRepository.findAllByCountryId(countryId);
            saveDataSources(unitId, dataSourceDTOS);
            return true;
        };
        Callable<Boolean> legalBasisTask = () -> {
            List<ProcessingLegalBasisResponseDTO> legalBasisDTOS = processingLegalBasisMongoRepository.findAllByCountryId(countryId);
            saveProcessingLegalBasis(unitId, legalBasisDTOS);
            return true;
        };
        Callable<Boolean> processingPurposeTask = () -> {
            List<ProcessingPurposeResponseDTO> processingPurposeDTOS = processingPurposeMongoRepository.findAllByCountryId(countryId);
            saveProcessingPurposes(unitId, processingPurposeDTOS);
            return true;
        };
        Callable<Boolean> responsibilityTypeTask = () -> {
            List<ResponsibilityTypeResponseDTO> responsibilityTypeDTOS = responsibilityTypeMongoRepository.findAllByCountryId(countryId);
            saveResponsibilityTypes(unitId, responsibilityTypeDTOS);
            return true;
        };
        Callable<Boolean> transferMethodTask = () -> {
            List<TransferMethodResponseDTO> transferMethodDTOS = transferMethodMongoRepository.findAllByCountryId(countryId);
            saveTransferMethods(unitId, transferMethodDTOS);
            return true;
        };

        callables.add(hostingProviderTask);
        callables.add(dataDispoaslTask);
        callables.add(hostingTypeTask);
        callables.add(technicalSecurityMeasureTask);
        callables.add(storageFormatTask);
        callables.add(orgSecurityMeasureTask);
        callables.add(accessorPartyTask);
        callables.add(dataSourceTask);
        callables.add(legalBasisTask);
        callables.add(processingPurposeTask);
        callables.add(responsibilityTypeTask);
        callables.add(transferMethodTask);
        asynchronousService.executeAsynchronously(callables);

        return true;

    }


    private void copyDataSubjectDataCategoryAndDataElementsFromCountryToUnit(Long countryId, Long unitId) {

        List<DataSubjectMappingResponseDTO> dataSubjectDTOS = dataSubjectMappingService.getAllDataSubjectWithDataCategory(countryId);
        List<DataCategoryResponseDTO> dataCategoryDTOS = new ArrayList<>();

        dataSubjectDTOS.forEach(dataSubjectDTO -> {

            DataSubjectMapping dataSubject = new DataSubjectMapping(dataSubjectDTO.getName());
            dataSubject.setOrganizationId(unitId);

            if (CollectionUtils.isNotEmpty(dataSubjectDTO.getDataCategories())) {


            }
        });


    }


    private void saveDataCategoryAndDataElement(Long countryId, Long unitId) {


    }


    private void saveDataDisposal(Long unitId, List<DataDisposalResponseDTO> dataDisposalDTOS) {
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

    private void saveHostingProvider(Long unitId, List<HostingProviderResponseDTO> hostingProviderDTOS) {
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

    private void saveHostingType(Long unitId, List<HostingTypeResponseDTO> hostingTypeDTOS) {
        if (CollectionUtils.isNotEmpty(hostingTypeDTOS)) {
            List<HostingType> hostingTypeList = new ArrayList<>();
            for (HostingTypeResponseDTO hostingTypeDTO : hostingTypeDTOS) {
                HostingType hostingType = new HostingType(hostingTypeDTO.getName());
                hostingType.setOrganizationId(unitId);
                hostingTypeList.add(hostingType);
            }
            hostingTypeMongoRepository.saveAll(getNextSequence(hostingTypeList));

        }
    }


    private void saveStorageFormat(Long unitId, List<StorageFormatResponseDTO> storageFormatDTOS) {
        if (CollectionUtils.isNotEmpty(storageFormatDTOS)) {
            List<StorageFormat> storageFormatList = new ArrayList<>();
            for (StorageFormatResponseDTO storageFormatDTO : storageFormatDTOS) {
                StorageFormat storageFormat = new StorageFormat(storageFormatDTO.getName());
                storageFormat.setOrganizationId(unitId);
                storageFormatList.add(storageFormat);
            }
            storageFormatMongoRepository.saveAll(getNextSequence(storageFormatList));
        }
    }

    private void saveTechnicalSecurityMeasure(Long unitId, List<TechnicalSecurityMeasureResponseDTO> techSecurityMeasureDTOS) {

        if (CollectionUtils.isNotEmpty(techSecurityMeasureDTOS)) {
            List<TechnicalSecurityMeasure> technicalSecurityMeasures = new ArrayList<>();
            for (TechnicalSecurityMeasureResponseDTO technicalSecurityMeasureDTO : techSecurityMeasureDTOS) {
                TechnicalSecurityMeasure technicalSecurityMeasure = new TechnicalSecurityMeasure(technicalSecurityMeasureDTO.getName());
                technicalSecurityMeasure.setOrganizationId(unitId);
                technicalSecurityMeasures.add(technicalSecurityMeasure);
            }
            technicalSecurityMeasureMongoRepository.saveAll(getNextSequence(technicalSecurityMeasures));
        }
    }

    private void saveOrgSecurityMeasure(Long unitId, List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasureDTOS) {
        if (CollectionUtils.isNotEmpty(orgSecurityMeasureDTOS)) {
            List<OrganizationalSecurityMeasure> organizationalSecurityMeasureList = new ArrayList<>();
            for (OrganizationalSecurityMeasureResponseDTO orgSecurityMeasureDTO : orgSecurityMeasureDTOS) {
                OrganizationalSecurityMeasure organizationalSecurityMeasure = new OrganizationalSecurityMeasure(orgSecurityMeasureDTO.getName());
                organizationalSecurityMeasure.setOrganizationId(unitId);
                organizationalSecurityMeasureList.add(organizationalSecurityMeasure);
            }
            organizationalSecurityMeasureMongoRepository.saveAll(getNextSequence(organizationalSecurityMeasureList));
        }


    }

    private void saveAccessorParties(Long unitId, List<AccessorPartyResponseDTO> accessorPartyDTOS) {
        if (CollectionUtils.isNotEmpty(accessorPartyDTOS)) {
            List<AccessorParty> accessorParties = new ArrayList<>();
            for (AccessorPartyResponseDTO accessorPartyDTO : accessorPartyDTOS) {
                AccessorParty accessorParty = new AccessorParty(accessorPartyDTO.getName());
                accessorParty.setOrganizationId(unitId);
                accessorParties.add(accessorParty);
            }
            accessorPartyMongoRepository.saveAll(getNextSequence(accessorParties));
        }

    }

    private void saveDataSources(Long unitId, List<DataSourceResponseDTO> dataSourceDTOS) {
        if (CollectionUtils.isNotEmpty(dataSourceDTOS)) {
            List<DataSource> dataSourceList = new ArrayList<>();
            for (DataSourceResponseDTO dataSourceDTO : dataSourceDTOS) {
                DataSource dataSource = new DataSource(dataSourceDTO.getName());
                dataSource.setOrganizationId(unitId);
                dataSourceList.add(dataSource);
            }
            dataSourceMongoRepository.saveAll(getNextSequence(dataSourceList));

        }
    }

    private void saveProcessingLegalBasis(Long unitId, List<ProcessingLegalBasisResponseDTO> legalBasisDTOS) {
        if (CollectionUtils.isNotEmpty(legalBasisDTOS)) {
            List<ProcessingLegalBasis> processingLegalBasisList = new ArrayList<>();
            for (ProcessingLegalBasisResponseDTO legalBasisDTO : legalBasisDTOS) {
                ProcessingLegalBasis processingLegalBasis = new ProcessingLegalBasis(legalBasisDTO.getName());
                processingLegalBasis.setOrganizationId(unitId);
                processingLegalBasisList.add(processingLegalBasis);
            }
            processingLegalBasisMongoRepository.saveAll(getNextSequence(processingLegalBasisList));

        }
    }

    private void saveProcessingPurposes(Long unitId, List<ProcessingPurposeResponseDTO> processingPurposeDTOS) {
        if (CollectionUtils.isNotEmpty(processingPurposeDTOS)) {
            List<ProcessingPurpose> processingPurposes = new ArrayList<>();
            for (ProcessingPurposeResponseDTO processingPurposeDTO : processingPurposeDTOS) {
                ProcessingPurpose processingPurpose = new ProcessingPurpose(processingPurposeDTO.getName());
                processingPurpose.setOrganizationId(unitId);
                processingPurposes.add(processingPurpose);
            }
            processingPurposeMongoRepository.saveAll(getNextSequence(processingPurposes));
        }
    }

    private void saveResponsibilityTypes(Long unitId, List<ResponsibilityTypeResponseDTO> responsibilityTypeDTOS) {
        if (CollectionUtils.isNotEmpty(responsibilityTypeDTOS)) {
            List<ResponsibilityType> responsibilityTypes = new ArrayList<>();
            for (ResponsibilityTypeResponseDTO responsibilityTypeDTO : responsibilityTypeDTOS) {
                ResponsibilityType responsibilityType = new ResponsibilityType(responsibilityTypeDTO.getName());
                responsibilityType.setOrganizationId(unitId);
                responsibilityTypes.add(responsibilityType);
            }
            responsibilityTypeMongoRepository.saveAll(getNextSequence(responsibilityTypes));
        }
    }

    private void saveTransferMethods(Long unitId, List<TransferMethodResponseDTO> transferMethodDTOS) {
        if (CollectionUtils.isNotEmpty(transferMethodDTOS)) {
            List<TransferMethod> transferMethods = new ArrayList<>();
            for (TransferMethodResponseDTO transferMethodResponseDTO : transferMethodDTOS) {
                TransferMethod transferMethod = new TransferMethod(transferMethodResponseDTO.getName());
                transferMethod.setOrganizationId(unitId);
                transferMethods.add(transferMethod);
            }
            transferMethodMongoRepository.saveAll(getNextSequence(transferMethods));
        }
    }


}


