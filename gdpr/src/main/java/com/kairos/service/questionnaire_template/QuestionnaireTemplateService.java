package com.kairos.service.questionnaire_template;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.master_data.QuestionnaireAssetTypeDTO;
import com.kairos.enums.gdpr.*;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateRepository;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.*;


@Service
public class QuestionnaireTemplateService {


    private final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireTemplateService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private QuestionnaireSectionService questionnaireSectionService;

    @Inject
    private QuestionnaireTemplateRepository questionnaireTemplateRepository;

    @Inject
    private AssessmentRepository assessmentRepository;


    /**
     * @param countryId
     * @param templateDto contain data of Questionnaire template
     * @return Object of Questionnaire template with template type and asset type if template type is(ASSET_TYPE_KEY)
     */
    public QuestionnaireTemplateDTO saveMasterQuestionnaireTemplate(Long countryId, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousMasterTemplate = questionnaireTemplateRepository.findByCountryIdAndName(countryId, templateDto.getName());
        if (Optional.ofNullable(previousMasterTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.questionnaireTemplate", templateDto.getName());
        }

        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription(), templateDto.getTemplateType());
        validateQuestionnaireTemplateAndAddTemplateType(countryId, false, questionnaireTemplate, templateDto);
        questionnaireTemplateRepository.save(questionnaireTemplate);
        templateDto.setId(questionnaireTemplate.getId());
        return templateDto;
    }


    /**
     * @param templateDto
     * @param questionnaireTemplate
     */
    private void validateQuestionnaireTemplateAndAddTemplateType(Long referenceId, boolean isOrganization, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {

        switch (templateDto.getTemplateType()) {
            case ASSET_TYPE:
                addAssetTypeAndSubAssetType(referenceId, isOrganization, questionnaireTemplate, templateDto);
                break;
            case RISK:
                if (!Optional.ofNullable(templateDto.getRiskAssociatedEntity()).isPresent()) {
                    exceptionService.invalidRequestException("message.risk.questionnaireTemplate.associated.entity.not.selected");
                }
                addRiskAssociatedEntity(referenceId, isOrganization, questionnaireTemplate, templateDto);
                break;
            default:
                QuestionnaireTemplate previousTemplate = isOrganization ? questionnaireTemplateRepository.findQuestionnaireTemplateByUnitIdAndTemplateTypeAndTemplateStatus(referenceId, templateDto.getTemplateType(), QuestionnaireTemplateStatus.PUBLISHED) : questionnaireTemplateRepository.findQuestionnaireTemplateByCountryIdAndTemplateType(referenceId, templateDto.getTemplateType());
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("duplicate.questionnaireTemplate.ofTemplateType", templateDto.getTemplateType());
                }
                break;
        }
        if (isOrganization) questionnaireTemplate.setTemplateStatus(templateDto.getTemplateStatus());

    }


