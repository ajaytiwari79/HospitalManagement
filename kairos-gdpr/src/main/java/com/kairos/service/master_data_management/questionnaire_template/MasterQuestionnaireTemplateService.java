package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.master_data.MasterQuestionnaireTemplateDto;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data_management.asset_management.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireSectionResponseDto;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateQueryResult;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDto;
import com.kairos.service.MongoBaseService;
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

    public MasterQuestionnaireTemplate addQuestionnaireTemplate(Long countryId, MasterQuestionnaireTemplateDto templateDto) {
        MasterQuestionnaireTemplate exisiting = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId, templateDto.getName().trim());
        if (Optional.ofNullable(exisiting).isPresent()) {
            throw new DuplicateDataException("Template Exists with same name");
        }
        MasterQuestionnaireTemplate questionnaireTemplate = new MasterQuestionnaireTemplate(templateDto.getName(), countryId, templateDto.getDescription());
        questionnaireTemplate = buildQuestionniareTemplate(templateDto, questionnaireTemplate);
        try {
            questionnaireTemplate = save(questionnaireTemplate);
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
    public MasterQuestionnaireTemplate buildQuestionniareTemplate(MasterQuestionnaireTemplateDto templateDto, MasterQuestionnaireTemplate questionnaireTemplate) {
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


    public Boolean deleteMasterQuestionnaireTemplate(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplate exist = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", id);
        }
        exist.setDeleted(true);
        save(exist);
        return true;
    }

    public MasterQuestionnaireTemplate updateQuestionniareTemplate(Long countryId, BigInteger id, MasterQuestionnaireTemplateDto templateDto) {
        MasterQuestionnaireTemplate exisiting = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId, templateDto.getName().trim());
        if (Optional.ofNullable(exisiting).isPresent() && !id.equals(exisiting.getId())) {
            throw new DuplicateDataException("Template Exists with same name");
        }
        exisiting = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exisiting).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "quetionnaire template", id);
        }
        exisiting.setName(templateDto.getName());
        exisiting.setDescription(templateDto.getDescription());
        exisiting = buildQuestionniareTemplate(templateDto, exisiting);
        try {
            exisiting = save(exisiting);
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
    public MasterQuestionnaireTemplateResponseDto getMasterQuestionniareTemplateWithSectionById(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplateResponseDto templateResponseDto = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId, id);
        if (templateResponseDto.getSections().get(0).getId() == null) {
            templateResponseDto.setSections(new ArrayList<>());
        }
        return templateResponseDto;
    }


    public List<MasterQuestionnaireTemplateResponseDto> getAllMasterQuestionniareTemplateWithSection(Long countryId) {
        List<MasterQuestionnaireTemplateResponseDto> templateResponseDtos = masterQuestionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId);
        templateResponseDtos.forEach(template -> {
            if (template.getSections().get(0).getId() == null) {
                template.setSections(new ArrayList<>());
            }
        });
        return templateResponseDtos;

    }


}
