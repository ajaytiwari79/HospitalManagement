package com.kairos.service.questionnaire_template;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.master_data.QuestionnaireAssetTypeDTO;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateRepository;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class QuestionnaireTemplateService {


    private Logger LOGGER = LoggerFactory.getLogger(QuestionnaireTemplateService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private QuestionnaireSectionService questionnaireSectionService;

    @Inject
    private QuestionnaireTemplateRepository questionnaireTemplateRepository;


    /**
     * @param countryId
     * @param templateDto contain data of Questionnaire template
     * @return Object of Questionnaire template with template type and asset type if template type is(ASSET_TYPE_KEY)
     */
    public QuestionnaireTemplateDTO saveMasterQuestionnaireTemplate(Long countryId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousMasterTemplate = questionnaireTemplateRepository.findByCountryIdAndDeletedAndName(countryId, false, templateDto.getName());
        if (Optional.ofNullable(previousMasterTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.questionnaireTemplate", templateDto.getName());
        }

        // TODO remove duplicate code
        //validateQuestionnaireTemplateDTOByTemplateTypeCriteria(countryId, templateDto);
        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        validateQuestionnaireTemplateAndAddTemplateType(countryId, false, questionnaireTemplate, templateDto);
        questionnaireTemplateRepository.save(questionnaireTemplate);
        templateDto.setId(questionnaireTemplate.getId());
        return templateDto;
    }

    //TODO duplicate code
    /**
     *
     * @param countryId
     * @param templateDto
     * @return
     *//*
    private void validateQuestionnaireTemplateDTOByTemplateTypeCriteria(Long countryId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplateType questionnaireTemplateType = templateDto.getTemplateType();
        QuestionnaireTemplate previousMasterTemplate = null;
        switch (questionnaireTemplateType) {
            case ASSET_TYPE:
                if (!templateDto.isDefaultAssetTemplate()) {
                    previousMasterTemplate = getQuestionnaireTemplateByAssetTypeOrSubType(countryId, templateDto);
                } else {
                    previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByCountryIdAndTemplateTypeAndDefaultAssetTemplate(countryId, questionnaireTemplateType, true);
                }
                break;
            case RISK:
                if (QuestionnaireTemplateType.ASSET_TYPE.equals(templateDto.getRiskAssociatedEntity())) {
                    previousMasterTemplate = getQuestionnaireTemplateByAssetTypeOrSubType(countryId, templateDto);
                } else {
                    previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndCountryId( questionnaireTemplateType, templateDto.getRiskAssociatedEntity(),countryId);
                }
                break;
            default:
                previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByCountryIdAndTemplateType(countryId, questionnaireTemplateType);
                break;
        }
        if (previousMasterTemplate != null) {
            exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.templateType.config", "Master Questionnaire template");
        }
    }
*/

    // TODO remove unused code
    /* *//**
     *
     * @param countryId
     * @param templateDto
     * @return
     *//*
    private QuestionnaireTemplate getQuestionnaireTemplateByAssetTypeOrSubType(Long countryId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousMasterTemplate = null;
        if (templateDto.getAssetSubType() != null) {
            previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeAndCountryId(templateDto.getTemplateType(), templateDto.getAssetType(), templateDto.getAssetSubType(),countryId);
        } else {
            previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndAndCountryId(templateDto.getTemplateType(), templateDto.getAssetType(),countryId);
        }
        return previousMasterTemplate;
    }
*/

    /**
     * @param templateDto
     * @param questionnaireTemplate
     */
    //todo add message
    private void validateQuestionnaireTemplateAndAddTemplateType(Long referenceId, boolean isUnitId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {

        switch (templateDto.getTemplateType()) {
            case ASSET_TYPE:
                addAssetTypeAndSubAssetType(referenceId, isUnitId, questionnaireTemplate, templateDto);
                break;
            case RISK:
                if (!Optional.ofNullable(templateDto.getRiskAssociatedEntity()).isPresent()) {
                    exceptionService.invalidRequestException("message.risk.questionnaireTemplate.associated.entity.not.selected");
                }
                addRiskAssociatedEntity(referenceId, isUnitId, questionnaireTemplate, templateDto);
                break;
            default:
                QuestionnaireTemplate previousTemplate = isUnitId ? questionnaireTemplateRepository.findQuestionnaireTemplateByUnitIdAndTemplateTypeAndTemplateStatus(referenceId, templateDto.getTemplateType(), QuestionnaireTemplateStatus.PUBLISHED) : questionnaireTemplateRepository.findQuestionnaireTemplateByCountryIdAndTemplateType(referenceId, templateDto.getTemplateType());
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("duplicate.questionnaireTemplate.ofTemplateType", templateDto.getTemplateType());
                }
                break;
        }
        if (isUnitId) questionnaireTemplate.setTemplateStatus(templateDto.getTemplateStatus());
        questionnaireTemplate.setTemplateType(templateDto.getTemplateType());

    }


    private void addAssetTypeAndSubAssetType(Long referenceId, boolean isUnitId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {

        QuestionnaireTemplate previousTemplate = null;
        if (templateDto.isDefaultAssetTemplate()) {
            previousTemplate = isUnitId ? questionnaireTemplateRepository.findDefaultAssetQuestionnaireTemplateByUnitId(referenceId, QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED) : questionnaireTemplateRepository.findDefaultAssetQuestionnaireTemplateByCountryId(referenceId);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.duplicateDataException("duplicate.questionnaire.template.assetType.defaultTemplate");
            }
            questionnaireTemplate.setDefaultAssetTemplate(true);
        } else {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isUnitId ? assetTypeRepository.findByIdAndOrganizationIdAndDeleted(templateDto.getAssetType(), referenceId, false) : assetTypeRepository.findByIdAndCountryIdAndDeleted(templateDto.getAssetType(), referenceId, false);
            questionnaireTemplate.setAssetType(assetType);
            if (CollectionUtils.isEmpty(assetType.getSubAssetTypes())) {
                previousTemplate = isUnitId ? questionnaireTemplateRepository.findQuestionnaireTemplateByUnitIdAssetTypeIdAndTemplateStatus(referenceId, templateDto.getAssetType(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED) : questionnaireTemplateRepository.findQuestionnaireTemplateByAssetTypeAndByCountryId(referenceId, templateDto.getAssetType(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType", previousTemplate.getName(), assetType.getName());
                }

            } else {
                if (CollectionUtils.isNotEmpty(assetType.getSubAssetTypes()) && (!Optional.ofNullable(templateDto.getAssetSubType()).isPresent())) {
                    exceptionService.invalidRequestException("message.assetSubType.not.selected");
                } else {
                    previousTemplate = isUnitId ? questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(referenceId, templateDto.getAssetType(), templateDto.getAssetSubType(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED) : questionnaireTemplateRepository.findQuestionnaireTemplateByAssetTypeAndSubAssetTypeByCountryId(referenceId, templateDto.getAssetType(), templateDto.getAssetSubType(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName(), assetType.getName());
                    }
                    AssetType assetSubType = isUnitId ? assetTypeRepository.findByIdAndOrganizationIdAndAssetTypeAndDeleted(templateDto.getAssetSubType(), templateDto.getAssetType(), referenceId) : assetTypeRepository.findByIdAndCountryIdAndAssetTypeAndDeleted(templateDto.getAssetSubType(), templateDto.getAssetType(), referenceId);
                    questionnaireTemplate.setAssetSubType(assetSubType);
                }
            }
        }
    }


    private void addRiskAssociatedEntity(Long referenceId, boolean isUnitId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousTemplate;
        if (QuestionnaireTemplateType.ASSET_TYPE.equals(templateDto.getRiskAssociatedEntity())) {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isUnitId ? assetTypeRepository.findByIdAndOrganizationIdAndDeleted(templateDto.getAssetType(), referenceId, false) : assetTypeRepository.findByIdAndCountryIdAndDeleted(templateDto.getAssetType(), referenceId, false);
            if (CollectionUtils.isEmpty(assetType.getSubAssetTypes())) {
                previousTemplate = isUnitId ? questionnaireTemplateRepository.findPublishedRiskTemplateByAssetTypeIdAndOrgId(referenceId, templateDto.getAssetType())
                        : questionnaireTemplateRepository.findRiskTemplateByCountryIdAndAssetTypeId(referenceId, templateDto.getAssetType());

                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.risk.questionnaireTemplate.assetType", previousTemplate.getName(), assetType.getName());
                }

            } else {
                AssetType selectedAssetSubType = assetType.getSubAssetTypes().stream().filter(assetSubType -> assetSubType.getId().equals(templateDto.getAssetSubType())).findFirst().orElse(null);
                if (!Optional.ofNullable(selectedAssetSubType).isPresent()) {
                    exceptionService.invalidRequestException("message.assetSubType.invalid.selection");
                }
              /*  if (CollectionUtils.isNotEmpty(assetType.getSubAssetTypes()) && (!Optional.ofNullable(templateDto.getAssetSubType()).isPresent() || !(assetType.getSubAssetTypes().stream().map(AssetType::getId).collect(Collectors.toList())).contains(templateDto.getAssetSubType()))) {
                }*/
                previousTemplate = isUnitId ? questionnaireTemplateRepository.findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId(referenceId, templateDto.getAssetType(), templateDto.getAssetSubType())
                        : questionnaireTemplateRepository.findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId(referenceId, templateDto.getAssetType(), templateDto.getAssetSubType());
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.invalidRequestException("duplicate.risk.questionnaireTemplate", previousTemplate.getName());
                }
                questionnaireTemplate.setAssetSubType(selectedAssetSubType);
            }
            questionnaireTemplate.setAssetType(assetType);

        } else {
            previousTemplate = isUnitId ? questionnaireTemplateRepository.findPublishedRiskTemplateByAssociatedEntityAndOrgId(referenceId, templateDto.getRiskAssociatedEntity())
                    : questionnaireTemplateRepository.findRiskTemplateByAssociatedEntityAndCountryId(referenceId, templateDto.getRiskAssociatedEntity());
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.invalidRequestException("duplicate.risk.questionnaireTemplate", previousTemplate.getName());
            }
        }
        questionnaireTemplate.setRiskAssociatedEntity(templateDto.getRiskAssociatedEntity());

    }


    /**
     * @param countryId
     * @param id        - id of questionnaire template
     * @return true id deletion is successful
     * @description delete questionnaire template ,sections and question related to template.
     */
    public boolean deleteMasterQuestionnaireTemplate(Long countryId, Long id) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByIdAndCountryIdAndDeleted(id, countryId, false);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire template", id);
        }
        questionnaireTemplate.delete();
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return true;
    }


    /**
     * @param countryId
     * @param questionnaireTemplateId questionnaire template id
     * @param templateDto
     * @return updated Questionnaire template with basic data (name,description ,template type)
     */
    public QuestionnaireTemplateDTO updateMasterQuestionnaireTemplate(Long countryId, Long questionnaireTemplateId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate masterQuestionnaireTemplate = questionnaireTemplateRepository.findByCountryIdAndDeletedAndName(countryId, false, templateDto.getName());
        if (Optional.ofNullable(masterQuestionnaireTemplate).isPresent() && !questionnaireTemplateId.equals(masterQuestionnaireTemplate.getId())) {
            throw new DuplicateDataException("Template Exists with same name " + templateDto.getName());
        }
        try {
            masterQuestionnaireTemplate = questionnaireTemplateRepository.getOne(questionnaireTemplateId);
            masterQuestionnaireTemplate.setName(templateDto.getName());
            masterQuestionnaireTemplate.setDescription(templateDto.getDescription());
            validateQuestionnaireTemplateAndAddTemplateType(countryId, false, masterQuestionnaireTemplate, templateDto);
            questionnaireTemplateRepository.save(masterQuestionnaireTemplate);
        } catch (EntityNotFoundException ene) {
            exceptionService.duplicateDataException("message.dataNotFound", "questionnaire template", questionnaireTemplateId);
        } catch (Exception ex) {
            LOGGER.error("Error in updating questionnaire template with id :: {}", questionnaireTemplateId);
            exceptionService.internalError(ex.getMessage());
        }
        return templateDto;
    }

    /**
     * @param referenceId
     * @param questionnaireTemplateId questionnaire template id
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateDataWithSectionsByTemplateIdAndUnitOrOrganisationId(Long referenceId, Long questionnaireTemplateId, boolean isMasterData) {
        QuestionnaireTemplate questionnaireTemplate = isMasterData ? questionnaireTemplateRepository.getMasterQuestionnaireTemplateWithSectionsByCountryId(referenceId, questionnaireTemplateId) :
                questionnaireTemplateRepository.getQuestionnaireTemplateWithSectionsByOrganizationId(referenceId, questionnaireTemplateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire template", questionnaireTemplateId);
        }
       /* if (!Optional.ofNullable(templateResponseDto.getSections().get(0).getId()).isPresent()) {
            templateResponseDto.setSections(new ArrayList<>());
        }*/
        return prepareQuestionnaireTemplateResponseData(questionnaireTemplate);
    }

    /**
     * This method is used to prepare Questionnaire template Response object from actual entity object
     */
    QuestionnaireTemplateResponseDTO prepareQuestionnaireTemplateResponseData(QuestionnaireTemplate questionnaireTemplate) {
        QuestionnaireTemplateResponseDTO questionnaireTemplateResponseDTO = new QuestionnaireTemplateResponseDTO(questionnaireTemplate.getId(), questionnaireTemplate.getName(), questionnaireTemplate.getDescription(), questionnaireTemplate.getTemplateType(), questionnaireTemplate.isDefaultAssetTemplate(), questionnaireTemplate.getTemplateStatus());
        questionnaireTemplateResponseDTO.setAssetType(new QuestionnaireAssetTypeDTO(questionnaireTemplate.getAssetType().getId(), questionnaireTemplate.getAssetType().getName(), questionnaireTemplate.getAssetType().isSubAssetType()));
        questionnaireTemplateResponseDTO.setAssetSubType(new QuestionnaireAssetTypeDTO(questionnaireTemplate.getAssetSubType().getId(), questionnaireTemplate.getAssetSubType().getName(), questionnaireTemplate.getAssetSubType().isSubAssetType()));
        questionnaireTemplateResponseDTO.setSections(ObjectMapperUtils.copyPropertiesOfListByMapper(questionnaireTemplate.getSections(), QuestionnaireSectionResponseDTO.class));

        return questionnaireTemplateResponseDTO;
    }


    /**
     * @param id
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public List<QuestionnaireTemplateResponseDTO> getAllQuestionnaireTemplateWithSectionOfCountryOrOrganization(Long id, boolean isMasterData) {
        List<QuestionnaireTemplateResponseDTO> questionnaireTemplateResponseDTOS = new ArrayList<>();
        List<QuestionnaireTemplate> questionnaireTemplates = isMasterData ? questionnaireTemplateRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(id) :
                questionnaireTemplateRepository.getAllQuestionnaireTemplateWithSectionsAndQuestionsByOrganizationId(id);
        questionnaireTemplates.forEach(questionnaireTemplate -> {
            questionnaireTemplateResponseDTOS.add(prepareQuestionnaireTemplateResponseData(questionnaireTemplate));
        });
        /*templateResponseDTOs.forEach(template -> {
            if (!Optional.ofNullable(template.getSections().get(0).getId()).isPresent()) {
                template.setSections(new ArrayList<>());
            }
        });*/
        return questionnaireTemplateResponseDTOS;

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

        QuestionnaireTemplate previousTemplate = questionnaireTemplateRepository.findByOrganizationIdAndDeletedAndName(unitId, false, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Questionnaire Template", questionnaireTemplateDTO.getName());
        }
        validateQuestionnaireTemplateDTOByTemplateTypeCriteriaForUnit(unitId, questionnaireTemplateDTO);
        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(questionnaireTemplateDTO.getName(), questionnaireTemplateDTO.getDescription(), QuestionnaireTemplateStatus.DRAFT);
        questionnaireTemplate.setOrganizationId(unitId);
        validateQuestionnaireTemplateAndAddTemplateType(unitId, true, questionnaireTemplate, questionnaireTemplateDTO);
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return questionnaireTemplateDTO.setId(questionnaireTemplate.getId());

    }

    /**
     * @param unitId
     * @param templateDto
     */
    private void validateQuestionnaireTemplateDTOByTemplateTypeCriteriaForUnit(Long unitId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplateType questionnaireTemplateType = templateDto.getTemplateType();
        QuestionnaireTemplate previousMasterTemplate;
        switch (questionnaireTemplateType) {
            case ASSET_TYPE:
                if (!templateDto.isDefaultAssetTemplate()) {
                    previousMasterTemplate = getQuestionnaireTemplateByAssetTypeOrSubTypeForUnit(unitId, templateDto);
                } else {
                    previousMasterTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByTemplateTypeAndDefaultAssetTemplateAndOrganizationId(questionnaireTemplateType, true, unitId, QuestionnaireTemplateStatus.PUBLISHED);
                }
                break;
            case RISK:
                if (QuestionnaireTemplateType.ASSET_TYPE.equals(templateDto.getRiskAssociatedEntity())) {
                    previousMasterTemplate = getQuestionnaireTemplateByAssetTypeOrSubTypeForUnit(unitId, templateDto);
                } else {
                    previousMasterTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndOrganizationId(questionnaireTemplateType, templateDto.getRiskAssociatedEntity(), unitId, QuestionnaireTemplateStatus.PUBLISHED);
                }
                break;
            default:
                previousMasterTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByOrganizationIdAndTemplateType(unitId, questionnaireTemplateType, QuestionnaireTemplateStatus.PUBLISHED);
                break;
        }
        if (previousMasterTemplate != null) {
            exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.templateType.config", "Organization Questionnaire template");
        }
    }

    /**
     * @param unitId
     * @param templateDto
     * @return
     */
    private QuestionnaireTemplate getQuestionnaireTemplateByAssetTypeOrSubTypeForUnit(Long unitId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousMasterTemplate = null;
        if (templateDto.getAssetSubType() != null) {
            previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeAndOrganizationId(templateDto.getTemplateType(), templateDto.getAssetType(), templateDto.getAssetSubType(), unitId);
        } else {
            previousMasterTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndAndOrganizationId(templateDto.getTemplateType(), templateDto.getAssetType(), unitId);
        }
        return previousMasterTemplate;
    }


    /**
     * @param unitId
     * @param questionnaireTemplateId
     * @param questionnaireTemplateDTO
     * @return
     * @description method update existing questionnaire template at organization level( check if template with same name exist, then throw exception )
     */
    public QuestionnaireTemplateDTO updateQuestionnaireTemplate(Long unitId, Long questionnaireTemplateId, QuestionnaireTemplateDTO questionnaireTemplateDTO) {

        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByOrganizationIdAndDeletedAndName(unitId, false, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(questionnaireTemplate).isPresent() && !questionnaireTemplateId.equals(questionnaireTemplate.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Questionnaire Template", questionnaireTemplateDTO.getName());
        }
        try {
            questionnaireTemplate = questionnaireTemplateRepository.getOne(questionnaireTemplateId);
            questionnaireTemplate.setName(questionnaireTemplateDTO.getName());
            questionnaireTemplate.setDescription(questionnaireTemplateDTO.getDescription());
            validateQuestionnaireTemplateAndAddTemplateType(unitId, true, questionnaireTemplate, questionnaireTemplateDTO);
            questionnaireTemplateRepository.save(questionnaireTemplate);
        } catch (EntityNotFoundException ene) {
            exceptionService.duplicateDataException("message.dataNotFound", "questionnaire template", questionnaireTemplateId);
        } catch (Exception ex) {
            exceptionService.internalError(ex.getMessage());
        }
        return questionnaireTemplateDTO;
    }


    public boolean deleteQuestionnaireTemplate(Long unitId, Long questionnaireTemplateId) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByIdAndOrganizationIdAndDeleted(questionnaireTemplateId, unitId, false);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire template", questionnaireTemplateId);
        }
        questionnaireTemplate.delete();
        questionnaireTemplateRepository.save(questionnaireTemplate);
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
    /*public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateDataWithSectionsByTemplateIdAndUnitOrOrganisationId(Long unitId, Long questionnaireTemplateId) {
        QuestionnaireTemplateResponseDTO templateResponseDto = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, questionnaireTemplateId);
        if (!Optional.ofNullable(templateResponseDto.getSections().get(0).getId()).isPresent()) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }*/


}
