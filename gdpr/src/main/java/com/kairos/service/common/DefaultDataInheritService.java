package com.kairos.service.common;

import com.kairos.dto.gdpr.OrgTypeSubTypeServiceCategoryVO;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationTypeAndSubTypeIdDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause.OrganizationClause;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.model.master_data.data_category_element.DataSubject;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.*;
import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataCategoryRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateRepository;
import com.kairos.response.dto.common.*;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.AsynchronousService;
import com.kairos.service.data_subject_management.DataCategoryService;
import com.kairos.service.data_subject_management.DataSubjectService;
import com.kairos.service.master_data.asset_management.AssetTypeService;
import com.kairos.service.questionnaire_template.QuestionnaireTemplateService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

@SuppressWarnings("unchecked")
@Service
public class DefaultDataInheritService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataInheritService.class);

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;

    @Inject
    private MasterAssetRepository masterAssetRepository;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;
    @Inject
    private DataDisposalRepository dataDisposalRepository;
    @Inject
    private HostingProviderRepository hostingProviderRepository;
    @Inject
    private HostingTypeRepository hostingTypeRepository;
    @Inject
    private OrganizationalSecurityMeasureRepository organizationalSecurityMeasureRepository;
    @Inject
    private StorageFormatRepository storageFormatRepository;
    @Inject
    private TechnicalSecurityMeasureRepository technicalSecurityMeasureRepository;
    @Inject
    private AccessorPartyRepository accessorPartyRepository;
    @Inject
    private DataSourceRepository dataSourceRepository;
    @Inject
    private ProcessingLegalBasisRepository processingLegalBasisRepository;
    @Inject
    private ProcessingPurposeRepository processingPurposeRepository;
    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;
    @Inject
    private TransferMethodRepository transferMethodRepository;
    @Inject
    private DataSubjectService dataSubjectService;
    @Inject
    private DataCategoryRepository dataCategoryRepository;

    @Inject
    private AssetTypeRepository assetTypeRepository;
    @Inject
    private QuestionnaireTemplateService questionnaireTemplateService;

    @Inject
    private QuestionnaireTemplateRepository questionnaireTemplateRepository;

    @Inject
    private DataSubjectRepository dataSubjectRepository;
    @Inject
    private ClauseTagRepository clauseTagRepository;
    @Inject
    private ClauseRepository clauseRepository;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private DataCategoryService dataCategoryService;


    public boolean copyMasterDataFromCountry(Long unitId, OrgTypeSubTypeServiceCategoryVO orgTypeSubTypeServiceCategoryVO) throws Exception {
        Long countryId = orgTypeSubTypeServiceCategoryVO.getCountryId();
        OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO = new OrganizationTypeAndSubTypeIdDTO(Collections.singletonList(orgTypeSubTypeServiceCategoryVO.getId()), orgTypeSubTypeServiceCategoryVO.getOrganizationSubTypes().stream().map(OrganizationSubTypeDTO::getId).collect(Collectors.toList()), orgTypeSubTypeServiceCategoryVO.getOrganizationServices().stream().map(ServiceCategoryDTO::getId).collect(Collectors.toList()), orgTypeSubTypeServiceCategoryVO.getOrganizationSubServices().stream().map(SubServiceCategoryDTO::getId).collect(Collectors.toList()));
        Map<Long, AssetType> longAssetTypeMap = copyAssetTypeFromCountry(unitId, assetTypeRepository.getAllAssetTypeByCountryId(countryId));
        copyQuestionnaireTemplateFromCountry(unitId, questionnaireTemplateService.getAllQuestionnaireTemplateWithSectionOfCountryOrOrganization(countryId, false), longAssetTypeMap);
        List<Callable<Boolean>> callables = new ArrayList<>();
        callables.add(getDataDisposalCreationlTask(unitId, countryId));
        callables.add(getHostingProviderCreationTask(unitId, countryId));
        callables.add(getHostingTypeCreationTask(unitId, countryId));
        callables.add(getStorageFormatCreationTask(unitId, countryId));
        callables.add(getTechnicalSecurityMeasureTask(unitId, countryId));
        callables.add(getOrgSecurityMeasureTask(unitId, countryId));
        callables.add(getAccessorPartyTask(unitId, countryId));
        callables.add(getDataSourceTask(unitId, countryId));
        callables.add(getLegalBasisTask(unitId, countryId));
        callables.add(getProcessingPurposeTask(unitId, countryId));
        callables.add(getResponsibilityTypeTask(unitId, countryId));
        callables.add(getTransferMethodTask(unitId, countryId));
        callables.add(getProcessingActivityTask(unitId, countryId, organizationMetaDataDTO));
        callables.add(getAssetTask(unitId, countryId, organizationMetaDataDTO, longAssetTypeMap));
        callables.add(getDataSubjectTask(unitId, countryId));
        callables.add(getClauseTask(unitId, countryId, organizationMetaDataDTO));
        asynchronousService.executeAsynchronously(callables);
        return true;
    }

    private Callable<Boolean> getClauseTask(Long unitId, Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO) {
        return () -> {
            List<Clause> clauses = clauseRepository.getClauseByCountryIdAndOrgTypeSubTypeCategoryAndSubCategory(countryId, organizationMetaDataDTO.getOrganizationTypeId(), organizationMetaDataDTO.getOrganizationSubTypeIds(), organizationMetaDataDTO.getServiceCategoryIds(), organizationMetaDataDTO.getSubServiceCategoryIds());
            copyClauseFromCountry(unitId, clauses);
            return true;
        };
    }

    private Callable<Boolean> getDataSubjectTask(Long unitId, Long countryId) {
        return () -> {
            List<DataCategory> masterDataCategories = dataCategoryRepository.getAllDataCategoriesByCountryId(countryId);
            List<DataSubject> masterDataSubjects = dataSubjectRepository.getAllDataSubjectByCountryId(countryId);
            copyDataSubjectAndDataCategoryFromCountry(unitId, masterDataCategories, masterDataSubjects);
            return true;
        };
    }

    private Callable<Boolean> getAssetTask(Long unitId, Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO, Map<Long, AssetType> longAssetTypeMap) {
        return () -> {
            List<MasterAsset> masterAssets = masterAssetRepository.findAllByCountryIdAndOrganizationalMetadata(countryId, organizationMetaDataDTO.getOrganizationTypeId(), organizationMetaDataDTO.getOrganizationSubTypeIds(), organizationMetaDataDTO.getServiceCategoryIds(), organizationMetaDataDTO.getSubServiceCategoryIds());
            copyMasterAssetAndAssetTypeFromCountryToUnit(unitId, masterAssets, longAssetTypeMap);
            LOGGER.info(" asset creation on inheriting data ");
            return true;
        };
    }

    private Callable<Boolean> getProcessingActivityTask(Long unitId, Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO) {
        return () -> {
            List<MasterProcessingActivity> masterProcessingActivities = masterProcessingActivityRepository.findAllByCountryIdAndOrganizationalMetadata(countryId, organizationMetaDataDTO.getOrganizationTypeId(), organizationMetaDataDTO.getOrganizationSubTypeIds(), organizationMetaDataDTO.getServiceCategoryIds(), organizationMetaDataDTO.getSubServiceCategoryIds());
            copyProcessingActivityAndSubProcessingActivitiesFromCountryToUnit(unitId, masterProcessingActivities);
            return true;
        };
    }

    private Callable<Boolean> getTransferMethodTask(Long unitId, Long countryId) {
        return () -> {
            List<TransferMethodResponseDTO> transferMethodDTOS = transferMethodRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List transferMethods = prepareMetadataObjectList(unitId, transferMethodDTOS, TransferMethod.class);
            transferMethodRepository.saveAll(transferMethods);
            return true;
        };
    }

    private Callable<Boolean> getResponsibilityTypeTask(Long unitId, Long countryId) {
        return () -> {
            List<ResponsibilityTypeResponseDTO> responsibilityTypeDTOS = responsibilityTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List responsibilityTypes = prepareMetadataObjectList(unitId, responsibilityTypeDTOS, ResponsibilityType.class);
            responsibilityTypeRepository.saveAll(responsibilityTypes);
            return true;
        };
    }

    private Callable<Boolean> getProcessingPurposeTask(Long unitId, Long countryId) {
        return () -> {
            List<ProcessingPurposeResponseDTO> processingPurposeDTOS = processingPurposeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List processingPurposes = prepareMetadataObjectList(unitId, processingPurposeDTOS, ProcessingPurpose.class);
            processingPurposeRepository.saveAll(processingPurposes);
            return true;
        };
    }

    private Callable<Boolean> getLegalBasisTask(Long unitId, Long countryId) {
        return () -> {
            List<ProcessingLegalBasisResponseDTO> legalBasisDTOS = processingLegalBasisRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List legalBasisList = prepareMetadataObjectList(unitId, legalBasisDTOS, ProcessingLegalBasis.class);
            processingLegalBasisRepository.saveAll(legalBasisList);
            return true;
        };
    }

    private Callable<Boolean> getDataSourceTask(Long unitId, Long countryId) {
        return () -> {
            List<DataSourceResponseDTO> dataSourceDTOS = dataSourceRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List dataSources = prepareMetadataObjectList(unitId, dataSourceDTOS, DataSource.class);
            dataSourceRepository.saveAll(dataSources);
            return true;
        };
    }

    private Callable<Boolean> getAccessorPartyTask(Long unitId, Long countryId) {
        return () -> {
            List<AccessorPartyResponseDTO> accessorPartyDTOS = accessorPartyRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List accessorParties = prepareMetadataObjectList(unitId, accessorPartyDTOS, AccessorParty.class);
            accessorPartyRepository.saveAll(accessorParties);
            return true;
        };
    }

    private Callable<Boolean> getOrgSecurityMeasureTask(Long unitId, Long countryId) {
        return () -> {
            List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasureDTOS = organizationalSecurityMeasureRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List orgSecurityMeasures = prepareMetadataObjectList(unitId, orgSecurityMeasureDTOS, OrganizationalSecurityMeasure.class);
            organizationalSecurityMeasureRepository.saveAll(orgSecurityMeasures);
            return true;
        };
    }

    private Callable<Boolean> getTechnicalSecurityMeasureTask(Long unitId, Long countryId) {
        return () -> {

            List<TechnicalSecurityMeasureResponseDTO> techSecurityMeasureDTOS = technicalSecurityMeasureRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List techSecurityMeasures = prepareMetadataObjectList(unitId, techSecurityMeasureDTOS, TechnicalSecurityMeasure.class);
            technicalSecurityMeasureRepository.saveAll(techSecurityMeasures);
            return true;

        };
    }

    private Callable<Boolean> getStorageFormatCreationTask(Long unitId, Long countryId) {
        return () -> {
            List<StorageFormatResponseDTO> storageFormatDTOS = storageFormatRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List storageFormats = prepareMetadataObjectList(unitId, storageFormatDTOS, StorageFormat.class);
            storageFormatRepository.saveAll(storageFormats);
            return true;

        };
    }

    private Callable<Boolean> getHostingTypeCreationTask(Long unitId, Long countryId) {
        return () -> {
            List<HostingTypeResponseDTO> hostingTypeDTOS = hostingTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List hostingTypes = prepareMetadataObjectList(unitId, hostingTypeDTOS, HostingType.class);
            hostingTypeRepository.saveAll(hostingTypes);
            return true;

        };
    }

    private Callable<Boolean> getHostingProviderCreationTask(Long unitId, Long countryId) {
        return () -> {
            List<HostingProviderResponseDTO> hostingProviderDTOS = hostingProviderRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List hostingProviders = prepareMetadataObjectList(unitId, hostingProviderDTOS, HostingProvider.class);
            hostingProviderRepository.saveAll(hostingProviders);
            return true;
        };
    }

    private Callable<Boolean> getDataDisposalCreationlTask(Long unitId, Long countryId) {
        return () -> {
            List<DataDisposalResponseDTO> dataDisposalResponseDTOS = dataDisposalRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List dataDisposalMDS = prepareMetadataObjectList(unitId, dataDisposalResponseDTOS, DataDisposal.class);
            dataDisposalRepository.saveAll(dataDisposalMDS);
            return true;
        };
    }


    private void copyQuestionnaireTemplateFromCountry(Long unitId, List<QuestionnaireTemplateResponseDTO> masterQuestionnaireTemplates, Map<Long, AssetType> masterAssetTypeMapWithUnitAssetTypeObject) {
        List<QuestionnaireTemplate> unitQuestionnaireTemplates = new ArrayList<>();
        masterQuestionnaireTemplates.forEach(masterQuestionnaireTemplate -> {
            QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(masterQuestionnaireTemplate.getName(), masterQuestionnaireTemplate.getDescription(), masterQuestionnaireTemplate.getTemplateType(), QuestionnaireTemplateStatus.DRAFT, unitId);
            questionnaireTemplate.setDefaultAssetTemplate(masterQuestionnaireTemplate.isDefaultAssetTemplate());
            if (masterQuestionnaireTemplate.getTemplateType().equals(QuestionnaireTemplateType.ASSET_TYPE)) {
                if (!masterQuestionnaireTemplate.isDefaultAssetTemplate()) {
                    questionnaireTemplate.setAssetType(masterAssetTypeMapWithUnitAssetTypeObject.get(masterQuestionnaireTemplate.getAssetType().getId()));
                    Optional.ofNullable(masterQuestionnaireTemplate.getSubAssetType()).ifPresent(assetSubType -> questionnaireTemplate.setSubAssetType(masterAssetTypeMapWithUnitAssetTypeObject.get(assetSubType.getId())));
                } else {
                    questionnaireTemplate.setDefaultAssetTemplate(true);
                }
            }
            if (masterQuestionnaireTemplate.getTemplateType().equals(QuestionnaireTemplateType.RISK)) {
                if (masterQuestionnaireTemplate.getRiskAssociatedEntity().equals(QuestionnaireTemplateType.ASSET_TYPE)) {
                    questionnaireTemplate.setAssetType(masterAssetTypeMapWithUnitAssetTypeObject.get(masterQuestionnaireTemplate.getAssetType().getId()));
                    Optional.ofNullable(masterQuestionnaireTemplate.getSubAssetType()).ifPresent(assetSubType -> questionnaireTemplate.setSubAssetType(masterAssetTypeMapWithUnitAssetTypeObject.get(assetSubType.getId())));
                }
                questionnaireTemplate.setRiskAssociatedEntity(masterQuestionnaireTemplate.getRiskAssociatedEntity());

            }
            setSections(unitId, masterQuestionnaireTemplate, questionnaireTemplate);
            unitQuestionnaireTemplates.add(questionnaireTemplate);
        });
        questionnaireTemplateRepository.saveAll(unitQuestionnaireTemplates);
    }

    private void setSections(Long unitId, QuestionnaireTemplateResponseDTO masterQuestionnaireTemplate, QuestionnaireTemplate questionnaireTemplate) {
        if (CollectionUtils.isNotEmpty(masterQuestionnaireTemplate.getSections())) {
            questionnaireTemplate.setSections(
                    masterQuestionnaireTemplate.getSections().stream().map(questionnaireSectionResponseDTO -> {
                        QuestionnaireSection questionnaireSection = new QuestionnaireSection(questionnaireSectionResponseDTO.getTitle(), null, unitId);
                        questionnaireSection.setQuestions(questionnaireSectionResponseDTO.getQuestions().stream().map(questionBasicResponseDTO ->
                                new Question(questionBasicResponseDTO.getQuestion(), questionBasicResponseDTO.getDescription(), questionBasicResponseDTO.isRequired(), questionBasicResponseDTO.getQuestionType(), questionBasicResponseDTO.isNotSureAllowed(), null, unitId)
                        ).collect(Collectors.toList()));
                        return questionnaireSection;
                    }).collect(Collectors.toList())
            );
        }
    }


    private Map<Long, AssetType> copyAssetTypeFromCountry(Long unitId, List<AssetType> masterAssetTypes) {
        Map<Long, AssetType> longAssetTypeMap = new HashMap<>();
        List<AssetType> assetTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(masterAssetTypes)) {
            masterAssetTypes.forEach(masterAssetType -> {
                AssetType assetType = new AssetType(masterAssetType.getName(), unitId, false);
                if (CollectionUtils.isNotEmpty(masterAssetType.getSubAssetTypes())) {
                    List<AssetType> unitSubAssetTypes = new ArrayList<>();
                    masterAssetType.getSubAssetTypes().forEach(masterSubAssetType -> {
                        AssetType assetSubType = new AssetType(masterSubAssetType.getName(), unitId, true);
                        assetSubType.setRisks(buildRiskForAssetType(unitId, masterSubAssetType.getRisks()));
                        assetType.setHasSubAssetType(true);
                        unitSubAssetTypes.add(assetSubType);
                        longAssetTypeMap.put(masterSubAssetType.getId(), assetSubType);

                    });
                    assetType.setSubAssetTypes(unitSubAssetTypes);
                }
                assetType.setRisks(buildRiskForAssetType(unitId, masterAssetType.getRisks()));
                longAssetTypeMap.put(masterAssetType.getId(), assetType);
                assetTypes.add(assetType);
            });
        }
        assetTypeRepository.saveAll(assetTypes);
        return longAssetTypeMap;
    }

    private List<Risk> buildRiskForAssetType(Long unitId, List<Risk> risks) {
        return risks.stream().map(masterRisk -> {
            Risk risk = new Risk(masterRisk.getName(), masterRisk.getDescription(), masterRisk.getRiskRecommendation(), masterRisk.getRiskLevel());
            risk.setOrganizationId(unitId);
            return risk;
        }).collect(Collectors.toList());
    }


    private void copyMasterAssetAndAssetTypeFromCountryToUnit(Long unitId, List<MasterAsset> masterAssets, Map<Long, AssetType> longAssetTypeMap) {

        LOGGER.info("Data inheriting Master Asset Size : {}", masterAssets.size());
        try {
            List<Asset> unitLevelAssets = new ArrayList<>();
            masterAssets.forEach(masterAsset -> {
                Asset asset = new Asset();
                asset.setName(masterAsset.getName());
                asset.setDescription(masterAsset.getDescription());
                asset.setOrganizationId(unitId);
                if (longAssetTypeMap.containsKey(masterAsset.getAssetType().getId())) {
                    asset.setAssetType(longAssetTypeMap.get(masterAsset.getAssetType().getId()));
                }
                Optional.ofNullable(masterAsset.getSubAssetType()).ifPresent(assetType -> asset.setSubAssetType(longAssetTypeMap.get(assetType.getId())));
                unitLevelAssets.add(asset);
            });
            assetRepository.saveAll(unitLevelAssets);
            LOGGER.info("Data inheriting Unit Asset Size : {}", unitLevelAssets.size());
        } catch (Exception ex) {
            LOGGER.error("Error in asset processing== {}", ex.getMessage());
        }
    }

    private void copyClauseFromCountry(Long unitId, List<Clause> clauses) {
        if (CollectionUtils.isNotEmpty(clauses)) {
            ClauseTag defaultClauseTag = clauseTagRepository.findDefaultTag();
            Set<ClauseTag> clauseTags = new HashSet<>();
            List<Clause> clauseList = new ArrayList<>();
            clauses.forEach(clauseResponse -> {
                OrganizationClause clause = new OrganizationClause(clauseResponse.getTitle(), clauseResponse.getDescription(), unitId);
                clause.setTemplateTypes(clauseResponse.getTemplateTypes());
                Set<ClauseTag> tags = new HashSet<>();
                clauseResponse.getTags().forEach(clauseTag -> {
                    if (clauseTag.isDefaultTag()) {
                        tags.add(defaultClauseTag);
                    } else {
                        ClauseTag tag = new ClauseTag(clauseTag.getName());
                        tag.setOrganizationId(unitId);
                        tag.setDefaultTag(clauseTag.isDefaultTag());
                        tags.add(tag);
                    }
                });
                clause.setTags(new ArrayList<>(tags));
                clauseTags.addAll(tags);
                clauseList.add(clause);
            });
            clauseTagRepository.saveAll(clauseTags);
            clauseRepository.saveAll(clauseList);
        }

    }


    private void copyProcessingActivityAndSubProcessingActivitiesFromCountryToUnit(Long unitId, List<MasterProcessingActivity> masterProcessingActivities) {

        if (CollectionUtils.isNotEmpty(masterProcessingActivities)) {
            List<ProcessingActivity> unitLevelProcessingActivities = prepareProcessingActivityAndSubProcessingActivityBasicDataOnly(unitId, masterProcessingActivities);
            processingActivityRepository.saveAll(unitLevelProcessingActivities);
        }
    }

    private List<ProcessingActivity> prepareProcessingActivityAndSubProcessingActivityBasicDataOnly(Long unitId, List<MasterProcessingActivity> masterProcessingActivities) {
        List<ProcessingActivity> processingActivityList = new ArrayList<>();
        for (MasterProcessingActivity masterProcessingActivity : masterProcessingActivities) {
            ProcessingActivity processingActivity = new ProcessingActivity(masterProcessingActivity.getName(), masterProcessingActivity.getDescription());
            processingActivity.setSubProcessingActivity(masterProcessingActivity.isSubProcessActivity());
            processingActivity.setOrganizationId(unitId);
            if (CollectionUtils.isNotEmpty(masterProcessingActivity.getSubProcessingActivities())) {
                processingActivity.setSubProcessingActivities(masterProcessingActivity.getSubProcessingActivities().stream().map(masterSubProcessingActivity -> {
                    ProcessingActivity subProcessingActivity = new ProcessingActivity(masterSubProcessingActivity.getName(), masterSubProcessingActivity.getDescription());
                    subProcessingActivity.setSubProcessingActivity(masterSubProcessingActivity.isSubProcessActivity());
                    subProcessingActivity.setOrganizationId(unitId);
                    subProcessingActivity.setRisks(buildRiskForAssetType(unitId, masterSubProcessingActivity.getRisks()));
                    subProcessingActivity.setProcessingActivity(processingActivity);
                    return subProcessingActivity;
                }).collect(Collectors.toList()));
            }
            processingActivity.setRisks(buildRiskForAssetType(unitId, masterProcessingActivity.getRisks()));
            processingActivityList.add(processingActivity);
        }
        return processingActivityList;
    }


    private void copyDataSubjectAndDataCategoryFromCountry(Long unitId, List<DataCategory> dataCategories, List<DataSubject> dataSubjects) {
        Map<Long, DataCategory> longDataCategoryMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dataCategories)) {
            List<DataCategory> unitDataCategories = dataCategories.stream().map(dataCategory -> {
                DataCategory unitDataCategory = new DataCategory(dataCategory.getName(), dataCategory.getDataElements().stream().map(dataElement -> new DataElement(dataElement.getName(), unitId)).collect(Collectors.toList()), unitId);
                longDataCategoryMap.put(dataCategory.getId(), unitDataCategory);
                return unitDataCategory;
            }).collect(Collectors.toList());
            dataCategoryRepository.saveAll(unitDataCategories);
        }
        if (CollectionUtils.isNotEmpty(dataSubjects)) {
            List<DataSubject> unitDataSubjects = dataSubjects.stream().map(dataSubject -> {
                DataSubject subject = new DataSubject(dataSubject.getName(), dataSubject.getDescription(), dataSubject.getDataCategories().stream().map(dataCategory -> longDataCategoryMap.get(dataCategory.getId())).collect(Collectors.toList()));
                subject.setOrganizationId(unitId);
                return subject;
            }).collect(Collectors.toList());
            dataSubjectRepository.saveAll(unitDataSubjects);
        }
    }


    private <T> List<BaseEntity> prepareMetadataObjectList(Long unitId, List<T> metadataDTOList, Class entityClass) {
        List<BaseEntity> baseEntityList = new ArrayList<>();
        try {
            Class[] argumentType = {String.class, Long.class};
            if (!metadataDTOList.isEmpty()) {
                Class dtoClass = metadataDTOList.get(0).getClass();
                for (T dto : metadataDTOList) {
                    String name = (String) new PropertyDescriptor("name", dtoClass).getReadMethod().invoke(dto);
                    Constructor<?> cons = entityClass.getConstructor(argumentType);
                    baseEntityList.add((BaseEntity) cons.newInstance(name, unitId));
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error in constructing the objects:: {}", ex.getMessage());
        }
        return baseEntityList;
    }

    public boolean copyMasterAssetToUnitAsset(Long countryId, Long unitId, List<Long> orgSubTypeId, Long orgSubServiceId) {
        List<MasterAsset> masterAssets = masterAssetRepository.findAllByCountryIdAndOrgSubTypeAndOrgSubService(countryId, orgSubTypeId, orgSubServiceId);
        if (isCollectionNotEmpty(masterAssets)) {
            Map<Long, AssetType> longAssetTypeMap = copyAssetTypeFromCountry(unitId, assetTypeRepository.getAllAssetTypeByCountryId(countryId));
            copyMasterAssetAndAssetTypeFromCountryToUnit(unitId, masterAssets, longAssetTypeMap);
        }
        return true;
    }
}


