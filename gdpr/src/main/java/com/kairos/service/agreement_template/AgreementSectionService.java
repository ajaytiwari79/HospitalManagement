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
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.AGREEMENT_SECTION;
import static com.kairos.constants.AppConstant.AGREEMENT_SECTION_WRAPPER;
import static com.kairos.constants.AppConstant.AGREEMENT_SUB_SECTION_MAP_CONTAINING_CLAUSE;



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
            agreementSectionIdList = updateAggrementSectionsAndClausesAndAddToAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        } else {
            agreementSectionIdList = createAggrementSectionsAndClausesAndAddtoAgreementTemplate(countryId, organizationId, agreementSectionDTOs, policyAgreementTemplate);
            policyAgreementTemplate.setAgreementSections(agreementSectionIdList);
        }
        try {
            policyAgreementTemplate = policyAgreementTemplateRepository.save(policyAgreementTemplate);
        } catch (MongoException e) {
            LOGGER.warn("Agreement template exception while saving template " + policyAgreementTemplate.getName());
            remove(agreementSectionIdList, AgreementSection.class);
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
    public List<BigInteger> createAggrementSectionsAndClausesAndAddtoAgreementTemplate(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSections(agreementSectionDTOS);
        List<AgreementSection> agreementSectionList = new ArrayList<>();
        List<ClauseBasicDTO> changedClausesDTOList = new ArrayList<>();
        List<ClauseBasicDTO> newClauseBasicDTOList = new ArrayList<>();
        List<BigInteger> changedClauseIdsList = new ArrayList<>();
        Map<String, AgreementSectionClauseWrapper> agreementSectionClauseAndClauseDtoHashMap = new HashMap<>();
        Map<String, AgreementSectionClauseWrapper> agreementSubSectionClauseAndClauseDtoHashMap = new HashMap<>();


        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOS) {
          /*  AgreementSection agreementSection = new AgreementSection(countryId, agreementSectionDTO.getName());
            agreementSection.setOrganizationId(organizationId);
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
*/
          Map<String,Object>  agreementSectionBuildMap=buildAgreementSectionsList( newClauseBasicDTOList,  changedClausesDTOList,  changedClauseIdsList , agreementSectionDTO ,agreementSubSectionClauseAndClauseDtoHashMap);
            AgreementSection agreementSection=(AgreementSection) agreementSectionBuildMap.get(AGREEMENT_SECTION);
            agreementSectionClauseAndClauseDtoHashMap.put(agreementSection.getName(), (AgreementSectionClauseWrapper) agreementSectionBuildMap.get(AGREEMENT_SECTION_WRAPPER));
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
        try {
            agreementSectionList = agreementSectionMongoRepository.saveAll(sequenceGenerator(agreementSectionList));
            agreementSectionList.forEach(agreementSection -> {
                agreementSectionIdList.add(agreementSection.getId());
            });
        } catch (MongoException e) {
            LOGGER.warn("Agreement section exception while saving sections:" + e.getMessage());
            throw new RuntimeException(e);
        }
        return agreementSectionIdList;
    }




    public Map<String,Object> buildAgreementSectionsList( List<ClauseBasicDTO> newClauseBasicDTOList, List<ClauseBasicDTO> changedClausesDTOList, List<BigInteger> changedClauseIdsList ,
                                            AgreementSectionDTO agreementSectionDTO ,Map<String, AgreementSectionClauseWrapper> agreementSubSectionClauseAndClauseDtoHashMap)
    {
        Map<String,Object> result=new HashMap<>();
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
        else if (Optional.ofNullable(agreementSectionDTO.getSubAgreementSections()).isPresent() && !agreementSectionDTO.getSubAgreementSections().isEmpty())
        {
            agreementSectionDTO.getSubAgreementSections().forEach(agreementSubSectionDTO->{

                AgreementSection agreementSubSection=new AgreementSection(UserContext.getCountryId(), agreementSubSectionDTO.getName());
                agreementSubSection.setOrganizationId(UserContext.getOrgId());
                AgreementSectionClauseWrapper agreementSubsectionClauseAndClauseDtoWrapper = new AgreementSectionClauseWrapper();

                if (Optional.ofNullable(agreementSubSectionDTO.getClauses()).isPresent() && !agreementSubSectionDTO.getClauses().isEmpty()) {
                    List<BigInteger> unchangedClauseidList = new ArrayList<>();
                    List<ClauseBasicDTO> changedClauseBelongToSubSection = new ArrayList<>();
                    List<ClauseBasicDTO> newClauseBelongToSubSection = new ArrayList<>();
                    agreementSectionDTO.getClauses().forEach(clauseBasicDTO -> {
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
                result.put(AGREEMENT_SUB_SECTION_MAP_CONTAINING_CLAUSE,agreementSubsectionClauseAndClauseDtoWrapper);

            });
            sectionClauseAndClauseDtoWrapper.setAgreementSubSections(agreementSectionDTO.getSubAgreementSections());
        }

        result.put(AGREEMENT_SECTION,agreementSection);
        result.put(AGREEMENT_SECTION_WRAPPER,sectionClauseAndClauseDtoWrapper);
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
        if (!updateExisitingClauseList.isEmpty()) {
            clauseMongoRepository.saveAll(sequenceGenerator(updateExisitingClauseList));
        }
        return agreementSectionList;

    }


    /**
     * @param clauseBasicDTOList        - list of clauses which were changed by user
     * @param updateClauseMap           - map contain clause as value and key is clause id.
     * @param updateExisitingClauseList -List of clauses which are changed and to reflect the changes in clauses
     * @param agreementSection          -contain list clause ids, add updated clauses to agreement sections
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
     * @description                   -this method is used in updateExisitingClauseAndAddToAgreementSection .this method add ids of new created clause  to section which clause belong.
     */
    public void addNewCreatedClauseIdsToAgreementSections(List<ClauseBasicDTO> clauseBasicDTOS, Map<String, Clause> newCreatedClauseHashMap, AgreementSection agreementSection) {
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
     *                                 ,Sections contain Exisitng clause and new clause
     * @param policyAgreementTemplate - new clauses which we need to create inherit it properties from policyAgreementTemplate .
     * @return
     */
    public List<BigInteger> updateAggrementSectionsAndClausesAndAddToAgreementTemplate(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOS, PolicyAgreementTemplate policyAgreementTemplate) {

        checkForDuplicacyInTitleOfAgreementSections(agreementSectionDTOS);
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
            agreementSectionIdList.addAll(createAggrementSectionsAndClausesAndAddtoAgreementTemplate(countryId, organizationId, newAgreementSectionDTOList, policyAgreementTemplate));

        }
        agreementSectionIdList.addAll(updateAggrementSectionsAndClauses(countryId, organizationId, exisitingAgreementSectionDtoList, policyAgreementTemplate));
        return agreementSectionIdList;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param policyAgreementTemplate - new clauses which we need to create inherit it properties from policyAgreementTemplate .
     * @return                      - list of Agreement Section ids
     * @description                - AgreementSectionClauseWrapper class  conatain list of clauses for new creation of clause and Clause which needs to be update.
     *this method update exisiting Sections with  list of clauses( changed or updated ,unchanged and new clause )
     * update  exisiting Agreement sections and clauses if exist. or create new clause in exisiting agreement sections
     */
    public List<BigInteger> updateAggrementSectionsAndClauses(Long countryId, Long organizationId, List<AgreementSectionDTO> agreementSectionDTOSList, PolicyAgreementTemplate policyAgreementTemplate) {


        List<BigInteger> agreementSectionIdList = new ArrayList<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOSList) {
            agreementSectionIdList.add(agreementSectionDTO.getId());
        }
        List<AgreementSection> agreementSectionList = agreementSectionMongoRepository.findAgreementSectionByIds(countryId, organizationId, agreementSectionIdList);
        Map<BigInteger, AgreementSection> agreementSectiosnMap = new HashMap<>();
        agreementSectionList.forEach(agreementSection -> {
            agreementSectiosnMap.put(agreementSection.getId(), agreementSection);
        });

        List<ClauseBasicDTO> changedClausesDTOList = new ArrayList<>();
        List<ClauseBasicDTO> newClauseBasicDTOList = new ArrayList<>();
        List<BigInteger> changedClauseIdsList = new ArrayList<>();
        agreementSectionList.clear();
        Map<String, AgreementSectionClauseWrapper> agreementSectionClauseAndClauseDtoHashMap = new HashMap<>();
        for (AgreementSectionDTO agreementSectionDTO : agreementSectionDTOSList) {
            AgreementSection agreementSection = agreementSectiosnMap.get(agreementSectionDTO.getId());
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
        try {
            agreementSectionIdList.clear();
            agreementSectionList = agreementSectionMongoRepository.saveAll(sequenceGenerator(agreementSectionList));
            agreementSectionList.forEach(agreementSection -> {
                agreementSectionIdList.add(agreementSection.getId());
            });
        } catch (MongoException e) {
            LOGGER.warn("Agreement section exception while saving sections:" + e.getMessage());
            throw new RuntimeException(e);
        }
        return agreementSectionIdList;


    }


    /**@param countryId
     * @param orgId
     * @param templateId     - Policy Agreement template id
     * @param id             -agreement section id
     * @return               -true on successfull deletion of section
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
        delete(exist);
        return true;
    }


    public AgreementSectionResponseDTO getAgreementSectionWithDataById(Long countryId, BigInteger id) {

        AgreementSectionResponseDTO exist = agreementSectionMongoRepository.getAgreementSectionWithDataById(id);
        if (Optional.ofNullable(exist).isPresent()) {
            return exist;
        }
        throw new DataNotFoundByIdException("agreement section for id " + id + " not exist");

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

