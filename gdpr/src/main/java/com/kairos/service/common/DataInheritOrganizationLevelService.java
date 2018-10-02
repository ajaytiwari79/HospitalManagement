package com.kairos.service.common;


import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
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
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
import com.kairos.response.dto.common.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class DataInheritOrganizationLevelService extends MongoBaseService {


    @Autowired
    private ExecutorService executorService;
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


    public Boolean inheritDataFromParentOrganization(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaData) throws Exception {


        inheritAssetAndAssetMetaDataFromCountry(countryId, unitId, organizationMetaData);

        return true;

    }

    @Async
    public CompletableFuture<Boolean> inheritAssetAndAssetMetaDataFromCountry(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaDataDTO) throws Exception {


        Callable<List<DataDisposalResponseDTO>> inheritDataDisposalTask = () -> {
            return dataDisposalMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<DataDisposalResponseDTO>> futureDataDisposal = executorService.submit(inheritDataDisposalTask);
        if (futureDataDisposal.get() != null && CollectionUtils.isNotEmpty(futureDataDisposal.get())) {
            List<DataDisposal> dataDisposalsList = new ArrayList<>();
            for (DataDisposalResponseDTO dataDisposalDTO : futureDataDisposal.get()) {
                DataDisposal dataDisposal = new DataDisposal(dataDisposalDTO.getName());
                dataDisposal.setOrganizationId(unitId);
                dataDisposalsList.add(dataDisposal);
            }
            dataDisposalMongoRepository.saveAll(getNextSequence(dataDisposalsList));
        }


        Callable<List<HostingProviderResponseDTO>> inheritHostingProviderTask = () -> {
            return hostingProviderMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<HostingProviderResponseDTO>> futureHostingProviderTask = executorService.submit(inheritHostingProviderTask);
        if (futureHostingProviderTask.get() != null && CollectionUtils.isNotEmpty(futureHostingProviderTask.get())) {
            List<HostingProvider> hostingProviderList = new ArrayList<>();
            for (HostingProviderResponseDTO hostingProviderDTO : futureHostingProviderTask.get()) {
                HostingProvider hostingProvider = new HostingProvider(hostingProviderDTO.getName());
                hostingProvider.setOrganizationId(unitId);
                hostingProviderList.add(hostingProvider);
            }
            hostingProviderMongoRepository.saveAll(getNextSequence(hostingProviderList));

        }

        Callable<List<HostingTypeResponseDTO>> inheritHostingTypeTask = () -> {
            return hostingTypeMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<HostingTypeResponseDTO>> futureHostingTypeTask = executorService.submit(inheritHostingTypeTask);
        if (futureHostingTypeTask.get() != null && CollectionUtils.isNotEmpty(futureHostingTypeTask.get())) {
            List<HostingType> hostingTypeList = new ArrayList<>();
            for (HostingTypeResponseDTO hostingTypeDTO : futureHostingTypeTask.get()) {
                HostingType hostingType = new HostingType(hostingTypeDTO.getName());
                hostingType.setOrganizationId(unitId);
                hostingTypeList.add(hostingType);
            }
            hostingTypeMongoRepository.saveAll(getNextSequence(hostingTypeList));

        }

        Callable<List<OrganizationalSecurityMeasureResponseDTO>> inheritOrgSecurityMeasureTask = () -> {
            return organizationalSecurityMeasureMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<OrganizationalSecurityMeasureResponseDTO>> futureOrgSecurityMeasureTask = executorService.submit(inheritOrgSecurityMeasureTask);
        if (futureOrgSecurityMeasureTask.get() != null && CollectionUtils.isNotEmpty(futureOrgSecurityMeasureTask.get())) {
            List<OrganizationalSecurityMeasure> organizationalSecurityMeasureList = new ArrayList<>();
            for (OrganizationalSecurityMeasureResponseDTO orgSecurityMeasureDTO : futureOrgSecurityMeasureTask.get()) {
                OrganizationalSecurityMeasure organizationalSecurityMeasure = new OrganizationalSecurityMeasure(orgSecurityMeasureDTO.getName());
                organizationalSecurityMeasure.setOrganizationId(unitId);
                organizationalSecurityMeasureList.add(organizationalSecurityMeasure);
            }
            organizationalSecurityMeasureMongoRepository.saveAll(getNextSequence(organizationalSecurityMeasureList));
        }


        Callable<List<StorageFormatResponseDTO>> inheritStorageFormatTask = () -> {
            return storageFormatMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<StorageFormatResponseDTO>> futureStorageFormatTask = executorService.submit(inheritStorageFormatTask);
        if (futureStorageFormatTask.get() != null && CollectionUtils.isNotEmpty(futureStorageFormatTask.get())) {
            List<StorageFormat> storageFormatList = new ArrayList<>();
            for (StorageFormatResponseDTO storageFormatDTO : futureStorageFormatTask.get()) {
                StorageFormat storageFormat = new StorageFormat(storageFormatDTO.getName());
                storageFormat.setOrganizationId(unitId);
                storageFormatList.add(storageFormat);
            }
            storageFormatMongoRepository.saveAll(getNextSequence(storageFormatList));
        }

        Callable<List<TechnicalSecurityMeasureResponseDTO>> inheritTechnicalSecurityMeasureTask = () -> {
            return technicalSecurityMeasureMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<TechnicalSecurityMeasureResponseDTO>> futureTechSecurityMeasureTask = executorService.submit(inheritTechnicalSecurityMeasureTask);
        if (futureTechSecurityMeasureTask.get() != null && CollectionUtils.isNotEmpty(futureTechSecurityMeasureTask.get())) {
            List<TechnicalSecurityMeasure> technicalSecurityMeasures = new ArrayList<>();
            for (TechnicalSecurityMeasureResponseDTO technicalSecurityMeasureDTO : futureTechSecurityMeasureTask.get()) {
                TechnicalSecurityMeasure technicalSecurityMeasure = new TechnicalSecurityMeasure(technicalSecurityMeasureDTO.getName());
                technicalSecurityMeasure.setOrganizationId(unitId);
                technicalSecurityMeasures.add(technicalSecurityMeasure);
            }
            technicalSecurityMeasureMongoRepository.saveAll(getNextSequence(technicalSecurityMeasures));
        }


        return CompletableFuture.completedFuture(true);

    }


    @Async
    public CompletableFuture<Boolean> inheritProcessingActivityMetaDataFromCountry(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaDataDTO) throws Exception {


        Callable<List<AccessorPartyResponseDTO>> inheritAccessorPartyTask = () -> {
            return accessorPartyMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<AccessorPartyResponseDTO>> futureAccessorParties = executorService.submit(inheritAccessorPartyTask);
        if (futureAccessorParties.get() != null && CollectionUtils.isNotEmpty(futureAccessorParties.get())) {
            List<AccessorParty> accessorParties = new ArrayList<>();
            for (AccessorPartyResponseDTO accessorPartyDTO : futureAccessorParties.get()) {
                AccessorParty accessorParty = new AccessorParty(accessorPartyDTO.getName());
                accessorParty.setOrganizationId(unitId);
                accessorParties.add(accessorParty);
            }
            accessorPartyMongoRepository.saveAll(getNextSequence(accessorParties));
        }


        Callable<List<DataSourceResponseDTO>> inheritDataSourceTask = () -> {
            return dataSourceMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<DataSourceResponseDTO>> futureHostingProviderTask = executorService.submit(inheritDataSourceTask);
        if (futureHostingProviderTask.get() != null && CollectionUtils.isNotEmpty(futureHostingProviderTask.get())) {
            List<DataSource> dataSourceList = new ArrayList<>();
            for (DataSourceResponseDTO dataSourceDTO : futureHostingProviderTask.get()) {
                DataSource dataSource = new DataSource(dataSourceDTO.getName());
                dataSource.setOrganizationId(unitId);
                dataSourceList.add(dataSource);
            }
            dataSourceMongoRepository.saveAll(getNextSequence(dataSourceList));

        }

        Callable<List<ProcessingLegalBasisResponseDTO>> inheritLegalBasis = () -> {
            return processingLegalBasisMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<ProcessingLegalBasisResponseDTO>> futureLegalBasisTask = executorService.submit(inheritLegalBasis);
        if (futureLegalBasisTask.get() != null && CollectionUtils.isNotEmpty(futureLegalBasisTask.get())) {
            List<ProcessingLegalBasis> processingLegalBasisList = new ArrayList<>();
            for (ProcessingLegalBasisResponseDTO legalBasisDTO : futureLegalBasisTask.get()) {
                ProcessingLegalBasis processingLegalBasis = new ProcessingLegalBasis(legalBasisDTO.getName());
                processingLegalBasis.setOrganizationId(unitId);
                processingLegalBasisList.add(processingLegalBasis);
            }
            processingLegalBasisMongoRepository.saveAll(getNextSequence(processingLegalBasisList));

        }

        Callable<List<ProcessingPurposeResponseDTO>> inheritOrgSecurityMeasureTask = () -> {
            return processingPurposeMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<ProcessingPurposeResponseDTO>> futureOrgSecurityMeasureTask = executorService.submit(inheritOrgSecurityMeasureTask);
        if (futureOrgSecurityMeasureTask.get() != null && CollectionUtils.isNotEmpty(futureOrgSecurityMeasureTask.get())) {
            List<ProcessingPurpose> processingPurposes = new ArrayList<>();
            for (ProcessingPurposeResponseDTO processingPurposeDTO : futureOrgSecurityMeasureTask.get()) {
                ProcessingPurpose processingPurpose = new ProcessingPurpose(processingPurposeDTO.getName());
                processingPurpose.setOrganizationId(unitId);
                processingPurposes.add(processingPurpose);
            }
            processingPurposeMongoRepository.saveAll(getNextSequence(processingPurposes));
        }


        Callable<List<ResponsibilityTypeResponseDTO>> inheritResponsibilityTypeTask = () -> {
            return responsibilityTypeMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<ResponsibilityTypeResponseDTO>> futureResponsibilityTypeTask = executorService.submit(inheritResponsibilityTypeTask);
        if (futureResponsibilityTypeTask.get() != null && CollectionUtils.isNotEmpty(futureResponsibilityTypeTask.get())) {
            List<ResponsibilityType> responsibilityTypes = new ArrayList<>();
            for (ResponsibilityTypeResponseDTO responsibilityTypeDTO : futureResponsibilityTypeTask.get()) {
                ResponsibilityType responsibilityType = new ResponsibilityType(responsibilityTypeDTO.getName());
                responsibilityType.setOrganizationId(unitId);
                responsibilityTypes.add(responsibilityType);
            }
            responsibilityTypeMongoRepository.saveAll(getNextSequence(responsibilityTypes));
        }

        Callable<List<TransferMethodResponseDTO>> inheritTransferMethods = () -> {
            return transferMethodMongoRepository.findAllByCountryId(countryId);
        };
        Future<List<TransferMethodResponseDTO>> futureTransferMethodTask = executorService.submit(inheritTransferMethods);
        if (futureTransferMethodTask.get() != null && CollectionUtils.isNotEmpty(futureTransferMethodTask.get())) {
            List<TransferMethod> transferMethods = new ArrayList<>();
            for (TransferMethodResponseDTO transferMethodResponseDTO : futureTransferMethodTask.get()) {
                TransferMethod transferMethod = new TransferMethod(transferMethodResponseDTO.getName());
                transferMethod.setOrganizationId(unitId);
                transferMethods.add(transferMethod);
            }
            transferMethodMongoRepository.saveAll(getNextSequence(transferMethods));
        }


        Callable<List<TransferMethodResponseDTO>> inheritProcessingActivities = () -> {
            return masterProcessingActivityRepository.findAllMasterProcessingActivityByIds(countryId);
        };
        Future<List<TransferMethodResponseDTO>> futureProcessingActivityTask = executorService.submit(inheritProcessingActivities);
        if (futureTransferMethodTask.get() != null && CollectionUtils.isNotEmpty(futureTransferMethodTask.get())) {
            List<TransferMethod> transferMethods = new ArrayList<>();
            for (TransferMethodResponseDTO transferMethodResponseDTO : futureTransferMethodTask.get()) {
                TransferMethod transferMethod = new TransferMethod(transferMethodResponseDTO.getName());
                transferMethod.setOrganizationId(unitId);
                transferMethods.add(transferMethod);
            }
            transferMethodMongoRepository.saveAll(getNextSequence(transferMethods));
        }


        return CompletableFuture.completedFuture(true);

    }


}


