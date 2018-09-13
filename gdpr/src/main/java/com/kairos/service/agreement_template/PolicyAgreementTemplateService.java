package com.kairos.service.agreement_template;


import com.kairos.dto.gdpr.PolicyAgreementTemplateDTO;
import com.kairos.persistance.model.agreement_template.PolicyAgreementTemplate;
import com.kairos.persistance.repository.agreement_template.PolicyAgreementTemplateRepository;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.template_type.TemplateTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PolicyAgreementTemplateService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateService.class);

    @Inject
    private PolicyAgreementTemplateRepository policyAgreementTemplateRepository;


    @Inject
    private AgreementSectionService agreementSectionService;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private TemplateTypeService templateTypeService;


    /**
     * @param countryId
     * @param organizationId
     * @param policyAgreementTemplateDto
     * @return return object of basic policy agreement template.
     * @description this method creates a basic policy Agreement template with basic detail about organization type,
     * organizationSubTypes ,service Category and sub service Category.
     */
    public PolicyAgreementTemplateDTO createBasicPolicyAgreementTemplate(Long countryId, Long organizationId, PolicyAgreementTemplateDTO policyAgreementTemplateDto) {

        PolicyAgreementTemplate previousTemplate = policyAgreementTemplateRepository.findByName(countryId, organizationId, policyAgreementTemplateDto.getName());
        if (Optional.ofNullable(previousTemplate).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Policy Agreement Template ", policyAgreementTemplateDto.getName());
        }
        templateTypeService.getTemplateById(policyAgreementTemplateDto.getTemplateTypeId(), countryId);
        PolicyAgreementTemplate policyAgreementTemplate = new PolicyAgreementTemplate(
                policyAgreementTemplateDto.getName(),
                policyAgreementTemplateDto.getDescription(),
                countryId,
                policyAgreementTemplateDto.getOrganizationTypes(),
                policyAgreementTemplateDto.getOrganizationSubTypes(),
                policyAgreementTemplateDto.getOrganizationServices(),
                policyAgreementTemplateDto.getOrganizationSubServices());
        policyAgreementTemplate.setAccountTypes(policyAgreementTemplateDto.getAccountTypes());
        policyAgreementTemplate.setTemplateType(policyAgreementTemplateDto.getTemplateTypeId());
        policyAgreementTemplate.setOrganizationId(organizationId);
        policyAgreementTemplateRepository.save(policyAgreementTemplate);
        policyAgreementTemplateDto.setId(policyAgreementTemplate.getId());
        return policyAgreementTemplateDto;

    }


    /**
     * @param countryId
     * @param organizationId
     * @return
     * @description method return policy agreement template with basic details
     */
    public List<PolicyAgreementTemplateResponseDTO> getAllPolicyAgreementTemplate(Long countryId, Long organizationId) {
        List<PolicyAgreementTemplateResponseDTO> policyAgreementTemplateResponseDTOS = policyAgreementTemplateRepository.getAllPolicyAgreementTemplateByCountryId(countryId, organizationId);
        policyAgreementTemplateResponseDTOS.forEach(policyAgreementTemplateResponseDTO -> policyAgreementTemplateResponseDTO.setSections(new ArrayList<>()));
        return policyAgreementTemplateResponseDTOS;
    }


    /**
     * @param countryId
     * @param unitId
     * @param agreementTemplateId
     * @return
     * @description method return list of Agreement sections with sub sections of policy agreement template
     */
    public List<AgreementSectionResponseDTO> getAllAgreementSectionsAndSubSectionsOfAgreementTemplateByTemplateId(Long countryId, Long unitId, BigInteger agreementTemplateId) {


        List<AgreementSectionResponseDTO> agreementSectionResponseDTOS = policyAgreementTemplateRepository.getAgreementTemplateWithSectionsAndSubSections(countryId, unitId, agreementTemplateId);
        agreementSectionResponseDTOS.forEach(agreementSectionResponseDTO ->
                {
                    Map<BigInteger, ClauseBasicResponseDTO> clauseBasicResponseDTOS = agreementSectionResponseDTO.getClauses().stream().collect(Collectors.toMap(ClauseBasicResponseDTO::getId, clauseBasicDTO -> clauseBasicDTO));
                    sortClauseOfAgreementSectionAndSubSectionInResponseDTO(clauseBasicResponseDTOS, agreementSectionResponseDTO);
                    if (!Optional.ofNullable(agreementSectionResponseDTO.getSubSections().get(0).getId()).isPresent()) {
                        agreementSectionResponseDTO.setSubSections(new ArrayList<>());
                    } else {
                        agreementSectionResponseDTO.getSubSections().forEach(agreementSubSectionResponseDTO -> {
                            Map<BigInteger, ClauseBasicResponseDTO> subSectionClauseBasicResponseDTOS = agreementSubSectionResponseDTO.getClauses().stream().collect(Collectors.toMap(ClauseBasicResponseDTO::getId, clauseBasicDTO -> clauseBasicDTO));
                            sortClauseOfAgreementSectionAndSubSectionInResponseDTO(subSectionClauseBasicResponseDTOS, agreementSubSectionResponseDTO);
                        });
                    }
                }
        );
        return agreementSectionResponseDTOS;
    }

    private void sortClauseOfAgreementSectionAndSubSectionInResponseDTO(Map<BigInteger, ClauseBasicResponseDTO> clauseBasicResponseDTOS, AgreementSectionResponseDTO agreementSectionResponseDTO) {
        List<BigInteger> clauseIdOrderedIndexs = agreementSectionResponseDTO.getClauseIdOrderedIndex();
        agreementSectionResponseDTO.getClauses().clear();
        List<ClauseBasicResponseDTO> clauses = new ArrayList<>();
        clauseIdOrderedIndexs.stream().forEach(clauseIdOrderedIndex -> clauses.add(clauseBasicResponseDTOS.get(clauseIdOrderedIndex)));
        agreementSectionResponseDTO.setClauses(clauses);
    }


    public Boolean deletePolicyAgreementTemplate(Long countryId, Long organizationId, BigInteger id) {

        PolicyAgreementTemplate exist = policyAgreementTemplateRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Policy Agreement Template ", id);
        }
        delete(exist);
        return true;

    }


}


