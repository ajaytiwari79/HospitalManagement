package com.kairos.service.agreement_template;


import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.master_data.AgreementSectionDTO;
import com.kairos.dto.master_data.ClauseBasicDTO;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.persistance.model.agreement_template.AgreementSectionClauseWrapper;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistance.repository.clause.ClauseMongoRepository;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.service.clause.ClauseService;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.*;


@Service
public class AgreementSectionService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionService.class);

    @Inject
    private AgreementSectionMongoRepository agreementSectionMongoRepository;

    @Inject
    private ClauseMongoRepository clauseMongoRepository;
    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseService clauseService;

    //todo add update section function also
    public PolicyAgreementTemplate createUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long countryId, Long organizationId, BigInteger templateId, List<AgreementSectionDTO> agreementSectionDTOs) {

        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, organizationId, templateId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", templateId);
        }
        Boolean flag = false;
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {

            if (Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                flag = true;
                break;
            }
        }
        Map<String, Object> agreementSections = new HashMap<>();
        if (flag) {
            //todo add updated sections functions also
        } else {

            agreementSections = createAggrementTemplateSectionAndClause(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections((List<BigInteger>) agreementSections.get(IDS_LIST));
        }
        try {
            policyAgreementTemplate = policyAgreementTemplateRepository.save(policyAgreementTemplate);
        } catch (MongoException e) {
            LOGGER.warn("Agreement template exception while saving template " + policyAgreementTemplate.getName());
            remove((List<BigInteger>) agreementSections.get(IDS_LIST), AgreementSection.class);
            throw new RuntimeException(e);
        }

        return policyAgreementTemplate;

    }


    /**
     * @param countryId
     * @param organizationId
     * @param agreementSectionDTOS
     * @param policyAgreementTemplate - object for inheriting properties (orgnaization Types,organization Sub Types,Service Category  and Sub Service Category) for new clauses
     * @return
     */
    public Map<String, Object> createAggrementTemplateSectionAndClause(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSections(agreementSectionDTOS);
        List<AgreementSection> agreementSectionList = new ArrayList<>();
        List<ClauseBasicDTO> changedClausesList = new ArrayList<>();
        List<ClauseBasicDTO> newClauseBasicDTOList = new ArrayList<>();
        List<BigInteger> changedClauseIdsList = new ArrayList<>();
        Map<String, AgreementSectionClauseWrapper> agreementSectionClauseAndClauseDtoHashMap = new HashMap<>();

        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {
            AgreementSection agreementSection = new AgreementSection(countryId, agreementSectionDTO.getName());
            AgreementSectionClauseWrapper sectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();
            agreementSection.setOrganizationId(organizationId);
            if (Optional.ofNullable(agreementSectionDTO.getClauses()).isPresent() && !agreementSectionDTO.getClauses().isEmpty()) {
                List<BigInteger> unchangedClauseidList = new ArrayList<>();
                List<ClauseBasicDTO> changedClauseBelongToSection = new ArrayList<>();
                List<ClauseBasicDTO> newClauseBelongToSection = new ArrayList<>();
                agreementSectionDTO.getClauses().forEach(clauseBasicDTO -> {
                    if (!Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                        newClauseBasicDTOList.add(clauseBasicDTO);
                        newClauseBelongToSection.add(clauseBasicDTO);
                    } else if (clauseBasicDTO.getRequireUpdate()) {
                        changedClauseIdsList.add(clauseBasicDTO.getId());
                        changedClausesList.add(clauseBasicDTO);
                        changedClauseBelongToSection.add(clauseBasicDTO);
                    } else {
                        unchangedClauseidList.add(clauseBasicDTO.getId());
                    }
                });
                sectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSection);
                sectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSection);
                agreementSection.setClauses(unchangedClauseidList);
            }
            agreementSectionClauseAndClauseDtoHashMap.put(agreementSection.getName(), sectionClauseAndClauseDtoWrapper);
            agreementSectionList.add(agreementSection);
        }
        if (!newClauseBasicDTOList.isEmpty() || !changedClauseIdsList.isEmpty()) {
            List<Clause> newCreatedClausesList = new ArrayList<>();
            if (!newClauseBasicDTOList.isEmpty()) {
                newCreatedClausesList = clauseService.createNewClauseUsingAgreementTemplateMetadata(countryId, organizationId, newClauseBasicDTOList, policyAgreementTemplate);
            }
            List<Clause> exisitingClauseList = clauseMongoRepository.getClauseListByIds(countryId, organizationId, changedClauseIdsList);
            agreementSectionList = updateExisitingClauseAndAddToAgreementSection(agreementSectionClauseAndClauseDtoHashMap, exisitingClauseList, agreementSectionList, newCreatedClausesList);
        }
        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        try {
            agreementSectionList = agreementSectionMongoRepository.saveAll(sequenceGenerator(agreementSectionList));
            agreementSectionList.forEach(agreementSection -> {
                agreementSectionIdList.add(agreementSection.getId());
            });
        } catch (MongoException e) {
            LOGGER.warn("Agreement section exception while saving sections:" + e.getMessage());
            throw new RuntimeException(e);
        }
        result.put(IDS_LIST, agreementSectionIdList);
        return result;
    }


    /**
     * @param agreementSectionClauseAndClauseDtoHashMap
     * @param changedClausesList
     * @param agreementSectionList
     * @param newClauseList
     * @return
     */
    public List<AgreementSection> updateExisitingClauseAndAddToAgreementSection(Map<String, AgreementSectionClauseWrapper> agreementSectionClauseAndClauseDtoHashMap, List<Clause> changedClausesList, List<AgreementSection> agreementSectionList, List<Clause> newClauseList) {

        Map<BigInteger, Clause> updateClauseMap = new HashMap<>();
        changedClausesList.forEach(clause -> {
            updateClauseMap.put(clause.getId(), clause);
        });

        Map<String, Clause> newCreatedClauseMap = new HashMap<>();
        newClauseList.forEach(clause -> {
            newCreatedClauseMap.put(clause.getTitle(), clause);
        });

        List<Clause> updateExisitingClauseList = new ArrayList<>();
        for (AgreementSection agreementSection : agreementSectionList) {

            AgreementSectionClauseWrapper agreementSectionClauseWrapper = agreementSectionClauseAndClauseDtoHashMap.get(agreementSection.getName());
            List<ClauseBasicDTO> changedClauseBasicDTOList = agreementSectionClauseWrapper.getChangedClausesList();
            List<ClauseBasicDTO> newClauseBasicDTOList = agreementSectionClauseWrapper.getNewClauses();
            updateExisitingClauseList = agreementSectionUpdateClauseListAndAddIdsToSection(changedClauseBasicDTOList, updateClauseMap, updateExisitingClauseList, agreementSection);
            addNewCreatedClauseIdsToAgreementSections(newClauseBasicDTOList, newCreatedClauseMap, agreementSection);

        }
        if (updateExisitingClauseList.isEmpty()) {
            clauseMongoRepository.saveAll(sequenceGenerator(updateExisitingClauseList));

        }
        return agreementSectionList;

    }


    /**
     * @param clauseBasicDTOList        - list of clauses which were changed by user
     * @param updateClauseMap           map contain clause as value and key is clause id.
     * @param updateExisitingClauseList List of clauses which are changed and to reflect the changes in clauses
     * @param agreementSection          contain list clause ids, add updated clauses to agreement sections
     * @return
     */
    public List<Clause> agreementSectionUpdateClauseListAndAddIdsToSection(List<ClauseBasicDTO> clauseBasicDTOList, Map<BigInteger, Clause> updateClauseMap, List<Clause> updateExisitingClauseList, AgreementSection agreementSection) {

        List<BigInteger> clauseIdList = agreementSection.getClauses();
        clauseBasicDTOList.forEach(clauseBasicDTO -> {
            Clause clause = updateClauseMap.get(clauseBasicDTO.getId());
            clause.setDescription(clauseBasicDTO.getDescription());
            updateExisitingClauseList.add(clause);
            clauseIdList.add(clauseBasicDTO.getId());

        });
        return updateExisitingClauseList;
    }


    /**
     * @param clauseBasicDTOS         -dto contain list of clause which are created at the time of section creation
     * @param newCreatedClauseHashMap -contain new created clause value coressponding to its title
     * @param agreementSection        -agreement setion contain name and list ids of clauses,
     * @description this method is used in updateExisitingClauseAndAddToAgreementSection .this method add ids of new created clause  to section.
     */
    public void addNewCreatedClauseIdsToAgreementSections(List<ClauseBasicDTO> clauseBasicDTOS, Map<String, Clause> newCreatedClauseHashMap, AgreementSection agreementSection) {

        List<BigInteger> clauseIdList = agreementSection.getClauses();
        clauseBasicDTOS.forEach(clauseBasicDTO -> {
            Clause clause = newCreatedClauseHashMap.get(clauseBasicDTO.getTitle());
            clauseIdList.add(clause.getId());
        });
        agreementSection.setClauses(clauseIdList);

    }


    //fixme update method
    public Boolean deleteAgreementSection(BigInteger id) {

        AgreementSection exist = agreementSectionMongoRepository.findByid(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return true;
        }
        throw new DataNotFoundByIdException(" agreement section for id " + id + " not exist");

    }


    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId, BigInteger id) {

        AgreementSectionResponseDTO exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

    }


    public List<AgreementSectionResponseDTO> getAllAgreementSection(Long countryId) {

        return agreementSectionMongoRepository.getAllAgreementSectionWithData(countryId);

    }


    public List<AgreementSectionResponseDTO> getAgreementSectionWithDataList(Long countryId, Set<BigInteger> ids) {
        return agreementSectionMongoRepository.getAgreementSectionWithDataList(countryId, ids);

    }


    public void checkForDuplicacyInTitleOfAgreementSections(List<AgreementSectionDTO> agreementSectionDTOS) {
        List<String> titles = new ArrayList<>();
        for (AgreementSectionDTO questionnaireSectionDto : agreementSectionDTOS) {
            if (titles.contains(questionnaireSectionDto.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", questionnaireSectionDto.getName());
            }
            titles.add(questionnaireSectionDto.getName().toLowerCase());
        }
    }

}

