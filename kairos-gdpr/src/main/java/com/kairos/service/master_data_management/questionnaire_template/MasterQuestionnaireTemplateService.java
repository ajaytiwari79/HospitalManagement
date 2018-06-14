package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data_management.asset_management.StorageTypeMongoRepository;
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
import java.util.stream.Collectors;

import static com.kairos.constant.AppConstant.IDS_LIST;
import static com.kairos.constant.AppConstant.QUESTION_LIST;
import static com.kairos.constant.AppConstant.QUESTIONNIARE_SECTIONS;


@Service
public class MasterQuestionnaireTemplateService extends MongoBaseService {


    private Logger LOGGER = LoggerFactory.getLogger(MasterQuestionnaireTemplateService.class);


    @Inject
    private MasterQuestionnaireTemplateMongoRepository masterQuestionnaireTemplateMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private StorageTypeMongoRepository storageTypeMongoRepository;

    @Inject
    private MasterQuestionnaireSectionService masterQuestionnaireSectionService;

    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    @Inject
    private MasterQuestionMongoRepository masterQuestionMongoRepository;


    public MasterQuestionnaireTemplate addQuestionnaireTemplate(Long countryId, MasterQuestionnaireTemplate masterQuestionnaireTemplate) {
        MasterQuestionnaireTemplate exisiting = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId, masterQuestionnaireTemplate.getName().trim());
        if (Optional.ofNullable(exisiting).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "quetionnaire template", masterQuestionnaireTemplate.getName());
        }
        MasterQuestionnaireTemplate newQuestionnaireTemplate = new MasterQuestionnaireTemplate();
        if (QuestionnaireTemplateType.valueOf(masterQuestionnaireTemplate.getTemplateType()) == null) {
            throw new InvalidRequestException("template type not found for " + masterQuestionnaireTemplate.getTemplateType());
        } else {
            addTemplateTypeToQuestionnaireTemplate(masterQuestionnaireTemplate.getAssetType(), newQuestionnaireTemplate, QuestionnaireTemplateType.valueOf(masterQuestionnaireTemplate.getTemplateType().trim()));
            try {
                newQuestionnaireTemplate.setCountryId(countryId);
                newQuestionnaireTemplate.setName(masterQuestionnaireTemplate.getName());
                newQuestionnaireTemplate.setDescription(masterQuestionnaireTemplate.getDescription());
                newQuestionnaireTemplate = save(newQuestionnaireTemplate);

            } catch (MongoException e) {
                LOGGER.info(e.getMessage());
                throw new MongoException(e.getMessage());
            }
        }
        return newQuestionnaireTemplate;
    }

    public MasterQuestionnaireTemplateResponseDto getMasterQuestionniareTemplateWithSectionById(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplateQueryResult queryResult = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId, id);
        List<MasterQuestionnaireTemplateQueryResult> queryResults = new ArrayList<>();
        queryResults.add(queryResult);
        return getQuestionniareTemplateResponseWithSectionAndQuestionResponse(queryResults).get(0);
    }


    public List<MasterQuestionnaireTemplateResponseDto> getAllMasterQuestionniareTemplateWithSection(Long countryId) {
        List<MasterQuestionnaireTemplateQueryResult> queryResults = masterQuestionnaireTemplateMongoRepository.getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(countryId);
        return getQuestionniareTemplateResponseWithSectionAndQuestionResponse(queryResults);

    }

    public Map<BigInteger, MasterQuestion> filterNonDeletedQuestion(List<MasterQuestion> masterQuestions) {

        Map<BigInteger, MasterQuestion> nonDeletedQuestions = new HashMap<>();

        masterQuestions.forEach(masterQuestion -> {

            if (!masterQuestion.isDeleted()) {
                nonDeletedQuestions.put(masterQuestion.getId(), masterQuestion);
            }
        });

        return nonDeletedQuestions;

    }


    public List<MasterQuestionnaireTemplateResponseDto> getQuestionniareTemplateResponseWithSectionAndQuestionResponse(List<MasterQuestionnaireTemplateQueryResult> templateQueryResults) {

        Map<BigInteger, MasterQuestionnaireSection> sections = new HashMap<>();
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
                    questionnaireTemplateResponseDto.setSections(sectionResponseDtos);
                }

            }
            responseListQuestionniareResult.add(questionnaireTemplateResponseDto);

        });


        return responseListQuestionniareResult;


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

    /**
     * add questionnaire section list to questionnaire template
     * duplicate sections are allowed for different questionniare template
     * sections contain list of question
     * Map<String, Object> is used to get ids of section if any exception then delete section and delete question releated to sections
     */



    public void addTemplateTypeToQuestionnaireTemplate(BigInteger id, MasterQuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateType templateType) {

        switch (templateType) {
            case VENDOR:
                questionnaireTemplate.setTemplateType(templateType.value);
                break;
            case GENERAL:
                questionnaireTemplate.setTemplateType(templateType.value);
                break;
            case ASSET_TYPE:
                if (id == null) {
                    exceptionService.invalidRequestException("message.invalid.request", "asset type is null");
                } else {
                    if (storageTypeMongoRepository.findByIdAndNonDeleted(UserContext.getCountryId(), id) != null) {
                        questionnaireTemplate.setTemplateType(templateType.value);
                        questionnaireTemplate.setAssetType(id);
                    } else {
                        exceptionService.dataNotFoundByIdException("message.dataNotFound", "asset type", questionnaireTemplate.getAssetType());
                    }
                }
                break;
            case PROCESSING_ACTIVITY:
                questionnaireTemplate.setTemplateType(templateType.value);
                break;

            default:
                throw new InvalidRequestException("invalid request template type not found for " + templateType.value);


        }


    }


}
