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
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
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


    public PolicyAgreementTemplate createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long countryId, Long organizationId, BigInteger templateId, List<AgreementSectionDTO> agreementSectionDTOs) {

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
            agreementSectionIdList = createAggrementSectionsAndClausesAndAddtoAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        }
        policyAgreementTemplate = policyAgreementTemplateRepository.save(policyAgreementTemplate);
        return policyAgreementTemplate;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param agreementSectionDTOS
     * @param policyAgreementTemplate - object for inheriting properties (orgnaization Types,organization Sub Types,Service Category  and Sub Service Category) for new clauses
     * @return
     */
    //updated for section and sub section creations
    public List<BigInteger> createAggrementSectionsAndClausesAndAddtoAgreementTemplate(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSectionAndSubSection(agreementSectionDTOS);
        List<AgreementSection> agreementSectionList = new ArrayList<>();
        List<ClauseBasicDTO> changedClausesDTOList = new ArrayList<>();
        List<ClauseBasicDTO> newClauseBasicDTOList = new ArrayList<>();
        List<BigInteger> changedClauseIdsList = new ArrayList<>();
        Map<String, AgreementSectionClauseWrapper> agreementSectionClauseAndClauseDtoHashMap = new HashMap<>();
        Map<String, AgreementSectionClauseWrapper> agreementSubSectionClauseAndClauseDtoHashMap = new HashMap<>();

        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {

            Map<String, Object> agreementSectionBuildMap = buildAgreementSectionsList(newClauseBasicDTOList, changedClausesDTOList, changedClauseIdsList, agreementSectionDTO, agreementSubSectionClauseAndClauseDtoHashMap);
            AgreementSection agreementSection = (AgreementSection) agreementSectionBuildMap.get(AGREEMENT_SECTION);
            agreementSectionClauseAndClauseDtoHashMap.put(agreementSection.getName(), (AgreementSectionClauseWrapper) agreementSectionBuildMap.get(AGREEMENT_SECTION_WRAPPER));
            agreementSectionList.add(agreementSection);
        }
        List<Clause> newCreatedClausesList = new ArrayList<>();
        List<Clause> exisitingClauseList = new ArrayList<>();
        if (!newClauseBasicDTOList.isEmpty() || !changedClauseIdsList.isEmpty()) {
            if (!newClauseBasicDTOList.isEmpty()) {
                newCreatedClausesList = clauseService.createNewClauseUsingAgreementTemplateMetadata(countryId, organizationId, newClauseBasicDTOList, policyAgreementTemplate);
            }
            exisitingClauseList = clauseMongoRepository.getClauseListByIds(countryId, organizationId, changedClauseIdsList);
        }
        agreementSectionList = updateExisitingClauseAndAddSubSectionToAgreementSection(agreementSectionClauseAndClauseDtoHashMap, agreementSubSectionClauseAndClauseDtoHashMap, exisitingClauseList, agreementSectionList, newCreatedClausesList);
        List<BigInteger> agreementSectionIdList = new ArrayList<>();

        agreementSectionList = agreementSectionMongoRepository.saveAll(sequenceGenerator(agreementSectionList));
        agreementSectionList.forEach(agreementSection -> {
            agreementSectionIdList.add(agreementSection.getId());
        });
        return agreementSectionIdList;
    }


    /**
     * @description - method addd new clause  of section and sub sections to  newClauseBasicDTOList ,so that we can create all the new clause at single time for all section and sub sections
     *                similarly changedClausesDTOList contain list of clauses whic are changed and we update the exisiting clauses single time.
     * @param newClauseBasicDTOList   -list of new clause
     * @param changedClausesDTOList  - list of clause which are changed and we need update those clause
     * @param changedClauseIdsList -list of clause id which we need to updated
     * @param agreementSectionDTO   ;-agreement section Dto contain list of clauses and  list of Sub section(sub section contain list of clauses )
     * @param agreementSubSectionClauseAndClauseDtoHashMap - map contain list of clauses (new clause ,and clause which need to be update)
     *                                                     corresponding to Agreement sub section name which is unique for every subsection.
     * @return       method return Object of agreement and Wrapper class which contain  Clause and Subsections corresponding to section
     */
    public Map<String, Object> buildAgreementSectionsList(List<ClauseBasicDTO> newClauseBasicDTOList, List<ClauseBasicDTO> changedClausesDTOList, List<BigInteger> changedClauseIdsList,
                                                          AgreementSectionDTO agreementSectionDTO, Map<String, AgreementSectionClauseWrapper> agreementSubSectionClauseAndClauseDtoHashMap) {
        Map<String, Object> result = new HashMap<>();
        AgreementSection agreementSection = new AgreementSection(UserContext.getCountryId(), agreementSectionDTO.getName());
        agreementSection.setOrganizationId(UserContext.getOrgId());
        AgreementSectionClauseWrapper sectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();

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
                    changedClausesDTOList.add(clauseBasicDTO);
                    changedClauseBelongToSection.add(clauseBasicDTO);
                } else {
                    unchangedClauseidList.add(clauseBasicDTO.getId());
                }
            });
            sectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSection);
            sectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSection);
            agreementSection.setClauses(unchangedClauseidList);
        }
        if (Optional.ofNullable(agreementSectionDTO.getSubAgreementSections()).isPresent() && !agreementSectionDTO.getSubAgreementSections().isEmpty()) {
            List<AgreementSection> agreementSubSectionList = new ArrayList<>();
            agreementSectionDTO.getSubAgreementSections().forEach(agreementSubSectionDTO -> {
                AgreementSection agreementSubSection = new AgreementSection(UserContext.getCountryId(), agreementSubSectionDTO.getName());
                agreementSubSection.setOrganizationId(UserContext.getOrgId());
                AgreementSectionClauseWrapper agreementSubsectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();

                if (Optional.ofNullable(agreementSectionDTO.getClauses()).isPresent() && !agreementSubSectionDTO.getClauses().isEmpty()) {
                    List<BigInteger> unchangedClauseidList = new ArrayList<>();
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
                            unchangedClauseidList.add(clauseBasicDTO.getId());
                        }
                    });
                    agreementSubsectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSubSection);
                    agreementSubsectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSubSection);
                    agreementSubSection.setClauses(unchangedClauseidList);
                }
                agreementSubSectionClauseAndClauseDtoHashMap.put(agreementSubSection.getName(), agreementSubsectionClauseAndClauseDtoWrapper);
                agreementSubSectionList.add(agreementSubSection);

            });
            sectionClauseAndClauseDtoWrapper.setAgreementSubSections(agreementSubSectionList);
        }
        result.put(AGREEMENT_SECTION, agreementSection);
        result.put(AGREEMENT_SECTION_WRAPPER, sectionClauseAndClauseDtoWrapper);
        return result;

    }


    /**
     * @param agreementSectionMap
     * @param changedClausesList
     * @param agreementSectionList
     * @param newClauseList
     * @return
     */
    public List<AgreementSection> updateExisitingClauseAndAddSubSectionToAgreementSection(Map<String, AgreementSectionClauseWrapper> agreementSectionMap, Map<String, AgreementSectionClauseWrapper> agreementSubSectionMap,
                                                                                          List<Clause> changedClausesList, List<AgreementSection> agreementSectionList, List<Clause> newClauseList) {

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
            AgreementSectionClauseWrapper agreementSectionClauseWrapper = agreementSectionMap.get(agreementSection.getName());
            List<ClauseBasicDTO> changedClauseBasicDTOList = agreementSectionClauseWrapper.getChangedClausesList();
            List<ClauseBasicDTO> newClauseBasicDTOList = agreementSectionClauseWrapper.getNewClauses();
            List<AgreementSection> agreementSubSectionList = agreementSectionClauseWrapper.getAgreementSubSections();
            if (!agreementSubSectionList.isEmpty()) {
                List<BigInteger> subSectionIdList = updateExisitingClauseAddNewClauseAndAddToAgreementSubSection(agreementSubSectionMap, updateClauseMap, newCreatedClauseMap, agreementSubSectionList, updateExisitingClauseList);
                agreementSection.setSubAgreementSections(subSectionIdList);
            }
            updateExisitingClauseList = agreementSectionUpdateClauseListAndAddIdsToSection(changedClauseBasicDTOList, updateClauseMap, updateExisitingClauseList, agreementSection);
            addNewCreatedClauseIdsToSectionOrSubSection(newClauseBasicDTOList, newCreatedClauseMap, agreementSection);
        }
        if (!updateExisitingClauseList.isEmpty()) {
            clauseMongoRepository.saveAll(sequenceGenerator(updateExisitingClauseList));
        }
        return agreementSectionList;
    }


    public List<BigInteger> updateExisitingClauseAddNewClauseAndAddToAgreementSubSection(Map<String, AgreementSectionClauseWrapper> agreementSubSectionMap, Map<BigInteger, Clause> updateClauseMap,
                                                                                         Map<String, Clause> newCreatedClauseMap, List<AgreementSection> agreementSubSectionList, List<Clause> updateExisitngClauseList) {


        for (AgreementSection agreementSection : agreementSubSectionList) {
            AgreementSectionClauseWrapper agreementSectionClauseWrapper = agreementSubSectionMap.get(agreementSection.getName());
            List<ClauseBasicDTO> changedClauseBasicDTOList = agreementSectionClauseWrapper.getChangedClausesList();
            List<ClauseBasicDTO> newClauseBasicDTOList = agreementSectionClauseWrapper.getNewClauses();
            updateExisitngClauseList = agreementSectionUpdateClauseListAndAddIdsToSection(changedClauseBasicDTOList, updateClauseMap, updateExisitngClauseList, agreementSection);
            addNewCreatedClauseIdsToSectionOrSubSection(newClauseBasicDTOList, newCreatedClauseMap, agreementSection);
        }
        agreementSubSectionList = agreementSectionMongoRepository.saveAll(sequenceGenerator(agreementSubSectionList));
        List<BigInteger> subSectionIdList = new ArrayList<>();
        agreementSubSectionList.forEach(agreementSection -> {
            subSectionIdList.add(agreementSection.getId());
        });

        return subSectionIdList;

    }


    /**
     * @param clauseBasicDTOList        - list of clauses which were changed by user
     * @param updateClauseMap           - map contain clause as value and key is clause id.
     * @param updateExisitingClauseList -List of clauses which are changed and to reflect the changes in clauses
     * @param agreementSection          -contain list clause ids, add updated clauses to agreement sections
     * @return
     */
    //updated
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
     * @description -this method is used in updateExisitingClauseAndAddToAgreementSection .this method add ids of new created clause  to section which clause belong.
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
     *                                ,Sections contain Exisitng clause and new clause
     * @param policyAgreementTemplate - new clauses which we need to create inherit it properties from policyAgreementTemplate .
     * @return
     */
    public List<BigInteger> updateAgreementSectionSubSectionAndClausesAndAddToAgreementTemplate(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSectionAndSubSection(agreementSectionDTOS);
        List<AgreementSectionDTO> exisitingAgreementSectionDtoList = new ArrayList<>();
        List<AgreementSectionDTO> newAgreementSectionDTOList = new ArrayList<>();
        agreementSectionDTOS.forEach(agreementSectionDTO -> {
            if (Optional.ofNullable(agreementSectionDTO.getId()).isPresent()) {
                exisitingAgreementSectionDtoList.add(agreementSectionDTO);
            } else {
                newAgreementSectionDTOList.add(agreementSectionDTO);
            }
        });
        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        if (!newAgreementSectionDTOList.isEmpty()) {
            List<BigInteger> newCreatedAgreementSectionsId = createAggrementSectionsAndClausesAndAddtoAgreementTemplate(countryId, organizationId, newAgreementSectionDTOList, policyAgreementTemplate);
            agreementSectionIdList.addAll(newCreatedAgreementSectionsId);

        }
        agreementSectionIdList.addAll(updateAgreementSectionsAndSubSectionsAndClauses(countryId, organizationId, exisitingAgreementSectionDtoList, policyAgreementTemplate));
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
            agreementSection.setName(agreementSectionDTO.getName());
            AgreementSectionClauseWrapper sectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();
            if (agreementSectionDTO.getClauses() != null && !agreementSectionDTO.getClauses().isEmpty()) {
                List<BigInteger> unchangedClauseidList = new ArrayList<>();
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
                        unchangedClauseidList.add(clauseBasicDTO.getId());
                    }
                });
                sectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSection);
                sectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSection);
                agreementSection.setClauses(unchangedClauseidList);
            }
            if (agreementSectionDTO.getSubAgreementSections() != null && !agreementSectionDTO.getSubAgreementSections().isEmpty()) {
                List<AgreementSection> agreementSubSectionList = updateSubSectionAndClausesList(newClauseBasicDTOList, changedClausesDTOList, changedClauseIdsList, agreementSectionDTO, agreementSubSectionClauseAndClauseDtoHashMap);
                sectionClauseAndClauseDtoWrapper.setAgreementSubSections(agreementSubSectionList);
            }
            agreementSectionClauseAndClauseDtoHashMap.put(agreementSection.getName(), sectionClauseAndClauseDtoWrapper);
            agreementSectionList.add(agreementSection);
        }
        List<Clause> newCreatedClausesList = new ArrayList<>();
        List<Clause> exisitingClauseList = new ArrayList<>();
        if (!newClauseBasicDTOList.isEmpty() || !changedClauseIdsList.isEmpty()) {

            if (!newClauseBasicDTOList.isEmpty()) {
                newCreatedClausesList = clauseService.createNewClauseUsingAgreementTemplateMetadata(countryId, organizationId, newClauseBasicDTOList, policyAgreementTemplate);
            }
            exisitingClauseList = clauseMongoRepository.getClauseListByIds(countryId, organizationId, changedClauseIdsList);
        }
        agreementSectionList = updateExisitingClauseAndAddSubSectionToAgreementSection(agreementSectionClauseAndClauseDtoHashMap, agreementSubSectionClauseAndClauseDtoHashMap, exisitingClauseList, agreementSectionList, newCreatedClausesList);
        agreementSectionIdList.clear();
        agreementSectionList = agreementSectionMongoRepository.saveAll(sequenceGenerator(agreementSectionList));
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

        agreementSectionDTO.getSubAgreementSections().forEach(agreementSubSectionDTO -> {
            subSectionIdList.add(agreementSubSectionDTO.getId());
        });
        List<AgreementSection> agreementSubSectionList = agreementSectionMongoRepository.findAgreementSectionByIds(UserContext.getCountryId(), UserContext.getOrgId(), subSectionIdList);
        Map<BigInteger, AgreementSection> agreementSubSectionMap = new HashMap<>();

        agreementSubSectionList.forEach(agreementSection -> {
            agreementSubSectionMap.put(agreementSection.getId(), agreementSection);
        });
        agreementSubSectionList.clear();
        agreementSectionDTO.getSubAgreementSections().forEach(agreementSubSectionDTO -> {
            AgreementSectionClauseWrapper agreementSubsectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();
            AgreementSection agreementSubSection;
            if (Optional.ofNullable(agreementSubSectionDTO.getId()).isPresent()) {
                agreementSubSection = agreementSubSectionMap.get(agreementSubSectionDTO.getId());
                agreementSubSection.setName(agreementSubSectionDTO.getName());
            } else {
                agreementSubSection = new AgreementSection(UserContext.getCountryId(), agreementSubSectionDTO.getName());
                agreementSubSection.setOrganizationId(UserContext.getOrgId());
            }

            if (agreementSubSectionDTO.getClauses() != null && !agreementSubSectionDTO.getClauses().isEmpty()) {
                List<BigInteger> unchangedClauseidList = new ArrayList<>();
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
                        unchangedClauseidList.add(clauseBasicDTO.getId());
                    }
                });
                agreementSubsectionClauseAndClauseDtoWrapper.setChangedClausesList(changedClauseBelongToSubSection);
                agreementSubsectionClauseAndClauseDtoWrapper.setNewClauses(newClauseBelongToSubSection);
                agreementSubSection.setClauses(unchangedClauseidList);
            }
            agreementSubSectionClauseAndClauseDtoHashMap.put(agreementSubSection.getName(), agreementSubsectionClauseAndClauseDtoWrapper);
            agreementSubSectionList.add(agreementSubSection);

        });
        return agreementSubSectionList;
    }

    /**
     * @param countryId
     * @param orgId
     * @param templateId - Policy Agreement template id
     * @param id         -agreement section id
     * @return -true on successfull deletion of section
     */
    public Boolean deleteAgreementSection(Long countryId, Long orgId, BigInteger templateId, BigInteger id) {

        AgreementSection exist = agreementSectionMongoRepository.findByIdAndNonDeleted(countryId, orgId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Agreement section " + id);
        }
        PolicyAgreementTemplate policyAgreementTemplate = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, orgId, templateId);
        List<BigInteger> agreementSectionIdList = policyAgreementTemplate.getAgreementSections();
        agreementSectionIdList.remove(id);
        policyAgreementTemplateRepository.save(sequenceGenerator(policyAgreementTemplate));
        agreementSectionMongoRepository.delete(exist);
        return true;
    }


    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId, BigInteger id) {

        AgreementSectionResponseDTO exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

    }


    public void checkForDuplicacyInTitleOfAgreementSectionAndSubSection(List<AgreementSectionDTO> agreementSectionDTOS) {
        List<String> titles = new ArrayList<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {
            if (titles.contains(agreementSectionDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", agreementSectionDTO.getName());
            }
            if (Optional.ofNullable(agreementSectionDTO.getSubAgreementSections()).isPresent()) {
                agreementSectionDTO.getSubAgreementSections().forEach(agreementSubSectionDTO -> {
                    if (titles.contains(agreementSubSectionDTO.getName().toLowerCase())) {
                        exceptionService.duplicateDataException("message.duplicate", "questionnaire section", agreementSubSectionDTO.getName());
                    }
                    titles.add(agreementSubSectionDTO.getName());
                });
            }
            titles.add(agreementSectionDTO.getName().toLowerCase());
        }
    }

}

