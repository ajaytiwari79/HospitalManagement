package com.kairos.service.agreement_template;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.dto.gdpr.master_data.AgreementSectionDTO;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
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
import com.kairos.utils.user_context.UserContext;
import jdk.nashorn.internal.runtime.options.Option;
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
    private ExceptionService exceptionService;

    @Inject
    private ClauseService clauseService;


    public List<AgreementSectionResponseDTO> createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long countryId, Long organizationId, BigInteger templateId, List<AgreementSectionDTO> agreementSectionDTOs) {

        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, organizationId, templateId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", templateId);
        }
        boolean flag = false;
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {

            if (Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                flag = true;
                break;
            }
        }
        List<BigInteger> agreementSectionIdList = flag ? updateSectionAndSubSectionOfAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate)
                : createSectionClauseAndSubSectionsOfAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
        policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        return policyAgreementTemplateRepository.getAgreementTemplateWithSectionsAndSubSections(countryId, organizationId, templateId);
    }


    /**
     * @param countryId
     * @param orgId
     * @param templateId - Policy Agreement template id
     * @param id         -agreement section id
     * @return -true on successful deletion of section
     */
    public boolean deleteAgreementSection(Long countryId, Long orgId, BigInteger templateId, BigInteger id) {

        AgreementSection agreementSection = agreementSectionMongoRepository.findByIdAndNonDeleted(countryId, orgId, id);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Agreement section " + id);
        }
        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, orgId, templateId);
        List<BigInteger> agreementSectionIdList = policyAgreementTemplate.getAgreementSections();
        agreementSectionIdList.remove(id);
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        agreementSectionMongoRepository.delete(agreementSection);
        return true;
    }


    /**
     * @param countryId
     * @param orgId
     * @param sectionId
     * @param subSectionId
     * @return
     */
    public boolean deleteAgreementSubSection(Long countryId, Long orgId, BigInteger sectionId, BigInteger subSectionId) {

        AgreementSection agreementSection = agreementSectionMongoRepository.findByIdAndNonDeleted(countryId, orgId, sectionId);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Agreement section " + sectionId);
        }
        agreementSection.getSubSections().remove(subSectionId);
        agreementSectionMongoRepository.findByIdAndSafeDelete(subSectionId);
        agreementSectionMongoRepository.save(agreementSection);
        return true;
    }


    /**
     * @param countryId
     * @param unitId
     * @param agreementSectionDTOs
     *///todo refactoring code
    private List<BigInteger> createSectionClauseAndSubSectionsOfAgreementTemplate(Long countryId, Long unitId, List<AgreementSectionDTO> agreementSectionDTOs, PolicyAgreementTemplate policyAgreementTemplate) {

        Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap = new HashMap<>();
        Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap = new HashMap<>();
        buildAgreementSectionAndSubSection(countryId, unitId, agreementSectionDTOs, agreementSubSectionListAndCoresspondingToAgreementSectionMap, globalAgreementSectionAndClauseDTOListHashMap);
        List<AgreementSection> agreementSectionList = new ArrayList<>();
        if (!globalAgreementSectionAndClauseDTOListHashMap.isEmpty()) {
            agreementSectionList = saveAndUpdateClauseOfAgreementSection(countryId, unitId, globalAgreementSectionAndClauseDTOListHashMap, agreementSubSectionListAndCoresspondingToAgreementSectionMap, policyAgreementTemplate);
        } else {
            saveAgreementSectionAndSubSectionIfClauseNotExist(agreementSectionList, agreementSubSectionListAndCoresspondingToAgreementSectionMap);
        }
        agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        return agreementSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList());
    }



    /**
     * @param countryId
     * @param unitId
     * @param agreementSectionDTOs
     * @param agreementSubSectionsCoresspondingToAgreementSection
     * @param globalAgreementSectionAndClauseDTOListHashMap
     */
    private void buildAgreementSectionAndSubSection(Long countryId, Long unitId, List<AgreementSectionDTO> agreementSectionDTOs, Map<AgreementSection, List<AgreementSection>> agreementSubSectionsCoresspondingToAgreementSection,
                                                    Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap) {

        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {
            AgreementSection agreementSection = new AgreementSection(countryId, agreementSectionDTO.getTitle(), agreementSectionDTO.getOrderedIndex());
            agreementSection.setOrganizationId(unitId);
            List<AgreementSection> subSectionList = new ArrayList<>();
            for (AgreementSectionDTO agreementSubSectionDTO : agreementSectionDTO.getSubSections()) {
                AgreementSection subSection = new AgreementSection(countryId, agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex());
                subSection.setOrganizationId(unitId);
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
     *
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
                entrySet.getKey().setSubSections(agreementSubSections.stream().map(AgreementSection::getId).collect(Collectors.toList()));
            }
            agreementSectionList.add(agreementSection);
        }

    }

    /**
     * @param countryId
     * @param unitId
     * @param agreementSectionDTOS
     * @param policyAgreementTemplate
     *///todo update section and subsection functions
    public List<BigInteger> updateSectionAndSubSectionOfAgreementTemplate(Long countryId, Long unitId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap = new HashMap<>();
        List<AgreementSectionDTO> newAgreementSectionDTOList = new ArrayList<>();
        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        List<BigInteger> agreementSubSectionIdList = new ArrayList<>();
        Map<BigInteger, List<ClauseBasicDTO>> clauseBasicDTOCoresspondingToSections = new HashMap<>();
        Map<BigInteger, List<AgreementSection>> newSubSectionListCoresspondingToSection = new HashMap<>();
        agreementSectionDTOS.forEach(agreementSectionDTO -> {
            if (!Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                newAgreementSectionDTOList.add(agreementSectionDTO);
            } else {
                agreementSectionIdList.add(agreementSectionDTO.getId());
                clauseBasicDTOCoresspondingToSections.put(agreementSectionDTO.getId(), agreementSectionDTO.getClauses());
                if (CollectionUtils.isNotEmpty(agreementSectionDTO.getSubSections())) {
                    List<BigInteger> subSectionIdList = new ArrayList<>();
                    List<AgreementSection> subSectionListCoresspondingToAgreementSection = new ArrayList<>();
                    for (AgreementSectionDTO agreementSubSectionDTO : agreementSectionDTO.getSubSections()) {
                        if (Optional.ofNullable(agreementSubSectionDTO.getId()).isPresent()) {
                            subSectionIdList.add(agreementSubSectionDTO.getId());
                            clauseBasicDTOCoresspondingToSections.put(agreementSubSectionDTO.getId(), agreementSubSectionDTO.getClauses());
                        } else {
                            AgreementSection subSection = new AgreementSection(countryId, agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex());
                            subSection.setOrganizationId(unitId);
                            if (CollectionUtils.isNotEmpty(agreementSubSectionDTO.getClauses())) {
                                globalAgreementSectionAndClauseDTOListHashMap.put(subSection, agreementSubSectionDTO.getClauses());
                            }
                            subSectionListCoresspondingToAgreementSection.add(subSection);
                        }
                    }
                    agreementSubSectionIdList.addAll(subSectionIdList);
                    newSubSectionListCoresspondingToSection.put(agreementSectionDTO.getId(), subSectionListCoresspondingToAgreementSection);
                }
            }
        });
        Map<AgreementSection, List<AgreementSection>> agreementSubSectionListCoresspondingToAgreementSection = new HashMap<>();
        if (CollectionUtils.isNotEmpty(newAgreementSectionDTOList)) {
            buildAgreementSectionAndSubSection(countryId, unitId, newAgreementSectionDTOList, agreementSubSectionListCoresspondingToAgreementSection, globalAgreementSectionAndClauseDTOListHashMap);
        }
        List<AgreementSection> agreementSections = agreementSectionMongoRepository.findAgreementSectionByIds(countryId, unitId, agreementSectionIdList);
        List<AgreementSection> agreementSubSectionList = agreementSectionMongoRepository.findAgreementSectionByIds(countryId, unitId, agreementSubSectionIdList);
        for (AgreementSection agreementSubSection : agreementSubSectionList) {
            if (!clauseBasicDTOCoresspondingToSections.get(agreementSubSection.getId()).isEmpty()) {
                globalAgreementSectionAndClauseDTOListHashMap.put(agreementSubSection, clauseBasicDTOCoresspondingToSections.get(agreementSubSection.getId()));
            }
        }
        for (AgreementSection agreementSection : agreementSections) {
            if (!clauseBasicDTOCoresspondingToSections.get(agreementSection.getId()).isEmpty()) {
                globalAgreementSectionAndClauseDTOListHashMap.put(agreementSection, clauseBasicDTOCoresspondingToSections.get(agreementSection.getId()));
            }
            agreementSubSectionListCoresspondingToAgreementSection.put(agreementSection, newSubSectionListCoresspondingToSection.get(agreementSection.getId()));
        }
        List<AgreementSection> agreementSectionList = new ArrayList<>();
        if (!globalAgreementSectionAndClauseDTOListHashMap.isEmpty()) {
            agreementSectionList = saveAndUpdateClauseOfAgreementSection(countryId, unitId, globalAgreementSectionAndClauseDTOListHashMap, agreementSubSectionListCoresspondingToAgreementSection, policyAgreementTemplate);
        } else {
            saveAgreementSectionAndSubSectionIfClauseNotExist(agreementSectionList, agreementSubSectionListCoresspondingToAgreementSection);
        }
        agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        return agreementSectionList.stream().map(AgreementSection::getId).collect(Collectors.toList());
    }


    /**
     * @param countryId
     * @param unitId
     * @param globalAgreementSectionAndClauseDTOListHashMap
     * @param agreementTemplate
     *///todo refactoring code
    private List<AgreementSection> saveAndUpdateClauseOfAgreementSection(Long countryId, Long unitId, Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap,
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
                if (clauseBasicDTO.getRequireUpdate() && Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
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
                clauseRelatedToAgreementSection = buildClauseForAgreementSection(countryId, unitId, newClauseRelatedToAgreementSection, agreementTemplate);
                clauseList.addAll(clauseRelatedToAgreementSection);
            }
            clauseListCoresspondingToAgreementSection.put(agreementSection, clauseRelatedToAgreementSection);
        });
        if (!exisitingClauseListCoresspondingToAgreementSections.isEmpty()) {
            clauseList.addAll(updateExisingClauseListOfAgreementSection(countryId, unitId, alteredClauseIdList, exisitingClauseListCoresspondingToAgreementSections, clauseListCoresspondingToAgreementSection));
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
            List<BigInteger> subSectionsIdList = new ArrayList<>();
            subSectionList.forEach(subSection -> subSectionsIdList.add(subSection.getId()));
            agreementSection.getSubSections().addAll(subSectionsIdList);
            agreementSubSections.add(agreementSection);
        });
        return agreementSubSections;

    }


    /**
     *
     * @param agreementSubSectionListAndCoresspondingToAgreementSectionMap
     * @param subSections
     * @param clauseListCoresspondingToAgreementSection
     * @param clauseOrderCoresspondingToAgreementSectionAndSubSection
     */
    private void sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap, List<AgreementSection> subSections,
                                                                                      Map<AgreementSection, List<Clause>> clauseListCoresspondingToAgreementSection, Map<AgreementSection, Map<BigInteger, Integer>> clauseOrderCoresspondingToAgreementSectionAndSubSection) {
        agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSectionList) ->
        {
            Map<BigInteger, Integer> agreementSectionClauseIdAndOrder = new HashMap<>();
            if (Optional.ofNullable(clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSection)).isPresent()) {
                agreementSectionClauseIdAndOrder = clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSection);
            }
            for (Clause clause : clauseListCoresspondingToAgreementSection.get(agreementSection)) {
                agreementSectionClauseIdAndOrder.put(clause.getId(), clause.getOrderedIndex());
            }
            agreementSection.setClauseIdOrderedIndex(agreementSectionClauseIdAndOrder.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList()));
            subSections.addAll(subSectionList);
            subSectionList.forEach(agreementSubSection -> {
                Map<BigInteger, Integer> agreementSubSectionClauseIdAndOrder = new HashMap<>();
                if (Optional.ofNullable(clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSubSection)).isPresent()) {
                    agreementSubSectionClauseIdAndOrder = clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSubSection);
                }
                for (Clause clause : clauseListCoresspondingToAgreementSection.get(agreementSubSection)) {
                    agreementSubSectionClauseIdAndOrder.put(clause.getId(), clause.getOrderedIndex());
                }
                agreementSubSection.setClauseIdOrderedIndex(agreementSubSectionClauseIdAndOrder.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList()));
            });
        });

    }


    /**
     * @param countryId
     * @param organizationId
     * @param clauseBasicDTOS
     * @param policyAgreementTemplate
     * @return
     */
    private List<Clause> buildClauseForAgreementSection(Long countryId, Long organizationId, List<ClauseBasicDTO> clauseBasicDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        List<Clause> clauseList = new ArrayList<>();
        for (ClauseBasicDTO clauseBasicDTO : clauseBasicDTOS) {
            Clause clause = new Clause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), countryId, policyAgreementTemplate.getOrganizationTypes(), policyAgreementTemplate.getOrganizationSubTypes()
                    , policyAgreementTemplate.getOrganizationServices(), policyAgreementTemplate.getOrganizationSubServices());
            List<BigInteger> templateTypes = new ArrayList<>();
            templateTypes.add(policyAgreementTemplate.getTemplateType());
            clause.setTemplateTypes(templateTypes);
            clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
            clause.setOrganizationId(organizationId);
            clause.setAccountTypes(policyAgreementTemplate.getAccountTypes());
            clauseList.add(clause);
        }
        return clauseList;
    }


    /**
     * @param countryId
     * @param unitId
     * @param existingClauseId
     * @param existingClauseMap
     * @param agreementSectionClauseList
     * @return
     *///todo refactoring code
    private List<Clause> updateExisingClauseListOfAgreementSection(Long countryId, Long unitId, List<BigInteger> existingClauseId, Map<AgreementSection, List<ClauseBasicDTO>> existingClauseMap, Map<AgreementSection, List<Clause>> agreementSectionClauseList) {

        List<Clause> exisitingClauseList = clauseMongoRepository.findClauseByCountryIdAndIdList(countryId, unitId, existingClauseId);
        Map<BigInteger, Clause> clauseIdMap = exisitingClauseList.stream().collect(Collectors.toMap(Clause::getId, clause -> clause));
        existingClauseMap.forEach((agreementSection, clauseBasicDTOS) -> {
            List<Clause> clausesRelateToAgreementSection = new ArrayList<>();
            clauseBasicDTOS.forEach(clauseBasicDTO -> {
                Clause clause = clauseIdMap.get(clauseBasicDTO.getId());
                ObjectMapperUtils.copyPropertiesExceptSpecific(clauseBasicDTO, clause);
                clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
                clausesRelateToAgreementSection.add(clause);
            });
            agreementSectionClauseList.get(agreementSection).addAll(clausesRelateToAgreementSection);
        });
        return exisitingClauseList;

    }


    /**
     * @param countryId
     * @param orgId
     * @param sectionId
     * @param clauseId
     * @return
     * @description remove clause id from Agreement section and Sub section if section contain clause and save section
     */
    public boolean removeClauseFromAgreementSection(Long countryId, Long orgId, BigInteger sectionId, BigInteger clauseId) {

        AgreementSection agreementSection = agreementSectionMongoRepository.findByIdAndNonDeleted(countryId, orgId, sectionId);
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

    public void checkForDuplicacyInTitleOfAgreementSectionAndSubSection(List<AgreementSectionDTO> agreementSectionDTOS) {
        List<String> titles = new ArrayList<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {
            if (titles.contains(agreementSectionDTO.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", agreementSectionDTO.getTitle());
            }
            if (Optional.ofNullable(agreementSectionDTO.getSubSections()).isPresent()) {
                agreementSectionDTO.getSubSections().forEach(agreementSubSectionDTO -> {
                    if (titles.contains(agreementSubSectionDTO.getTitle().toLowerCase())) {
                        exceptionService.duplicateDataException("message.duplicate", "questionnaire section", agreementSubSectionDTO.getTitle());
                    }
                    titles.add(agreementSubSectionDTO.getTitle());
                });
            }
            titles.add(agreementSectionDTO.getTitle().toLowerCase());
        }
    }

}

