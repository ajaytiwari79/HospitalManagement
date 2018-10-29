package com.kairos.service.questionnaire_template;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;


@Service
public class QuestionnaireTemplateService extends MongoBaseService {


    private Logger LOGGER = LoggerFactory.getLogger(QuestionnaireTemplateService.class);


    @Inject
    private QuestionnaireTemplateMongoRepository questionnaireTemplateMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private QuestionnaireSectionService questionnaireSectionService;

    @Inject
    private QuestionnaireSectionRepository questionnaireSectionRepository;

    @Inject
    private QuestionMongoRepository questionMongoRepository;


    /**
     * @param countryId
     * @param templateDto contain data of Questionnaire template
     * @return Object of Questionnaire template with template type and asset type if template type is(ASSET_TYPE_KEY)
     */
    public QuestionnaireTemplateDTO saveMasterQuestionnaireTemplate(Long countryId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousMasterTemplate = questionnaireTemplateMongoRepository.findByNameAndCountryId(countryId, templateDto.getName());
        if (Optional.ofNullable(previousMasterTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Master Questionnaire template", templateDto.getName());
        }
        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        addTemplateTypeToQuestionnaireTemplate(countryId, false, questionnaireTemplate, templateDto);
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        templateDto.setId(questionnaireTemplate.getId());
        return templateDto.setId(questionnaireTemplate.getId());
    }

    /**
     * @param templateDto
     * @param questionnaireTemplate
     */
    //todo add message
    private void addTemplateTypeToQuestionnaireTemplate(Long referenceId, boolean isUnitId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {

        switch (templateDto.getTemplateType()) {
            case ASSET_TYPE:
                questionnaireTemplate.setTemplateType(templateDto.getTemplateType());
                addAssetTypeAndSubAssetTypeToQuestionnaireTemplateOnTemplateTypeStatusAssetTypeSelection(referenceId, isUnitId, questionnaireTemplate, templateDto);
                break;
            case Risk:
                if (!Optional.ofNullable(templateDto.getRiskAssociatedEntity()).isPresent()) {
                    exceptionService.invalidRequestException("message.risk.questionnaireTemplate.associated.entity.not.selected");
                }
                addRiskAssociatedEntityTypeOnTemplateTypeStatusRiskSelection(referenceId, isUnitId, questionnaireTemplate, templateDto);
                break;
            default:
                QuestionnaireTemplate previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findPublishedQuestionnaireTemplateByUnitIdAndTemplateType(referenceId, templateDto.getTemplateType()) : questionnaireTemplateMongoRepository.findQuestionnaireTemplateByCountryIdAndTemplateType(referenceId, templateDto.getTemplateType());
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("duplicate.questionnaireTemplate.ofTemplateType", templateDto.getTemplateType());
                }
                questionnaireTemplate.setTemplateType(templateDto.getTemplateType());
                break;
        }
    }


    //todo add message
    private void addRiskAssociatedEntityTypeOnTemplateTypeStatusRiskSelection(Long referenceId, boolean isUnitId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousTemplate = null;
        if (QuestionnaireTemplateType.ASSET_TYPE.equals(templateDto.getRiskAssociatedEntity())) {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isUnitId ? assetTypeMongoRepository.findByIdAndUnitId(referenceId, templateDto.getAssetType()) : assetTypeMongoRepository.findByIdAndCountryId(referenceId, templateDto.getAssetType());
            if (CollectionUtils.isEmpty(assetType.getSubAssetTypes())) {
                previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findPublishedTemplateOfTemplateTypeRiskByUnitIdAndAssetTypeId(referenceId, templateDto.getAssetType())
                        : questionnaireTemplateMongoRepository.findQuestionnaireTemplateOfTemplateTypeRiskByCountryIdAndAssetTypeId(referenceId, templateDto.getAssetType());

                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.risk.questionnaireTemplate.assetType", previousTemplate.getName(), assetType.getName());
                }
                questionnaireTemplate.setAssetType(templateDto.getAssetType());

            } else {
                if (CollectionUtils.isNotEmpty(assetType.getSubAssetTypes()) && (!Optional.ofNullable(templateDto.getAssetSubType()).isPresent() || !assetType.getSubAssetTypes().contains(templateDto.getAssetSubType()))) {
                    exceptionService.invalidRequestException("message.assetSubType.not.selected");
                }
                previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findPublishedTemplateOfTemplateTypeRiskByUnitIdAndAssetTypeIdAndSubAssetTypeId(referenceId, templateDto.getAssetType(), templateDto.getAssetSubType())
                        : questionnaireTemplateMongoRepository.findQuestionnaireTemplateOfTemplateTypeRiskByCountryIdAndAssetTypeIdAndSubAssetTypeId(referenceId, templateDto.getAssetType(), templateDto.getAssetSubType());
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.invalidRequestException("duplicate.risk.questionnaireTemplate", previousTemplate.getName());
                }
                questionnaireTemplate.setAssetType(templateDto.getAssetType());
                questionnaireTemplate.setAssetSubType(templateDto.getAssetSubType());
            }

        } else {
            previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findPublishedQuestionnaireTemplateOfTemplateTypeRiskAndAssociatedEntityProcessingActivityByUnitId(referenceId)
                    : questionnaireTemplateMongoRepository.findQuestionnaireTemplateOfTemplateTypeRiskAndAsssociatedEntityProcessingActivityByCountryId(referenceId);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.invalidRequestException("duplicate.risk.questionnaireTemplate", previousTemplate.getName());
            }
        }


        questionnaireTemplate.setTemplateStatus(templateDto.getTemplateStatus());
        questionnaireTemplate.setRiskAssociatedEntity(templateDto.getRiskAssociatedEntity());

    }


    private void addAssetTypeAndSubAssetTypeToQuestionnaireTemplateOnTemplateTypeStatusAssetTypeSelection(Long referenceId, boolean isUnitId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {

        QuestionnaireTemplate previousTemplate = null;
        if (templateDto.isDefaultAssetTemplate()) {
            previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findDefaultAssetQuestionnaireTemplateByUnitId(referenceId) : questionnaireTemplateMongoRepository.findDefaultAssetQuestionnaireTemplateByCountryId(referenceId);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.duplicateDataException("duplicate.questionnaire.template.assetType.defaultTemplate");
            }
            questionnaireTemplate.setDefaultAssetTemplate(true);
        } else {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isUnitId ? assetTypeMongoRepository.findByIdAndUnitId(referenceId, templateDto.getAssetType()) : assetTypeMongoRepository.findByIdAndCountryId(referenceId, templateDto.getAssetType());
            if (CollectionUtils.isEmpty(assetType.getSubAssetTypes())) {
                previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findPublishedQuestionnaireTemplateByAssetTypeAndByUnitId(referenceId, templateDto.getAssetType()) : questionnaireTemplateMongoRepository.findQuestionnaireTemplateByAssetTypeAndByCountryId(referenceId, templateDto.getAssetType());
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType", previousTemplate.getName(), assetType.getName());
                }
                questionnaireTemplate.setAssetType(templateDto.getAssetType());
            } else {
                if (CollectionUtils.isNotEmpty(assetType.getSubAssetTypes()) && (!Optional.ofNullable(templateDto.getAssetSubType()).isPresent() || !assetType.getSubAssetTypes().contains(templateDto.getAssetSubType()))) {
                    exceptionService.invalidRequestException("message.assetSubType.not.selected");
                } else {
                    previousTemplate = isUnitId ? questionnaireTemplateMongoRepository.findPublishedQuestionnaireTemplateByAssetTypeAndSubAssetTypeByUnitId(referenceId, templateDto.getAssetType(), Collections.singletonList(templateDto.getAssetSubType())) : questionnaireTemplateMongoRepository.findQuestionnaireTemplateByAssetTypeAndSubAssetTypeByCountryId(referenceId, templateDto.getAssetType(), Collections.singletonList(templateDto.getAssetSubType()));
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName(), assetType.getName());
                    }
                    questionnaireTemplate.setAssetType(templateDto.getAssetType());
                    questionnaireTemplate.setAssetSubType(templateDto.getAssetSubType());
                }
            }
        }
    }

    /**
     * @param countryId
     * @param id        - id of questionnaire template
     * @return true id deletion is successful
     * @description delete questionnaire template ,sections and question related to template.
     */
    public boolean deleteMasterQuestionnaireTemplate(Long countryId, BigInteger id) {
        QuestionnaireTemplate exist = questionnaireTemplateMongoRepository.findByCountryIdAndId(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire template", id);
        }
        delete(exist);
        return true;
    }


    /**
     * @param countryId
     * @param questionnaireTemplateId questionnaire template id
     * @param templateDto
     * @return updated Questionnaire template with basic data (name,description ,template type)
     */
    public QuestionnaireTemplateDTO updateMasterQuestionnaireTemplate(Long countryId, BigInteger questionnaireTemplateId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate masterQuestionnaireTemplate = questionnaireTemplateMongoRepository.findByNameAndCountryId(countryId, templateDto.getName());
        if (Optional.ofNullable(masterQuestionnaireTemplate).isPresent() && !questionnaireTemplateId.equals(masterQuestionnaireTemplate.getId())) {
            throw new DuplicateDataException("Template Exists with same name " + templateDto.getName());
        }
        masterQuestionnaireTemplate = questionnaireTemplateMongoRepository.findOne(questionnaireTemplateId);
        if (!Optional.ofNullable(masterQuestionnaireTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "questionnaire template", questionnaireTemplateId);
        }
        masterQuestionnaireTemplate.setName(templateDto.getName());
        masterQuestionnaireTemplate.setDescription(templateDto.getDescription());
        addTemplateTypeToQuestionnaireTemplate(countryId, false, masterQuestionnaireTemplate, templateDto);
        questionnaireTemplateMongoRepository.save(masterQuestionnaireTemplate);
        return templateDto;
    }

    /**
     * @param countryId
     * @param questionnaireTemplateId questionnaire template id
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public QuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionById(Long countryId, BigInteger questionnaireTemplateId) {
        QuestionnaireTemplateResponseDTO templateResponseDto = questionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsByCountryId(countryId, questionnaireTemplateId);
        if (!Optional.ofNullable(templateResponseDto.getSections().get(0).getId()).isPresent()) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }


    /**
     * @param countryId
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public List<QuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSection(Long countryId) {
        List<QuestionnaireTemplateResponseDTO> templateResponseDTOs = questionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(countryId);
        templateResponseDTOs.forEach(template -> {
            if (!Optional.ofNullable(template.getSections().get(0).getId()).isPresent()) {
                template.setSections(new ArrayList<>());
            }
        });
        return templateResponseDTOs;

    }


    public Object[] getQuestionnaireTemplateAttributeNames(String templateType) {

        QuestionnaireTemplateType questionnaireTemplateType = QuestionnaireTemplateType.valueOf(templateType);
        switch (questionnaireTemplateType) {
            case ASSET_TYPE:
                return AssetAttributeName.values();
            case PROCESSING_ACTIVITY:
                return ProcessingActivityAttributeName.values();
            default:
                return null;
        }
    }


    /**
     * @param unitId
     * @param questionnaireTemplateDTO
     * @return
     * @description create Questionnaire template at organization level
     */
    public QuestionnaireTemplateDTO saveQuestionnaireTemplate(Long unitId, QuestionnaireTemplateDTO questionnaireTemplateDTO) {

        QuestionnaireTemplate previousTemplate = questionnaireTemplateMongoRepository.findByNameAndUnitId(unitId, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Questionnaire Template", questionnaireTemplateDTO.getName());
        }
        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(questionnaireTemplateDTO.getName(), questionnaireTemplateDTO.getDescription(), QuestionnaireTemplateStatus.DRAFT);
        questionnaireTemplate.setOrganizationId(unitId);
        addTemplateTypeToQuestionnaireTemplate(unitId, true, questionnaireTemplate, questionnaireTemplateDTO);
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return questionnaireTemplateDTO.setId(questionnaireTemplate.getId());

    }

    /**
     * @param unitId
     * @param questionnaireTemplateId
     * @param questionnaireTemplateDTO
     * @return
     * @description method update exisiting questionnaire template at organization level( check if template with same name exist, then throw exception )
     */
    public QuestionnaireTemplateDTO updateQuestionnaireTemplate(Long unitId, BigInteger questionnaireTemplateId, QuestionnaireTemplateDTO questionnaireTemplateDTO) {

        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByNameAndUnitId(unitId, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(questionnaireTemplate).isPresent() && !questionnaireTemplateId.equals(questionnaireTemplate.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Questionnaire Template", questionnaireTemplateDTO.getName());
        }
        questionnaireTemplate = questionnaireTemplateMongoRepository.findOne(questionnaireTemplateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "questionnaire template", questionnaireTemplateId);
        }
        questionnaireTemplate.setName(questionnaireTemplateDTO.getName());
        questionnaireTemplate.setDescription(questionnaireTemplateDTO.getDescription());
        addTemplateTypeToQuestionnaireTemplate(unitId, true, questionnaireTemplate, questionnaireTemplateDTO);
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return questionnaireTemplateDTO;
    }


    public boolean deleteQuestionnaireTemplate(Long unitId, BigInteger questionnaireTemplateId) {
        questionnaireTemplateMongoRepository.safeDelete(questionnaireTemplateId);
        return true;
    }

    /**
     * @param unitId
     * @param questionnaireTemplateId
     * @return method return questionnaire template with sections and questions
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionByUnitIdAndId(Long unitId, BigInteger questionnaireTemplateId) {
        QuestionnaireTemplateResponseDTO templateResponseDto = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, questionnaireTemplateId);
        if (!Optional.ofNullable(templateResponseDto.getSections().get(0).getId()).isPresent()) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }

    /**
     * @param unitId
     * @return
     */
    public List<QuestionnaireTemplateResponseDTO> getAllQuestionnaireTemplateWithSectionByUnitId(Long unitId) {
        List<QuestionnaireTemplateResponseDTO> templateResponseDTOs = questionnaireTemplateMongoRepository.getAllQuestionnaireTemplateWithSectionsAndQuestionsByUnitId(unitId);
        templateResponseDTOs.forEach(template -> {
            if (!Optional.ofNullable(template.getSections().get(0).getId()).isPresent()) {
                template.setSections(new ArrayList<>());
            }
        });
        return templateResponseDTOs;

    }

}