    private void addAssetTypeAndSubAssetType(Long referenceId, boolean isOrganization, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {

        QuestionnaireTemplate previousTemplate = null;
        if (templateDto.isDefaultAssetTemplate()) {
            previousTemplate = isOrganization ? questionnaireTemplateRepository.findDefaultTemplateByUnitIdAndTemplateTypeAndStatus(referenceId, QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED) : questionnaireTemplateRepository.findDefaultAssetQuestionnaireTemplateByCountryId(referenceId);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.duplicateDataException("duplicate.questionnaire.template.assetType.defaultTemplate");
            }
            questionnaireTemplate.setDefaultAssetTemplate(true);
        } else {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isOrganization ? assetTypeRepository.findByIdAndOrganizationIdAndDeleted(templateDto.getAssetType(), referenceId) : assetTypeRepository.findByIdAndCountryIdAndDeleted(templateDto.getAssetType(), referenceId);
            questionnaireTemplate.setAssetType(assetType);
            if (CollectionUtils.isEmpty(assetType.getSubAssetTypes())) {
                previousTemplate = isOrganization ? questionnaireTemplateRepository.findTemplateByUnitIdAssetTypeIdAndTemplateTypeAndTemplateStatus(referenceId, templateDto.getAssetType(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED)
                        : questionnaireTemplateRepository.findTemplateByCountryIdAndAssetTypeIdAndTemplateType(referenceId, templateDto.getAssetType(), QuestionnaireTemplateType.ASSET_TYPE);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType", previousTemplate.getName(), assetType.getName());
                }

            } else {
                if (CollectionUtils.isNotEmpty(assetType.getSubAssetTypes()) && (!Optional.ofNullable(templateDto.getSubAssetType()).isPresent())) {
                    exceptionService.invalidRequestException("message.subAssetType.not.selected");
                } else {
                    previousTemplate = isOrganization ? questionnaireTemplateRepository.findTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeIdTemplateTypeAndStatus(referenceId, templateDto.getAssetType(), templateDto.getSubAssetType(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED)
                            : questionnaireTemplateRepository.findTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeIdAndTemplateType(referenceId, templateDto.getAssetType(), templateDto.getSubAssetType(), QuestionnaireTemplateType.ASSET_TYPE);
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName(), assetType.getName());
                    }
                    AssetType subAssetType = isOrganization ? assetTypeRepository.findByIdAndOrganizationIdAndAssetTypeAndDeleted(templateDto.getSubAssetType(), templateDto.getAssetType(), referenceId) : assetTypeRepository.findByIdAndCountryIdAndAssetTypeAndDeleted(templateDto.getSubAssetType(), templateDto.getAssetType(), referenceId);
                    questionnaireTemplate.setAssetSubType(subAssetType);
                }
            }
        }
    }


    private void addRiskAssociatedEntity(Long referenceId, boolean isOrganization, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateDTO templateDto) {
        QuestionnaireTemplate previousTemplate;
        if (QuestionnaireTemplateType.ASSET_TYPE.equals(templateDto.getRiskAssociatedEntity())) {
            if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
                exceptionService.invalidRequestException("message.assetType.not.selected");
            }
            AssetType assetType = isOrganization ? assetTypeRepository.findByIdAndOrganizationIdAndDeleted(templateDto.getAssetType(), referenceId) : assetTypeRepository.findByIdAndCountryIdAndDeleted(templateDto.getAssetType(), referenceId);
            if (CollectionUtils.isEmpty(assetType.getSubAssetTypes())) {
                previousTemplate = isOrganization ? questionnaireTemplateRepository.findTemplateByUnitIdAssetTypeIdAndTemplateTypeAndTemplateStatus(referenceId, templateDto.getAssetType(), QuestionnaireTemplateType.RISK, QuestionnaireTemplateStatus.PUBLISHED)
                        : questionnaireTemplateRepository.findTemplateByCountryIdAndAssetTypeIdAndTemplateType(referenceId, templateDto.getAssetType(), QuestionnaireTemplateType.RISK);

                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.risk.questionnaireTemplate.assetType", previousTemplate.getName(), assetType.getName());
                }

            } else {
                AssetType selectedAssetSubType = assetType.getSubAssetTypes().stream().filter(subAssetType -> subAssetType.getId().equals(templateDto.getSubAssetType())).findFirst().orElse(null);
                if (!Optional.ofNullable(selectedAssetSubType).isPresent()) {
                    exceptionService.invalidRequestException("message.subAssetType.invalid.selection");
                }
                previousTemplate = isOrganization ? questionnaireTemplateRepository.findTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeIdTemplateTypeAndStatus(referenceId, templateDto.getAssetType(), templateDto.getSubAssetType(), QuestionnaireTemplateType.RISK, QuestionnaireTemplateStatus.PUBLISHED)
                        : questionnaireTemplateRepository.findTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeIdAndTemplateType(referenceId, templateDto.getAssetType(), templateDto.getSubAssetType(), QuestionnaireTemplateType.RISK);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.invalidRequestException("duplicate.risk.questionnaireTemplate", previousTemplate.getName());
                }
                questionnaireTemplate.setAssetSubType(selectedAssetSubType);
            }
            questionnaireTemplate.setAssetType(assetType);

        } else {
            previousTemplate = isOrganization ? questionnaireTemplateRepository.findTemplateByUnitIdAndRiskAssociatedEntityAndTemplateTypeAndStatus(referenceId, QuestionnaireTemplateType.RISK, templateDto.getRiskAssociatedEntity(), QuestionnaireTemplateStatus.PUBLISHED)
                    : questionnaireTemplateRepository.findTemplateByCountryIdAndTemplateTypeAndRiskAssociatedEntity(referenceId, QuestionnaireTemplateType.RISK, templateDto.getRiskAssociatedEntity());
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
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
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
    public QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByTemplateIdAndCountryIdOrOrganisationId(Long referenceId, Long questionnaireTemplateId, boolean isOrganization) {
        QuestionnaireTemplate questionnaireTemplate = isOrganization ?
                questionnaireTemplateRepository.getQuestionnaireTemplateWithSectionsByOrganizationId(referenceId, questionnaireTemplateId) : questionnaireTemplateRepository.getMasterQuestionnaireTemplateWithSectionsByCountryId(referenceId, questionnaireTemplateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", questionnaireTemplateId);
        }
        return prepareQuestionnaireTemplateResponseData(questionnaireTemplate);

    }

    /**
     * This method is used to prepare Questionnaire template Response object from actual entity object
     */
    QuestionnaireTemplateResponseDTO prepareQuestionnaireTemplateResponseData(QuestionnaireTemplate questionnaireTemplate) {
        QuestionnaireTemplateResponseDTO questionnaireTemplateResponseDTO = new QuestionnaireTemplateResponseDTO(questionnaireTemplate.getId(), questionnaireTemplate.getName(), questionnaireTemplate.getDescription(), questionnaireTemplate.getTemplateType(), questionnaireTemplate.isDefaultAssetTemplate(), questionnaireTemplate.getTemplateStatus(), questionnaireTemplate.getRiskAssociatedEntity());
        if (Optional.ofNullable(questionnaireTemplate.getAssetType()).isPresent()) {
            questionnaireTemplateResponseDTO.setAssetType(new QuestionnaireAssetTypeDTO(questionnaireTemplate.getAssetType().getId(), questionnaireTemplate.getAssetType().getName(), questionnaireTemplate.getAssetType().isSubAssetType()));
        }
        if (Optional.ofNullable(questionnaireTemplate.getAssetSubType()).isPresent()) {
            questionnaireTemplateResponseDTO.setAssetSubType(new QuestionnaireAssetTypeDTO(questionnaireTemplate.getAssetSubType().getId(), questionnaireTemplate.getAssetSubType().getName(), questionnaireTemplate.getAssetSubType().isSubAssetType()));
        }
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
    public List<QuestionnaireTemplateResponseDTO> getAllQuestionnaireTemplateWithSectionOfCountryOrOrganization(Long id, boolean isOrganization) {
        List<QuestionnaireTemplateResponseDTO> questionnaireTemplateResponseDTOS = new ArrayList<>();
        List<QuestionnaireTemplate> questionnaireTemplates = isOrganization ? questionnaireTemplateRepository.getAllQuestionnaireTemplateWithSectionsAndQuestionsByOrganizationId(id) : questionnaireTemplateRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(id);
        questionnaireTemplates.forEach(questionnaireTemplate -> questionnaireTemplateResponseDTOS.add(prepareQuestionnaireTemplateResponseData(questionnaireTemplate)));
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

        QuestionnaireTemplate previousTemplate = questionnaireTemplateRepository.findByOrganizationIdAndDeletedAndName(unitId, questionnaireTemplateDTO.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.questionnaireTemplate", questionnaireTemplateDTO.getName());
        }
        QuestionnaireTemplate questionnaireTemplate = new QuestionnaireTemplate(questionnaireTemplateDTO.getName(), questionnaireTemplateDTO.getDescription(), questionnaireTemplateDTO.getTemplateType(), QuestionnaireTemplateStatus.DRAFT, unitId);
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
        List<String> assessmentNames = assessmentRepository.findAllNamesByUnitIdQuestionnaireTemplateIdAndStatus(unitId, questionnaireTemplateId, AssessmentStatus.IN_PROGRESS);
        if (CollectionUtils.isNotEmpty(assessmentNames)) {
            exceptionService.invalidRequestException("message.cannot.update.questionnaireTemplate.inProgress.assessment.linked", StringUtils.join(assessmentNames, ","));
        }
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateRepository.findByIdAndOrganizationIdAndDeletedFalse(questionnaireTemplateId, unitId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", questionnaireTemplateId);
        }
        questionnaireTemplate.delete();
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return true;
    }

}
