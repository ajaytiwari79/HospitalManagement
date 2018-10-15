package com.kairos.service.agreement_template;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.gdpr.master_data.AgreementSectionDTO;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistence.repository.clause.ClauseMongoRepository;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.service.clause.ClauseService;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


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
    private PolicyAgreementTemplateService policyAgreementTemplateService;
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseService clauseService;


    public List<AgreementSectionResponseDTO> createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long countryId, BigInteger templateId, List<AgreementSectionDTO> agreementSectionDTOs) {

        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndCountryId(countryId, templateId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", templateId);
        }
        checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(agreementSectionDTOs);
        List<BigInteger> agreementSectionIdList = createOrupdateSectionAndSubSectionOfAgreementTemplate(countryId, agreementSectionDTOs, policyAgreementTemplate);
        policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        return policyAgreementTemplateService.getAllAgreementSectionsAndSubSectionsOfAgreementTemplateByTemplateId(countryId, templateId);
    }


    /**
     * @param countryId
     * @param templateId - Policy Agreement template id
     * @param id         -agreement section id
     * @return -true on successful deletion of section
     */
    public boolean deleteAgreementSection(Long countryId, BigInteger templateId, BigInteger id) {

        AgreementSection agreementSection = agreementSectionMongoRepository.findByIdAndCountryId(countryId, id);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Agreement section " + id);
        }
        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndCountryId(countryId, templateId);
        List<BigInteger> agreementSectionIdList = policyAgreementTemplate.getAgreementSections();
        agreementSectionIdList.remove(id);
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        agreementSectionMongoRepository.delete(agreementSection);
        return true;
    }


    /**
     * @param countryId
     * @param sectionId
     * @param subSectionId
     * @return
     */
    public boolean deleteAgreementSubSection(Long countryId, BigInteger sectionId, BigInteger subSectionId) {

        AgreementSection agreementSection = agreementSectionMongoRepository.findByIdAndCountryId(countryId, sectionId);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Agreement section " + sectionId);
        }
        agreementSection.getSubSections().remove(subSectionId);
        agreementSectionMongoRepository.safeDelete(subSectionId);
        agreementSectionMongoRepository.save(agreementSection);
        return true;
    }

    /**
     * @param countryId
     * @param agreementSectionDTOs
     * @param agreementSubSectionsCoresspondingToAgreementSection
     * @param globalAgreementSectionAndClauseDTOListHashMap
     */
    private void buildAgreementSectionAndSubSection(Long countryId, List<AgreementSectionDTO> agreementSectionDTOs, Map<AgreementSection, List<AgreementSection>> agreementSubSectionsCoresspondingToAgreementSection,
                                                    Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap) {

        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {
            AgreementSection agreementSection = new AgreementSection(countryId, agreementSectionDTO.getTitle(), agreementSectionDTO.getOrderedIndex(), false);
            List<AgreementSection> subSectionList = new ArrayList<>();
            for (AgreementSectionDTO agreementSubSectionDTO : agreementSectionDTO.getSubSections()) {
                AgreementSection subSection = new AgreementSection(countryId, agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex(), true);
                if (!agreementSubSectionDTO.getClauses().isEmpty()) {
                    globalAgreementSectionAndClauseDTOListHashMap.put(subSection, agreementSubSectionDTO.getClauses());
                }
                subSectionList.add(subSection);
            }
            if (!agreementSectionDTO.getClauses().isEmpty()) {
                globalAgreementSectionAndClauseDTOListHashMap.put(agreementSection, agreementSectionDTO.getClauses());
            }
            agreementSubSectionsCoresspondingToAgreementSection.put(agreementSection, subSectionList);
        }

    }

    /**
     * @param agreementSectionList
     * @param agreementSubSectionListAndCoresspondingToAgreementSectionMap
     */
    private void saveAgreementSectionAndSubSectionIfClauseNotExist(List<AgreementSection> agreementSectionList, Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap) {
        List<AgreementSection> subSectionList = new ArrayList<>();
        agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSections) -> subSectionList.addAll(subSections));
        if (CollectionUtils.isNotEmpty(subSectionList)) {
            agreementSectionMongoRepository.saveAll(getNextSequence(subSectionList));
        }
        for (Map.Entry<AgreementSection, List<AgreementSection>> entrySet : agreementSubSectionListAndCoresspondingToAgreementSectionMap.entrySet()) {
            AgreementSection agreementSection = entrySet.getKey();
            List<AgreementSection> agreementSubSections = entrySet.getValue();
            if (CollectionUtils.isNotEmpty(agreementSubSections)) {
                entrySet.getKey().getSubSections().addAll(agreementSubSections.stream().map(AgreementSection::getId).collect(Collectors.toList()));
            }
            agreementSectionList.add(agreementSection);
        }

    }

    /**
     * @description
     * newAgreementSectionDTOList  - for first time call of save operation Section list equals to newAgreementSectionDTOList,
     * and if save call is second time and section contain id then sections with no id equals to newAgreementSectionDTOList.
     * method create section ,Sub Section clause and update clauses,section ,sub section.
     * @param countryId
     * @param agreementSectionDTOS
     * @param policyAgreementTemplate
     */
    public List<BigInteger> createOrupdateSectionAndSubSectionOfAgreementTemplate(Long countryId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap = new HashMap<>();
        List<AgreementSectionDTO> newAgreementSectionDTOList = new ArrayList<>();
        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        Map<BigInteger, List<AgreementSection>> newSubSectionListCoresspondingToSection = new HashMap<>();
        Map<BigInteger, AgreementSectionDTO> agreementSectionDTOMap = new HashMap<>();
        agreementSectionDTOS.forEach(agreementSectionDTO -> {
            if (!Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                newAgreementSectionDTOList.add(agreementSectionDTO);
            } else {
                List<AgreementSection> subSectionListCoresspondingToAgreementSection = new ArrayList<>();
                agreementSectionIdList.add(agreementSectionDTO.getId());
                agreementSectionDTOMap.put(agreementSectionDTO.getId(), agreementSectionDTO);
                if (CollectionUtils.isNotEmpty(agreementSectionDTO.getSubSections())) {
                    List<BigInteger> subSectionIdList = new ArrayList<>();
                    for (AgreementSectionDTO agreementSubSectionDTO : agreementSectionDTO.getSubSections()) {
                        if (Optional.ofNullable(agreementSubSectionDTO.getId()).isPresent()) {
                            subSectionIdList.add(agreementSubSectionDTO.getId());
                            agreementSectionDTOMap.put(agreementSubSectionDTO.getId(), agreementSubSectionDTO);
                        } else {
                            AgreementSection subSection = new AgreementSection(countryId, agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex(), true);
                            if (CollectionUtils.isNotEmpty(agreementSubSectionDTO.getClauses())) {
                                globalAgreementSectionAndClauseDTOListHashMap.put(subSection, agreementSubSectionDTO.getClauses());
                            }
                            subSectionListCoresspondingToAgreementSection.add(subSection);
                        }
                    }
                    agreementSectionIdList.addAll(subSectionIdList);
                }
                newSubSectionListCoresspondingToSection.put(agreementSectionDTO.getId(), subSectionListCoresspondingToAgreementSection);
            }
        });
        Map<AgreementSection, List<AgreementSection>> agreementSubSectionListCoresspondingToAgreementSection = new HashMap<>();
        if (CollectionUtils.isNotEmpty(newAgreementSectionDTOList)) {
            buildAgreementSectionAndSubSection(countryId, newAgreementSectionDTOList, agreementSubSectionListCoresspondingToAgreementSection, globalAgreementSectionAndClauseDTOListHashMap);
        }
        List<AgreementSection> agreementSections = agreementSectionMongoRepository.findAgreementSectionByIds(countryId, agreementSectionIdList);
        updateExistingSectionAndSubSection(agreementSections, agreementSectionDTOMap, newSubSectionListCoresspondingToSection, globalAgreementSectionAndClauseDTOListHashMap
                , agreementSubSectionListCoresspondingToAgreementSection);
        List<AgreementSection> agreementSectionList = new ArrayList<>();
        if (!globalAgreementSectionAndClauseDTOListHashMap.isEmpty()) {
            agreementSectionList = saveAndUpdateClauseOfAgreementSection(countryId, globalAgreementSectionAndClauseDTOListHashMap, agreementSubSectionListCoresspondingToAgreementSection, policyAgreementTemplate);
        } else {
            saveAgreementSectionAndSubSectionIfClauseNotExist(agreementSectionList, agreementSubSectionListCoresspondingToAgreementSection);
        }
        agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        return agreementSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList());
    }


    /**
     * @param agreementSections
     * @param agreementSectionDTOMap
     * @param newSubSectionListCoresspondingToSection                - sub section list coressponding to exisitng agreement section id
     * @param globalAgreementSectionAndClauseDTOListHashMap          - Clause DTO list coressponding to sections and sub sections
     * @param agreementSubSectionListCoresspondingToAgreementSection list of agreement sub section coressponding to section
     */
    private void updateExistingSectionAndSubSection(List<AgreementSection> agreementSections, Map<BigInteger, AgreementSectionDTO> agreementSectionDTOMap,
                                                    Map<BigInteger, List<AgreementSection>> newSubSectionListCoresspondingToSection,
                                                    Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap,
                                                    Map<AgreementSection, List<AgreementSection>> agreementSubSectionListCoresspondingToAgreementSection) {
        for (AgreementSection agreementSection : agreementSections) {
            AgreementSectionDTO agreementSectionDTO = agreementSectionDTOMap.get(agreementSection.getId());
            agreementSection.setTitle(agreementSectionDTO.getTitle());
            agreementSection.setOrderedIndex(agreementSectionDTO.getOrderedIndex());
            if (!agreementSectionDTO.getClauses().isEmpty()) {
                globalAgreementSectionAndClauseDTOListHashMap.put(agreementSection, agreementSectionDTO.getClauses());
            }
            if (agreementSection.isSubSection()) {
                agreementSubSectionListCoresspondingToAgreementSection.put(agreementSection, new ArrayList<>());

            } else {
                agreementSubSectionListCoresspondingToAgreementSection.put(agreementSection, newSubSectionListCoresspondingToSection.get(agreementSection.getId()));
            }
        }

    }

    /**
     * @param countryId
     * @param globalAgreementSectionAndClauseDTOListHashMap
     * @param agreementTemplate
     *///todo refactoring code
    private List<AgreementSection> saveAndUpdateClauseOfAgreementSection(Long countryId, Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap,
                                                                         Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap, PolicyAgreementTemplate agreementTemplate) {

        List<Clause> clauseList = new ArrayList<>();
        Map<AgreementSection, List<Clause>> clauseListCoresspondingToAgreementSection = new HashMap<>();
        Map<AgreementSection, List<ClauseBasicDTO>> exisitingClauseListCoresspondingToAgreementSections = new HashMap<>();
        Map<AgreementSection, Map<BigInteger, Integer>> clauseOrderCoresspondingToAgreementSectionAndSubSection = new HashMap<>();
        List<BigInteger> alteredClauseIdList = new ArrayList<>();
        globalAgreementSectionAndClauseDTOListHashMap.forEach((agreementSection, clauseBasicDTOList) -> {
            List<ClauseBasicDTO> exisitingClauseList = new ArrayList<>();
            List<ClauseBasicDTO> newClauseRelatedToAgreementSection = new ArrayList<>();
            Map<BigInteger, Integer> clauseIdAndOrder = new HashMap<>();
            clauseBasicDTOList.forEach(clauseBasicDTO -> {
                if (clauseBasicDTO.isRequireUpdate() && Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                    alteredClauseIdList.add(clauseBasicDTO.getId());
                    exisitingClauseList.add(clauseBasicDTO);
                } else if (Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                    agreementSection.getClauseIdOrderedIndex().add(clauseBasicDTO.getId());
                    clauseIdAndOrder.put(clauseBasicDTO.getId(), clauseBasicDTO.getOrderedIndex());
                    clauseOrderCoresspondingToAgreementSectionAndSubSection.put(agreementSection, clauseIdAndOrder);
                } else {
                    newClauseRelatedToAgreementSection.add(clauseBasicDTO);
                }
            });
            if (!exisitingClauseList.isEmpty()) {
                exisitingClauseListCoresspondingToAgreementSections.put(agreementSection, exisitingClauseList);
            }
            List<Clause> clauseRelatedToAgreementSection = new ArrayList<>();
            if (!newClauseRelatedToAgreementSection.isEmpty()) {
                clauseRelatedToAgreementSection = buildClauseForAgreementSection(countryId, newClauseRelatedToAgreementSection, agreementTemplate);
                clauseList.addAll(clauseRelatedToAgreementSection);
            }
            clauseListCoresspondingToAgreementSection.put(agreementSection, clauseRelatedToAgreementSection);
        });
        if (!exisitingClauseListCoresspondingToAgreementSections.isEmpty()) {
            clauseList.addAll(updateExisingClauseListOfAgreementSection(countryId, alteredClauseIdList, exisitingClauseListCoresspondingToAgreementSections, clauseListCoresspondingToAgreementSection));
        }
        List<AgreementSection> agreementSubSections = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(clauseList)) {
            clauseMongoRepository.saveAll(getNextSequence(clauseList));
        }
        sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(agreementSubSectionListAndCoresspondingToAgreementSectionMap, agreementSubSections, clauseListCoresspondingToAgreementSection, clauseOrderCoresspondingToAgreementSectionAndSubSection);
        if (!agreementSubSections.isEmpty()) {
            agreementSectionMongoRepository.saveAll(getNextSequence(agreementSubSections));
        }
        agreementSubSections.clear();
        agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSectionList) -> {
            if (!agreementSection.isSubSection()) {
                agreementSection.getSubSections().addAll(subSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList()));
                agreementSubSections.add(agreementSection);
            }
        });
        return agreementSubSections;

    }


    /**
     * @param agreementSubSectionListAndCoresspondingToAgreementSectionMap
     * @param subSections
     * @param clauseListCoresspondingToAgreementSection
     * @param clauseOrderCoresspondingToAgreementSectionAndSubSection
     */
    private void sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap, List<AgreementSection> subSections,
                                                                                      Map<AgreementSection, List<Clause>> clauseListCoresspondingToAgreementSection, Map<AgreementSection, Map<BigInteger, Integer>> clauseOrderCoresspondingToAgreementSectionAndSubSection) {
        agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSectionList) ->
        {
            addClauseIdInIndexedOrderToSectionAndSubSection(agreementSection, clauseOrderCoresspondingToAgreementSectionAndSubSection, clauseListCoresspondingToAgreementSection);
            if (agreementSection.isSubSection()) {
                subSections.add(agreementSection);
            } else {
                if (Optional.ofNullable(subSectionList).isPresent() && CollectionUtils.isNotEmpty(subSectionList)) {
                    subSections.addAll(subSectionList);
                    subSectionList.forEach(agreementSubSection -> addClauseIdInIndexedOrderToSectionAndSubSection(agreementSubSection, clauseOrderCoresspondingToAgreementSectionAndSubSection, clauseListCoresspondingToAgreementSection));
                }
            }

        });

    }

    public void addClauseIdInIndexedOrderToSectionAndSubSection(AgreementSection agreementSection, Map<AgreementSection, Map<BigInteger, Integer>> clauseOrderCoresspondingToAgreementSectionAndSubSection
            , Map<AgreementSection, List<Clause>> clauseListCoresspondingToAgreementSection) {

        Map<BigInteger, Integer> agreementSubSectionClauseIdAndOrder = new HashMap<>();
        if (Optional.ofNullable(clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSection)).isPresent()) {
            agreementSubSectionClauseIdAndOrder = clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSection);
        }
        if (Optional.ofNullable(clauseListCoresspondingToAgreementSection.get(agreementSection)).isPresent() && CollectionUtils.isNotEmpty(clauseListCoresspondingToAgreementSection.get(agreementSection))) {
            for (Clause clause : clauseListCoresspondingToAgreementSection.get(agreementSection)) {
                agreementSubSectionClauseIdAndOrder.put(clause.getId(), clause.getOrderedIndex());
            }
        }
        agreementSection.setClauseIdOrderedIndex(agreementSubSectionClauseIdAndOrder.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    /**
     * @param countryId
     * @param clauseBasicDTOS
     * @param policyAgreementTemplate
     * @return
     */
    private List<Clause> buildClauseForAgreementSection(Long countryId, List<ClauseBasicDTO> clauseBasicDTOS, PolicyAgreementTemplate policyAgreementTemplate) {


        List<String> clauseTitles = new ArrayList<>();
        List<Clause> clauseList = new ArrayList<>();
        for (ClauseBasicDTO clauseBasicDTO : clauseBasicDTOS) {
            if (clauseTitles.contains(clauseBasicDTO.getTitle())) {
                exceptionService.duplicateDataException("message.duplicate", "Clause title ", clauseBasicDTO.getTitle());
            }
            clauseTitles.add(clauseBasicDTO.getTitle());
            Clause clause = new Clause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), countryId, policyAgreementTemplate.getOrganizationTypes(), policyAgreementTemplate.getOrganizationSubTypes()
                    , policyAgreementTemplate.getOrganizationServices(), policyAgreementTemplate.getOrganizationSubServices());
            List<BigInteger> templateTypes = new ArrayList<>();
            templateTypes.add(policyAgreementTemplate.getTemplateType());
            clause.setTemplateTypes(templateTypes);
            clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
            clause.setOrganizationId(UserContext.getOrgId());
            clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
            clauseList.add(clause);
        }
        List<Clause> existingClause = clauseMongoRepository.findClausesByTitle(countryId, clauseTitles);
        if (CollectionUtils.isNotEmpty(existingClause)) {
            exceptionService.duplicateDataException("message.duplicate", " Clause " + existingClause.get(0).getTitle());
        }
        return clauseList;
    }


    /**
     * @param countryId
     * @param existingClauseId
     * @param existingClauseMap
     * @param agreementSectionClauseList
     * @return
     *///todo refactoring code
    private List<Clause> updateExisingClauseListOfAgreementSection(Long countryId, List<BigInteger> existingClauseId, Map<AgreementSection, List<ClauseBasicDTO>> existingClauseMap, Map<AgreementSection, List<Clause>> agreementSectionClauseList) {

        List<Clause> exisitingClauseList = clauseMongoRepository.findClauseByCountryIdAndIdList(countryId, existingClauseId);
        Map<BigInteger, Clause> clauseIdMap = exisitingClauseList.stream().collect(Collectors.toMap(Clause::getId, clause -> clause));
        existingClauseMap.forEach((agreementSection, clauseBasicDTOS) -> {
            List<Clause> clausesRelateToAgreementSection = new ArrayList<>();
            clauseBasicDTOS.forEach(clauseBasicDTO -> {
                Clause clause = clauseIdMap.get(clauseBasicDTO.getId());
                clause.setDescription(clauseBasicDTO.getDescription());
                clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
                clausesRelateToAgreementSection.add(clause);
            });
            agreementSectionClauseList.get(agreementSection).addAll(clausesRelateToAgreementSection);
        });
        return exisitingClauseList;

    }


    /**
     * @param countryId
     * @param sectionId
     * @param clauseId
     * @return
     * @description remove clause id from Agreement section and Sub section if section contain clause and save section
     */
    public boolean removeClauseFromAgreementSection(Long countryId, BigInteger sectionId, BigInteger clauseId) {

        AgreementSection agreementSection = agreementSectionMongoRepository.findByIdAndCountryId(countryId, sectionId);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Agreement section " + sectionId);
        }
        List<BigInteger> clausesList = agreementSection.getClauseIdOrderedIndex();
        if (clausesList.contains(clauseId)) {
            clausesList.remove(clauseId);
            agreementSection.setClauseIdOrderedIndex(clausesList);
            agreementSectionMongoRepository.save(agreementSection);
        } else {
            exceptionService.invalidRequestException("message.invalidRequest", "Clause", clauseId);
        }
        return true;
    }


    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId, BigInteger id) {

        AgreementSectionResponseDTO exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(countryId, id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

    }

    private void checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(List<AgreementSectionDTO> agreementSectionDTOS) {
        Set<String> titles = new HashSet<>();
        Set<String> clauseTitles = new HashSet<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {
            if (titles.contains(agreementSectionDTO.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Agreement Section ", agreementSectionDTO.getTitle());
            }
            if (Optional.ofNullable(agreementSectionDTO.getClauses()).isPresent() && !agreementSectionDTO.getClauses().isEmpty()) {
                checkDuplicateClauseInAgreementSection(agreementSectionDTO.getClauses(), clauseTitles, agreementSectionDTO.getTitle());
            }
            if (Optional.ofNullable(agreementSectionDTO.getSubSections()).isPresent()) {
                agreementSectionDTO.getSubSections().forEach(agreementSubSectionDTO -> {
                    if (titles.contains(agreementSubSectionDTO.getTitle().toLowerCase())) {
                        exceptionService.duplicateDataException("message.duplicate", "Agreement Sub section", agreementSubSectionDTO.getTitle());
                    }
                    if (Optional.ofNullable(agreementSubSectionDTO.getClauses()).isPresent() && !agreementSubSectionDTO.getClauses().isEmpty()) {
                        checkDuplicateClauseInAgreementSection(agreementSubSectionDTO.getClauses(), clauseTitles, agreementSubSectionDTO.getTitle());
                    }
                    titles.add(agreementSubSectionDTO.getTitle().toLowerCase());
                });
            }
            titles.add(agreementSectionDTO.getTitle().toLowerCase());
        }
    }

    private void checkDuplicateClauseInAgreementSection(List<ClauseBasicDTO> clauseBasicDTOS, Set<String> clauseTitles, String sectionName) {
        for (ClauseBasicDTO clauseBasicDTO : clauseBasicDTOS) {

            if (clauseTitles.contains(clauseBasicDTO.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate.clause.agreement.section", clauseBasicDTO.getTitle(), sectionName);
            }
            clauseTitles.add(clauseBasicDTO.getTitle().toLowerCase());
        }

    }
}

