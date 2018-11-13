package com.kairos.service.agreement_template;


import com.kairos.dto.gdpr.agreement_template.AgreementSectionDTO;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateSectionDTO;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.repository.agreement_template.AgreementSectionMongoRepository;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.persistence.repository.clause.ClauseMongoRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagMongoRepository;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.service.clause.ClauseService;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.s3bucket.AWSBucketService;
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

    @Inject
    private ClauseTagMongoRepository clauseTagMongoRepository;

    @Inject
    private AWSBucketService awsBucketService;


    public AgreementTemplateSectionResponseDTO createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long countryId, BigInteger templateId, AgreementTemplateSectionDTO agreementTemplateSectionDTO) {

        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndCountryId(countryId, templateId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", templateId);
        }
        if (CollectionUtils.isNotEmpty(agreementTemplateSectionDTO.getSections())) {
            checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(agreementTemplateSectionDTO.getSections());
            List<BigInteger> agreementSectionIdList = createOrupdateSectionAndSubSectionOfAgreementTemplate(countryId, agreementTemplateSectionDTO.getSections(), policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        }
        policyAgreementTemplate.setCoverPageAdded(agreementTemplateSectionDTO.isCoverPageAdded());
        policyAgreementTemplate.setCoverPageData(agreementTemplateSectionDTO.getCoverPageData());
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
        agreementSectionMongoRepository.safeDeleteById(subSectionId);
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
            agreementSection.setTitleHtml(agreementSectionDTO.getTitleHtml());
            List<AgreementSection> subSectionList = new ArrayList<>();
            for (AgreementSectionDTO agreementSubSectionDTO : agreementSectionDTO.getSubSections()) {
                AgreementSection subSection = new AgreementSection(countryId, agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex(), true);
                subSection.setTitleHtml(agreementSubSectionDTO.getTitleHtml());
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
     * @param countryId
     * @param agreementSectionDTOS
     * @param policyAgreementTemplate
     * @description newAgreementSectionDTOList  - for first time call of save operation Section list equals to newAgreementSectionDTOList,
     * and if save call is second time and section contain id then sections with no id equals to newAgreementSectionDTOList.
     * method create section ,Sub Section clause and update clauses,section ,sub section.
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
                            subSection.setTitleHtml(agreementSubSectionDTO.getTitleHtml());
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

        if (CollectionUtils.isNotEmpty(agreementSectionList)) {
            agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        }
        return agreementSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList());
    }


    /**
     * @param agreementSections
     * @param agreementSectionDTOMap
     * @param newSubSectionListCoresspondingToSection                - sub section list coressponding to existing agreement section id
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
            agreementSection.setTitleHtml(agreementSectionDTO.getTitleHtml());
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
        Set<BigInteger> alteredClauseIdList = new HashSet<>();
        globalAgreementSectionAndClauseDTOListHashMap.forEach((agreementSection, clauseBasicDTOList) -> {
            List<ClauseBasicDTO> exisitingClauseList = new ArrayList<>();
            List<ClauseBasicDTO> newClauseRelatedToAgreementSection = new ArrayList<>();
            clauseBasicDTOList.forEach(clauseBasicDTO -> {
                if (Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                    alteredClauseIdList.add(clauseBasicDTO.getId());
                    exisitingClauseList.add(clauseBasicDTO);
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
            clauseList.addAll(updateExisingClauseListOfAgreementSection(countryId, alteredClauseIdList, exisitingClauseListCoresspondingToAgreementSections, clauseListCoresspondingToAgreementSection, agreementTemplate));
        }
        List<AgreementSection> agreementSubSections = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(clauseList)) {
            clauseMongoRepository.saveAll(getNextSequence(clauseList));
        }
        sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(agreementSubSectionListAndCoresspondingToAgreementSectionMap, agreementSubSections, clauseListCoresspondingToAgreementSection);
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
     * @param
     */
    private void sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap, List<AgreementSection> subSections,
                                                                                      Map<AgreementSection, List<Clause>> clauseListCoresspondingToAgreementSection) {
        agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSectionList) ->
        {
            mapClauseCkEditorHtmlAndSortClauseArray(agreementSection, clauseListCoresspondingToAgreementSection.get(agreementSection));
            if (agreementSection.isSubSection()) {
                subSections.add(agreementSection);
            } else {
                if (Optional.ofNullable(subSectionList).isPresent() && CollectionUtils.isNotEmpty(subSectionList)) {
                    subSections.addAll(subSectionList);
                    subSectionList.forEach(agreementSubSection ->
                            mapClauseCkEditorHtmlAndSortClauseArray(agreementSubSection, clauseListCoresspondingToAgreementSection.get(agreementSubSection)));

                }
            }
        });

    }


    private void mapClauseCkEditorHtmlAndSortClauseArray(AgreementSection agreementSection, List<Clause> clauseList) {
        if (CollectionUtils.isNotEmpty(clauseList)) {
            List<ClauseCkEditorVO> clauseCkEditorVOS = new ArrayList<>();
            List<Clause> clauses = clauseList.stream().sorted(Comparator.comparing(Clause::getOrderedIndex)).collect(Collectors.toList());
            clauses.forEach(clause -> clauseCkEditorVOS.add(new ClauseCkEditorVO(clause.getId(), clause.getTitleHtml(), clause.getDescriptionHtml())));
            agreementSection.setClauseIdOrderedIndex(clauses.stream().map(Clause::getId).collect(Collectors.toList()));
            agreementSection.setClauseCkEditorVOS(clauseCkEditorVOS);
        }

    }

    /**
     * @param countryId
     * @param clauseBasicDTOS
     * @param policyAgreementTemplate
     * @return
     */
    private List<Clause> buildClauseForAgreementSection(Long countryId, List<ClauseBasicDTO> clauseBasicDTOS, PolicyAgreementTemplate policyAgreementTemplate) {


        ClauseTag defaultTag = clauseTagMongoRepository.findDefaultTag(countryId);
        List<String> clauseTitles = new ArrayList<>();
        List<Clause> clauseList = new ArrayList<>();
        for (ClauseBasicDTO clauseBasicDTO : clauseBasicDTOS) {
            if (clauseTitles.contains(clauseBasicDTO.getTitle())) {
                exceptionService.duplicateDataException("message.duplicate", "Clause title ", clauseBasicDTO.getTitle());
            }
            clauseTitles.add(clauseBasicDTO.getTitle());
            Clause clause = new Clause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), countryId, policyAgreementTemplate.getOrganizationTypes(), policyAgreementTemplate.getOrganizationSubTypes()
                    , policyAgreementTemplate.getOrganizationServices(), policyAgreementTemplate.getOrganizationSubServices());
            clause.setTemplateTypes(Collections.singletonList(policyAgreementTemplate.getTemplateType()));
            clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
            clause.setTags(Collections.singletonList(defaultTag));
            clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
            clause.setTitleHtml(clauseBasicDTO.getTitleHtml());
            clause.setDescriptionHtml(clauseBasicDTO.getDescriptionHtml());
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
     *///todo add versioning part here
    private List<Clause> updateExisingClauseListOfAgreementSection(Long countryId, Set<BigInteger> existingClauseId, Map<AgreementSection, List<ClauseBasicDTO>> existingClauseMap, Map<AgreementSection, List<Clause>> agreementSectionClauseList, PolicyAgreementTemplate agreementTemplate) {


      /*  Set<BigInteger> clauseIdListPresentInOtherSectionAndSubSection = agreementSectionMongoRepository.getClauseIdListPresentInAgreementSectionAndSubSectionsByCountryIdAndClauseIds(countryId, existingClauseId);

        if (CollectionUtils.isNotEmpty(clauseIdListPresentInOtherSectionAndSubSection)) {
            existingClauseId.remove(clauseIdListPresentInOtherSectionAndSubSection);
        }*/
        List<Clause> exisitingClauseList = clauseMongoRepository.findClauseByCountryIdAndIdList(countryId, existingClauseId);
        Map<BigInteger, Clause> clauseIdMap = exisitingClauseList.stream().collect(Collectors.toMap(Clause::getId, clause -> clause));
        existingClauseMap.forEach((agreementSection, clauseBasicDTOS) -> {
            List<Clause> clausesRelateToAgreementSection = new ArrayList<>();
            clauseBasicDTOS.forEach(clauseBasicDTO -> {
                Clause clause;
               /* if (clauseIdListPresentInOtherSectionAndSubSection.contains(clauseBasicDTO.getId())) {
                    clause = createVersionOfClause(countryId, clauseBasicDTO, agreementTemplate);
                    exisitingClauseList.add(clause);

                } else {*/
                    clause = clauseIdMap.get(clauseBasicDTO.getId());
                    clause.setTitle(clauseBasicDTO.getTitle());
                    clause.setDescription(clauseBasicDTO.getDescription());
                    clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
                /*}*/
                clause.setTitleHtml(clauseBasicDTO.getTitleHtml());
                clause.setDescriptionHtml(clauseBasicDTO.getDescriptionHtml());
                clausesRelateToAgreementSection.add(clause);
            });
            agreementSectionClauseList.get(agreementSection).addAll(clausesRelateToAgreementSection);
        });
        return exisitingClauseList;

    }


    private Clause createVersionOfClause(Long countryId, ClauseBasicDTO clauseBasicDTO, PolicyAgreementTemplate policyAgreementTemplate) {
        Clause clause = new Clause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), countryId, policyAgreementTemplate.getOrganizationTypes(), policyAgreementTemplate.getOrganizationSubTypes()
                , policyAgreementTemplate.getOrganizationServices(), policyAgreementTemplate.getOrganizationSubServices());
        clause.setTemplateTypes(Collections.singletonList(policyAgreementTemplate.getTemplateType()));
        clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
        clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
        clause.setParentClauseId(clauseBasicDTO.getId());
        return clause;
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
        agreementSection.getClauseIdOrderedIndex().remove(clauseId);
        agreementSectionMongoRepository.save(agreementSection);
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
            } else if (CollectionUtils.isNotEmpty(agreementSectionDTO.getClauses())) {
                checkDuplicateClauseInAgreementSection(agreementSectionDTO.getClauses(), clauseTitles, agreementSectionDTO.getTitle());
            } else if (Optional.ofNullable(agreementSectionDTO.getSubSections()).isPresent()) {
                agreementSectionDTO.getSubSections().forEach(agreementSubSectionDTO -> {
                    if (titles.contains(agreementSubSectionDTO.getTitle().toLowerCase())) {
                        exceptionService.duplicateDataException("message.duplicate", "Agreement Sub section", agreementSubSectionDTO.getTitle());
                    }
                    if (CollectionUtils.isNotEmpty(agreementSectionDTO.getClauses())) {
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

