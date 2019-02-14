package com.kairos.service.questionnaire_template;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateRepository;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.*;


@Service
public class QuestionnaireTemplateService {


    private final Logger  LOGGER = LoggerFactory.getLogger(QuestionnaireTemplateService.class);

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
        QuestionnaireTemplate previousMasterTemplate = questionnaireTemplateRepository.findByCountryIdAndName(countryId,  templateDto.getName());
        if (Optional.ofNullable(previousMasterTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.questionnaireTemplate", templateDto.getName());
        }

        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        validateQuestionnaireTemplateAndAddTemplateType(countryId, false, questionnaireTemplate, templateDto);
        questionnaireTemplateRepository.save(questionnaireTemplate);
        templateDto.setId(questionnaireTemplate.getId());
        return templateDto;
    }


    /**
     * @param templateDto
     * @param questionnaireTemplate
     */
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
            previousTemplate = isUnitId ? questionnaireTemplateRepository.getDefaultPublishedAssetQuestionnaireTemplateByUnitId(referenceId) : questionnaireTemplateRepository.findDefaultAssetQuestionnaireTemplateByCountryId(referenceId);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.duplicateDataException("duplicate.questionnaire.template.assetType.defaultTemplate");
            }
            questionnaireTemplate.setDefaultAssetTemplate(true);
        } else {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isUnitId ? assetTypeRepository.findByIdAndOrganizationIdAndDeleted(templateDto.getAssetType(), referenceId) : assetTypeRepository.findByIdAndCountryIdAndDeleted(templateDto.getAssetType(), referenceId);
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
            AssetType assetType = isUnitId ? assetTypeRepository.findByIdAndOrganizationIdAndDeleted(templateDto.getAssetType(), referenceId) : assetTypeRepository.findByIdAndCountryIdAndDeleted(templateDto.getAssetType(), referenceId);
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
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByIdAndCountryIdAndDeletedFalse( id,countryId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", id);
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

        QuestionnaireTemplate masterQuestionnaireTemplate = questionnaireTemplateRepository.findByCountryIdAndName(countryId, templateDto.getName());
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
            exceptionService.duplicateDataException("message.dataNotFound", "message.questionnaireTemplate", questionnaireTemplateId);
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
    public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByTemplateIdAndCountryIdOrOrganisationId(Long referenceId, Long questionnaireTemplateId, boolean isUnitId) {
        QuestionnaireTemplate questionnaireTemplate = isUnitId ?
                questionnaireTemplateRepository.getQuestionnaireTemplateWithSectionsByOrganizationId(referenceId, questionnaireTemplateId) : questionnaireTemplateRepository.getMasterQuestionnaireTemplateWithSectionsByCountryId(referenceId, questionnaireTemplateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", questionnaireTemplateId);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(questionnaireTemplate, QuestionnaireTemplateResponseDTO.class);
    }

    /**
     * @param referenceId
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public List<QuestionnaireTemplate> getAllQuestionnaireTemplateByCountryIdOrOrganizationId(Long referenceId, boolean isUnitId) {
        return isUnitId ? questionnaireTemplateRepository.getAllQuestionnaireTemplateByOrganizationId(referenceId) : questionnaireTemplateRepository.getAllMasterQuestionnaireTemplateByCountryId(referenceId);

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

        QuestionnaireTemplate previousTemplate = questionnaireTemplateRepository.findByOrganizationIdAndDeletedAndName(unitId, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.questionnaireTemplate", questionnaireTemplateDTO.getName());
        }
        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(questionnaireTemplateDTO.getName(), questionnaireTemplateDTO.getDescription(), QuestionnaireTemplateStatus.DRAFT);
        questionnaireTemplate.setOrganizationId(unitId);
        validateQuestionnaireTemplateAndAddTemplateType(unitId, true, questionnaireTemplate, questionnaireTemplateDTO);
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return questionnaireTemplateDTO.setId(questionnaireTemplate.getId());

    }


    /**
     * @param unitId
     * @param questionnaireTemplateId
     * @param questionnaireTemplateDTO
     * @return
     * @description method update existing questionnaire template at organization level( check if template with same name exist, then throw exception )
     */
    public QuestionnaireTemplateDTO updateQuestionnaireTemplate(Long unitId, Long questionnaireTemplateId, QuestionnaireTemplateDTO questionnaireTemplateDTO) {

        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByOrganizationIdAndDeletedAndName(unitId, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(questionnaireTemplate).isPresent() && !questionnaireTemplateId.equals(questionnaireTemplate.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.questionnaireTemplate", questionnaireTemplateDTO.getName());
        }
        try {
            questionnaireTemplate = questionnaireTemplateRepository.getOne(questionnaireTemplateId);
            questionnaireTemplate.setName(questionnaireTemplateDTO.getName());
            questionnaireTemplate.setDescription(questionnaireTemplateDTO.getDescription());
            validateQuestionnaireTemplateAndAddTemplateType(unitId, true, questionnaireTemplate, questionnaireTemplateDTO);
            questionnaireTemplateRepository.save(questionnaireTemplate);
        } catch (EntityNotFoundException ene) {
            exceptionService.duplicateDataException("message.dataNotFound", "message.questionnaireTemplate", questionnaireTemplateId);
        } catch (Exception ex) {
            exceptionService.internalError(ex.getMessage());
        }
        return questionnaireTemplateDTO;
    }


    public boolean deleteQuestionnaireTemplate(Long unitId, Long questionnaireTemplateId) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByIdAndOrganizationIdAndDeleted(questionnaireTemplateId, unitId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", questionnaireTemplateId);
        }
        questionnaireTemplate.delete();
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return true;
    }
    /*
      @param unitId
     * @param questionnaireTemplateId
     * @return method return questionnaire template with sections and questions
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    /*public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByTemplateIdAndCountryIdOrOrganisationId(Long unitId, Long questionnaireTemplateId) {
        QuestionnaireTemplateResponseDTO templateResponseDto = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, questionnaireTemplateId);
        if (!Optional.ofNullable(templateResponseDto.getSections().get(0).getId()).isPresent()) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }*/


}
