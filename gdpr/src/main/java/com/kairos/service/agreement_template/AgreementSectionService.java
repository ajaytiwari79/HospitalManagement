package com.kairos.service.agreement_template;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.agreement_template.AgreementSectionDTO;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateSectionDTO;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
import com.kairos.persistence.model.agreement_template.AgreementSection;
import com.kairos.persistence.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause.MasterClause;
import com.kairos.persistence.model.clause.OrganizationClause;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.repository.agreement_template.AgreementSectionRepository;
import com.kairos.persistence.repository.agreement_template.PolicyAgreementRepository;
import com.kairos.persistence.repository.clause.ClauseRepository;
import com.kairos.persistence.repository.clause_tag.ClauseTagRepository;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.service.clause.ClauseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.s3bucket.AWSBucketService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AgreementSectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionService.class);


    @Inject
    private AgreementSectionRepository agreementSectionRepository;

    @Inject
    private ClauseRepository clauseRepository;
    @Inject
    private ClauseTagRepository clauseTagRepository;

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
     * @param isOrganization
     * @param templateId
     * @param agreementTemplateSectionDTO
     * @return
     */
    public AgreementTemplateSectionResponseDTO createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(Long referenceId, boolean isOrganization, Long templateId, AgreementTemplateSectionDTO agreementTemplateSectionDTO) {
        PolicyAgreementTemplate policyAgreementTemplate = isOrganization ? policyAgreementRepository.findByIdAndOrganizationIdAndDeletedFalse(templateId, referenceId) : policyAgreementRepository.findByIdAndCountryIdAndDeletedFalse(templateId, referenceId);
        if (!Optional.ofNullable(policyAgreementTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.policy.agreementTemplate", templateId);
        }
        if (CollectionUtils.isNotEmpty(agreementTemplateSectionDTO.getAgreementSections())) {
            checkForDuplicacyInTitleOfAgreementSectionAndSubSectionAndClauseTitle(agreementTemplateSectionDTO.getAgreementSections());
            List<AgreementSection> agreementSections = saveNewClausesAndMapToEmbeddedClausesOfSectionDTO(referenceId, isOrganization, policyAgreementTemplate, agreementTemplateSectionDTO.getAgreementSections());
            agreementSections.forEach(agreementSection -> agreementSection.linkSubSectionsWithParentSectionAndCountryOrUnitId(isOrganization, referenceId));
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
        return policyAgreementTemplateService.getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(referenceId, isOrganization, templateId);
    }

    @SuppressWarnings("unchecked")
    private List<ClauseBasicDTO> findNewClauses(List<AgreementSectionDTO> sectionDTOList) {
        List<ClauseBasicDTO> list = new ArrayList();
        sectionDTOList.forEach(section -> {
            List<ClauseBasicDTO> clauses = section.getClauses().stream().filter(c -> c.getId() == null).collect(Collectors.toList());
            clauses.addAll(findNewClauses(section.getAgreementSubSections()));
            list.addAll(clauses);
        });
        return list;
    }

    private void mapClauseIdToEmbeddedClausesOfSectionDTO(List<AgreementSectionDTO> sectionDTOList, Map<UUID, Long> clauseData) {
        sectionDTOList.forEach(section -> {
            section.getClauses().forEach(clause -> {
                if (clauseData.containsKey(clause.getTempClauseId())) {
                    clause.setId(clauseData.get(clause.getTempClauseId()));
                }
            });
            mapClauseIdToEmbeddedClausesOfSectionDTO(section.getAgreementSubSections(), clauseData);
        });
    }


    private List<AgreementSection> saveNewClausesAndMapToEmbeddedClausesOfSectionDTO(Long referenceId, boolean isOrganization, PolicyAgreementTemplate policyAgreementTemplate, List<AgreementSectionDTO> sectionDTOList) {
        List<ClauseBasicDTO> newClauses = findNewClauses(sectionDTOList);
        List<Clause> clauses = new ArrayList<>();
        ClauseTag defaultTag = clauseTagRepository.findDefaultTag();
        for (ClauseBasicDTO clauseBasicDTO : newClauses) {
            Clause clause;
            if (isOrganization)
                clause = new OrganizationClause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), Arrays.asList(defaultTag), Arrays.asList(policyAgreementTemplate.getTemplateType()), referenceId, clauseBasicDTO.getTempClauseId());
            else
                clause = new MasterClause(clauseBasicDTO.getTitle(), clauseBasicDTO.getDescription(), Arrays.asList(defaultTag), Arrays.asList(policyAgreementTemplate.getTemplateType()), referenceId, clauseBasicDTO.getTempClauseId(),
                        ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementTemplate.getAccountTypes(), AccountType.class),
                        ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementTemplate.getOrganizationTypes(), OrganizationType.class),
                        ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementTemplate.getOrganizationSubTypes(), OrganizationSubType.class),
                        ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementTemplate.getOrganizationServices(), ServiceCategory.class),
                        ObjectMapperUtils.copyPropertiesOfListByMapper(policyAgreementTemplate.getOrganizationSubServices(), SubServiceCategory.class));
            clauses.add(clause);
        }
        clauses = clauseRepository.saveAll(clauses);
        Map<UUID, Long> clauseData = new HashMap<>();
        clauses.forEach(clause -> clauseData.put(clause.getTempClauseId(), clause.getId()));
        mapClauseIdToEmbeddedClausesOfSectionDTO(sectionDTOList, clauseData);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(sectionDTOList, AgreementSection.class);
    }

    /**
     * @param templateId - Policy Agreement template id
     * @param sectionId  -agreement section id
     * @return -true on successful deletion of section
     */
    public boolean deleteAgreementSection(Long referenceId, boolean isOrganization, Long templateId, Long sectionId) {

        AgreementSection agreementSection = isOrganization ? agreementSectionRepository.findByIdAndOrganizationIdAndDeleted(sectionId, referenceId) : agreementSectionRepository.findByIdAndCountryIdAndDeleted(sectionId, referenceId);
        if (!Optional.ofNullable(agreementSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.agreement.section", sectionId);
        }
        PolicyAgreementTemplate policyAgreementTemplate = isOrganization ? policyAgreementRepository.findByIdAndOrganizationIdAndDeletedFalse(templateId, referenceId) : policyAgreementRepository.findByIdAndCountryIdAndDeletedFalse(templateId, referenceId);
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

        Integer updateCount = agreementSectionRepository.deleteAgreementSubSection(sectionId, subSectionId);
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.agreement.subSection" + sectionId);
        } else {
            LOGGER.info("Sub section with id :: {} is successfully deleted from section with id :: {}", subSectionId, sectionId);
        }

        return true;
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
        } else {
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

