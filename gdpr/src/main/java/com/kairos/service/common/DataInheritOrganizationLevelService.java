package com.kairos.service.common;


import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.*;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
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
import com.kairos.persistence.repository.risk_management.RiskMongoRepository;
import com.kairos.response.dto.common.*;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.AsynchronousService;
import com.kairos.service.master_data.data_category_element.DataSubjectMappingService;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
    @Inject
    private RiskMongoRepository riskMongoRepository;
    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;


    private Map<String, BigInteger> globalAssetTypeAndSubAssetTypeMap = new HashMap<>();


    public Boolean copyDataFromCountryToUnitIdOnOnBoarding(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaDataDTO) throws Exception {


        //  inheritMetaDataOfAssetAndProcessingActivityMetaDataAndProcessingActivity(countryId, unitId, organizationMetaDataDTO);
        List<MasterAssetResponseDTO> masterAssetDTOS = masterAssetMongoRepository.getMasterAssetByOrgTypeSubTypeCategoryAndSubCategory(countryId, organizationMetaDataDTO);
        copyMasterAssetAndAssetTypeFromCountryToUnit(unitId, masterAssetDTOS);
        return true;

    }


    public void inheritMetaDataOfAssetAndProcessingActivityMetaDataAndProcessingActivity(Long countryId, Long unitId, OrganizationMetaDataDTO organizationMetaDataDTO) throws Exception {

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
        Callable<Boolean> processingActivityTask = () -> {
            List<MasterProcessingActivityResponseDTO> masterProcessingActivityDTOS = masterProcessingActivityRepository.getMasterProcessingActivityByOrgTypeSubTypeCategoryAndSubCategory(countryId, organizationMetaDataDTO);
            copyProcessingActivityAndSubProcessingActivitiesFromCountryToUnit(unitId, masterProcessingActivityDTOS);
            return true;
        };
        Callable<Boolean> assetTypeInheritTask = () -> {
            List<AssetTypeRiskResponseDTO> assetTypeDTOS = assetTypeMongoRepository.getAllAssetTypeWithSubAssetTypeAndRiskByCountryId(countryId);
            saveAssetTypeAndAssetSubType(unitId, assetTypeDTOS);
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
        callables.add(processingActivityTask);
        callables.add(assetTypeInheritTask);
        asynchronousService.executeAsynchronously(callables);
    }


    private void copyMasterAssetAndAssetTypeFromCountryToUnit(Long unitId, List<MasterAssetResponseDTO> masterAssetDTOS) {
        if (CollectionUtils.isNotEmpty(masterAssetDTOS)) {
            List<Asset> assets = new ArrayList<>();
            for (MasterAssetResponseDTO masterAssetDTO : masterAssetDTOS) {
                Asset asset = new Asset(masterAssetDTO.getName(), masterAssetDTO.getDescription(), false);
                asset.setOrganizationId(unitId);
                if (Optional.ofNullable(masterAssetDTO.getAssetType()).isPresent()) {
                    AssetTypeBasicResponseDTO assetTypeBasicDTO = masterAssetDTO.getAssetType();
                    asset.setAssetType(globalAssetTypeAndSubAssetTypeMap.get(assetTypeBasicDTO.getName().trim().toLowerCase()));
                    if (CollectionUtils.isNotEmpty(masterAssetDTO.assetSubTypes)) {
                        List<BigInteger> subAssetTypeIds = new ArrayList<>();
                        masterAssetDTO.assetSubTypes.forEach(subAssetType -> subAssetTypeIds.add(globalAssetTypeAndSubAssetTypeMap.get(subAssetType.getName().toLowerCase().trim())));
                    }
                }
                assets.add(asset);
            }
            assetMongoRepository.saveAll(getNextSequence(assets));
        }


    }

    private void copyProcessingActivityAndSubProcessingActivitiesFromCountryToUnit(Long unitId, List<MasterProcessingActivityResponseDTO> masterProcessingActivityDTOS) {

        if (CollectionUtils.isNotEmpty(masterProcessingActivityDTOS)) {
            List<ProcessingActivity> processingActivities = new ArrayList<>();
            Map<ProcessingActivity, List<ProcessingActivity>> processingActivitySubProcessingActivityListMap = new HashMap<>();
            for (MasterProcessingActivityResponseDTO masterProcessingActivityDTO : masterProcessingActivityDTOS) {
                ProcessingActivity processingActivity = new ProcessingActivity(masterProcessingActivityDTO.getName(), masterProcessingActivityDTO.getDescription(), false);
                processingActivity.setOrganizationId(unitId);
                List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(masterProcessingActivityDTO.getSubProcessingActivities())) {
                    for (MasterProcessingActivityResponseDTO subProcessingActivityDTO : masterProcessingActivityDTO.getSubProcessingActivities()) {
                        ProcessingActivity subProcessingActivity = new ProcessingActivity(subProcessingActivityDTO.getName(), subProcessingActivityDTO.getDescription(), false);
                        subProcessingActivity.setOrganizationId(unitId);
                        subProcessingActivities.add(subProcessingActivity);
                    }
                    processingActivities.addAll(subProcessingActivities);
                    processingActivitySubProcessingActivityListMap.put(processingActivity, subProcessingActivities);
                }
            }

            processingActivities.clear();
            if (processingActivities.isEmpty()) {
                processingActivityMongoRepository.saveAll(getNextSequence(processingActivities));
            }
            processingActivities = new ArrayList<>(processingActivitySubProcessingActivityListMap.keySet());
            processingActivitySubProcessingActivityListMap.forEach((processingActivity, subProcessingActivities) -> {
                if (CollectionUtils.isNotEmpty(subProcessingActivities)) {
                    processingActivity.setSubProcessingActivities(subProcessingActivities.stream().map(ProcessingActivity::getId).collect(Collectors.toList()));
                }
            });
            processingActivityMongoRepository.saveAll(getNextSequence(processingActivities));
        }
    }


    private void saveAssetTypeAndAssetSubType(Long unitId, List<AssetTypeRiskResponseDTO> assetTypeDTOS) {

        if (CollectionUtils.isNotEmpty(assetTypeDTOS)) {


            List<Risk> risks = new ArrayList<>();
            Map<AssetType, List<Risk>> assetTypeRiskMap = new HashMap<>();
            Map<AssetType, List<AssetType>> assetTypeAndSubAssetTypeMap = new HashMap<>();
            for (AssetTypeRiskResponseDTO assetTypeDTO : assetTypeDTOS) {
                AssetType assetType = new AssetType(assetTypeDTO.getName());
                assetType.setOrganizationId(unitId);
                assetTypeRiskMap.put(assetType, buildRisks(unitId, assetTypeDTO.getRisks()));
                if (CollectionUtils.isNotEmpty(assetTypeDTO.getSubAssetTypes())) {

                    List<AssetType> subAssetTypes = new ArrayList<>();
                    for (AssetTypeRiskResponseDTO subAssetTypeDTO : assetTypeDTO.getSubAssetTypes()) {
                        AssetType subAssetType = new AssetType(subAssetTypeDTO.getName());
                        subAssetType.setOrganizationId(unitId);
                        assetTypeRiskMap.put(subAssetType, buildRisks(unitId, subAssetTypeDTO.getRisks()));
                        subAssetTypes.add(subAssetType);
                    }
                    assetTypeAndSubAssetTypeMap.put(assetType, subAssetTypes);
                }
            }
            assetTypeRiskMap.forEach((assetType, riskList) -> risks.addAll(riskList));
            if (CollectionUtils.isNotEmpty(risks)) {
                riskMongoRepository.saveAll(getNextSequence(risks));
            }
            List<AssetType> assetSubTypes = new ArrayList<>();
            assetTypeAndSubAssetTypeMap.forEach((assetType, subAssetTypes) -> {
                assetType.setRisks(assetTypeRiskMap.get(assetType).stream().map(Risk::getId).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(subAssetTypes)) {
                    assetSubTypes.addAll(subAssetTypes);
                    subAssetTypes.stream().forEach(subAssetType -> subAssetType.setRisks(assetTypeRiskMap.get(subAssetType).stream().map(Risk::getId).collect(Collectors.toList())));
                }
            });
            if (CollectionUtils.isNotEmpty(assetSubTypes)) {
                assetTypeMongoRepository.saveAll(getNextSequence(assetSubTypes));
                assetSubTypes.forEach(subAssetType -> globalAssetTypeAndSubAssetTypeMap.put(subAssetType.getName().toLowerCase(), subAssetType.getId()));
            }
            List<AssetType> assetTypes = new ArrayList<>(assetTypeAndSubAssetTypeMap.keySet());
            assetTypes.forEach(assetType -> {
                globalAssetTypeAndSubAssetTypeMap.put(assetType.getName().toLowerCase(), assetType.getId());
                if (CollectionUtils.isNotEmpty(assetTypeAndSubAssetTypeMap.get(assetType))) {
                    assetType.setSubAssetTypes(assetTypeAndSubAssetTypeMap.get(assetType).stream().map(AssetType::getId).collect(Collectors.toList()));
                }
            });
            assetTypeMongoRepository.saveAll(getNextSequence(assetTypes));
        }
    }


    private List<Risk> buildRisks(Long unitId, List<RiskResponseDTO> riskDTOS) {

        List<Risk> risks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(riskDTOS)) {
            riskDTOS.forEach(riskDTO -> {
                Risk risk = new Risk(riskDTO.getName(), riskDTO.getDescription(), riskDTO.getRiskRecommendation(), riskDTO.getRiskLevel());
                risk.setOrganizationId(unitId);
                risks.add(risk);
            });
        }
        return risks;

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


