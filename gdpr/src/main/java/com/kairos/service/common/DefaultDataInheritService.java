package com.kairos.service.common;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.OrganizationTypeAndSubTypeIdDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.asset.AssetDeprecated;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.*;
import com.kairos.persistence.model.questionnaire_template.QuestionDeprecated;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSectionDeprecated;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplateDeprecated;
import com.kairos.persistence.model.risk_management.RiskDeprecated;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
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
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionBasicResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.AsynchronousService;
import com.kairos.service.data_subject_management.DataCategoryService;
import com.kairos.service.data_subject_management.DataSubjectMappingService;
import com.kairos.service.master_data.asset_management.AssetTypeService;
import com.kairos.service.questionnaire_template.QuestionnaireTemplateService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
public class DefaultDataInheritService{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataInheritService.class);

    @Inject
    private AsynchronousService asynchronousService;

    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;

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
    private TransferMethodRepository transferMethodMongoRepository;
    @Inject
    private DataSubjectMappingService dataSubjectMappingService;
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


    private Map<String, BigInteger> globalAssetTypeAndSubAssetTypeMap = new HashMap<>();
    private Map<String, BigInteger> globalCategoryNameAndIdMap = new HashMap<>();


    public boolean copyMasterDataFromCountry(Long unitId, OrgTypeSubTypeServiceCategoryVO orgTypeSubTypeServiceCategoryVO) throws Exception {

        Long countryId = orgTypeSubTypeServiceCategoryVO.getCountryId();
        OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO = new OrganizationTypeAndSubTypeIdDTO(Collections.singletonList(orgTypeSubTypeServiceCategoryVO.getId()),
                orgTypeSubTypeServiceCategoryVO.getOrganizationSubTypes().stream().map(OrganizationSubTypeDTO::getId).collect(Collectors.toList()),
                orgTypeSubTypeServiceCategoryVO.getOrganizationServices().stream().map(ServiceCategoryDTO::getId).collect(Collectors.toList()),
                orgTypeSubTypeServiceCategoryVO.getOrganizationSubServices().stream().map(SubServiceCategoryDTO::getId).collect(Collectors.toList()));
       /* List<AssetTypeRiskResponseDTO> assetTypeDTOS = assetTypeService.getAllAssetTypeWithSubAssetTypeAndRisk(countryId);
        List<DataCategoryResponseDTO> dataCategoryDTOS = dataCategoryService.getAllDataCategoryWithDataElementByCountryId(countryId);
        saveAssetTypeAndAssetSubType(unitId, assetTypeDTOS);
        copyDataCategoryAndDataElements(unitId, dataCategoryDTOS);*/


        List<Callable<Boolean>> callables = new ArrayList<>();
       Callable<Boolean> dataDisposalCreationlTask = () -> {
            List<DataDisposalResponseDTO> dataDisposalResponseDTOS = dataDisposalRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List dataDisposalMDS = prepareMetadataObjectList(unitId,dataDisposalResponseDTOS, DataDisposal.class);
            dataDisposalRepository.saveAll(dataDisposalMDS);
            return true;
        };
       Callable<Boolean> hostingProviderCreationTask = () -> {
            List<HostingProviderResponseDTO> hostingProviderDTOS = hostingProviderRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List hostingProviders = prepareMetadataObjectList(unitId,hostingProviderDTOS, HostingProvider.class);
            hostingProviderRepository.saveAll(hostingProviders);
            return true;
        };
         Callable<Boolean> hostingTypeCreationTask = () -> {
            List<HostingTypeResponseDTO> hostingTypeDTOS = hostingTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List hostingTypes = prepareMetadataObjectList(unitId,hostingTypeDTOS, HostingType.class);
            hostingTypeRepository.saveAll(hostingTypes);
            return true;

        };
        Callable<Boolean> storageFormatCreationTask = () -> {
            List<StorageFormatResponseDTO> storageFormatDTOS = storageFormatRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List storageFormats = prepareMetadataObjectList(unitId,storageFormatDTOS, StorageFormat.class);
            storageFormatRepository.saveAll(storageFormats);
            return true;

        };
        Callable<Boolean> technicalSecurityMeasureTask = () -> {

            List<TechnicalSecurityMeasureResponseDTO> techSecurityMeasureDTOS = technicalSecurityMeasureRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List techSecurityMeasures = prepareMetadataObjectList(unitId,techSecurityMeasureDTOS, TechnicalSecurityMeasure.class);
            technicalSecurityMeasureRepository.saveAll(techSecurityMeasures);
            return true;

        };
        Callable<Boolean> orgSecurityMeasureTask = () -> {
            List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasureDTOS = organizationalSecurityMeasureRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List orgSecurityMeasures = prepareMetadataObjectList(unitId,orgSecurityMeasureDTOS, OrganizationalSecurityMeasure.class);
            organizationalSecurityMeasureRepository.saveAll(orgSecurityMeasures);
            return true;
        };

        Callable<Boolean> accessorPartyTask = () -> {
            List<AccessorPartyResponseDTO> accessorPartyDTOS = accessorPartyRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List accessorParties = prepareMetadataObjectList(unitId,accessorPartyDTOS, AccessorParty.class);
            accessorPartyRepository.saveAll(accessorParties);
            return true;
        };

        Callable<Boolean> dataSourceTask = () -> {
            List<DataSourceResponseDTO> dataSourceDTOS = dataSourceRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List dataSources = prepareMetadataObjectList(unitId,dataSourceDTOS, DataSource.class);
            dataSourceRepository.saveAll(dataSources);
            return true;
        };
       Callable<Boolean> legalBasisTask = () -> {
            List<ProcessingLegalBasisResponseDTO> legalBasisDTOS = processingLegalBasisRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
           List legalBasisList = prepareMetadataObjectList(unitId,legalBasisDTOS, ProcessingLegalBasis.class);
           processingLegalBasisRepository.saveAll(legalBasisList);
            return true;
        };
        Callable<Boolean> processingPurposeTask = () -> {
            List<ProcessingPurposeResponseDTO> processingPurposeDTOS = processingPurposeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List processingPurposes = prepareMetadataObjectList(unitId,processingPurposeDTOS, ProcessingPurpose.class);
            processingPurposeRepository.saveAll(processingPurposes);
            return true;
        };
         Callable<Boolean> responsibilityTypeTask = () -> {
            List<ResponsibilityTypeResponseDTO> responsibilityTypeDTOS = responsibilityTypeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
             List responsibilityTypes = prepareMetadataObjectList(unitId,responsibilityTypeDTOS, ResponsibilityType.class);
             responsibilityTypeRepository.saveAll(responsibilityTypes);
            return true;
        };
        Callable<Boolean> transferMethodTask = () -> {
            List<TransferMethodResponseDTO> transferMethodDTOS = transferMethodMongoRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
            List transferMethods = prepareMetadataObjectList(unitId,transferMethodDTOS, TransferMethod.class);
            responsibilityTypeRepository.saveAll(transferMethods);
            return true;
        };
        Callable<Boolean> processingActivityTask = () -> {
            List<MasterProcessingActivity> masterProcessingActivityDTOS = masterProcessingActivityRepository.findAllByCountryIdAndOrganizationalMetadata(countryId, organizationMetaDataDTO.getOrganizationTypeId(),organizationMetaDataDTO.getOrganizationSubTypeIds(), organizationMetaDataDTO.getServiceCategoryIds(), organizationMetaDataDTO.getSubServiceCategoryIds());
            copyProcessingActivityAndSubProcessingActivitiesFromCountryToUnit(unitId, masterProcessingActivityDTOS);
            return true;
        };
        /*Callable<Boolean> questionniareTemplateTask = () -> {
            List<QuestionnaireTemplateResponseDTO> questionnaireTemplateDTOS = questionnaireTemplateService.getAllQuestionnaireTemplateByCountryIdOrOrganizationId(countryId);
            copyQuestionnaireTemplateFromCountry(unitId, questionnaireTemplateDTOS);
            return true;
        };
        Callable<Boolean> assetTask = () -> {
            List<MasterAssetResponseDTO> masterAssetDTOS = masterAssetMongoRepository.getMasterAssetByOrgTypeSubTypeCategoryAndSubCategory(countryId, organizationMetaDataDTO);
            copyMasterAssetAndAssetTypeFromCountryToUnit(unitId, masterAssetDTOS);
            return true;
        };*/
        Callable<Boolean> dataSubjectTask = () -> {
            List<DataSubjectMappingResponseDTO> dataSubjectMappingDTOS = dataSubjectMappingService.getAllDataSubjectWithDataCategoryByCountryId(countryId, false);
            copyDataSubjectAndDataCategoryFromCountry(unitId, dataSubjectMappingDTOS);
            return true;
        };
        Callable<Boolean> clauseTask = () -> {
            List<Clause> clauses = clauseRepository.getClauseByCountryIdAndOrgTypeSubTypeCategoryAndSubCategory(countryId, organizationMetaDataDTO.getOrganizationTypeId(),organizationMetaDataDTO.getOrganizationSubTypeIds(), organizationMetaDataDTO.getServiceCategoryIds(), organizationMetaDataDTO.getSubServiceCategoryIds());
            copyClauseFromCountry(unitId, clauses);
            return true;
        };


//        callables.add(technicalSecurityMeasureTask);
//        callables.add(storageFormatTask);
//        callables.add(orgSecurityMeasureTask);
//        callables.add(accessorPartyTask);
//        callables.add(dataSourceTask);
//        callables.add(legalBasisTask);
//        callables.add(processingPurposeTask);
//        callables.add(responsibilityTypeTask);
//        callables.add(transferMethodTask);
 //       callables.add(processingActivityTask);
//        callables.add(questionniareTemplateTask);
//        callables.add(assetTask);
        callables.add(dataSubjectTask);
 //       callables.add(clauseTask);
        //callables.add(dataDisposalCreationlTask);
        /*callables.add(hostingProviderCreationTask);
        callables.add(hostingTypeCreationTask);*/
        asynchronousService.executeAsynchronously(callables);
        return true;
    }


    private void copyMasterAssetAndAssetTypeFromCountryToUnit(Long unitId, List<MasterAssetResponseDTO> masterAssetDTOS) {
        if (CollectionUtils.isNotEmpty(masterAssetDTOS)) {
            List<AssetDeprecated> assets = new ArrayList<>();
            for (MasterAssetResponseDTO masterAssetDTO : masterAssetDTOS) {
                AssetDeprecated asset = new AssetDeprecated(masterAssetDTO.getName(), masterAssetDTO.getDescription(), false);
               // asset.setOrganizationId(unitId);
                AssetTypeBasicResponseDTO assetTypeBasicDTO = masterAssetDTO.getAssetType();
//                asset.setAssetTypeId(globalAssetTypeAndSubAssetTypeMap.get(assetTypeBasicDTO.getName().trim().toLowerCase()));
                if (Optional.of(masterAssetDTO.getAssetSubType()).isPresent()) {
                   // asset.setAssetSubTypeId(globalAssetTypeAndSubAssetTypeMap.get(masterAssetDTO.getAssetSubType().getName().toLowerCase().trim()));
                }
                assets.add(asset);
            }
           // assetMongoRepository.saveAll(getNextSequence(assets));
        }
    }

    private void copyClauseFromCountry(Long unitId, List<Clause> clauses) {
        if (CollectionUtils.isNotEmpty(clauses)) {

            Set<Long> clauseTagIds = new HashSet<>();
            List<Clause> clauseList = new ArrayList<>();
            clauses.forEach(clauseResponse -> {
                Clause clause = new Clause(clauseResponse.getTitle(), clauseResponse.getDescription());
                clause.setOrganizationId(unitId);
                List<ClauseTag> tags = new ArrayList<>();
                clauseResponse.getTags().forEach(clauseTag -> {
                    if (!clauseTagIds.contains(clauseTag.getId())) {
                        ClauseTag tag = new ClauseTag(clauseTag.getName());
                        tag.setOrganizationId(unitId);
                        tag.setDefaultTag(clauseTag.isDefaultTag());
                        clauseTagIds.add(clauseTag.getId());
                        tags.add(tag);
                    }
                });
                clause.setTags(tags);
                clauseList.add(clause);
            });
           // clauseTagRepository.saveAll(clauseTags);
            clauseRepository.saveAll(clauseList);
        }

    }


    private void copyProcessingActivityAndSubProcessingActivitiesFromCountryToUnit(Long unitId, List<MasterProcessingActivity> masterProcessingActivities) {

        if (CollectionUtils.isNotEmpty(masterProcessingActivities)) {
            List<ProcessingActivity> unitLevelProcessingActivities = prepareProcessingActivityAndSubProcessingActivityBasicDataOnly(unitId, masterProcessingActivities, false);
            processingActivityRepository.saveAll(unitLevelProcessingActivities);
        }
    }

    private List<ProcessingActivity> prepareProcessingActivityAndSubProcessingActivityBasicDataOnly(Long unitId, List<MasterProcessingActivity> masterProcessingActivities, boolean isSubProcessingActivity){
        List<ProcessingActivity> processingActivityList = new ArrayList<>();
        for (MasterProcessingActivity masterProcessingActivity : masterProcessingActivities) {
            ProcessingActivity processingActivity = new ProcessingActivity(masterProcessingActivity.getName(), masterProcessingActivity.getDescription(), false);
            processingActivity.setSubProcessingActivity(isSubProcessingActivity);
            processingActivity.setOrganizationId(unitId);
            if(!masterProcessingActivity.getSubProcessingActivities().isEmpty() && masterProcessingActivity.isHasSubProcessingActivity() == true) {
                processingActivity.setSubProcessingActivities(prepareProcessingActivityAndSubProcessingActivityBasicDataOnly(unitId, masterProcessingActivity.getSubProcessingActivities(), true));
            }
            processingActivityList.add(processingActivity);
        }
        return processingActivityList;
    }


    private void copyDataCategoryAndDataElements(Long unitId, List<DataCategoryResponseDTO> dataCategoryDTOS) {
        if (CollectionUtils.isNotEmpty(dataCategoryDTOS)) {
            List<DataCategory> dataCategories = ObjectMapperUtils.copyPropertiesOfListByMapper(dataCategoryDTOS, DataCategory.class);

            dataCategories.forEach(dataCategory -> {
                dataCategory.setOrganizationId(unitId);
                dataCategory.setCountryId(null);
                dataCategory.getDataElements().forEach( dataElement -> {
                    dataElement.setOrganizationId(unitId);
                    dataElement.setCountryId(null);
                });
            });
            dataCategoryRepository.saveAll(dataCategories);
        }
    }


    private void copyDataSubjectAndDataCategoryFromCountry(Long unitId, List<DataSubjectMappingResponseDTO> dataSubjectMappingResponseDTOS) {
        if (CollectionUtils.isNotEmpty(dataSubjectMappingResponseDTOS)) {
            List<DataSubjectMapping> dataSubjects = new ArrayList<>();
            for (DataSubjectMappingResponseDTO dataSubjectDTO : dataSubjectMappingResponseDTOS) {
                DataSubjectMapping dataSubjectMapping = new DataSubjectMapping(dataSubjectDTO.getName(), dataSubjectDTO.getDescription());
                dataSubjectMapping.setOrganizationId(unitId);
                if (CollectionUtils.isNotEmpty(dataSubjectDTO.getDataCategories())) {
                    List<DataCategory> dataCategories = new ArrayList<>();
                    dataSubjectDTO.getDataCategories().forEach( dataCategory ->{
                        List<DataElement> dataElements = new ArrayList<>();
                        DataCategory newDataCategory = new DataCategory(dataCategory.getName());
                        newDataCategory.setOrganizationId(unitId);
                        dataCategory.getDataElements().forEach( dataElement -> {
                                DataElement newDataElement = new DataElement(dataElement.getName());
                                newDataElement.setOrganizationId(unitId);
                            dataElements.add(newDataElement);
                        });
                        newDataCategory.setDataElements(dataElements);
                        dataCategories.add(newDataCategory);
                    });
                    dataCategoryRepository.saveAll(dataCategories);
                    dataSubjectMapping.setDataCategories(dataCategories);
                }
                dataSubjects.add(dataSubjectMapping);
            }
            dataSubjectRepository.saveAll(dataSubjects);
        }

    }


    private void copyQuestionnaireTemplateFromCountry(Long unitId, List<QuestionnaireTemplateResponseDTO> questionnaireTemplateDTOS) {


        Map<QuestionnaireTemplateDeprecated, List<QuestionnaireSectionDeprecated>> questionnaireTemplateAndSectionListMap = new HashMap<>();
        Map<QuestionnaireSectionDeprecated, List<QuestionDeprecated>> questionnaireSectionAndQuestionListMap = new HashMap<>();


        for (QuestionnaireTemplateResponseDTO questionnaireTemplateDTO : questionnaireTemplateDTOS) {

            QuestionnaireTemplateDeprecated questionnaireTemplate = buildQuestionnaireTemplate(unitId, questionnaireTemplateDTO);
            List<QuestionnaireSectionDeprecated> questionnaireSections = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(questionnaireTemplateDTO.getSections())) {
                for (QuestionnaireSectionResponseDTO questionnaireSectionDTO : questionnaireTemplateDTO.getSections()) {
                    QuestionnaireSectionDeprecated questionnaireSection = new QuestionnaireSectionDeprecated(questionnaireSectionDTO.getTitle());
                    //questionnaireSection.setOrganizationId(unitId);
                    questionnaireSections.add(questionnaireSection);
                    if (CollectionUtils.isNotEmpty(questionnaireSectionDTO.getQuestions())) {
                        List<QuestionDeprecated> questions = new ArrayList<>();
                        for (QuestionBasicResponseDTO questionBasicDTO : questionnaireSectionDTO.getQuestions()) {
                            QuestionDeprecated question = new QuestionDeprecated(questionBasicDTO.getQuestion(), questionBasicDTO.getDescription(), questionBasicDTO.isRequired(), questionBasicDTO.getQuestionType(), questionBasicDTO.isNotSureAllowed());
                           // question.setOrganizationId(unitId);
                            questions.add(question);
                        }
                        questionnaireSectionAndQuestionListMap.put(questionnaireSection, questions);
                    }
                }
                questionnaireTemplateAndSectionListMap.put(questionnaireTemplate, questionnaireSections);
            }
        }

        saveQuestionAndAddToQuestionnaireSection(questionnaireSectionAndQuestionListMap);
        saveQuestionnaireSectionAndAddToQuestionnaireTemplate(questionnaireTemplateAndSectionListMap);
        List<QuestionnaireTemplateDeprecated> questionnaireTemplates = new ArrayList<>(questionnaireTemplateAndSectionListMap.keySet());
       // questionnaireTemplateRepository.saveAll(questionnaireTemplates);
    }


    private void saveQuestionAndAddToQuestionnaireSection(Map<QuestionnaireSectionDeprecated, List<QuestionDeprecated>> questionnaireSectionListMap) {

        if (CollectionUtils.isNotEmpty(questionnaireSectionListMap.keySet())) {
            List<QuestionDeprecated> questionList = new ArrayList<>();
            questionnaireSectionListMap.forEach((questionnaireSection, questions) -> questionList.addAll(questions));
            //questionMongoRepository.saveAll(questionList);
           // questionnaireSectionListMap.forEach((questionnaireSection, questions) -> questionnaireSection.setQuestions(questions.stream().map(QuestionM::getId).collect(Collectors.toList())));
        }
    }


    private void saveQuestionnaireSectionAndAddToQuestionnaireTemplate(Map<QuestionnaireTemplateDeprecated, List<QuestionnaireSectionDeprecated>> questionnaireTemplateAndSectionListMap) {
        if (CollectionUtils.isNotEmpty(questionnaireTemplateAndSectionListMap.keySet())) {
            List<QuestionnaireSectionDeprecated> questionnaireSectionList = new ArrayList<>();
            questionnaireTemplateAndSectionListMap.forEach((questionnaireTemplate, questionnaireSections) -> questionnaireSectionList.addAll(questionnaireSections));
           // questionnaireSectionRepository.saveAll(questionnaireSectionList);
            //questionnaireTemplateAndSectionListMap.forEach((questionnaireTemplate, questionnaireSections) -> questionnaireTemplate.setSections(questionnaireSections.stream().map(QuestionnaireSection::getId).collect(Collectors.toList())));
        }
    }


    private QuestionnaireTemplateDeprecated buildQuestionnaireTemplate(Long unitId, QuestionnaireTemplateResponseDTO questionnaireTemplateDTO) {

        QuestionnaireTemplateDeprecated questionnaireTemplate = new QuestionnaireTemplateDeprecated(questionnaireTemplateDTO.getName(), questionnaireTemplateDTO.getDescription(), QuestionnaireTemplateStatus.DRAFT);
        //questionnaireTemplate.setOrganizationId(unitId);
        switch (questionnaireTemplateDTO.getTemplateType()) {
            case ASSET_TYPE:
                if (questionnaireTemplateDTO.isDefaultAssetTemplate()) {
                    questionnaireTemplate.setDefaultAssetTemplate(true);
                } else {
                    questionnaireTemplate.setAssetTypeId(globalAssetTypeAndSubAssetTypeMap.get(questionnaireTemplateDTO.getAssetType().getName().trim().toLowerCase()));
                    if (Optional.ofNullable(questionnaireTemplateDTO.getAssetSubType()).isPresent()) {
                        questionnaireTemplate.setAssetSubTypeId(globalAssetTypeAndSubAssetTypeMap.get(questionnaireTemplateDTO.getAssetSubType().getName().toLowerCase().trim()));
                    }
                }
                break;
            default:
                questionnaireTemplate.setTemplateType(questionnaireTemplateDTO.getTemplateType());
                break;
        }


        return questionnaireTemplate;

    }


    private void saveDataDisposal(Long unitId, List<DataDisposalResponseDTO> dataDisposalDTOS) {
        List<DataDisposal> dataDisposalsList = new ArrayList<>();
         for (DataDisposalResponseDTO dataDisposalDTO : dataDisposalDTOS) {
            DataDisposal dataDisposal = new DataDisposal(dataDisposalDTO.getName());
             dataDisposal.setOrganizationId(unitId);
             dataDisposalsList.add(dataDisposal);
         }
        dataDisposalRepository.saveAll(dataDisposalsList);
        }

    private <T extends Object> List<BaseEntity> prepareMetadataObjectList(Long unitId, List<T> metadataDTOList, Class entityClass) {
        List<BaseEntity> baseEntityList = new ArrayList<>();
        try {
            Class[] argumentType = { String.class, Long.class };
            if (!metadataDTOList.isEmpty()) {
                Class dtoClass = metadataDTOList.get(0).getClass();
                for (T dataDisposalDTO : metadataDTOList) {
                    String name = (String)new PropertyDescriptor("name", dtoClass).getReadMethod().invoke(dataDisposalDTO);
                    Constructor<?> cons = entityClass.getConstructor(argumentType);
                    baseEntityList.add((BaseEntity)cons.newInstance(name,unitId));
                }
            }
        }catch (Exception ex){
                LOGGER.error("Error in constructing the objects::"+ex.getMessage());
        }
        return baseEntityList;
    }

    /*
    private  <T extends BaseEntity>  List saveMetaData(Long unitId, List metadataDTOList,Class metadataEntity) {
        List metadataEntityList = new ArrayList();
        if (CollectionUtils.isNotEmpty(metadataDTOList)) {
            try {
                    Class entityClass = metadataEntity;
                    String countrySetterMethodName = "setCountryId";
                    String organizationSetterMethodName = "setOrganizationId";

                    Method countrySetterMethod = entityClass.getDeclaredMethod(countrySetterMethodName);
                    Method organizationSetterMethod = entityClass.getDeclaredMethod(organizationSetterMethodName);
                     metadataEntityList = ObjectMapperUtils.copyPropertiesOfListByMapper(metadataDTOList, entityClass);
                    metadataEntityList.forEach(metadata -> {
                        try {
                            countrySetterMethod.invoke(metadata, (Object) null);
                            organizationSetterMethod.invoke(metadata, unitId);
                        } catch (Exception ex) {
                            LOGGER.error("Exception while invoking setter methods of metadata using reflection ::" + ex.getMessage());
                        }

                    });
                    //customGenericRepository.saveAll(metadataEntityList);

            }
            catch(Exception e){
                    e.printStackTrace();
                    LOGGER.error("Exception while updating country and organizationId of metadata using reflection ::" + e.getMessage());
                    throw new RuntimeException(e);
                }


        }
        return  metadataEntityList;
    }
*/


    private void saveAssetTypeAndAssetSubType(Long unitId, List<AssetTypeRiskResponseDTO> assetTypeDTOS) {

        if (CollectionUtils.isNotEmpty(assetTypeDTOS)) {
            List<AssetType> assetTypes = ObjectMapperUtils.copyPropertiesOfListByMapper(assetTypeDTOS, AssetType.class);
            assetTypes = updateOrganizationIdAndCountryIdOfAssetTypeAndMetaData(assetTypes, unitId);
            assetTypeRepository.saveAll(assetTypes);

        }
    }

    private List<AssetType> updateOrganizationIdAndCountryIdOfAssetTypeAndMetaData(List<AssetType> assetTypes, Long unitId){
        assetTypes.forEach(assetType -> {
            assetType.setOrganizationId(unitId);
            assetType.setCountryId(null);
            assetType.getRisks().forEach(assetTypeRisk -> {
                assetTypeRisk.setOrganizationId(unitId);
                assetTypeRisk.setCountryId(null);
            });
            updateOrganizationIdAndCountryIdOfAssetTypeAndMetaData( assetType.getSubAssetTypes(), unitId);

        });
        return assetTypes;
    }


    private List<RiskDeprecated> buildRisks(Long unitId, List<RiskBasicResponseDTO> riskDTOS) {

        List<RiskDeprecated> risks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(riskDTOS)) {
            riskDTOS.forEach(riskDTO -> {
                RiskDeprecated risk = new RiskDeprecated(riskDTO.getName(), riskDTO.getDescription(), riskDTO.getRiskRecommendation(), riskDTO.getRiskLevel());
                //risk.setOrganizationId(unitId);
                risks.add(risk);
            });
        }
        return risks;

    }


}


