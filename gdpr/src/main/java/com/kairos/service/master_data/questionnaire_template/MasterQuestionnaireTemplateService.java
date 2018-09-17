package com.kairos.service.master_data.questionnaire_template;


import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.AssetAttributeName;
import com.kairos.enums.ProcessingActivityAttributeName;
import com.kairos.dto.gdpr.master_data.MasterQuestionnaireTemplateDTO;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.master_data.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistence.repository.master_data.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistence.repository.master_data.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;


@Service
public class MasterQuestionnaireTemplateService extends MongoBaseService {


    private Logger LOGGER = LoggerFactory.getLogger(MasterQuestionnaireTemplateService.class);


    @Inject
    private MasterQuestionnaireTemplateMongoRepository masterQuestionnaireTemplateMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private MasterQuestionnaireSectionService masterQuestionnaireSectionService;

    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    @Inject
    private MasterQuestionMongoRepository masterQuestionMongoRepository;


    /**
     * @param countryId
     * @param templateDto contain data of Questionnaire template
     * @return Object of Questionnaire template with template type and asset type if template type is(ASSET_TYPE_KEY)
     */
    public MasterQuestionnaireTemplateDTO addQuestionnaireTemplate(Long countryId, MasterQuestionnaireTemplateDTO templateDto) {
        MasterQuestionnaireTemplate previousTemplate = masterQuestionnaireTemplateMongoRepository.findByName(countryId, templateDto.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            throw new DuplicateDataException("Template Exists with same name");
        }
        MasterQuestionnaireTemplate questionnaireTemplate = new MasterQuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        if (QuestionnaireTemplateType.valueOf(templateDto.getTemplateType()) == null) {
            exceptionService.invalidRequestException("template type not found for" + templateDto.getTemplateType());
        }
        addTemplateTypeToQuestionnaireTemplate(countryId, questionnaireTemplate, templateDto);
        masterQuestionnaireTemplateMongoRepository.save(questionnaireTemplate);
        templateDto.setId(questionnaireTemplate.getId());
        return templateDto;
    }

    /**
     * @param templateDto
     * @param questionnaireTemplate
     */
    private void addTemplateTypeToQuestionnaireTemplate(Long countryId, MasterQuestionnaireTemplate questionnaireTemplate, MasterQuestionnaireTemplateDTO templateDto) {

        QuestionnaireTemplateType templateType = QuestionnaireTemplateType.valueOf(templateDto.getTemplateType());
        switch (templateType) {
            case VENDOR:
                questionnaireTemplate.setTemplateType(templateType);
                break;
            case GENERAL:
                questionnaireTemplate.setTemplateType(templateType);
                break;
            case ASSET_TYPE:
                if (templateDto.isDefaultAssetTemplate()) {
                    MasterQuestionnaireTemplate previousTemplate=masterQuestionnaireTemplateMongoRepository.findDefaultAssetQuestionnaireTemplate(countryId);
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.invalidRequestException("message.invalid.request", "Default Questionnaire Template is Already Present");
                    }
                    questionnaireTemplate.setTemplateType(templateType);
                    questionnaireTemplate.setDefaultAssetTemplate(true);
                } else {
                    addAssetTypeToQuestionnaireTemplate(countryId, questionnaireTemplate, templateDto);
                }

                break;
            case PROCESSING_ACTIVITY:
                questionnaireTemplate.setTemplateType(templateType);
                break;
            default:
                throw new InvalidRequestException("invalid request template type not found for " + templateType);
        }
    }

    private void addAssetTypeToQuestionnaireTemplate(Long countryId, MasterQuestionnaireTemplate questionnaireTemplate, MasterQuestionnaireTemplateDTO templateDto) {
        if (!Optional.ofNullable(templateDto.getAssetType()).isPresent()) {
            exceptionService.invalidRequestException("message.invalid.request", "asset type is not selected");
        } else {
            AssetType assetType = assetTypeMongoRepository.findByIdAndCountryId(countryId, templateDto.getAssetType());
            if (!Optional.ofNullable(assetType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", templateDto.getAssetType());
            } else if (!assetType.getSubAssetTypes().contains(templateDto.getAssetSubType())) {
                exceptionService.invalidRequestException("message.invalid.request", "Sub Asset Type is Not Selected");
            } else {
                questionnaireTemplate.setAssetType(templateDto.getAssetType());
                questionnaireTemplate.setAssetSubType(templateDto.getAssetSubType());
            }
        }
    }

    /**
     * @param countryId
     * @param id        - id of questionnaire template
     * @return true id deletion is successful
     * @description delete questionnaire template ,sections and question related to template.
     */
    public Boolean deleteMasterQuestionnaireTemplate(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplate exist = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire template", id);
        }
        delete(exist);
        return true;
    }


    /**
     * @param countryId
     * @param id          questionnaire template id
     * @param templateDto
     * @return updated Questionnaire template with basic data (name,description ,template type)
     */
    public MasterQuestionnaireTemplateDTO updateQuestionnaireTemplate(Long countryId, BigInteger id, MasterQuestionnaireTemplateDTO templateDto) {
        MasterQuestionnaireTemplate questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByName(countryId, templateDto.getName().trim());
        if (Optional.ofNullable(questionnaireTemplate).isPresent() && !id.equals(questionnaireTemplate.getId())) {
            throw new DuplicateDataException("Template Exists with same name " + templateDto.getName());
        }
        questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "questionnaire template", id);
        }
        questionnaireTemplate.setName(templateDto.getName());
        questionnaireTemplate.setDescription(templateDto.getDescription());
        addTemplateTypeToQuestionnaireTemplate(countryId, questionnaireTemplate, templateDto);
        masterQuestionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return templateDto;
    }

    /**
     * @param countryId
     * @param id        questionnaire template id
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionById(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplateResponseDTO templateResponseDto = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId, id);
        if (templateResponseDto.getSections().get(0).getId() == null) {
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
    public List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSection(Long countryId) {
        List<MasterQuestionnaireTemplateResponseDTO> templateResponseDTOs = masterQuestionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId);
        templateResponseDTOs.forEach(template -> {
            if (template.getSections().get(0).getId() == null) {
                template.setSections(new ArrayList<>());
            }
        });
        return templateResponseDTOs;

    }


    public Object[] getQuestionnaireTemplateAttributeNames(String templateType) {

        if (QuestionnaireTemplateType.valueOf(templateType) == null) {
            throw new InvalidRequestException("template type not found for" + templateType);
        }
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


}
