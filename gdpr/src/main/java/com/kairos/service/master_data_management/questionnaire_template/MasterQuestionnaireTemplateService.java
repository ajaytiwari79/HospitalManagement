package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.master_data.MasterQuestionnaireTemplateDTO;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data_management.asset_management.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
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

    public MasterQuestionnaireTemplate addQuestionnaireTemplate(Long countryId,Long organizationId, MasterQuestionnaireTemplateDTO templateDto) {
        MasterQuestionnaireTemplate exisiting = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId,organizationId,templateDto.getName());
        if (Optional.ofNullable(exisiting).isPresent()) {
            throw new DuplicateDataException("Template Exists with same name");
        }
        MasterQuestionnaireTemplate questionnaireTemplate = new MasterQuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        questionnaireTemplate.setOrganizationId(organizationId);
        questionnaireTemplate = buildQuestionniareTemplate(templateDto, questionnaireTemplate);
        try {
            questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.save(save(questionnaireTemplate));
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            throw new MongoException(e.getMessage());
        }
        return questionnaireTemplate;
    }


    /**
     * @param templateDto           contain data to create basic questionniare Template
     * @param questionnaireTemplate is template in which we add properties of Template Type and Asset Type if present
     * @return
     */
    public MasterQuestionnaireTemplate buildQuestionniareTemplate(MasterQuestionnaireTemplateDTO templateDto, MasterQuestionnaireTemplate questionnaireTemplate) {
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
                } else if (assetTypeMongoRepository.findByIdAndNonDeleted(UserContext.getCountryId(),UserContext.getOrgId(), assetTypeId) != null) {
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


    public Boolean deleteMasterQuestionnaireTemplate(Long countryId,Long organizationId, BigInteger id) {
        MasterQuestionnaireTemplate exist = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", id);
        }
        delete(exist);
        return true;
    }

    public MasterQuestionnaireTemplate updateQuestionniareTemplate(Long countryId, Long orgId,BigInteger id, MasterQuestionnaireTemplateDTO templateDto) {
        MasterQuestionnaireTemplate exisiting = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId,orgId,templateDto.getName().trim());
        if (Optional.ofNullable(exisiting).isPresent() && !id.equals(exisiting.getId())) {
            throw new DuplicateDataException("Template Exists with same name");
        }
        exisiting = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId,orgId,id);
        if (!Optional.ofNullable(exisiting).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "quetionnaire template", id);
        }
        exisiting.setName(templateDto.getName());
        exisiting.setDescription(templateDto.getDescription());
        exisiting = buildQuestionniareTemplate(templateDto, exisiting);
        try {
            exisiting = masterQuestionnaireTemplateMongoRepository.save(save(exisiting));
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            throw new MongoException(e.getMessage());
        }
        return exisiting;
    }

    /**
     * we get section[ {} ] as query response from mongo we are not using JsonInclude non empty so we can filter data
     * we are not using JsonInclude.NON_EMPTY so that we  get object with {id=null,name=null,description=null} for section
     * and send section as empty array after filtering data
     * @param countryId
     * @param id
     * @return
     */
    public MasterQuestionnaireTemplateResponseDTO getMasterQuestionniareTemplateWithSectionById(Long countryId,Long organizationId,BigInteger id) {
        MasterQuestionnaireTemplateResponseDTO templateResponseDto = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId,organizationId,id);
        if (templateResponseDto.getSections().get(0).getId() == null) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }


    public List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionniareTemplateWithSection(Long countryId,Long organizationId) {
        List<MasterQuestionnaireTemplateResponseDTO> templateResponseDtos = masterQuestionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId,organizationId);
        templateResponseDtos.forEach(template -> {
            if (template.getSections().get(0).getId() == null) {
                template.setSections(new ArrayList<>());
            }
        });
        return templateResponseDtos;

    }


}
