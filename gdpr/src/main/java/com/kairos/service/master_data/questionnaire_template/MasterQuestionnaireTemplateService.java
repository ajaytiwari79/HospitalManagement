package com.kairos.service.master_data.questionnaire_template;


import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.master_data.MasterQuestionnaireTemplateDTO;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
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
     * @param organizationId
     * @param templateDto    contain data of Questionnaire template
     * @return Object of Questionnaire template with template type and asset type if template type is(ASSET_TYPE_KEY)
     */
    public MasterQuestionnaireTemplateDTO addQuestionnaireTemplate(Long countryId, Long organizationId, MasterQuestionnaireTemplateDTO templateDto) {
        MasterQuestionnaireTemplate existing = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId, organizationId, templateDto.getName());
        if (Optional.ofNullable(existing).isPresent()) {
            throw new DuplicateDataException("Template Exists with same name");
        }
        MasterQuestionnaireTemplate questionnaireTemplate = new MasterQuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        questionnaireTemplate.setOrganizationId(organizationId);
        questionnaireTemplate = buildQuestionnaireTemplate(templateDto, questionnaireTemplate);
        try {
            masterQuestionnaireTemplateMongoRepository.save(questionnaireTemplate);
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            throw new MongoException(e.getMessage());
        }
        templateDto.setId(questionnaireTemplate.getId());
        return templateDto;
    }


    /**
     * @param templateDto           create basic questionnaire Template without sections
     * @param questionnaireTemplate is template in which we add properties of Template Type and Asset Type if present
     * @return object of questionnaire template with template type
     * @throws InvalidRequestException; if template type enum value not exist
     * @descriptiom buildQuestionnaireTemplate()  build questionnaire template ,add template type to questionnaire template (Template Type enum) and if enum type
     * is ASSET_TYPE_KEY then add asset to template and return ;(addTemplateTypeToQuestionnaireTemplate)
     */
    public MasterQuestionnaireTemplate buildQuestionnaireTemplate(MasterQuestionnaireTemplateDTO templateDto, MasterQuestionnaireTemplate questionnaireTemplate) {
        if (QuestionnaireTemplateType.valueOf(templateDto.getTemplateType()) == null) {
            throw new InvalidRequestException("template type not found for" + templateDto.getTemplateType());
        }
        addTemplateTypeToQuestionnaireTemplate(templateDto.getAssetType(), questionnaireTemplate, QuestionnaireTemplateType.valueOf(templateDto.getTemplateType()));
        return questionnaireTemplate;
    }


    public void addTemplateTypeToQuestionnaireTemplate(BigInteger assetTypeId, MasterQuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateType templateType) {

        switch (templateType) {
            case VENDOR:
                questionnaireTemplate.setTemplateType(templateType.value);
                break;
            case GENERAL:
                questionnaireTemplate.setTemplateType(templateType.value);
                break;
            case ASSET_TYPE:
                if (assetTypeId == null) {
                    exceptionService.invalidRequestException("message.invalid.request", "asset type is null");
                } else if (assetTypeMongoRepository.findByIdAndNonDeleted(UserContext.getCountryId(), assetTypeId) != null) {
                    questionnaireTemplate.setTemplateType(templateType.value);
                    questionnaireTemplate.setAssetType(assetTypeId);
                } else {
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "asset type", assetTypeId);
                }
                break;
            case PROCESSING_ACTIVITY:
                questionnaireTemplate.setTemplateType(templateType.value);
                break;
            default:
                throw new InvalidRequestException("invalid request template type not found for " + templateType.value);
        }
    }


    /**
     * @param countryId
     * @param organizationId
     * @param id             - id of questionnaire template
     * @return true id deletion is successful
     * @description delete questionnaire template ,sections and question related to template.
     */
    public Boolean deleteMasterQuestionnaireTemplate(Long countryId, Long organizationId, BigInteger id) {
        MasterQuestionnaireTemplate exist = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire template", id);
        }
        masterQuestionnaireSectionService.deleteAll(countryId, organizationId, exist.getSections());
        delete(exist);
        return true;
    }


    /**
     * @param countryId
     * @param orgId       organization id to which questionnaire template belong
     * @param id          questionnaire template id
     * @param templateDto
     * @return updated Questionnaire template with basic data (name,description ,template type)
     */
    public MasterQuestionnaireTemplateDTO updateQuestionnaireTemplate(Long countryId, Long orgId, BigInteger id, MasterQuestionnaireTemplateDTO templateDto) {
        MasterQuestionnaireTemplate questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId, orgId, templateDto.getName().trim());
        if (Optional.ofNullable(questionnaireTemplate).isPresent() && !id.equals(questionnaireTemplate.getId())) {
            throw new DuplicateDataException("Template Exists with same name " + templateDto.getName());
        }
        questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, orgId, id);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "questionnaire template", id);
        }
        questionnaireTemplate.setName(templateDto.getName());
        questionnaireTemplate.setDescription(templateDto.getDescription());
        questionnaireTemplate = buildQuestionnaireTemplate(templateDto, questionnaireTemplate);
        try {
            masterQuestionnaireTemplateMongoRepository.save(questionnaireTemplate);
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            throw new MongoException(e.getMessage());
        }
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
    public MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionById(Long countryId, Long organizationId, BigInteger id) {
        MasterQuestionnaireTemplateResponseDTO templateResponseDto = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId, organizationId, id);
        if (templateResponseDto.getSections().get(0).getId() == null) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }


    /**
     * @param countryId
     * @param organizationId
     * @return Master Questionnaire template with sections list and question list (empty if sections are not present in template)
     * @description we get  section[ {} ] as query response from mongo on using group operation,
     * That why  we are not using JsonInclude.NON_EMPTY so we can get response of section as [{id=null,name=null,description=null}] instead of section [{}]
     * and filter section in application layer and send empty array of section []
     */
    public List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSection(Long countryId, Long organizationId) {
        List<MasterQuestionnaireTemplateResponseDTO> templateResponseDTOs = masterQuestionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId, organizationId);
        templateResponseDTOs.forEach(template -> {
            if (template.getSections().get(0).getId() == null) {
                template.setSections(new ArrayList<>());
            }
        });
        return templateResponseDTOs;

    }


}
