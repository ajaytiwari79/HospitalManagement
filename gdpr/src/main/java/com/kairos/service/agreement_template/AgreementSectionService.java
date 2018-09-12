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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.AGREEMENT_SECTION;
import static com.kairos.constants.AppConstant.AGREEMENT_SECTION_WRAPPER;


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
        Boolean flag = false;
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOs) {

            if (Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                flag = true;
                break;
            }
        }
        List<BigInteger> agreementSectionIdList;
        if (flag) {
            agreementSectionIdList = updateAgreementSectionSubSectionAndClausesAndAddToAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        } else {
            agreementSectionIdList = createSectionClauseAndSubSectionsOfAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        }
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        return policyAgreementTemplateRepository.getAgreementTemplateAllSectionAndSubSections(countryId, organizationId, templateId);
    }


    /**
     * @param agreementSectionMap
     * @param changedClausesList
     * @param agreementSectionList
     * @param newClauseList
     * @return
     */
    public List<AgreementSection> updateExistingClauseAndAddSubSectionToAgreementSection(Map<String, AgreementSectionClauseWrapper> agreementSectionMap, Map<String, AgreementSectionClauseWrapper> agreementSubSectionMap,
                                                                                         List<Clause> changedClausesList, List<AgreementSection> agreementSectionList, List<Clause> newClauseList) {

        Map<BigInteger, Clause> updateClauseMap = new HashMap<>();
        changedClausesList.forEach(clause -> {
            updateClauseMap.put(clause.getId(), clause);
        });
        Map<String, Clause> newCreatedClauseMap = new HashMap<>();
        newClauseList.forEach(clause -> {
            newCreatedClauseMap.put(clause.getTitle(), clause);
        });
        List<Clause> updateExistingClauseList = new ArrayList<>();
        for (AgreementSection agreementSection : agreementSectionList) {
            AgreementSectionClauseWrapper agreementSectionClauseWrapper = agreementSectionMap.get(agreementSection.getTitle());
            List<ClauseBasicDTO> changedClauseBasicDTOList = agreementSectionClauseWrapper.getChangedClausesList();
            List<ClauseBasicDTO> newClauseBasicDTOList = agreementSectionClauseWrapper.getNewClauses();
            List<AgreementSection> agreementSubSectionList = agreementSectionClauseWrapper.getAgreementSubSections();
            if (!agreementSubSectionList.isEmpty()) {
                List<BigInteger> subSectionIdList = updateExistingClauseAddNewClauseAndAddToAgreementSubSection(agreementSubSectionMap, updateClauseMap, newCreatedClauseMap, agreementSubSectionList, updateExistingClauseList);
                agreementSection.setSubSections(subSectionIdList);
            }
            updateExistingClauseList = agreementSectionUpdateClauseListAndAddIdsToSection(changedClauseBasicDTOList, updateClauseMap, updateExistingClauseList, agreementSection);
            addNewCreatedClauseIdsToSectionOrSubSection(newClauseBasicDTOList, newCreatedClauseMap, agreementSection);
        }
        if (!updateExistingClauseList.isEmpty()) {
            clauseMongoRepository.saveAll(getNextSequence(updateExistingClauseList));
        }
        return agreementSectionList;
    }


    public List<BigInteger> updateExistingClauseAddNewClauseAndAddToAgreementSubSection(Map<String, AgreementSectionClauseWrapper> agreementSubSectionMap, Map<BigInteger, Clause> updateClauseMap,
                                                                                        Map<String, Clause> newCreatedClauseMap, List<AgreementSection> agreementSubSectionList, List<Clause> updateExistingClauseList) {


        for (AgreementSection agreementSection : agreementSubSectionList) {
            AgreementSectionClauseWrapper agreementSectionClauseWrapper = agreementSubSectionMap.get(agreementSection.getTitle());
            List<ClauseBasicDTO> changedClauseBasicDTOList = agreementSectionClauseWrapper.getChangedClausesList();
            List<ClauseBasicDTO> newClauseBasicDTOList = agreementSectionClauseWrapper.getNewClauses();
            updateExistingClauseList = agreementSectionUpdateClauseListAndAddIdsToSection(changedClauseBasicDTOList, updateClauseMap, updateExistingClauseList, agreementSection);
            addNewCreatedClauseIdsToSectionOrSubSection(newClauseBasicDTOList, newCreatedClauseMap, agreementSection);
        }
        agreementSubSectionList = agreementSectionMongoRepository.saveAll(getNextSequence(agreementSubSectionList));
        List<BigInteger> subSectionIdList = new ArrayList<>();
        agreementSubSectionList.forEach(agreementSection -> {
            subSectionIdList.add(agreementSection.getId());
        });

        return subSectionIdList;

    }


    /**
     * @param clauseBasicDTOList       - list of clauses which were changed by user
     * @param updateClauseMap          - map contain clause as value and key is clause id.
     * @param updateExistingClauseList -List of clauses which are changed and to reflect the changes in clauses
     * @param agreementSection         -contain list clause ids, add updated clauses to agreement sections
     * @return
     */
    //updated
    public List<Clause> agreementSectionUpdateClauseListAndAddIdsToSection(List<ClauseBasicDTO> clauseBasicDTOList, Map<BigInteger, Clause> updateClauseMap, List<Clause> updateExistingClauseList, AgreementSection agreementSection) {

        List<BigInteger> clauseIdList = agreementSection.getClauses();
        clauseBasicDTOList.forEach(clauseBasicDTO -> {
            Clause clause = updateClauseMap.get(clauseBasicDTO.getId());
            clause.setDescription(clauseBasicDTO.getDescription());
            clause.setOrderedIndex(clauseBasicDTO.getOrderedIndex());
            updateExistingClauseList.add(clause);
            clauseIdList.add(clauseBasicDTO.getId());
        });
        return updateExistingClauseList;
    }


    /**
     * @param clauseBasicDTOS         -dto contain list of clause which are created at the time of section creation
     * @param newCreatedClauseHashMap -contain new created clause value corresponding to its title
     * @param agreementSection        -agreement section contain name and list ids of clauses,
     * @description -this method is used in update Existing ClauseAndAddToAgreementSection .this method add ids of new created clause  to section which clause belong.
     */
    //updated
    public void addNewCreatedClauseIdsToSectionOrSubSection(List<ClauseBasicDTO> clauseBasicDTOS, Map<String, Clause> newCreatedClauseHashMap, AgreementSection agreementSection) {
        List<BigInteger> clauseIdList = agreementSection.getClauses();
        clauseBasicDTOS.forEach(clauseBasicDTO -> {
            Clause clause = newCreatedClauseHashMap.get(clauseBasicDTO.getTitle());
            clauseIdList.add(clause.getId());
        });
        agreementSection.setClauses(clauseIdList);
    }


    /**
     * @param countryId
     * @param organizationId
     * @param agreementSectionDTOS    -agreementSectionDTOS contain List of Agreement section Dto Which were already present and also contain new  Agreement Section
     *                                ,Sections contain Existing clause and new clause
     * @param policyAgreementTemplate - new clauses which we need to create inherit it properties from policyAgreementTemplate .
     * @return
     */
    public List<BigInteger> updateAgreementSectionSubSectionAndClausesAndAddToAgreementTemplate(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSectionAndSubSection(agreementSectionDTOS);
        List<AgreementSectionDTO> existingAgreementSectionDtoList = new ArrayList<>();
        List<AgreementSectionDTO> newAgreementSectionDTOList = new ArrayList<>();
        agreementSectionDTOS.forEach(agreementSectionDTO -> {
            if (Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                existingAgreementSectionDtoList.add(agreementSectionDTO);
            } else {
                newAgreementSectionDTOList.add(agreementSectionDTO);
            }
        });
        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        if (!newAgreementSectionDTOList.isEmpty()) {
            List<BigInteger> newCreatedAgreementSectionsId = createSectionClauseAndSubSectionsOfAgreementTemplate(countryId, organizationId, newAgreementSectionDTOList, policyAgreementTemplate);

            agreementSectionIdList.addAll(newCreatedAgreementSectionsId);

        }
        agreementSectionIdList.addAll(updateAgreementSectionsAndSubSectionsAndClauses(countryId, organizationId, existingAgreementSectionDtoList, policyAgreementTemplate));
        return agreementSectionIdList;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param agreementSectionDTOList
     * @param policyAgreementTemplate
     * @return
     */
    public List<BigInteger> updateAgreementSectionsAndSubSectionsAndClauses(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOList, PolicyAgreementTemplate policyAgreementTemplate) {

        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOList) {
            agreementSectionIdList.add(agreementSectionDTO.getId());
        }
        List<AgreementSection> agreementSectionList = agreementSectionMongoRepository.findAgreementSectionByIds(countryId, organizationId, agreementSectionIdList);

        Map<BigInteger, AgreementSection> agreementSectionMap = new HashMap<>();
        agreementSectionList.forEach(agreementSection -> {
            agreementSectionMap.put(agreementSection.getId(), agreementSection);
        });

        List<ClauseBasicDTO> changedClausesDTOList = new ArrayList<>();
        List<ClauseBasicDTO> newClauseBasicDTOList = new ArrayList<>();
        List<BigInteger> changedClauseIdsList = new ArrayList<>();
        agreementSectionList.clear();
        Map<String, AgreementSectionClauseWrapper> agreementSectionClauseAndClauseDtoHashMap = new HashMap<>();
        Map<String, AgreementSectionClauseWrapper> agreementSubSectionClauseAndClauseDtoHashMap = new HashMap<>();

        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOList) {
            AgreementSection agreementSection = agreementSectionMap.get(agreementSectionDTO.getId());
            agreementSection.setTitle(agreementSectionDTO.getTitle());
            agreementSection.setOrderedIndex(agreementSectionDTO.getOrderedIndex());
            AgreementSectionClauseWrapper sectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();
            if (agreementSectionDTO.getClauses() != null && !agreementSectionDTO.getClauses().isEmpty()) {
                List<BigInteger> unchangedClauseIdList = new ArrayList<>();
                List<ClauseBasicDTO> changedClauseBelongToSection = new ArrayList<>();
                List<ClauseBasicDTO> newClauseBelongToSection = new ArrayList<>();
                agreementSectionDTO.getClauses().forEach(clauseBasicDTO -> {
                    if (!Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                        newClauseBasicDTOList.add(clauseBasicDTO);
                        newClauseBelongToSection.add(clauseBasicDTO);
                    } else if (clauseBasicDTO.getRequireUpdate()) {
                        changedClauseIdsList.add(clauseBasicDTO.getId());
                        changedClausesDTOList.add(clauseBasicDTO);
                        changedClauseBelongToSection.add(clauseBasicDTO);
                    } else {
                        unchangedClauseIdList.add(clauseBasicDTO.getId());
                    }
                });
                sectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSection);
                sectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSection);
                agreementSection.setClauses(unchangedClauseIdList);
            }
            if (agreementSectionDTO.getSubSections() != null && !agreementSectionDTO.getSubSections().isEmpty()) {
                List<AgreementSection> agreementSubSectionList = updateSubSectionAndClausesList(newClauseBasicDTOList, changedClausesDTOList, changedClauseIdsList, agreementSectionDTO, agreementSubSectionClauseAndClauseDtoHashMap);
                sectionClauseAndClauseDtoWrapper.setAgreementSubSections(agreementSubSectionList);
            }
            agreementSectionClauseAndClauseDtoHashMap.put(agreementSection.getTitle(), sectionClauseAndClauseDtoWrapper);
            agreementSectionList.add(agreementSection);
        }
        List<Clause> newCreatedClausesList = new ArrayList<>();
        List<Clause> existingClauseList = new ArrayList<>();
        if (!newClauseBasicDTOList.isEmpty() || !changedClauseIdsList.isEmpty()) {

            if (!newClauseBasicDTOList.isEmpty()) {
                newCreatedClausesList = clauseService.createNewClauseUsingAgreementTemplateMetadata(countryId, organizationId, newClauseBasicDTOList, policyAgreementTemplate);
            }
            existingClauseList = clauseMongoRepository.findClauseByCountryIdAndIdList(countryId, organizationId, changedClauseIdsList);
        }
        agreementSectionList = updateExistingClauseAndAddSubSectionToAgreementSection(agreementSectionClauseAndClauseDtoHashMap, agreementSubSectionClauseAndClauseDtoHashMap, existingClauseList, agreementSectionList, newCreatedClausesList);
        agreementSectionIdList.clear();
        agreementSectionList = agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        agreementSectionList.forEach(agreementSection -> {
            agreementSectionIdList.add(agreementSection.getId());
        });

        return agreementSectionIdList;
    }


    /**
     * @param newClauseBasicDTOList
     * @param changedClausesDTOList
     * @param changedClauseIdsList
     * @param agreementSectionDTO
     * @param agreementSubSectionClauseAndClauseDtoHashMap
     * @return
     */
    public List<AgreementSection> updateSubSectionAndClausesList(List<ClauseBasicDTO> newClauseBasicDTOList, List<ClauseBasicDTO> changedClausesDTOList, List<BigInteger> changedClauseIdsList,
                                                                 AgreementSectionDTO agreementSectionDTO, Map<String, AgreementSectionClauseWrapper> agreementSubSectionClauseAndClauseDtoHashMap) {

        List<BigInteger> subSectionIdList = new ArrayList<>();

        agreementSectionDTO.getSubSections().forEach(agreementSubSectionDTO -> {
            subSectionIdList.add(agreementSubSectionDTO.getId());
        });
        List<AgreementSection> agreementSubSectionList = agreementSectionMongoRepository.findAgreementSectionByIds(UserContext.getCountryId(), UserContext.getOrgId(), subSectionIdList);
        Map<BigInteger, AgreementSection> agreementSubSectionMap = new HashMap<>();

        agreementSubSectionList.forEach(agreementSection -> {
            agreementSubSectionMap.put(agreementSection.getId(), agreementSection);
        });
        agreementSubSectionList.clear();
        agreementSectionDTO.getSubSections().forEach(agreementSubSectionDTO -> {
            AgreementSectionClauseWrapper agreementSubsectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();
            AgreementSection agreementSubSection;
            if (Optional.ofNullable(agreementSubSectionDTO.getId()).isPresent()) {
                agreementSubSection = agreementSubSectionMap.get(agreementSubSectionDTO.getId());
                agreementSubSection.setTitle(agreementSubSectionDTO.getTitle());
                agreementSubSection.setOrderedIndex(agreementSubSectionDTO.getOrderedIndex());
            } else {
                agreementSubSection = new AgreementSection(UserContext.getCountryId(), agreementSubSectionDTO.getTitle(), agreementSubSectionDTO.getOrderedIndex());
                agreementSubSection.setOrganizationId(UserContext.getOrgId());
            }

            if (agreementSubSectionDTO.getClauses() != null && !agreementSubSectionDTO.getClauses().isEmpty()) {
                List<BigInteger> unchangedClauseIdList = new ArrayList<>();
                List<ClauseBasicDTO> changedClauseBelongToSubSection = new ArrayList<>();
                List<ClauseBasicDTO> newClauseBelongToSubSection = new ArrayList<>();
                agreementSubSectionDTO.getClauses().forEach(clauseBasicDTO -> {
                    if (!Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                        newClauseBasicDTOList.add(clauseBasicDTO);
                        newClauseBelongToSubSection.add(clauseBasicDTO);
                    } else if (clauseBasicDTO.getRequireUpdate()) {
                        changedClauseIdsList.add(clauseBasicDTO.getId());
                        changedClausesDTOList.add(clauseBasicDTO);
                        changedClauseBelongToSubSection.add(clauseBasicDTO);
                    } else {
                        unchangedClauseIdList.add(clauseBasicDTO.getId());
                    }
                });
                agreementSubsectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSubSection);
                agreementSubsectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSubSection);
                agreementSubSection.setClauses(unchangedClauseIdList);
            }
            agreementSubSectionClauseAndClauseDtoHashMap.put(agreementSubSection.getTitle(), agreementSubsectionClauseAndClauseDtoWrapper);
            agreementSubSectionList.add(agreementSubSection);

        });
        return agreementSubSectionList;
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
    public List<BigInteger> createSectionClauseAndSubSectionsOfAgreementTemplate(Long countryId, Long unitId, List<AgreementSectionDTO> agreementSectionDTOs, PolicyAgreementTemplate policyAgreementTemplate) {

        Map<AgreementSection, List<AgreementSection>> agreementSubSectionListAndCoresspondingToAgreementSectionMap = new HashMap<>();
        Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap = new HashMap<>();
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
            agreementSubSectionListAndCoresspondingToAgreementSectionMap.put(agreementSection, subSectionList);
        }
        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        List<AgreementSection> agreementSectionList;
        if (!globalAgreementSectionAndClauseDTOListHashMap.isEmpty()) {
            agreementSectionList = saveAndUpdateClauseOfAgreementSection(countryId, unitId, globalAgreementSectionAndClauseDTOListHashMap, agreementSubSectionListAndCoresspondingToAgreementSectionMap, policyAgreementTemplate);
        } else {

            List<AgreementSection> subSectionList = new ArrayList<>();
            agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSections) -> subSectionList.addAll(subSections));
            agreementSectionMongoRepository.saveAll(getNextSequence(subSectionList));
            List<AgreementSection> agreementSections = new ArrayList<>();
            agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSections) -> {
                List<BigInteger> subSectionIdList = new ArrayList<>();
                subSections.forEach(agreementSubSection -> subSectionIdList.add(agreementSubSection.getId()));
                agreementSection.setSubSections(subSectionIdList);
                agreementSections.add(agreementSection);
            });
            agreementSectionList = agreementSections;
        }
        agreementSectionMongoRepository.saveAll(getNextSequence(agreementSectionList));
        agreementSectionList.forEach(agreementSection -> agreementSectionIdList.add(agreementSection.getId()));
        return agreementSectionIdList;
    }


    /**
     * @param countryId
     * @param unitId
     * @param globalAgreementSectionAndClauseDTOListHashMap
     * @param agreementTemplate
     *///todo refactoring code
    public List<AgreementSection> saveAndUpdateClauseOfAgreementSection(Long countryId, Long unitId, Map<AgreementSection, List<ClauseBasicDTO>> globalAgreementSectionAndClauseDTOListHashMap,
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
                if (clauseBasicDTO.getRequireUpdate()) {
                    alteredClauseIdList.add(clauseBasicDTO.getId());
                    exisitingClauseList.add(clauseBasicDTO);
                } else if (Optional.ofNullable(clauseBasicDTO.getId()).isPresent()) {
                    agreementSection.getClauses().add(clauseBasicDTO.getId());
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
        List<AgreementSection> agreementSections = new ArrayList<>();
        clauseMongoRepository.saveAll(getNextSequence(clauseList));
        sortClauseByOrderIndexAndAddClauseIdToAgreementSectionAndSubSections(agreementSubSectionListAndCoresspondingToAgreementSectionMap, agreementSections, clauseListCoresspondingToAgreementSection, clauseOrderCoresspondingToAgreementSectionAndSubSection);
        if (!agreementSections.isEmpty()) {
            agreementSectionMongoRepository.saveAll(getNextSequence(agreementSections));
        }
        agreementSections.clear();
        agreementSubSectionListAndCoresspondingToAgreementSectionMap.forEach((agreementSection, subSectionList) -> {
            List<BigInteger> subSectionsIdList = new ArrayList<>();
            subSectionList.forEach(subSection -> subSectionsIdList.add(subSection.getId()));
            agreementSection.setSubSections(subSectionsIdList);
            agreementSections.add(agreementSection);
        });
        return agreementSections;

    }


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
            sortClauseByOrderIndex(agreementSectionClauseIdAndOrder, agreementSection);
            subSections.addAll(subSectionList);
            subSectionList.forEach(agreementSubSection -> {
                Map<BigInteger, Integer> agreementSubSectionClauseIdAndOrder = new HashMap<>();
                if (Optional.ofNullable(clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSubSection)).isPresent()) {
                    agreementSubSectionClauseIdAndOrder = clauseOrderCoresspondingToAgreementSectionAndSubSection.get(agreementSubSection);
                }
                for (Clause clause : clauseListCoresspondingToAgreementSection.get(agreementSubSection)) {
                    agreementSubSectionClauseIdAndOrder.put(clause.getId(), clause.getOrderedIndex());
                }
                sortClauseByOrderIndex(agreementSubSectionClauseIdAndOrder, agreementSubSection);

            });
        });

    }

    private void sortClauseByOrderIndex(Map<BigInteger, Integer> clauseIdAndOrder, AgreementSection agreementSection) {
        List<BigInteger> orderedClauseIdList = clauseIdAndOrder.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList());
        agreementSection.setClauses(orderedClauseIdList);
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


        existingClauseMap.forEach((agreementSection, clauseBasicDTOS) ->
        {
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
        List<BigInteger> clausesList = agreementSection.getClauses();
        if (clausesList.contains(clauseId)) {
            clausesList.remove(clauseId);
            agreementSection.setClauses(clausesList);
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



