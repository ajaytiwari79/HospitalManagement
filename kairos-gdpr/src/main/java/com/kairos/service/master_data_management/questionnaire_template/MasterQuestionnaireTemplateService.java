package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data_management.asset_management.StorageTypeService;
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
    private StorageTypeService storageTypeService;


    @Inject
    private MasterQuestionnaireSectionService masterQuestionnaireSectionService;

    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;


    public MasterQuestionnaireTemplate addQuestionnaireTemplate(Long countryId, MasterQuestionnaireTemplate masterQuestionnaireTemplate) {


        MasterQuestionnaireTemplate exisiting = masterQuestionnaireTemplateMongoRepository.findByCountryIdAndName(countryId, masterQuestionnaireTemplate.getName().trim());

        if (Optional.ofNullable(exisiting).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "quetionnaire template", masterQuestionnaireTemplate.getName());
        }
        MasterQuestionnaireTemplate questionnaireTemplate = new MasterQuestionnaireTemplate();
        if (QuestionnaireTemplateType.ASSET_TYPE.value.equals(masterQuestionnaireTemplate.getTemplateType())) {
            if (storageTypeService.getStorageType(countryId, masterQuestionnaireTemplate.getAssetType()) != null) {
                questionnaireTemplate.setAssetType(masterQuestionnaireTemplate.getAssetType());
            } else {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "asset type", masterQuestionnaireTemplate.getAssetType());
            }
        } else {
            questionnaireTemplate.setCountryId(countryId);
            questionnaireTemplate.setName(masterQuestionnaireTemplate.getName());
            questionnaireTemplate.setDescription(masterQuestionnaireTemplate.getDescription());
            questionnaireTemplate.setTemplateType(masterQuestionnaireTemplate.getTemplateType());

        }
        return save(questionnaireTemplate);

    }


    public List<MasterQuestionnaireTemplateResponseDto> getAllMasterQuestionniareTemplate(Long countryId) {
        return masterQuestionnaireTemplateMongoRepository.getAllBasicMasterQuestionnaireTemplate(countryId);

    }


    public Boolean deleteMasterQuestionnaireTemplate(Long countryId, BigInteger id) {
        MasterQuestionnaireTemplate exist = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "asset type", id);
        }
        exist.setDeleted(true);
        save(exist);
        return true;

    }


    public MasterQuestionnaireTemplate addMasterQuestionnaireSectionToQuestionnaireTemplate(Long countryId, BigInteger id, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDto) {
        MasterQuestionnaireTemplate existing = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(existing).isPresent()) {
            throw new DataNotFoundByIdException("questionnaire template not found by id " + id);
        }
        Map<String, Object> questionnaireSection = new HashMap<>();
        questionnaireSection = masterQuestionnaireSectionService.addQuestionnaireSection(countryId, masterQuestionnaireSectionDto);
        existing.setSections((Set<BigInteger>) questionnaireSection.get("ids"));
        try {
            existing = save(existing);
        } catch (Exception e) {
            masterQuestionnaireSectionRepository.deleteAll((Set<MasterQuestionnaireSection>) questionnaireSection.get("sections"));
            LOGGER.info(e.getMessage());

        }
        return existing;

    }


}
