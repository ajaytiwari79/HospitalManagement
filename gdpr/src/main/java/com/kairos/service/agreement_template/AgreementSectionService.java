package com.kairos.service.agreement_template;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.agreement_template.AgreementSectionDTO;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateSectionDTO;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
import com.kairos.persistence.model.agreement_template.AgreementSectionDeprecated;
import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplateDeprecated;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.clause.ClauseDeprecated;
import com.kairos.persistence.model.clause.ClauseCkEditorVO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.embeddables.CoverPage;
import com.kairos.persistence.repository.agreement_template.AgreementSectionRepository;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementRepository;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.service.clause.ClauseService;
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
public class AgreementSectionService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionService.class);


    @Inject
    private AgreementSectionRepository agreementSectionRepository;

    @Inject
    private ClauseRepository clauseRepository;

    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ClauseService clauseService;

    @Inject
    private AWSBucketService awsBucketService;

    @Inject
    private PolicyAgreementRepository policyAgreementRepository;


    /**
     * @param referenceId
     * @param isUnitId
     * @param templateId
     * @param agreementTemplateSectionDTO
     * @return
     */
    public AgreementTemplateSectionResponseDTO createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long referenceId, boolean isUnitId, Long templateId, AgreementTemplateSectionDTO agreementTemplateSectionDTO) {
        AgreementTemplateSectionResponseDTO agreementTemplateSectionResponseDTO = new AgreementTemplateSectionResponseDTO();
        PolicyAgreementTemplate policyAgreementTemplate = isUnitId ? policyAgreementRepository.findByIdAndOrganizationIdAndDeletedFalse(templateId, referenceId) : policyAgreementRepository.findByIdAndCountryIdAndDeletedFalse(templateId,referenceId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.policy.agreementTemplate", templateId);
        }
        if (CollectionUtils.isNotEmpty(agreementTemplateSectionDTO.getSections())) {
            checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(agreementTemplateSectionDTO.getSections());
            List<AgreementSection> agreementSections = saveNewClausesAndMapToEmbeddedClausesOfSectionDTO(agreementTemplateSectionDTO.getSections());
            policyAgreementTemplate.setAgreementSections(agreementSections);
        }
        policyAgreementTemplate.setSignatureComponentAdded(agreementTemplateSectionDTO.isSignatureComponentAdded());
        policyAgreementTemplate.setSignatureComponentLeftAlign(agreementTemplateSectionDTO.isSignatureComponentLeftAlign());
        policyAgreementTemplate.setSignatureComponentRightAlign(agreementTemplateSectionDTO.isSignatureComponentRightAlign());
        policyAgreementTemplate.setSignatureHtml(agreementTemplateSectionDTO.getSignatureHtml());
        policyAgreementTemplate.setCoverPageAdded(agreementTemplateSectionDTO.isCoverPageAdded());
        policyAgreementTemplate.setCoverPageData(ObjectMapperUtils.copyPropertiesByMapper(agreementTemplateSectionDTO.getCoverPageData(), CoverPage.class));
        policyAgreementTemplate.setIncludeContentPage(agreementTemplateSectionDTO.isIncludeContentPage());

        policyAgreementRepository.save(policyAgreementTemplate);
        //return policyAgreementTemplateService.getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(referenceId, isUnitId, templateId);
        return agreementTemplateSectionResponseDTO;
    }

    private List<ClauseBasicDTO> findNewClauses(List<AgreementSectionDTO> sectionDTOList){
         List<ClauseBasicDTO> list= new ArrayList();
        sectionDTOList.forEach( section -> {
            List<ClauseBasicDTO> clauses   = section.getClauses().stream().filter( c -> c.getId() == null).collect(Collectors.toList());
            clauses.addAll(findNewClauses(section.getAgreementSubSections()));
            list.addAll(clauses);
        });
        return list;
    }

    private void mapClauseIdToEmbeddedClausesOfSectionDTO(List<AgreementSectionDTO> sectionDTOList, Map<UUID,Long > clauseData){
        sectionDTOList.forEach( section -> {
            section.getClauses().forEach( clause -> {
                clause.setId(clauseData.get(clause.getTempClauseId()));
            });
            mapClauseIdToEmbeddedClausesOfSectionDTO(section.getAgreementSubSections(),clauseData);
        });
    }


    private List<AgreementSection> saveNewClausesAndMapToEmbeddedClausesOfSectionDTO(List<AgreementSectionDTO> sectionDTOList){
        List<ClauseBasicDTO> newClauses = findNewClauses(sectionDTOList);
        List<Clause> clauses = ObjectMapperUtils.copyPropertiesOfListByMapper(newClauses, Clause.class);
        clauses = clauseRepository.saveAll(clauses);
        Map<UUID,Long > clauseData = new HashMap<>();
        clauses.forEach(clause -> {
            clauseData.put(clause.getTempClauseId(), clause.getId());
        });
        mapClauseIdToEmbeddedClausesOfSectionDTO(sectionDTOList, clauseData);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(sectionDTOList, AgreementSection.class);
    }

    /**
     * @param templateId - Policy Agreement template id
     * @param sectionId  -agreement section id
     * @return -true on successful deletion of section
     */
    public boolean deleteAgreementSection(Long referenceId, boolean isUnitId, Long templateId, Long sectionId) {

        AgreementSection agreementSection = isUnitId ? agreementSectionRepository.findByIdAndOrganizationIdAndDeleted(sectionId, referenceId) : agreementSectionRepository.findByIdAndCountryIdAndDeleted(sectionId, referenceId);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.agreement.section" + sectionId);
        }
        PolicyAgreementTemplate policyAgreementTemplate = isUnitId ? policyAgreementRepository.findByIdAndOrganizationIdAndDeletedFalse(templateId, referenceId) : policyAgreementRepository.findByIdAndCountryIdAndDeletedFalse(templateId, referenceId);
        policyAgreementTemplate.getAgreementSections().remove(agreementSection);
        policyAgreementRepository.save(policyAgreementTemplate);
        agreementSection.delete();
        agreementSectionRepository.save(agreementSection);
        return true;
    }


    /**
     * @param sectionId
     * @param subSectionId
     * @return
     */
    public boolean deleteAgreementSubSection(Long sectionId, Long subSectionId) {

        Integer updateCount = agreementSectionRepository.deleteAgreementSubSection(sectionId,subSectionId );
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.agreement.subSection" + sectionId);
        }else{
            LOGGER.info("Sub section with id :: {} is successfully deleted from section with id :: {}", subSectionId, sectionId);
        }

        return true;
    }

    /**
     * @param referenceId
     * @param agreementSectionDTOs
     * @param agreementSubSectionsCorrespondingToAgreementSection
     * @param globalAgreementSectionAndClauseDTOListHashMap
     */
    //todo refactored for country and unit both
    private void buildAgreementSectionAndSubSection(Long referenceId, boolean isUnitId, List<AgreementSectionDTO> agreementSectionDTOs, Map<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> agreementSubSectionsCorrespondingToAgreementSection,
                                                    Map<AgreementSectionDeprecated, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap) {

        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {
            AgreementSectionDeprecated agreementSection = new AgreementSectionDeprecated(agreementSectionDTO.getTitle(), agreementSectionDTO.getOrderedIndex(), false, agreementSectionDTO.getTitleHtml());
           //TODO
            // if (isUnitId) agreementSection.setOrganizationId(referenceId);
            //else agreementSection.setCountryIdList(referenceId);
            List<AgreementSectionDeprecated> subSectionList = new ArrayList<>();
            for (AgreementSectionDTO subSectionDTO : agreementSectionDTO.getAgreementSubSections()) {
                AgreementSectionDeprecated subSection = new AgreementSectionDeprecated(subSectionDTO.getTitle(), subSectionDTO.getOrderedIndex(), true, subSectionDTO.getTitleHtml());
                //TODO
                //if (isUnitId) subSection.setOrganizationId(referenceId);
                //else subSection.setCountryIdList(referenceId);
                if (CollectionUtils.isNotEmpty(subSectionDTO.getClauses())) {
                    globalAgreementSectionAndClauseDTOListHashMap.put(subSection, subSectionDTO.getClauses());
                }
                subSectionList.add(subSection);
            }
            if (!agreementSectionDTO.getClauses().isEmpty()) {
                globalAgreementSectionAndClauseDTOListHashMap.put(agreementSection, agreementSectionDTO.getClauses());
            }
            agreementSubSectionsCorrespondingToAgreementSection.put(agreementSection, subSectionList);
        }

    }

    /**
     * @param agreementSectionList
     * @param agreementSubSectionListAndCorrespondingToAgreementSectionMap
     */
    private void saveAgreementSectionAndSubSectionIfClauseNotExist(List<AgreementSectionDeprecated> agreementSectionList, Map<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> agreementSubSectionListAndCorrespondingToAgreementSectionMap) {
        List<AgreementSectionDeprecated> subSectionList = new ArrayList<>();
        agreementSubSectionListAndCorrespondingToAgreementSectionMap.forEach((agreementSection, subSections) -> subSectionList.addAll(subSections));
        if (CollectionUtils.isNotEmpty(subSectionList)) {
           // agreementSectionMongoRepository.saveAll(getNextSequence(subSectionList));
        }
        for (Map.Entry<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> entrySet : agreementSubSectionListAndCorrespondingToAgreementSectionMap.entrySet()) {
            AgreementSectionDeprecated agreementSection = entrySet.getKey();
            List<AgreementSectionDeprecated> agreementSubSections = entrySet.getValue();
            if (CollectionUtils.isNotEmpty(agreementSubSections)) {
                //entrySet.getKey().getSubSections().addAll(agreementSubSections.stream().map(AgreementSection::getId).collect(Collectors.toList()));
            }
            agreementSectionList.add(agreementSection);
        }

    }

    /**
     * @param referenceId             - unit id or country id
     * @param isUnitid                - to check whether  referenceId is unit id or country id
     * @param agreementSectionDTOS
     * @param policyAgreementTemplate
     * @description newAgreementSectionDTOList  - for first time call of save operation Section list equals to newAgreementSectionDTOList,
     * and if save call is second time and section contain id then sections with no id equals to newAgreementSectionDTOList.
     * method create section ,Sub Section clause and update clauses,section ,sub section.
     */
    public List<BigInteger> createOrUpdateSectionAndSubSectionOfAgreementTemplate(Long referenceId, boolean isUnitid, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap = new HashMap<>();
        List<AgreementSectionDTO> newAgreementSectionDTOList = new ArrayList<>();
        List<Long> agreementSectionIdList = new ArrayList<>();
        Map<Long, List<AgreementSection>> newSubSectionListCorrespondingToSection = new HashMap<>();
        Map<Long, AgreementSectionDTO> agreementSectionDTOMap = new HashMap<>();
        agreementSectionDTOS.forEach(agreementSectionDTO -> {
            if (!Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                newAgreementSectionDTOList.add(agreementSectionDTO);
            } else {
                List<AgreementSectionDeprecated> subSectionListCorrespondingToAgreementSection = new ArrayList<>();
                agreementSectionIdList.add(agreementSectionDTO.getId());
                agreementSectionDTOMap.put(agreementSectionDTO.getId(), agreementSectionDTO);
                if (CollectionUtils.isNotEmpty(agreementSectionDTO.getAgreementSubSections())) {
                    List<Long> subSectionIdList = new ArrayList<>();
                    for (AgreementSectionDTO agreementSubSectionDTO : agreementSectionDTO.getAgreementSubSections()) {
                        if (Optional.ofNullable(agreementSubSectionDTO.getId()).isPresent()) {
                            subSectionIdList.add(agreementSubSectionDTO.getId());
                            agreementSectionDTOMap.put(agreementSubSectionDTO.getId(), agreementSubSectionDTO);
                        } else {
                            AgreementSectionDeprecated subSection = new AgreementSectionDeprecated(agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex(), true, agreementSubSectionDTO.getTitleHtml());
                            if (isUnitid) {
                                // subSection.setOrganizationId(referenceId);
                            }
                            else
                                subSection.setCountryId(referenceId);
                            if (CollectionUtils.isNotEmpty(agreementSubSectionDTO.getClauses())) {
                                //globalAgreementSectionAndClauseDTOListHashMap.put(subSection, agreementSubSectionDTO.getClauses());
                            }
                            subSectionListCorrespondingToAgreementSection.add(subSection);
                        }
                    }
                    agreementSectionIdList.addAll(subSectionIdList);
                }
                //newSubSectionListCorrespondingToSection.put(agreementSectionDTO.getId(), subSectionListCorrespondingToAgreementSection);
            }
        });
        Map<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> agreementSubSectionListCorrespondingToAgreementSection = new HashMap<>();
        if (CollectionUtils.isNotEmpty(newAgreementSectionDTOList)) {
            //buildAgreementSectionAndSubSection(referenceId, isUnitid, newAgreementSectionDTOList, agreementSubSectionListCorrespondingToAgreementSection, globalAgreementSectionAndClauseDTOListHashMap);
        }
        List<AgreementSectionDeprecated> agreementSectionList = new ArrayList<>();
       /* List<AgreementSection> agreementSections = isUnitid ? agreementSectionMongoRepository.findAllByUnitIdAndIds(referenceId, agreementSectionIdList) : agreementSectionMongoRepository.findAllByCountryIdAndIds(referenceId, agreementSectionIdList);
        updateExistingSectionAndSubSection(agreementSections, agreementSectionDTOMap, newSubSectionListCorrespondingToSection, globalAgreementSectionAndClauseDTOListHashMap
                , agreementSubSectionListCorrespondingToAgreementSection);

        if (!globalAgreementSectionAndClauseDTOListHashMap.isEmpty()) {
            agreementSectionList = saveAndUpdateClauseOfAgreementSection(referenceId, isUnitid, globalAgreementSectionAndClauseDTOListHashMap, agreementSubSectionListCorrespondingToAgreementSection, policyAgreementTemplate);
        } else {
            saveAgreementSectionAndSubSectionIfClauseNotExist(agreementSectionList, agreementSubSectionListCorrespondingToAgreementSection);
        }
*/
        if (CollectionUtils.isNotEmpty(agreementSectionList)) {
            //agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        }
       // return agreementSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList());
        return new ArrayList<>();
    }


    /**
     * @param agreementSections
     * @param agreementSectionDTOMap
     * @param newSubSectionListCorrespondingToSection                - sub section list Corresponding to existing agreement section id
     * @param globalAgreementSectionAndClauseDTOListHashMap          - Clause DTO list Corresponding to sections and sub sections
     * @param agreementSubSectionListCorrespondingToAgreementSection list of agreement sub section Corresponding to section
     */
    private void updateExistingSectionAndSubSection(List<AgreementSectionDeprecated> agreementSections, Map<BigInteger, AgreementSectionDTO> agreementSectionDTOMap,
                                                    Map<BigInteger, List<AgreementSectionDeprecated>> newSubSectionListCorrespondingToSection,
                                                    Map<AgreementSectionDeprecated, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap,
                                                    Map<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> agreementSubSectionListCorrespondingToAgreementSection) {
        //TODO
        /*for (AgreementSection agreementSection : agreementSections) {
            AgreementSectionDTO agreementSectionDTO = agreementSectionDTOMap.get(agreementSection.getId());
            agreementSection.setTitle(agreementSectionDTO.getTitle());
            agreementSection.setOrderedIndex(agreementSectionDTO.getOrderedIndex());
            agreementSection.setTitleHtml(agreementSectionDTO.getTitleHtml());
            if (!agreementSectionDTO.getClauses().isEmpty()) {
                globalAgreementSectionAndClauseDTOListHashMap.put(agreementSection, agreementSectionDTO.getClauses());
            }
            if (agreementSection.isSubSection()) {
                agreementSubSectionListCorrespondingToAgreementSection.put(agreementSection, new ArrayList<>());

            } else {
                agreementSubSectionListCorrespondingToAgreementSection.put(agreementSection, newSubSectionListCorrespondingToSection.get(agreementSection.getId()));
            }
        }*/

    }

    /**
     * @param referenceId
     * @param globalAgreementSectionAndClauseDTOListHashMap
     * @param agreementTemplate
     */
    //todo refactored for unit and country both
    private List<AgreementSectionDeprecated> saveAndUpdateClauseOfAgreementSection(Long referenceId, boolean isUnitId, Map<AgreementSectionDeprecated, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap,
                                                                                   Map<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> agreementSectionAndSubSectionListMap, PolicyAgreementTemplateDeprecated agreementTemplate) {

        List<ClauseDeprecated> clauseList = new ArrayList<>();
        Map<AgreementSectionDeprecated, List<ClauseDeprecated>> clauseListCorrespondingToAgreementSection = new HashMap<>();
        Map<AgreementSectionDeprecated, List<ClauseBasicDTO>> existingClauseListCorrespondingToAgreementSections = new HashMap<>();
        Set<BigInteger> alteredClauseIdList = new HashSet<>();
       // ClauseTag defaultTag = clauseTagMongoRepository.findDefaultTag();
        //agreementTemplate.setDefaultClauseTag(defaultTag);
        globalAgreementSectionAndClauseDTOListHashMap.forEach((agreementSection, clauseBasicDTOList) -> {
            List<ClauseBasicDTO> existingClauseList = new ArrayList<>();
            List<ClauseBasicDTO> newClauseRelatedToAgreementSection = new ArrayList<>();
            clauseBasicDTOList.forEach(clauseBasicDTO -> {
                if (Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                    //alteredClauseIdList.add(clauseBasicDTO.getId());
                    existingClauseList.add(clauseBasicDTO);
                } else {
                    newClauseRelatedToAgreementSection.add(clauseBasicDTO);
                }
            });
            if (!existingClauseList.isEmpty()) {
                existingClauseListCorrespondingToAgreementSections.put(agreementSection, existingClauseList);
            }
            List<ClauseDeprecated> clauseRelatedToAgreementSection = new ArrayList<>();
            if (!newClauseRelatedToAgreementSection.isEmpty()) {
                clauseRelatedToAgreementSection = buildClauseForAgreementSection(referenceId, isUnitId, newClauseRelatedToAgreementSection, agreementTemplate);
                clauseList.addAll(clauseRelatedToAgreementSection);
            }
            clauseListCorrespondingToAgreementSection.put(agreementSection, clauseRelatedToAgreementSection);
        });
        if (!existingClauseListCorrespondingToAgreementSections.isEmpty()) {
            clauseList.addAll(updateClauseListOfAgreementSection(referenceId, isUnitId, alteredClauseIdList, existingClauseListCorrespondingToAgreementSections, clauseListCorrespondingToAgreementSection, agreementTemplate));
        }
        List<AgreementSectionDeprecated> agreementSubSections = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(clauseList)) {
           // clauseMongoRepository.saveAll(getNextSequence(clauseList));
        }
        sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(agreementSectionAndSubSectionListMap, agreementSubSections, clauseListCorrespondingToAgreementSection);
        if (!agreementSubSections.isEmpty()) {
            //agreementSectionMongoRepository.saveAll(getNextSequence(agreementSubSections));
        }
        agreementSubSections.clear();
        agreementSectionAndSubSectionListMap.forEach((agreementSection, subSectionList) -> {
            if (!agreementSection.isSubSection()) {
                //agreementSection.getSubSections().addAll(subSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList()));
                agreementSubSections.add(agreementSection);
            }
        });
        return agreementSubSections;

    }


    /**
     * @param agreementSubSectionListAndCorrespondingToAgreementSectionMap
     * @param subSections
     * @param clauseListCorrespondingToAgreementSection
     * @param
     */
    private void sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(Map<AgreementSectionDeprecated, List<AgreementSectionDeprecated>> agreementSubSectionListAndCorrespondingToAgreementSectionMap, List<AgreementSectionDeprecated> subSections,
                                                                                      Map<AgreementSectionDeprecated, List<ClauseDeprecated>> clauseListCorrespondingToAgreementSection) {
        agreementSubSectionListAndCorrespondingToAgreementSectionMap.forEach((agreementSection, subSectionList) ->
        {
            mapClauseCkEditorHtmlAndSortClauseArray(agreementSection, clauseListCorrespondingToAgreementSection.get(agreementSection));
            if (agreementSection.isSubSection()) {
                subSections.add(agreementSection);
            } else {
                if (Optional.ofNullable(subSectionList).isPresent() && CollectionUtils.isNotEmpty(subSectionList)) {
                    subSections.addAll(subSectionList);
                    subSectionList.forEach(agreementSubSection ->
                            mapClauseCkEditorHtmlAndSortClauseArray(agreementSubSection, clauseListCorrespondingToAgreementSection.get(agreementSubSection)));

                }
            }
        });

    }


    private void mapClauseCkEditorHtmlAndSortClauseArray(AgreementSectionDeprecated agreementSection, List<ClauseDeprecated> clauseList) {
        if (CollectionUtils.isNotEmpty(clauseList)) {
            Set<ClauseCkEditorVO> clauseCkEditorVOS = new HashSet<>();
            List<ClauseDeprecated> clauses = clauseList.stream().sorted(Comparator.comparing(ClauseDeprecated::getOrderedIndex)).collect(Collectors.toList());
//            clauses.forEach(clause -> clauseCkEditorVOS.add(new ClauseCkEditorVO(clause.getId(), clause.getTitleHtml(), clause.getDescriptionHtml())));
            //agreementSection.setClauseIdOrderedIndex(clauses.stream().map(Clause::getId).collect(Collectors.toList()));
            agreementSection.setClauseCkEditorVOS(clauseCkEditorVOS);
        }

    }

    /**
     * @param referenceId
     * @param clauseBasicDTOS
     * @param policyAgreementTemplate
     * @return
     */
    private List<ClauseDeprecated> buildClauseForAgreementSection(Long referenceId, boolean isUnitId, List<ClauseBasicDTO> clauseBasicDTOS, PolicyAgreementTemplateDeprecated policyAgreementTemplate) {


        List<String> clauseTitles = new ArrayList<>();
        List<ClauseDeprecated> clauseList = new ArrayList<>();
        for (ClauseBasicDTO clauseBasicDTO : clauseBasicDTOS) {
            clauseTitles.add(clauseBasicDTO.getTitle());
            ClauseDeprecated clause = new ClauseDeprecated(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription());
            if (isUnitId) {
                //clause.setOrganizationId(referenceId);
            } else {

                clause.setOrganizationTypes(policyAgreementTemplate.getOrganizationTypeDTOS());
                clause.setOrganizationSubTypeDTOS(policyAgreementTemplate.getOrganizationSubTypeDTOS());
                clause.setOrganizationServices(policyAgreementTemplate.getOrganizationServices());
                clause.setOrganizationSubServices(policyAgreementTemplate.getOrganizationSubServices());
                clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
                clause.setTemplateTypes(Collections.singletonList(policyAgreementTemplate.getTemplateTypeId()));
                clause.setCountryId(referenceId);

            }
            clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
            clause.setTags(Collections.singletonList(policyAgreementTemplate.getDefaultClauseTag()));
            clause.setTitleHtml(clauseBasicDTO.getTitleHtml());
            clause.setDescriptionHtml(clauseBasicDTO.getDescriptionHtml());
            clauseList.add(clause);
        }
       /* List<Clause> existingClause = clauseMongoRepository.findClauseByReferenceIdAndTitles(referenceId, isUnitId, clauseTitles);
        if (CollectionUtils.isNotEmpty(existingClause)) {
            exceptionService.duplicateDataException("message.duplicate", "message.clause" + existingClause.get(0).getTitle());
        }*/
        return clauseList;
    }


    /**
     * @param referenceId
     * @param clauseIds
     * @param existingClauseMap
     * @param agreementSectionClauseList
     * @return
     */
    private List<ClauseDeprecated> updateClauseListOfAgreementSection(Long referenceId, boolean isUnitId, Set<BigInteger> clauseIds,
                                                                      Map<AgreementSectionDeprecated, List<ClauseBasicDTO>> existingClauseMap,
                                                                      Map<AgreementSectionDeprecated, List<ClauseDeprecated>> agreementSectionClauseList,
                                                                      PolicyAgreementTemplateDeprecated agreementTemplate) {

            //TODO
        /*Set<BigInteger> clauseIdListPresentInOtherSectionAndSubSection = policyAgreementTemplateRepository.getClauseIdListPresentInOtherTemplateByReferenceIdAndTemplateIdAndClauseIds(referenceId, isUnitId, agreementTemplate.getId(), clauseIds);
        List<Clause> clauseList = isUnitId ? clauseMongoRepository.findAllByUnitIdAndIdList(referenceId, clauseIds) : clauseMongoRepository.findAllByCountryIdAndIdList(referenceId, clauseIds);
        Map<BigInteger, Clause> clauseIdMap = clauseList.stream().collect(Collectors.toMap(Clause::getId, clause -> clause));
        existingClauseMap.forEach((agreementSection, clauseBasicDTOS) -> {
            List<Clause> clausesRelateToAgreementSection = new ArrayList<>();
            clauseBasicDTOS.forEach(clauseBasicDTO -> {
                Clause clause;
                if (clauseIdListPresentInOtherSectionAndSubSection.contains(clauseBasicDTO.getId()) && clauseBasicDTO.isRequireUpdate()) {
                    clause = createVersionOfClause(referenceId, isUnitId, clauseBasicDTO, agreementTemplate);
                    clauseList.add(clause);

                } else {
                    clause = clauseIdMap.get(clauseBasicDTO.getId());
                    clause.setTitle(clauseBasicDTO.getTitle());
                    clause.setDescription(clauseBasicDTO.getDescription());
                    clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
                }
                clause.setTitleHtml(clauseBasicDTO.getTitleHtml());
                clause.setDescriptionHtml(clauseBasicDTO.getDescriptionHtml());
                clausesRelateToAgreementSection.add(clause);
            });
            agreementSectionClauseList.get(agreementSection).addAll(clausesRelateToAgreementSection);
        });*/
        return new ArrayList<>();

    }


    private ClauseDeprecated createVersionOfClause(Long referenceId, boolean isUnitd, ClauseBasicDTO clauseBasicDTO, PolicyAgreementTemplateDeprecated policyAgreementTemplate) {

        ClauseDeprecated clause = new ClauseDeprecated(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription());
        if (isUnitd) {
           // clause.setOrganizationId(referenceId);
        } else {
            clause.setOrganizationTypes(policyAgreementTemplate.getOrganizationTypeDTOS());
            clause.setOrganizationSubTypeDTOS(policyAgreementTemplate.getOrganizationSubTypeDTOS());
            clause.setOrganizationServices(policyAgreementTemplate.getOrganizationServices());
            clause.setOrganizationSubServices(policyAgreementTemplate.getOrganizationSubServices());
            clause.setCountryId(referenceId);
        }
        clause.setTags(Collections.singletonList(policyAgreementTemplate.getDefaultClauseTag()));
        clause.setTemplateTypes(Collections.singletonList(policyAgreementTemplate.getTemplateTypeId()));
        clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
        clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
        //clause.setParentClauseId(clauseBasicDTO.getId());
        return clause;
    }


    /**
     * @param sectionId
     * @param clauseId
     * @return
     * @description remove clause id from Agreement section and Sub section if section contain clause and save section
     */
    public boolean removeClauseIdFromAgreementSection(Long sectionId, Long clauseId) {

        Integer updateCount = agreementSectionRepository.removeClauseIdFromAgreementSection(sectionId, clauseId);
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.agreement.section" + sectionId);
        }else{
            LOGGER.info("Clause with id :: {} removed from section with id :: {}", clauseId, sectionId);
        }

        return true;
    }


    private void checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(List<AgreementSectionDTO> agreementSectionDTOS) {
        Set<String> titles = new HashSet<>();
        Set<String> clauseTitles = new HashSet<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {
            if (titles.contains(agreementSectionDTO.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "message.agreement.section", agreementSectionDTO.getTitle());
            } else if (CollectionUtils.isNotEmpty(agreementSectionDTO.getClauses())) {
                checkDuplicateClauseInAgreementSection(agreementSectionDTO.getClauses(), clauseTitles, agreementSectionDTO.getTitle());
            } else if (Optional.ofNullable(agreementSectionDTO.getAgreementSubSections()).isPresent()) {
                checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(agreementSectionDTO.getAgreementSubSections());
                /*agreementSectionDTO.getAgreementSubSections().forEach(agreementSubSectionDTO -> {
                    if (titles.contains(agreementSubSectionDTO.getTitle().toLowerCase())) {
                        exceptionService.duplicateDataException("message.duplicate", "message.agreement.subSection", agreementSubSectionDTO.getTitle());
                    }
                    if (CollectionUtils.isNotEmpty(agreementSectionDTO.getClauses())) {
                        checkDuplicateClauseInAgreementSection(agreementSubSectionDTO.getClauses(), clauseTitles, agreementSubSectionDTO.getTitle());
                    }
                    titles.add(agreementSubSectionDTO.getTitle().toLowerCase());
                });*/
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

