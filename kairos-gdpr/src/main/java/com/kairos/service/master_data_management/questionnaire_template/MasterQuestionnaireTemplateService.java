package com.kairos.service.master_data_management.questionnaire_template;


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


    /**
     * @param countryId
     * @param templateDto contain basic value for creating basic Questionniare template
     * @return
     */
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
        if (Optional.ofNullable(exisiting).isPresent()) {
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


    public MasterQuestionnaireTemplateResponseDto getMasterQuestionniareTemplateWithSectionById(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplateQueryResult queryResult = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId, id);
        List<MasterQuestionnaireTemplateQueryResult> queryResults = new ArrayList<>();
        queryResults.add(queryResult);
        return getQuestionniareTemplateResponseWithSectionAndQuestion(queryResults).get(0);
    }


    public List<MasterQuestionnaireTemplateResponseDto> getAllMasterQuestionniareTemplateWithSection(Long countryId) {
        List<MasterQuestionnaireTemplateQueryResult> queryResults = masterQuestionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId);
        return getQuestionniareTemplateResponseWithSectionAndQuestion(queryResults);

    }


    /**
     * @param masterQuestions list of question which need to be filter and Append in Questionniare sections
     * @return list of non deleted question
     */
    public Map<BigInteger, MasterQuestion> filterNonDeletedQuestion(List<MasterQuestion> masterQuestions) {
        Map<BigInteger, MasterQuestion> nonDeletedQuestions = new HashMap<>();
        masterQuestions.forEach(masterQuestion -> {

            if (!masterQuestion.isDeleted()) {
                nonDeletedQuestions.put(masterQuestion.getId(), masterQuestion);
            }
        });
        return nonDeletedQuestions;

    }

    public List<MasterQuestionnaireTemplateResponseDto> getQuestionniareTemplateResponseWithSectionAndQuestion(List<MasterQuestionnaireTemplateQueryResult> templateQueryResults) {

        Map<BigInteger, MasterQuestion> questions = new HashMap<>();

        templateQueryResults.forEach(masterQuestionnaireTemplateQueryResult -> {
            if (masterQuestionnaireTemplateQueryResult.getQuestions().size() != 0) {
                questions.putAll(filterNonDeletedQuestion(masterQuestionnaireTemplateQueryResult.getQuestions()));
            }

        });
        List<MasterQuestionnaireTemplateResponseDto> responseListQuestionniareResult = new ArrayList<>();
        templateQueryResults.forEach(questionnaireResult -> {

            MasterQuestionnaireTemplateResponseDto questionnaireTemplateResponseDto =
                    new MasterQuestionnaireTemplateResponseDto(questionnaireResult.getId(), questionnaireResult.getName(), questionnaireResult.getDescription());
            questionnaireTemplateResponseDto.setAssetType(questionnaireResult.getAssetType());
            questionnaireTemplateResponseDto.setTemplateType(questionnaireResult.getTemplateType());

            if (questionnaireResult.getSections().size() != 0) {

                List<MasterQuestionnaireSectionResponseDto> sectionResponseDtos = new ArrayList<>();
                for (MasterQuestionnaireSection questionnaireSection : questionnaireResult.getSections()) {
                    if (!questionnaireSection.isDeleted()) {
                        MasterQuestionnaireSectionResponseDto sectionResponseDto = new MasterQuestionnaireSectionResponseDto();
                        sectionResponseDto.setId(questionnaireSection.getId());
                        sectionResponseDto.setTitle(questionnaireSection.getTitle());
                        sectionResponseDto.setCountryId(questionnaireSection.getCountryId());
                        List<MasterQuestion> questionList = new ArrayList<>();
                        for (BigInteger id : questionnaireSection.getQuestions()) {
                            questionList.add(questions.get(id));
                        }
                        sectionResponseDto.setQuestions(questionList);
                        sectionResponseDtos.add(sectionResponseDto);
                    }
                    questionnaireTemplateResponseDto.setSections(sectionResponseDtos); }

            }
            responseListQuestionniareResult.add(questionnaireTemplateResponseDto);

        });

        return responseListQuestionniareResult;

    }


}
